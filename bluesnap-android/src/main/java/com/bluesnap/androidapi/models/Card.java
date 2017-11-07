package com.bluesnap.androidapi.models;

import android.text.TextUtils;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;

/**
 * Credit card representation and validation.
 */
public class Card {

    private transient String number;
    private String cvc;
    private String expDateString;
    private Integer expMonth;
    private Integer expYear;
    private String holderName;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZip;
    private String addressCountry;
    private String last4;
    private String type;
    private String country;
    private transient boolean modified = false;
    private boolean tokenizedSuccess = false;

    public Card(String number, Integer expMonth, Integer expYear, String cvc, String holderName, String addressStreet, String addressCity, String addressState, String addressZip, String addressCountry, String last4, String type, String country) {
        this.number = normalizeCardNumber(number);
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvc = cvc.trim();
        this.holderName = holderName;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZip = addressZip;
        this.addressCountry = addressCountry;
        this.last4 = last4.trim();
        this.type = type;
        this.country = country;
        this.type = CardType.getType(number);
        this.last4 = getLast4();

    }

    public Card() {

    }

    public static boolean validateExpiryDate(int expYear, int expMonth) {
        if (expMonth > 12 || expMonth < 1)
            return false;
        return AndroidUtil.isDateInFuture(expMonth, expYear);
    }

    public static boolean validateExpiryDate(String expDateString) {
        int mm, yy;
        try {
            String[] mmyy = expDateString.split("\\/");
            mm = (Integer.valueOf(mmyy[0]));
            yy = (Integer.valueOf(mmyy[1]));
            return validateExpiryDate(yy, mm);
        } catch (Exception e1) {
            try {
                String[] mmyy = expDateString.split("/");
                if (mmyy.length < 2 || TextUtils.isEmpty(mmyy[0]) || TextUtils.isEmpty(mmyy[1]))
                    return validateExpiryDate(Integer.valueOf(mmyy[1]), Integer.valueOf(mmyy[0]));
            } catch (Exception e2) {
                return false;
            }
        }
        return false;
    }

    public static boolean isValidLuhnNumber(String number) {
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

    public void update(String creditCardNumberEditTextText, String expDateString, String cvvText, String addressZip, String holderName) {
        setExpDateFromString(expDateString);
        this.cvc = cvvText;
        this.addressZip = addressZip;
        this.holderName = holderName;
        modified = true;
        tokenizedSuccess = false;
        this.number = creditCardNumberEditTextText;
        type = CardType.getType(number);
    }

    private void setLast4() {
        this.last4 = number.substring(number.length() - 4, number.length());
    }

    private void setExpDateFromString(String expDateString) {
        expMonth = 0;
        expYear = 0;

        try {
            String[] mmyy = expDateString.split("\\/");
            this.setExpMonth(Integer.valueOf(mmyy[0]));
            this.setExpYear(Integer.valueOf(mmyy[1]));
            return;
        } catch (Exception e) {

        }
        try {
            String[] mmyy = expDateString.split("/");
            if (mmyy.length < 2 || TextUtils.isEmpty(mmyy[0]) || TextUtils.isEmpty(mmyy[1]))
                return;
            this.setExpMonth(Integer.valueOf(mmyy[0]));
            this.setExpYear(Integer.valueOf(mmyy[1]));
            return;
        } catch (Exception e) {
            Log.e("setEX", "setexp", e);
        }
    }

    public boolean validateAll() {
        if (cvc == null) {
            return validateNumber() && validateExpiryDate();
        } else {
            return validateNumber() && validateExpiryDate() && validateCVC();
        }
    }

    public boolean validateNumber() {
        if (AndroidUtil.isBlank(number)) {
            return false;
        }
        String rawNumber = number.trim().replaceAll("\\s+|-", "");
        if (AndroidUtil.isBlank(rawNumber)
                || !isValidLuhnNumber(rawNumber)) {
            return false;
        }
        type = CardType.getType(number);
        setLast4();
        return CardType.validateByType(type, rawNumber);
    }

    public boolean validateExpiryDate() {
        return validateExpiryDate(this.expYear, this.expMonth);
    }

    public boolean validateCVC() {
        if (AndroidUtil.isBlank(cvc)) {
            return false;
        }
        if (cvc.length() >= 3 && cvc.length() < 5) {
            if (CardType.AMEX.equals(type)) {
                if (cvc.length() != 4)
                    return false;
            } else if (cvc.length() != 3)
                return false;
        } else return false;

        return true;
    }

    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", "");
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCVC() {
        return cvc;
    }

    public void setCVC(String cvc) {
        this.cvc = cvc;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        if (expYear < 2000) {
            expYear += 2000;
        }
        this.expYear = expYear;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getLast4() {
        if (!AndroidUtil.isBlank(last4)) {
            return last4;
        }
        if (number != null && number.length() > 4) {
            return number.substring(number.length() - 4, number.length());
        }
        return null;
    }


    public String getCountry() {
        return country;
    }

    public String getExpDate() {
        if (expYear < 2000) {
            expYear += 2000;
        }
        return expMonth + "/" + expYear;
    }

    public String getExpDateForEditText() {
        int m = expYear;
        if (m > 2000) {
            m -= 2000;
        }
        return expMonth + "/" + m;
    }

    public String getType() {
        return type;
    }

    public void setTokenizationSucess() {
        tokenizedSuccess = true;
    }

    public void setModified() {
        modified = true;
        tokenizedSuccess = false;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean validForReuse() {
        return last4 != null && validateExpiryDate() && tokenizedSuccess;
    }

    public boolean requireValidation() {
        return modified || last4 == null;
    }

    @Override
    public String toString() {
        return "Card{" +
                "type='" + type + '\'' +
                ", tokenizedSuccess=" + tokenizedSuccess +
                ", modified=" + modified +
                ", last4='" + last4 + '\'' +
                '}';
    }
}