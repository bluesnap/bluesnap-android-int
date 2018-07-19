package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
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

public class MinimalBillingWithShippingTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
    }


    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    @Test
    public void empty_fields_invalid_error_validation_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.empty_fields_invalid_error_validation(R.id.newShoppershippingViewComponent, true, false, R.id.shippingButtonComponentView);
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
    @Test
    public void name_invalid_error_validation_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.name_invalid_error_validation(R.id.newShoppershippingViewComponent, false, R.id.input_zip);
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
    @Test
    public void name_invalid_error_validation_using_ime_button_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.name_invalid_error_validation(R.id.newShoppershippingViewComponent, true, 0);

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
    @Test
    public void zip_invalid_error_validation_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.zip_invalid_error_validation(R.id.newShoppershippingViewComponent, false, R.id.input_name);
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
    @Test
    public void zip_invalid_error_validation_using_ime_button_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.zip_invalid_error_validation(R.id.newShoppershippingViewComponent, true, 0);
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
    @Test
    public void city_invalid_error_validation_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.city_invalid_error_validation(R.id.newShoppershippingViewComponent, false, R.id.input_address);
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
    @Test
    public void city_invalid_error_validation_using_ime_button_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.city_invalid_error_validation(R.id.newShoppershippingViewComponent, true, 0);
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
    @Test
    public void address_invalid_error_validation_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.address_invalid_error_validation(R.id.newShoppershippingViewComponent, false, R.id.input_city);
    }

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    @Test
    public void state_invalid_error_in_shipping() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.state_invalid_error(R.id.newShoppershippingViewComponent, R.id.shippingButtonComponentView);
    }

    /**
     * This test verifies the ime action button works as it should
     * in shipping contact info
     */
    @Test
    public void check_ime_action_button_in_billing_contact_info() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(defaultCountry, false, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        ContactInfoTesterCommon.check_ime_action_button_in_contact_info(defaultCountry, R.id.newShoppershippingViewComponent, true, false);
    }

}
