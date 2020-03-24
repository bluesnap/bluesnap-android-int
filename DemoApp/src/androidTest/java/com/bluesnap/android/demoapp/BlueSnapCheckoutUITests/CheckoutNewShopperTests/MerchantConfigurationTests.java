package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutNewShopperTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.SmallTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by sivani on 17/07/2018.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MerchantConfigurationTests extends CheckoutEspressoBasedTester {

    public MerchantConfigurationTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements();
    }

    public void setupForSdk(boolean fullBillingRequired, boolean emailRequired, boolean shippingRequired, boolean shippingSameAsBilling, boolean allowCurrencyChange, boolean hideStoreCardSwitch, boolean disableGooglePay) throws InterruptedException, BSPaymentRequestException, JSONException {
        shopperCheckoutRequirements.setTestingShopperCheckoutRequirements(fullBillingRequired, emailRequired, shippingRequired, shippingSameAsBilling);

        checkoutSetup(allowCurrencyChange, hideStoreCardSwitch, disableGooglePay, false);
    }


    //------------------------------------ Common tests ----------------------------

    @Test
    public void test_allow_currency_change() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, true, false, false);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        currencyChangeHamburgerViewValidation(true);
    }

    @Test
    public void test_not_allow_currency_change() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, false, false, false);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        currencyChangeHamburgerViewValidation(false);
    }

    @Test
    public void test_hide_store_card_switch() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, true, true, false);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        storeCardSwitchVisibility(true);
    }

    @Test
    public void test_not_hide_store_card_switch() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, true, false, false);
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        storeCardSwitchVisibility(false);
    }

    @Test
    public void test_enable_googlePay() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, true, false, false);

        googlePayButtonVisibility(true);
    }

    @Test
    public void test_disable_googlePay() throws InterruptedException, BSPaymentRequestException, JSONException {
        setupForSdk(true, true, true, false, true, false, true);

        googlePayButtonVisibility(false);
    }



    /**
     * This test verifies the visibility of the currency hamburger button,
     * according to whether we allowed currency change or not.
     * It covers visibility in billing, shipping and after changing activities
     */
    private void currencyChangeHamburgerViewValidation(boolean isChangeCurrencyAllowed) {
        //check hamburger button visibility in billing
        checkCurrencyHamburgerButtonVisibility(isChangeCurrencyAllowed);

        //check hamburger button visibility after opening country activity
        onView(withId(R.id.countryImageButton)).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility(isChangeCurrencyAllowed);

        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, "SP", true, true);

        //check hamburger button visibility in shipping
        TestUtils.setShippingSameAsBillingSwitch(false);
        TestUtils.pressBuyNowButton();
        checkCurrencyHamburgerButtonVisibility(isChangeCurrencyAllowed);

        //check hamburger button visibility after opening country activity
        onView(allOf(withId(R.id.countryImageButton), isDescendantOfA(withId(R.id.newShoppershippingViewComponent)))).perform(click());
        onData(hasToString(containsString("Spain"))).inAdapterView(withId(R.id.country_list_view)).perform(click());
        checkCurrencyHamburgerButtonVisibility(isChangeCurrencyAllowed);

        //check hamburger button visibility back in billing
        TestUtils.goBack();
        checkCurrencyHamburgerButtonVisibility(isChangeCurrencyAllowed);
    }

    private void checkCurrencyHamburgerButtonVisibility(boolean isChangeCurrencyAllowed) {
        if (isChangeCurrencyAllowed)
            onView(withId(R.id.hamburger_button))
                    .withFailureHandler(new CustomFailureHandler("currency_change_hamburger_view_validation: Hamburger button is not displayed"))
                    .check(matches(ViewMatchers.isDisplayed()));
        else
            onView(withId(R.id.hamburger_button))
                    .withFailureHandler(new CustomFailureHandler("currency_change_hamburger_view_validation: Hamburger button is displayed"))
                    .check(matches(not(ViewMatchers.isDisplayed())));

    }

    /**
     * This test verifies the visibility of the store card switch,
     * according to whether the merchant requested to hide the store card switch or not.
     */
    private void storeCardSwitchVisibility(boolean isStoreCardSwitchHidden) {
        CreditCardVisibilityTesterCommon.check_store_card_visibility("storeCardSwitchVisibility", !isStoreCardSwitchHidden);

    }

    /**
     * This test verifies the activation of Google Pay as a payment method,
     * according to whether the merchant requested to enable it or not.
     */
    private void googlePayButtonVisibility(boolean isGooglePayEnabled) {
        CreditCardVisibilityTesterCommon.check_payment_methods_visibility("GooglePayButtonVisibility",true, isGooglePayEnabled);

    }
}
