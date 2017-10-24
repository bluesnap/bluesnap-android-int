package com.bluesnap.android.demoapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.bluesnap.android.demoapp.CardFormTesterCommon.cardNumberGeneratorTest;
import static com.bluesnap.android.demoapp.CardFormTesterCommon.invalidCardNumberGeneratorTest;
import static org.hamcrest.Matchers.not;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CCormValidityTest extends EspressoBasedTest {
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, true, false);
    private BluesnapCheckoutActivity mActivity;

    @After
    public void keepRunning() throws InterruptedException {
        //        while (true) { Thread.sleep(2000); } //Remove this
        Thread.sleep(1000);
    }

    @Before
    public void setup() {
        super.setup();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(23.4);
        Intent intent = new Intent();
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        paymentRequest.setCurrencyNameCode("USD");
        paymentRequest.setShippingRequired(false);
        paymentRequest.allowRememberUser(false);
        mActivityRule.launchActivity(intent);
        mActivity = mActivityRule.getActivity();
        clearPrefs(mActivity.getApplicationContext());
    }

    @Test
    public void invalidCardMessageCheck() throws InterruptedException {
        //Test validation of invalid number
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(not(ViewMatchers.isDisplayed())));
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(invalidCardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(ViewMatchers.isDisplayed()));

        //Clear the invalid number
        onView(withId(R.id.creditCardNumberEditText)).perform(clearText());

        //Put a valid number
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(not(ViewMatchers.isDisplayed())));
    }


    @Test
    public void dateFieldValidationCheck() throws InterruptedException {
        //Test validation of invalid Month (56)
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(clearText(), typeText("56 44"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.expDateLabelTextView)).check(matches((TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(clearText());

        //Now enter a valid month
        onView(withId(R.id.expDateEditText))
                .perform(typeText("12 36"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
    }

    @Test
    public void dateFieldPastCheck() throws InterruptedException {
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(typeText("11 05"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches((TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(clearText());

        //Now enter a valid month
        onView(withId(R.id.expDateEditText))
                .perform(typeText("12 30"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
    }

    /**
     * Test that when entering valid data and then modifying it eventually invalidates the form.
     *
     * @throws InterruptedException
     */
    @Test
    public void test_validate_invalidate() throws InterruptedException {
        CardFormTesterCommon.fillInAllFieldsWithValidCard();

        onView(withId(R.id.creditCardNumberEditText))
                .perform(ViewActions.closeSoftKeyboard())
                //.perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(clearText())
                .perform(typeText("1"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(ViewMatchers.isDisplayed()));
    }


}

