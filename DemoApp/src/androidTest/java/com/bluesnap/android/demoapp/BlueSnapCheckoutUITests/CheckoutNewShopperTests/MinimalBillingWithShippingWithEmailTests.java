package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
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

@RunWith(AndroidJUnit4.class)

public class MinimalBillingWithShippingWithEmailTests extends CheckoutEspressoBasedTester {
    public MinimalBillingWithShippingWithEmailTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, true, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup();

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void minimal_billing_with_shipping_with_email_test() {
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        check_store_card_visibility();
        shipping_button_validation();

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, false, true, true);
        new_credit_shipping_contact_info_visibility_validation();
        new_credit_shipping_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        pay_button_in_shipping_validation();
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
        CreditCardVisibilityTesterCommon.contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, false, true);
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
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    public void default_country_zip_view_validation_in_shipping() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_shipping", defaultCountryKey, R.id.newShoppershippingViewComponent);
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
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_shipping_validation() {
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.shippingButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public void shipping_button_validation() {
        CreditCardVisibilityTesterCommon.shipping_button_visibility_and_content_validation("shipping_button_validation");
    }

}
