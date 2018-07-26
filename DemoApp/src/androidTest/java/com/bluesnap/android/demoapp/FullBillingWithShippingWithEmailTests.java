package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingWithEmailTests extends EspressoBasedTest {
    private String checkoutCurrency = "USD";
    private Double purchaseAmount = 55.5;
    private Double taxAmount = 0.0;

    @After
    public void keepRunning() throws InterruptedException {
        sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.applicationContext);
        taxAmount = defaultCountry.equals("US") ? TestUtils.round_amount(purchaseAmount * 0.05) : 0.0;
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    @Test
    public void new_credit_cc_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_cc_info_visibility_validation();
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with shipping when choosing new credit card.
     */
    @Test
    public void new_credit_billing_contact_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that all the shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    @Test
    public void new_credit_shipping_contact_info_visibility_validation() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, true);
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering billing info.
     * (according to its location, or us by default)
     */
    @Test
    public void default_country_view_validation_in_billing() throws InterruptedException, IOException {
        NewCardVisibilityTesterCommon.default_country_view_validation(applicationContext, defaultCountry, R.id.billingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    @Test
    public void default_country_zip_view_validation_in_billing() throws InterruptedException {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation(defaultCountry, R.id.billingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void default_country_state_view_validation_in_billing() throws InterruptedException {
        NewCardVisibilityTesterCommon.default_country_state_view_validation(R.id.billingViewComponent, defaultCountry);
    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering shipping info.
     * (according to its location, or us by default)
     */
    @Test
    public void default_country_view_validation_in_shipping() throws InterruptedException, IOException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, true);
        NewCardVisibilityTesterCommon.default_country_view_validation(applicationContext, defaultCountry, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     */
    @Test
    public void default_country_zip_view_validation_in_shipping() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, true);
        NewCardVisibilityTesterCommon.default_country_zip_view_validation(defaultCountry, R.id.newShoppershippingViewComponent);
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void default_country_state_view_validation_in_shipping() throws InterruptedException {
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, true);
        NewCardVisibilityTesterCommon.default_country_state_view_validation(R.id.newShoppershippingViewComponent, defaultCountry);
    }

    /**
     * This test verifies that the billing contact info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button
     */
    @Test
    public void contact_info_saved_validation_in_billing() throws InterruptedException {
        //Changing country to USA for state and zip appearance
        ContactInfoTesterCommon.change_country(R.id.billingViewComponent, "United States");
        //fill in info, continue to shipping and back to billing
        TestUtils.continue_to_shipping_or_pay_in_new_card("US", true, true);
        TestUtils.go_back_to_billing_in_new_card();

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_saved_validation(R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It checks that the shipping button changed to pay, and that the tax
     * and subtotal are presented if they supposed to.
     */
    @Test
    public void shipping_same_as_billing_view_validation() throws InterruptedException {
        Double amountAfterTax = TestUtils.round_amount(purchaseAmount + taxAmount);
        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight()); //choose shipping same as billing option
//        String buyNowButtonText = TestUtils.getText(withId(R.id.buyNowButton));
        //verify that the "Shipping" button has changed to "Pay ..."
        onView(withId(R.id.buyNowButton)).check(matches(withText(TestUtils.getStringFormatAmount("Pay",
                AndroidUtil.getCurrencySymbol(checkoutCurrency), purchaseAmount + taxAmount))));

        if (defaultCountry.equals("US"))
            //verify that the amount tax shipping component is presented
            NewCardVisibilityTesterCommon.amount_tax_shipping_view_validation(R.id.amountTaxShippingComponentView, checkoutCurrency,
                    TestUtils.get_amount_in_string(df, purchaseAmount), TestUtils.get_amount_in_string(df, taxAmount));

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft()); //rewind the choice

        //verify that the shipping button has changed back "Shipping"
        onView(withId(R.id.buyNowButton)).check(matches(withText("Shipping")));
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It verifies that the billing info has been saved after the swipe.
     */
    @Test
    public void shipping_same_as_billing_info_saved_in_billing_validation() throws InterruptedException {
        ContactInfoTesterCommon.change_country(R.id.billingViewComponent, "United States");
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, "US", true, true);

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //verify that the credit card info remained the same
        CreditCardLineTesterCommon.cc_card_info_saved_validation("5288", "12/26", "123");

        //verify that the contact card info remained the same
        ContactInfoTesterCommon.contact_info_saved_validation(R.id.billingViewComponent, true, true);

    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It verifies that the shipping info has been saved after choosing billing same as billing,
     * and than rewind the choice.
     */
    @Test
    public void shipping_same_as_billing_info_saved_in_shipping_validation() throws InterruptedException {
        //continue to shipping
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, true, true);
        //fill in info in shipping
        ContactInfoTesterCommon.change_country(R.id.newShoppershippingViewComponent, "United States");
        ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, "US", true, false);
        //return to billing
        TestUtils.go_back_to_billing_in_new_card();

        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());
        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeLeft());

        //continue to shipping
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView)))).perform(click());
        //verify that the shipping contact card info remained the same
        ContactInfoTesterCommon.contact_info_saved_validation(R.id.newShoppershippingViewComponent, true, false);
    }
}
