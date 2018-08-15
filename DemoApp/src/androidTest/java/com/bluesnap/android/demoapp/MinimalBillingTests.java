package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

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


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void minimal_billing_test() {
        new_credit_card_info_visibility_validation();
        new_credit_card_info_error_messages_validation();
        billing_contact_info_visibility_validation();
        billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        pay_button_in_billing_validation();
        check_ime_action_button_in_cc_info();
        check_filling_in_cc_info_flow();
        check_ime_action_button_in_billing_contact_info();
        initial_currency_view_validation_in_billing();
        change_currency_in_billing_validation();
        change_currency_in_billing_amount_validation();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void new_credit_card_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.new_credit_card_info_visibility_validation("new_credit_card_info_visibility_validation");
    }

    /**
     * This test verifies that all invalid error messages of credit card info
     * fields are not displayed.
     */
    public void new_credit_card_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.new_credit_card_info_error_messages_validation("new_credit_card_info_error_messages_validation");
    }

    /**
     * This test verifies that the all billing contact info fields are displayed
     * according to minimal billing when choosing new credit card.
     */
    public void billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("billing_contact_info_visibility_validation", R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("billing_contact_info_error_messages_validation", R.id.billingViewComponent, false, false);
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
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies the ime action button works as it should
     * in credit card info
     */
    public void check_ime_action_button_in_cc_info() {
        CreditCardLineTesterCommon.check_ime_action_button_in_cc_info("check_ime_action_button_in_cc_info");
    }

    /**
     * This test verifies the flow of filling in credit card fields happens as it should.
     */
    public void check_filling_in_cc_info_flow() {
        CreditCardLineTesterCommon.check_filling_in_cc_info_flow("check_filling_in_cc_info_flow");
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    public void check_ime_action_button_in_billing_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info", defaultCountryKey, R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies that the initial currency in billing is presented
     * as it should in the hamburger and buy now buttons.
     */
    public void initial_currency_view_validation_in_billing() {
        CurrencyChangeTesterCommon.currency_view_validation("initial_currency_view_validation_in_billing", R.id.billingButtonComponentView, checkoutCurrency);
    }

    /**
     * This test verifies that changing the currency in billing
     * changes as it should in billing.
     */
    public void change_currency_in_billing_validation() {
        CurrencyChangeTesterCommon.changeCurrency("GBP");
        CurrencyChangeTesterCommon.currency_view_validation("change_currency_in_billing_validation", R.id.billingButtonComponentView, "GBP");
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one in billing, the amount remains the same
     */
    public void change_currency_in_billing_amount_validation() {
        CurrencyChangeTesterCommon.change_currency_amount_validation("change_currency_in_billing_amount_validation", R.id.billingButtonComponentView, checkoutCurrency, Double.toString(purchaseAmount));
    }

    /**
     * This test verifies that the credit card number error message is
     * displayed after entering all cc line info and then edit the
     * credit card number to an invalid one.
     */
    //TODO: restore this when the bug is fixed (AS-147)
    //@Test
    public void invalid_cc_number_with_valid_exp_and_cvv_validation() {
        CreditCardLineTesterCommon.invalid_cc_number_with_valid_exp_and_cvv_validation("invalid_cc_number_with_valid_exp_and_cvv_validation");
    }

}
