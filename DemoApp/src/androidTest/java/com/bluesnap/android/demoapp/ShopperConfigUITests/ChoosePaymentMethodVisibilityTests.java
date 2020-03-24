package com.bluesnap.android.demoapp.ShopperConfigUITests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ReturningShopperVisibilityTesterCommon;
import com.bluesnap.android.demoapp.CustomFailureHandler;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
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

/**
 * Created by sivani on 27/08/2018.
 */

public class ChoosePaymentMethodVisibilityTests extends ChoosePaymentMethodEspressoBasedTester {

    public ChoosePaymentMethodVisibilityTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        choosePaymentSetup(true, true);
    }

    @Test
    public void supported_payment_methods_visibility_test() {
        //verify that new card button is displayed
        onView(withId(R.id.newCardButton))
                .withFailureHandler(new CustomFailureHandler("new Credit Card button is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));

        //verify that payPal button is displayed
        onView(withId(R.id.payPalButton))
                .withFailureHandler(new CustomFailureHandler("Paypal button is not displayed"))
                .check(matches(ViewMatchers.isDisplayed()));

        //verify that existing credit card component is displayed with the right content
        ReturningShopperVisibilityTesterCommon.credit_card_in_list_visibility_validation("supported_payment_methods_visibility_test in ",
                TestingShopperCreditCard.VISA_CREDIT_CARD.getCardLastFourDigits(),
                Integer.toString(TestingShopperCreditCard.VISA_CREDIT_CARD.getExpirationMonth()) + "/" +
                        TestingShopperCreditCard.VISA_CREDIT_CARD.getExpirationYearLastTwoDigit());
    }

    @Test
    public void choose_existing_card_visibility_test() {
        //choose existing credit card
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        currency_hamburger_button_visibility_in_credit_card();
        submit_button_visibility_and_content_in_existing_card();

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        currency_hamburger_button_visibility_in_billing();
        Espresso.pressBack();


        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
        currency_hamburger_button_visibility_in_shipping();
        Espresso.pressBack();
    }

    @Test
    public void choose_new_card_visibility_test() {
        // choose new credit card
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        currency_hamburger_button_visibility_in_billing();

        // verify store card is visible
        check_store_card_visibility();

        // check store card after changing activities
        check_store_card_visibility_after_changing_activities(true);
        check_store_card_visibility_after_changing_activities(false);

        TestUtils.continueToShippingOrPayInNewCard(defaultCountryKey, true, true, true, true);
        currency_hamburger_button_visibility_in_shipping();

        submit_button_visibility_and_content_in_new_card();
    }

    /**
     * This test verifies that the "Submit" button is visible and contains
     * the correct content in existing card
     */
    public void submit_button_visibility_and_content_in_existing_card() {
        ShopperConfigVisibilityTesterCommon.submit_button_visibility_and_content("submit_button_visibility_and_content", R.id.returningShppoerCCNFragmentButtonComponentView);
    }

    /**
     * This test verifies that the "Submit" button is visible and contains
     * the correct content
     */
    public void submit_button_visibility_and_content_in_new_card() {
        ShopperConfigVisibilityTesterCommon.submit_button_visibility_and_content("submit_button_visibility_and_content", R.id.shippingButtonComponentView);
    }

    /**
     * This test verifies that the hamburger button is not displayed in credit card
     */
    private void currency_hamburger_button_visibility_in_credit_card() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_credit_card");
    }

    /**
     * This test verifies that the hamburger button is not displayed in billing
     */
    public void currency_hamburger_button_visibility_in_billing() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_billing");
    }

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility and switch state
     */
    public void check_store_card_visibility() {
        CreditCardVisibilityTesterCommon.check_store_card_visibility("check_store_card_visibility" + shopperCheckoutRequirements, true);
    }

    /**
     * This test verifies the visibility of store card switch.
     * It covers visibility and switch state
     */
    public void check_store_card_visibility_after_changing_activities(boolean setTo) {
        CreditCardVisibilityTesterCommon.check_store_card_visibility_after_changing_activities(true, setTo, shopperCheckoutRequirements, defaultCountryValue, ContactInfoTesterCommon.getDefaultStateByCountry(defaultCountryKey), checkoutCurrency, false);
    }

    /**
     * This test verifies that the hamburger button is not displayed in shipping
     */
    public void currency_hamburger_button_visibility_in_shipping() {
        ShopperConfigVisibilityTesterCommon.currency_hamburger_button_visibility("currency_hamburger_button_visibility_in_shipping");
    }


}
