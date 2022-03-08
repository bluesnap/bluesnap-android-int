package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bluesnap.androidapi.utils.JsonParser;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class CreditCard extends BSModel {
    private static final String SECURITY_CODE = "securityCode";
    private  final String CARD_NUMBER = "cardNumber";

    private static final String CARD_LAST_FOUR_DIGITS = "cardLastFourDigits";
    private static final String EXPIRATION_MONTH = "expirationMonth";
    private static final String EXPIRATION_YEAR = "expirationYear";
    private static final String CARD_TYPE = "cardType";
    public static final String CARD_SUB_TYPE = "cardSubType";

    private transient String number;
    private String cvc;
    private boolean tokenizedSuccess = false;
    private boolean newCreditCard = false;

    private String cardLastFourDigits;
    @Nullable
    private String cardType;
    @Nullable
    private String cardSubType;
    private Integer expirationMonth;
    private Integer expirationYear;

    public CreditCard() {
    }

    @Nullable
    public static CreditCard fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        CreditCard creditCard = new CreditCard();
        creditCard.setCardLastFourDigits(JsonParser.getOptionalString(jsonObject, CARD_LAST_FOUR_DIGITS));
        creditCard.setCardType(JsonParser.getOptionalString(jsonObject, CARD_TYPE));
        creditCard.setCardSubType(JsonParser.getOptionalString(jsonObject, CARD_SUB_TYPE));
        creditCard.setExpirationMonth(Integer.valueOf(JsonParser.getOptionalString(jsonObject, EXPIRATION_MONTH)));
        creditCard.setExpirationYear(Integer.valueOf(JsonParser.getOptionalString(jsonObject, EXPIRATION_YEAR)));
        return creditCard;

    }

    public CreditCard(CreditCard creditCard) {
        this.cardLastFourDigits = creditCard.cardLastFourDigits;
        this.cardType = creditCard.cardType;
        this.cardSubType = creditCard.cardSubType;
        this.expirationMonth = creditCard.expirationMonth;
        this.expirationYear = creditCard.expirationYear;
    }

    /**
     * update credit card details
     *
     * @param creditCard - credit card object
     */
    public void update(CreditCard creditCard) {
        setExpirationMonth(creditCard.getExpirationMonth());
        setExpirationYear(creditCard.getExpirationYear());
        setCvc(creditCard.getCvc());
        tokenizedSuccess = false;
        setNumber(creditCard.getNumber());
    }

    /**
     * update credit card details
     *
     * @param creditCardNumberString - credit card number String
     * @param expDateString          - credit card expiration date String
     * @param cvvString              - credit card cvv String
     */
    public void update(String creditCardNumberString, String expDateString, String cvvString) {
        setExpDateFromString(expDateString);
        setCvc(cvvString);
        tokenizedSuccess = false;
        setNumber(creditCardNumberString);
    }

    /**
     * trim number and deletes spaces
     */
    private String normalizeCardNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.trim().replaceAll("\\s+|-", "");
    }

    /**
     * @return credit card number
     */
    public String getNumber() {
        return number;
    }

    /**
     * set credit card number (normalized) and updates last 4 digits and card type
     *
     * @param number - set credit card number String
     */
    public void setNumber(String number) {
        String normalizedCardNumber = normalizeCardNumber(number);
        String cardType = CreditCardTypeResolver.getInstance().getType(normalizedCardNumber);

        if (normalizedCardNumber != null && normalizedCardNumber.length() > 2) {
            setCardType(cardType);
            if (normalizedCardNumber.length() > 4)
                setCardLastFourDigits(getNumberLastFourDigits(normalizedCardNumber));
        }

        setIsNewCreditCard();
        this.number = normalizedCardNumber;
    }

    /**
     * get last four digits from number
     *
     * @param normalizedCardNumber - normalized Card Number (see normalizeCardNumber function)
     * @return normalized credit card Last Four Digits
     */
    private String getNumberLastFourDigits(String normalizedCardNumber) {
        return normalizedCardNumber.substring(normalizedCardNumber.length() - 4);
    }

    /**
     * @return credit card Last Four Digits
     */
    public String getCardLastFourDigits() {
        if (!TextUtils.isEmpty(cardLastFourDigits)) {
            return cardLastFourDigits;
        }
        if (number != null && number.length() > 4) {
            return number.substring(number.length() - 4);
        }
        return null;
    }

    /**
     * set credit card Last Four Digits
     *
     * @param cardLastFourDigits - set credit card Last Four Digits String
     */
    private void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    /**
     * @return cardType
     */
    @Nullable
    public String getCardType() {
        return cardType;
    }

    /**
     * set cardType
     *
     * @param cardType - set cardType String
     * @see CreditCardTypeResolver
     */
    public void setCardType(@Nullable String cardType) {
        this.cardType = cardType;
    }

    @Nullable
    public String getCardSubType() {
        return cardSubType;
    }

    public void setCardSubType(@Nullable String cardSubType) {
        this.cardSubType = cardSubType;
    }

    /**
     * @return cvv
     */
    public String getCvc() {
        return cvc;
    }

    /**
     * set cvv
     *
     * @param cvc - set cvc String
     */
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    /**
     * @return expiration Date Month
     */
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    /**
     * @return expiration Date Year
     */
    public Integer getExpirationYear() {
        return expirationYear;
    }

    /**
     * set expiration Date Month
     *
     * @param expirationMonth - expiration Date Month
     */
    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    /**
     * set expiration Date Year
     * if year not 4 digits, change to 4 digits
     *
     * @param expirationYear - expiration Date Year
     */
    public void setExpirationYear(Integer expirationYear) {
        if (expirationYear < 2000) {
            expirationYear += 2000;
        }
        this.expirationYear = expirationYear;
    }

    /**
     * change Expiration Month Integer To Two Digits (MM) String
     *
     * @return Month String representation
     */
    private String changeExpirationMonthIntegerToTwoDigitsString() {
        return ((expirationMonth < 10) ? "0" + expirationMonth : String.valueOf(expirationMonth));
    }

    /**
     * get Expiration Date
     *
     * @return MM/YYYY
     */
    public String getExpirationDate() {
        if (expirationYear < 2000) {
            expirationYear += 2000;
        }
        return changeExpirationMonthIntegerToTwoDigitsString() + "/" + expirationYear;
    }

    /**
     * get Expiration Date For EditText And TextView
     *
     * @return MM/YY
     */
    public String getExpirationDateForEditTextAndSpinner() {
        return (null != expirationMonth && null != expirationYear && !(expirationMonth.equals(0) || expirationYear.equals(0)))
                ? changeExpirationMonthIntegerToTwoDigitsString()
                + "/"
                + ((expirationYear > 2000) ? expirationYear - 2000 : expirationYear)
                : null;
    }

    /**
     * set Expiration Date From String
     *
     * @param expDateString - expiration Date From EditText And TextView
     */
    public void setExpDateFromString(String expDateString) {
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

    /**
     * set Tokeanization Successful
     */
    public void setTokenizationSuccess() {
        tokenizedSuccess = true;
    }

    /**
     * Is a New Credit Card or a Previously Used (BlueSnap) one
     */
    public boolean getIsNewCreditCard() {
        return newCreditCard;
    }

    /**
     * set Credit Card as a new Credit Card
     */
    private void setIsNewCreditCard() {
        if (!this.newCreditCard)
            this.newCreditCard = true;
    }

    /**
     * create JSON object from Credit Card
     *
     * @return JSONObject
     */
    @NonNull
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, SECURITY_CODE, getCvc());
        putJSONifNotNull(jsonObject, CARD_NUMBER, getNumber());
        putJSONifNotNull(jsonObject, CARD_LAST_FOUR_DIGITS, getCardLastFourDigits());
        putJSONifNotNull(jsonObject, EXPIRATION_MONTH, getExpirationMonth());
        putJSONifNotNull(jsonObject, EXPIRATION_YEAR, getExpirationYear());
        putJSONifNotNull(jsonObject, CARD_TYPE, getCardType());
        putJSONifNotNull(jsonObject, CARD_SUB_TYPE, getCardSubType());
        return jsonObject;
    }

    @Override
    public String toString() {
        return "CreditCard{}";
    }
}
