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
 * Created by sivani on 21/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class MinimalBillingWithEmailTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.applicationContext);
    }

    @Test
    public void minimal_billing_with_email_test() {
        new_credit_card_info_visibility_validation();
        new_credit_billing_contact_info_visibility_validation();
        default_country_zip_view_validation_in_billing();
        pay_button_in_billing_validation();
        check_ime_action_button_in_billing_contact_info();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing new credit card.
     */
    public void new_credit_card_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_card_info_visibility_validation("new_credit_card_info_visibility_validation");
    }

    /**
     * This test verifies that the all billing contact info fields are displayed
     * according to minimal billing when choosing new credit card.
     */
    public void new_credit_billing_contact_info_visibility_validation() {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation("new_credit_billing_contact_info_visibility_validation", R.id.billingViewComponent, false, true);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        NewCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing", defaultCountry, R.id.billingViewComponent);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        NewCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_billing_validation", R.id.billingButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    public void check_ime_action_button_in_billing_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info", defaultCountry, R.id.billingViewComponent, false, true);
    }
}
