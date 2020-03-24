package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by roy.biber on 07/11/2017.
 */


//TODO: join cardtype class with this class or conjoin or something
public class SupportedPaymentMethods extends BSModel {
    private static final String TAG = SupportedPaymentMethods.class.getSimpleName();

    public static final String PAYPAL = "PAYPAL";
    public static final String GOOGLE_PAY = "GOOGLE_PAY";
    public static final String GOOGLE_PAY_TOKENIZED_CARD = "GOOGLE_PAY_TOKENIZED_CARD";
    public static final String CC = "CC";
    public static final String USD = "USD";

    private HashMap<String, Boolean> paymentMethods = new HashMap<>();
    @Nullable
    private ArrayList<String> paypalCurrencies;
    private ArrayList<String> creditCardBrands;
    private ArrayList<String> creditCardTypes;
    private LinkedHashMap<String, String> creditCardRegex;

    public HashMap<String, Boolean> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethod(String paymentMethod) {
        paymentMethods.put(paymentMethod, true);
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

        Boolean res = paymentMethods.get(paymentMethod);

        return ((res == null) ? false : res);
    }

    public void setPaymentMethodActive(String paymentMethod, Boolean activate) {

        if(paymentMethods.containsKey(paymentMethod))
            paymentMethods.put(paymentMethod, activate);
    }

    @NonNull
    @Override
    public JSONObject toJson() {
        return null;
    }


    /**
     * Set Payment Methods according to merchant configurations
     *
     * @param paymentMethods  - The name of the extra data, with package prefix.
     */
    public void setPaymentMethods(HashMap<String, Boolean> paymentMethods) {

        for (Map.Entry<String, Boolean> entry : paymentMethods.entrySet()) {
            setPaymentMethodActive(entry.getKey(), entry.getValue());
        }



    }

}
