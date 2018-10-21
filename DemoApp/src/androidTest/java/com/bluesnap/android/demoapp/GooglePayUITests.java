package com.bluesnap.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
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

    protected UIAutoTestingBlueSnapService<DemoMainActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

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

    //TODO: change this test so that it won't use the DemoApp
    @Test
    public void basicGooglePayTest() throws UiObjectNotFoundException, InterruptedException, JSONException {
        UiObject paymentAmountEditText = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/productPriceEditText"));
        UiObject checkoutButton = mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/merchantAppSubmitButton"));

        // wait for field to appear
        while (!paymentAmountEditText.exists())
            sleep(1000);

        // set amount text and choose full billing, shipping and email
        paymentAmountEditText.setText(Double.toString(uIAutoTestingBlueSnapService.getPurchaseAmount()));
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/shippingSwitch"))
                .swipeRight(1);
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/billingSwitch"))
                .swipeRight(1);
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/emailSwitch"))
                .swipeRight(1);

        // start checkout
        checkoutButton.click();


        // choose googlePay method
        mDevice.findObject(new UiSelector()
                .resourceIdMatches(".*:id/googlePayButton"))
                .click();

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
        uIAutoTestingBlueSnapService.retrieveTransaction(transactionId);

        TestingShopperCheckoutRequirements shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
        uIAutoTestingBlueSnapService.get_shopper_from_server(shopperCheckoutRequirements, googlePayContactInfo);

    }
}
