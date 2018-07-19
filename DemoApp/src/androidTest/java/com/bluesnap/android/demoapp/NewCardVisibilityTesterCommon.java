package com.bluesnap.android.demoapp;

import android.content.Context;
import android.support.test.espresso.Espresso;

import android.support.test.espresso.matcher.ViewMatchers;
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

import java.io.IOException;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
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
@RunWith(AndroidJUnit4.class)
@SmallTest
public class NewCardVisibilityTesterCommon extends EspressoBasedTest {

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

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
     * It covers both billing and shipping.
     */
    @Test
    public void state_view_validation() throws InterruptedException {
        String billingCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());
        boolean withState;

        //Test validation of state appearance in billing
        if (billingCountry.equals("US") || billingCountry.equals("CA") || billingCountry.equals("BR")) { //Country is one of US CA BR- has state
            onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed())); //Check that the state view is displayed
            withState = true;
        } else { //Country is not one of US CA BR- doesn't have state
            onView(withId(R.id.input_layout_state)).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the state view is not displayed
            withState = false;
        }

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(billingCountry, true, false);

        //Test validation of state appearance in shipping
        onView(withId(R.id.buyNowButton)).perform(click());
        if (withState) //Country is one of US CA BR- has state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(ViewMatchers.isDisplayed())); //Check that the state view is displayed

        else //Country is not one of US CA BR- doesn't have state
            onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the state view is displayed

        //Go back to billing
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        //Test validation of state appearance in billing
        if (withState) //Country is one of US CA BR- has state
            onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed())); //Check that the state view is displayed

        else //Country is not one of US CA BR- doesn't have state
            onView(withId(R.id.input_layout_state)).check(matches(not(ViewMatchers.isDisplayed()))); //Check that the state view is not displayed
    }


    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in billing info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void state_view_validation_after_changing_country_in_billing() throws InterruptedException {
        //------------------------------------------
        // Country Image
        //------------------------------------------

        //Test validation of state appearance. changing to USA
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.input_layout_state)).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.input_layout_state)).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in shipping info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void state_view_validation_after_changing_country_in_Shipping() throws InterruptedException {
        //------------------------------------------
        // Country Image
        //------------------------------------------
        String billingCountry = BlueSnapService.getInstance().getUserCountry(this.mActivity.getApplicationContext());

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling(billingCountry, true, false);
        onView(withId(R.id.buyNowButton)).perform(click());

        //Test validation of state appearance. changing to USA
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(allOf(withId(R.id.input_layout_state), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * This test verifies that changing the country in billing doesn't change the country in
     * shipping as well, and vice versa.
     */
    @Test
    public void country_changes_per_fragment_validation() throws InterruptedException {
        //Changing country to Spain
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("SP", true, false);

        //Continue to Shipping
        onView(withId(R.id.buyNowButton)).perform(click());

        //Verify country hasn't change in shipping
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).check(matches(not(TestUtils.withDrawable(R.drawable.es))));

        //Changing Country to Italy
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        //Go back to billing
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        //Verify country hasn't change in billing
        onView(withId(R.id.countryImageButton)).check(matches(TestUtils.withDrawable(R.drawable.es)));

    }

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


}
