package com.bluesnap.androidapi.services;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthRequest;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.CardinalJWT;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.cardinalcommerce.cardinalmobilesdk.Cardinal;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType;
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters;
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse;
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService;
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver;
import com.cardinalcommerce.shared.models.enums.DirectoryServerID;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.net.HttpURLConnection.HTTP_OK;

public class CardinalManager  {
    public static final String CARDINAL_VALIDATED = "com.bluesnap.intent.CARDINAL_CARD_VALIDATED";
    public static final String CARDINAL_INITIALIZED = "com.bluesnap.intent.CARDINAL_INITIALIZED";
    ;
    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    //private static Cardinal cardinal = Cardinal.getInstance();
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private CardinalJWT cardinalToken;

    @Nullable
    private ValidateResponse cardinalValidationResult = null;


    public static CardinalManager getInstance() {
        if (instance == null) {
            instance = new CardinalManager();
            return instance;
        } else {
            return instance;
        }
    }

    // This method can and should be caleld before a token is obtained to save time later
    public void configureCardinal(Context context) {
        CardinalConfigurationParameters cardinalConfigurationParameters = new CardinalConfigurationParameters();
        //TODO: Staging or production
        cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.STAGING);
        cardinalConfigurationParameters.setTimeout(8000);
        JSONArray rType = new JSONArray();
        rType.put(CardinalRenderType.OTP);
        rType.put(CardinalRenderType.SINGLE_SELECT);
        rType.put(CardinalRenderType.MULTI_SELECT);
        rType.put(CardinalRenderType.OOB);
        rType.put(CardinalRenderType.HTML);
        cardinalConfigurationParameters.setRenderType(rType);

        cardinalConfigurationParameters.setUiType(CardinalUiType.BOTH);
        Cardinal.getInstance().configure(context, cardinalConfigurationParameters);
    }


    public BS3DSAuthResponse authWith3DS(String currency, Double amount) throws Exception {
        BS3DSAuthRequest authRequest = new BS3DSAuthRequest(currency , amount, cardinalToken.getJWT());
        BlueSnapHTTPResponse response = blueSnapAPI.tokenizeDetails(authRequest.toJson().toString());
        JSONObject jsonObject;
        if (response.getResponseCode() != HTTP_OK) {
            throw new Exception("BS API Exception - tokenize 3ds"); // TODO create BSExceptions
        }
        jsonObject = new JSONObject(response.getResponseString());
        return BS3DSAuthResponse.fromJson(jsonObject);
    }
    /**
     *
     * @return
     * @throws  TODO: This should throw specific error
     */
    public CardinalJWT createCardinalJWT() throws Exception {
        BlueSnapHTTPResponse response = blueSnapAPI.createCardinalJWT();
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "Get cardinal error:\n" + response);
            throw new Exception("unable to get Cardinal token");
        }

        CardinalJWT  cardinalJWT = new CardinalJWT();
        cardinalJWT.parse(response.getResponseString());
        this.cardinalToken = cardinalJWT;

        return cardinalJWT;
    }

    // TODO: add a callback argument or Broadcast an event

    /**
     * @param creditCard
     * @return
     * @throws TODO: This should throw specific error
     */
    public void initCardinal(CreditCard creditCard, Activity activity) {
        Cardinal.getInstance().init(cardinalToken.getJWT(), creditCard.getNumber(), new CardinalInitService() {
            @Override
            public void onSetupCompleted(String consumerSessionID) {
                Log.d(TAG, "cardinal init completed");
                DirectoryServerID directoryServerID = DirectoryServerID.DEFAULT;
                BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_INITIALIZED, TAG);
            }

            @Override
            public void onValidated(ValidateResponse validateResponse, String s) {
                Log.d(TAG, "Error Message: " + validateResponse.getErrorDescription());
            }
        });

    }


    //cardinal.cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), activity, CardinalManager.instance);
//                cardinal.cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), authResponse.getAcsUrl(),  DirectoryServerID.VISA04, (Activity) activity, CardinalManager.instance);
//                cardinal.cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), authResponse.getAcsUrl(),  DirectoryServerID.VISA02, (Activity) activity, CardinalManager.instance);
//                cardinal.cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), authResponse.getAcsUrl(),  DirectoryServerID.VISA03, (Activity) activity, CardinalManager.instance);
//                cardinal.cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), authResponse.getAcsUrl(),  DirectoryServerID.VISA01, (Activity) activity, CardinalManager.instance);



    public CardinalJWT getCardinalToken() {
        return cardinalToken;
    }

    /**
     * Call cardinal cca_continue,
     * We use the deprecated method due to the payload returned from the API.
     * @param authResponse
     * @param activity
     */
    public void process(final BS3DSAuthResponse authResponse, Activity activity, final PurchaseDetails purchaseDetails) {

        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run()
            {
                Cardinal.getInstance().cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), activity, new CardinalValidateReceiver() {
                    @Override
                    public void onValidated(Context context, ValidateResponse validateResponse, String s) {
                        Log.d(TAG, "Cardinal validated callback");
                        cardinalValidationResult = validateResponse;
                        BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_VALIDATED , TAG);
                    }
                });
            }
        });



    }

    private DirectoryServerID resolveCardinalDirectoryServerID(String cardType) {

        switch (cardType.toUpperCase()){
            case "MASTER_CARD":
                return DirectoryServerID.MASTER_CARD;
            case "VISA":
                return DirectoryServerID.VISA01;
             default:
                 return DirectoryServerID.DEFAULT;
        }
    }

    /**
     * Cardinal Bean processing completion
     */
//    @Override
//    public void onComplete() {
//            Log.d(TAG, "Cardinal processing bean callback");
//
//    }
//
//    @Override
//    public void onValidated(Context context, ValidateResponse validateResponse, String s) {
//            Log.d(TAG, "Cardinal validated callback");
//            cardinalValidationResult = validateResponse;
//            BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_VALIDATED , TAG);
//    }

}
