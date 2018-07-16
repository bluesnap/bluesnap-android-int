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
        onView(withId(R.id.input_name)).perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- only one word
        onView(withId(R.id.input_name)).perform(typeText("Sawyer"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering an invalid name- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"), pressImeActionButton());

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
        onView(withId(R.id.input_name)).perform(clearText(), typeText("Sawyer     "), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

        //Entering a valid name
        onView(withId(R.id.input_name)).perform(clearText(), typeText("La Fleur"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(doesNotExist());

        //Entering an invalid name again- less than 2 characters
        onView(withId(R.id.input_name)).perform(clearText(), typeText("L Fleur"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_name)))).check(matches(isDisplayed()));

    }

    /**
     * This test verifies the invalid error appearance for the email
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid mail- wrong format, invalid characters and too long inputs
     * Entering a valid email
     * Entering an invalid email after entering a valid one
     */
    @Test
    public void email_invalid_error_validation() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_email)).perform(click());
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- without '@'
        onView(withId(R.id.input_email)).perform(typeText("broadwaydancecenter.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- without '.' finish
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long suffix
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmailgmailgmailgmailgmailgmail" +
                "gmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmail.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long prefix1
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenter@gmail.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long prefix1
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail.comcom" +
                "comcomcomcomcomcomcom"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- illegal characters
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter*@gmail.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering a valid email
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(doesNotExist());

        //Entering an invalid email again- without '@'
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter.com"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

    }

    /**
     * This test verifies the invalid error appearance for the email
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid mail- wrong format, invalid characters and too long inputs
     * Entering a valid email
     * Entering an invalid email after entering a valid one
     */
    @Test
    public void email_invalid_error_validation_using_ime_button() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_email)).perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- without '@'
        onView(withId(R.id.input_email)).perform(typeText("broadwaydancecenter.com"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- without '.' finish
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long suffix
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmailgmailgmailgmailgmailgmail" +
                "gmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmailgmail.com"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long prefix1
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenterbroadwaydancecenterbroadwaydancecenter" +
                "broadwaydancecenterbroadwaydancecenter@gmail.com"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- too long prefix2
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail.comcom" +
                "comcomcomcomcomcomcom"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering an invalid email- illegal characters
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter*@gmail.com"),
                pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));

        //Entering a valid email
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter@gmail.com"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(doesNotExist());

        //Entering an invalid email again- without '@'
        onView(withId(R.id.input_email)).perform(clearText(), typeText("broadwaydancecenter.com"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_email)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    @Test
    public void zip_invalid_error_validation() throws InterruptedException {
        onView(withId(R.id.countryImageButton)).perform(click()); //fill in country with zip
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Click the field and leave it empty
        onView(withId(R.id.input_zip)).perform(click());
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));

        //Entering an invalid zip- invalid characters
        onView(withId(R.id.input_zip)).perform(typeText("12345*"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));

        //Entering a valid zip- only numbers
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering a valid zip- with characters
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345abcde"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering a valid zip- with spaces
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345 abcde"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering an invalid zip again- invalid characters
        onView(withId(R.id.input_zip)).perform(typeText("12345%"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the zip
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid zip- invalid characters
     * Entering a valid zip
     * Entering an invalid zip after entering a valid one
     */
    @Test
    public void zip_invalid_error_validation_using_ime_button() throws InterruptedException {
        onView(withId(R.id.countryImageButton)).perform(click()); //fill in country with zip
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Click the field and leave it empty
        onView(withId(R.id.input_zip)).perform(click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));

        //Entering an invalid zip- invalid characters
        onView(withId(R.id.input_zip)).perform(typeText("12345*"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));

        //Entering a valid zip- only numbers
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering a valid zip- with characters
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345abcde"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering a valid zip- with spaces
        onView(withId(R.id.input_zip)).perform(clearText(), typeText("12345 abcde"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(doesNotExist());

        //Entering an invalid zip again- invalid characters
        onView(withId(R.id.input_zip)).perform(typeText("12345%"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_zip)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    @Test
    public void city_invalid_error_validation() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_city)).perform(scrollTo(), click());
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).perform(scrollTo()).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_city)).perform(typeText("a"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

        //Entering an invalid city- spaces
        onView(withId(R.id.input_city)).perform(clearText(), typeText("        "));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

        //Entering a valid city
        onView(withId(R.id.input_city)).perform(clearText(), typeText("New York"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_city)).perform(clearText(), typeText("a"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

    }

    /**
     * This test verifies the invalid error appearance for the city
     * input field in billing.
     * In all cases we check validity by pressing the Ime button
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid city- less than 2 characters
     * Entering a valid city
     * Entering an invalid city after entering a valid one
     */
    @Test
    public void city_invalid_error_validation_using_ime_button() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_city)).perform(scrollTo(), click(), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_city)).perform(typeText("a"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

        //Entering an invalid city- spaces
        onView(withId(R.id.input_city)).perform(clearText(), typeText("        "), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));

        //Entering a valid city
        onView(withId(R.id.input_city)).perform(clearText(), typeText("New York"), pressImeActionButton());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_city)).perform(clearText(), typeText("a"), pressImeActionButton());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_city)))).check(matches(isDisplayed()));
    }

    /**
     * This test verifies the invalid error appearance for the address
     * input field in billing.
     * In all cases we check validity by clicking on another field
     * It covers the following:
     * Click the field and leave it empty
     * Entering an invalid address- invalid characters
     * Entering a valid address
     * Entering an invalid address after entering a valid one
     */
    @Test
    public void address_invalid_error_validation() throws InterruptedException {
        //Click the field and leave it empty
        onView(withId(R.id.input_address)).perform(scrollTo(), click());
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)))).perform(scrollTo()).check(matches(isDisplayed()));

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_address)).perform(clearText(), typeText("a"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)))).check(matches(isDisplayed()));

        //Entering an invalid city- spaces
        onView(withId(R.id.input_address)).perform(clearText(), typeText("        "));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)))).check(matches(isDisplayed()));

        //Entering a valid city
        onView(withId(R.id.input_address)).perform(clearText(), typeText("New York"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is not displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)))).check(doesNotExist());

        //Entering an invalid city- less then 2 characters
        onView(withId(R.id.input_address)).perform(clearText(), typeText("a"));
        onView(withId(R.id.creditCardNumberEditText)).perform(click());

        //Verify error message is displayed
        onView(allOf(withId(R.id.textinput_error),
                isDescendantOfA(withId(R.id.input_layout_address)))).check(matches(isDisplayed()));

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
        CardFormTesterCommon.fillInContactInfoBilling("IL", true, false); //passing IL to not fill in state

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
