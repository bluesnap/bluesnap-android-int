package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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
        initSdkRequest(amount, currencyNameCode, taxAmount, billingRequired, emailRequired, shippingRequired);
    }

    public SdkRequest(Double amount, String currencyNameCode, Double taxAmount) {
        initSdkRequest(amount, currencyNameCode, taxAmount, false, false, false);
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        initSdkRequest(amount, currencyNameCode, 0D, false, false, false);
    }

    private void initSdkRequest(Double amount, String currencyNameCode, Double taxAmount, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        if (taxAmount > 0D)
            setAmountWithTax(amount, taxAmount);
        else {
            setAmountNoTax(amount);
        }

        setCurrencyNameCode(currencyNameCode);
        setBase();

        setBillingRequired(billingRequired);
        setEmailRequired(emailRequired);
        setShippingRequired(shippingRequired);
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
