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
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;
import junit.framework.Assert;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.*;
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
    String billingCountryKey;
    String billingCountryValue;
    String shippingCountryKey;
    String shippingCountryValue;

    @After
    public void keepRunning() {
        mActivityRule.getActivity().finish();
        //Thread.sleep(1000);
    }

    @Before
    public void setup() {
        demoMainActivity = mActivityRule.getActivity();
        applicationContext = demoMainActivity.getApplicationContext();
        //defaultCountryKey = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        try {
            wakeUpDeviceScreen();
        } catch (RemoteException e) {
            fail("Could not wake up device");
            e.printStackTrace();
        }

        String[] billingCountry = randomTestValuesGenerator.randomReturningShopperCountry(applicationContext);
        billingCountryKey = billingCountry[0];
        billingCountryValue = billingCountry[1];

        String[] shippingCountry = randomTestValuesGenerator.randomReturningShopperCountry(applicationContext);
        shippingCountryKey = shippingCountry[0];
        shippingCountryValue = shippingCountry[1];
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
    public void minimal_billing_basic_flow_transaction() {
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_shipping_basic_flow_transaction() {
        withShipping = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_email_basic_flow_transaction() {
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void minimal_billing_with_shipping_with_email_basic_flow_transaction() {
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_basic_flow_transaction() {
        fullInfo = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_shipping_basic_flow_transaction() {
        fullInfo = true;
        withShipping = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_email_basic_flow_transaction() {
        fullInfo = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void full_billing_with_shipping_with_email_basic_flow_transaction() {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction();
        get_shopper_after_transaction();
    }

    @Test
    public void shipping_same_as_billing_basic_flow_transaction() {
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

        new_card_basic_fill_info();

        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finish_demo_purchase(sdkResult);
    }

    public void new_card_basic_fill_info() {
        if (shippingSameAsBilling)
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //fill in info in billing and continue to shipping or paying
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, billingCountryValue);
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountryKey, fullInfo, withEmail);

        onView(withId(R.id.buyNowButton)).perform(click());

        if (withShipping) {
            if (billingCountryKey.equals("US")) //updating purchaseAmount to include tax
                purchaseAmount *= 1.05; //TODO: add comment
            if (!shippingSameAsBilling) {
                ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, shippingCountryValue);
                ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, shippingCountryKey, true, false);
                onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());
            }
        }
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
                fail("Cannot obtain shopper info from merchant server");
            }
        });
    }

    private void get_shopper_service(final GetShopperServiceInterface getShopperServiceInterface) {
        BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_GET_SHOPPER + shopperId, "application/json", "application/json", sahdboxHttpHeaders);
        if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
            getShopperResponse = response.getResponseString();
            getShopperServiceInterface.onServiceSuccess();
        } else {
            Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
            getShopperServiceInterface.onServiceFailure();
        }
    }

    //TODO: add validation that the new credit card info has been saved correctly
    private void new_shopper_info_saved_validation() {
        new_shopper_component_info_saved_validation(true);
        if (withShipping)
            new_shopper_component_info_saved_validation(false);
    }

    private void new_shopper_component_info_saved_validation(boolean isBillingInfo) {
        String address = isBillingInfo ? "address" : "address1";
        String countryKey = isBillingInfo ? billingCountryKey : shippingCountryKey;
        String countryValue = isBillingInfo ? billingCountryValue : shippingCountryValue;


        ShopperContactInfo contactInfo = isBillingInfo ? ContactInfoTesterCommon.billingContactInfo : ContactInfoTesterCommon.shippingContactInfo;


        check_if_field_identify(isBillingInfo, "country", countryKey.toLowerCase());

        check_if_field_identify(isBillingInfo, "first-name", contactInfo.getFirstName());
        check_if_field_identify(isBillingInfo, "last-name", contactInfo.getLastName());

        if (isBillingInfo && withEmail)
            check_if_field_identify(true, "email", contactInfo.getEmail());

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(countryValue))
            check_if_field_identify(isBillingInfo, "zip", contactInfo.getZip());

        if (fullInfo || !isBillingInfo) { //full info or shipping
            if (countryKey.equals("US") || countryKey.equals("CA") || countryKey.equals("BR")) {
                if (countryKey.equals("US"))
                    check_if_field_identify(isBillingInfo, "state", "New York");
                else if (countryKey.equals("CA"))
                    check_if_field_identify(isBillingInfo, "state", "Quebec");
                else
                    check_if_field_identify(isBillingInfo, "state", "Rio de Janeiro");
            }
            check_if_field_identify(isBillingInfo, "city", contactInfo.getCity());
            check_if_field_identify(isBillingInfo, address, contactInfo.getAddress());
        }
    }

    private void check_if_field_identify(boolean isBillingInfo, String fieldName, String expectedResult) {
        String fieldContent = null;
        try {
            String shopperInfo = (isBillingInfo) ? getShopperResponse.substring(getShopperResponse.indexOf("<vaulted-shopper-id>") +
                    ("<vaulted-shopper-id>").length(), getShopperResponse.indexOf("<payment-sources>")) :
                    getShopperResponse.substring(getShopperResponse.indexOf("<shipping-contact-info>") +
                            ("<shipping-contact-info>").length(), getShopperResponse.indexOf("</shipping-contact-info>"));
            fieldContent = shopperInfo.substring(shopperInfo.indexOf("<" + fieldName + ">") +
                    ("<" + fieldName + ">").length(), shopperInfo.indexOf("</" + fieldName + ">"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("missing field in server response:\n Expected fieldName: " + fieldName + " Expected Value:" + expectedResult + "\n" + getShopperResponse);
        }

        Assert.assertEquals(fieldName + "was not saved correctly in DataBase", fieldContent, expectedResult);
    }
}
