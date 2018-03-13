package com.bluesnap.androidapi.services;

import android.text.TextUtils;
import android.util.Patterns;

import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;

import java.util.Calendar;

/**
 * Created by roy.biber on 25/02/2018.
 */

public class BlueSnapValidator {
    private static Calendar calendarInstance;
    public static final String[] STATE_NEEDED_COUNTRIES = {"US", "BR", "CA"};

    /**
     * EditText Field Names
     */
    public enum EditTextFields {
        NAME_FIELD, COUNTRY_FIELD, STATE_FIELD, CITY_FIELD, ADDRESS_FIELD, ZIP_FIELD, EMAIL_FIELD, PHONE_FIELD
    }

    public static BlueSnapValidator getInstance() {
        return BlueSnapValidatorHOLDER.INSTANCE;
    }

    private static class BlueSnapValidatorHOLDER {
        public static final BlueSnapValidator INSTANCE = new BlueSnapValidator();
    }

    /**
     * Credit Card Number Validation
     *
     * @param creditCard - a CreditCard Object
     * @see CreditCard
     */
    public static boolean creditCardFullValidation(CreditCard creditCard) {
        return creditCardNumberValidation(creditCard.getNumber())
                && creditCardCVVValidation(creditCard.getCvc(), creditCard.getCardType())
                && creditCardExpiryDateValidation(creditCard.getExpirationYear(), creditCard.getExpirationYear());
    }

    /**
     * Credit Card Only Luhn Number Validation
     *
     * @param number - credit card number
     */
    private static boolean creditCardLuhnNumberValidation(String number) {
        boolean isOdd = true;
        int sum = 0;

        for (int index = number.length() - 1; index >= 0; index--) {
            char c = number.charAt(index);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digitInteger = Integer.parseInt("" + c);
            isOdd = !isOdd;

            if (isOdd) {
                digitInteger *= 2;
            }

            if (digitInteger > 9) {
                digitInteger -= 9;
            }

            sum += digitInteger;
        }

        return sum % 10 == 0;
    }

    /**
     * Credit Card Number Validation
     *
     * @param number - credit card number
     */
    public static boolean creditCardNumberValidation(String number) {
        if (TextUtils.isEmpty(number))
            return false;

        String rawNumber = number.trim().replaceAll("\\s+|-", "");
        return !(TextUtils.isEmpty(rawNumber) || !creditCardLuhnNumberValidation(rawNumber))
                && (number.length() > 11 && number.length() < 20);
    }

    /**
     * Credit Card Expiry Date Validation
     *
     * @param expirationYear  - expiration date, Year
     * @param expirationMonth - expiration date, Month
     */
    public static boolean creditCardExpiryDateValidation(int expirationYear, int expirationMonth) {
        return !(expirationMonth > 12 || expirationMonth < 1) && isDateInFuture(expirationMonth, expirationYear);
    }

    /**
     * Credit Card Expiry Date Validation
     *
     * @param expDateString - expiration date from TextView MM/YY
     */
    public static boolean creditCardExpiryDateValidation(String expDateString) {
        int mm, yy;
        try {
            String[] mmyy = expDateString.split("\\/");
            mm = (Integer.valueOf(mmyy[0]));
            yy = (Integer.valueOf(mmyy[1]));
            return creditCardExpiryDateValidation(yy, mm);
        } catch (Exception e1) {
            try {
                String[] mmyy = expDateString.split("/");
                if (mmyy.length < 2 || TextUtils.isEmpty(mmyy[0]) || TextUtils.isEmpty(mmyy[1]))
                    return creditCardExpiryDateValidation(Integer.valueOf(mmyy[1]), Integer.valueOf(mmyy[0]));
            } catch (Exception e2) {
                return false;
            }
        }
        return false;
    }

    /**
     * returns a Calendar Instance
     */
    private static Calendar getCalendarInstance() {
        return calendarInstance != null ? (Calendar) calendarInstance.clone() : Calendar.getInstance();
    }

    /**
     * Check if received date >= current month and current year
     *
     * @param year  - expiration date, Year
     * @param month - expiration date, Month
     */
    private static boolean isDateInFuture(int month, int year) {
        Calendar now = getCalendarInstance();
        int currentYear = now.get(Calendar.YEAR);
        if (year < 2000) {
            year += 2000;
        }
        return (year > currentYear && year < (11 + currentYear)) || (year == currentYear && month >= (now.get(Calendar.MONTH) + 1));
    }

    /**
     * Credit Card CVV Validation
     *
     * @param cvv      - credit card cvv number
     * @param cardType - card type associated to the cvv
     */
    public static boolean creditCardCVVValidation(String cvv, String cardType) {
        if (TextUtils.isEmpty(cvv)) {
            return false;
        }
        if (cvv.length() >= 3 && cvv.length() < 5) {
            if (null != cardType && CreditCardTypeResolver.AMEX.equals(cardType)) {
                if (cvv.length() != 4)
                    return false;
            } else if (cvv.length() != 3)
                return false;
        } else return false;

        return true;
    }

    /**
     * Check if Country has a State Field requirement (Required)
     *
     * @param countryText - ISO 3166-1 alpha-2 standard
     */
    public static boolean checkCountryForState(String countryText) {
        for (String item : STATE_NEEDED_COUNTRIES) {
            if (item.equals(countryText)) {
                return true;
            }
        }
        return false;
    }

    /**
     * validate EditText by it's validation type
     *
     * @param editTextString - editText String
     * @param validationType - type of validation taken from EditTextFields Enum
     * @see EditTextFields
     */
    public static boolean validateEditTextString(String editTextString, EditTextFields validationType) {
        String regex = "^[a-zA-Z0-9-]*$";
        String editTextStringNoSpaces = editTextString.trim().replaceAll(" ", "");
        String[] splittedNames = editTextString.trim().split(" ");

        if (TextUtils.isEmpty(editTextStringNoSpaces) || editTextStringNoSpaces.length() < 2 || TextUtils.isEmpty(validationType.toString()))
            return false;

        if ((EditTextFields.NAME_FIELD.equals(validationType)) && (splittedNames.length < 2 || splittedNames[0].length() < 2)) {
            return false;
        } else if ((EditTextFields.ZIP_FIELD.equals(validationType)) && (!editTextString.matches(regex))) {
            return false;
        } else if ((EditTextFields.EMAIL_FIELD.equals(validationType)) && (!Patterns.EMAIL_ADDRESS.matcher(editTextString).matches())) {
            return false;
        } else if ((EditTextFields.STATE_FIELD.equals(validationType)) && (editTextString.length() != 2)) {
            return false;
        } else {
            return true;
        }
    }

}