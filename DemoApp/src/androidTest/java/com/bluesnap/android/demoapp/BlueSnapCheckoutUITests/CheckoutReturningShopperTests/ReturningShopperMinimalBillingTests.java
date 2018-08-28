package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CurrencyChangeTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ReturningShopperVisibilityTesterCommon;
import com.bluesnap.android.demoapp.EspressoBasedTest;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by sivani on 28/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class ReturningShopperMinimalBillingTests extends EspressoBasedTest {
    private static String BILLING_COUNTRY;

    public ReturningShopperMinimalBillingTests() {
        super(true, "");
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        //sdkRequest.getShopperCheckoutRequirements().setBillingRequired(true);
        setupAndLaunch(sdkRequest);
        int cardPosition = randomTestValuesGenerator.randomReturningShopperCardPosition();
        //cardLastDigit = TestUtils.getText(withId(R.id.oneLineCCViewComponentsListView));
        BILLING_COUNTRY = returningShopper.getBillingContactInfo().getCountryKey();
    }

    public void returning_shopper_minimal_billing_common_tester() throws IOException {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(ViewMatchers.withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        billing_summarized_contact_info_visibility_validation();
        onView(withId(R.id.shippingViewSummarizedComponent)).check(matches(not(isDisplayed()))); //check shipping is not displayed

        if (ReturningShoppersFactory.COUNTER == 1) {
            pay_button_in_billing_validation();

            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
            billing_contact_info_content_validation();
            TestUtils.goBackToCreditCardInReturningShopper(false, 0);

            initial_currency_view_validation_in_billing();
            change_currency_in_billing_validation();
            change_currency_in_billing_amount_validation();


            //Pre-condition: current info is billingInfo
            returning_shopper_edit_billing_contact_info_using_back_button_validation();
            Espresso.pressBack();
            returning_shopper_edit_billing_contact_info_using_done_button_validation();
            Espresso.pressBack();
        } else
            component_opens_when_pressing_buyNow_with_missing_info();
    }

    @Test
    public void returning_shopper_minimal_billing_test_1() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_2() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_3() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_4() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_5() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_6() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_7() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_test_8() throws IOException {
        returning_shopper_minimal_billing_common_tester();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * in the returning shopper cards list.
     */
    public void credit_card_in_list_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_in_list_visibility_validation("credit_card_in_list_visibility_validation in " + returningShopper.getShopperDescription(),
                "5288", "12/26");
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing an existing credit card in returning shopper.
     */
    public void credit_card_view_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_view_visibility_validation("credit_card_view_visibility_validation in " + returningShopper.getShopperDescription(),
                "5288", "12/26");
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void billing_summarized_contact_info_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation in" + returningShopper.getShopperDescription(),
                R.id.billingViewSummarizedComponent, false, false, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation in " + returningShopper.getShopperDescription(),
                R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, 0.0);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() throws IOException {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewComponent, BILLING_COUNTRY, false, false);
    }

    /**
     * This test checks whether the zip field is visible to the user or not, according
     * to the default Country (the one that is chosen when entering billing).
     */
    public void default_country_zip_view_validation_in_billing() {
        CreditCardVisibilityTesterCommon.default_country_zip_view_validation("default_country_zip_view_validation_in_billing in " + returningShopper.getShopperDescription(), BILLING_COUNTRY, R.id.billingViewComponent);
    }

    /**
     * This test verifies the ime action button works as it should
     * in billing contact info
     */
    public void check_ime_action_button_in_billing_contact_info() {
        ContactInfoTesterCommon.check_ime_action_button_in_contact_info("check_ime_action_button_in_billing_contact_info in " + returningShopper.getShopperDescription(), BILLING_COUNTRY, R.id.billingViewComponent, false, false);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(), applicationContext, R.id.billingViewSummarizedComponent, false, false, true, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(), applicationContext, R.id.billingViewSummarizedComponent, false, false, false, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the initial currency in billing is presented
     * as it should in the hamburger and buy now buttons.
     */
    public void initial_currency_view_validation_in_billing() {
        CurrencyChangeTesterCommon.currency_view_validation("initial_currency_view_validation_in_billing in " + returningShopper.getShopperDescription(), R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency);
    }

    /**
     * This test verifies that changing the currency in billing
     * changes as it should in billing.
     */
    public void change_currency_in_billing_validation() {
        CurrencyChangeTesterCommon.changeCurrency("GBP");
        CurrencyChangeTesterCommon.currency_view_validation("change_currency_in_billing_validation in " + returningShopper.getShopperDescription(), R.id.returningShppoerCCNFragmentButtonComponentView, "GBP");
    }

    /**
     * This test verifies that after changing to different currencies
     * and back to the origin one in billing, the amount remains the same
     */
    public void change_currency_in_billing_amount_validation() {
        CurrencyChangeTesterCommon.change_currency_amount_validation("change_currency_in_billing_amount_validation in " + returningShopper.getShopperDescription(), R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, TestUtils.getDecimalFormat().format(purchaseAmount));
    }

    /**
     * This test verifies that when there is missing info in returning shopper,
     * and we press "pay", it passes to the edit component,
     * and not making a transaction.
     */
    public void component_opens_when_pressing_buyNow_with_missing_info() {
        ReturningShopperVisibilityTesterCommon.component_opens_when_pressing_buyNow_with_missing_info("component_opens_when_pressing_buyNow_with_missing_info in " + returningShopper.getShopperDescription(), false, false, false, returningShopper);
    }

}
