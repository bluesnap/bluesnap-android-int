package com.bluesnap.android.demoapp;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 05/07/2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class BillingContactInfoInvalidErrorsTests extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

    }


    /**
     * This test verifies that an invalid error appears for every
     * field when leaving it empty (without entering at all)
     */
    @Test
    public void empty_fields_invalid_error_validation() throws InterruptedException {
        //String defaultCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());

        //Choosing brazil (that has state and zip)
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Continue to Shipping- leaving all fields empty
        onView(withId(R.id.buyNowButton)).perform(click());

        //verify error messages are displayed
        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_name))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_email))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_zip))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_state))))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_city))))
                .check(matches(isDisplayed()));

        //onView(withId(R.id.input_address)).perform(scrollTo());

        onView(allOf(withId(R.id.textinput_error), isDescendantOfA(withId(R.id.input_layout_address))))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

//        onView(withId(R.id.buyNowButton)).perform(click());

    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    @Test
    public void name_invalid_error_validation() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_name)).perform(click());
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- only one word
        onView(withId(R.id.input_name)).perform(typeText("Sawyer"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters. BUG! waiting for it to be fixed
//        onView(withId(R.id.input_name)).perform(clearText(), typeText("La F"));
//        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- spaces
        onView(withId(R.id.input_name)).perform(clearText(), typeText("Sawyer     "));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering a valid name
        onView(withId(R.id.input_name)).perform(clearText(), typeText("La Fleur"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(doesNotExist());

        //Entering an invalid name again- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

    }

    /**
     * This test verifies the invalid error appearance for the name
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid name- less than 2 words or less than 2 characters
     * Entering a valid name
     * Entering an invalid name after entering a valid one
     */
    @Test
    public void name_invalid_error_validation_using_ime_button() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_name)).perform(click());
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- only one word
        onView(withId(R.id.input_name)).perform(typeText("Sawyer"));
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"));
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters. BUG! waiting for it to be fixed
//        onView(withId(R.id.input_name)).perform(clearText(), typeText("La F"));
//        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
//        onView(allOf(withId(R.id.textinput_error),
//                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- spaces
        onView(withId(R.id.input_name)).perform(clearText(), typeText("Sawyer     "));
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering a valid name
        onView(withId(R.id.input_name)).perform(clearText(), typeText("La Fleur"));
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(doesNotExist());

        //Entering an invalid name again- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"));
        onView(withId(R.id.input_name)).perform(pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

    }

    //Waiting for this bug to be fixed

    /**
     * This test verifies that the invalid state error disappears
     * after entering a state.
     */
    @Test
    public void state_invalid_error() throws InterruptedException {
        //Choosing brazil (that has state)
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfo("IL", true, false); //passing IL to not fill in state

        Espresso.closeSoftKeyboard();
        //Continue to Shipping without filling in state
        onView(withId(R.id.buyNowButton)).perform(click());

        //Espresso.closeSoftKeyboard();
        //verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_state)))).check(matches(isDisplayed()));

        //filling in Rio de Janeiro
        onView(withId(R.id.input_state)).perform(click());
        onData(hasToString(containsString("Rio de Janeiro"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
        onView(withId(R.id.input_name)).perform(click());
        Espresso.closeSoftKeyboard();

        //waiting for this bug to be fixed
//        //verify error message is not displayed anymore
//        onView(allOf(withId(R.id.textinput_error),
//                isDescendantOfA(withId(R.id.input_layout_state)))).check(matches(not(isDisplayed())));
    }

}
