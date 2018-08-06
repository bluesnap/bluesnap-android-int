package com.bluesnap.android.demoapp;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingWithEmailTests extends EspressoBasedTest {
    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountryKey = BlueSnapService.getInstance().getUserCountry(this.applicationContext);
    }

    @Test
    public void full_billing_with_shipping_with_email_test() throws IOException {
        new_credit_cc_info_visibility_validation();
        new_credit_card_info_error_messages_validation();
        new_credit_billing_contact_info_visibility_validation();
        new_credit_billing_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_billing();
        default_country_state_view_validation_in_billing();
        shipping_button_validation();

        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountryKey, true, true);
        new_credit_shipping_contact_info_visibility_validation();
        new_credit_shipping_contact_info_error_messages_validation();
        default_country_zip_view_validation_in_shipping();
        default_country_state_view_validation_in_shipping();
        pay_button_in_shipping_validation();

        TestUtils.go_back_to_billing_in_new_card();
        contact_info_saved_validation_in_billing();
        shipping_same_as_billing_view_validation();
        shipping_same_as_billing_info_saved_in_billing_validation();
        shipping_same_as_billing_info_saved_in_shipping_validation();

    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void new_credit_cc_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_card_info_visibility_validation("new_credit_cc_info_visibility_validation");
    }

    /**
     * This test verifies that all invalid error messages of credit card info
     * fields are not displayed.
     */
    public void new_credit_card_info_error_messages_validation() {
        NewCardVisibilityTesterCommon.new_credit_card_info_error_messages_validation("new_credit_card_info_error_messages_validation");
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with shipping when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_billing_contact_info_error_messages_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_error_messages_validation("new_credit_contact_info_error_messages_validation", R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that all the shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    public void new_credit_shipping_contact_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation("new_credit_shipping_contact_info_visibility_validation", R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that all invalid error messages of billing contact info
     * fields are not displayed.
     */
    public void new_credit_shipping_contact_info_error_messages_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_error_messages_validation("new_credit_contact_info_error_messages_validation", R.id.billingViewComponent, true, false);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountryKey, R.id.billingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void default_country_state_view_validation_in_billing() {
        NewCardVisibilityTesterCommon.default_country_state_view_validation("default_country_state_view_validation_in_billing", R.id.billingViewComponent, defaultCountryKey);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    public void default_country_zip_view_validation_in_shipping() {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_shipping", defaultCountryKey, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public void default_country_state_view_validation_in_shipping() {
        NewCardVisibilityTesterCommon.default_country_state_view_validation("default_country_state_view_validation_in_shipping", R.id.newShoppershippingViewComponent, defaultCountryKey);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_shipping_validation() {
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;
        NewCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.shippingButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public void shipping_button_validation() {
        NewCardVisibilityTesterCommon.shipping_button_visibility_and_content_validation("shipping_button_validation", R.id.billingButtonComponentView);
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
//        Double amountAfterTax = TestUtils.round_amount(purchaseAmount + taxAmount);
        double tax = defaultCountryKey.equals("US") ? taxAmount : 0.00;

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight()); //choose shipping same as billing option
//        String buyNowButtonText = TestUtils.getText(withId(R.id.buyNowButton));
        //verify that the "Shipping" button has changed to "Pay ..."
        NewCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("shipping_same_as_billing_view_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, tax);

        if (defaultCountryKey.equals("US"))
            //verify that the amount tax shipping component is displayed
            onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(R.id.amountTaxShippingComponentView))))
                    .withFailureHandler(new CustomFailureHandler("shipping_same_as_billing_view_validation" + ": Amount-tax layout is not visible"))
                    .check(matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft()); //rewind the choice

        //verify that the shipping button has changed back "Shipping"
        onView(withId(R.id.buyNowButton))
                .withFailureHandler(new CustomFailureHandler("shipping_same_as_billing_view_validation" + ": Buy now button didn't changed back to Shipping"))
                .check(matches(withText("Shipping")));
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

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //verify that the credit card info remained the same
        CreditCardLineTesterCommon.credit_card_info_saved_validation("shipping_same_as_billing_info_saved_in_billing_validation", "5288", "12/26", "123");

        //verify that the contact card info remained the same
        ContactInfoTesterCommon.contact_info_content_validation("shipping_same_as_billing_info_saved_in_billing_validation", applicationContext, R.id.billingViewComponent, "US", true, true);

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft());
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It verifies that the shipping info has been saved after choosing billing same as billing,
     * and than rewind the choice.
     */
    public void shipping_same_as_billing_info_saved_in_shipping_validation() throws IOException {
        //continue to shipping
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView)))).perform(click());

        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "Brazil");

        //Fill in contact info in shipping
        ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, "BR", true, false);

        //return to billing
        TestUtils.go_back_to_billing_in_new_card();

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());
        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft());

        //continue to shipping
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView)))).perform(click());
        //verify that the shipping contact card info remained the same
        ContactInfoTesterCommon.contact_info_content_validation("shipping_same_as_billing_info_saved_in_shipping_validation", applicationContext, R.id.newShoppershippingViewComponent, "BR", true, false);
    }
}
