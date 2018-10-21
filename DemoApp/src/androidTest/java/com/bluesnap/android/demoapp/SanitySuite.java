package com.bluesnap.android.demoapp;

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
public class SanitySuite {
}
