package com.bluesnap.android.demoapp.WebViewUITests;

import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;

/**
 * Created by sivani on 26/08/2018.
 */

public class PayPalFallbackToUSDCurrencyTest extends PayPalTests {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, "ILS"); //choose ILS as checkout currency
        setupAndLaunch(sdkRequest, "ILS");  //choose EUR as base currency

        //update currency and amount according to fallback to default currency, i.e. USD
        updateCurrencyAndAmount("ILS", "USD");
    }

    @Test
    public void pay_pal_transaction_fallback_to_usd_currency_test() throws InterruptedException {
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
