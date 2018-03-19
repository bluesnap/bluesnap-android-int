package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

/**
 * Holds details of price: subtotal amount, tax amount, currency
 *
 * Created by shevie.chen on 3/14/2018.
 */

public class PriceDetails {

    private String currencyCode;
    private Double subtotalAmount;
    private Double taxAmount;

    public PriceDetails(Double subtotalAmount, String currencyCode, Double taxAmount) {
        set(subtotalAmount, currencyCode, taxAmount);
    }

    public void set(Double subtotalAmount, String currencyCode, Double taxAmount) {
        setSubtotalAmount(subtotalAmount);
        setTaxAmount(taxAmount);
        setCurrencyCode(currencyCode);
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    private void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    private void setSubtotalAmount(Double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public boolean verify() throws BSPaymentRequestException {
        if (subtotalAmount == null)
            throw new BSPaymentRequestException("Invalid amount");
        if (subtotalAmount <= 0)
            throw new BSPaymentRequestException(String.format("Invalid subtotal %f", subtotalAmount));
        if (taxAmount != null && taxAmount < 0)
            throw new BSPaymentRequestException(String.format("Invalid tax %f", taxAmount));
        if (currencyCode == null)
            throw new BSPaymentRequestException("Invalid currency");
        return true;
    }

    public boolean isSubtotalTaxSet() {
        return taxAmount != null && taxAmount.doubleValue() != 0D;
    }

    public Double getAmount() {
        return (taxAmount == null) ? subtotalAmount : subtotalAmount + taxAmount;
    }

    @Override
    public String toString() {
        return "PriceDetails{" +
                "currencyCode='" + currencyCode + '\'' +
                ", subtotalAmount=" + subtotalAmount +
                ", taxAmount=" + taxAmount +
                '}';
    }
}
