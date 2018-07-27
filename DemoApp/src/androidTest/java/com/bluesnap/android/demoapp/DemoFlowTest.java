package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoFlowTest extends EspressoBasedTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);
    private Double demoPurchaseAmount;


    @After
    public void keepRunning() throws InterruptedException {
        mActivityRule.getActivity().finish();
        //Thread.sleep(1000);
    }

//    public void setup() throws InterruptedException, BSPaymentRequestException {
//        super.doSetup();
//
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

    public Double startDemoPurchase() {
        demoPurchaseAmount = randomTestValuesGenerator.randomDemoAppPrice();
        //Double demoTaxPrecent = randomTestValuesGenerator.randomTaxPercentage();
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);
        checkToken();
        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.productPriceEditText)).check(matches((isDisplayed())));
        //TODO: To test the tax we should calculate the subtotal
//                onView(withId(R.id.demoTaxEditText)).perform(typeText(demoTaxPrecent.toString()));
//        onData(allOf(is(instanceOf(String.class)), containsString("USD"))).inAdapterView(withId(R.id.rateSpinner))
//                        .perform(click());

        onView(withId(R.id.rateSpinner)).check(matches(isDisplayed())).perform(closeSoftKeyboard(), click());
//        ViewInteraction customTextView = onView(
//                allOf(withId(R.id.rateSpinner), withSpinnerText("USD")));
//        customTextView.perform(click());

        onData(allOf(is(instanceOf(String.class)), itemListMatcher(containsString("USD"))))
                .perform(click());

        // onView(withId(R.id.rateSpinner)).perform(click(), closeSoftKeyboard());
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(demoPurchaseAmount.toString()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        onView(withId(R.id.newCardButton)).perform(click());
        return demoPurchaseAmount;
    }

    @Test
    public void A_valid_CC_without_Shipping_Transaction_Test() throws InterruptedException {
        startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        DemoMainActivity demoMainActivity = mActivityRule.getActivity();
        String billingCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountry, false, false);
        onView(withId(R.id.buyNowButton)).perform(click());
        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finishDemoPurchase("USD", demoPurchaseAmount, sdkResult);
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

    @Test
    public void change_currency_once_back_to_usd_espresso_test() throws InterruptedException {
        Double startDemoPurchaseAmount = startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        onView(withId(R.id.buyNowButton)).check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));
        DemoMainActivity demoMainActivity = mActivityRule.getActivity();
        String billingCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountry, false, false);
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("CAD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("CAD")))));


        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("USD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getDecimalFormat().format(startDemoPurchaseAmount)))));


        onView(withId(R.id.buyNowButton)).perform(click());
        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finishDemoPurchase("USD", startDemoPurchaseAmount, sdkResult);


    }

    @Test
    public void change_currency_twice_back_to_usd_espresso_test() throws InterruptedException {
        Double startDemoPurchaseAmount = startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        onView(withId(R.id.buyNowButton)).check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));
        DemoMainActivity demoMainActivity = mActivityRule.getActivity();
        String billingCountry = BlueSnapService.getInstance().getUserCountry(demoMainActivity.getApplicationContext());
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountry, false, false);
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("CAD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("CAD")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("ILS"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("ILS")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("USD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getDecimalFormat().format(startDemoPurchaseAmount)))));


        onView(withId(R.id.buyNowButton)).perform(click());
        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finishDemoPurchase("USD", startDemoPurchaseAmount, sdkResult);
    }
}

