package com.bluesnap.androidapi;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.bluesnap.androidapi.SandboxToken.SANDBOX_CREATE_TRANSACTION;
import static com.bluesnap.androidapi.SandboxToken.SANDBOX_PASS;
import static com.bluesnap.androidapi.SandboxToken.SANDBOX_URL;
import static com.bluesnap.androidapi.SandboxToken.SANDBOX_USER;
import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static java.net.HttpURLConnection.HTTP_OK;

public class IntegrationTestsDemoTransactions {

    private static final String TAG = IntegrationTestsDemoTransactions.class.getSimpleName();
    private static final IntegrationTestsDemoTransactions INSTANCE = new IntegrationTestsDemoTransactions();
    private String SHOPPER_ID = "SHOPPER_ID";
    private String message;
    private String title;
    private Context context;
    private String transactionId;
    private String cardLastFourDigits;
    private String tokenSuffix = "";


    public static IntegrationTestsDemoTransactions getInstance() {
        return INSTANCE;
    }

    public void createCreditCardTransaction(final SdkResult sdkResult, final BluesnapServiceCallback callback) {

        //TODO: I'm just a string but please don't make me look that bad..Use String.format
        String body = "<card-transaction xmlns=\"http://ws.plimus.com\">" +
                "<card-transaction-type>AUTH_CAPTURE</card-transaction-type>" +
                "<recurring-transaction>ECOMMERCE</recurring-transaction>" +
                "<soft-descriptor>MobileSDK</soft-descriptor>" +
                "<amount>" + sdkResult.getAmount() + "</amount>" +
                "<currency>" + sdkResult.getCurrencyNameCode() + "</currency>" +
                "<transaction-fraud-info>" +
                "<fraud-session-id>" + sdkResult.getKountSessionId() + "</fraud-session-id>" +
                "</transaction-fraud-info>" +
                "<pf-token>" + sdkResult.getToken() + "</pf-token>" +
                "</card-transaction>";


        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        BlueSnapHTTPResponse httpResponse = HTTPOperationController.post(SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, body, "application/xml", "application/xml", headerParams);
        String responseString = httpResponse.getResponseString();
        if (httpResponse.getResponseCode() == HTTP_OK && httpResponse.getHeaders() != null) {
//            setShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
//                    "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
            setTransactionId(responseString.substring(responseString.indexOf("<transaction-id>") +
                    "<transaction-id>".length(), responseString.indexOf("</transaction-id>")));
            setCardLastFourDigits(responseString.substring(responseString.indexOf("<card-last-four-digits>") +
                    "<card-last-four-digits>".length(), responseString.indexOf("</card-last-four-digits>")));

            String merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();
            setTokenSuffix(merchantToken.substring(merchantToken.length() - 6));
            Log.d(TAG, responseString);
            setMessage("Transaction Success " + getTransactionId());
            setTitle("Merchant Server");
            callback.onSuccess();
        } else {
            Log.e(TAG, responseString);
            //Disabled until server will return a reasonable error
            String errorName = "Transaction Failed";
            try {
                if (responseString != null)
                    errorName = responseString.substring(responseString.indexOf("<error-name>") + "<error-name>".length(), responseString.indexOf("</error-name>"));
                Log.e(TAG, "Failed TX Response:  " + responseString);
            } catch (Exception e) {
                Log.e(TAG, "failed to get error name from response string");
                Log.e(TAG, "Failed TX Response:  " + responseString);
            }
            setMessage(errorName);
            setTitle("Merchant Server");
            callback.onFailure();
        }

    }

    public String getTransactionId() {
        return transactionId;
    }

    private void setTransactionId(String id) {
        this.transactionId = id;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getTokenSuffix() {
        return tokenSuffix;
    }

    private void setTokenSuffix(String token) {
        this.tokenSuffix = token;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }
}
