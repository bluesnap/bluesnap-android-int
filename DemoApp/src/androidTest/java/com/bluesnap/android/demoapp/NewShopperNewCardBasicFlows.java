package com.bluesnap.android.demoapp;

import android.os.RemoteException;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by sivani on 23/07/2018.
 */

public class NewShopperNewCardBasicFlows extends EspressoBasedTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);

    DemoMainActivity demoMainActivity;
    private static final String TAG = "NewShopperBasicFlow";
    private boolean fullInfo = false;
    private boolean withShipping = false;
    private boolean withEmail = false;
    private boolean shippingSameAsBilling = false;

    private static final int HTTP_MAX_RETRIES = 2;
    private static final int HTTP_RETRY_SLEEP_TIME_MILLIS = 3750;
    public static final String SANDBOX_GET_SHOPPER = "vaulted-shoppers/";
    private String shopperId;
    private String getShopperResponse;

    @After
    public void keepRunning() throws InterruptedException {
        mActivityRule.getActivity().finish();
        //Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException {
        demoMainActivity = mActivityRule.getActivity();
        defaultCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }
    }

    public static Matcher<Object> itemListMatcher(final Matcher<String> itemListText) {
        Checks.checkNotNull(itemListText);
        return new BoundedMatcher<Object, String>(String.class) {
            @Override
            public boolean matchesSafely(String item) {
                return itemListText.matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text: " + itemListText.toString());
                itemListText.describeTo(description);
            }
        };
    }

    /**
     * This test verifies that the shipping same as billing switch works as
     * it should.
     * It checks that the shipping button changed to pay, and that the tax
     * and subtotal are presented if they supposed to.
     */
    @Test
    public void minimal_billing_basic_flow_transaction() throws InterruptedException {
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_shipping_basic_flow_transaction() throws InterruptedException {
        withShipping = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_email_basic_flow_transaction() throws InterruptedException {
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException {
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_shipping_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void shipping_same_as_billing_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        shippingSameAsBilling = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    /**
     * This test does an end-to-end new card flow for all 8 options:
     * with/without full billing, shipping, email.
     */
    public void new_card_basic_flow_transaction() {
        start_demo_purchase();
        onView(withId(R.id.newCardButton)).perform(click());
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        if (shippingSameAsBilling)
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //fill in info in billing and continue to shipping or paying
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, fullInfo, withEmail);

        if (withShipping) {
            if (defaultCountry.equals("US")) //updating purchaseAmount to include tax
                purchaseAmount *= 1.05; //TODO: add comment
            if (!shippingSameAsBilling) {
                ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, defaultCountry, true, false);
                onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());
            }
        }

        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        // merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();

        finish_demo_purchase(sdkResult);
    }

    public Double start_demo_purchase() {
        //purchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);
        checkToken();
        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.rateSpinner)).check(matches(isDisplayed())).perform(closeSoftKeyboard(), click());
        onData(allOf(is(instanceOf(String.class)), itemListMatcher(containsString(checkoutCurrency))))
                .perform(click());

        // onView(withId(R.id.rateSpinner)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(Double.toString(purchaseAmount)), ViewActions.closeSoftKeyboard());

        if (fullInfo)
            onView(withId(R.id.billingSwitch)).perform(swipeRight());

        if (withShipping)
            onView(withId(R.id.shippingSwitch)).perform(swipeRight());

        if (withEmail)
            onView(withId(R.id.emailSwitch)).perform(swipeRight());

        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        return purchaseAmount;
    }


    public void finish_demo_purchase(SdkResult sdkResult) {
        //wait for transaction to finish
        Espresso.registerIdlingResources(transactionMessageIR);
        IdlingPolicies.setIdlingResourceTimeout(120, TimeUnit.SECONDS);
        onView(withId(R.id.transactionResult)) //verify transaction success
                .check(matches(withText(containsString("Transaction Success"))));

        //TODO: change this stupid thing. in demoApp as well
        shopperId = TestUtils.getText(withId(R.id.shopperId)).substring(13);

        Espresso.unregisterIdlingResources(transactionMessageIR);

        //verify that both currency symbol and purchase amount received by sdkResult matches those we actually chose
        Assert.assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - purchaseAmount) < 0.00000000001);
        Assert.assertEquals("SDKResult wrong currency", sdkResult.getCurrencyNameCode(), checkoutCurrency);
    }

    private void get_shopper_after_transaction() {
        get_shopper_service(new GetShopperServiceInterface() {
            @Override
            public void onServiceSuccess() {
                new_shopper_info_saved_validation();
            }

            @Override
            public void onServiceFailure() {
                Log.d(TAG, "Cannot obtain shopper info from merchant server");
            }
        });
    }

    private void get_shopper_service(final GetShopperServiceInterface getShopperServiceInterface) {
        final SyncHttpClient httpClient = new SyncHttpClient();
        httpClient.setMaxRetriesAndTimeout(HTTP_MAX_RETRIES, HTTP_RETRY_SLEEP_TIME_MILLIS);
        httpClient.setBasicAuth(SANDBOX_USER, SANDBOX_PASS);
        //httpClient.addHeader("Token-Authentication", merchantToken);

        httpClient.get(SANDBOX_URL + SANDBOX_GET_SHOPPER + shopperId, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString, throwable);
                getShopperServiceInterface.onServiceFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                getShopperResponse = responseString;
                getShopperServiceInterface.onServiceSuccess();
            }
        });
    }

    //TODO: add validation that the new credit card info has been saved correctly
    private void new_shopper_info_saved_validation() {
        new_shopper_component_info_saved_validation(true);
        if (withShipping)
            new_shopper_component_info_saved_validation(false);
    }

    private void new_shopper_component_info_saved_validation(boolean isBillingInfo) {
        String address = isBillingInfo ? "address" : "address1";
        check_if_field_identify(isBillingInfo, "country", defaultCountry);

        check_if_field_identify(isBillingInfo, "first-name", "La");
        check_if_field_identify(isBillingInfo, "last-name", "Fleur");

        if (isBillingInfo && withEmail)
            check_if_field_identify(true, "email", "test@sdk.com");

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(defaultCountry))
            check_if_field_identify(isBillingInfo, "zip", "3abc 324a");

        if (fullInfo || !isBillingInfo) { //full info or shipping
            if (defaultCountry.equals("US") || defaultCountry.equals("CA") || defaultCountry.equals("BR")) {
                if (defaultCountry.equals("US"))
                    check_if_field_identify(isBillingInfo, "state", "New York");
                else if (defaultCountry.equals("CA"))
                    check_if_field_identify(isBillingInfo, "state", "Quebec");
                else
                    check_if_field_identify(isBillingInfo, "state", "Rio de Janeiro");
            }
            check_if_field_identify(isBillingInfo, "city", "New York");
            check_if_field_identify(isBillingInfo, address, "555 Broadway street");
        }
    }

    private void check_if_field_identify(boolean isBillingInfo, String fieldName, String expectedResult) {
        String shopperInfo = (isBillingInfo) ? getShopperResponse.substring(getShopperResponse.indexOf("<vaulted-shopper-id>") +
                ("<vaulted-shopper-id>").length(), getShopperResponse.indexOf("<payment-sources>")) :
                getShopperResponse.substring(getShopperResponse.indexOf("<shipping-contact-info>") +
                        ("<shipping-contact-info>").length(), getShopperResponse.indexOf("</shipping-contact-info>"));
        String fieldContent = shopperInfo.substring(shopperInfo.indexOf("<" + fieldName + ">") +
                ("<" + fieldName + ">").length(), shopperInfo.indexOf("</" + fieldName + ">"));
        assert fieldContent.equals(expectedResult);
    }


}
