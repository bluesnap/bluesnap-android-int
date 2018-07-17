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
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AllowCurrencyChangeTest extends EspressoBasedTest {
    boolean isAllowed = true;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(55.5, "USD");
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setAllowCurrencyChange(isAllowed);
        setupAndLaunch(sdkRequest);
        onView(withId(R.id.newCardButton)).perform(click());
    }

    /**
     * This test verifies the visibility of the currency hamburger button to the shopper,
     * according to whether we allowed currency change or not.
     * It covers visibility in billing, shipping and after changing activities
     */
    @Test
    public void hide_currency_change_hamburger_validation() throws InterruptedException {
        //check hamburger button is not displayed in billing
        check_currency_hamburger_button_visibility();
        //check hamburger button is not displayed after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        check_currency_hamburger_button_visibility();

        CardFormTesterCommon.fillInCCLineWithValidCard();
        CardFormTesterCommon.fillInContactInfoBilling("SP", true, false);

        //check hamburger button is not displayed in shipping
        onView(withId(R.id.buyNowButton)).perform(click());
        check_currency_hamburger_button_visibility();

        //check hamburger button is not displayed after opening country activity
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        check_currency_hamburger_button_visibility();

        //check hamburger button is not displayed back in billing
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();
        check_currency_hamburger_button_visibility();
    }

    private void check_currency_hamburger_button_visibility() throws InterruptedException {
        if (isAllowed)
            onView(withId(R.id.hamburger_button)).check(matches(ViewMatchers.isDisplayed()));
        else
            onView(withId(R.id.hamburger_button)).check(matches(not(ViewMatchers.isDisplayed())));

    }
}
