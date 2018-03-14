package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

/**
 * Created by shevie.chen on 3/14/2018.
 */

public class PriceDetails {

    private String currencyNameCode;
    private Double amount;
    private Double subtotalAmount;
    private Double taxAmount;

    private String baseCurrency;
    private Double baseAmount;
    private Double baseTaxAmount;
    private Double baseSubtotalAmount;

    public PriceDetails(Double amount, String currencyNameCode, Double taxAmount) {
        if (taxAmount > 0D)
            setAmountWithTax(amount, taxAmount);
        else {
            setAmountNoTax(amount);
        }

        setCurrencyNameCode(currencyNameCode);
        setBase();
    }

    public String getCurrencyNameCode() {
        return currencyNameCode;
    }

    public void setCurrencyNameCode(String currencyNameCode) {
        if (baseCurrency == null)
            baseCurrency = currencyNameCode;
        this.currencyNameCode = currencyNameCode;
    }

    public Double getAmount() {
        return amount;
    }

    void setAmount(Double amount) {
        if (baseAmount == null)
            baseAmount = amount;
        if (isSubtotalTaxSet() && (baseAmount != baseTaxAmount + baseSubtotalAmount))
            baseAmount = baseTaxAmount + baseSubtotalAmount;
        this.amount = amount;
    }


    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    void setSubtotalAmount(Double subtotalAmount) {
        if (baseSubtotalAmount == null || (baseSubtotalAmount == 0D && baseSubtotalAmount != subtotalAmount))
            baseSubtotalAmount = subtotalAmount;

        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    void setTaxAmount(Double taxAmount) {
        if (baseTaxAmount == null || (baseTaxAmount == 0D && baseTaxAmount != taxAmount))
            baseTaxAmount = taxAmount;

        this.taxAmount = taxAmount;
    }

    public void setAmountNoTax(Double amount) {
        setTaxAmount(0D);
        setSubtotalAmount(0D);
        setAmount(amount);
    }

    public void setAmountWithTax(Double subtotalAmount, Double taxAmount) {
        setTaxAmount(taxAmount);
        setSubtotalAmount(subtotalAmount);
        setAmount(subtotalAmount + taxAmount);
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public Double getBaseTaxAmount() {
        return baseTaxAmount;
    }

    public Double getBaseSubtotalAmount() {
        return baseSubtotalAmount;
    }


    public boolean verify() throws BSPaymentRequestException {
        if (amount == null)
            throw new BSPaymentRequestException("Invalid amount");
        if (amount <= 0)
            throw new BSPaymentRequestException(String.format("Invalid amount %f", amount));
        if (currencyNameCode == null)
            throw new BSPaymentRequestException("Invalid currency");

        return true;
    }

    public boolean isSubtotalTaxSet() {
        return (baseSubtotalAmount != 0D && baseTaxAmount != 0D);
    }

    public void setBase() {
        //Set these values once to remember the base currency values
        baseCurrency = currencyNameCode;
        baseAmount = amount;
        baseTaxAmount = taxAmount;
        baseSubtotalAmount = subtotalAmount;
    }
}
