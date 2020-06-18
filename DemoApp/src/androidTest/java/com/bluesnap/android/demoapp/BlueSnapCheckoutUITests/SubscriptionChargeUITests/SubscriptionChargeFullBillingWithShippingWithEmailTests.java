package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.SubscriptionChargeUITests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 18/03/2019.
 */

@RunWith(AndroidJUnit4.class)

public class SubscriptionChargeFullBillingWithShippingWithEmailTests extends SubscriptionChargeEspressoBasedTester {
    public SubscriptionChargeFullBillingWithShippingWithEmailTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
    }

    public void setupBeforeSubscription(boolean withPriceDetails, boolean forReturningShopper) throws InterruptedException, BSPaymentRequestException, JSONException {
        subscriptionChargeSetup(withPriceDetails, forReturningShopper);
    }

    /**
     * This test does an end-to-end new card subscription flow for full
     * billing with shipping with email new shopper
     * with price details presented
     */
    @Test
    public void full_billing_with_shipping_with_email_with_price_details_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(true, false);
        new_card_basic_subscription_flow(true);
    }

    /**
     * This test does an end-to-end new card subscription flow for full
     * billing with shipping with email new shopper
     * without price details presented
     */
    @Test
    public void full_billing_with_shipping_with_email_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(false, false);
        new_card_basic_subscription_flow(false);
    }

    /**
     * This test does an end-to-end existing card subscription flow for full
     * billing with shipping with email returning shopper
     * with price details presented
     */
    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_with_price_details_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(true, true);
        returning_shopper_card_basic_subscription_flow(true);
    }

    /**
     * This test does an end-to-end existing card subscription flow for full
     * billing with shipping with email returning shopper
     * without price details presented
     */
    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(false, true);
        returning_shopper_card_basic_subscription_flow(false);
    }

    /**
     * Test a bug in new card subscription flow when trying to choose new card twice and then change the state.
     * For full billing with shipping with email new shopper
     * with price details presented
     */
    @Test
    public void choosing_new_card_twice_state_bug() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(true, false);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        Espresso.pressBack();

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        String state = ContactInfoTesterCommon.getDefaultStateByCountry(ContactInfoTesterCommon.billingContactInfo.getCountryKey());

        if (state != null) {
            ContactInfoTesterCommon.changeState(R.id.billingViewComponent, state);
        }
    }
}
