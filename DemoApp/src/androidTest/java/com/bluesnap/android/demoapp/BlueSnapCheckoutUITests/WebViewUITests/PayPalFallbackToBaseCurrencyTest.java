package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by sivani on 26/08/2018.
 */

@RunWith(AndroidJUnit4.class)
public class PayPalFallbackToBaseCurrencyTest extends PayPalWebViewTests {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        payPalCheckoutSetup("EUR", "ILS"); //choose EUR as base currency, and ILS as checkout currency

        //update currency and amount according to fallback to store currency, i.e. EUR
        updateCurrencyAndAmountAfterConversion("ILS", "EUR");
    }

    @Test
    public void pay_pal_transaction_fallback_to_base_currency_test() throws InterruptedException {
        payPalBasicTransaction();
    }

}
