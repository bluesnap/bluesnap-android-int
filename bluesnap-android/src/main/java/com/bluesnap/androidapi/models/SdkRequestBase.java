package com.bluesnap.androidapi.models;

import android.view.View;

import com.bluesnap.androidapi.services.*;

/**
 * An abstract Request for all payment options in the SDK.
 * A new SdkRequest type derivative should be used for each purchase.
 */
public abstract class SdkRequestBase {

    PriceDetails priceDetails;
    ShopperCheckoutRequirements shopperCheckoutRequirements;
    private TaxCalculator taxCalculator;

    boolean allowCurrencyChange = true;

    public PriceDetails getPriceDetails() {
        return priceDetails;
    }

    public ShopperCheckoutRequirements getShopperCheckoutRequirements() {
        return shopperCheckoutRequirements;
    }

    public TaxCalculator getTaxCalculator() {
        return taxCalculator;
    }

    public void setTaxCalculator(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    public void setAllowCurrencyChange(boolean allowCurrencyChange) {
        this.allowCurrencyChange = allowCurrencyChange;
    }

    public abstract boolean isAllowCurrencyChange();

    /**
     * verify SdkRequestBase
     *
     * @return boolean, true if verified false if not
     * @throws BSPaymentRequestException
     */
    public abstract boolean verify() throws BSPaymentRequestException;


    /**
     * set Sdk Result with sdk Reuqst details
     *
     * @param sdkResult - {@link SdkResult}
     */
    public abstract void setSdkResult(SdkResult sdkResult);

    /**
     * Update the price details according to shipping country and state, by calling the provided TaxCalculator.
     *
     * @param shippingCountry- String
     * @param shippingState-   String
     */
    public abstract void updateTax(String shippingCountry, String shippingState);

    /**
     * set Buy Now Button Text
     */
    public abstract String getBuyNowButtonText(View view);

    /**
     * get String Format For Pay Amount ("Pay $ 0.00")
     *
     * @param text             - String
     * @param currencyNameCode - The ISO 4217 currency name
     * @param amount           - amount
     * @return - String ("Pay $ 0.00")
     */
    public static String getStringFormatAmount(String text, String currencyNameCode, Double amount) {
        return String.format("%s %s %s",
                text,
                AndroidUtil.getCurrencySymbol(currencyNameCode),
                AndroidUtil.getDecimalFormat().format(amount)
        );
    }

}