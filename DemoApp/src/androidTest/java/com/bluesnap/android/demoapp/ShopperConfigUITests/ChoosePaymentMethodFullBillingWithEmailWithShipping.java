package com.bluesnap.android.demoapp.ShopperConfigUITests;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sivani on 01/09/2018.
 */

public class ChoosePaymentMethodFullBillingWithEmailWithShipping extends ChoosePaymentMethodEspressoBasedTester {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        //TODO: restore email true when AS-155 is fixed
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
        choosePaymentSetup(true);
    }

    /**
     * This test does a full billing with email and shipping
     * end-to-end choose payment flow, for both new card and existing card.
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws BSPaymentRequestException
     */
    @Test
    public void full_billing_with_email_with_shipping_choose_payment_flow() throws InterruptedException, JSONException, BSPaymentRequestException {
        //choose new card
        chooseNewCardPaymentMethod(TestingShopperCreditCard.MASTERCARD_CREDIT_CARD);

        //setup for full billing
        choosePaymentSetup(false);
        uIAutoTestingBlueSnapService.setIsReturningShopper(true);

        //choose first credit card
        chooseExistingCardPaymentMethod(TestingShopperCreditCard.VISA_CREDIT_CARD);
    }

//    /**
//     * This test does a full billing end-to-end choose
//     * payment flow, for both new card and existing card,
//     * for a minimal billing shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_minimal_billing_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(false, false, false);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a minimal billing with mail shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_minimal_billing_with_email_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(false, true, false);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a minimal billing with shipping shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_minimal_billing_with_shipping_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(false, false, true);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a minimal billing with email with shipping shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_minimal_billing_with_shipping_with_email_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(false, true, true);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a full billing with shipping shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_full_billing_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(true, false, false);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a full billing with email shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_full_billing_with_email_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(true, true, false);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a full billing with shipping shopper.
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_full_billing_with_shipping_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(true, false, true);
//    }
//
//    /**
//     * This test does a full billing with email and shipping
//     * end-to-end choose payment flow, for both new card and existing card,
//     * for a full billing with email with shipping shopper
//     *
//     * @throws InterruptedException
//     */
//    @Test
//    public void choose_payment_flow_with_full_billing_with_email_with_shipping_shopper() throws InterruptedException, JSONException, BSPaymentRequestException {
//        full_billing_with_email_with_shipping_choose_payment_flow(true, true, true);
//    }


}
