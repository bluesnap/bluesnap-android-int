package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.EspressoBasedTest;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

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

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void minimal_billing_test() {
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
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
     * This test verifies the flow of filling in credit card fields happens as it should.
     */
    @Test
    public void check_focus_from_cvv_text_view_in_cc_line() {
        CreditCardLineTesterCommon.check_focus_from_cvv_text_view_in_cc_line("check_focus_from_cvv_text_view_in_cc_line");
    }

    /**
     * This test does an end-to-end new card flow for minimal
     * billing new shopper
     */
    @Test
    public void minimal_billing_basic_flow_transaction() throws InterruptedException {
        new_card_basic_flow_transaction(false, false, false, false);
    }

    /**
     * This test does an end-to-end existing card flow for minimal
     * billing returning shopper
     */
    @Test
    public void change_currency_twice_back_to_usd_espresso_test() throws InterruptedException {
        new_card_basic_fill_info(false, false, false, false);
        CurrencyChangeTesterCommon.changeCurrency("CAD");
        CurrencyChangeTesterCommon.changeCurrency("ILS");
        CurrencyChangeTesterCommon.changeCurrency(checkoutCurrency);
        onView(withId(R.id.buyNowButton)).perform(click());
        sdkResult = BlueSnapService.getInstance().getSdkResult();
        finish_demo_purchase(sdkResult, false, false, false, false);
    }

    @Test
    public void returning_shopper_minimal_billing_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException {
        //make transaction to create a new shopper
        new_card_basic_flow_transaction(false, false, false, false);

        //setup sdk for the returning shopper
        returningShopperSetUp(false, false, false);

        //make a transaction with the returning shopper
        returning_shopper_card_basic_flow_transaction(false, false, false);
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
    public void billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("billing_contact_info_visibility_validation", R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("billing_contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, false, false);
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
        CurrencyChangeTesterCommon.change_currency_amount_validation("change_currency_in_billing_amount_validation", R.id.billingButtonComponentView, checkoutCurrency, TestUtils.getDecimalFormat().format(purchaseAmount));
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
