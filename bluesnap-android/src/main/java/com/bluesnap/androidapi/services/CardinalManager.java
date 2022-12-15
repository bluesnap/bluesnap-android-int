package com.bluesnap.androidapi.services;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver;
import com.cardinalcommerce.shared.models.enums.DirectoryServerID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;
import static java.net.HttpURLConnection.HTTP_OK;

public class CardinalManager  {
    public static final String THREE_DS_AUTH_DONE_EVENT = "com.bluesnap.intent.3DS_AUTH_DONE";
    public static final String THREE_DS_AUTH_DONE_EVENT_NAME = "THREE_DS_AUTH_DONE_EVENT";
    private static final String SUPPORTED_CARD_VERSION = "2";

    private static final String TAG = CardinalManager.class.getSimpleName();
    private static CardinalManager instance = null;
    private BlueSnapAPI blueSnapAPI = BlueSnapAPI.getInstance();
    private String cardinalToken;

    // this property is an indication to NOT trigger cardinal in second phase (after submit)
    private boolean cardinalError = false;
    private String threeDSAuthResult = ThreeDSManagerResponse.AUTHENTICATION_UNAVAILABLE.name();


    public enum ThreeDSManagerResponse {
        // server response
        AUTHENTICATION_BYPASSED,
        AUTHENTICATION_SUCCEEDED,
        AUTHENTICATION_UNAVAILABLE, // authentication unavailable for this card
        AUTHENTICATION_FAILED, // card authentication in cardinal challenge failed

        // challenge was canceled by the user
        AUTHENTICATION_CANCELED,

        // cardinal internal error or server error
        THREE_DS_ERROR,

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
        // reset CardinalFailure and setThreeDSAuthResult for singleton use
        setCardinalError(false);
        setThreeDSAuthResult(ThreeDSManagerResponse.AUTHENTICATION_UNAVAILABLE.name());

        this.cardinalToken = tokenJWT;
    }

    // This method can and should be called before a token is obtained to save time later
    void configureCardinal(Context context, boolean isProduction) {
        if (!is3DSecureEnabled()) { // 3DS is disabled in merchant configuration
            return;
        }

        CardinalConfigurationParameters cardinalConfigurationParameters = new CardinalConfigurationParameters();
        if (isProduction) {
            cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.PRODUCTION);
        } else {
            cardinalConfigurationParameters.setEnvironment(CardinalEnvironment.STAGING);
        }

//        cardinalConfigurationParameters.setTimeout(8000);
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

