package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests;

import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 18/10/2018.
 */

public class SanityCheckoutEndToEndTests extends CheckoutEspressoBasedTester {
    /**
     * This test does an end-to-end new card flow for full
     * billing with email and shipping new shopper
     */
    @Test
    public void full_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true, false);

        checkoutSetup();
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with full billing, email and shipping
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_full_billing_with_email_with_shipping_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with full billing, email and shipping
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }


}
