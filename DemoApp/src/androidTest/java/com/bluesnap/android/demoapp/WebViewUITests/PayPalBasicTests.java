package com.bluesnap.android.demoapp.WebViewUITests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 27/08/2018.
 */

public class PayPalBasicTests extends PayPalWebViewTests {
    @Test
    public void pay_pal_back_button_test() throws InterruptedException {
        loadPayPalWebView();

        Espresso.pressBack();

        //verify we are back in choose payment methods
        onView(withId(R.id.newCardButton))
                .withFailureHandler(new CustomFailureHandler("pay_pal_back_button_test: New Card button is not displayed after pressing back"))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.payPalButton))
                .withFailureHandler(new CustomFailureHandler("pay_pal_back_button_test: PayPal button is not displayed after pressing back"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void pay_pal_entering_twice_test() throws InterruptedException {
        loadPayPalWebView();

        Espresso.pressBack();

        loadPayPalWebView();

        loginToPayPal();
        submitPayPalPayment();
    }

    @Test
    public void pay_pal_basic_transaction_test() throws InterruptedException {
        payPalBasicTransaction();
    }

    @Test
    public void pay_pal_transaction_after_changing_currency_test() throws InterruptedException {
        onView(withId(R.id.newCardButton)).perform(click());
        CurrencyChangeTesterCommon.changeCurrency("GBP");
        updateCurrencyAndAmount("USD", "GBP");

        Espresso.pressBack();

        payPalBasicTransaction();
    }
}