    public void authWith3DS(String currency, Double amount, Activity activity, final CreditCard creditCard) throws JSONException {
        if (!is3DSecureEnabled() || isCardinalError()) { // cardinal is disabled in merchant configuration or error occurred
            BlueSnapLocalBroadcastManager.sendMessage(activity, THREE_DS_AUTH_DONE_EVENT, TAG);
            return;
        }

        BS3DSAuthRequest authRequest = new BS3DSAuthRequest(currency, amount, cardinalToken);
        BlueSnapHTTPResponse response = blueSnapAPI.tokenizeDetails(authRequest.toJson().toString());
        JSONObject jsonObject;
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "BS Server Error in 3DS Auth API call:\n" + response);
            setThreeDSAuthResult(ThreeDSManagerResponse.THREE_DS_ERROR.name());
            BlueSnapLocalBroadcastManager.sendMessage(activity, THREE_DS_AUTH_DONE_EVENT, response.toString(), TAG);
        }

        jsonObject = new JSONObject(response.getResponseString());
        BS3DSAuthResponse authResponse = BS3DSAuthResponse.fromJson(jsonObject);
        if (authResponse == null) {
            setThreeDSAuthResult(ThreeDSManagerResponse.THREE_DS_ERROR.name());
            BlueSnapLocalBroadcastManager.sendMessage(activity, THREE_DS_AUTH_DONE_EVENT, TAG);
            return;
        }

        if (authResponse.getEnrollmentStatus().equals("CHALLENGE_REQUIRED")) {
            // verifying 3DS version
            String firstChar = authResponse.getThreeDSVersion().substring(0, 1);
            if (!firstChar.equals(SUPPORTED_CARD_VERSION)) {
                setThreeDSAuthResult(ThreeDSManagerResponse.CARD_NOT_SUPPORTED.name());
                BlueSnapLocalBroadcastManager.sendMessage(activity, THREE_DS_AUTH_DONE_EVENT, TAG);
            } else { // call process
                process(authResponse, activity);
            }
        } else { // populate Enrollment Status as 3DS result
            setThreeDSAuthResult(authResponse.getEnrollmentStatus());
            BlueSnapLocalBroadcastManager.sendMessage(activity, THREE_DS_AUTH_DONE_EVENT, TAG);
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
     */
//    private void process(@NonNull final BS3DSAuthResponse authResponse, Activity activity, final CreditCard creditCard) {
//
//        Handler refresh = new Handler(Looper.getMainLooper());
//        refresh.post(new Runnable() {
//            public void run() {
//
//                if (creditCard.getIsNewCreditCard()) { // new card mode - passing the cc number to cardinal for processing
//                    Cardinal.getInstance().processBin(creditCard.getNumber(), new CardinalProcessBinService() {
//                        @Override
//                        public void onComplete() {
//                            process(authResponse, activity);
//                        }
//                    });
//                } else { // vaulted card - moving straight to cardinal challenge
//                    process(authResponse, activity);
//                }
//            }
//        });
//    }

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

                if (validateResponse.getActionCode().equals(CardinalActionCode.NOACTION) || validateResponse.getActionCode().equals(CardinalActionCode.SUCCESS)) {
                    try {
                        processCardinalResult(s);
                    } catch (BSProcess3DSResultRequestException | JSONException e) {
                        setThreeDSAuthResult(ThreeDSManagerResponse.THREE_DS_ERROR.name());
                        BlueSnapLocalBroadcastManager.sendMessage(context, THREE_DS_AUTH_DONE_EVENT, e.getMessage(), TAG);
                        return;
                    }

                } else if (validateResponse.getActionCode().equals(CardinalActionCode.FAILURE)) {
                    setThreeDSAuthResult(ThreeDSManagerResponse.AUTHENTICATION_FAILED.name());
                } else if (validateResponse.getActionCode().equals(CardinalActionCode.ERROR)) {
                    setThreeDSAuthResult(ThreeDSManagerResponse.THREE_DS_ERROR.name());
                } else { // cancel
                    setThreeDSAuthResult(ThreeDSManagerResponse.AUTHENTICATION_CANCELED.name());
                }

                BlueSnapLocalBroadcastManager.sendMessage(context, THREE_DS_AUTH_DONE_EVENT, TAG);
            }
        });

    }

    /**
     * @return
     * @throws BSProcess3DSResultRequestException
     */
    private void processCardinalResult(String resultJwt) throws BSProcess3DSResultRequestException, JSONException {
        String body = createDataObject(resultJwt).toString();
        BlueSnapHTTPResponse response = blueSnapAPI.processCardinalResult(body);
        if (response.getResponseCode() != HTTP_OK) {
            Log.e(TAG, "BS Server Error in 3DS process result API call:\n" + response);
            throw new BSProcess3DSResultRequestException(response.toString());
        } else {
            JSONObject jsonObject = new JSONObject(response.getResponseString());
            setThreeDSAuthResult(getOptionalString(jsonObject, "authResult"));
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

    private boolean is3DSecureEnabled() {
        return (cardinalToken != null);
    }

    public String getThreeDSAuthResult() {
        return threeDSAuthResult;
    }

    private void setThreeDSAuthResult(String threeDSAuthResult) {
        this.threeDSAuthResult = threeDSAuthResult;
    }

    private boolean isCardinalError() {
        return cardinalError;
    }

    private void setCardinalError(boolean cardinalError) {
        if (cardinalError)
            setThreeDSAuthResult(ThreeDSManagerResponse.THREE_DS_ERROR.name());
        this.cardinalError = cardinalError;
    }

}
