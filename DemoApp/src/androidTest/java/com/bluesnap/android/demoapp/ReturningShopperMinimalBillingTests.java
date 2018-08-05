package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 28/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class ReturningShopperMinimalBillingTests extends EspressoBasedTest {
    private static final String RETURNING_SHOPPER_ID_MIN_BILLING = "22852981";

    private String cardLastDigit;

    public ReturningShopperMinimalBillingTests() {
        super("?shopperId=" + RETURNING_SHOPPER_ID_MIN_BILLING);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        //sdkRequest.setBillingRequired(true);
        setupAndLaunch(sdkRequest);
        int cardPosition = randomTestValuesGenerator.randomReturningShopperCardPosition();
        //cardLastDigit = TestUtils.getText(withId(R.id.oneLineCCViewComponentsListView));

    }

    @Test
    public void returning_shopper_minimal_billing_test() throws IOException {
        credit_card_in_list_visibility_validation();
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        credit_card_view_visibility_validation();
        billing_summarized_contact_info_visibility_validation();
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        billing_contact_info_content_validation();
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * in the returning shopper cards list.
     */
    public void credit_card_in_list_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_in_list_visibility_validation("5288", "12/26");
    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing an existing credit card in returning shopper.
     */
    public void credit_card_view_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_view_visibility_validation("5288", "12/26");
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    public void billing_summarized_contact_info_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation(R.id.billingViewSummarizedComponent, "RU",
                false, false);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    public void billing_contact_info_content_validation() throws IOException {
        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation", applicationContext, R.id.billingViewComponent, "RU", false, false);
    }

}
