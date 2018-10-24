package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import android.support.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 18/10/2018.
 */

public class NewShopperEndToEndTests extends CheckoutEspressoBasedTester {

    public NewShopperEndToEndTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements();
    }

    public void setupBeforeTransaction(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired, boolean shippingSameAsBilling) throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements.setTestingShopperCheckoutRequirements(fullBillingRequired, emailRequired, shippingRequired, shippingSameAsBilling);

        checkoutSetup();
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test does an end-to-end existing card flow for minimal
     * billing returning shopper
     */
    @Test
    public void change_currency_twice_back_to_usd_espresso_test() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(false, false, false, false);
        new_card_basic_fill_info();
        CurrencyChangeTesterCommon.changeCurrency("CAD");
        CurrencyChangeTesterCommon.changeCurrency("ILS");
        CurrencyChangeTesterCommon.changeCurrency(checkoutCurrency);
        onView(withId(R.id.buyNowButton)).perform(click());
        uIAutoTestingBlueSnapService.finishDemoPurchase(shopperCheckoutRequirements);
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing new shopper
     */
    @Test
    public void minimal_billing_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(false, false, false, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing with email new shopper
     */
    @Test
    public void minimal_billing_with_email_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(false, true, false, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing with shipping new shopper
     */
    @Test
    public void minimal_billing_with_shipping_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(false, false, true, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing with email and shipping new shopper
     */
    @Test
    public void minimal_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(false, true, true, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for full
     * billing new shopper
     */
    @Test
    public void full_billing_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(true, false, false, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for full
     * billing with email new shopper
     */
    @Test
    public void full_billing_with_email_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(true, true, false, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for full
     * billing with shipping new shopper
     */
    @Test
    public void full_billing_with_shipping_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(true, false, true, false);
        new_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card flow for full
     * billing with email and shipping new shopper
     */
//    @Test
//    public void full_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
//        setupBeforeTransaction(true,true,true,false);
//        new_card_basic_flow_transaction();
//    }

    /**
     * This test does an end-to-end new card flow for full
     * billing with email and shipping new shopper, with shipping same as billing
     */
    @Test
    public void shipping_same_as_billing_basic_flow_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupBeforeTransaction(true, true, true, true);
        new_card_basic_flow_transaction();
    }
}
