package com.bluesnap.android.demoapp;

/**
 * Created by sivani on 30/08/2018.
 */

public class TestingShopperCreditCard {

    public static final TestingShopperCreditCard VISA_CREDIT_CARD = new TestingShopperCreditCard("1111",
            "VISA", "", 11, 2019);
    public static final TestingShopperCreditCard MASTERCARD_CREDIT_CARD = new TestingShopperCreditCard("5288",
            "MASTERCARD", "DEBIT", 12, 2026);

    private String cardLastFourDigits;
    private String cardType;
    private String cardSubType;
    private int expirationMonth;
    private int expirationYear;

    public TestingShopperCreditCard(String cardLastFourDigits, String cardType,
                                    String cardSubType, int expirationMonth, int expirationYear) {
        this.cardLastFourDigits = cardLastFourDigits;
        this.cardType = cardType;
        this.cardSubType = cardSubType;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
    }

    public TestingShopperCreditCard(TestingShopperCreditCard creditCard) {
        cardLastFourDigits = creditCard.getCardLastFourDigits();
        cardType = creditCard.getCardType();
        cardSubType = creditCard.getCardSubType();
        expirationMonth = creditCard.getExpirationMonth();
        expirationYear = creditCard.getExpirationYear();
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
}
