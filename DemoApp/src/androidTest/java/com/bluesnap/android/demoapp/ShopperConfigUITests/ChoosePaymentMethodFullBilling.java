package com.bluesnap.android.demoapp.ShopperConfigUITests;

import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sivani on 01/09/2018.
 */

public class ChoosePaymentMethodFullBilling extends ChoosePaymentMethodEspressoBasedTester {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        preSetup(true, true, false, false);
    }

    /**
     * This test does an end-to-end choose payment flow,
     * for both new card and existing card.
     *
     * @throws InterruptedException
     */
    @Test
    public void full_billing_choose_payment_flow() throws InterruptedException, JSONException, BSPaymentRequestException {
        //choose new card
        chooseNewCardPaymentMethod(true, false, false, TestingShopperCreditCard.MASTERCARD_CREDIT_CARD, 0);

        //setup
        preSetup(false, true, false, false);

        //choose another new card
        chooseNewCardPaymentMethod(true, false, false, TestingShopperCreditCard.VISA_CREDIT_CARD, 1);

        //setup
        preSetup(false, true, false, false);

        //choose first credit card
        chooseExistingCardPaymentMethod(true, false, false, TestingShopperCreditCard.MASTERCARD_CREDIT_CARD, 0);
    }

}
