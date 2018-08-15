package com.bluesnap.android.demoapp;

import com.bluesnap.androidapi.Constants;

import org.hamcrest.Matchers;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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
        ShopperContactInfo contactInfo = (componentResourceId == R.id.billingViewSummarizedComponent) ? ContactInfoTesterCommon.billingContactInfo : ContactInfoTesterCommon.shippingContactInfo;
        summarized_contact_info_visibility_validation(testName, componentResourceId, fullInfo, withEmail, contactInfo);
    }

    public static void summarized_contact_info_visibility_validation(String testName, int componentResourceId, boolean fullInfo, boolean withEmail, ShopperContactInfo contactInfo) {
        //verifies that the right component(billing/shipping) is displayed
        onView(withId(componentResourceId))
                .withFailureHandler(new CustomFailureHandler(testName + ": Component is not displayed"))
                .check(matches(isDisplayed()));

        //verifies that all right fields in the component are displayed and contain the correct data
        if (!contactInfo.getCountry().equals(""))
            onView(allOf(withId(R.id.countryTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Country TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getCountry()))));

        onView(allOf(withId(R.id.nameTextView), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Name TextView doesn't present the correct content"))
                .check(matches(allOf(isDisplayed(), withText(contactInfo.getFirstName() + " " + contactInfo.getLastName()))));

        if (withEmail) {
            onView(allOf(withId(R.id.emailTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": email TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getEmail()))));
        } else if (componentResourceId == R.id.billingViewSummarizedComponent)
            onView(allOf(withId(R.id.emailTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": email TextView is displayed"))
                    .check(matches(not(isDisplayed())));

        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(contactInfo.getCountry())) {
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Zip TextView doesn't present the correct content"))
                    .check(matches(allOf(isDisplayed(), withText(contactInfo.getZip()))));
        } else
            onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Zip TextView is displayed"))
                    .check(matches(not(isDisplayed())));

        if (fullInfo) {
            if (contactInfo.getCountry().equals("US") || contactInfo.getCountry().equals("CA") || contactInfo.getCountry().equals("BR")) {
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
            onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": State TextView is displayed"))
                    .check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": City TextView is displayed"))
                    .check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Address TextView is displayed"))
                    .check(matches(not(isDisplayed())));
        }

    }

    public static void shipping_empty_summarized_contact_info_visibility_validation(String testName) {
        //verifies that the right component(billing/shipping) is displayed
        onView(withId(R.id.shippingViewSummarizedComponent)).check(matches(isDisplayed()));

        String countryTry = TestUtils.getText(allOf(withId(R.id.countryTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))));
        String zipTry = TestUtils.getText(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))));

        //verifies that all right fields in the component are displayed and contain the correct data
        onView(allOf(withId(R.id.countryTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country TextView is displayed"))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.nameTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Name TextView is displayed"))
                .check(matches(allOf(isDisplayed(), withText(" "))));


        onView(allOf(withId(R.id.emailTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": email TextView is displayed"))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.zipTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Zip TextView is displayed"))
                .check(matches(allOf(isDisplayed(), withText(""))));


        String cityTry = TestUtils.getText(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))));
        String addressTry = TestUtils.getText(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))));

        onView(allOf(withId(R.id.stateTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": State TextView is displayed"))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.cityTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": City TextView is displayed"))
                .check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.addressTextView), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Address TextView is displayed"))
                .check(matches(not(isDisplayed())));
    }

    /**
     * This test verifies that changing the country in one component (billing/shipping contact
     * info) doesn't change the country in the other.
     *
     * @param summarizedComponentResourceId- The component in which it changes the country
     */
    public static void country_changes_per_fragment_validation(String testName, int summarizedComponentResourceId, String countryKey, String countryValue) {
        int editableComponent = (summarizedComponentResourceId == R.id.billingViewSummarizedComponent) ? R.id.billingViewComponent : R.id.returningShoppershippingViewComponent;
        int firstSummarizedComponent = summarizedComponentResourceId; //the component to change
        int secondSummarizedComponent = (summarizedComponentResourceId == R.id.billingViewSummarizedComponent) ? R.id.shippingViewSummarizedComponent : R.id.billingViewSummarizedComponent;
        int buttonComponent = (summarizedComponentResourceId == R.id.billingViewSummarizedComponent) ? R.id.returningShopperBillingFragmentButtonComponentView : R.id.returningShopperShippingFragmentButtonComponentView;

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(firstSummarizedComponent)))).perform(click());

        //Changing country to Spain in first component
        ContactInfoTesterCommon.changeCountry(editableComponent, countryValue);

        //go back to credit card
        TestUtils.go_back_to_credit_card_in_returning_shopper(true, buttonComponent);

        //Verify country hasn't change in other info component
        onView(allOf(withId(R.id.countryTextView), isDescendantOfA(withId(secondSummarizedComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country TextView changed in second component"))
                .check(matches(not(withText(countryKey))));
    }

    /**
     * This test verifies that when there is missing info in returning shopper,
     * and we press "pay", it passes to the edit component,
     * and not making a transaction.
     */
    public static void component_opens_when_pressing_buyNow_with_missing_info(String testName, boolean fullBilling, boolean withShipping, boolean withEmail, ReturningShoppersFactory.Shopper returningShopper) {
        int componentResourceId;
        if ((fullBilling && !returningShopper.isFullBilling()) || (withEmail && !returningShopper.isWithEmail()))
            componentResourceId = R.id.billingViewComponent;
        else if (withShipping && !returningShopper.isWithShipping())
            componentResourceId = R.id.newShoppershippingViewComponent;
        else
            componentResourceId = -1;

        if (componentResourceId != -1) {
            onView(withId(R.id.buyNowButton)).perform(click());
            //verifies that the right component(billing/shipping) with the missing info is displayed
            onView(withId(componentResourceId))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Component didn't open"))
                    .check(matches(isDisplayed()));
        }
    }
}
