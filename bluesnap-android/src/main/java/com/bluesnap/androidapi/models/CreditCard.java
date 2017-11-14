package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;
import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCard {
    public static final String CCNUMBER = "ccNumber";
    public static final String CVV = "cvv";
    public static final String EXPDATE = "expDate";

    private transient String number;
    private String cvc;
    private transient boolean modified = false;
    private boolean tokenizedSuccess = false;

    @SerializedName("cardLastFourDigits")
    private String cardLastFourDigits;
    @SerializedName("cardType")
    private String cardType;
    @Nullable
    @SerializedName("cardSubType")
    private String cardSubType;
    @SerializedName("expirationMonth")
    private Integer expirationMonth;
    @SerializedName("expirationYear")
    private Integer expirationYear;

    public CreditCard() {
    }

    public static boolean validateExpiryDate(int expirationYear, int expirationMonth) {
        return !(expirationMonth > 12 || expirationMonth < 1) && AndroidUtil.isDateInFuture(expirationMonth, expirationYear);
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

    public void update(String creditCardNumberEditTextText, String expDateString, String cvvText) {
        setExpDateFromString(expDateString);
        this.cvc = cvvText;
        modified = true;
        tokenizedSuccess = false;
        this.number = creditCardNumberEditTextText;
        cardType = CreditCardTypes.getType(number);
    }

    private void setLast4() {
        this.cardLastFourDigits = number.substring(number.length() - 4, number.length());
    }

    private void setExpDateFromString(String expDateString) {
        expirationMonth = 0;
        expirationYear = 0;

        try {
            if (!"".equals(expDateString)) {
                String[] mmyy = expDateString.split("\\/");
                this.setExpirationMonth(Integer.valueOf(mmyy[0]));
                this.setExpirationYear(Integer.valueOf(mmyy[1]));
                return;
            }
        } catch (Exception e) {
            Log.e("setEX", "setexp", e);
        }
        try {
            String[] mmyy = expDateString.split("/");
            if (mmyy.length < 2 || TextUtils.isEmpty(mmyy[0]) || TextUtils.isEmpty(mmyy[1]))
                return;
            this.setExpirationMonth(Integer.valueOf(mmyy[0]));
            this.setExpirationYear(Integer.valueOf(mmyy[1]));
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
        cardType = CreditCardTypes.getType(number);
        setLast4();
        return CreditCardTypes.validateByType(cardType, rawNumber);

    }

    public boolean validateExpiryDate() {
        return validateExpiryDate(this.expirationYear, this.expirationMonth);
    }

    public boolean validateCVC() {
        if (AndroidUtil.isBlank(cvc)) {
            return false;
        }
        if (cvc.length() >= 3 && cvc.length() < 5) {
            if (CreditCardTypes.AMEX.equals(cardType)) {
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

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getCardLastFourDigits() {
        if (!AndroidUtil.isBlank(cardLastFourDigits)) {
            return cardLastFourDigits;
        }
        if (number != null && number.length() > 4) {
            return number.substring(number.length() - 4, number.length());
        }
        return null;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Nullable
    public String getCardSubType() {
        return cardSubType;
    }

    public void setCardSubType(@Nullable String cardSubType) {
        this.cardSubType = cardSubType;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        if (expirationYear < 2000) {
            expirationYear += 2000;
        }
        this.expirationYear = expirationYear;
    }

    public String getExpirationDate() {
        if (expirationYear < 2000) {
            expirationYear += 2000;
        }
        return expirationMonth + "/" + expirationYear;
    }

    public String getExpirationDateForEditText() {
        int m = expirationYear;
        if (m > 2000) {
            m -= 2000;
        }
        return expirationMonth + "/" + m;
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
        return cardLastFourDigits != null && validateExpiryDate() && tokenizedSuccess;
    }

    public boolean requireValidation() {
        return modified || cardLastFourDigits == null;
    }

    @Override
    public String toString() {
        return "Card {" +
                "cardType:'" + cardType + '\'' +
                ", tokenizedSuccess:" + tokenizedSuccess +
                ", modified:" + modified +
                ", cardLastFourDigits:'" + cardLastFourDigits + '\'' +
                '}';
    }
}
