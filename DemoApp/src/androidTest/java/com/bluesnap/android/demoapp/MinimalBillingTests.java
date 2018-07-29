package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class MinimalBillingTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(200);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    @Test
    public void new_credit_cc_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_cc_info_visibility_validation();
    }

    /**
     * This test verifies that the all billing contact info fields are displayed
     * according to minimal billing when choosing new credit card.
     */
    @Test
    public void new_credit_billing_contact_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.billingViewComponent, false, false);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    @Test
    public void default_country_zip_view_validation_in_billing() throws InterruptedException {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation(defaultCountry, R.id.billingViewComponent);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */

    @Test
    public void pay_button_in_billing_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.pay_button_visibility_and_content_validation(R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    @Test
    public void check_ime_action_button_in_billing_contact_info() throws InterruptedException {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info(defaultCountry, R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies the ime action button works as it should
     * in credit card info
     */
    @Test
    public void check_ime_action_button_in_cc_info() throws InterruptedException {
        CreditCardLineTesterCommon.check_ime_action_button_in_cc_info();
    }

    /**
     * This test verifies the flow of filling in credit card fields happens as it should.
     */
    @Test
    public void check_filling_in_cc_info_flow() throws InterruptedException {
        CreditCardLineTesterCommon.check_filling_in_cc_info_flow();
    }

    /**
     * This test verifies that the initial currency in billing is presented
     * as it should in the hamburger buy buttons.
     */
    @Test
    public void initial_currency_view_validation_in_billing() throws InterruptedException {
        CurrencyChangeTest.currency_view_validation(R.id.billingButtonComponentView, checkoutCurrency);
    }

    /**
     * This test verifies that changing the currency in billing
     * changes as it should in billing.
     */
    @Test
    public void change_currency_in_billing_validation() throws InterruptedException {
        CreditCardLineTesterCommon.changeCurrency("GBP");
        CurrencyChangeTest.currency_view_validation(R.id.billingButtonComponentView, "GBP");
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one in billing, the amount remains the same
     */
    @Test
    public void change_currency_in_billing_amount_validation() throws InterruptedException {
        CurrencyChangeTest.change_currency_amount_validation(R.id.billingButtonComponentView, checkoutCurrency, Double.toString(purchaseAmount));
    }

}
