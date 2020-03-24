package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class MinimalBillingWithShippingTests extends CheckoutEspressoBasedTester {
    public MinimalBillingWithShippingTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup();
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void minimal_billing_with_shipping_test() throws IOException {
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        check_store_card_visibility();
        shipping_button_validation();

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, false, false, true);
        new_credit_shipping_contact_info_visibility_validation();
        new_credit_shipping_contact_info_error_messages_validation();
        default_country_view_validation_in_shipping();
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        changing_country_view_validation_in_shipping();
        changing_country_zip_view_validation_in_shipping();
        changing_country_state_view_validation_in_shipping();

        pay_button_in_shipping_validation();
        check_ime_action_button_in_shipping_contact_info();
        contact_info_saved_validation_in_shipping();
        initial_currency_view_validation_in_shipping();
        change_currency_in_shipping_validation();
        change_currency_in_shipping_amount_validation();
        amount_tax_view_in_shipping_validation();

        Espresso.pressBack();
        credit_card_info_saved_validation();
        change_currency_in_billing_with_shipping_validation();
    }

    @Test
    public void minimal_billing_with_shipping_test_inputs() {
        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, false, false, true);
        empty_fields_invalid_error_validation_in_shipping();
        name_invalid_error_validation_in_shipping();
        name_invalid_error_validation_using_ime_button_in_shipping();
        zip_invalid_error_validation_in_shipping();
        zip_invalid_error_validation_using_ime_button_in_shipping();
        city_invalid_error_validation_in_shipping();
        city_invalid_error_validation_using_ime_button_in_shipping();
        address_invalid_error_validation_in_shipping();
        state_invalid_error_in_shipping();

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
     * according to full billing when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_billing_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, false, true);
    }

    /**
     * This test verifies that the all shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    public void new_credit_shipping_contact_info_visibility_validation() {
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_shipping_contact_info_visibility_validation", R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_shipping_contact_info_error_messages_validation() {
        CreditCardVisibilityTesterCommon.contact_info_error_messages_validation("contact_info_error_messages_validation", R.id.billingViewComponent, defaultCountryKey, true, false);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountryKey, R.id.billingViewComponent);
    }

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility and switch state
     */
    public void check_store_card_visibility() {
        CreditCardVisibilityTesterCommon.check_store_card_visibility("check_store_card_visibility" + shopperCheckoutRequirements, true);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering shipping info.
     * (according to its location, or us by default)
     */
    public void default_country_view_validation_in_shipping() throws IOException {
        CreditCardVisibilityTesterCommon.country_view_validation("default_country_view_validation_in_shipping", applicationContext, defaultCountryKey, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test verifies that the country image changes as expected, according
     * to different choices in shipping info.
     */
    public void changing_country_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.changing_country_view_validation("changing_country_view_validation_in_shipping", R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    public void default_country_zip_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_shipping", defaultCountryKey, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to different choices of countries in shipping info.
     */
    public void changing_country_zip_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.changing_country_zip_view_validation("changing_country_zip_view_validation_in_shipping", R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void default_country_state_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.default_country_state_view_validation("default_country_state_view_validation_in_shipping", R.id.newShoppershippingViewComponent, defaultCountryKey);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in shipping info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void changing_country_state_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.changing_country_state_view_validation("changing_country_state_view_validation_in_shipping", R.id.newShoppershippingViewComponent);
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public void shipping_button_validation() {
        CreditCardVisibilityTesterCommon.shipping_button_visibility_and_content_validation("shipping_button_validation");
    }

    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    public void empty_fields_invalid_error_validation_in_shipping() {
        ContactInfoTesterCommon.contact_info_empty_fields_invalid_error_validation("empty_fields_invalid_error_validation_in_shipping", R.id.newShoppershippingViewComponent, true, false);
    }


    /**
     * This test verifies the invalid error appearance for the name
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    public void name_invalid_error_validation_in_shipping() {
        ContactInfoTesterCommon.name_invalid_error_validation("name_invalid_error_validation_in_shipping", R.id.newShoppershippingViewComponent, false, R.id.input_zip);
    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    public void name_invalid_error_validation_using_ime_button_in_shipping() {
        ContactInfoTesterCommon.name_invalid_error_validation("name_invalid_error_validation_using_ime_button_in_shipping", R.id.newShoppershippingViewComponent, true, R.id.input_zip);

    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    public void zip_invalid_error_validation_in_shipping() {
        ContactInfoTesterCommon.zip_invalid_error_validation("zip_invalid_error_validation_in_shipping", R.id.newShoppershippingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    public void zip_invalid_error_validation_using_ime_button_in_shipping() {
        ContactInfoTesterCommon.zip_invalid_error_validation("zip_invalid_error_validation_using_ime_button_in_shipping", R.id.newShoppershippingViewComponent, true, R.id.input_city);
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    public void city_invalid_error_validation_in_shipping() {
        ContactInfoTesterCommon.city_invalid_error_validation("city_invalid_error_validation_in_shipping", R.id.newShoppershippingViewComponent, false, R.id.input_address);
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    public void city_invalid_error_validation_using_ime_button_in_shipping() {
        ContactInfoTesterCommon.city_invalid_error_validation("city_invalid_error_validation_using_ime_button_in_shipping", R.id.newShoppershippingViewComponent, true, R.id.input_address);
    }

    /**
     * This test verifies the invalid error appearance for the address
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid address- invalid characters
     * Entering a valid address
     * Entering an invalid address after entering a valid one
     */
    public void address_invalid_error_validation_in_shipping() {
        ContactInfoTesterCommon.address_invalid_error_validation("address_invalid_error_validation_in_shipping", R.id.newShoppershippingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    public void state_invalid_error_in_shipping() {
        ContactInfoTesterCommon.state_invalid_error("state_invalid_error_in_shipping", R.id.newShoppershippingViewComponent, R.id.shippingButtonComponentView);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */

    public void pay_button_in_shipping_validation() {
        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, defaultCountryValue);
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.shippingButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies the ime action button works as it should
     * in shipping contact info
     */
    public void check_ime_action_button_in_shipping_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_shipping_contact_info", defaultCountryKey, R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that the shipping contact info is saved when
     * going back to billing and entering the shipping once again.
     */
    public void contact_info_saved_validation_in_shipping() throws IOException {
        //Changing country to Brazil for state and zip appearance
        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "Brazil");
        //fill in info
        ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, "BR", true, false);

        //go back and forward
        TestUtils.goBack();
        TestUtils.pressBuyNowButton();

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("contact_info_saved_validation_in_shipping", applicationContext, R.id.newShoppershippingViewComponent, "BR", true, false);
    }

    /**
     * This test verifies that the credit card line info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button.
     */
    public void credit_card_info_saved_validation() {
        CreditCardLineTesterCommon.credit_card_info_saved_validation("credit_card_info_saved_validation", "5288", "12/26", "123");
    }

    /**
     * This test verifies that the initial currency in shipping is presented
     * as it should in the hamburger buy buttons.
     */
    public void initial_currency_view_validation_in_shipping() {
        CurrencyChangeTesterCommon.currency_view_validation("initial_currency_view_validation_in_shipping", R.id.shippingButtonComponentView, checkoutCurrency);
    }

    /**
     * This test verifies that changing the currency in billing
     * changes as it should in shipping.
     */
    public void change_currency_in_shipping_validation() {
        CurrencyChangeTesterCommon.changeCurrency("CAD");
        CurrencyChangeTesterCommon.currency_view_validation("change_currency_in_shipping_validation", R.id.shippingButtonComponentView, "CAD");
    }

    /**
     * This test verifies that changing the currency in billing, while shipping is enabled,
     * changes as it should in shipping.
     */
    public void change_currency_in_billing_with_shipping_validation() {
        CurrencyChangeTesterCommon.changeCurrency("GBP");
        TestUtils.pressBuyNowButton();
        CurrencyChangeTesterCommon.currency_view_validation("change_currency_in_billing_with_shipping_validation", R.id.shippingButtonComponentView, "GBP");
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one, the amount remains the same
     */
    public void change_currency_in_shipping_amount_validation() {
        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, defaultCountryValue);
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        CurrencyChangeTesterCommon.change_currency_amount_validation("change_currency_in_shipping_amount_validation", R.id.shippingButtonComponentView, checkoutCurrency, TestUtils.getDecimalFormat().format(purchaseAmount + tax));
    }

    /**
     * This test verifies that the amount tax shipping component is visible
     * in shipping, and that it presents the right amount and tax.
     */
    public void amount_tax_view_in_shipping_validation() {
        if (!defaultCountryKey.equals("US"))
            ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "United States");

        //verify that the amount tax shipping component is presented
        CreditCardVisibilityTesterCommon.amount_tax_shipping_view_validation("amount_tax_view_in_shipping_validation", R.id.shippingAmountTaxShippingComponentView, checkoutCurrency,
                TestUtils.getDecimalFormat().format(purchaseAmount), TestUtils.getDecimalFormat().format(taxAmount));
    }
}
