package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 05/08/2018.
 */

public class ReturningShopperAllowCurrencyChangeTest extends EspressoBasedTest {

    protected boolean isAllowed = true;

    private static final String RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING = "22862697";

    public ReturningShopperAllowCurrencyChangeTest() {
        super(true, "?shopperId=" + RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING);
    }

    @Before
    public void setup() throws BSPaymentRequestException, InterruptedException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.getShopperCheckoutRequirements().setShippingRequired(true);
        sdkRequest.setAllowCurrencyChange(isAllowed);
        setupAndLaunch(sdkRequest);
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
    }

    /**
     * This test verifies the visibility of the currency hamburger button to the shopper,
     * according to whether we allowed currency change or not.
     * It covers visibility in billing, shipping and after changing activities
     */
    @Test
    public void currency_change_hamburger_view_validation() {
        //check hamburger button visibility in returning shopper existing card
        checkCurrencyHamburgerButtonVisibility(isAllowed);

        //check hamburger button visibility in billing
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        checkCurrencyHamburgerButtonVisibility(false);

        //check hamburger button visibility after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility(false);

        Espresso.pressBack();

        //check hamburger button visibility back in returning shopper existing card while using "back" button
        checkCurrencyHamburgerButtonVisibility(isAllowed);

        //check hamburger button visibility back in returning shopper existing card while using "Done" button
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.returningShopperBillingFragmentButtonComponentView)))).perform(click());
        checkCurrencyHamburgerButtonVisibility(isAllowed);

        //check hamburger button visibility in shipping
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
        checkCurrencyHamburgerButtonVisibility(false);

        //check hamburger button visibility after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility(false);

        Espresso.pressBack();

        //check hamburger button visibility back in returning shopper existing card while using "back" button
        checkCurrencyHamburgerButtonVisibility(isAllowed);

        //check hamburger button visibility back in returning shopper existing card while using "Done" button
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
        onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.returningShopperShippingFragmentButtonComponentView)))).perform(click());
        checkCurrencyHamburgerButtonVisibility(isAllowed);
    }

    private void checkCurrencyHamburgerButtonVisibility(boolean isAllowed) {
        if (isAllowed)
            onView(withId(R.id.hamburger_button))
                    .withFailureHandler(new CustomFailureHandler("currency_change_hamburger_view_validation: Hamburger button is not displayed"))
                    .check(matches(ViewMatchers.isDisplayed()));
        else
            onView(withId(R.id.hamburger_button))
                    .withFailureHandler(new CustomFailureHandler("currency_change_hamburger_view_validation: Hamburger button is displayed"))
                    .check(matches(not(ViewMatchers.isDisplayed())));

    }
}
