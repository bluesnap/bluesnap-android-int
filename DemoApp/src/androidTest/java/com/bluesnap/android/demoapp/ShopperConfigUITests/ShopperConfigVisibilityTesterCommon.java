package com.bluesnap.android.demoapp.ShopperConfigUITests;

import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 27/08/2018.
 */

public class ShopperConfigVisibilityTesterCommon {

    public static void submit_button_visibility_and_content(String testName, int buttonComponent) {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Submit button is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Submit button doesn't display the correct content"))
                .check(matches(withText("Submit")));
    }

    public static void currency_hamburger_button_visibility(String testName) {
        onView(withId(R.id.hamburger_button))
                .withFailureHandler(new CustomFailureHandler(testName + " : Hamburger button is displayed"))
                .check(matches(not(ViewMatchers.isDisplayed())));
    }

}
