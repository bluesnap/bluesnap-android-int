package com.bluesnap.android.demoapp;

import android.content.Context;
import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by sivani on 18/10/2018.
 */


@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class GooglePayUITests {

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
        Context context = ApplicationProvider.getApplicationContext();
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
     * with full billing, shipping and email.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void googlePay_checkout_transaction() throws UiObjectNotFoundException, InterruptedException, JSONException {
        TestingShopperCheckoutRequirements shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);

        fillInPurchaseRequirements(shopperCheckoutRequirements);
        startRegularCheckout();
        basicGooglePayFlow(shopperCheckoutRequirements, true);
    }

    /**
     * This test does an end-to-end GooglePay choose payment
     * method and create payment flow
     * with full billing, shipping and email.
     * <p>
     * It runs in test mode.
     */
    @Test
    public void googlePay_choose_and_create_payment() throws UiObjectNotFoundException, InterruptedException, JSONException {
        TestingShopperCheckoutRequirements shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);

        // choose googlePay payment method
        fillInReturningShopper();
        fillInPurchaseRequirements(shopperCheckoutRequirements);
        googlePayChoosePaymentMethodFlow();

        // create googlePayMethod
        startCreatePayment();
        basicGooglePayFlow(shopperCheckoutRequirements, false);
    }

    public void fillInReturningShopper() throws UiObjectNotFoundException, InterruptedException, JSONException {
        UiObject returningShopperSwitch = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/returningShopperSwitch"));

        UiObject returningShopperEditText = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/returningShopperEditText"));

        uIAutoTestingBlueSnapService.createVaultedShopper(false);

        returningShopperSwitch.swipeRight(1);
        returningShopperEditText.setText(uIAutoTestingBlueSnapService.getVaultedShopperId());
        returningShopperEditText.click();

        // click price edit text change focus
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/productPriceEditText")).click();

        UiObject shopperDetailsTextView = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/shopperDetailsTextView"));

        // wait for shopper token
        while (!shopperDetailsTextView.getText().contains("Fanny"))
            sleep(2000);
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
    }

    public void startRegularCheckout() throws UiObjectNotFoundException {
        UiObject checkoutButton = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/merchantAppSubmitButton"));
        // start checkout
        checkoutButton.click();
    }

    public void googlePayChoosePaymentMethodFlow() throws UiObjectNotFoundException, JSONException, InterruptedException {
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/shopperConfigSwitch"))
                .swipeRight(1);

        mDevice.pressBack();

        UiObject choosePaymentButton = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/merchantAppSubmitButton"));
        // start choose payment flow
        choosePaymentButton.click();

        // choose googlePay method
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/googlePayButton"))
                .click();

        // wait for choose payment flow to complete
        while (!choosePaymentButton.exists())
            sleep(1000);

        uIAutoTestingBlueSnapService.chosenPaymentMethodValidationInServer(true);
    }

    //Pre-condition: returning shopper and activate shopper config switches are on
    //Pre-condition: returningShopperId is filled in
    public void startCreatePayment() throws UiObjectNotFoundException, InterruptedException {
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/returningShopperEditText"))
                .click();

        // click price edit text change focus
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/productPriceEditText"))
                .click();

        UiObject shopperDetailsTextView = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/shopperDetailsTextView"));

        // wait for shopper token
        while (!shopperDetailsTextView.getText().contains("Fanny"))
            sleep(2000);

        mDevice.pressBack();

        UiObject createPaymentButton = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/merchantAppSecondStepForShopperConfigurationButton"));
        // start checkout
        createPaymentButton.click();
    }

    public void basicGooglePayFlow(TestingShopperCheckoutRequirements shopperCheckoutRequirements, boolean pressGooglePayButton) throws UiObjectNotFoundException, InterruptedException, JSONException {
        if (pressGooglePayButton) {// choose googlePay method
            mDevice.findObject(new UiSelector()
                    .resourceIdMatches(".*:id/googlePayButton"))
                    .click();
        }

        UiObject googlePayContinueButton = mDevice.findObject(new UiSelector()
                .text("Continue"));

        // wait for googlePay pop-up
        while (!googlePayContinueButton.exists())
            sleep(2000);

        // press continue button in googlePay pop-up
        googlePayContinueButton.click();

        UiObject transactionStatusTextView = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/transactionResult"));

        // wait for transaction status
        while (!transactionStatusTextView.exists())
            sleep(2000);

        String transactionResult = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "transactionResult")).getText();
        String transactionId = transactionResult.substring(transactionResult.indexOf("Success") + "Success".length() + 1);

        String vaultedShopperResult = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "shopperId")).getText();
        String vaultedShopperId = vaultedShopperResult.substring(vaultedShopperResult.indexOf("ID:") + "ID:".length() + 2);

        uIAutoTestingBlueSnapService.setVaultedShopperId(vaultedShopperId);

        // validate amount, currency and success status of the transaction in server
        uIAutoTestingBlueSnapService.retrieveTransaction(transactionId);

        // validate amount, currency and success status of the transaction in server
        uIAutoTestingBlueSnapService.get_shopper_from_server(shopperCheckoutRequirements, googlePayContactInfo);

    }




}
