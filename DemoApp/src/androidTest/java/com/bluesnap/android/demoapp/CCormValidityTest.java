package com.bluesnap.android.demoapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
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
 * Checks validations on the new CC form input fields
 * 
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CCormValidityTest extends EspressoBasedTest {
    private static final String TAG = CCormValidityTest.class.getSimpleName();

    @After
    public void keepRunning() throws InterruptedException {
        //        while (true) { Thread.sleep(2000); } //Remove this
        Thread.sleep(1000);
    }


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(23.4, "USD");
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    @Test
    public void ccn_new_card_validation_messages() throws InterruptedException {

        //------------------------------------------
        // CC number
        //------------------------------------------

        Matcher<View> creditCardNumberErrorTextVM = withId(R.id.creditCardNumberErrorTextView);
        Matcher<View> ccNumberEditTextVM = withId(R.id.creditCardNumberEditText);
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        //Test validation of invalid number
        onView(creditCardNumberErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));
        onView(ccNumberEditTextVM)
                .perform(typeText(invalidCardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(ViewMatchers.isDisplayed()));

        // Clear the invalid number
        onView(ccNumberEditTextVM).perform(clearText());

        // Put a valid number
        onView(ccNumberEditTextVM)
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

        //------------------------------------------
        // EXP date
        //------------------------------------------

        Matcher<View> expErrorTextVM = withId(R.id.expErrorTextView);
        Matcher<View> expEditTextVM = withId(R.id.expEditText);

        // Test validation of invalid exp date: invalid Month (56)
        onView(expErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));
        onView(expEditTextVM)
                .perform(typeText("56 44"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(expErrorTextVM).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid month
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("12 26"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(expErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));

        // Test validation of past date
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("11 17"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(expErrorTextVM).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid month
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("12 26"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(expErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));


        //------------------------------------------
        // CVV
        //------------------------------------------

        Matcher<View> cvvErrorTextVM = withId(R.id.cvvErrorTextView);
        Matcher<View> cvvEditTextVM = withId(R.id.cvvEditText);

        // Test validation of invalid exp date: invalid cvv (56)
        onView(cvvEditTextVM)
                .perform(typeText("56"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(cvvErrorTextVM).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid cvv
        onView(cvvEditTextVM).perform(click());
        onView(cvvEditTextVM).perform(clearText());
        onView(cvvEditTextVM)
                .perform(typeText("123"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(cvvErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));

        // Test that when entering valid data and then modifying it eventually invalidates the form.
//        onView(creditCardNumberErrorTextVM).check(matches(not(ViewMatchers.isDisplayed())));
//        onView(ccNumberEditTextVM).perform(click());
//        onView(ccNumberEditTextVM).perform(clearText());
//        onView(ccNumberEditTextVM).perform(clearText(), typeText("1876987"), ViewActions.closeSoftKeyboard());
//        onView(buynowButtonVM).perform(click());
//        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(ViewMatchers.isDisplayed()));

    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void cc_new_Card_ccn_first() throws InterruptedException {
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.expEditText)).perform(typeText(""));
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

    }

    /**
     * This test is reproducing validation state where no input is entered to make sure IndexOutOFBoundsException is not thrown
     *
     * @throws InterruptedException
     */
    @Test
    public void cc_new_card_empty_name_then_ccn() throws InterruptedException {
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        onView(withId(R.id.input_name)).perform(clearText(), typeText("john doe"));
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.expEditText)).perform(typeText(""));
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

    }

}

