package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by roy.biber on 07/11/2017.
 */


//TODO: join cardtype class with this class or conjoin or something
public class SupportedPaymentMethods {
    private static final String TAG = SupportedPaymentMethods.class.getSimpleName();

    public static final String PAYPAL = "PAYPAL";
    public static final String CC = "CC";
    public static final String USD = "USD";

    @SerializedName("paymentMethods")
    private ArrayList<String> paymentMethods;
    @Nullable
    @SerializedName("paypalCurrencies")
    private ArrayList<String> paypalCurrencies;
    @SerializedName("creditCardBrands")
    private ArrayList<String> creditCardBrands;
    @SerializedName("creditCardTypes")
    private ArrayList<String> creditCardTypes;
    @SerializedName("creditCardRegex")
    private HashMap<String, String> creditCardRegex;

    public ArrayList<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(ArrayList<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    @Nullable
    public ArrayList<String> getPaypalCurrencies() {
        return paypalCurrencies;
    }

    public void setPaypalCurrencies(@Nullable ArrayList<String> paypalCurrencies) {
        this.paypalCurrencies = paypalCurrencies;
    }

    public ArrayList<String> getCreditCardBrands() {
        return creditCardBrands;
    }

    public void setCreditCardBrands(ArrayList<String> creditCardBrands) {
        this.creditCardBrands = creditCardBrands;
    }

    public ArrayList<String> getCreditCardTypes() {
        return creditCardTypes;
    }

    public void setCreditCardTypes(ArrayList<String> creditCardTypes) {
        this.creditCardTypes = creditCardTypes;
    }

    public HashMap<String, String> getCreditCardRegex() {
        return creditCardRegex;
    }

    public void setCreditCardRegex(HashMap<String, String> creditCardRegex) {
        this.creditCardRegex = creditCardRegex;
    }

    public boolean isPaymentMethodActive(String paymentMethod) {
        ArrayList<String> arr = paymentMethods;
        Boolean res = false;
        for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).equals(paymentMethod)) {
                    res = true;
                    break;
                }
        }
        return res;
    }
}
