package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */


//TODO: join cardtype class with this class or conjoin or something
public class SupportedPaymentMethods {
    private static final String TAG = SupportedPaymentMethods.class.getSimpleName();
    private static final String PAYMENTMETHODS = "paymentMethods";
    private static final String PAYPALCURRENCIES = "paypalCurrencies";
    private static final String CREDITCARDBRANDS = "creditCardBrands";
    private static final String CREDITCARDTYPES = "creditCardTypes";
    private static final String CREDITCARDREGEX = "creditCardRegex";

    public static final String PAYPAL = "PAYPAL";
    public static final String CC = "CC";
    public static final String USD = "USD";

    private JSONArray paymentMethods;
    @Nullable
    private JSONArray paypalCurrencies;
    private JSONArray creditCardBrands;
    private JSONArray creditCardTypes;
    private JSONObject creditCardRegex;

    public SupportedPaymentMethods(@Nullable JSONObject supportedPaymentMethods) {
        paymentMethods = (JSONArray) AndroidUtil.getObjectFromJsonObject(supportedPaymentMethods, PAYMENTMETHODS, TAG);
        paypalCurrencies = (JSONArray) AndroidUtil.getObjectFromJsonObject(supportedPaymentMethods, PAYPALCURRENCIES, TAG);
        creditCardBrands = (JSONArray) AndroidUtil.getObjectFromJsonObject(supportedPaymentMethods, CREDITCARDBRANDS, TAG);
        creditCardTypes = (JSONArray) AndroidUtil.getObjectFromJsonObject(supportedPaymentMethods, CREDITCARDTYPES, TAG);
        creditCardRegex = (JSONObject) AndroidUtil.getObjectFromJsonObject(supportedPaymentMethods, CREDITCARDREGEX, TAG);
    }

    public boolean isPaymentMethodActive(String paymentMethod) {
        JSONArray arr = paymentMethods;
        Boolean res = false;
        for (int i = 0; i < arr.length(); i++) {
            try {
                if (arr.get(i).equals(paymentMethod)) {
                    res = true;
                    break;
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing exception", e);
            }
        }
        return res;
    }
}
