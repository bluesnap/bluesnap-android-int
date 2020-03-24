package com.bluesnap.androidapi.services;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.models.ShippingContactInfo;

import java.util.Arrays;
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
                && creditCardExpiryDateValidation(creditCard.getExpirationYear(), creditCard.getExpirationMonth());
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
    public static boolean isDateInFuture(int month, int year) {
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
                return cvv.length() == 4;
            } else return cvv.length() == 3;
        } else return false;

    }

    /**
     * Check if Country has a State Field requirement (Required)
     *
     * @param countryText - ISO 3166-1 alpha-2 standard
     */
    public static boolean checkCountryHasState(String countryText) {
        for (String item : STATE_NEEDED_COUNTRIES) {
            if (item.equalsIgnoreCase(countryText)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if Country has a Zip Field requirement (Required)
     *
     * @param countryText - ISO 3166-1 alpha-2 standard
     * @return true if country has zip, false w.s.
     */
    public static boolean checkCountryHasZip(String countryText) {
        return !Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(countryText.toUpperCase());
    }

    /**
     * validate EditText by it's validation type
     *
     * @param editTextString - editText String
     * @param validationType - type of validation taken from EditTextFields Enum
     * @see EditTextFields
     */
    public static boolean validateEditTextString(String editTextString, EditTextFields validationType) {
        String regex = "^[a-zA-Z0-9- ]*$";
        String editTextStringNoSpaces = editTextString.trim().replaceAll(" ", "");
        String[] splittedNames = editTextString.trim().split(" ");

        if (TextUtils.isEmpty(editTextStringNoSpaces) || editTextStringNoSpaces.length() < 2 || TextUtils.isEmpty(validationType.toString()))
            return false;

        if ((EditTextFields.NAME_FIELD.equals(validationType)) && (splittedNames.length < 2)) {
            return false;
        } else if ((EditTextFields.ZIP_FIELD.equals(validationType)) && (!editTextString.matches(regex))) {
            return false;
        } else if ((EditTextFields.EMAIL_FIELD.equals(validationType)) && (!Patterns.EMAIL_ADDRESS.matcher(editTextString).matches())) {
            return false;
        } else return (!EditTextFields.STATE_FIELD.equals(validationType)) || (editTextString.length() == 2);
    }


    /**
     * Billing Info Validation
     *
     * @param billingContactInfo           {@link BillingContactInfo}
     * @param isEmailRequired       is Email Required
     * @param isFullBillingRequired is Full Billing Required
     */
    public static boolean billingInfoValidation(@NonNull BillingContactInfo billingContactInfo, boolean isEmailRequired, boolean isFullBillingRequired) {
        boolean validInput = contactInfoValidation(billingContactInfo, isFullBillingRequired);
        if (isEmailRequired)
            validInput &= BlueSnapValidator.validateEditTextString(AndroidUtil.stringify((billingContactInfo.getEmail())), BlueSnapValidator.EditTextFields.EMAIL_FIELD);
        return validInput;
    }

    /**
     * Shipping Info Validation
     *
     * @param shippingContactInfo {@link ShippingContactInfo}
     */
    public static boolean shippingInfoValidation(@NonNull ShippingContactInfo shippingContactInfo) {
        return contactInfoValidation(shippingContactInfo, true);
    }

    /**
     * Contact Info Validation
     *
     * @param contactInfo                       {@link ContactInfo}
     * @param isFullBillingRequiredOrIsShipping - boolean, if shipping or if full billing is required - true
     */
    private static boolean contactInfoValidation(@NonNull ContactInfo contactInfo, boolean isFullBillingRequiredOrIsShipping) {
        boolean validInput = BlueSnapValidator.validateEditTextString(AndroidUtil.stringify(contactInfo.getFullName()), BlueSnapValidator.EditTextFields.NAME_FIELD);

        String country = AndroidUtil.stringify(contactInfo.getCountry());
        if (checkCountryHasZip(country))
            validInput &= BlueSnapValidator.validateEditTextString(AndroidUtil.stringify(contactInfo.getZip()), BlueSnapValidator.EditTextFields.ZIP_FIELD);

        if (isFullBillingRequiredOrIsShipping) {
            if (BlueSnapValidator.checkCountryHasState(country))
                validInput &= BlueSnapValidator.validateEditTextString(AndroidUtil.stringify(contactInfo.getState()), BlueSnapValidator.EditTextFields.STATE_FIELD);

            validInput &= BlueSnapValidator.validateEditTextString(AndroidUtil.stringify(contactInfo.getCity()), BlueSnapValidator.EditTextFields.CITY_FIELD);
            validInput &= BlueSnapValidator.validateEditTextString(AndroidUtil.stringify(contactInfo.getAddress()), BlueSnapValidator.EditTextFields.ADDRESS_FIELD);
        }

        return validInput;
    }

    /**
     * validate Store Card switch - the shopper consent to store the credit card details, in case it is mandatory.
     * The shopper consent is mandatory only in the following cases: choose new card as payment method flow (shopper configuration), subscription mode.
     *
     * @param isShopperRequirements - is shopper configuration flow
     * @param isSubscriptionCharge  - is subscription mode
     * @param isStoreCard           - the switch value
     * @see EditTextFields
     */
    public static boolean validateStoreCard(boolean isShopperRequirements, boolean isSubscriptionCharge, boolean isStoreCard) {
        return (isShopperRequirements || isSubscriptionCharge) ? isStoreCard : true;
    }

}
