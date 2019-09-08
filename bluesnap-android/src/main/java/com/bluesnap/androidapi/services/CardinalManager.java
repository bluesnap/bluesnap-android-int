package com.bluesnap.androidapi.services;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthRequest;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.CreditCard;
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

    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    //private static Cardinal cardinal = Cardinal.getInstance();
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private String cardinalToken;

    // this property is an indication to NOT trigger cardinal in second phase (after submit)
    private boolean cardinalFailure = false;
    private String cardinalResult = CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name();


    public enum CardinalManagerResponse {
        AUTHENTICATION_BYPASSED,
        AUTHENTICATION_SUCCEEDED,
        AUTHENTICATION_UNAVAILABLE,
        AUTHENTICATION_FAILED,
        AUTHENTICATION_NOT_SUPPORTED
    }

    public static CardinalManager getInstance() {
        if (instance == null) {
            instance = new CardinalManager();
            return instance;
        } else {
            return instance;
        }
    }

    /**
     *
     */
    public void setCardinalJWT(String tokenJWT) {
        // reset CardinalFailure and CardinalResult for singleton use
        setCardinalFailure(false);
        setCardinalResult(CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());

        if (tokenJWT == null)
            setCardinalFailure(true);

        this.cardinalToken = tokenJWT;
    }

    // This method can and should be called before a token is obtained to save time later
    public void configureCardinal(Context context, Boolean isProduction) {
        if (isCardinalFailure())
            return;

        CardinalConfigurationParameters cardinalConfigurationParameters = new CardinalConfigurationParameters();
        if (isProduction) {
            cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.PRODUCTION);
        } else {
            cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.STAGING);
        }

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

    /**
     * @throws //TODO: This should throw specific error
     */
    public void initCardinal(InitCardinalServiceCallback initCardinalServiceCallback) {
        if (isCardinalFailure()) {
            initCardinalServiceCallback.onComplete();
            return;
        }

        Cardinal.getInstance().init(cardinalToken, new CardinalInitService() {
            @Override
            public void onSetupCompleted(String consumerSessionID) {
                Log.d(TAG, "cardinal init completed");
                DirectoryServerID directoryServerID = DirectoryServerID.DEFAULT;
                initCardinalServiceCallback.onComplete();
            }

            @Override
            public void onValidated(ValidateResponse validateResponse, String s) {
                Log.d(TAG, "Error Message: " + validateResponse.getErrorDescription());
                setCardinalFailure(true);
                initCardinalServiceCallback.onComplete();
            }
        });

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
            setCardinalResult(authResponse.getEnrollmentStatus());
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

                                if (validateResponse.actionCode.getString().equals("NOACTION") || validateResponse.actionCode.getString().equals("SUCCESS")) {
                                    processCardinalResult(s);
                                } else if (validateResponse.actionCode.getString().equals("FAILURE")) {
                                    setCardinalResult(CardinalManagerResponse.AUTHENTICATION_FAILED.name());
                                } else {
                                    setCardinalResult(CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());
                                }

                                BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_VALIDATED, validateResponse.actionCode.getString(), TAG);
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
        String body = createDataObject(resultJwt).toString();
        BlueSnapHTTPResponse response = blueSnapAPI.processCardinalResult(body);
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "Error in processing cardinal result:\n" + response);
            setCardinalResult(CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());
        } else {
            try {
                JSONObject jsonObject = new JSONObject(response.getResponseString());
                setCardinalResult(getOptionalString(jsonObject, "authResult"));
            } catch (JSONException e) {
                Log.e(TAG, "Error in parsing cardinal result:\n" + response);
                setCardinalResult(CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());
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

    public void setCardinalResult(String cardinalResult) {
        this.cardinalResult = cardinalResult;
    }

    public boolean isCardinalFailure() {
        return cardinalFailure;
    }

    public void setCardinalFailure(boolean cardinalFailure) {
        this.cardinalFailure = cardinalFailure;
    }

}
