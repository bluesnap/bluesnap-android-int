package com.bluesnap.android.demoapp;

import android.support.test.filters.SmallTest;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests.FullBillingWithShippingWithEmailTests;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests.MinimalBillingWithShippingWithEmailTests;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShopperFullBillingWithShippingWithEmailTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FullBillingWithShippingWithEmailTests.class,
        MinimalBillingWithShippingWithEmailTests.class,
        ReturningShopperFullBillingWithShippingWithEmailTests.class
})
@SmallTest
public class SanitySuite {
}
