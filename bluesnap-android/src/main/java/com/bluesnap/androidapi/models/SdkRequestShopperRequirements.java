package com.bluesnap.androidapi.models;

import android.view.View;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

public class SdkRequestShopperRequirements extends SdkRequestBase {

    public SdkRequestShopperRequirements() {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements();
    }

    public SdkRequestShopperRequirements(ShopperCheckoutRequirements shopperCheckoutRequirements) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shopperCheckoutRequirements);
    }

    public SdkRequestShopperRequirements(boolean billingRequired, boolean emailRequired, boolean shippingRequired) {
        shopperCheckoutRequirements = new ShopperCheckoutRequirements(shippingRequired, billingRequired, emailRequired);
    }

    @Override
    public void setActivate3DS(boolean activate3DS) {
    }

    @Override
    public boolean isAllowCurrencyChange() {
        return false;
    }

    @Override
    public boolean isHideStoreCardSwitch() {
        return false;
    }

    @Override
    public boolean verify() {
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
        sdkResult.setResult(BluesnapChoosePaymentMethodActivity.BS_CHOOSE_PAYMENT_METHOD_RESULT_OK);
    }

    @Override
    public void updateTax(String shippingCountry, String shippingState) {
    }

    @Override
    public String getBuyNowButtonText(View view) {
        return view.getResources().getString(R.string.submit);
    }
}