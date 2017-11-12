package com.bluesnap.androidapi.models.returningshopper;


import com.bluesnap.androidapi.services.AndroidUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class CreditCardTypes {
    public static final String AMEX = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String CHINA_UNION_PAY = "China Union Pay";
    public static final String CARTE_BLEUE = "Carte Bleue";
    public static final String CABAL = "Cabal";
    public static final String ARGENCARD = "Argencard";
    public static final String TARJETASHOPPING = "Tarjeta Shopping";
    public static final String NARANJA = "Naranja";
    public static final String CENCOSUD = "Cencosud";
    public static final String IPERCARD = "Hipercard";
    public static final String ELO = "Elo";
    public static final String UNKNOWN = "Unknown";

    public static HashMap<String, String> creditCardTypes;


    public CreditCardTypes(HashMap<String, String> creditCardRegex) {
        creditCardTypes = new HashMap<>();
        for (HashMap.Entry<String, String> entry : creditCardRegex.entrySet()) {
            creditCardTypes.put(entry.getValue(), entry.getKey());
        }

    }

    public static String getType(String number) {
        for (String regex : creditCardTypes.keySet()) {
            if (Pattern.matches(regex, number))
                return creditCardTypes.get(regex);
        }
        return UNKNOWN;
    }

    public static boolean validateByType(String type, String number) {
        return number.length() > 11 && number.length() < 20;
    }
}