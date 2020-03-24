package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters;


import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperContactInfo;

import org.hamcrest.Matchers;

import javax.annotation.Nullable;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 17/07/2018.
 */

public class ContactInfoTesterCommon {
    public static final TestingShopperContactInfo billingContactInfo = new TestingShopperContactInfo("La Fleur", "test@sdk.com",
            "New York", "555 Broadway street", "NY", "3abc 324a", "US", "United States");

    public static final TestingShopperContactInfo shippingContactInfo = new TestingShopperContactInfo("Taylor Love", "null",
            "CityTest", "AddressTest", "RJ", "12345", "BR", "Brazil");

    public static final TestingShopperContactInfo editBillingContactInfo = new TestingShopperContactInfo("Funny Brice", "broadwaydancecenter@gmail.com",
            "Barcelona", "77 Rambla street", "QC", "4815", "CA", "Canada");

    public static final TestingShopperContactInfo editShippingContactInfo = new TestingShopperContactInfo("Janet Weiss", "null",
            "Denton", "75 some street", "NY", "162342", "US", "United States");

    public static void check_ime_action_button_in_contact_info(String testName, String country, int componentResourceId, boolean fullInfo, boolean withEmail) {
        onView(Matchers.allOf(ViewMatchers.withId(R.id.input_name), isDescendantOfA(withId(componentResourceId)))).perform(scrollTo(), click(), pressImeActionButton());
        if (withEmail)
            onView(withId(R.id.input_email))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input email editText is not focused, after pressing the ime button"))
                    .check(matches(TestUtils.isViewFocused())).perform(pressImeActionButton());
        if (TestUtils.checkCountryHasZip(country))
            onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is not focused, after pressing the ime button"))
                    .check(matches(TestUtils.isViewFocused())).perform(pressImeActionButton());

        if (fullInfo) {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input city editText is not focused, after pressing the ime button"))
                    .check(matches(TestUtils.isViewFocused())).perform(pressImeActionButton());
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input address editText is not focused, after pressing the ime button"))
                    .check(matches(TestUtils.isViewFocused())).perform(pressImeActionButton());
        }
    }

    public static void contact_info_empty_fields_invalid_error_validation(String testName, int componentResourceId, boolean fullInfo, boolean withEmail) {
        int buttonComponent = (componentResourceId == R.id.billingViewComponent) ? R.id.billingButtonComponentView : R.id.shippingButtonComponentView;

        //Choosing brazil (that has state and zip)
        changeCountry(componentResourceId, "United States");

        //fix this- generalize to match shipping as well
        //Continue- leaving all fields empty
        TestUtils.pressBuyNowButton(buttonComponent);

        //verify input name editText is focused
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name editText is not displayed"))
                .check(matches(isDisplayed()));

        //verify error messages are displayed
        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name errorText is not displayed"))
                .check(matches(isDisplayed()));

        if (withEmail)
            onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_email)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input email errorText is not displayed"))
                    .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input zip errorText is not displayed"))
                .check(matches(isDisplayed()));

        if (fullInfo) {
            onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_state)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input state errorText is not displayed"))
                    .check(matches(isDisplayed()));

            onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_city)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input city errorText is not displayed"))
                    .perform(scrollTo()).check(matches(isDisplayed()));

            onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_address)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input address errorText is not displayed"))
                    .perform(scrollTo()).check(matches(isDisplayed()));
        }

    }

    public static void name_invalid_error_validation(String testName, int componentResourceId, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "", true);

        //enter a valid name and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Fanny Brice", false);

        //Entering an invalid name- only one word and verify error message is displayed
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Sawyer", true);

        //enter a valid name and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Fanny Brice", false);

        //Entering an invalid name- less than 2 characters and verify error message is displayed
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "L", true);

        //enter a valid name and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Fanny Brice", false);

        //Entering an invalid name- spaces and verify error message is displayed
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Sawyer     ", true);

        //enter a valid name and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_name, R.id.input_layout_name, componentResourceId, withImeButton, nextFieldResourceId, "Fanny Brice", false);

    }

    public static void email_invalid_error_validation(String testName, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- without '@' and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter.com", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- without '.' finish and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- too long suffix and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmailgmailgmailgmailgmailgmail" +
                "gmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmail.com", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- too long prefix1 and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenter@gmail.com", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- too long prefix2 and verify error message is displayed
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.comcom" +
                "comcomcomcomcomcomcom", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

        //Entering an invalid email- illegal characters
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter*@gmail.com", true);

        //Entering a valid email and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_email, R.id.input_layout_email, R.id.billingViewComponent, withImeButton, nextFieldResourceId, "broadwaydancecenter@gmail.com", false);

    }

    public static void zip_invalid_error_validation(String testName, int componentResourceId, boolean withImeButton, int nextFieldResourceId) {
        //fill in country with zip
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Israel");

        //Click the field and leave it empty and verify error message is displayed
        check_input_validation(testName, R.id.input_zip, R.id.input_layout_zip, componentResourceId, withImeButton, nextFieldResourceId, "", true);

        //enter a valid zip and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_zip, R.id.input_layout_zip, componentResourceId, withImeButton, nextFieldResourceId, "12345", false);

        //Entering an invalid zip- invalid characters and verify error message is displayed
        check_input_validation(testName, R.id.input_zip, R.id.input_layout_zip, componentResourceId, withImeButton, nextFieldResourceId, "12345*", true);

        //enter a valid zip and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_zip, R.id.input_layout_zip, componentResourceId, withImeButton, nextFieldResourceId, "12345 abcde", false);
    }

    public static void city_invalid_error_validation(String testName, int componentResourceId, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "", true);

        //enter a valid city and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "New York", false);

        //Entering an invalid city- less then 2 characters and verify error message is displayed
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "a", true);

        //enter a valid city and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "Tel Aviv", false);

        //Entering an invalid zip- spaces and verify error message is displayed
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "            ", true);

        //enter a valid city and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_city, R.id.input_layout_city, componentResourceId, withImeButton, nextFieldResourceId, "Tel Aviv", false);
    }

    public static void address_invalid_error_validation(String testName, int componentResourceId, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "a", true);
        //enter a valid address and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "Broadway 777", false);

        //Entering an invalid address- less then 2 characters and verify error message is displayed
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "a", true);

        //enter a valid address and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "Broadway", false);

        //Entering an invalid address- spaces and verify error message is displayed
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "         ", true);

        //enter a valid address and verify error message is not displayed anymore
        check_input_validation(testName, R.id.input_address, R.id.input_layout_address, componentResourceId, withImeButton, nextFieldResourceId, "Broadway", false);
    }

    public static void state_invalid_error(String testName, int componentResourceId, int buttonComponent) {
        //Choosing brazil (that has state)
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Brazil");

        //Try to pay without filling in state
        TestUtils.pressBuyNowButton(buttonComponent);

        //verify error message is displayed
        CreditCardVisibilityTesterCommon.check_contact_info_invalid_error_visibility(testName, R.id.input_layout_state, componentResourceId, "state", true);

        //filling in Rio de Janeiro
        ContactInfoTesterCommon.changeState(componentResourceId, "Rio de Janeiro");

        //verify error message is not displayed anymore
        CreditCardVisibilityTesterCommon.check_contact_info_invalid_error_visibility(testName, R.id.input_layout_state, componentResourceId, "state", false);
    }

    public static void contact_info_content_validation(String testName, Context context, int componentResourceId, String country, boolean fullInfo, boolean withEmail) {
        TestingShopperContactInfo contactInfo = (componentResourceId == R.id.billingViewComponent) ? billingContactInfo : shippingContactInfo;
        contact_info_content_validation(testName, context, componentResourceId, country, fullInfo, withEmail, contactInfo);
    }

    /**
     * This test verifies that the billing contact info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button summarized_contact_info_visibility_validation
     */
    public static void contact_info_content_validation(String testName, Context context, int componentResourceId, String country, boolean fullInfo, boolean withEmail,
                                                       TestingShopperContactInfo contactInfo) {
        Espresso.closeSoftKeyboard();

        //Verify country has been saved in current component
        if (!country.equals(""))
            CreditCardVisibilityTesterCommon.country_view_validation(testName, context, country, componentResourceId);
        //onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).check(matches(TestUtils.withDrawable(R.drawable.us)));

        //Verify full name has been saved in current component
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Full name wasn't saved"))
                .check(matches(withText(contactInfo.getName())));

        if (withEmail) //Verify email has been saved in billing component
            onView(withId(R.id.input_email))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Email wasn't saved"))
                    .check(matches(withText(contactInfo.getEmail())));

        if (TestUtils.checkCountryHasZip(country))
            //Verify zip has been saved in current component
            onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Zip wasn't saved"))
                    .check(matches(withText(contactInfo.getZip())));

        if (fullInfo) {
            //Verify city has been saved in current component
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": City wasn't saved"))
                    .check(matches(withText(contactInfo.getCity())));
            //Verify address has been saved in current component
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Address wasn't saved"))
                    .check(matches(withText(contactInfo.getAddress())));
            if (country.equals("US") || country.equals("CA") || country.equals("BR"))
                //Verify state has been saved in current component
                onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(componentResourceId))))
                        .withFailureHandler(new CustomFailureHandler(testName + ": State wasn't saved"))
                        .check(matches(withText(contactInfo.getState())));
        }
    }

    /**
     * If useDoneButton is true then it uses "Done" button and verifies the info changes,
     * o.w. it uses the "Back" button and verifies the info doesn't change.
     */
    public static void returning_shopper_edit_contact_info_validation(String testName, Context context, int summarizedComponentResourceId, boolean fullInfo,
                                                                      boolean withEmail, boolean useDoneButton, TestingShopperContactInfo verifyContactInfo) {
        int buttonComponent = (summarizedComponentResourceId == R.id.billingViewSummarizedComponent) ? R.id.returningShopperBillingFragmentButtonComponentView : R.id.returningShopperShippingFragmentButtonComponentView;
        int editableComponent = (summarizedComponentResourceId == R.id.billingViewSummarizedComponent) ? R.id.billingViewComponent : R.id.returningShoppershippingViewComponent;

        String countryKey;
        boolean shopperHasEmail, shopperHasFullBilling;
        //Enter to edit info
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(summarizedComponentResourceId)))).perform(click());

        if (useDoneButton) {
            verifyContactInfo = editBillingContactInfo;
            countryKey = "CA";
        } else
            countryKey = verifyContactInfo.getCountryKey();


        //TODO: move this outside
        shopperHasEmail = withEmail & (!verifyContactInfo.getEmail().isEmpty());
        shopperHasFullBilling = fullInfo & (!verifyContactInfo.getCity().isEmpty());


        //Edit the component info
        ContactInfoTesterCommon.changeCountry(editableComponent, "Canada");
        fillInContactInfo(editableComponent, "CA", fullInfo, withEmail, editBillingContactInfo);

        //go back and verify info has changed or was saved in the summarized component
        TestUtils.goBackToCreditCardInReturningShopper(useDoneButton, buttonComponent);
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation(testName, summarizedComponentResourceId, shopperHasFullBilling, shopperHasEmail, verifyContactInfo);

        //Press edit to verify info was saved in the edit component
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(summarizedComponentResourceId)))).perform(click());
        contact_info_content_validation(testName, context, editableComponent, countryKey, fullInfo, withEmail, verifyContactInfo);
    }

    public static void fillInContactInfo(int componentResourceId, String country, boolean fullInfo, boolean withEmail) {
        TestingShopperContactInfo contactInfo = (componentResourceId == R.id.billingViewComponent) ? billingContactInfo : shippingContactInfo;
        fillInContactInfo(componentResourceId, country, fullInfo, withEmail, contactInfo);
    }

    public static void fillInContactInfo(int componentResourceId, String country, boolean fullInfo, boolean withEmail, TestingShopperContactInfo contactInfo) {
//        if (changeCountry)
//            changeCountry(componentResourceId, country);
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(componentResourceId)))).perform(clearText(), typeText(contactInfo.getName()), pressImeActionButton());

        if (withEmail)
            onView(withId(R.id.input_email)).perform(clearText(), typeText(contactInfo.getEmail()), pressImeActionButton());

        if (TestUtils.checkCountryHasZip(country))
            onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(componentResourceId)))).perform(clearText(), typeText(contactInfo.getZip()), pressImeActionButton());

        if (fullInfo) {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId)))).perform(clearText(), typeText(contactInfo.getCity()), pressImeActionButton());
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId)))).perform(clearText(), typeText(contactInfo.getAddress()));

            String state = getDefaultStateByCountry(country);

            if (state != null) {
                changeState(componentResourceId, state);
            }
        }
    }

    private static void moveToNextField(int componentResourceId, boolean withImeButton, int nextFieldResourceId, int currFieldResourceId) {
        if (withImeButton)
            onView(allOf(withId(currFieldResourceId), isDescendantOfA(withId(componentResourceId)))).perform(pressImeActionButton());
        else {
            closeSoftKeyboard();
            onView(allOf(withId(nextFieldResourceId), isDescendantOfA(withId(componentResourceId)))).perform(scrollTo(), click());
        }
    }

    public static void changeCountry(int componentResourceId, String country) {
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(scrollTo(), click());
        onData(hasToString(containsString(country))).inAdapterView(withId(R.id.country_list_view)).perform(click());
    }

    public static void changeState(int componentResourceId, String state) {
        onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(componentResourceId)))).perform(scrollTo(), click());
        onData(hasToString(containsString(state))).inAdapterView(withId(R.id.state_list_view)).perform(click());
    }

    public static void check_input_validation(String testName, int fieldResourceId, int layoutResourceId, int componentResourceId, boolean withImeButton, int nextFieldResourceId, String input, boolean isInvalid) {
        onView(allOf(withId(fieldResourceId), isDescendantOfA(withId(componentResourceId)))).perform(scrollTo(), click(), clearText(), typeText(input));
        moveToNextField(componentResourceId, withImeButton, nextFieldResourceId, fieldResourceId);
        CreditCardVisibilityTesterCommon.check_contact_info_invalid_error_visibility(testName, layoutResourceId, componentResourceId, isInvalid);
    }

    @Nullable
    public static String getDefaultStateByCountry(String countryKey) {
        String state = null;

        if (countryKey.equals("US") || countryKey.equals("CA") || countryKey.equals("BR")) {
            if (countryKey.equals("US"))
                state = "New York";
            else if (countryKey.equals("CA"))
                state = "Quebec";
            else
                state = "Rio de Janeiro";
        }

        return state;
    }

}
