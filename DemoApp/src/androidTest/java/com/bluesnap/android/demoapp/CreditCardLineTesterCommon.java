package com.bluesnap.android.demoapp;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Helper class for UI tests, handles the New CC form.
 *
 * Created by oz on 5/30/16.
 */
public class CreditCardLineTesterCommon {

    static Matcher<View> creditCardNumberErrorTextVM = withId(R.id.creditCardNumberErrorTextView);
    static Matcher<View> ccNumberEditTextVM = withId(R.id.creditCardNumberEditText);
    static Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);
    static Matcher<View> expEditTextVM = withId(R.id.expEditText);
    static Matcher<View> expErrorTextVM = withId(R.id.expErrorTextView);
    static Matcher<View> cvvEditTextVM = withId(R.id.cvvEditText);


    public static void check_ime_action_button_in_cc_info(String testName) {
        onView(withId(R.id.creditCardNumberEditText)).perform(click(), pressImeActionButton());
        onView(withId(R.id.input_name))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));
    }

    public static void check_filling_in_cc_info_flow(String testName) {
        onView(withId(R.id.creditCardNumberEditText)).perform(typeText(cardNumberGeneratorTest()));
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Exp date editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));

        onView(withId(R.id.expEditText)).perform(typeText("12 26"));
        onView(withId(R.id.cvvEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Cvv number editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));

        onView(withId(R.id.cvvEditText)).perform(typeText("123"));
    }

    /**
     * This test verifies that the credit card line info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button.
     */
    public static void credit_card_info_saved_validation(String testName, String creditCardNum, String expDate, String cvvNum) {
        //Verify cc number has been saved
        onView(withId(R.id.creditCardNumberEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Credit Card number wasn't saved"))
                .check(matches(withText(containsString(creditCardNum))));

        //Verify exp date has been saved
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Expiration date wasn't saved"))
                .check(matches(withText(expDate)));

        //Verify cvv number has been saved
        onView(withId(R.id.cvvEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Cvv number wasn't saved"))
                .check(matches(withText(cvvNum)));
    }

    /**
     * This test verifies that the credit card number error message is
     * displayed after entering all cc line info and then edit the
     * credit card number to an invalid one.
     */
    public static void invalid_cc_number_with_valid_exp_and_cvv_validation(String testName) {
        //fill in cc line info
        fillInCCLineWithValidCard();
        //change credit card number to an invalid one
        onView(withId(R.id.creditCardNumberEditText))
                .perform(click(), clearText(), typeText("5572758881122"));

        onView(withId(R.id.buyNowButton)).perform(click());


        onView(withId(R.id.creditCardNumberErrorTextView))
                .withFailureHandler(new CustomFailureHandler(testName + ": Invalid error message is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    public static void fillInCCLineWithValidCard() {

        //------------------------------------------
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()));

        onView(expEditTextVM).perform(typeText("12 26"));

        onView(cvvEditTextVM).perform(typeText("123"));

    }

    public static void changeCurrency(String currencyCode) {
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString(currencyCode))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
    }

    @Test
    public void ccn_new_card_validation_messages() {
        Matcher<View> ccNumberEditTextVM = withId(R.id.creditCardNumberEditText);
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        //Test validation of invalid number
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));
        onView(ccNumberEditTextVM)
                .perform(typeText(invalidCardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(ViewMatchers.isDisplayed()));

        // Clear the invalid number
        onView(ccNumberEditTextVM).perform(clearText());

        // Put a valid number
        onView(ccNumberEditTextVM)
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

        //------------------------------------------
        // EXP date
        //------------------------------------------

        Matcher<View> expErrorTextVM = withId(R.id.expErrorTextView);
        Matcher<View> expEditTextVM = withId(R.id.expEditText);

        // Test validation of invalid exp date: invalid Month (56)
        onView(withId(R.id.expErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));
        onView(expEditTextVM)
                .perform(typeText("56 44"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expErrorTextView)).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid month
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("12 26"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

        // Test validation of past date
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("11 17"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(expErrorTextVM).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid month
        onView(expEditTextVM).perform(click());
        onView(expEditTextVM).perform(clearText());
        onView(expEditTextVM)
                .perform(typeText("12 26"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
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
        onView(withId(R.id.cvvErrorTextView)).check(matches(ViewMatchers.isDisplayed()));

        // Now enter a valid cvv
        onView(cvvEditTextVM).perform(click());
        onView(cvvEditTextVM).perform(clearText());
        onView(cvvEditTextVM)
                .perform(typeText("123"), ViewActions.closeSoftKeyboard());
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.cvvErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

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
    @Test
    public void cc_new_card_empty_name_then_ccn() {
        Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);

        onView(withId(R.id.input_name)).perform(clearText(), typeText("john doe"));
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.expEditText)).perform(typeText(""));
        onView(buynowButtonVM).perform(click());
        onView(withId(R.id.creditCardNumberErrorTextView)).check(matches(not(ViewMatchers.isDisplayed())));

    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }


}
