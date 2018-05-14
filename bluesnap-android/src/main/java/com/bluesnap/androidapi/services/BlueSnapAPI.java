package com.bluesnap.androidapi.services;

import android.util.Log;

import com.bluesnap.androidapi.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by roy.biber on 14/11/2017.
 */

class BlueSnapAPI {
    private static final String TAG = BlueSnapAPI.class.getSimpleName();
    private static final double BLUESNAP_VERSION_HEADER = 2.0;
    private static final BlueSnapAPI INSTANCE = new BlueSnapAPI();

    public static final String TOKEN_AUTHENTICATION = "Token-Authentication";
    private static final String CARD_TOKENIZE = "payment-fields-tokens/";
    private static final String RATES_SERVICE = "tokenized-services/rates";
    private static final String BASE_CURRENCY = "?base-currency=";
    private static final String SUPPORTED_PAYMENT_METHODS = "tokenized-services/supported-payment-methods";
    private static final String SDK_INIT = "tokenized-services/sdk-init";
    private static final String PAYPAL_SERVICE = "tokenized-services/paypal-token?amount=";
    private static final String PAYPAL_SHIPPING = "&req-confirm-shipping=0&no-shipping=2";
    private static final String RETRIEVE_TRANSACTION_SERVICE = "tokenized-services/transaction-status";
    private final AsyncHttpClient httpClient;
    private String merchantToken;
    private String url;

    static BlueSnapAPI getInstance() {
        return INSTANCE;
    }

    /**
     * set BlueSnap API headers and connection setup
     */
    private BlueSnapAPI() {
        httpClient = new AsyncHttpClient();
        httpClient.setMaxRetriesAndTimeout(2, 2000);
        httpClient.setResponseTimeout(60000);
        httpClient.setConnectTimeout(20000);
        httpClient.addHeader("Accept", "application/json");
        httpClient.addHeader("ANDROID_SDK_VERSION_NAME", BuildConfig.VERSION_NAME);
        httpClient.addHeader("ANDROID_SDK_VERSION_CODE", String.valueOf(BuildConfig.VERSION_CODE));
        httpClient.addHeader("BLUESNAP_VERSION_HEADER", String.valueOf(BLUESNAP_VERSION_HEADER));
    }

    /**
     * tokenize details to server
     *
     * @param jsonObject      - details to set
     * @param responseHandler
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    void tokenizeDetails(JSONObject jsonObject, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpClient.put(null, url + CARD_TOKENIZE + merchantToken, entity, "application/json", responseHandler);
    }

    /**
     * add header to http client for TOKEN_AUTHENTICATION and set domain path
     *
     * @param merchantToken
     * @param url
     */
    void setupMerchantToken(String merchantToken, String url) {
        this.merchantToken = merchantToken;
        this.url = url;
        httpClient.addHeader(TOKEN_AUTHENTICATION, merchantToken);
    }

    /**
     * get sdk initilize data from server
     *
     * @param baseCurrency            - currency to base the rates on
     * @param jsonHttpResponseHandler
     */
    void sdkInit(final String baseCurrency, JsonHttpResponseHandler jsonHttpResponseHandler) {
        httpClient.get(url + SDK_INIT + BASE_CURRENCY + baseCurrency, jsonHttpResponseHandler);
    }

    /**
     * create PayPal Token (url)
     *
     * @param amount                  - amount to charge
     * @param currency                - currency to charge with
     * @param isShippingRequired      - boolean is shipping required
     * @param jsonHttpResponseHandler
     */
    void createPayPalToken(final Double amount, final String currency, boolean isShippingRequired, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String urlString = url + PAYPAL_SERVICE + amount + "&currency=" + currency;
        if (isShippingRequired)
            urlString += PAYPAL_SHIPPING;

        httpClient.get(urlString, jsonHttpResponseHandler);
    }

    /**
     * check transaction status after PayPal transaction occurred
     *
     * @param jsonHttpResponseHandler - what to do on success or failure
     */
    void retrieveTransactionStatus(JsonHttpResponseHandler jsonHttpResponseHandler) {
        httpClient.get(url + RETRIEVE_TRANSACTION_SERVICE, jsonHttpResponseHandler);
    }

}
