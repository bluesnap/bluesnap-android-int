package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.androidapi.services.AndroidUtil;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 04/06/2018.
 */
public class CreditCardVisibilityTesterCommon {
    public static void cc_line_fields_visibility_validation(String testName) {
        onView(ViewMatchers.withId(R.id.oneLineCCEditComponent))
                .withFailureHandler(new CustomFailureHandler(testName + ": One line credit card is not displayed")).check(matches(isDisplayed()));
        onView(withId(R.id.creditCardNumberEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Credit card editText is not displayed")).check(matches(isDisplayed()));
        onView(withId(R.id.expEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Exp date editText is displayed")).check(matches(not(isDisplayed())));
        onView(withId(R.id.cvvEditText))
                .withFailureHandler(new CustomFailureHandler(testName + ": Cvv number editText is displayed")).check(matches(not(isDisplayed())));
    }

    public static void cc_line_error_messages_not_displayed_validation(String testName) {
        check_cc_info_invalid_error_visibility(testName, R.id.creditCardNumberErrorTextView, false);
        check_cc_info_invalid_error_visibility(testName, R.id.expErrorTextView, false);
        check_cc_info_invalid_error_visibility(testName, R.id.cvvErrorTextView, false);
    }

    public static void contact_info_visibility_validation(String testName, int componentResourceId, boolean fullInfo, boolean withEmail) {
        //verify that the right component(billing/shipping) is displayed- is this necessary?
        onView(withId(componentResourceId)).check(matches(isDisplayed()));

        Espresso.closeSoftKeyboard();
        //verify that all right fields are displayed in the component
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input name editText is not displayed"))
                .check(matches(isDisplayed()));
        if (withEmail)
            onView(withId(R.id.input_email))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input email editText is not displayed"))
                    .check(matches(isDisplayed()));
        else if (componentResourceId == R.id.billingViewComponent)
            onView(withId(R.id.input_email))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input email editText is displayed"))
                    .check(matches(not(isDisplayed())));

        if (fullInfo) {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input city editText is not displayed"))
                    .check(matches(isDisplayed()));
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input address editText is not displayed")).
                    check(matches(isDisplayed()));
        } else {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input city editText is displayed"))
                    .check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input address editText is displayed"))
                    .check(matches(not(isDisplayed())));
        }

    }

    public static void contact_info_error_messages_validation(String testName, int componentResourceId, String country, boolean fullInfo, boolean withEmail) {
        Espresso.closeSoftKeyboard();
        //verify that all error messages are not displayed in the component
        check_contact_info_invalid_error_visibility(testName, R.id.input_layout_name, componentResourceId, "name", false);
        if (withEmail)
            check_contact_info_invalid_error_visibility(testName, R.id.input_layout_email, componentResourceId, "email", false);

        if (TestUtils.checkCountryHasZip(country)) //Country with zip
            check_contact_info_invalid_error_visibility(testName, R.id.input_layout_zip, componentResourceId, "zip", false);

        if (fullInfo) {
            if (country.equals("US") || country.equals("CA") || country.equals("BR"))  //Country is one of US CA BR- has state
                check_contact_info_invalid_error_visibility(testName, R.id.input_layout_state, componentResourceId, "state", false);

            check_contact_info_invalid_error_visibility(testName, R.id.input_layout_city, componentResourceId, "city", false);
            check_contact_info_invalid_error_visibility(testName, R.id.input_layout_address, componentResourceId, "address", false);
        }
    }

    /**
     * This test verifies that the country image matches the parameter country
     */
    public static void country_view_validation(String testName, Context context, String country, int componentResourceId) {
        //get the expected drawable id
        Integer resourceId = context.getResources().getIdentifier(country.toLowerCase(), "drawable", context.getPackageName());

        //check image is as expected
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country image button doesn't display the correct image"))
                .check(matches(TestUtils.withDrawable(resourceId)));
    }

    /**
     * This test verifies that the country image changes as expected, according
     * to different choices in billing or shipping info.
     */
    public static void changing_country_view_validation(String testName, int componentResourceId) {
        //Test validation of country image- changing to Canada
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Canada");
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country image button doesn't display the correct image"))
                .check(matches(TestUtils.withDrawable(R.drawable.ca)));

        //Test validation of country image- changing to Argentina
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Argentina");
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country image button doesn't display the correct image"))
                .check(matches(TestUtils.withDrawable(R.drawable.ar)));
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing and shipping).
     */
    public static void default_country_zip_view_validation(String testName, String defaultCountry, int componentResourceId) {
        //Test validation of zip appearance according to the country
        if (TestUtils.checkCountryHasZip(defaultCountry)) //Country with zip
            onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is not displayed"))
                    .check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed
        else //Country without zip
            onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is displayed"))
                    .check(matches(not(ViewMatchers.isDisplayed()))); //Check that the zip view is not displayed
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to different choices of countries in billing or shipping info.
     */
    public static void changing_country_zip_view_validation(String testName, int componentResourceId) {
        //Test validation of zip appearance. changing to USA
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is not displayed"))
                .check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed

        //changing to Angola (without zip)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Angola"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is displayed"))
                .check(matches(not(ViewMatchers.isDisplayed()))); //Check that the zip view is not displayed

        //Test validation of zip appearance. changing to Israel
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input zip editText is not displayed"))
                .check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing and shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public static void default_country_state_view_validation(String testName, int componentResourceId, String country) {
        //Test validation of state appearance
        if (country.equals("US") || country.equals("CA") || country.equals("BR"))  //Country is one of US CA BR- has state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is not displayed"))
                    .check(matches(ViewMatchers.isDisplayed())); //Check that the state view is displayed
        else  //Country is not one of US CA BR- doesn't have state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is displayed"))
                    .check(matches(not(ViewMatchers.isDisplayed()))); //Check that the state view is not displayed
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in billing or shipping info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public static void changing_country_state_view_validation(String testName, int componentResourceId) {
        //Test validation of state appearance. changing to USA
        ContactInfoTesterCommon.changeCountry(componentResourceId, "United States");
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Italy");
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is displayed"))
                .check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Canada");
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Spain");
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is displayed"))
                .check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        ContactInfoTesterCommon.changeCountry(componentResourceId, "Brazil");
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Input state editText is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * This test verifies that changing the country in one fragment (billing/shipping contact
     * info) doesn't change the country in the other.
     */
    public static void country_changes_per_fragment_validation(String testName) {
        //Changing country to Spain in billing fragment
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "Spain");

        //continue to shipping
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView)))).perform(click());

        //Verify country hasn't change in shipping fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country changed in shipping"))
                .check(matches(not(TestUtils.withDrawable(R.drawable.es))));

        //Changing Country to Italy in shipping fragment
        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "Italy");

        //go back to billing
        TestUtils.goBackToBillingInNewCard();

        //Verify country hasn't change in billing fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.billingViewComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country changed in billing"))
                .check(matches(TestUtils.withDrawable(R.drawable.es)));
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public static void pay_button_visibility_and_content_validation(String testName, int buttonComponent, String checkoutCurrency, double purchaseAmount, double taxAmount) {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button is not visible"))
                .check(matches(ViewMatchers.isDisplayed()));

        String buttonContent = TestUtils.getText(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))));
        double totalAmount = purchaseAmount + taxAmount;

        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button doesn't display the correct content!" +
                        " expected amount: " + TestUtils.getDecimalFormat().format(totalAmount) + ", expected currency: " + checkoutCurrency + ", actual content: " + buttonContent))
                .check(matches(withText(TestUtils.getStringFormatAmount("Pay",
                        AndroidUtil.getCurrencySymbol(checkoutCurrency), totalAmount))));
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public static void shipping_button_visibility_and_content_validation(String testName, int buttonComponent) {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Shipping button is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Shipping button does not display the correct content"))
                .check(matches(withText("Shipping")));
    }

    /**
     * This test verifies that the amount tax shipping component is visible
     */
    public static void amount_tax_shipping_view_validation(String testName, int amountTaxShippingComponent, String currency, String amount, String tax) {
        //verify component is visible
        onView(withId(amountTaxShippingComponent))
                .withFailureHandler(new CustomFailureHandler(testName + ": Amount-tax component is not displayed in a country with tax"))
                .check(matches(ViewMatchers.isDisplayed()));

        //verify amount and tax is visible
        onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(amountTaxShippingComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Amount-tax layout is not displayed in a country with tax"))
                .check(matches(ViewMatchers.isDisplayed()));

        //verify that the presented amount and tax are correct
        onView(allOf(withId(R.id.amountTextView), isDescendantOfA(withId(amountTaxShippingComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Amount displayed is not correct"))
                .check(matches(withText(AndroidUtil.getCurrencySymbol(currency) + " " + amount)));
//                .check(matches(withText(containsString(amount))));
        onView(allOf(withId(R.id.taxTextView), isDescendantOfA(withId(amountTaxShippingComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Tax displayed is not correct"))
                .check(matches(withText(AndroidUtil.getCurrencySymbol(currency) + " " + tax)));
    }

    public static void check_contact_info_invalid_error_visibility(String testName, int layoutResourceId, int componentResourceId, boolean isDisplayed) {
        check_contact_info_invalid_error_visibility(testName, layoutResourceId, componentResourceId, "", isDisplayed);
    }

    public static void check_contact_info_invalid_error_visibility(String testName, int layoutResourceId, int componentResourceId, String fieldName, boolean isDisplayed) {
        if (isDisplayed) //Verify error message is displayed
            onView(allOf(withId(R.id.textinput_error),
                    isDescendantOfA(withId(layoutResourceId)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Invalid error message is not displayed for " + fieldName))
                    .perform(scrollTo()).check(matches(isDisplayed()));

        else //Verify error message is not displayed
            onView(allOf(withId(R.id.textinput_error),
                    isDescendantOfA(withId(layoutResourceId)),
                    isDescendantOfA(withId(componentResourceId))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Invalid error message is displayed"))
                    .check(doesNotExist());
    }

    public static void check_cc_info_invalid_error_visibility(String testName, int errorFieldResourceId, boolean isDisplayed) {
        if (isDisplayed) //Verify error message is displayed
            onView(withId(errorFieldResourceId))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Invalid error message is not displayed"))
                    .check(matches(ViewMatchers.isDisplayed()));


        else //Verify error message is not displayed
            onView(withId(errorFieldResourceId))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Invalid error message is displayed"))
                    .check(matches(not(ViewMatchers.isDisplayed())));

    }

}
