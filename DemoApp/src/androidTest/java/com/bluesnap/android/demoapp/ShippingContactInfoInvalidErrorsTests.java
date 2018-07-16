package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 05/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ShippingContactInfoInvalidErrorsTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

    }

    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    @Test
    public void empty_fields_invalid_error_validation() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Choosing brazil (that has state and zip)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Continue to Shipping- leaving all fields empty
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());

        //verify error messages are displayed
        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_state)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //onView(withId(R.id.input_address)).perform(scrollTo());

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    @Test
    public void name_invalid_error_validation() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- only one word
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(typeText("Sawyer"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("L Fleur"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters. BUG! waiting for it to be fixed
//        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("La F"));
//        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
//        onView(allOf(withId(R.id.textinput_error),
//                isDescendantOfA(withId(R.id.input_layout_name)),
//                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- spaces
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("Sawyer     "));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid name
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("La Fleur"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid name again- less than 2 characters
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(clearText(), typeText("L Fleur"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    @Test
    public void name_invalid_error_validation_using_ime_button() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default- don't need state

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- only one word
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("Sawyer"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("L Fleur"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters. BUG! waiting for it to be fixed
//        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
//              .perform(clearText(), typeText("La F"), pressImeActionButton());

        //Verify error message is displayed
//        onView(allOf(withId(R.id.textinput_error),
//                isDescendantOfA(withId(R.id.input_layout_name)),
//                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid name- spaces
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("Sawyer     "), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid name
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("La Fleur"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid name again- less than 2 characters
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("L Fleur"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    @Test
    public void zip_invalid_error_validation() throws InterruptedException {
        //ViewInteraction shippingZipVI = onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //fill in country with zip
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid zip- invalid characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("12345*"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid zip- only numbers
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering a valid zip- with characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345abcde"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering a valid zip- with spaces
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345 abcde"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid zip again- invalid characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("12345%"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    @Test
    public void zip_invalid_error_validation_using_ime_button() throws InterruptedException {
        //ViewInteraction shippingZipVI = onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //fill in country with zip
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid zip- invalid characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("12345*"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid zip- only numbers
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering a valid zip- with characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345abcde"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering a valid zip- with spaces
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("12345 abcde"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid zip again- invalid characters
        onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("12345%"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    @Test
    public void city_invalid_error_validation() throws InterruptedException {
        //ViewInteraction shippingZipVI = onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("a"));
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid zip- spaces
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("        "));
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid zip- with characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("New York"));
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("a"));
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in shipping.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    @Test
    public void city_invalid_error_validation_using_ime_button() throws InterruptedException {
        //ViewInteraction shippingZipVI = onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("a"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering an invalid zip- spaces
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("        "), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));

        //Entering a valid zip- with characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("New York"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("a"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the address
     * input field in shipping.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    @Test
    public void address_invalid_error_validation() throws InterruptedException {
        //ViewInteraction shippingZipVI = onView(allOf(withId(R.id.input_zip), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Click the field and leave it empty
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(scrollTo()).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(typeText("a"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(scrollTo()).check(matches(isDisplayed()));

        //Entering an invalid zip- spaces
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("        "));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(scrollTo()).check(matches(isDisplayed()));

        //Entering a valid zip- with characters
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("New York"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(clearText(), typeText("a"));
        onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)),
                isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .perform(scrollTo()).check(matches(isDisplayed()));
    }


    //Waiting for this bug to be fixed

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    @Test
    public void state_invalid_error() throws InterruptedException {
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("IL", false, false); //passing IL as default

        Espresso.closeSoftKeyboard();
        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Choosing brazil (that has state)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Try to pay without filling in state
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());

        //Espresso.closeSoftKeyboard();
        //verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_state)))).check(matches(isDisplayed()));

        //filling in Rio de Janeiro
        onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Rio de Janeiro"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        Espresso.closeSoftKeyboard();

        //waiting for this bug to be fixed
//        //verify error message is not displayed anymore
//        onView(allOf(withId(R.id.textinput_error),
//                isDescendantOfA(withId(R.id.input_layout_state)))).check(matches(not(isDisplayed())));
    }


}
