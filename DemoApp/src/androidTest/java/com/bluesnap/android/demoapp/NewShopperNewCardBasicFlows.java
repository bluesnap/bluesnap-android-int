package com.bluesnap.android.demoapp;

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
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
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
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;
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
    private String checkoutCurrency = "USD";
    private Double demoPurchaseAmount = 55.5;
    private Double taxAmount = demoPurchaseAmount * 0.05;
    private boolean fullInfo = false;
    private boolean withShipping = false;
    private boolean withEmail = false;
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

//    @Before
//    public void setup() throws InterruptedException, BSPaymentRequestException {
//        DemoMainActivity demoMainActivity = mActivityRule.getActivity();
//        defaultCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
//    }

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

    @Test
    public void minimal_billing_basic_flow_transaction() throws InterruptedException {
        fullInfo = false;
        withShipping = false;
        withEmail = false;
        basic_flow_transaction();
        get_shopper_after_transaction();

    }

    @Test
    public void minimal_billing_with_shipping_basic_flow_transaction() throws InterruptedException {
        fullInfo = false;
        withShipping = true;
        withEmail = false;
        basic_flow_transaction();
    }

    @Test
    public void minimal_billing_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = false;
        withShipping = false;
        withEmail = true;
        basic_flow_transaction();
    }

    @Test
    public void minimal_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = false;
        withShipping = true;
        withEmail = true;
        basic_flow_transaction();
    }

    @Test
    public void full_billing_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = false;
        withEmail = false;
        basic_flow_transaction();
    }

    @Test
    public void full_billing_with_shipping_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = true;
        withEmail = false;
        basic_flow_transaction();
    }

    @Test
    public void full_billing_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = false;
        withEmail = true;
        basic_flow_transaction();
    }

    @Test
    public void full_billing_with_shipping_with_email_basic_flow_transaction() throws InterruptedException {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        basic_flow_transaction();
        get_shopper_after_transaction();
    }


    public void basic_flow_transaction() {
        //defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        start_demo_purchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        //fill in info in billing and continue to shipping or paying
        TestUtils.continue_to_shipping_or_pay_in_new_card(defaultCountry, fullInfo, withEmail);

        if (withShipping) {
            if (defaultCountry.equals("US")) //updating demoPurchaseAmount to include tax
                demoPurchaseAmount *= 1.05;
            ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, defaultCountry, true, false);
            onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());
        }

        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();

        finish_demo_purchase("USD", demoPurchaseAmount, sdkResult);
    }

    public Double start_demo_purchase() {
        demoMainActivity = mActivityRule.getActivity();
        defaultCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());

        //demoPurchaseAmount = randomTestValuesGeneretor.randomDemoAppPrice();
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);
        checkToken();
        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.rateSpinner)).check(matches(isDisplayed())).perform(closeSoftKeyboard(), click());
        onData(allOf(is(instanceOf(String.class)), itemListMatcher(containsString("USD"))))
                .perform(click());

        // onView(withId(R.id.rateSpinner)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(demoPurchaseAmount.toString()), ViewActions.closeSoftKeyboard());

        if (fullInfo)
            onView(withId(R.id.billingSwitch)).perform(swipeRight());

        if (withShipping)
            onView(withId(R.id.shippingSwitch)).perform(swipeRight());

        if (withEmail)
            onView(withId(R.id.emailSwitch)).perform(swipeRight());

        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        onView(withId(R.id.newCardButton)).perform(click());
        return demoPurchaseAmount;
    }

    public void finish_demo_purchase(String currencySymbol, Double amountRequestedInTest, SdkResult sdkResult) {
        Espresso.registerIdlingResources(transactionMessageIR);
        IdlingPolicies.setIdlingResourceTimeout(120, TimeUnit.SECONDS);
        onView(withId(R.id.transactionResult))
                .check(matches(withText(containsString("Transaction Success"))));

        //change this stupid thing
        shopperId = TestUtils.getText(withId(R.id.transactionResult)).substring(38);

        onView(withId(R.id.paymentResultTextView2))
                .check(matches(withText(containsString(currencySymbol))))
                .check(matches(withText(containsString(AndroidUtil.getDecimalFormat().format(demoPurchaseAmount)))))
        ;

        Espresso.unregisterIdlingResources(transactionMessageIR);

        Assert.assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - amountRequestedInTest) < 0.00000000001);
        Assert.assertEquals("SDKResult wrong currency", sdkResult.getCurrencyNameCode(), "USD");

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

    private void new_shopper_info_saved_validation() {
        boolean hasState = false;
        String state = null;
        //verify billing info has been saved correctly
        check_if_field_identify(true, "country", defaultCountry);

        check_if_field_identify(true, "first-name", "La");
        check_if_field_identify(true, "last-name", "Fleur");

        if (withEmail)
            check_if_field_identify(true, "email", "test@sdk.com");

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(defaultCountry))
            check_if_field_identify(true, "zip", "3abc 324a");

        if (fullInfo) {
            if (defaultCountry.equals("US") || defaultCountry.equals("CA") || defaultCountry.equals("BR")) {
                hasState = true;
                if (defaultCountry.equals("US")) {
                    state = "New York";
                    check_if_field_identify(true, "state", state);
                } else if (defaultCountry.equals("CA")) {
                    state = "Quebec";
                    check_if_field_identify(true, "state", state);
                } else {
                    state = "Rio de Janeiro";
                    check_if_field_identify(true, "state", state);
                }
            }

            check_if_field_identify(false, "country", defaultCountry);
            check_if_field_identify(false, "first-name", "La");
            check_if_field_identify(false, "last-name", "Fleur");
            if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(defaultCountry))
                check_if_field_identify(false, "zip", "3abc 324a");
            if (hasState)
                check_if_field_identify(false, "state", state);

            check_if_field_identify(false, "city", "New York");
            check_if_field_identify(false, "address1", "555 Broadway street");
        }


        //verify shipping info has been saved correctly
        if (withShipping) {
            check_if_field_identify(false, "first-name", "La");
            check_if_field_identify(false, "last-name", "Fleur");
        }


    }

    private void check_if_field_identify(boolean billingInfo, String fieldName, String expectedResult) {
        String shopperInfo = (billingInfo) ? getShopperResponse.substring(getShopperResponse.indexOf("<vaulted-shopper-id>") +
                ("<vaulted-shopper-id>").length(), getShopperResponse.indexOf("</" + fieldName + ">")) :
                getShopperResponse.substring(getShopperResponse.indexOf("<shipping-contact-info>") +
                        ("<shipping-contact-info>").length(), getShopperResponse.indexOf("</shipping-contact-info>"));
        assert (shopperInfo.substring(shopperInfo.indexOf("<" + fieldName + ">") +
                ("<" + fieldName + ">").length(), shopperInfo.indexOf("</" + fieldName + ">"))).equals(expectedResult);
    }



}
