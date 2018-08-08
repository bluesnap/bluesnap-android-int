package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.TaxCalculator;

/**
 * A Request for payment process in the SDK.
 * A new SdkRequest should be used for each purchase.
 */
public class SdkRequest {

    private PriceDetails priceDetails;
    private boolean allowCurrencyChange = true;
    private ShopperInfoConfig shopperInfoConfig = new ShopperInfoConfig(false, false, false);
    private TaxCalculator taxCalculator;

    private boolean shopperConfig = false;

    private SdkRequest() {
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequest(Double amount, String currencyNameCode, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {

        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
        shopperInfoConfig = new ShopperInfoConfig(shippingRequired, billingRequired, emailRequired);
    }

    public SdkRequest(boolean billingRequired, boolean emailRequired, boolean shippingRequired) {

        priceDetails = new PriceDetails(0D, SupportedPaymentMethods.USD, 0D);
        shopperInfoConfig = new ShopperInfoConfig(shippingRequired, billingRequired, emailRequired);
        shopperConfig = true;
    }

    public PriceDetails getPriceDetails() {
        return priceDetails;
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

    public ShopperInfoConfig getShopperInfoConfig() {
        return shopperInfoConfig;
    }

    public boolean isShippingRequired() {
        return shopperInfoConfig.isShippingRequired();
    }

    public boolean isBillingRequired() {
        return shopperInfoConfig.isBillingRequired();
    }

    public boolean isEmailRequired() {
        return shopperInfoConfig.isEmailRequired();
    }

    public boolean isShopperConfig() {
        return shopperConfig;
    }
}
