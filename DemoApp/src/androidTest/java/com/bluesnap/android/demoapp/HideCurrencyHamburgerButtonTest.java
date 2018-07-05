package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 03/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HideCurrencyHamburgerButtonTest extends EspressoBasedTest {
    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }


    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setAllowCurrencyChange(false);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());

    }

    /**
     * This test verifies that the currency hamburger button is not visible to the shopper,
     * as we didn't allow currency change.
     * It covers visibility in billing, shipping and after changing activities
     */
    @Test
    public void hide_currency_change_hamburger_validation() throws InterruptedException, BSPaymentRequestException {
        //check hamburger button is not displayed in billing
        onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

        //check hamburger button is not displayed after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfo("SP", true, false);

        //check hamburger button is not displayed in shipping
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

        //check hamburger button is not displayed after opening country activity
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

        //check hamburger button is not displayed back in billing
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();
        onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));
    }
}
