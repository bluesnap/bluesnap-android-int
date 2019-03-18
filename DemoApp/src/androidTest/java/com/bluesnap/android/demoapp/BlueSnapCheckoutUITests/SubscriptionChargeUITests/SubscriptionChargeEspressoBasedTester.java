package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.SubscriptionChargeUITests;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequestSubscriptionCharge;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

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
            uIAutoTestingBlueSnapService.createVaultedShopper(false);
            returningShopperId = uIAutoTestingBlueSnapService.getVaultedShopperId();
        }

        SdkRequestSubscriptionCharge sdkRequest;
        if (withPriceDetails)
            sdkRequest = new SdkRequestSubscriptionCharge(purchaseAmount, checkoutCurrency);
        else
            sdkRequest = new SdkRequestSubscriptionCharge();

        sdkRequest.setAllowCurrencyChange(allowCurrencyChange);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);
        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, forReturningShopper, returningShopperId);
//        returningShopper = uIAutoTestingBlueSnapService.getReturningShopper();
        applicationContext = uIAutoTestingBlueSnapService.applicationContext;
        defaultCountryKey = uIAutoTestingBlueSnapService.getDefaultCountryKey();
        defaultCountryValue = uIAutoTestingBlueSnapService.getDefaultCountryValue();
    }

    public void new_card_basic_subscription_flow() throws InterruptedException, JSONException {
        //ƒintending(hasExtraWithKey(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT));

        int buttonComponent = (shopperCheckoutRequirements.isShippingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;
        //onView(withId(R.id.newCardButton)).perform(click());
        new_card_basic_fill_info();
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent)))).perform(click());
        String planId = uIAutoTestingBlueSnapService.createSubscriptionPlan();
        uIAutoTestingBlueSnapService.createSubscriptionCharge(planId);
    }


}
