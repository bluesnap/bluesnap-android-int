package com.bluesnap.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.EditText;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.IsNull.notNullValue;

public class ThreeDSecureUITests extends CheckoutEspressoBasedTester {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.bluesnap.android.demoapp";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;

    public ThreeDSecureUITests() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements();
    }

    public void setupBeforeTransaction(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired) throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements.setTestingShopperCheckoutRequirements(fullBillingRequired, emailRequired, shippingRequired, false);

        checkoutSetup(true, false, false, true);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for success credit card
     * with minimal billing/
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_success_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(false, false, false);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS, true, CardinalManager.CardinalManagerResponse.AUTHENTICATION_SUCCEEDED.name());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for success credit card
     * with full billing, shipping and email.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_success_full_billing_with_email_with_shipping_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(true, true, true);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS, true, CardinalManager.CardinalManagerResponse.AUTHENTICATION_SUCCEEDED.name());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for bypass credit card
     * with minimal billing.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_bypass_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(false, false, false);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_BYPASS, true, CardinalManager.CardinalManagerResponse.AUTHENTICATION_BYPASSED.name());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for unavailable credit card
     * with minimal billing.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_unavailable_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(false, false, false);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_UNAVAILABLE, false, CardinalManager.CardinalManagerResponse.AUTHENTICATION_UNAVAILABLE.name());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for unsupported credit card
     * with minimal billing.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_unsupported_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(false, false, false);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_NOT_SUPPORTED, false, CardinalManager.CardinalManagerResponse.AUTHENTICATION_NOT_SUPPORTED.name());
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for failure credit card
     * with minimal billing.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_failure_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupBeforeTransaction(false, false, false);
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_FAILURE, true, CardinalManager.CardinalManagerResponse.AUTHENTICATION_FAILED.name(), false);
    }

    private void basic3DSFlow(TestingShopperCreditCard creditCard, boolean isChallengeRequired, String expected3DSResult) throws UiObjectNotFoundException, InterruptedException {
        basic3DSFlow(creditCard, isChallengeRequired, expected3DSResult, true);
    }

    private void basic3DSFlow(TestingShopperCreditCard creditCard, boolean isChallengeRequired, String expected3DSResult, boolean isResultOK) throws UiObjectNotFoundException, InterruptedException {

        int buttonComponent = (shopperCheckoutRequirements.isShippingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;
        //onView(withId(R.id.newCardButton)).perform(click());

        new_card_basic_fill_info(creditCard);

        TestUtils.pressBuyNowButton(buttonComponent);

        if (isChallengeRequired) {
            UiObject threeDSSubmitButton = mDevice.findObject(new UiSelector()
                    .text("SUBMIT"));

            // wait for cardinal activity
            while (!threeDSSubmitButton.exists())
                sleep(2000);

            mDevice.findObject(new UiSelector()
                    .className(EditText.class.getName())).setText("1234");

            // press submit button in cardinal activity
            threeDSSubmitButton.click();
        }

        uIAutoTestingBlueSnapService.finishDemoPurchase(shopperCheckoutRequirements, expected3DSResult, isResultOK);

    }

}
