package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.EspressoBasedTest;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)
public class FullBillingTests extends EspressoBasedTest {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.getShopperCheckoutRequirements().setBillingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void full_billing_test() throws IOException {
        new_credit_cc_info_visibility_validation();
        new_credit_card_info_error_messages_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_view_validation_in_billing();
        default_country_zip_view_validation_in_billing();
        default_country_state_view_validation_in_billing();

        //Pre-condition: Current billing country is the default one
        changing_country_view_validation_in_billing();
        changing_country_zip_view_validation_in_billing();
        changing_country_state_view_validation_in_billing();
        pay_button_in_billing_validation();
        check_ime_action_button_in_billing_contact_info();
    }

    @Test
    public void full_billing_basic_flow_transaction() throws InterruptedException {
        new_card_basic_flow_transaction(true, false, false, false);
    }

    @Test
    public void returning_shopper_full_billing_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException {
        //make transaction to create a new shopper
        new_card_basic_flow_transaction(true, false, false, false);

        //setup sdk for the returning shopper
        returningShopperSetUp(true, false, false);

        //make a transaction with the returning shopper
        returning_shopper_card_basic_flow_transaction(true, false, false);
    }

    /**
     * This test verifies that all the credit card and billing contact info fields
     * are displayed as they should when choosing new credit card.
     */
    public void new_credit_cc_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.new_credit_card_info_visibility_validation("new_credit_cc_info_visibility_validation");
    }

    /**
     * This test verifies that all invalid error messages of credit card info
     * fields are not displayed.
     */
    public void new_credit_card_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.new_credit_card_info_error_messages_validation("new_credit_card_info_error_messages_validation");
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, true, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, true, false);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering billing info.
     * (according to its location, or us by default)
     */
    public void default_country_view_validation_in_billing() throws IOException {
        CreditCardVisibilityTesterCommon.country_view_validation("default_country_view_validation_in_billing", applicationContext, defaultCountryKey, R.id.billingViewComponent);
    }

    /**
     * This test verifies that the country image changes as expected, according
     * to different choices in billing info.
     */
    public void changing_country_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.changing_country_view_validation("changing_country_view_validation_in_billing", R.id.billingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountryKey, R.id.billingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to different choices of countries in billing info.
     */
    public void changing_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.changing_country_zip_view_validation("changing_country_zip_view_validation_in_billing", R.id.billingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void default_country_state_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_state_view_validation("default_country_state_view_validation_in_billing", R.id.billingViewComponent, defaultCountryKey);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in billing info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void changing_country_state_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.changing_country_state_view_validation("changing_country_state_view_validation_in_billing", R.id.billingViewComponent);
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
     * in full billing contact info
     */
    public void check_ime_action_button_in_billing_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info", defaultCountryKey, R.id.billingViewComponent, true, false);
    }


}

