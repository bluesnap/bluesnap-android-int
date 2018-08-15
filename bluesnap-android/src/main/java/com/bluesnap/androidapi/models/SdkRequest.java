package com.bluesnap.androidapi.models;

import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.TaxCalculator;

/**
 * A Request for payment process in the SDK.
 * A new SdkRequest should be used for each purchase.
 */
public class SdkRequest extends ShopperInfoConfig {

    private PriceDetails priceDetails;
    private boolean allowCurrencyChange = true;
    private TaxCalculator taxCalculator;

    private SdkRequest() {
        super();
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        super();
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequest(Double amount, String currencyNameCode, ShopperInfoConfig shopperInfoConfig) {
        super(shopperInfoConfig);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequest(Double amount, String currencyNameCode, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        super(shippingRequired, billingRequired, emailRequired);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
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
}
