package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.Constants;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 28/07/2018.
 */

public class ReturningShopperVisibilityTesterCommon {
    public static void credit_card_view_visibility_validation(String lastFourDigits, String expDate) {
        onView(withId(R.id.oneLineCCViewComponent)).check(matches(isDisplayed()));
        onView(withId(R.id.ccLastFourDigitsTextView)).check(matches(withText(lastFourDigits)));
        onView(withId(R.id.expTextView)).check(matches(withText(expDate)));
    }

    public static void summarized_contact_info_visibility_validation(int componentResourceId, boolean fullInfo, boolean withEmail,
                                                                     ShopperContactInfo contactInfo) {
        //verifies that the right component(billing/shipping) is displayed
        onView(withId(componentResourceId)).check(matches(isDisplayed()));

        //verifies that all right fields in the component are displayed and contain the correct data
        onView(allOf(withId(R.id.nameTextView), isDescendantOfA(withId(componentResourceId))))
                .check(matches(allOf(isDisplayed(), withText(contactInfo.getName()))));

        if (withEmail) {
            onView(withId(R.id.emailTextView))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getEmail()))));
        } else if (componentResourceId == R.id.billingViewSummarizedTextView)
            onView(withId(R.id.emailTextView)).check(matches(not(isDisplayed())));

        String country = contactInfo.getCountry();

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(country)) {
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId))))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getZip()))));
        } else
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));

        if (fullInfo) {
            if (country.equals("US") || country.equals("CA") || country.equals("BR")) {
                onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(componentResourceId))))
                        .check(matches(allOf(isDisplayed(), withText(contactInfo.getState()))));
            } else
                onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));

            onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(componentResourceId))))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getCity()))));

            String address = TestUtils.getText(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId))));
            onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId))))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getAddress()))));
        } else {
            onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));
        }

    }

    public static void contact_info_visibility_validation(int componentResourceId, boolean fullInfo, boolean withEmail,
                                                          ShopperContactInfo contactInfo) {

    }

}
