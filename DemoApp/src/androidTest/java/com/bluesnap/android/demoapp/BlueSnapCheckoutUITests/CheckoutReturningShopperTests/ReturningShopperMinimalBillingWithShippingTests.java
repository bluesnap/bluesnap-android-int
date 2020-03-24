package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ReturningShopperVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutEspressoBasedTester;
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
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 02/08/2018.
 */

public class ReturningShopperMinimalBillingWithShippingTests extends CheckoutEspressoBasedTester {
    private String BILLING_COUNTRY;
    private String SHIPPING_COUNTRY;

    public ReturningShopperMinimalBillingWithShippingTests() {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(false, false, true);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException, JSONException {
        checkoutSetup(true);
        BILLING_COUNTRY = returningShopper.getBillingContactInfo().getCountryKey();
        SHIPPING_COUNTRY = returningShopper.getShippingContactInfo().getCountryKey();
        if (!returningShopper.isWithShipping()) //reset shipping info for this shopper
            returningShopper.getShippingContactInfo().resetAllFields();
    }

    public void returning_shopper_minimal_billing_with_shipping_common_tester() {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(ViewMatchers.withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        billing_summarized_contact_info_visibility_validation();

        if (ReturningShoppersFactory.COUNTER == 3) { //minimal billing with shipping returning shopper

            pay_button_in_billing_validation();

            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
            billing_contact_info_content_validation();
            Espresso.pressBack();
        }

        shipping_summarized_contact_info_visibility_validation();
        if (ReturningShoppersFactory.COUNTER == 3) { //minimal billing with shipping returning shopper
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            shipping_contact_info_content_validation();
            Espresso.pressBack();

            //Pre-condition: current info is billingInfo
            returning_shopper_edit_billing_contact_info_using_back_button_validation();
            Espresso.pressBack();
            returning_shopper_edit_billing_contact_info_using_done_button_validation();
            Espresso.pressBack();

            //Pre-condition: current info is shippingInfo
            returning_shopper_edit_shipping_contact_info_using_back_button_validation();
            Espresso.pressBack();

            returning_shopper_edit_shipping_contact_info_using_done_button_validation();
            TestUtils.goBackToCreditCardInReturningShopper(false, 0);
            amount_tax_view_in_shipping_validation();
            country_changes_per_billing_validation();
            country_changes_per_shipping_validation();
        } else
            component_opens_when_pressing_buyNow_with_missing_info();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_1() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_2() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_3() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_4() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_5() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_6() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_7() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_test_8() {
        returning_shopper_minimal_billing_with_shipping_common_tester();
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
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                R.id.billingViewSummarizedComponent, false, false, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        double tax = SHIPPING_COUNTRY.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation in " + returningShopper.getShopperDescription(),
                R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewComponent, BILLING_COUNTRY, false, false);
    }

    /**
     * This test verifies that the summarized shipping contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void shipping_summarized_contact_info_visibility_validation() {
        if (!returningShopper.isWithShipping()) //reset shipping info for this shopper
            ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("shipping_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                    R.id.shippingViewSummarizedComponent, false, false, returningShopper.getShippingContactInfo());
        else
            ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("shipping_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                    R.id.shippingViewSummarizedComponent, true, false, returningShopper.getShippingContactInfo());
    }

    /**
     * This test verifies that the shipping contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void shipping_contact_info_content_validation() {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("shipping_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.returningShoppershippingViewComponent, SHIPPING_COUNTRY, true, false, returningShopper.getShippingContactInfo());
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_done_button_validation() {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewSummarizedComponent, false, false, true, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_back_button_validation() {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewSummarizedComponent, false, false, false, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_done_button_validation() {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.shippingViewSummarizedComponent, true, false, true, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_back_button_validation()  {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_contact_info_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.shippingViewSummarizedComponent, true, false, false, returningShopper.getShippingContactInfo());
    }

    /**
     * This test verifies that the amount tax shipping component is visible
     * in shipping, and that it presents the right amount and tax.
     */
    public void amount_tax_view_in_shipping_validation() {
        if (!SHIPPING_COUNTRY.equals("US")) {
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            ContactInfoTesterCommon.changeCountry(R.id.returningShoppershippingViewComponent, "United States");
            ContactInfoTesterCommon.changeState(R.id.returningShoppershippingViewComponent, "New York");
            TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperShippingFragmentButtonComponentView);
        }

        //verify that the amount tax shipping component is presented
        CreditCardVisibilityTesterCommon.amount_tax_shipping_view_validation("amount_tax_view_in_shipping_validation in " + returningShopper.getShopperDescription(), R.id.amountTaxShippingComponentView, checkoutCurrency,
                TestUtils.getDecimalFormat().format(purchaseAmount), TestUtils.getDecimalFormat().format(taxAmount));
    }

    /**
     * This test verifies that changing the country in billing component (billing/shipping contact
     * info) doesn't change the country in shipping component.
     */
    public void country_changes_per_billing_validation() {
        ReturningShopperVisibilityTesterCommon.country_changes_per_fragment_validation("country_changes_per_billing_validation in " + returningShopper.getShopperDescription(),
                R.id.billingViewSummarizedComponent, "ES", "Spain");
    }

    /**
     * This test verifies that changing the country in shipping component (billing/shipping contact
     * info) doesn't change the country in billing component.
     */
    public void country_changes_per_shipping_validation() {
        ReturningShopperVisibilityTesterCommon.country_changes_per_fragment_validation("country_changes_per_shipping_validation in " + returningShopper.getShopperDescription(),
                R.id.shippingViewSummarizedComponent, "IT", "Italy");
    }

    /**
     * This test verifies that when there is missing info in returning shopper,
     * and we press "pay", it passes to the edit component,
     * and not making a transaction.
     */
    public void component_opens_when_pressing_buyNow_with_missing_info() {
        ReturningShopperVisibilityTesterCommon.component_opens_when_pressing_buyNow_with_missing_info("component_opens_when_pressing_buyNow_with_missing_info in " + returningShopper.getShopperDescription(), false, true, false, returningShopper);
    }
}
