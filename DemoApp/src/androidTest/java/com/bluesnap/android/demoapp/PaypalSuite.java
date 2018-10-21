package com.bluesnap.android.demoapp;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests.PayPalBasicTests;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests.PayPalFallbackToBaseCurrencyTest;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests.PayPalFallbackToUSDCurrencyTest;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests.PayPalWebViewTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PayPalBasicTests.class,
        PayPalFallbackToBaseCurrencyTest.class,
        PayPalFallbackToUSDCurrencyTest.class,
        PayPalWebViewTests.class
})
public class PaypalSuite {
}
