package com.bluesnap.android.demoapp;

import android.content.Context;
import android.util.Log;

import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BufferedHeader;

import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_CREATE_TRANSACTION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;

/**
 * A Demo class that mocks server to server calls
 * You should not do these calls on your mobile app.
 */
public class DemoTransactions {

    private static final String TAG = DemoTransactions.class.getSimpleName();
    private static final DemoTransactions INSTANCE = new DemoTransactions();
    private String SHOPPER_ID = "SHOPPER_ID";
    private String message;
    private String title;
    private Context context;

    public static DemoTransactions getInstance() {
        return INSTANCE;
    }

    public static String extractTokenFromHeaders(Header[] headers) {
        String token = null;
        for (Header hr : headers) {
            BufferedHeader bufferedHeader = (BufferedHeader) hr;
            if (bufferedHeader.getName().equals("Location")) {
                String path = bufferedHeader.getValue();
                token = path.substring(path.lastIndexOf('/') + 1);
            }
        }
        return token;
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

        StringEntity entity = new StringEntity(body, "UTF-8");
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setBasicAuth(SANDBOX_USER, SANDBOX_PASS);
        Log.d(TAG, "Create transaction body:\n" + body);
        httpClient.post(getContext(), SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, entity, "application/xml", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString, throwable);
                //Disabled until server will return a reasonable error
                String errorName = "Error Server";
                try {
                    if (responseString != null)
                        errorName = responseString.substring(responseString.indexOf("<error-name>") + "<error-name>".length(), responseString.indexOf("</error-name>"));
                } catch (Exception e) {
                    Log.w(TAG, "failed to get error name from response string");
                }
                setMessage(errorName);
                setTitle("Merchant Server");
                callback.onFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                setShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
                        "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
                Log.d(TAG, responseString);
                setMessage("Transaction Success " + getShopperId());
                setTitle("Merchant Server");
                callback.onSuccess();
            }
        });
    }

    private String getShopperId() {
        PrefsStorage prefsStorage = new PrefsStorage(getContext());
        return prefsStorage.getString(SHOPPER_ID, "");
    }

    private void setShopperId(String shopperId) {
        PrefsStorage prefsStorage = new PrefsStorage(getContext());
        prefsStorage.putString(SHOPPER_ID, shopperId);
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
