package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

/**
 * A Request for payment process in the SDK.
 * A new SdkRequest should be used for each purchase.
 */
public class SdkRequest {
    private String currencyNameCode;
    private Double amount;
    private String customTitle;
    private String userEmail;
    private boolean shippingRequired;
    private boolean billingRequired;
    private boolean emailRequired;
    private String shopperID;
    private Double subtotalAmount;
    private Double taxAmount;
    private String baseCurrency;
    private Double baseAmount;
    private Double baseTaxAmount;
    private Double baseSubtotalAmount;

    public SdkRequest(Double amount, String currencyNameCode, Double taxAmount, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        setSubtotalAmount(amount);
        setCurrencyNameCode(currencyNameCode);
        setTaxAmount(taxAmount);
        setAmount(amount + taxAmount);
        setBase();

        setBillingRequired(billingRequired);
        setEmailRequired(emailRequired);
        setShippingRequired(shippingRequired);
    }

    public SdkRequest(Double amount, String currencyNameCode, Double taxAmount) {
        setSubtotalAmount(amount);
        setCurrencyNameCode(currencyNameCode);
        setTaxAmount(taxAmount);
        setAmount(amount + taxAmount);
        setBase();
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        setSubtotalAmount(0D);
        setCurrencyNameCode(currencyNameCode);
        setTaxAmount(0D);
        setAmount(amount);
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

    public void setAmount(Double amount) {
        if (baseAmount == null)
            baseAmount = amount;
        this.amount = amount;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public boolean isBillingRequired() {
        return billingRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public void setBillingRequired(boolean billingRequired) {
        this.billingRequired = billingRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public String getShopperID() {
        return shopperID;
    }


    public Double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        if (baseSubtotalAmount == null)
            baseSubtotalAmount = subtotalAmount;

        this.subtotalAmount = subtotalAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        if (baseTaxAmount == null)
            baseTaxAmount = taxAmount;

        this.taxAmount = taxAmount;
    }

    public void setAmountWithTax(Double subtotalAmount, Double taxAmount) {
        this.taxAmount = taxAmount;
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
