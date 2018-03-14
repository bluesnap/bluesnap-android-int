package com.bluesnap.androidapi.models;

import android.util.Log;

import com.bluesnap.androidapi.R;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class CreditCardTypes {
    private static final String TAG = CreditCardTypes.class.getSimpleName();
    private static final CreditCardTypes INSTANCE = new CreditCardTypes();
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
    public static final String HIPERCARD = "Hipercard";
    public static final String ELO = "Elo";
    public static final String UNKNOWN = "Unknown";
    public static final String NEWCARD = "NEWCARD";

    public static CreditCardTypes getInstance() {
        return INSTANCE;
    }

    private static LinkedHashMap<String, String> creditCardRegex = new LinkedHashMap<>();

    public static String getType(String number) {
         for (LinkedHashMap.Entry<String, String> entry : creditCardRegex.entrySet()) {
            if (Pattern.matches(entry.getValue(), number))
                return getCardTypeResource(entry.getKey());
        }
        return UNKNOWN;
    }

    static boolean validateByType(String type, String number) {
        return number.length() > 11 && number.length() < 20;
    }

    public static int getCardTypeDrawable(final String type) {
        int cardDrawable = 0;
        if (null == type)
            return cardDrawable;

        if (AMEX.equalsIgnoreCase(type))
            cardDrawable = R.drawable.amex_dark;
        else if (VISA.equalsIgnoreCase(type))
            cardDrawable = R.drawable.visa_dark;
        else if (MASTERCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.mastercard_dark;
        else if (DISCOVER.equalsIgnoreCase(type))
            cardDrawable = R.drawable.discover_dark;
        else if (DINERS.equalsIgnoreCase(type))
            cardDrawable = R.drawable.dinersclub_dark;
        else if (JCB.equalsIgnoreCase(type))
            cardDrawable = R.drawable.jcb_dark;
        else if (CHINA_UNION_PAY.equalsIgnoreCase(type))
            cardDrawable = R.drawable.unionpay_dark;
        else if (CARTE_BLEUE.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (CABAL.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (ARGENCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (TARJETASHOPPING.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (NARANJA.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (CENCOSUD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (HIPERCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (ELO.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_dark;
        else if (NEWCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.add_new_card_dark;

        return cardDrawable;
    }

    public static void setCreditCardRegex(LinkedHashMap<String, String> newCreditCardRegex) {
        creditCardRegex = newCreditCardRegex;
    }

    public static String getCardTypeResource(String cardTypeResourceName) {
        try {
            return (String) CreditCardTypes.class.getDeclaredField(cardTypeResourceName).get(null);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "getCardTypeResource IllegalAccessException: ", e);
            return UNKNOWN;
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "getCardTypeResource NoSuchFieldException: ", e);
            return UNKNOWN;
        }
    }
}