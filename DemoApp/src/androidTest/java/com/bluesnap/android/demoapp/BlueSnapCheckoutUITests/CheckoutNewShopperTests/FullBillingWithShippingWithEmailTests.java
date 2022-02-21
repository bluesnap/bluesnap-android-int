package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.filters.FlakyTest;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
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

import java.io.IOException;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingWithEmailTests extends CheckoutEspressoBasedTester {

    public FullBillingWithShippingWithEmailTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup();

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    @Test
    @FlakyTest
    public void full_billing_with_shipping_with_email_test() throws IOException {
        cc_line_fields_visibility_validation();
        cc_line_error_messages_not_displayed_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        default_country_state_view_validation_in_billing();
        check_store_card_visibility();
        pay_button_in_billing_validation();

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, true, true, true);
        new_credit_shipping_contact_info_visibility_validation();
        new_credit_shipping_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        pay_button_in_shipping_validation();

        TestUtils.goBack();
        contact_info_saved_validation_in_billing();
        shipping_same_as_billing_view_validation();
        shipping_same_as_billing_info_saved_in_billing_validation();
        shipping_same_as_billing_info_saved_in_shipping_validation();

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
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with shipping when choosing new credit card.
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
     * This test verifies that the billing contact info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button
     */
    public void contact_info_saved_validation_in_billing() throws IOException {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("contact_info_saved_validation_in_billing", applicationContext, R.id.billingViewComponent, defaultCountryKey, true, true);
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It checks that the shipping button changed to pay, and that it presents the correct amount.
     */
    public void shipping_same_as_billing_view_validation() {
//        Double amountAfterTax = TestUtils.round_amount(roundedPurchaseAmount + taxAmount);
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;

        TestUtils.setShippingSameAsBillingSwitch(true); //choose shipping same as billing option
//        String buyNowButtonText = TestUtils.getText(withId(R.id.buyNowButton));
        //verify that the "Shipping" button has changed to "Pay ..."
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("shipping_same_as_billing_view_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, tax);

        if (defaultCountryKey.equals("US"))
            //verify that the amount tax shipping component is displayed
            onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(R.id.amountTaxShippingComponentView))))
                    .withFailureHandler(new CustomFailureHandler("shipping_same_as_billing_view_validation" + ": Amount-tax layout is not visible"))
                    .check(matches(ViewMatchers.isDisplayed()));

        TestUtils.setShippingSameAsBillingSwitch(false); //un-checking the switch

        //verify that the shipping button has changed back "Shipping"
        CreditCardVisibilityTesterCommon.shipping_button_visibility_and_content_validation("shipping_same_as_billing_view_validation");
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It verifies that the billing info has been saved after the swipe.
     */
    public void shipping_same_as_billing_info_saved_in_billing_validation() throws IOException {
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "United States");
        onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(R.id.billingViewComponent)))).perform(scrollTo(), click());
        onData(hasToString(containsString("New York"))).inAdapterView(withId(R.id.state_list_view)).perform(click());

        TestUtils.setShippingSameAsBillingSwitch(true);

        //verify that the credit card info remained the same
        CreditCardLineTesterCommon.credit_card_info_saved_validation("shipping_same_as_billing_info_saved_in_billing_validation", "5288", "12/26", "123");

        //verify that the contact card info remained the same
        ContactInfoTesterCommon.contact_info_content_validation("shipping_same_as_billing_info_saved_in_billing_validation", applicationContext, R.id.billingViewComponent, "US", true, true);

        TestUtils.setShippingSameAsBillingSwitch(false);
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It verifies that the shipping info has been saved after choosing billing same as billing,
     * and than rewind the choice.
     */
    public void shipping_same_as_billing_info_saved_in_shipping_validation() throws IOException {
        //continue to shipping
        TestUtils.pressBuyNowButton();

        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "Brazil");

        //Fill in contact info in shipping
        ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, "BR", true, false);

        //return to billing
        TestUtils.goBack();

        TestUtils.setShippingSameAsBillingSwitch(true);
        TestUtils.setShippingSameAsBillingSwitch(false);

        //continue to shipping
        TestUtils.pressBuyNowButton();
        //verify that the shipping contact card info remained the same
        ContactInfoTesterCommon.contact_info_content_validation("shipping_same_as_billing_info_saved_in_shipping_validation", applicationContext, R.id.newShoppershippingViewComponent, "BR", true, false);
    }
}
