package com.bluesnap.androidapi.models;

/**
 * A representation of server exchange rate.
 */
public class Currency {
    //@SerializedName("quoteCurrency")
    private String quoteCurrency;
    //@SerializedName("quoteCurrencyName")
    private String quoteCurrencyName;
    //@SerializedName("fractionDigits")
    private double fractionDigits;
    //@SerializedName("conversionRate")
    private double conversionRate;

    public Currency() {
    }

    public Currency(String quoteCurrency, String quoteCurrencyName, double fractionDigits, double conversionRate) {
        this.quoteCurrency = quoteCurrency;
        this.quoteCurrencyName = quoteCurrencyName;
        this.fractionDigits = fractionDigits;
        this.conversionRate = conversionRate;
    }


    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public String getQuoteCurrencyName() {
        return quoteCurrencyName;
    }

    public void setQuoteCurrencyName(String quoteCurrencyName) {
        this.quoteCurrencyName = quoteCurrencyName;
    }

    public double getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(double fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }


}
