package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.AndroidUtil;

import javax.annotation.Nullable;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 04/06/2018.
 */
public class CreditCardVisibilityTesterCommon {

    public static void check_payment_methods_visibility(String testName, boolean isPayPal, boolean isGooglePay) {
        onView(withId(R.id.newCardButton))
                .withFailureHandler(new CustomFailureHandler(testName + ": New credit card button is not displayed")).check(matches(isDisplayed()));

        if (isPayPal)
            onView(withId(R.id.payPalButton))
                    .withFailureHandler(new CustomFailureHandler(testName + ": PayPal button is not displayed")).check(matches(isDisplayed()));
        else
            onView(withId(R.id.payPalButton))
                    .withFailureHandler(new CustomFailureHandler(testName + ": PayPal button is displayed")).check(matches(not(isDisplayed())));

        if (isGooglePay)
            onView(withId(R.id.googlePayButton))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Google Pay button is not displayed")).check(matches(isDisplayed()));
        else
            onView(withId(R.id.googlePayButton))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Google Pay button is displayed")).check(matches(not(isDisplayed())));

    }

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
        TestUtils.pressBuyNowButton();

        //Verify country hasn't change in shipping fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country changed in shipping"))
                .check(matches(not(TestUtils.withDrawable(R.drawable.es))));

