package com.bluesnap.androidapi.models;

import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

/**
 * A Request for payment process in the SDK.
 * A new SdkRequest should be used for each purchase.
 */
public class SdkRequest extends SdkRequestBase {
    private static final String TAG = SdkRequest.class.getSimpleName();

    public SdkRequest() {
    }

    public SdkRequest(Double amount, String currencyNameCode) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements();
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequest(Double amount, String currencyNameCode, ShopperCheckoutRequirements shopperCheckoutRequirements) {
        this.shopperCheckoutRequirements = new ShopperCheckoutRequirements(shopperCheckoutRequirements);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequest(Double amount, String currencyNameCode, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shippingRequired, billingRequired, emailRequired);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    @Override
    public void setActivate3DS(boolean activate3DS) {
        this.activate3DS = activate3DS;
    }

    @Override
    public boolean isAllowCurrencyChange() {
        return allowCurrencyChange;
    }

    @Override
    public boolean isHideStoreCardSwitch() {
        return hideStoreCardSwitch;
    }

    @Override
    public boolean verify() throws BSPaymentRequestException {
        priceDetails.verify();
        return true;
    }

    /**
     * set Sdk Result with sdk Reuqst details
     *
     * @param sdkResult - {@link SdkResult}
     */
    @Override
    public void setSdkResult(SdkResult sdkResult) {
        // Copy values from request
        sdkResult.setResult(BluesnapCheckoutActivity.BS_CHECKOUT_RESULT_OK);
        sdkResult.setAmount(priceDetails.getAmount());
        sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
    }

    @Override
    public void updateTax(String shippingCountry, String shippingState) {
        TaxCalculator taxCalculator = getTaxCalculator();
        if (getShopperCheckoutRequirements().isShippingRequired() && taxCalculator != null) {
            PriceDetails priceDetails = getPriceDetails();
            Log.d(TAG, "Calling taxCalculator; shippingCountry=" + shippingCountry + ", shippingState=" + shippingState + ", priceDetails=" + priceDetails);
            taxCalculator.updateTax(shippingCountry, shippingState, priceDetails);
            Log.d(TAG, "After calling taxCalculator; priceDetails=" + priceDetails);
        }
    }

    @Override
    public String getBuyNowButtonText(View view) {
        return (
                getStringFormatAmount(
                        view.getResources().getString(R.string.pay),
                        priceDetails.getCurrencyCode(),
                        priceDetails.getAmount()
                )
        );
    }

}
