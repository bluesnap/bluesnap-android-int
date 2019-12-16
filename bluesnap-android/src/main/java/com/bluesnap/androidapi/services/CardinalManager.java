package com.bluesnap.androidapi.services;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthRequest;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.CreditCard;
import com.cardinalcommerce.cardinalmobilesdk.Cardinal;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType;
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType;
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode;
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
    public static final String CARDINAL_PROCESS_DONE = "com.bluesnap.intent.CARDINAL_CARD_VALIDATED";

    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    //private static Cardinal cardinal = Cardinal.getInstance();
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private String cardinalToken;

    // this property is an indication to NOT trigger cardinal in second phase (after submit)
    private boolean cardinalError = false;
    private String cardinalResult = CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name();


    public enum CardinalManagerResponse {
        // server response
        AUTHENTICATION_BYPASSED,
        AUTHENTICATION_SUCCEEDED,
        AUTHENTICATION_UNAVAILABLE,
        AUTHENTICATION_FAILED,

        // challenge was canceled by the user
        AUTHENTICATION_CANCELED,

        // cardinal internal or server error
        CARDINAL_ERROR,

        // V1 unsupported cards
        CARD_NOT_SUPPORTED
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
    void setCardinalJWT(@Nullable String tokenJWT) {
        // reset CardinalFailure and CardinalResult for singleton use
        setCardinalError(false);
        setCardinalResult(CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());

        this.cardinalToken = tokenJWT;
    }

    // This method can and should be called before a token is obtained to save time later
    void configureCardinal(Context context, boolean isProduction) {
        if (!is3DSecureEnabled()) { // cardinal is disabled in merchant configuration
            return;
        }

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
     */
    void initCardinal(InitCardinalServiceCallback initCardinalServiceCallback) {
        if (!is3DSecureEnabled()) { // cardinal is disabled in merchant configuration
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
                setCardinalError(true);
                initCardinalServiceCallback.onComplete();
            }
        });

    }

    public void authWith3DS(String currency, Double amount, Activity activity, final CreditCard creditCard, boolean isReturningShopper) throws JSONException {
        if (!is3DSecureEnabled() || isCardinalError()) { // cardinal is disabled in merchant configuration or error occurred
            BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_PROCESS_DONE, TAG);
            return;
        }

        BS3DSAuthRequest authRequest = new BS3DSAuthRequest(currency, amount, cardinalToken);
        BlueSnapHTTPResponse response = blueSnapAPI.tokenizeDetails(authRequest.toJson().toString());
        JSONObject jsonObject;
        if (response.getResponseCode() != HTTP_OK) {
            //TODO: should we return this service error response in the event so that we can return it to the merchant in onResult?
            Log.e(TAG, "BS Server Error in 3DS Auth API call:\n" + response);
            setCardinalResult(CardinalManagerResponse.CARDINAL_ERROR.name());
            BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_PROCESS_DONE, response.toString(), TAG);
        }

        jsonObject = new JSONObject(response.getResponseString());
        BS3DSAuthResponse authResponse = BS3DSAuthResponse.fromJson(jsonObject);
        if (authResponse == null) {
            setCardinalResult(CardinalManagerResponse.CARDINAL_ERROR.name());
            BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_PROCESS_DONE, TAG);
            return;
        }

        if (authResponse.getEnrollmentStatus().equals("CHALLENGE_REQUIRED")) {
            // verifying 3DS version
            String firstChar = authResponse.getThreeDSVersion().substring(0, 1);
            if (!firstChar.equals("2")) {
                setCardinalResult(CardinalManagerResponse.CARD_NOT_SUPPORTED.name());
                BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_PROCESS_DONE, TAG);
            } else { // call process
                process(authResponse, activity, creditCard, isReturningShopper);
            }
        } else { // populate Enrollment Status as cardinal result
            setCardinalResult(authResponse.getEnrollmentStatus());
            BlueSnapLocalBroadcastManager.sendMessage(activity, CARDINAL_PROCESS_DONE, TAG);
        }
    }


    public String getCardinalToken() {
        return cardinalToken;
    }

    /**
     * Call cardinal cca_continue,
     * We use the deprecated method due to the payload returned from the API.
     * @param authResponse - 3DS authentication response from server
     * @param activity - current displayed activity
     * @param creditCard - shopper's credit card
     * @param isReturningShopper - true if this is a returning shopper flow, false w.s.
     */
    public void process(@NonNull final BS3DSAuthResponse authResponse, Activity activity, final CreditCard creditCard, boolean isReturningShopper) {

        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run() {

                if (!isReturningShopper) { // new card mode - passing the cc number to cardinal for processing
                    Cardinal.getInstance().processBin(creditCard.getNumber(), new CardinalProcessBinService() {
                        @Override
                        public void onComplete() {
                            process(authResponse, activity);
                        }
                    });
                } else { // vaulted card - moving straight to cardinal challenge
                    process(authResponse, activity);
                }
            }
        });
    }

    /**
     * Call cardinal cca_continue,
     * We use the deprecated method due to the payload returned from the API.
     *
     * @param authResponse - 3DS authentication response from server
     * @param activity     - current displayed activity
     */
    private void process(final BS3DSAuthResponse authResponse, Activity activity) {

        Cardinal.getInstance().cca_continue(authResponse.getTransactionId(), authResponse.getPayload(), activity, new CardinalValidateReceiver() {
            @Override
            public void onValidated(Context context, ValidateResponse validateResponse, String s) {
                Log.d(TAG, "Cardinal validated callback");

                if (validateResponse.actionCode.equals(CardinalActionCode.NOACTION) || validateResponse.actionCode.equals(CardinalActionCode.SUCCESS)) {
                    try {
                        processCardinalResult(s);
                    } catch (BSProcess3DSResultRequestException e) {
                        //TODO: should we return this service error response in the event so that we can return it to the merchant in onResult?
                        setCardinalResult(CardinalManagerResponse.CARDINAL_ERROR.name());
                        BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_PROCESS_DONE, e.getMessage(), TAG);
                        return;
                    }
                } else if (validateResponse.actionCode.equals(CardinalActionCode.FAILURE)) {
                    setCardinalResult(CardinalManagerResponse.AUTHENTICATION_FAILED.name());
                } else if (validateResponse.actionCode.equals(CardinalActionCode.ERROR)) {
                    setCardinalResult(CardinalManagerResponse.CARDINAL_ERROR.name());
                } else { // cancel
                    setCardinalResult(CardinalManagerResponse.AUTHENTICATION_CANCELED.name());
                }

                BlueSnapLocalBroadcastManager.sendMessage(context, CARDINAL_PROCESS_DONE, TAG);
            }
        });

    }

    /**
     * @return
     * @throws BSProcess3DSResultRequestException
     */
    private void processCardinalResult(String resultJwt) throws BSProcess3DSResultRequestException {
        String body = createDataObject(resultJwt).toString();
        BlueSnapHTTPResponse response = blueSnapAPI.processCardinalResult(body);
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "BS Server Error in 3DS process result API call:\n" + response);
            throw new BSProcess3DSResultRequestException(response.toString());
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

    public boolean is3DSecureEnabled() {
        return (cardinalToken != null);
    }

    public String getCardinalResult() {
        return cardinalResult;
    }

    private void setCardinalResult(String cardinalResult) {
        this.cardinalResult = cardinalResult;
    }

    private boolean isCardinalError() {
        return cardinalError;
    }

    private void setCardinalError(boolean cardinalError) {
        if (cardinalError)
            setCardinalResult(CardinalManagerResponse.CARDINAL_ERROR.name());
        this.cardinalError = cardinalError;
    }

}
