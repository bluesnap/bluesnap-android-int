package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

/**
 * Helper class for UI tests, handles the New CC form.
 *
 * Created by oz on 5/30/16.
 */
public class CreditCardLineTesterCommon {
    public static void check_ime_action_button_in_cc_info(String testName) {
        //verify that "next" action moves focus to name input field (since exp date and cvv are hidden at the moment)
        onView(withId(R.id.creditCardNumberEditText)).perform(click(), pressImeActionButton());
        check_field_focused(testName, R.id.input_name, "name");

        onView(withId(R.id.creditCardNumberEditText)).perform(click(), clearText(), typeText(cardNumberGeneratorTest()));

        //verify that "next" action moves focus to exp input field
        onView(withId(R.id.creditCardNumberEditText)).perform(click(), pressImeActionButton());
        check_field_focused(testName, R.id.expEditText, "exp date");

        //verify that "next" action moves focus to cvv input field
        onView(withId(R.id.expEditText)).perform(pressImeActionButton());
        check_field_focused(testName, R.id.cvvEditText, "cvv");

        //verify that "next" action moves focus to cvv input field
        onView(withId(R.id.cvvEditText)).perform(pressImeActionButton());
        check_field_focused(testName, R.id.input_name, "name");
    }

    public static void cc_empty_fields_invalid_error_validation(String testName) {
        //Continue- leaving all fields empty
        TestUtils.pressBuyNowButton();

        //verify error messages are displayed
        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.creditCardNumberErrorTextView, true);
//        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.expErrorTextView, true);
//        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.cvvErrorTextView, true);
    }

    public static void check_filling_in_cc_info_flow(String testName) {
        onView(withId(R.id.creditCardNumberEditText)).perform(clearText(), typeText(cardNumberGeneratorTest()));
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Exp date editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));

        onView(withId(R.id.expEditText)).perform(typeText("12 26"));
        onView(withId(R.id.cvvEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Cvv number editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));

        onView(withId(R.id.cvvEditText)).perform(typeText("123"));
    }

    public static void check_focus_from_cvv_text_view_in_cc_line(String testName) {
        onView(withId(R.id.creditCardNumberEditText)).perform(typeText(cardNumberGeneratorTest()));
        onView(withId(R.id.expEditText)).perform(typeText("12 26"));

        //now cvv is focused
        //verify focused is changed to exp date editText when clicking on it before filling in cvv number
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Exp date number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));

        //verify focused is changed to input name editText when clicking on it before filling in cvv number
        onView(withId(R.id.cvvEditText)).perform(click());
        onView(withId(R.id.input_name))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));

        //verify focused is changed to credit card number editText when clicking on it before filling in cvv number
        onView(withId(R.id.cvvEditText)).perform(click());
        onView(withId(R.id.creditCardNumberEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Credit card number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));

        //open cc line (it shrank since the focus is on credit card number)
        onView(withId(R.id.moveToCcImageButton)).perform(click());

        //verify focused is changed to exp date editText when clicking on it after filling in cvv number
        onView(withId(R.id.cvvEditText)).perform(typeText("123"));
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Exp date number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));

        //verify focused is changed to nampe editText when clicking on it after filling in cvv number
        onView(withId(R.id.cvvEditText)).perform(click());
        onView(withId(R.id.input_name))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));

        //verify focused is changed to credit card number editText when clicking on it after filling in cvv number
        onView(withId(R.id.cvvEditText)).perform(click());
        onView(withId(R.id.creditCardNumberEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": credit card number editText is not focused, after clicking on it"))
                .perform(click()).check(matches(TestUtils.isViewFocused()));
    }

    /**
     * This test verifies that the credit card line info is saved when
     * continuing to shipping and going back to billing,
     * while using the back button.r
     */
    public static void credit_card_info_saved_validation(String testName, String creditCardNum, String expDate, String cvvNum) {
        //Verify cc number has been saved
        onView(withId(R.id.creditCardNumberEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Credit Card number wasn't saved"))
                .check(matches(withText(containsString(creditCardNum))));

        //Verify exp date has been saved
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Expiration date wasn't saved"))
                .check(matches(withText(expDate)));

        //Verify cvv number has been saved
        onView(withId(R.id.cvvEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Cvv number wasn't saved"))
                .check(matches(withText(cvvNum)));
    }

    public static void invalid_cc_number_with_valid_exp_and_cvv_validation(String testName) {
        //fill in cc line info
        fillInCCLineWithValidCard(TestingShopperCreditCard.VISA_CREDIT_CARD);

        //change credit card number to an invalid one
        onView(withId(R.id.creditCardNumberEditText))
                .perform(click(), clearText(), typeText(invalidCardNumberGeneratorTest()));

        TestUtils.pressBuyNowButton();

        //verify exp and cvv error messages are displayed
        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.creditCardNumberErrorTextView, true);
    }

    public static void hidden_invalid_exp_and_cvv(String testName) {
        //fill in valid cc number
        onView(withId(R.id.creditCardNumberEditText))
                .perform(click(), clearText(), typeText(TestingShopperCreditCard.MASTERCARD_CREDIT_CARD.getCardNumber()));

        //fill in invalid exp date
        onView(withId(R.id.expEditText)).perform(click(), clearText(), typeText("10 17"));

        //fill in invalid cvv number
        onView(withId(R.id.cvvEditText)).perform(click(), clearText(), typeText("1234"));

        //press cc number editText so that exp and cvv will be hidden
        onView(withId(R.id.creditCardNumberEditText))
                .perform(click());

        TestUtils.pressBuyNowButton();

        //verify exp and cvv error messages are displayed
        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.expErrorTextView, true);
        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, R.id.cvvErrorTextView, true);
    }

    public static void fillInCCLineWithValidCard() {
        fillInCCLineWithValidCard(TestingShopperCreditCard.MASTERCARD_CREDIT_CARD);
    }

    public static void fillInCCLineWithValidCard(TestingShopperCreditCard creditCard) {
        onView(withId(R.id.creditCardNumberEditText))
                .perform(click(), clearText(), typeText(creditCard.getCardNumber()));

        onView(withId(R.id.expEditText)).perform(clearText(), typeText(Integer.toString(creditCard.getExpirationMonth()) + " " + creditCard.getExpirationYearLastTwoDigit()));

        onView(withId(R.id.cvvEditText)).perform(clearText(), typeText(creditCard.getCvv()));
    }

    public static void cc_number_invalid_error_validation(String testName, boolean withImeButton, int nextFieldResourceId) {
        //Entering an invalid cc number- not lan, and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.creditCardNumberEditText, R.id.creditCardNumberErrorTextView, false, withImeButton, nextFieldResourceId, invalidCardNumberGeneratorTest(), true);

        //enter a valid cc number and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.creditCardNumberEditText, R.id.creditCardNumberErrorTextView, false, withImeButton, nextFieldResourceId, cardNumberGeneratorTest(), false);

        //Entering an invalid cc number- too few digits and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.creditCardNumberEditText, R.id.creditCardNumberErrorTextView, true, withImeButton, nextFieldResourceId, "557275888601", true);

//        //enter a valid cc number and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.creditCardNumberEditText, R.id.creditCardNumberErrorTextView, false, withImeButton, nextFieldResourceId, cardNumberGeneratorTest(), false);
    }

    //Pre-condition: exp date field is displayed
    public static void exp_date_invalid_error_validation(String testName, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, true, withImeButton, nextFieldResourceId, "", true);

        //enter a valid exp date and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "10 25", false);

        //Entering an invalid exp date- invalid month, and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "20 20", true);

        //enter a valid exp date and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "10 25", false);

        //Entering an invalid exp date- past date, and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "10 17", true);

        //enter a valid exp date and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "10 25", false);

        //Entering an invalid exp date- only month, and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, true, withImeButton, nextFieldResourceId, "12", true);

        //enter a valid exp date and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.expEditText, R.id.expErrorTextView, false, withImeButton, nextFieldResourceId, "12 26", false);

    }

    //Pre-condition: cvv field is displayed
    public static void cvv_number_invalid_error_validation(String testName, boolean withImeButton, int nextFieldResourceId) {
        //Click the field and leave it empty and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "", true);

        //enter a valid cvv number and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "123", false);

        //Entering an invalid cvv number- too few digits, and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "56", true);

        //enter a valid cvv number and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "123", false);

        //Entering an invalid cc number- more than 3 digits , and verify error message is displayed
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "1234", true);

        //enter a valid cvv number and verify error message is not displayed anymore
        check_cc_line_input_validation(testName, R.id.cvvEditText, R.id.cvvErrorTextView, true, withImeButton, nextFieldResourceId, "123", false);
    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "5572758881122333";
    }

    private static void check_cc_line_input_validation(String testName, int fieldResourceId, int errorFieldResourceId, boolean moveToNextField,
                                                       boolean withImeButton, int nextFieldResourceId, String input, boolean isInvalid) {
        onView(withId(fieldResourceId)).perform(click(), clearText(), typeText(input));
        if (moveToNextField)
            moveToNextField(withImeButton, nextFieldResourceId, fieldResourceId);
        CreditCardVisibilityTesterCommon.check_cc_info_invalid_error_visibility(testName, errorFieldResourceId, isInvalid);
    }

    private static void moveToNextField(boolean withImeButton, int nextFieldResourceId, int currFieldResourceId) {
        if (withImeButton)
            onView(withId(currFieldResourceId)).perform(pressImeActionButton());
        else
            onView(withId(nextFieldResourceId)).perform(click());
    }

    private static void check_field_focused(String testName, int fieldResourceId, String fieldName) {
        onView(withId(fieldResourceId))
                .withFailureHandler(new CustomFailureHandler(testName + ": " + fieldName + " editText is not focused, after pressing the ime button"))
                .check(matches(TestUtils.isViewFocused()));
    }

}
