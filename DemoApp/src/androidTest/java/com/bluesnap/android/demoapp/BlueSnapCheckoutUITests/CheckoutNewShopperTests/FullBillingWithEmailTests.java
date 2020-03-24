package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

/**
 * Created by sivani on 21/07/2018.
 */

//@RunWith(AndroidJUnit4.class)

public class FullBillingWithEmailTests extends CheckoutEspressoBasedTester {

    public FullBillingWithEmailTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, false);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup();

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    //    @Test
    public void full_billing_with_email_test() {
        cc_line_fields_visibility_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        default_country_state_view_validation_in_billing();
        check_store_card_visibility();
        check_store_card_visibility_after_changing_activities(true);
        check_store_card_visibility_after_changing_activities(false);
        pay_button_in_billing_validation();
        empty_fields_invalid_error_validation_in_billing();
        check_ime_action_button_in_billing_contact_info();
        Espresso.closeSoftKeyboard(); //Close soft keyboard to make all fields visible
        //Pre-condition: All fields are empty
    }

    //    @Test
    public void full_billing_with_email_test_inputs() {
        cc_line_error_messages_not_displayed_validation();

        cc_number_invalid_error_validation();
        cc_number_invalid_error_validation_using_ime_button();

        //Pre-condition: exp date field is displayed
        exp_date_invalid_error_validation();
        exp_date_invalid_error_validation_using_ime_button();

        //Pre-condition: cvv field is displayed
        cvv_number_invalid_error_validation();
        cvv_number_invalid_error_validation_using_ime_button();

        name_invalid_error_validation_in_billing();
        name_invalid_error_validation_using_ime_button_in_billing();
        email_invalid_error_validation_in_billing();
        email_invalid_error_validation_using_ime_button_in_billing();
        zip_invalid_error_validation_in_billing();
        zip_invalid_error_validation_using_ime_button_in_billing();
        city_invalid_error_validation_in_billing();
        city_invalid_error_validation_using_ime_button_in_billing();
        address_invalid_error_validation_in_billing();
        state_invalid_error_in_billing();
    }

    /**
     * This test verifies that all the credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void cc_line_fields_visibility_validation() {
        CreditCardVisibilityTesterCommon.cc_line_fields_visibility_validation("cc_line_fields_visibility_validation");
    }

    /**
     * This test verifies that all invalid error messages of credit card info
     * fields are not displayed before clicking/entering any text.
     */
    public void cc_line_error_messages_not_displayed_validation() {
        CreditCardVisibilityTesterCommon.cc_line_error_messages_not_displayed_validation("cc_line_error_messages_not_displayed_validation");
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with email when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, true, true);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountryKey, R.id.billingViewComponent);
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
     * This test verifies the visibility of store card switch.
     * It covers visibility and switch state
     */
    public void check_store_card_visibility() {
        CreditCardVisibilityTesterCommon.check_store_card_visibility("check_store_card_visibility" + shopperCheckoutRequirements, true);
    }

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility and switch state
     */
    public void check_store_card_visibility_after_changing_activities(boolean setTo) {
        CreditCardVisibilityTesterCommon.check_store_card_visibility_after_changing_activities(true, setTo, shopperCheckoutRequirements, defaultCountryValue, ContactInfoTesterCommon.getDefaultStateByCountry(defaultCountryKey), checkoutCurrency);
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
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info", defaultCountryKey, R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    public void empty_fields_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.contact_info_empty_fields_invalid_error_validation("empty_fields_invalid_error_validation_in_billing", R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies the invalid error appearance for the cc number
     * input field in billing.
     * It checks validity by clicking on another field
     */
    public void cc_number_invalid_error_validation() {
        CreditCardLineTesterCommon.cc_number_invalid_error_validation("cc_number_invalid_error_validation", false, R.id.input_name);
    }

    /**
     * This test verifies the invalid error appearance for the cc number
     * input field in billing.
     * It checks validity by pressing the Ime button
     */
    public void cc_number_invalid_error_validation_using_ime_button() {
        CreditCardLineTesterCommon.cc_number_invalid_error_validation("cc_number_invalid_error_validation_using_ime_button", true, 0);
    }

    /**
     * This test verifies the invalid error appearance for the exp date
     * input field in billing.
     * It checks validity by clicking on another field
     */
    public void exp_date_invalid_error_validation() {
        CreditCardLineTesterCommon.exp_date_invalid_error_validation("exp_date_invalid_error_validation", false, R.id.input_name);
    }

    /**
     * This test verifies the invalid error appearance for the exp_date
     * input field in billing.
     * It checks validity by pressing the Ime button
     */
    public void exp_date_invalid_error_validation_using_ime_button() {
        CreditCardLineTesterCommon.exp_date_invalid_error_validation("exp_date_invalid_error_validation_using_ime_button", true, 0);
    }

    /**
     * This test verifies the invalid error appearance for the cvv number
     * input field in billing.
     * It checks validity by clicking on another field
     */
    public void cvv_number_invalid_error_validation() {
        CreditCardLineTesterCommon.cvv_number_invalid_error_validation("cvv_number_invalid_error_validation", false, R.id.input_name);
    }

    /**
     * This test verifies the invalid error appearance for the cvv number
     * input field in billing.
     * It checks validity by pressing the Ime button
     */
    public void cvv_number_invalid_error_validation_using_ime_button() {
        CreditCardLineTesterCommon.cvv_number_invalid_error_validation("cvv_number_invalid_error_validation_using_ime_button", true, 0);
    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    public void name_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.name_invalid_error_validation("name_invalid_error_validation_in_billing", R.id.billingViewComponent, false, R.id.input_zip);
    }


    /**
     * This test verifies the invalid error appearance for the name
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    public void name_invalid_error_validation_using_ime_button_in_billing() {
        ContactInfoTesterCommon.name_invalid_error_validation("name_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, true, R.id.input_email);

    }

    /**
     * This test verifies the invalid error appearance for the email
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid email- without '@', without '.' finish, too long suffix, too long prefix and illegal characters
     * Entering a valid email
     * Entering an invalid email after entering a valid one
     */
    public void email_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.email_invalid_error_validation("email_invalid_error_validation_in_billing", false, R.id.input_zip);
    }

    /**
     * This test verifies the invalid error appearance for the email
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid email- without '@', without '.' finish, too long suffix, too long prefix and illegal characters
     * Entering a valid email
     * Entering an invalid email after entering a valid one
     */
    public void email_invalid_error_validation_using_ime_button_in_billing() {
        ContactInfoTesterCommon.email_invalid_error_validation("email_invalid_error_validation_using_ime_button_in_billing", true, R.id.input_zip);

    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    public void zip_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.zip_invalid_error_validation("zip_invalid_error_validation_in_billing", R.id.billingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    public void zip_invalid_error_validation_using_ime_button_in_billing() {
        ContactInfoTesterCommon.zip_invalid_error_validation("zip_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, true, R.id.input_city);
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    public void city_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.city_invalid_error_validation("zip_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, false, R.id.input_address);
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    public void city_invalid_error_validation_using_ime_button_in_billing() {
        ContactInfoTesterCommon.city_invalid_error_validation("zip_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, true, R.id.input_address);
    }

    /**
     * This test verifies the invalid error appearance for the address
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid address- invalid characters
     * Entering a valid address
     * Entering an invalid address after entering a valid one
     */
    public void address_invalid_error_validation_in_billing() {
        ContactInfoTesterCommon.address_invalid_error_validation("zip_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    public void state_invalid_error_in_billing() {
        ContactInfoTesterCommon.state_invalid_error("zip_invalid_error_validation_using_ime_button_in_billing", R.id.billingViewComponent, R.id.billingButtonComponentView);
    }


}
