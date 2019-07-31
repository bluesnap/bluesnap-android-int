package com.bluesnap.androidapi.models;

import android.util.Log;

import com.bluesnap.androidapi.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class CreditCardTypeResolver {

    private static final String TAG = CreditCardTypeResolver.class.getSimpleName();
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

    private static Map<String, String> creditCardTypes;

    static {
        creditCardTypes = new HashMap<>();
        creditCardTypes.put("AMEX", "American Express");
        creditCardTypes.put("DISCOVER", "Discover");
        creditCardTypes.put("JCB", "JCB");
        creditCardTypes.put("DINERS", "Diners Club");
        creditCardTypes.put("VISA", "Visa");
        creditCardTypes.put("MASTERCARD", "MasterCard");
        creditCardTypes.put("CHINA_UNION_PAY", "China Union Pay");
        creditCardTypes.put("CARTE_BLEUE", "Carte Bleue");
        creditCardTypes.put("CABAL", "Cabal");
        creditCardTypes.put("ARGENCARD", "Argencard");
        creditCardTypes.put("TARJETASHOPPING", "Tarjeta Shopping");
        creditCardTypes.put("NARANJA", "Naranja");
        creditCardTypes.put("CENCOSUD", "Cencosud");
        creditCardTypes.put("HIPERCARD", "Hipercard");
        creditCardTypes.put("ELO", "Elo");
        creditCardTypes.put("UNKNOWN", "Unknown");
        creditCardTypes.put("NEWCARD", "NEWCARD");
    }

    private static final CreditCardTypeResolver INSTANCE = new CreditCardTypeResolver();

    public static CreditCardTypeResolver getInstance() {
        return INSTANCE;
    }

    protected CreditCardTypeResolver() {
    }

    private static LinkedHashMap<String, String> creditCardRegex = new LinkedHashMap<>();

    /**
     * @param number - credit card number
     * @return Card Type Resource String
     */
    public String getType(String number) {
        number = (number != null) ? number.trim().replaceAll("\\s+|-", "") : "";
        for (LinkedHashMap.Entry<String, String> entry : creditCardRegex.entrySet()) {
            if (Pattern.matches(entry.getValue(), number))
                return getCardTypeResource(entry.getKey());
        }
        return UNKNOWN;
    }

    boolean validateByType(String type, String number) {
        return number.length() > 11 && number.length() < 20;
    }

    /**
     * @param type - receive string type
     * @return Card Type Drawable
     */
    public int getCardTypeDrawable(final String type) {
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
        else
            cardDrawable = R.drawable.default_card;
        /*else if (CARTE_BLEUE.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (CABAL.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (ARGENCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (TARJETASHOPPING.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (NARANJA.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (CENCOSUD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (HIPERCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (ELO.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;
        else if (NEWCARD.equalsIgnoreCase(type))
            cardDrawable = R.drawable.default_card;*/

        return cardDrawable;
    }

    public static void setCreditCardRegex(LinkedHashMap<String, String> newCreditCardRegex) {
        creditCardRegex = newCreditCardRegex;
    }

    /**
     * @param cardTypeResourceName - server name representation of credit card type
     * @return client side name representation of credit card type
     */
    public String getCardTypeResource(String cardTypeResourceName) {

        String result = creditCardTypes.get(cardTypeResourceName);

        return (result == null ? UNKNOWN : result);

    }
}