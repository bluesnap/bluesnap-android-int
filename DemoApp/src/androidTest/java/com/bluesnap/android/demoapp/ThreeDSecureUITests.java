package com.bluesnap.android.demoapp;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import android.widget.EditText;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONException;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.anything;
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

    public void setupForReturningShopperBeforeTransaction(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired, TestingShopperCreditCard creditCard) throws InterruptedException, BSPaymentRequestException, JSONException {
        //make transaction to create a new shopper
        uIAutoTestingBlueSnapService.createVaultedShopper(creditCard);

        shopperCheckoutRequirements.setTestingShopperCheckoutRequirements(fullBillingRequired, emailRequired, shippingRequired, false);

        //setup sdk for the returning shopper
        uIAutoTestingBlueSnapService.returningShopperSetUp(shopperCheckoutRequirements, true);

        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS, true, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_SUCCEEDED.name());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS, true, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_SUCCEEDED.name());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_BYPASS, true, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_BYPASSED.name());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_UNAVAILABLE, false, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_UNAVAILABLE.name());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_NOT_SUPPORTED, false, CardinalManager.ThreeDSManagerResponse.CARD_NOT_SUPPORTED.name());
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
        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_FAILURE, true, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_FAILED.name(), false);
    }

    /**
     * This test does an end-to-end checkout with 3DS flow
     * for success credit card
     * with minimal billing/
     * <p>
     * It runs in test mode.
     */
//    @Test
    public void threeDS_success_vaulted_card_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException, BSPaymentRequestException {
        setupForReturningShopperBeforeTransaction(false, false, false, TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS);
//        basic3DSFlow(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS_SUCCESS, true, CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_SUCCEEDED.name());
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
