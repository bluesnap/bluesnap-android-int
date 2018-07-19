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

public class MinimalBillingTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
    }

    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    @Test
    public void empty_fields_invalid_error_validation_in_billing() throws InterruptedException {
        ContactInfoTesterCommon.empty_fields_invalid_error_validation(R.id.billingViewComponent, false, false, R.id.billingButtonComponentView);
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
        ContactInfoTesterCommon.zip_invalid_error_validation(R.id.billingViewComponent, false, R.id.input_name);
    }

    /**
     * This test verifies the ime action button works as it should
     * in credit card info
     */
    @Test
    public void check_ime_action_button_in_CC_info() throws InterruptedException {
        ContactInfoTesterCommon.check_ime_action_button_in_CC_info();
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    @Test
    public void check_ime_action_button_in_billing_contact_info() throws InterruptedException {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info(defaultCountry, R.id.billingViewComponent, false, false);
    }

}
