package com.bluesnap.android.demoapp.ShopperConfigUITests;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sivani on 24/10/2018.
 */

public class ChoosePayPalPaymentMethod extends ChoosePaymentMethodEspressoBasedTester {

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);

    }

    /**
     * This test does a full billing with email and shipping
     * end-to-end choose paypal payment flow, for shopper with cc.
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws BSPaymentRequestException
     */
    @Test
    public void choose_paypal_payment_for_shopper_with_cc() throws InterruptedException, JSONException, BSPaymentRequestException {
        choosePaymentSetup(true, true);

        //choose first credit card
        choosePayPalPaymentMethod();
    }

    /**
     * This test does a full billing with email and shipping
     * end-to-end choose paypal payment flow, for shopper without cc.
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws BSPaymentRequestException
     */
    @Test
    public void choose_paypal_payment_for_shopper_without_cc() throws InterruptedException, JSONException, BSPaymentRequestException {
        choosePaymentSetup(true, false);

        //choose first credit card
        choosePayPalPaymentMethod();
    }
}
