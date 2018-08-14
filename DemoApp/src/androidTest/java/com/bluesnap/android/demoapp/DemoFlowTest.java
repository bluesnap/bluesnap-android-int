package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.AndroidUtil;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;

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


/**
 * Created by oz on 5/26/16.
 */
public class DemoFlowTest extends EspressoBasedTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);
    private Double demoPurchaseAmount;


    @After
    public void keepRunning() {
        mActivityRule.getActivity().finish();
        //Thread.sleep(1000);
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

