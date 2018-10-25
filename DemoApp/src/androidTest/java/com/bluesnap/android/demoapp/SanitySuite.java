package com.bluesnap.android.demoapp;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests.FullBillingWithShippingWithEmailTests;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShopperFullBillingWithShippingWithEmailTests;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.SanityCheckoutEndToEndTests;
import com.bluesnap.android.demoapp.ShopperConfigUITests.ChooseCreditCardPaymentMethod;
import com.bluesnap.android.demoapp.ShopperConfigUITests.ChoosePaymentMethodVisibilityTests;
import com.bluesnap.android.demoapp.ShopperConfigUITests.CreateCreditCardPayment;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FullBillingWithShippingWithEmailTests.class,
        ReturningShopperFullBillingWithShippingWithEmailTests.class,
        ChoosePaymentMethodVisibilityTests.class,
        ChooseCreditCardPaymentMethod.class,
        CreateCreditCardPayment.class,
        SanityCheckoutEndToEndTests.class
})
public class SanitySuite {
}
