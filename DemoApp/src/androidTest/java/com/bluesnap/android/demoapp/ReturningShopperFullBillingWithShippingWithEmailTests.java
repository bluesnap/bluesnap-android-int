package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 13/08/2018.
 */

public class ReturningShopperFullBillingWithShippingWithEmailTests extends EspressoBasedTest {
    private static String BILLING_COUNTRY;
    private static String SHIPPING_COUNTRY;

    public ReturningShopperFullBillingWithShippingWithEmailTests() {
//        super("?shopperId=" + RETURNING_SHOPPER_ID_MIN_BILLING_WITH_SHIPPING_WITH_EMAIL);
        super(true, "");
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        sdkRequest.setEmailRequired(true);
        setupAndLaunch(sdkRequest);
        BILLING_COUNTRY = returningShopper.getBillingContactInfo().getCountry();
        SHIPPING_COUNTRY = returningShopper.getShippingContactInfo().getCountry();
        if (!returningShopper.isFullBilling()) //reset full billing info for this shopper
            returningShopper.getBillingContactInfo().resetFullBillingFields();
        if (!returningShopper.isWithEmail()) //reset email for this shopper
            returningShopper.getBillingContactInfo().setEmail("");
        if (!returningShopper.isWithShipping()) //reset shipping info for this shopper
            returningShopper.getShippingContactInfo().resetAllFields();
    }

    @After
    public void keepRunning() {
        Intents.release();
        //Thread.sleep(1000);
    }

    public void returning_shopper_full_billing_with_shipping_with_email_common_tester() throws IOException {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        billing_summarized_contact_info_visibility_validation();

        if (ReturningShoppersFactory.COUNTER == 0) {
            pay_button_in_billing_validation();
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
            billing_contact_info_content_validation();
            Espresso.pressBack();
        }

        shipping_summarized_contact_info_visibility_validation();
        if (ReturningShoppersFactory.COUNTER == 0) {
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            shipping_contact_info_content_validation();
            Espresso.pressBack();

            //Pre-condition: current info is billingInfo
            //TODO: restore this when the bug is fixed (AS-148)
//        returning_shopper_edit_billing_contact_info_using_back_button_validation();
//        Espresso.pressBack();
            returning_shopper_edit_billing_contact_info_using_done_button_validation();
            Espresso.pressBack();

            //Pre-condition: current info is shippingInfo
            //TODO: restore this when the bug is fixed (AS-148)
//        returning_shopper_edit_shipping_contact_info_using_back_button_validation();
//        Espresso.pressBack();
            returning_shopper_edit_shipping_contact_info_using_done_button_validation();
        } else if (returningShopper.isWithShipping()) //TODO: change to else (without condition) after the bug is fixed (AS-119)
            component_opens_when_pressing_buyNow_with_missing_info();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_1() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_2() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_3() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_4() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_5() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_6() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_7() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_test_8() throws IOException {
        returning_shopper_full_billing_with_shipping_with_email_common_tester();
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
     * This test verifies that the "Pay" button is visible and contains
     * the correct currency symbol and amount
     */
    public void pay_button_in_billing_validation() {
        double tax = BILLING_COUNTRY.equals("US") ? taxAmount : 0.00;
        CreditCardVisibilityTesterCommon.pay_button_visibility_and_content_validation("pay_button_in_shipping_validation in " + returningShopper.getShopperDescription(),
                R.id.returningShppoerCCNFragmentButtonComponentView, checkoutCurrency, purchaseAmount, tax);
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void billing_summarized_contact_info_visibility_validation() {
        boolean isEmailVisible = returningShopper.isWithEmail();
        if (!returningShopper.isFullBilling()) //reset shipping info for this shopper
            ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                    R.id.billingViewSummarizedComponent, false, isEmailVisible, returningShopper.getBillingContactInfo());
        else
            ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation("billing_summarized_contact_info_visibility_validation in " + returningShopper.getShopperDescription(),
                    R.id.billingViewSummarizedComponent, true, isEmailVisible, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() throws IOException {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewComponent, BILLING_COUNTRY, true, true, returningShopper.getBillingContactInfo());
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
    public void shipping_contact_info_content_validation() throws IOException {

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("shipping_contact_info_content_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.returningShoppershippingViewComponent, SHIPPING_COUNTRY, true, false, returningShopper.getShippingContactInfo());
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_billing_contact_info_using_done_button_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewSummarizedComponent, true, true, true, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_billing_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_billing_contact_info_using_back_button_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.billingViewSummarizedComponent, true, true, false, returningShopper.getBillingContactInfo());
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the new content after editing the info.
     * It uses the "Done" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_done_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_shipping_contact_info_using_done_button_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.shippingViewSummarizedComponent, true, false, true, null);
    }

    /**
     * This test verifies that the summarized billing contact info and the
     * billing contact info presents the old content after editing the info,
     * since it uses the "Back" button to go back to credit card fragment.
     */
    public void returning_shopper_edit_shipping_contact_info_using_back_button_validation() throws IOException {
        ContactInfoTesterCommon.returning_shopper_edit_contact_info_validation("returning_shopper_edit_shipping_contact_info_using_back_button_validation in " + returningShopper.getShopperDescription(),
                applicationContext, R.id.shippingViewSummarizedComponent, true, false, false, returningShopper.getShippingContactInfo());
    }

    /**
     * This test verifies that when there is missing info in returning shopper,
     * and we press "pay", it passes to the edit component,
     * and not making a transaction.
     */
    public void component_opens_when_pressing_buyNow_with_missing_info() {
        ReturningShopperVisibilityTesterCommon.component_opens_when_pressing_buyNow_with_missing_info("component_opens_when_pressing_buyNow_with_missing_info in " + returningShopper.getShopperDescription(), true, true, true, returningShopper);
    }
}
