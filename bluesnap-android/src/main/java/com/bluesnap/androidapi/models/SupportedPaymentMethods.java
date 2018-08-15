package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by roy.biber on 07/11/2017.
 */


//TODO: join cardtype class with this class or conjoin or something
public class SupportedPaymentMethods extends BSModel {
    private static final String TAG = SupportedPaymentMethods.class.getSimpleName();

    public static final String PAYPAL = "PAYPAL";
    public static final String CC = "CC";
    public static final String USD = "USD";

    private ArrayList<String> paymentMethods;
    @Nullable
    private ArrayList<String> paypalCurrencies;
    private ArrayList<String> creditCardBrands;
    private ArrayList<String> creditCardTypes;
    private LinkedHashMap<String, String> creditCardRegex;

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

    public LinkedHashMap<String, String> getCreditCardRegex() {
        return creditCardRegex;
    }

    public void setCreditCardRegex(LinkedHashMap<String, String> creditCardRegex) {
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


    @NonNull
    @Override
    public JSONObject toJson() {
        return null;
    }



}
