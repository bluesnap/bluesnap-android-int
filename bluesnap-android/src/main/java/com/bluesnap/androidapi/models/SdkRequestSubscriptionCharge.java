package com.bluesnap.androidapi.models;

import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

/**
 * Created by sivani on 14/03/2019.
 */

public class SdkRequestSubscriptionCharge extends SdkRequestBase {
    private static final String TAG = SdkRequestSubscriptionCharge.class.getSimpleName();

    public SdkRequestSubscriptionCharge() {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements();
    }

    public SdkRequestSubscriptionCharge(Double amount, String currencyNameCode) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements();
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequestSubscriptionCharge(Double amount, String currencyNameCode, ShopperCheckoutRequirements shopperCheckoutRequirements) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shopperCheckoutRequirements);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequestSubscriptionCharge(ShopperCheckoutRequirements shopperCheckoutRequirements) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shopperCheckoutRequirements);
    }

    public SdkRequestSubscriptionCharge(Double amount, String currencyNameCode, boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shippingRequired, billingRequired, emailRequired);
        priceDetails = new PriceDetails(amount, currencyNameCode, 0D);
    }

    public SdkRequestSubscriptionCharge(boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shippingRequired, billingRequired, emailRequired);
    }

    @Override
    public void setActivate3DS(boolean activate3DS) {
    }

    @Override
    public boolean isAllowCurrencyChange() {
        return hasPriceDetails() && allowCurrencyChange;
    }

    @Override
    public boolean isHideStoreCardSwitch() {
        return false;
    }

    @Override
    public boolean verify() throws BSPaymentRequestException {
        if (hasPriceDetails())
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
        if (hasPriceDetails()) {
            sdkResult.setAmount(priceDetails.getAmount());
            sdkResult.setCurrencyNameCode(priceDetails.getCurrencyCode());
        }
    }

    @Override
    public void updateTax(String shippingCountry, String shippingState) {
        if (hasPriceDetails()) {
            TaxCalculator taxCalculator = getTaxCalculator();
            if (getShopperCheckoutRequirements().isShippingRequired() && taxCalculator != null) {
                PriceDetails priceDetails = getPriceDetails();
                Log.d(TAG, "Calling taxCalculator; shippingCountry=" + shippingCountry + ", shippingState=" + shippingState + ", priceDetails=" + priceDetails);
                taxCalculator.updateTax(shippingCountry, shippingState, priceDetails);
                Log.d(TAG, "After calling taxCalculator; priceDetails=" + priceDetails);
            }
        }
    }

    @Override
    public String getBuyNowButtonText(View view) {
        String buttonText;

        if (hasPriceDetails()) {
            buttonText = (
                    getStringFormatAmount(
                            view.getResources().getString(R.string.subscribe),
                            priceDetails.getCurrencyCode(),
                            priceDetails.getAmount()
                    )
            );
        } else {
            buttonText = view.getResources().getString(R.string.subscribe);
        }


        return buttonText;
    }

    private boolean hasPriceDetails() {
        return priceDetails != null;
    }

}
