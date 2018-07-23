package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

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

    private String checkoutCurrency = "USD";
    private Double demoPurchaseAmount = 55.5;
    private Double taxAmount = demoPurchaseAmount * 0.05;
    private boolean fullInfo = false;
    private boolean withShipping = false;
    private boolean withEmail = false;


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
    }


    public void basic_flow_transaction() {
        //defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        startDemoPurchase();
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
        finishDemoPurchase("USD", demoPurchaseAmount, sdkResult);
    }

    public Double startDemoPurchase() {
        DemoMainActivity demoMainActivity = mActivityRule.getActivity();
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

    public void finishDemoPurchase(String currencySymbol, Double amountRequestedInTest, SdkResult sdkResult) {
        Espresso.registerIdlingResources(transactionMessageIR);
        IdlingPolicies.setIdlingResourceTimeout(120, TimeUnit.SECONDS);
        onView(withId(R.id.transactionResult))
                .check(matches(withText(containsString("Transaction Success"))));
        onView(withId(R.id.paymentResultTextView2))
                .check(matches(withText(containsString(currencySymbol))))
                .check(matches(withText(containsString(AndroidUtil.getDecimalFormat().format(demoPurchaseAmount)))))
        ;

        Espresso.unregisterIdlingResources(transactionMessageIR);

        Assert.assertTrue("SDK Result amount not equals", Math.abs(sdkResult.getAmount() - amountRequestedInTest) < 0.00000000001);
        Assert.assertEquals("SDKResult wrong currency", sdkResult.getCurrencyNameCode(), "USD");

    }

}
