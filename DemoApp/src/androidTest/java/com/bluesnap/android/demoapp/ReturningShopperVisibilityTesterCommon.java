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
    static ShopperContactInfo billingContactInfo = new ShopperContactInfo("La Fleur", "test@sdk.com",
            "New York", "555 Broadway street", "New York", "3abc 324a", "US");

    static ShopperContactInfo shippingContactInfo = new ShopperContactInfo("Taylor Love", "email@test.com",
            "CityTest", "AddressTest", "RJ", "12345", "BR");


    public static void credit_card_in_list_visibility_validation(String testName, String lastFourDigits, String expDate) {
//        String cardLastDigit = TestUtils.getText(isDescendantOfA(withId(R.id.oneLineCCViewComponentsListView)));
        onView(withId(R.id.ccLastFourDigitsTextView)).withFailureHandler(new CustomFailureHandler(testName + ": Credit card last four digit TextView does not display the correct content"))
                .check(matches(withText(lastFourDigits)));
        onView(withId(R.id.expTextView)).withFailureHandler(new CustomFailureHandler(testName + ": Expiration date TextView does not display the correct content"))
                .check(matches(withText(expDate)));
    }

    public static void credit_card_view_visibility_validation(String testName, String lastFourDigits, String expDate) {
        onView(withId(R.id.oneLineCCViewComponent)).withFailureHandler(new CustomFailureHandler(testName + ": One line CC TextView is not displayed"))
                .check(matches(isDisplayed()));
        onView(withId(R.id.ccLastFourDigitsTextView)).withFailureHandler(new CustomFailureHandler(testName + ": Credit card last four digit TextView does not display the correct content"))
                .check(matches(withText(lastFourDigits)));
        onView(withId(R.id.expTextView)).withFailureHandler(new CustomFailureHandler(testName + ": Expiration date TextView does not display the correct content"))
                .check(matches(withText(expDate)));
    }

    public static void summarized_contact_info_visibility_validation(String testName, int componentResourceId, String country, boolean fullInfo, boolean withEmail) {
        ShopperContactInfo contactInfo = (componentResourceId == R.id.billingViewSummarizedComponent) ? billingContactInfo : shippingContactInfo;
        summarized_contact_info_visibility_validation(testName, componentResourceId, country, fullInfo, withEmail, contactInfo);
    }

    public static void summarized_contact_info_visibility_validation(String testName, int componentResourceId, String country, boolean fullInfo, boolean withEmail, ShopperContactInfo contactInfo) {
        //verifies that the right component(billing/shipping) is displayed
        onView(withId(componentResourceId)).check(matches(isDisplayed()));

        //verifies that all right fields in the component are displayed and contain the correct data
        onView(allOf(withId(R.id.countryTextView), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country TextView doesn't present the correct content"))
                .check(matches(allOf(isDisplayed(), withText(country))));

        onView(allOf(withId(R.id.nameTextView), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Name TextView doesn't present the correct content"))
                .check(matches(allOf(isDisplayed(), withText(contactInfo.getName()))));

        if (withEmail) {
            onView(allOf(withId(R.id.emailTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": email TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getEmail()))));
        } else if (componentResourceId == R.id.billingViewSummarizedComponent)
            onView(allOf(withId(R.id.emailTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": email TextView is displayed"))
                    .check(matches(not(isDisplayed())));

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(country)) {
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Zip TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getZip()))));
        } else
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Zip TextView is displayed"))
                    .check(matches(not(isDisplayed())));

        if (fullInfo) {
            if (country.equals("US") || country.equals("CA") || country.equals("BR")) {
                onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(componentResourceId))))
                        .withFailureHandler(new CustomFailureHandler(testName + ": State TextView doesn't present the correct content"))
                        .check(matches(allOf(isDisplayed(), withText(contactInfo.getState()))));
            } else
                onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(componentResourceId))))
                        .withFailureHandler(new CustomFailureHandler(testName + ": State TextView is displayed"))
                        .check(matches(not(isDisplayed())));

            onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": City TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getCity()))));

            onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Address TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getAddress() + ','))));
        } else {
            onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": City TextView is displayed"))
                    .check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Address TextView is displayed"))
                    .check(matches(not(isDisplayed())));
        }

    }

}
