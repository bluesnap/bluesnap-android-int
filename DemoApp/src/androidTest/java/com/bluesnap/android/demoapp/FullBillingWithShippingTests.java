package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.ButtonComponent;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by sivani on 19/07/2018.
 */
@RunWith(AndroidJUnit4.class)

public class FullBillingWithShippingTests extends EspressoBasedTest {
    private Double purchaseAmount = 55.5;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
        defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
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
     * This test verifies that the all billing contact info fields are displayed
     * according to full billing when choosing new credit card.
     */
    @Test
    public void new_credit_billing_contact_info_visibility_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.billingViewComponent, true, false);
    }

    /**
     * This test verifies that the all shipping contact info fields are displayed
     * according to shipping enabled when choosing new credit card.
     */
    @Test
    public void new_credit_shipping_contact_info_visibility_validation() throws InterruptedException {
        ContactInfoTesterCommon.continue_to_shipping(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.new_credit_contact_info_visibility_validation(R.id.newShoppershippingViewComponent, true, false);
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It checks that the shipping button changed to pay, and that the tax
     * and subtotal are presented if they supposed to.
     */
    @Test
    public void shipping_same_as_billing_view_validation() throws InterruptedException {
        onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());
//        String buyNowButtonText = TestUtils.getText(withId(R.id.buyNowButton));
        onView(withId(R.id.buyNowButton)).check(matches(withText(TestUtils.getStringFormatAmount("Pay",
                AndroidUtil.getCurrencySymbol("USD"), purchaseAmount))));


    }

    /**
     * This test verifies that changing the country in billing
     * doesn't change the country in shipping as well.
     */
    @Test
    public void country_changes_per_billing_validation() throws InterruptedException {
        NewCardVisibilityTesterCommon.country_changes_per_fragment_validation(true, true, false);
    }

    /**
     * This test verifies that changing the country in shipping
     * doesn't change the country in billing as well.
     */
    @Test
    public void country_changes_per_shipping_validation() throws InterruptedException {
        ContactInfoTesterCommon.continue_to_shipping(defaultCountry, true, false);
        NewCardVisibilityTesterCommon.country_changes_per_fragment_validation(false, true, false);
    }
}
