package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class MinimalBillingWithShippingWithEmailTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void minimal_billing_with_shipping_with_email_test() throws IOException {
        new_credit_card_info_visibility_validation();
        new_credit_billing_contact_info_visibility_validation();
        default_country_zip_view_validation_in_billing();
        shipping_button_validation();

        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, false, true);
        new_credit_shipping_contact_info_visibility_validation();
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        pay_button_in_shipping_validation();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void new_credit_card_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_card_info_visibility_validation("new_credit_card_info_visibility_validation");
    }

    /**
     * This test verifies that the all billing contact info fields are displayed
     * according to full billing when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, false, true);
    }

    /**
     * This test verifies that the all shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    public void new_credit_shipping_contact_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation("new_credit_shipping_contact_info_visibility_validation", R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountry, R.id.billingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    public void default_country_zip_view_validation_in_shipping() {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_shipping", defaultCountry, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void default_country_state_view_validation_in_shipping() {
        NewCardVisibilityTesterCommon.default_country_state_view_validation("default_country_state_view_validation_in_shipping", R.id.newShoppershippingViewComponent, defaultCountry);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_shipping_validation() {
        double tax = defaultCountry.equals("US") ? taxAmount : 0.00;
        NewCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.shippingButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public void shipping_button_validation() {
        NewCardVisibilityTesterCommon.shipping_button_visibility_and_content_validation("shipping_button_validation", R.id.billingButtonComponentView);
    }

}
