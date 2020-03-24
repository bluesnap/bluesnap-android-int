package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.SubscriptionChargeUITests;

import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.androidapi.models.SdkRequestSubscriptionCharge;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 18/03/2019.
 */

public class SubscriptionChargeEspressoBasedTester extends CheckoutEspressoBasedTester {

    public SubscriptionChargeEspressoBasedTester() {
    }


    protected void subscriptionChargeSetup(boolean withPriceDetails) throws BSPaymentRequestException, InterruptedException, JSONException {
        subscriptionChargeSetup(withPriceDetails, false, withPriceDetails);
    }

    protected void subscriptionChargeSetup(boolean withPriceDetails, boolean forReturningShopper) throws BSPaymentRequestException, InterruptedException, JSONException {
        subscriptionChargeSetup(withPriceDetails, forReturningShopper, withPriceDetails);
    }

    protected void subscriptionChargeSetup(boolean withPriceDetails, boolean forReturningShopper, boolean allowCurrencyChange) throws BSPaymentRequestException, InterruptedException, JSONException {
        String returningShopperId = "";
        if (forReturningShopper) {
            uIAutoTestingBlueSnapService.createVaultedShopper(true);
            returningShopperId = uIAutoTestingBlueSnapService.getVaultedShopperId();
        }

        SdkRequestSubscriptionCharge sdkRequest;
        if (withPriceDetails)
            sdkRequest = new SdkRequestSubscriptionCharge(purchaseAmount, checkoutCurrency);
        else
            sdkRequest = new SdkRequestSubscriptionCharge();

        if (forReturningShopper)
            uIAutoTestingBlueSnapService.setExistingCard(true);

        sdkRequest.setAllowCurrencyChange(allowCurrencyChange);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);
        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, forReturningShopper, returningShopperId);
//        returningShopper = uIAutoTestingBlueSnapService.getReturningShopper();
        applicationContext = uIAutoTestingBlueSnapService.applicationContext;
        defaultCountryKey = uIAutoTestingBlueSnapService.getDefaultCountryKey();
        defaultCountryValue = uIAutoTestingBlueSnapService.getDefaultCountryValue();
    }


    public void new_card_basic_subscription_flow(boolean withPriceDetails) throws InterruptedException, JSONException {
        int buttonComponent = (shopperCheckoutRequirements.isShippingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;

        //TODO: make this choice general based on supported payment methods (check if only new cc is available and click otherwise)
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        CreditCardVisibilityTesterCommon.check_store_card_visibility("check_store_card_visibility" + shopperCheckoutRequirements, true);

        // check subscribe button
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, tax, true, withPriceDetails);

        new_card_basic_fill_info(true, true, null);

        if (shopperCheckoutRequirements.isShippingRequired()) {
            CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.shippingButtonComponentView, checkoutCurrency, purchaseAmount, tax, true, withPriceDetails);
        }

        TestUtils.pressBuyNowButton(buttonComponent);
        String planId = uIAutoTestingBlueSnapService.createSubscriptionPlan();
        uIAutoTestingBlueSnapService.createSubscriptionCharge(planId, shopperCheckoutRequirements, TestingShopperCreditCard.MASTERCARD_CREDIT_CARD);
    }


    public void returning_shopper_card_basic_subscription_flow(boolean withPriceDetails) throws InterruptedException, JSONException {
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());

        existing_card_edit_info();

        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation",
                R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, taxAmount, true, withPriceDetails);

        TestUtils.pressBuyNowButton(R.id.returningShppoerCCNFragmentButtonComponentView);
        String planId = uIAutoTestingBlueSnapService.createSubscriptionPlan();
        uIAutoTestingBlueSnapService.createSubscriptionCharge(planId, shopperCheckoutRequirements, TestingShopperCreditCard.VISA_CREDIT_CARD);
    }


}
