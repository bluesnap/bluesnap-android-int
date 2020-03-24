package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Created by sivani on 05/08/2018.
 */
@RunWith(AndroidJUnit4.class)

public class ReturningShopperHideCurrencyHamburgerButtonTest extends ReturningShopperAllowCurrencyChangeTest {
    @Before
    public void setup() throws BSPaymentRequestException, InterruptedException, JSONException {
        this.isAllowed = false;
        super.setup();
    }
}
