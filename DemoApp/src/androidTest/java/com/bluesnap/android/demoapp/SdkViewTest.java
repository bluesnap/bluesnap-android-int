package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.NoMatchingViewException;
//import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageButton;

import com.bluesnap.androidapi.models.SdkRequest;
//import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.ContactInfoViewComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.getIdlingResources;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static com.bluesnap.android.demoapp.CardFormTesterCommon.cardNumberGeneratorTest;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.CardFormTesterCommon.invalidCardNumberGeneratorTest;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 04/06/2018.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SdkViewTest extends EspressoBasedTest {

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
     * This test checks whether the state field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void state_view_validation() throws InterruptedException {
        Matcher<View> countryImageButtonImageBottunVM = withId(R.id.countryImageButton);
        Matcher<View> stateLayoutVM = withId(R.id.input_layout_state);

        //Test validation of state appearance
        onView(countryImageButtonImageBottunVM).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
                //Country is not one of US CA BR
                //Check that the state view is not displayed
                onView(withId(R.id.input_layout_state)).check(matches(not(ViewMatchers.isDisplayed())));
            }
        })
                .check(matches(anyOf(TestUtils.withDrawable(R.drawable.us), TestUtils.withDrawable(R.drawable.ca), TestUtils.withDrawable(R.drawable.br))));

        onView(countryImageButtonImageBottunVM).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
                //Country is one of US CA BR
                //Check that the state view is displayed
                onView(withId(R.id.input_layout_state)).check(matches(ViewMatchers.isDisplayed()));
            }
        })
                .check(matches(not(anyOf(TestUtils.withDrawable(R.drawable.us), TestUtils.withDrawable(R.drawable.ca), TestUtils.withDrawable(R.drawable.br)))));

    }

    /**
     * This test checks whether the state field is visible to the user or not, according
     * to different choices of countries in billing info.
     * If the country is USA, Canada or Brazil, then it should be visible,
     * o.w. it doesn't.
     */
    @Test
    public void state_view_validation_after_changing_country() throws InterruptedException {
        //------------------------------------------
        // Country Image
        //------------------------------------------

        String billingCountry = "USA";

        Matcher<View> countryImageButtonImageBottunVM = withId(R.id.countryImageButton);
        Matcher<View> stateLayoutVM = withId(R.id.input_layout_state);

        //Test validation of state appearance. changing to USA
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("United States"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(TestUtils.withDrawable(R.drawable.us)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

        //changing to Italy (without state)
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Italy"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(TestUtils.withDrawable(R.drawable.it)));
        onView(stateLayoutVM).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Canada
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Canada"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(TestUtils.withDrawable(R.drawable.ca)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

        //changing to Spain (without state)
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(TestUtils.withDrawable(R.drawable.es)));
        onView(stateLayoutVM).check(matches(not(ViewMatchers.isDisplayed())));

        //Test validation of state appearance. changing to Brazil
        onView(countryImageButtonImageBottunVM).perform(click());
        onData(hasToString(containsString("Brazil"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(countryImageButtonImageBottunVM).check(matches(TestUtils.withDrawable(R.drawable.br)));
        onView(stateLayoutVM).check(matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void state_invalid_error() throws InterruptedException {


    }


    @Test
    public void country_changes_in_billing_and_shipping() throws InterruptedException {
        //Matcher<View> countryImageButtonImageBottunVM = withId(R.id.countryImageButton);
        //Matcher<View> buynowButtonVM = withId(R.id.buyNowButton);
        //Changing country to Spain
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfo(this.mActivity.getApplicationContext(), true, false);

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


}
