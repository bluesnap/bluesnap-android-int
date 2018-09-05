package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;

/**
 * Created by sivani on 05/08/2018.
 */

public class ReturningShopperHideCurrencyHamburgerButtonTest extends ReturningShopperAllowCurrencyChangeTest {
    @Before
    public void setup() throws BSPaymentRequestException, InterruptedException, JSONException {
        this.isAllowed = false;
        super.setup();
    }
}
