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
public class CardFormTesterCommon {

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
        //onView(withId(R.id.expEditText)).perform(typeText(""));
        //onView(withId(R.id.cvvEditText)).perform(typeText("")).perform(ViewActions.closeSoftKeyboard());

        onView(expEditTextVM)
                .perform(typeText("12 26"));

        onView(cvvEditTextVM)
                .perform(typeText("123"));

    }

    public static void fillInContactInfoBilling(String country, boolean fullInfo, boolean withEmail) {
        onView(withId(R.id.input_name)).perform(typeText("La Fleur"), pressImeActionButton());

        if (withEmail)
            onView(withId(R.id.input_email)).perform(clearText(), typeText("test@sdk.com"), pressImeActionButton());

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(country))
            onView(withId(R.id.input_zip)).perform(clearText(), typeText("3abc 324a"), pressImeActionButton());

        if (fullInfo) {
            onView(withId(R.id.input_city)).perform(clearText(), typeText("Tel Aviv"), pressImeActionButton());
            onView(withId(R.id.input_address)).perform(clearText(), typeText("Rotchild street"));
            //Espresso.closeSoftKeyboard();
            if (country.equals("US") || country.equals("CA") || country.equals("BR")) {
                onView(withId(R.id.input_layout_state)).perform(scrollTo());
                onView(withId(R.id.input_state)).perform(click());
                if (country.equals("US"))
                    onData(hasToString(containsString("New York"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
                else if (country.equals("CA"))
                    onData(hasToString(containsString("Quebec"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
                else
                    onData(hasToString(containsString("Rio de Janeiro"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
            }
        }
    }

    public static void fillInContactInfoShipping(String country) {
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(typeText("La Fleur"), pressImeActionButton());

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(country))
            onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("3abc 324a"), pressImeActionButton());

        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("Tel Aviv"), pressImeActionButton());
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("Rotchild street"));
        if (country.equals("US") || country.equals("CA") || country.equals("BR")) {
            //onView(withId(R.id.input_layout_state)).perform(scrollTo());
            onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(scrollTo(), click());
            if (country.equals("US"))
                onData(hasToString(containsString("New York"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
            else if (country.equals("CA"))
                onData(hasToString(containsString("Quebec"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
            else
                onData(hasToString(containsString("Rio de Janeiro"))).inAdapterView(withId(R.id.state_list_view)).perform(click());

        }
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

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }



}
