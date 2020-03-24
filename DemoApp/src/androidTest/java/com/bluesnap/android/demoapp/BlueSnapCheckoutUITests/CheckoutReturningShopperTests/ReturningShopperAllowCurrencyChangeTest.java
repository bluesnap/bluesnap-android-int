package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 05/08/2018.
 */

public class ReturningShopperAllowCurrencyChangeTest extends CheckoutEspressoBasedTester {

    protected boolean isAllowed = true;

    private static final String RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING = "22862697";

    public ReturningShopperAllowCurrencyChangeTest() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, true);
    }

    @Before
    public void setup() throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(true, RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING, isAllowed, false, false, false);

        onData(anything()).inAdapterView(ViewMatchers.withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
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
        TestUtils.pressBuyNowButton(R.id.returningShopperBillingFragmentButtonComponentView);
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
        TestUtils.pressBuyNowButton(R.id.returningShopperShippingFragmentButtonComponentView);
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
