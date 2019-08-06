package com.bluesnap.androidapi.services;

import android.util.Log;

import com.bluesnap.androidapi.BuildConfig;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;

import java.util.ArrayList;

/**
 * Created by roy.biber on 14/11/2017.
 */

class BlueSnapAPI {
    private static final String TAG = BlueSnapAPI.class.getSimpleName();
    private static final String CONTENT_TYPE = "application/json";
    private static final String ACCEPT = "application/json";

    //blueSnap API version
    private static final double BLUESNAP_VERSION_HEADER = 2.0;
    private static final BlueSnapAPI INSTANCE = new BlueSnapAPI();

    public static final String TOKEN_AUTHENTICATION = "Token-Authentication";
    private static final String CARD_TOKENIZE = "payment-fields-tokens/";
    private static final String RATES_SERVICE = "tokenized-services/rates";
    private static final String BASE_CURRENCY = "?base-currency=";
    private static final String CREATE_JWT = "&create-jwt=";
    private static final String SUPPORTED_PAYMENT_METHODS = "tokenized-services/supported-payment-methods";
    private static final String SDK_INIT = "tokenized-services/sdk-init";
    private static final String UPDATE_SHOPPER = "tokenized-services/shopper";
    private static final String PAYPAL_SERVICE = "tokenized-services/paypal-token?amount=";
    private static final String CARDINAL_SERVICE_CREATE_JWT = "tokenized-services/3ds-jwt";
    private static final String CARDINAL_SERVICE_PROCESS_RESULT = "tokenized-services/3ds-process-result";
    private static final String PAYPAL_SHIPPING = "&req-confirm-shipping=0&no-shipping=2";
    private static final String RETRIEVE_TRANSACTION_SERVICE = "tokenized-services/transaction-status";
    private String merchantToken;
    private String url;
    private ArrayList<CustomHTTPParams> headerParams;


    static BlueSnapAPI getInstance() {
        return INSTANCE;
    }

    /**
     * set BlueSnap API headers and connection setup
     */
    private BlueSnapAPI() {

        headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("BLUESNAP_ORIGIN_HEADER", "ANDROID SDK " + BuildConfig.VERSION_CODE));
        headerParams.add(new CustomHTTPParams("BLUESNAP_ORIGIN_VERSION_HEADER", BuildConfig.VERSION_NAME));
        headerParams.add(new CustomHTTPParams("BLUESNAP_VERSION_HEADER", String.valueOf(BLUESNAP_VERSION_HEADER)));
    }

    /**
     * tokenize details to server
     *
     * @param body - details to set
     */
    BlueSnapHTTPResponse tokenizeDetails(final String body) {
        Log.d(TAG, "Api request for token detail");
        // headerParams.add(new CustomHTTPParams(TOKEN_AUTHENTICATION, String.valueOf(merchantToken)));
        return HTTPOperationController.put(url + CARD_TOKENIZE + merchantToken, body, CONTENT_TYPE,
                ACCEPT, headerParams);
    }

    /**
     * update shopper details to server
     *
     * @param body - body string to send to server
     * @return {@link BlueSnapHTTPResponse}
     */
    BlueSnapHTTPResponse updateShopper(final String body) {
        Log.d(TAG, "Api request for token detail");
        //headerParams.add(new CustomHTTPParams(TOKEN_AUTHENTICATION, String.valueOf(merchantToken)));
        return HTTPOperationController.put(url + UPDATE_SHOPPER, body, CONTENT_TYPE,
                ACCEPT, headerParams);
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
        headerParams.add(new CustomHTTPParams(TOKEN_AUTHENTICATION, String.valueOf(merchantToken)));
    }

    /**
     * get sdk initilize data from server
     *
     * @param baseCurrency - currency to base the rates on
     */
    BlueSnapHTTPResponse sdkInit(final String baseCurrency) {

        return HTTPOperationController.get(url + SDK_INIT + BASE_CURRENCY + baseCurrency + CREATE_JWT + "True", CONTENT_TYPE, ACCEPT, headerParams);
    }

    /**
     * create PayPal Token (url)
     *
     * @param amount             - amount to charge
     * @param currency           - currency to charge with
     * @param isShippingRequired - boolean is shipping required
     */
    BlueSnapHTTPResponse createPayPalToken(final Double amount, final String currency, boolean isShippingRequired) {
        String urlString = url + PAYPAL_SERVICE + amount + "&currency=" + currency;
        if (isShippingRequired)
            urlString += PAYPAL_SHIPPING;
        return HTTPOperationController.get(urlString, CONTENT_TYPE, ACCEPT, headerParams);
    }

    /**
     * check transaction status after PayPal transaction occurred
     */
    BlueSnapHTTPResponse retrieveTransactionStatus() {
        return HTTPOperationController.get(url + RETRIEVE_TRANSACTION_SERVICE, CONTENT_TYPE, ACCEPT, headerParams);
    }



    /**
     * create Cardinal JWT
     *
     */
    BlueSnapHTTPResponse createCardinalJWT() {
        String urlString = url + CARDINAL_SERVICE_CREATE_JWT;
        return HTTPOperationController.post(urlString, null, CONTENT_TYPE, ACCEPT, headerParams);
    }

    /**
     * process Cardinal result
     */
    BlueSnapHTTPResponse processCardinalResult(String body) {
        String urlString = url + CARDINAL_SERVICE_PROCESS_RESULT;
        return HTTPOperationController.post(urlString, body, CONTENT_TYPE, ACCEPT, headerParams);
    }

}
