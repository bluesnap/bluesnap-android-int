package com.bluesnap.android.demoapp;

import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Helper class for UI tests, handles the New CC form.
 *
 * Created by oz on 5/30/16.
 */
public class CardFormTesterCommon {

    public static void fillInAllFieldsWithValidCard() {
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.expEditText)).perform(typeText(""));
        onView(withId(R.id.cvvEditText)).perform(typeText("")).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.input_name)).perform(clearText(), typeText("john doe"));
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("abXD"));
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

    //TODO: this should accept country and state parameters
    public static void fillInShippingDetails() {
        onView(withId(R.id.input_layout_name)).perform(clearText(), typeText("john doe"));
        onView(withId(R.id.input_layout_address)).perform(clearText(), typeText("baker street"));
        onView(withId(R.id.input_layout_city)).perform(clearText(), typeText("London"));
        onView(withId(R.id.input_layout_state)).perform(clearText(), typeText("UK")).perform(ViewActions.closeSoftKeyboard());
    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }


}
