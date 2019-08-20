package com.bluesnap.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.IsNull.notNullValue;

public class ThreeDSecureUITests {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.bluesnap.android.demoapp";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;

    private final TestingShopperContactInfo googlePayContactInfo = new TestingShopperContactInfo("Susan madden", "bluesnapbluesnap@gmail.com",
            "Milton", "351 Eliot Street", "MA", "02186", "US", "United States");

    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class, false, false);

    private UIAutoTestingBlueSnapService<DemoMainActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

    //TODO: change this test so that it won't use the DemoApp
    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    /**
     * This test does an end-to-end GooglePay checkout flow
     * with minimal billing.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_minimal_billing_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException {
        TestingShopperCheckoutRequirements shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, false);

        fillInPurchaseRequirements(shopperCheckoutRequirements);
        startRegularCheckout();
        basic3DSFlow(shopperCheckoutRequirements);
    }

    /**
     * This test does an end-to-end GooglePay checkout flow
     * with full billing, shipping and email.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void threeDS_full_billing_with_email_with_shipping_basic_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException {
        TestingShopperCheckoutRequirements shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);

        fillInPurchaseRequirements(shopperCheckoutRequirements);
        startRegularCheckout();
        basic3DSFlow(shopperCheckoutRequirements);
    }

    public void fillInPurchaseRequirements(TestingShopperCheckoutRequirements shopperCheckoutRequirements) throws UiObjectNotFoundException, InterruptedException, JSONException {
        UiObject paymentAmountEditText = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/productPriceEditText"));

        // wait for field to appear
        while (!paymentAmountEditText.exists())
            sleep(1000);

        // set amount text and choose full billing, shipping and email
        paymentAmountEditText.setText(Double.toString(uIAutoTestingBlueSnapService.getPurchaseAmount()));
        if (shopperCheckoutRequirements.isShippingRequired())
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/shippingSwitch"))
                    .swipeRight(1);
        if (shopperCheckoutRequirements.isFullBillingRequired())
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/billingSwitch"))
                    .swipeRight(1);
        if (shopperCheckoutRequirements.isEmailRequired())
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/emailSwitch"))
                    .swipeRight(1);

        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/activate3DSSwitch"))
                .swipeRight(1);
    }

    public void startRegularCheckout() throws UiObjectNotFoundException {
        UiObject checkoutButton = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/merchantAppSubmitButton"));
        // start checkout
        checkoutButton.click();
    }

    public void basic3DSFlow(TestingShopperCheckoutRequirements shopperCheckoutRequirements) throws UiObjectNotFoundException, InterruptedException, JSONException {

        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/newCardButton"))
                .click();

        new_card_basic_fill_info(shopperCheckoutRequirements);


        if (shopperCheckoutRequirements.isShippingRequired()) {
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/shippingButtonComponentView"))
                    .click();
        } else {
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/billingButtonComponentView"))
                    .click();
        }

        UiObject threeDSSubmitButton = mDevice.findObject(new UiSelector()
                .text("SUBMIT"));

        // wait for cardinal activity
        while (!threeDSSubmitButton.exists())
            sleep(2000);

        mDevice.findObject(new UiSelector()
                .className(EditText.class.getName())).setText("1234");

        // press submit button in cardinal activity
        threeDSSubmitButton.click();

        UiObject transactionStatusTextView = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/transactionResult"));

        // wait for transaction status
        while (!transactionStatusTextView.exists())
            sleep(2000);

        String transactionResult = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "transactionResult")).getText();
        String transactionId = transactionResult.substring(transactionResult.indexOf("Success") + "Success".length() + 1);

        String vaultedShopperResult = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "shopperId")).getText();
        String vaultedShopperId = vaultedShopperResult.substring(vaultedShopperResult.indexOf("ID:") + "ID:".length() + 2);


//        uIAutoTestingBlueSnapService.checkSDKResult(CardinalManager.CardinalManagerResponse.AUTHENTICATION_SUCCEEDED.name());

        uIAutoTestingBlueSnapService.setVaultedShopperId(vaultedShopperId);

        // validate amount, currency and success status of the transaction in server
        uIAutoTestingBlueSnapService.retrieveTransaction(transactionId);

        // validate amount, currency and success status of the transaction in server
//        uIAutoTestingBlueSnapService.get_shopper_from_server(shopperCheckoutRequirements, googlePayContactInfo);

    }

    public void new_card_basic_fill_info(TestingShopperCheckoutRequirements shopperCheckoutRequirements) {
        if (shopperCheckoutRequirements.isShippingRequired() && shopperCheckoutRequirements.isFullBillingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling())
            TestUtils.setShippingSameAsBillingSwitch(false);

        //fill in info in billing and continue to shipping or paying
        CreditCardLineTesterCommon.fillInCCLineWithValidCard(TestingShopperCreditCard.VISA_CREDIT_CARD_FOR_3DS);
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryKey(), shopperCheckoutRequirements.isFullBillingRequired(), shopperCheckoutRequirements.isEmailRequired());


        if (shopperCheckoutRequirements.isShippingRequired()) {
            if (shopperCheckoutRequirements.isShippingSameAsBilling()) { //updating roundedPurchaseAmount to include tax since billing country is US
//                updatePurchaseAmountForTax();
            } else { //continue to fill in shipping
                TestUtils.pressBuyNowButton();
                ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryValue());
                ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryKey(), true, false);
            }
        }
    }

}
