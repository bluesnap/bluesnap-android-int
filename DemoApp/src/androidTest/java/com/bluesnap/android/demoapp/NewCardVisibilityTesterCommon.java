package com.bluesnap.android.demoapp;

import android.content.Context;
import android.support.test.espresso.Espresso;

import android.support.test.espresso.matcher.ViewMatchers;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.services.AndroidUtil;

import java.io.IOException;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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
public class NewCardVisibilityTesterCommon {
    public static void new_credit_cc_info_visibility_validation() {
        onView(withId(R.id.creditCardNumberEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.expEditText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.cvvEditText)).check(matches(not(isDisplayed())));
    }

    public static void new_credit_contact_info_visibility_validation(int componentResourceId, boolean fullInfo, boolean withEmail) {
        //verifies that the right component(billing/shipping) is displayed- is this necessary?
        onView(withId(componentResourceId)).check(matches(isDisplayed()));

        Espresso.closeSoftKeyboard();
        //verifies that all right fields are displayed in the component
        onView(allOf(withId(R.id.input_name), isDescendantOfA(withId(componentResourceId)))).check(matches(isDisplayed()));
        if (withEmail)
            onView(withId(R.id.input_email)).check(matches(isDisplayed()));
        else if (componentResourceId == R.id.billingViewComponent)
            onView(withId(R.id.input_email)).check(matches(not(isDisplayed())));

        if (fullInfo) {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId)))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId)))).check(matches(isDisplayed()));
        } else {
            onView(allOf(withId(R.id.input_city), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));
            onView(allOf(withId(R.id.input_address), isDescendantOfA(withId(componentResourceId)))).check(matches(not(isDisplayed())));
        }

    }

    /**
     * This test verifies that the country image matches the shopper's country
     * when first entering billing or shipping info.
     * (according to its location, or us by default)
     */
    public static void default_country_view_validation(Context context, String defaultCountry, int componentResourceId) throws InterruptedException, IOException {
        //get the expected drawable id
        Integer resourceId = context.getResources().getIdentifier(defaultCountry.toLowerCase(), "drawable", context.getPackageName());

        //check image is as expected
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).check(matches(TestUtils.withDrawable(resourceId)));
    }

    /**
     * This test verifies that the country image changes as expected, according
     * to different choices in billing or shipping info.
     */
    public static void changing_country_view_validation(int componentResourceId) throws InterruptedException {
        //Test validation of country image- changing to Canada
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).check(matches(TestUtils.withDrawable(R.drawable.ca)));

        //Test validation of country image- changing to Argentina
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Argentina"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).check(matches(TestUtils.withDrawable(R.drawable.ar)));
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing and shipping).
     */
    public static void default_country_zip_view_validation(String defaultCountry, int componentResourceId) throws InterruptedException {
        //Test validation of zip appearance according to the country
        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(defaultCountry)) //Country with zip
            onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed
        else //Country without zip
            onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId)))).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the zip view is displayed
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to different choices of countries in billing or shipping info.
     */
    public static void changing_country_zip_view_validation(int componentResourceId) throws InterruptedException {
        //Test validation of zip appearance. changing to USA
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed

        //changing to Angola (without zip)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Angola"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId)))).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the zip view is displayed

        //Test validation of zip appearance. changing to Israel
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Israel"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_zip), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed())); //Check that the zip view is displayed
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing and shipping).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public static void default_country_state_view_validation(int componentResourceId, String country) throws InterruptedException {
        //Test validation of state appearance
        if (country.equals("US") || country.equals("CA") || country.equals("BR"))  //Country is one of US CA BR- has state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed())); //Check that the state view is displayed
        else  //Country is not one of US CA BR- doesn't have state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the state view is displayed
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in billing or shipping info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    public static void changing_country_state_view_validation(int componentResourceId) throws InterruptedException {
        //Test validation of state appearance. changing to USA
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(componentResourceId)))).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(componentResourceId)))).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * This test verifies that changing the country in one fragment (billing/shipping contact
     * info) doesn't change the country in the other.
     */
    public static void country_changes_per_fragment_validation(boolean inBilling, boolean fullInfo, boolean withEmail) throws InterruptedException {
        int firstComponentResourceId, secondComponentResourceId;
        firstComponentResourceId = inBilling ? R.id.billingViewComponent : R.id.newShoppershippingViewComponent;
        secondComponentResourceId = inBilling ? R.id.newShoppershippingViewComponent : R.id.billingViewComponent;

        //Changing country to Spain in first fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(firstComponentResourceId)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        if (inBilling) //continue to shipping
            TestUtils.continue_to_shipping_or_pay_in_new_card("SP", fullInfo, withEmail);

        else //go back to billing
            TestUtils.go_back_to_billing_in_new_card();


        //Changing Country to Italy in second fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(secondComponentResourceId)))).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        if (inBilling) //go back to billing
            TestUtils.go_back_to_billing_in_new_card();

        else //continue to shipping
            onView(withId(R.id.buyNowButton)).perform(click());

        //Verify country hasn't change in first fragment
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(firstComponentResourceId)))).check(matches(TestUtils.withDrawable(R.drawable.es)));
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public static void pay_button_validation(int buttonComponent, String checkoutCurrency, Double purchaseAmount, Double taxAmount) throws InterruptedException {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(withText(TestUtils.getStringFormatAmount("Pay",
                        AndroidUtil.getCurrencySymbol(checkoutCurrency), purchaseAmount + taxAmount))));
    }

    /**
     * This test verifies that the "Shipping" button is visible
     */
    public static void shipping_button_validation(int buttonComponent) throws InterruptedException {
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(ViewMatchers.isDisplayed()));
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))))
                .check(matches(withText("Shipping")));
    }

    /**
     * This test verifies that the amount tax shipping component is visible
     */
    public static void amount_tax_shipping_view_validation(int amountTaxShippingComponent, String currency, String amount, String tax) throws InterruptedException {
        //verify component is visible
        onView(withId(amountTaxShippingComponent)).check(matches(ViewMatchers.isDisplayed()));

        //verify amount and tax is visible
        onView(allOf(withId(R.id.amountTaxLinearLayout), isDescendantOfA(withId(amountTaxShippingComponent))))
                .check(matches(ViewMatchers.isDisplayed()));

        //verify that the presented amount and tax are correct
        onView(allOf(withId(R.id.amountTextView), isDescendantOfA(withId(amountTaxShippingComponent))))
                .check(matches(withText(AndroidUtil.getCurrencySymbol(currency) + " " + amount)));
//                .check(matches(withText(containsString(amount))));
        onView(allOf(withId(R.id.taxTextView), isDescendantOfA(withId(amountTaxShippingComponent))))
                .check(matches(withText(AndroidUtil.getCurrencySymbol(currency) + " " + tax)));
    }


}
