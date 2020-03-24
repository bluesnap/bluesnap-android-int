package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.CustomFailureHandler;
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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 19/07/2018.
 */
@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingTests extends CheckoutEspressoBasedTester {

    public FullBillingWithShippingTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup();

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void full_billing_with_shipping_test() {
        //Pre-condition: credit card number wasn't entered
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        //Pre-condition: Current billing country is the default one
        default_country_zip_view_validation_in_billing();
        default_country_state_view_validation_in_billing();
        check_store_card_visibility();
        pay_button_in_billing_validation();

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, true, false, true);
        //Pre-condition: The current fragment displayed is shipping
        new_credit_shipping_contact_info_visibility_validation();
        new_credit_shipping_contact_info_error_messages_validation();
        //Pre-condition: Current country is the default one
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        pay_button_in_shipping_validation();

        TestUtils.goBack();

        //Pre-condition: Current country is the default one
        country_changes_per_fragment_validation();
        amount_tax_view_before_choosing_shipping_same_as_billing();
        TestUtils.setShippingSameAsBillingSwitch(false); //annul shipping same as billing option
        amount_tax_view_after_choosing_shipping_same_as_billing();
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
     * fields are not displayed.
     */
    public void cc_line_error_messages_not_displayed_validation() {
        CreditCardVisibilityTesterCommon.cc_line_error_messages_not_displayed_validation("cc_line_error_messages_not_displayed_validation");
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with shipping when choosing new credit card.
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
     * This test verifies that all the shipping contact info fields are displayed
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
    public void pay_button_in_billing_validation() {
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, tax);
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
     * This test verifies that changing the country in billing
     * doesn't change the country in shipping as well, and vice versa.
     */
    public void country_changes_per_fragment_validation() {
        CreditCardVisibilityTesterCommon.country_changes_per_fragment_validation("country_changes_per_fragment_validation");
    }

    /**
     * This test verifies that the amount tax shipping component is visible when
     * using shipping same as billing, after choosing USA (which has shipping tax),
     * and that it presents the right amount and tax.
     * It also verifies that the component isn't presented any longer after changing
     * to a country without tax.
     */
    public void amount_tax_view_before_choosing_shipping_same_as_billing() {
        //choose United States for shipping tax
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "United States");

        TestUtils.setShippingSameAsBillingSwitch(true); //choose shipping same as billing option

        //verify that the amount tax shipping component is presented
        CreditCardVisibilityTesterCommon.amount_tax_shipping_view_validation("amount_tax_view_before_choosing_shipping_same_as_billing", R.id.amountTaxShippingComponentView, checkoutCurrency,
                TestUtils.getDecimalFormat().format(purchaseAmount), TestUtils.getDecimalFormat().format(taxAmount));

        //change to Spain- a country without shipping tax
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "Spain");

        //verify that the amount tax shipping component isn't displayed
        onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(R.id.amountTaxShippingComponentView))))
                .withFailureHandler(new CustomFailureHandler("amount_tax_view_before_choosing_shipping_same_as_billing: Amount-tax component is displayed in a country without tax"))
                .check(matches(not(ViewMatchers.isDisplayed())));
    }

    /**
     * This test verifies that the amount tax shipping component isn't presented when changing
     * to a country without tax and choosing shipping same as billing.
     * Than it verifies that after choosing USA (which has shipping tax),
     * the amount tax shipping component is visible and it presents the right amount and tax.
     */
    public void amount_tax_view_after_choosing_shipping_same_as_billing() {
        //change to Costa Rica- a country without shipping tax
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "Costa Rica");

        TestUtils.setShippingSameAsBillingSwitch(true); //choose shipping same as billing option

        //verify that the amount tax shipping component isn't displayed
        onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(R.id.amountTaxShippingComponentView))))
                .withFailureHandler(new CustomFailureHandler("amount_tax_view_after_choosing_shipping_same_as_billing: Amount-tax component is displayed in a country without tax"))
                .check(matches(not(ViewMatchers.isDisplayed())));

        //change to United States, which has shipping tax
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "United States");

        //verify that the amount tax shipping component is presented
        CreditCardVisibilityTesterCommon.amount_tax_shipping_view_validation("amount_tax_view_after_choosing_shipping_same_as_billing", R.id.amountTaxShippingComponentView, checkoutCurrency,
                TestUtils.getDecimalFormat().format(purchaseAmount), TestUtils.getDecimalFormat().format(taxAmount));

    }

}