        //Changing Country to Italy in shipping fragment
        ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, "Italy");

        //go back to billing
        TestUtils.goBack();

        //Verify country hasn't change in billing fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.billingViewComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Country changed in billing"))
                .check(matches(TestUtils.withDrawable(R.drawable.es)));
    }

    // for regular checkout
    public static void pay_button_visibility_and_content_validation(String testName, int buttonComponent, String checkoutCurrency, double purchaseAmount, double taxAmount) {
        pay_button_visibility_and_content_validation(testName, buttonComponent, checkoutCurrency, purchaseAmount, taxAmount, false, false);
    }

    // for subscription without price details
    public static void pay_button_visibility_and_content_validation(String testName, int buttonComponent) {
        pay_button_visibility_and_content_validation(testName, buttonComponent, "", 0.0, 0.0, true, false);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public static void pay_button_visibility_and_content_validation(String testName, int buttonComponent, String checkoutCurrency, double purchaseAmount, double taxAmount,
                                                                    boolean subscriptionMode, boolean subscriptionHasPriceDetails) {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button is not visible"))
                .check(matches(ViewMatchers.isDisplayed()));

        String buttonContent = TestUtils.getText(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))));
        double totalAmount = purchaseAmount + taxAmount;

        if (subscriptionMode && !subscriptionHasPriceDetails) {
            onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Shipping button does not display the correct content"))
                    .check(matches(withText("Subscribe")));
        } else {
            String payText = subscriptionMode ? "Subscribe" : "Pay";

            onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Buy now button doesn't display the correct content!" +
                            " expected amount: " + TestUtils.getDecimalFormat().format(totalAmount) + ", expected currency: " + checkoutCurrency + ", actual content: " + buttonContent))
                    .check(matches(withText(TestUtils.getStringFormatAmount(payText,
                            AndroidUtil.getCurrencySymbol(checkoutCurrency), totalAmount))));
        }
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public static void shipping_button_visibility_and_content_validation(String testName) {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView))))
                .withFailureHandler(new CustomFailureHandler(testName + ": Shipping button is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.billingButtonComponentView))))
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
//                    .perform(scrollTo())
                    .check(matches(isDisplayed()));

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

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility, switch state (on/off) and validation (if mandatory)
     * Pre-Conditions:
     */
    public static void check_store_card_visibility(String testName, boolean shouldBeVisible) {

        check_store_card_visibility(testName, shouldBeVisible, false, true);
    }

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility, switch state (on/off) and validation (if mandatory)
     */
    public static void check_store_card_visibility(String testName, boolean shouldBeVisible, boolean shouldBeOn, boolean isValid) {

        // check visibility
        check_store_card_layout_visibility(testName, shouldBeVisible);

        // check switch state
        check_store_card_switch_state(testName, shouldBeOn);

        if (!isValid) {
            // maybe one day we'll check check_store_card_mandatory here (that the color is red and field is considered invalid)
        }
    }

    private static void check_store_card_layout_visibility(String testName, boolean shouldBeVisible) {

        if (shouldBeVisible) {
            //verify store card layout is visible
            onView(withId(R.id.storeCardRelativeLayout))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card layout is not visible"))
                    .check(matches(isDisplayed()));

            //verify store card switch is visible
            onView(withId(R.id.storeCardSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card switch is not visible"))
                    .check(matches(isDisplayed()));
        } else {
            //verify store card layout is not visible
            onView(withId(R.id.storeCardRelativeLayout))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card layout is visible"))
                    .check(matches(not(ViewMatchers.isDisplayed())));

            //verify store card switch is not visible
            onView(withId(R.id.storeCardSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card switch is visible"))
                    .check(matches(not(ViewMatchers.isDisplayed())));
        }

    }

    private static void check_store_card_switch_state(String testName, boolean shouldBeOn) {

        if (shouldBeOn) {
            //verify store card layout is checked
            onView(withId(R.id.storeCardSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card switch is not checked"))
                    .check(matches(isChecked()));
        } else {
            //verify store card layout is not checked
            onView(withId(R.id.storeCardSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Store card switch is checked"))
                    .check(matches(not(isChecked())));
        }

    }

    /**
     * This test verifies the validation of store card switch (if mandatory)
     * when trying to pay with the swith off, and store card is mandatory
     * this is the only way to check validation for now, due to UI XCTest limitations
     */
    public static void check_store_card_mandatory(String testName) { //TODO: see if we can check text color for this validation
        TestUtils.pressBuyNowButton();
        check_store_card_visibility(testName, true);
    }

    public static void check_store_card_visibility_after_changing_activities(boolean shouldBeVisible, boolean setTo, TestingShopperCheckoutRequirements shopperCheckoutRequirements,
                                                                             String country, @Nullable String state, String currencyCode) {
        check_store_card_visibility_after_changing_activities(shouldBeVisible, setTo, shopperCheckoutRequirements, country, state, currencyCode, true);
    }

    /**
     * This test verifies the visibility of store card switch after changing to other screens and returning to payment screen.
     * It covers visibility and swith state (on/off)
     */
    public static void check_store_card_visibility_after_changing_activities(boolean shouldBeVisible, boolean setTo, TestingShopperCheckoutRequirements shopperCheckoutRequirements,
                                                                             String country, @Nullable String state, String currencyCode, boolean allowCurrencyChange) {

        // set store card switch to the desired mode
        if (setTo)
            TestUtils.setStoreCardSwitch(setTo);

        // check store card after changing country
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, country);
        check_store_card_visibility("check_store_card_visibility_after_changing_activities", shouldBeVisible, setTo, true);

        if (shopperCheckoutRequirements.isFullBillingRequired() && state != null) {
            // check store card after changing state
            ContactInfoTesterCommon.changeState(R.id.billingViewComponent, state);
            check_store_card_visibility("check_store_card_visibility_after_changing_activities", shouldBeVisible, setTo, true);
        }

        if (allowCurrencyChange) {
            // check store card after changing currency
            CurrencyChangeTesterCommon.changeCurrency(currencyCode);
            check_store_card_visibility("check_store_card_visibility_after_changing_activities", shouldBeVisible, setTo, true);
        }

        // set store card switch back to the initial mode
        if (setTo)
            TestUtils.setStoreCardSwitch(!setTo);
    }

    // Use in case shouldBeVisible is false
    public static void check_shipping_same_as_billing_switch_visibility(String testName, boolean shouldBeVisible) {
        check_shipping_same_as_billing_switch_visibility(testName, shouldBeVisible,false);
    }

    /**
     * This test verifies the visibility of the "shipping same as billing" switch.
     */
    public static void check_shipping_same_as_billing_switch_visibility(String testName, boolean shouldBeVisible, boolean shouldBeOn) {

        if (shouldBeVisible) {
            //verify store card layout is visible
            onView(withId(R.id.shippingSameAsBillingRelativeLayout))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing layout is not visible"))
                    .check(matches(isDisplayed()));

            //verify store card switch is visible
            onView(withId(R.id.shippingSameAsBillingSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing switch is not visible"))
                    .check(matches(isDisplayed()));
        } else {
            //verify store card layout is not visible
            onView(withId(R.id.shippingSameAsBillingRelativeLayout))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing layout is visible"))
                    .check(matches(not(ViewMatchers.isDisplayed())));

            //verify store card switch is not visible
            onView(withId(R.id.shippingSameAsBillingSwitch))
                    .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing switch is visible"))
                    .check(matches(not(ViewMatchers.isDisplayed())));
        }

        if (shouldBeVisible) {
            if (shouldBeOn) {
                //verify store card layout is checked
                onView(withId(R.id.shippingSameAsBillingSwitch))
                        .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing switch is not checked"))
                        .check(matches(isChecked()));
            } else {
                //verify store card layout is not checked
                onView(withId(R.id.shippingSameAsBillingSwitch))
                        .withFailureHandler(new CustomFailureHandler(testName + ": Shipping same as billing switch is checked"))
                        .check(matches(not(isChecked())));
            }
        }
    }

}
