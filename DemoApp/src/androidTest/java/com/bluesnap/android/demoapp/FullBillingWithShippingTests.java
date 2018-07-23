package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by sivani on 19/07/2018.
 */
@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingTests extends EspressoBasedTest {
    private String checkoutCurrency = "USD";
    private Double purchaseAmount = 55.5;
    //private Double taxAmount = 0.0;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
    }

    /**
     * This test verifies that all the credit card fields are displayed as they should
     * when choosing new credit card.
     */
    @Test
    public void new_credit_cc_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_cc_info_visibility_validation();
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with shipping when choosing new credit card.
     */
    @Test
    public void new_credit_billing_contact_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.billingViewComponent, true, false);
    }

    /**
     * This test verifies that all the shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    @Test
    public void new_credit_shipping_contact_info_visibility_validation() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering billing info.
     * (according to its location, or us by default)
     */
    @Test
    public void default_country_view_validation_in_billing() throws InterruptedException, IOException {
        NewCardVisibilityTesterCommon.default_country_view_validation(applicationContext, defaultCountry, R.id.billingViewComponent);
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
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void default_country_state_view_validation_in_billing() throws InterruptedException {
        NewCardVisibilityTesterCommon.default_country_state_view_validation(R.id.billingViewComponent, defaultCountry);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering shipping info.
     * (according to its location, or us by default)
     */
    @Test
    public void default_country_view_validation_in_shipping() throws InterruptedException, IOException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.default_country_view_validation(applicationContext, defaultCountry, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    @Test
    public void default_country_zip_view_validation_in_shipping() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.default_country_zip_view_validation(defaultCountry, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void default_country_state_view_validation_in_shipping() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.default_country_state_view_validation(R.id.newShoppershippingViewComponent, defaultCountry);
    }

    /**
     * This test verifies that changing the country in billing
     * doesn't change the country in shipping as well.
     */
    @Test
    public void country_changes_per_billing_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.country_changes_per_fragment_validation(true, true, false);
    }

    /**
     * This test verifies that changing the country in shipping
     * doesn't change the country in billing as well.
     */
    @Test
    public void country_changes_per_shipping_validation() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.country_changes_per_fragment_validation(false, true, false);
    }

}
