package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Before;

/**
 * Created by sivani on 05/08/2018.
 */

public class ReturningShopperHideCurrencyHamburgerButtonTest extends ReturningShopperAllowCurrencyChangeTest {
    @Before
    public void setup() throws BSPaymentRequestException, InterruptedException {
        this.isAllowed = false;
        super.setup();
    }
}
