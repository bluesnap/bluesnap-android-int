package com.bluesnap.android.demoapp.ShopperConfigUITests;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;

import static java.lang.Thread.sleep;

/**
 * Created by sivani on 24/10/2018.
 */

public class CreatePayPalPayment extends CreatePaymentEspressoBasedTester {

    @Test
    public void paypal_create_payment_flow() throws InterruptedException, JSONException, BSPaymentRequestException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, false);
        createPaymentSetup(RETURNING_SHOPPER_PAY_PAL);

        //wait for web to load
        sleep(20000);

        payPalWebViewTests.payPalBasicTransaction(false, checkoutCurrency, purchaseAmount);
    }
}
