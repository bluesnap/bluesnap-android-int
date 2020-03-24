package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import android.view.View;

import com.bluesnap.android.demoapp.R;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon.cardNumberGeneratorTest;
import static com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon.invalidCardNumberGeneratorTest;
import static org.hamcrest.Matchers.not;


/**
 * Checks validations on the new CC form input fields
 * 
 * Created by oz on 5/26/16.
 */
//@RunWith(AndroidJUnit4.class)
public class CCFormValidityTest extends CheckoutEspressoBasedTester {
    private static final String TAG = CCFormValidityTest.class.getSimpleName();

    //    @Test
    public void ccn_new_card_validation_messages() {

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
//    @Test
    public void cc_new_Card_ccn_first() {
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
//    @Test
    public void cc_new_card_empty_name_then_ccn() {
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        onView(withId(R.id.input_name)).perform(clearText(), typeText("john doe"));
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.expEditText)).perform(typeText(""));
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

    }

}

