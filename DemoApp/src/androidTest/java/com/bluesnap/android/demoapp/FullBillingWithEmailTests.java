package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class FullBillingWithEmailTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test verifies that all the credit card fields are displayed as they should
     * when choosing new credit card.
     */
    @Test
    public void new_credit_cc_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_cc_info_visibility_validation();
    }

    /**
     * This test verifies that all the billing contact info fields are displayed
     * according to full billing with email when choosing new credit card.
     */
    @Test
    public void new_credit_billing_contact_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.billingViewComponent, true, true);
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
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    @Test
    public void empty_fields_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.empty_fields_invalid_error_validation(R.id.billingViewComponent, true, true);
    }

    /**
     * This test verifies the ime action button works as it should
     * in full billing contact info
     */
    @Test
    public void check_ime_action_button_in_billing_contact_info() throws InterruptedException {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info(defaultCountry, R.id.billingViewComponent, true, true);
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
    @Test
    public void name_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.name_invalid_error_validation(R.id.billingViewComponent, false, R.id.input_zip);
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
    @Test
    public void name_invalid_error_validation_using_ime_button_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.name_invalid_error_validation(R.id.billingViewComponent, true, 0);

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
    @Test
    public void email_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.email_invalid_error_validation(false, R.id.input_zip);
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
    @Test
    public void email_invalid_error_validation_using_ime_button_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.email_invalid_error_validation(true, 0);

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
    @Test
    public void zip_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.zip_invalid_error_validation(R.id.billingViewComponent, false, R.id.input_city);
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
    @Test
    public void zip_invalid_error_validation_using_ime_button_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.zip_invalid_error_validation(R.id.billingViewComponent, true, 0);
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
    @Test
    public void city_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.city_invalid_error_validation(R.id.billingViewComponent, false, R.id.input_address);
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
    @Test
    public void city_invalid_error_validation_using_ime_button_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.city_invalid_error_validation(R.id.billingViewComponent, true, 0);
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
    @Test
    public void address_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.address_invalid_error_validation(R.id.billingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    @Test
    public void state_invalid_error_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.state_invalid_error(R.id.billingViewComponent, R.id.billingButtonComponentView);
    }


}
