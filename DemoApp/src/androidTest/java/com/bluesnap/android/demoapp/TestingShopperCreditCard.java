package com.bluesnap.android.demoapp;

/**
 * Created by sivani on 30/08/2018.
 */

public class TestingShopperCreditCard {

    public static final TestingShopperCreditCard VISA_CREDIT_CARD = new TestingShopperCreditCard("4111111111111111", "123", "1111",
            "VISA", "", 11, 2025, "25");
    public static final TestingShopperCreditCard MASTERCARD_CREDIT_CARD = new TestingShopperCreditCard("5572758886015288", "123", "5288",
            "MASTERCARD", "DEBIT", 12, 2026, "26");

    public static final TestingShopperCreditCard VISA_CREDIT_CARD_FOR_3DS_SUCCESS = new TestingShopperCreditCard("4000000000001091", "123", "1091",
            "VISA", "", 10, 2025, "25");

    public static final TestingShopperCreditCard VISA_CREDIT_CARD_FOR_3DS_UNAVAILABLE = new TestingShopperCreditCard("4000000000001059", "123", "1059",
            "VISA", "", 10, 2025, "25");

    public static final TestingShopperCreditCard VISA_CREDIT_CARD_FOR_3DS_NOT_SUPPORTED = new TestingShopperCreditCard("4000000000000002", "123", "0002",
            "VISA", "", 10, 2025, "25");

    public static final TestingShopperCreditCard VISA_CREDIT_CARD_FOR_3DS_BYPASS = new TestingShopperCreditCard("4000000000001133", "123", "1133",
            "VISA", "", 10, 2025, "25");

    public static final TestingShopperCreditCard VISA_CREDIT_CARD_FOR_3DS_FAILURE = new TestingShopperCreditCard("4000000000001109", "123", "1109",
            "VISA", "", 10, 2025, "25");

    private String cardNumber;
    private String cvv;
    private String cardLastFourDigits;
    private String cardType;
    private String cardSubType;
    private int expirationMonth;
    private int expirationYear;
    private String expirationYearLastTwoDigit;

    public TestingShopperCreditCard(String cardNumber, String cvv, String cardLastFourDigits, String cardType,
                                    String cardSubType, int expirationMonth, int expirationYear, String expirationYearLastTwoDigit) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.cardLastFourDigits = cardLastFourDigits;
        this.cardType = cardType;
        this.cardSubType = cardSubType;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.expirationYearLastTwoDigit = expirationYearLastTwoDigit;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
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

    public String getCardSubType() {
        return cardSubType;
    }

    public void setCardSubType(String cardSubType) {
        this.cardSubType = cardSubType;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getExpirationYearLastTwoDigit() {
        return expirationYearLastTwoDigit;
    }

    public void setExpirationYearLastTwoDigit(String expirationYearLastTwoDigit) {
        this.expirationYearLastTwoDigit = expirationYearLastTwoDigit;
    }
}
