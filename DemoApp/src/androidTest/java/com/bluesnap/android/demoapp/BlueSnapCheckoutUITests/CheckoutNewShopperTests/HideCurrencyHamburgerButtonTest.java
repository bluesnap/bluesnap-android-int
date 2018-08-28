package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Created by sivani on 03/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HideCurrencyHamburgerButtonTest extends AllowCurrencyChangeTest {

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        this.isAllowed = false;
        super.setup();
    }
}
