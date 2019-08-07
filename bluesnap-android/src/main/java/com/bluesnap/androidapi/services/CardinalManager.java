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
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalProcessBinService;
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver;
import com.cardinalcommerce.shared.models.enums.DirectoryServerID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;
import static java.net.HttpURLConnection.HTTP_OK;

public class CardinalManager  {
    public static final String CARDINAL_VALIDATED = "com.bluesnap.intent.CARDINAL_CARD_VALIDATED";
    private static final String AUTHENTICATION_UNAVAILABLE = "AUTHENTICATION_UNAVAILABLE";

    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    //private static Cardinal cardinal = Cardinal.getInstance();
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private String cardinalToken;

    private boolean cardinalFailure = false;
    private String cardinalResult = AUTHENTICATION_UNAVAILABLE;


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
        setCardinalFailure(false);
        cardinalResult = AUTHENTICATION_UNAVAILABLE;

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

    // TODO: add a callback argument or Broadcast an event

    /**
     * @return
     * @throws //TODO: This should throw specific error
     */
    public void initCardinal(InitCardinalServiceCallback initCardinalServiceCallback) {
        if (isCardinalFailure())
            return;

        Cardinal.getInstance().init(cardinalToken, new CardinalInitService() {
            @Override
            public void onSetupCompleted(String consumerSessionID) {
                Log.d(TAG, "cardinal init completed");
                DirectoryServerID directoryServerID = DirectoryServerID.DEFAULT;
                initCardinalServiceCallback.onSuccess();
            }

            @Override
            public void onValidated(ValidateResponse validateResponse, String s) {
                Log.d(TAG, "Error Message: " + validateResponse.getErrorDescription());
                cardinalFailure = true;
            }
        });

    }

    /**
     *
     * @return
     * @throws  //TODO: This should throw specific error
     */
    public void setCardinalJWT(String tokenJWT) throws Exception {

        this.cardinalToken = tokenJWT;

    }

    public BS3DSAuthResponse authWith3DS(String currency, Double amount) throws Exception {
        if (isCardinalFailure())
            return null;

        BS3DSAuthRequest authRequest = new BS3DSAuthRequest(currency, amount, cardinalToken);
        BlueSnapHTTPResponse response = blueSnapAPI.tokenizeDetails(authRequest.toJson().toString());
        JSONObject jsonObject;
        if (response.getResponseCode() != HTTP_OK) {
            throw new Exception("BS API Exception - tokenize 3ds"); // TODO create BSExceptions
        }

        jsonObject = new JSONObject(response.getResponseString());
        BS3DSAuthResponse authResponse = BS3DSAuthResponse.fromJson(jsonObject);
        if (!authResponse.getEnrollmentStatus().equals("CHALLENGE_REQUIRED")) {
            cardinalResult = authResponse.getEnrollmentStatus();
        }
        return authResponse;
    }


    public String getCardinalToken() {
        return cardinalToken;
    }

    /**
     * Call cardinal cca_continue,
     * We use the deprecated method due to the payload returned from the API.
     * @param authResponse
     * @param activity
     */
    public void process(final BS3DSAuthResponse authResponse, Activity activity, final CreditCard creditCard) {
        if (isCardinalFailure())
            return;

        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run() {

                Cardinal.getInstance().processBin(creditCard.getNumber(), new CardinalProcessBinService() {
                    @Override
                    public void onComplete() {
                        Cardinal.getInstance().cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), activity, new CardinalValidateReceiver() {
                            @Override
                            public void onValidated(Context context, ValidateResponse validateResponse, String s) {
                                Log.d(TAG, "Cardinal validated callback");

                                if (!s.isEmpty()) {
                                    processCardinalResult(s);
                                } else {
                                    cardinalResult = AUTHENTICATION_UNAVAILABLE;
                                }

                                BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_VALIDATED, TAG);
                            }
                        });
                    }
                });

            }
        });


    }

    /**
     * @return
     * @throws //TODO: This should throw specific error
     */
    public void processCardinalResult(String resultJwt) {
        if (isCardinalFailure())
            return;

        String body = createDataObject(resultJwt).toString();
        BlueSnapHTTPResponse response = blueSnapAPI.processCardinalResult(body);
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "Error in processing cardinal result:\n" + response);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(response.getResponseString());
                cardinalResult = getOptionalString(jsonObject, "authResult");
            } catch (JSONException e) {
                Log.e(TAG, "Error in parsing cardinal result:\n" + response);
            }
        }
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

    private JSONObject createDataObject(String resultJwt) {
        JSONObject jsonObject = new JSONObject();

        putJSONifNotNull(jsonObject, "jwt", cardinalToken);
        putJSONifNotNull(jsonObject, "resultJwt", resultJwt);


        return jsonObject;
    }

    public String getCardinalResult() {
        return cardinalResult;
    }

    public boolean isCardinalFailure() {
        return cardinalFailure;
    }

    public void setCardinalFailure(boolean cardinalFailure) {
        this.cardinalFailure = cardinalFailure;
    }
}
