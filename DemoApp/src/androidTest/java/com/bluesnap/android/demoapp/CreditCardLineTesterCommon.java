package com.bluesnap.android.demoapp;

import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
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

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }


}
