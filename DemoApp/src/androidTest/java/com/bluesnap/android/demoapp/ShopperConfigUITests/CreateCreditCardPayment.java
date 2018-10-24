package com.bluesnap.android.demoapp.ShopperConfigUITests;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sivani on 24/10/2018.
 */

public class CreateCreditCardPayment extends CreatePaymentEspressoBasedTester {
    /**
     * This test does a full billing with email and shipping
     * end-to-end create payment flow for the chosen card.
     * <p>
     * pre-condition: chosen card is TestingShopperCreditCard.VISA_CREDIT_CARD;
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws BSPaymentRequestException
     */
    @Test
    public void full_billing_with_email_with_shipping_create_payment_flow() throws InterruptedException, JSONException, BSPaymentRequestException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, true);
        createPaymentSetup(RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING_CREDIT_CARD);

        uIAutoTestingBlueSnapService.makeCreatePaymentTransaction();
        Assert.assertEquals("wrong credit card was charged", uIAutoTestingBlueSnapService.getTransactions().getCardLastFourDigits(), TestingShopperCreditCard.VISA_CREDIT_CARD.getCardLastFourDigits());
    }
}
