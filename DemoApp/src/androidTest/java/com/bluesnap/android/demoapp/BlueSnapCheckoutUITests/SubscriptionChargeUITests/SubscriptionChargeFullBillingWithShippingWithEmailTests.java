package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.SubscriptionChargeUITests;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 18/03/2019.
 */

@RunWith(AndroidJUnit4.class)

public class SubscriptionChargeFullBillingWithShippingWithEmailTests extends SubscriptionChargeEspressoBasedTester {
    public SubscriptionChargeFullBillingWithShippingWithEmailTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
    }

    public void setupBeforeSubscription(boolean withPriceDetails) throws InterruptedException, BSPaymentRequestException, JSONException {
        subscriptionChargeSetup(withPriceDetails);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test does an end-to-end new card subscription flow for full
     * billing with shipping with email new shopper
     * with price details presented
     */
    @Test
    public void full_billing_with_shipping_with_email_with_price_details_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(true);
        new_card_basic_subscription_flow();
    }

    /**
     * This test does an end-to-end new card subscription flow for full
     * billing with shipping with email new shopper
     * without price details presented
     */
    @Test
    public void full_billing_with_shipping_with_email_basic_subscription_flow() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeSubscription(false);
        new_card_basic_subscription_flow();
    }
}
