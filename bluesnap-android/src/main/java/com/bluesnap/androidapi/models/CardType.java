package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.AndroidUtil;

import java.util.regex.Pattern;

public class CardType {
    public static final String AMEX = "American Express";
    public static final String DISCOVER = "Discover";
    public static final String JCB = "JCB";
    public static final String DINERS = "Diners Club";
    public static final String VISA = "Visa";
    public static final String MASTERCARD = "MasterCard";
    public static final String CHINA_UNION_PAY = "China Union Pay";
    public static final String CARTE_BLEUE ="Carte Bleue";
    public static final String CABAL ="Cabal";
    public static final String ARGENCARD ="Argencard";
    public static final String TARJETASHOPPING ="Tarjeta Shopping";
    public static final String NARANJA ="Naranja";
    public static final String CENCOSUD ="Cencosud";
    public static final String IPERCARD ="Hipercard";
    public static final String ELO ="Elo";
    public static final String UNKNOWN = "Unknown";

    public static String PREFIXES_AMEX = "^3(24|4[0-9]|7|56904|379(41|12|13)).+";
    public static String PREFIXES_DISCOVER = "^(3[8-9]|(6((01(1|300))|4[4-9]|5))).+";
    public static String PREFIXES_JCB = "^(2131|1800|35).*";
    public static String PREFIXES_DINERS = "^(3(0([0-5]|9|55)|6)).*";
    public static String PREFIXES_VISA = "^4.+";
    public static String PREFIXES_MASTERCARD = "^(5(([1-5])|(0[1-5]))|2(([2-6])|(7(1|20)))|6((0(0[2-9]|1[2-9]|2[6-9]|[3-5]))|(2((1(0|2|3|[5-9]))|20|7[0-9]|80))|(60|3(0|[3-9]))|(4[0-2]|[6-8]))).+";
    public static String PREFIXES_CHINA_UNION_PAY = "(^62(([4-6]|8)[0-9]{13,16}|2[2-9][0-9]{12,15}))$";
    public static String PREFIXES_CARTE_BLEUE;
    public static String PREFIXES_CABAL;
    public static String PREFIXES_ARGENCARD;
    public static String PREFIXES_TARJETASHOPPING;
    public static String PREFIXES_NARANJA;
    public static String PREFIXES_CENCOSUD;
    public static String PREFIXES_IPERCARD;
    public static String PREFIXES_ELO;

    public static String getType(String number) {
        if (!AndroidUtil.isBlank(number)) {
            if (Pattern.matches(PREFIXES_VISA, number)) {
                return VISA;
            } else if (Pattern.matches(PREFIXES_MASTERCARD, number)) {
                return MASTERCARD;
            } else if (Pattern.matches(PREFIXES_AMEX, number)) {
                return AMEX;
            } else if (Pattern.matches(PREFIXES_DISCOVER, number)) {
                return DISCOVER;
            } else if (Pattern.matches(PREFIXES_DINERS, number)) {
                return DINERS;
            } else if (Pattern.matches(PREFIXES_JCB, number)) {
                return JCB;
            } else if (Pattern.matches(PREFIXES_CHINA_UNION_PAY, number)) {
                return CHINA_UNION_PAY;
            } else {
                return UNKNOWN;
            }
        }
        return UNKNOWN;
    }


    public static boolean validateByType(String type, String number) {
        return number.length() > 11 && number.length() < 20;
    }
}
