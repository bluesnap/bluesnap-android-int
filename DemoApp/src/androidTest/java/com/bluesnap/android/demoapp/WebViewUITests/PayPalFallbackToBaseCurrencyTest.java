package com.bluesnap.android.demoapp.WebViewUITests;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;

/**
 * Created by sivani on 26/08/2018.
 */

@RunWith(AndroidJUnit4.class)

public class PayPalFallbackToBaseCurrencyTest extends PayPalTests {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, "ILS"); //choose ILS as checkout currency
        setupAndLaunch(sdkRequest, "EUR");  //choose EUR as base currency

        //update currency and amount according to fallback to store currency, i.e. EUR
        updateCurrencyAndAmount("ILS", "EUR");
    }

    @Test
    public void pay_pal_transaction_fallback_to_base_currency_test() throws InterruptedException {
        onView(withId(R.id.payPalButton)).perform(click());

        //wait for web to load
        sleep(20000);

        loginToPayPal();
        submitPayPalPayment();

        sdkResult = blueSnapService.getSdkResult();

        //wait for transaction to finish
        while ((payPalInvoiceId = sdkResult.getPaypalInvoiceId()) == null)
            sleep(5000);

        //verify transaction status
        retrievePayPalTransaction();
    }
}
