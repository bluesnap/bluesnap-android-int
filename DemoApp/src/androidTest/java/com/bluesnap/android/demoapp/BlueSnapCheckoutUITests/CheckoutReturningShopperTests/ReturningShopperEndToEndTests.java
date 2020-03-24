package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by sivani on 27/09/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ReturningShopperEndToEndTests extends CheckoutEspressoBasedTester {

    /**
     * This test does an end-to-end existing card with minimal billing
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_minimal_billing_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, false);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with minimal billing and email
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_minimal_billing_with_email_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, true, false);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with minimal billing and shipping
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_minimal_billing_with_shipping_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, true);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with minimal billing, email and shipping
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_minimal_billing_with_shipping_with_email_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, true, true);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with full billing
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_full_billing_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, false);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with full billing and email
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_full_billing_with_email_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, false);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end existing card with full billing and shipping
     * flow for returning shopper.
     */
    @Test
    public void returning_shopper_full_billing_with_shipping_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, true);
        returning_shopper_with_existing_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with minimal billing
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_minimal_billing_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, false);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with minimal billing and email
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_minimal_billing_with_email_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, true, false);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with minimal billing and shipping
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_minimal_billing_with_shipping_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, true);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with minimal billing, email and shipping
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_minimal_billing_with_email_with_shipping_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, true, true);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with full billing
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_full_billing_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, false);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with full billing and email
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_full_billing_with_email_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, false);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }

    /**
     * This test does an end-to-end new card with full billing and shipping
     * flow for returning shopper without credit card info.
     */
    @Test
    public void returning_shopper_new_credit_card_full_billing_with_shipping_transaction() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, true);

        returning_shopper_with_new_credit_card_basic_flow_transaction();
    }
}
