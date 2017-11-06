package com.bluesnap.androidapi.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SDKInitData {

    public static final String KOUNTMERCHANTID = "kountMerchantId";

    public static final String RATES = "rates";
    public static final String BASECURRENCY = "baseCurrency";
    public static final String EXCHANGERATE = "exchangeRate";

    public static final String SHOPPER = "shopper";
    public static final String PAYMENTSOURCES = "paymentSources";
    public static final String CREDITCARDINFO = "creditCardInfo";
    public static final String SHIPPINGCONTACTINFO = "shippingContactInfo";
    public static final String LASTPAYMENTINFO = "lastPaymentInfo";

    public static final String SUPPORTEDPAYMENTMETHODS = "supportedPaymentMethods";
    public static final String PAYMENTMETHODS = "paymentMethods";
    public static final String PAYPALCURRENCIES = "paypalCurrencies";
    public static final String CREDITCARDREGEX = "creditCardRegex";

    private String kountMerchantId;
    private JSONObject shopper;
    private JSONObject rates;
    private JSONObject supportedPaymentMethods;

    public SDKInitData(JSONObject sdkInitData) {
        kountMerchantId = (String) getObjectFromJsonObject(sdkInitData, KOUNTMERCHANTID);
        rates = (JSONObject) getObjectFromJsonObject(sdkInitData, RATES);
        shopper = (JSONObject) getObjectFromJsonObject(sdkInitData, SHOPPER);
        supportedPaymentMethods = (JSONObject) getObjectFromJsonObject(sdkInitData, SUPPORTEDPAYMENTMETHODS);
    }

    private Object getObjectFromJsonObject(JSONObject jsonObject, String key) {
        Object response = null;
        try {
            if (null != jsonObject.optJSONObject(key))
                response = jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getKountMerchantId() {
        return kountMerchantId;
    }

    public String getBaseCurrency() {
        return (String) getObjectFromJsonObject(rates, BASECURRENCY);
    }

    public JSONArray getExchangeRate() {
        return (JSONArray) getObjectFromJsonObject(rates, EXCHANGERATE);
    }

    public JSONArray getCreditCardInfo() {
        return (JSONArray) getObjectFromJsonObject((JSONObject) getObjectFromJsonObject(shopper, PAYMENTSOURCES), CREDITCARDINFO);
    }

    public JSONObject getShippingContactInfo() {
        return (JSONObject) getObjectFromJsonObject(shopper, SHIPPINGCONTACTINFO);
    }

    public JSONObject getLastPaymentInfo() {
        return (JSONObject) getObjectFromJsonObject(shopper, LASTPAYMENTINFO);
    }

    public JSONObject getSupportedPaymentMethods() {
        return supportedPaymentMethods;
    }

    public JSONArray getPaymentMethods() {
        return paymentMethods;
    }

    public JSONArray getPaypalCurrencies() {
        return paypalCurrencies;
    }

    public JSONObject getCreditCardRegex() {
        return creditCardRegex;
    }

}
