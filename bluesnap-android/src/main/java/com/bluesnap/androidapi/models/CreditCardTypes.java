package com.bluesnap.androidapi.models;

import android.util.Log;

import com.bluesnap.androidapi.R;

import java.util.HashMap;
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
    public static final String NEWCARD = "NewCard";

    public static CreditCardTypes getInstance() {
        return INSTANCE;
    }

    public static String getType(String number) {
        LinkedHashMap<String, String> creditCardRegex = creditCardRegex();
        for (HashMap.Entry<String, String> entry : creditCardRegex.entrySet()) {
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

    private static LinkedHashMap<String, String> creditCardRegex() {
        LinkedHashMap<String, String> creditCardRegex = new LinkedHashMap<>();
        creditCardRegex.put("ELO", "^(40117[8-9]|431274|438935|451416|457393|45763[1-2]|504175|506699|5067[0-6][0-9]|50677[0-8]|509[0-9][0-9][0-9]|636368|636369|636297|627780).*");
        creditCardRegex.put("HIPERCARD", "^(606282|637095).*");
        creditCardRegex.put("CENCOSUD", "^603493.*");
        creditCardRegex.put("NARANJA", "^589562.*");
        creditCardRegex.put("TARJETASHOPPING", "^(603488|(27995[0-9])).*");
        creditCardRegex.put("ARGENCARD", "^(501105).*");
        creditCardRegex.put("CABAL", "^((627170)|(589657)|(603522)|(604((20[1-9])|(2[1-9][0-9])|(3[0-9]{2})|(400)))).*");
        creditCardRegex.put("VISA", "^4.+");
        creditCardRegex.put("MASTERCARD", "^(5(([1-5])|(0[1-5]))|2(([2-6])|(7(1|20)))|6((0(0[2-9]|1[2-9]|2[6-9]|[3-5]))|(2((1(0|2|3|[5-9]))|20|7[0-9]|80))|(60|3(0|[3-9]))|(4[0-2]|[6-8]))).+");
        creditCardRegex.put("AMEX", "^3(24|4[0-9]|7|56904|379(41|12|13)).+");
        creditCardRegex.put("DISCOVER", "^(3[8-9]|(6((01(1|300))|4[4-9]|5))).+");
        creditCardRegex.put("DINERS", "^(3(0([0-5]|9|55)|6)).*");
        creditCardRegex.put("JCB", "^(2131|1800|35).*");
        creditCardRegex.put("CHINA_UNION_PAY", "(^62(([4-6]|8)[0-9]{13,16}|2[2-9][0-9]{12,15}))$");
        creditCardRegex.put("CARTE_BLEUE", "^((3(6[1-4]|77451))|(4(059(?!34)|150|201|561|562|533|556|97))|(5(0[1-4]|13|30066|341[0-1]|587[0-2]|6|8))|(6(27244|390|75[1-6]|799999998))).*");
        return creditCardRegex;
    }

    private static String getCardTypeResource(String cardTypeResourceName) {
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