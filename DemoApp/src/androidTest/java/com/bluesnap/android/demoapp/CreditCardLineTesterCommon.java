package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.hamcrest.Matcher;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.getIdlingResources;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
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
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void check_filling_in_cc_info_flow() {
        onView(withId(R.id.creditCardNumberEditText)).perform(typeText(cardNumberGeneratorTest()));
        onView(withId(R.id.expEditText)).check(matches(TestUtils.isViesFocused()));

        onView(withId(R.id.expEditText)).perform(typeText("12 26"));
        onView(withId(R.id.cvvEditText)).check(matches(TestUtils.isViesFocused()));

        onView(withId(R.id.cvvEditText)).perform(typeText("123"));
    }

    public static void check_ime_action_button_in_cc_info() {
        onView(withId(R.id.creditCardNumberEditText)).perform(click(), pressImeActionButton());
//        onView(withId(R.id.expEditText)).check(matches(TestUtils.isViesFocused())).perform(pressImeActionButton());
//        onView(withId(R.id.cvvEditText)).check(matches(TestUtils.isViesFocused())).perform(pressImeActionButton());
        onView(withId(R.id.input_name)).check(matches(TestUtils.isViesFocused()));
    }

    /**
     * This test verifies that the credit card line info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button.
     */
    public static void cc_card_info_saved_validation(String defaultCountry, boolean fullInfo, boolean withEmail) throws InterruptedException {
        fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, defaultCountry, fullInfo, withEmail);
        //String creditCardNumber = TestUtils.getText(withId(R.id.creditCardNumberEditText));

        //Continue to Shipping and back to billing
        onView(withId(R.id.buyNowButton)).perform(click());
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        //Verify cc number has been saved in billing
        onView(withId(R.id.creditCardNumberEditText)).check(matches(withText("5288")));

        //Verify cc number has been saved in billing
        onView(withId(R.id.expEditText)).check(matches(withText("12/26")));

        //Verify cvv number has been saved in billing
        onView(withId(R.id.cvvEditText)).check(matches(withText("123")));
    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }


}
