package com.bluesnap.androidapi.models;

/**
 * A representation of server exchange rate.
 */
public class ExchangeRate {
    private String quoteCurrency;
    private double conversionRate;

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;

    }

}
