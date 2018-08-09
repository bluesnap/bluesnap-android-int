package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.junit.Before;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by sivani on 02/08/2018.
 */

public class ReturningShopperMinimalBillingWithShippingTests extends EspressoBasedTest {
    private static String BILLING_COUNTRY;
    private static String SHIPPING_COUNTRY;


    public ReturningShopperMinimalBillingWithShippingTests() {
//        super("?shopperId=" + RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING);
        super(true, "");
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        BILLING_COUNTRY = returningShopper.getBillingCountry();
        SHIPPING_COUNTRY = returningShopper.getShippingCountry();
        if (!returningShopper.isWithShipping())
            returningShopperBillingContactInfo.setEmail("");
    }

    //@Test
    public void returning_shopper_minimal_billing_with_shipping_test() throws IOException {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        pay_button_in_billing_validation();

        billing_summarized_contact_info_visibility_validation();
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        billing_contact_info_content_validation();
        Espresso.pressBack();

        shipping_summarized_contact_info_visibility_validation();
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
        shipping_contact_info_content_validation();
        Espresso.pressBack();

        //Pre-condition: current info is billingInfo
        returning_shopper_edit_billing_contact_info_using_back_button_validation();
        Espresso.pressBack();
        returning_shopper_edit_billing_contact_info_using_done_button_validation();
        Espresso.pressBack();

        //Pre-condition: current info is shippingInfo
//        returning_shopper_edit_shipping_contact_info_using_back_button_validation();
//        Espresso.pressBack();
        returning_shopper_edit_shipping_contact_info_using_done_button_validation();
        TestUtils.go_back_to_credit_card_in_returning_shopper(false, 0);
        amount_tax_view_in_shipping_validation();
        country_changes_per_billing_validation();
        country_changes_per_shipping_validation();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * in the returning shopper cards list.
     */
    public void credit_card_in_list_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_in_list_visibility_validation("credit_card_in_list_visibility_validation", "5288", "12/26");
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing an existing credit card in returning shopper.
     */
    public void credit_card_view_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_view_visibility_validation("credit_card_view_visibility_validation", "5288", "12/26");
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void billing_summarized_contact_info_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation", R.id.billingViewSummarizedComponent, BILLING_COUNTRY,
                false, false);
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        double tax = BILLING_COUNTRY.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation", R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() throws IOException {

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation", applicationContext, R.id.billingViewComponent, BILLING_COUNTRY, false, false);
    }

    /**
     * This test verifies that the summarized shipping contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void shipping_summarized_contact_info_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("shipping_summarized_contact_info_visibility_validation", R.id.shippingViewSummarizedComponent, SHIPPING_COUNTRY,
                true, false);
    }

    /**
     * This test verifies that the shipping contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void shipping_contact_info_content_validation() throws IOException {

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("shipping_contact_info_content_validation", applicationContext, R.id.returningShoppershippingViewComponent, SHIPPING_COUNTRY, false, false);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation", applicationContext, R.id.billingViewSummarizedComponent, false, false, true, BILLING_COUNTRY, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation", applicationContext, R.id.billingViewSummarizedComponent, false, false, false, BILLING_COUNTRY, returningShopperBillingContactInfo);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation", applicationContext, R.id.shippingViewSummarizedComponent, true, false, true, SHIPPING_COUNTRY, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation", applicationContext, R.id.shippingViewSummarizedComponent, true, false, false, SHIPPING_COUNTRY, returningShopperShippingContactInfo);
    }

    /**
     * This test verifies that the amount tax shipping component is visible
     * in shipping, and that it presents the right amount and tax.
     */
    public void amount_tax_view_in_shipping_validation() {
        if (!BILLING_COUNTRY.equals("US")) {
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            ContactInfoTesterCommon.changeCountry(R.id.returningShoppershippingViewComponent, "United States");
            onView(allOf(withId(R.id.input_state), isDescendantOfA(withId(R.id.returningShoppershippingViewComponent)))).perform(scrollTo(), click());
            onData(hasToString(containsString("New York"))).inAdapterView(withId(R.id.state_list_view)).perform(click());
            TestUtils.go_back_to_credit_card_in_returning_shopper(true, R.id.returningShopperShippingFragmentButtonComponentView);
        }

        //verify that the amount tax shipping component is presented
        CreditCardVisibilityTesterCommon.amount_tax_shipping_view_validation("amount_tax_view_in_shipping_validation", R.id.amountTaxShippingComponentView, checkoutCurrency,
                TestUtils.get_amount_in_string(df, purchaseAmount), TestUtils.get_amount_in_string(df, taxAmount));
    }

    /**
     * This test verifies that changing the country in billing component (billing/shipping contact
     * info) doesn't change the country in shipping component.
     */
    public void country_changes_per_billing_validation() {
        ReturningShopperVisibilityTesterCommon.country_changes_per_fragment_validation("country_changes_per_billing_validation", R.id.billingViewSummarizedComponent, "ES", "Spain");
    }

    /**
     * This test verifies that changing the country in shipping component (billing/shipping contact
     * info) doesn't change the country in billing component.
     */
    public void country_changes_per_shipping_validation() {
        ReturningShopperVisibilityTesterCommon.country_changes_per_fragment_validation("country_changes_per_shipping_validation", R.id.shippingViewSummarizedComponent, "IT", "Italy");
    }
}
