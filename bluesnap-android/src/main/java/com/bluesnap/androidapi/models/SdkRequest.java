package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.TaxCalculator;

/**
 * A Request for payment process in the SDK.
 * A new SdkRequest should be used for each purchase.
 */
public class SdkRequest {

    private PriceDetails priceDetails;
    private boolean shippingRequired;
    private boolean billingRequired;
    private boolean emailRequired;
    private boolean allowCurrencyChange = true;
    private TaxCalculator taxCalculator;

    private SdkRequest() {
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
        setBillingRequired(false);
        setEmailRequired(false);
        setShippingRequired(false);
    }

    public SdkRequest(Double amount, String currencyNameCode, Double taxAmount, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {

        priceDetails = new PriceDetails(amount, currencyNameCode, taxAmount);
        setBillingRequired(billingRequired);
        setEmailRequired(emailRequired);
        setShippingRequired(shippingRequired);
    }

    public PriceDetails getPriceDetails() {
        return priceDetails;
    }

    public boolean isShippingRequired() {
        return shippingRequired;
    }

    public void setShippingRequired(boolean shippingRequired) {
        this.shippingRequired = shippingRequired;
    }

    public boolean isBillingRequired() {
        return billingRequired;
    }

    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public void setBillingRequired(boolean billingRequired) {
        this.billingRequired = billingRequired;
    }

    public boolean isAllowCurrencyChange() {
        return allowCurrencyChange;
    }

    public void setAllowCurrencyChange(boolean allowCurrencyChange) {
        this.allowCurrencyChange = allowCurrencyChange;
    }

    public boolean verify() throws BSPaymentRequestException {
        priceDetails.verify();
        return true;
    }

    public TaxCalculator getTaxCalculator() {
        return taxCalculator;
    }

    public void setTaxCalculator(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }
}
