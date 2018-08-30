package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class MinimalBillingWithEmailTests extends CheckoutEspressoBasedTester {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.getShopperCheckoutRequirements().setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        defaultCountryKey = BlueSnapService.getInstance().getUserCountry(this.applicationContext);
    }

    @Test
    public void minimal_billing_with_email_test() {
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
        new_credit_billing_contact_info_visibility_validation();
        billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        pay_button_in_billing_validation();
        check_ime_action_button_in_billing_contact_info();
        cc_empty_fields_invalid_error_validation();
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing with email new shopper
     */
    @Test
    public void minimal_billing_with_email_basic_flow_transaction() throws InterruptedException {
        new_card_basic_flow_transaction(false, true, false, false);
    }

    @Test
    public void returning_shopper_minimal_billing_with_email_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException {
        //make transaction to create a new shopper
        new_card_basic_flow_transaction(false, true, false, false);

        //setup sdk for the returning shopper
        returningShopperSetUp(false, true, false);

        //make a transaction with the returning shopper
        returning_shopper_card_basic_flow_transaction(false, true, false);
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void cc_line_fields_visibility_validation() {
        CreditCardVisibilityTesterCommon.cc_line_fields_visibility_validation("cc_line_fields_visibility_validation");
    }

    /**
     * This test verifies that all invalid error messages of credit card info
     * fields are not displayed.
     */
    public void cc_line_error_messages_not_displayed_validation() {
        CreditCardVisibilityTesterCommon.cc_line_error_messages_not_displayed_validation("cc_line_error_messages_not_displayed_validation");
    }

    /**
     * This test verifies that the all billing contact info fields are displayed
     * according to minimal billing when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, false, true);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("billing_contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, false, true);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountryKey, R.id.billingViewComponent);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_billing_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    public void check_ime_action_button_in_billing_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info", defaultCountryKey, R.id.billingViewComponent, false, true);
    }

    /**
     * This test verifies that an invalid error appears for every
     * field in cc line when leaving it empty (pressing buy button)
     */
    public void cc_empty_fields_invalid_error_validation() {
        CreditCardLineTesterCommon.cc_empty_fields_invalid_error_validation("cc_empty_fields_invalid_error_validation");
    }
}
