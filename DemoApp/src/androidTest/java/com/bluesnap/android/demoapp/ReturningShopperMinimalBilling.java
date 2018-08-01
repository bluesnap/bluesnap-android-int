package com.bluesnap.android.demoapp;

import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.is;

/**
 * Created by sivani on 28/07/2018.
 */

@RunWith(AndroidJUnit4.class)

public class ReturningShopperMinimalBilling extends EspressoBasedTest {
    private static final String RETURNING_SHOPPER_ID_MIN_BILLING = BluesnapCheckoutActivity.class.getSimpleName();
    ShopperContactInfo billingContactInfo = new ShopperContactInfo("La Fleur", "test@sdk.com",
            "New York", "555 Broadway street", "New York", "3abc 324a", "US");

    ShopperContactInfo shippingContactInfo = new ShopperContactInfo("Taylor Love", "email@test.com",
            "CityTest", "AddressTest", "RJ", "12345", "BR");


    private String cardLastDigit;

    public ReturningShopperMinimalBilling() {
        super("?shopperId=22828965");
    }

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() throws InterruptedException, BSPaymentRequestException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        //sdkRequest.setBillingRequired(true);
        sdkRequest.setShippingRequired(true);
        setupAndLaunch(sdkRequest);
        int cardPosition = randomTestValuesGenerator.randomReturningShopperCardPosition();
        //cardLastDigit = TestUtils.getText(withId(R.id.oneLineCCViewComponentsListView));
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());

    }

    /**
     * This test verifies that the all credit card fields are displayed as they should
     * when choosing an existing credit card in returning shopper.
     */
    @Test
    public void credit_card_view_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.credit_card_view_visibility_validation("5288", "12/26");
    }

    /**
     * This test verifies that the summarized billing contact info is displayed
     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
     */
    @Test
    public void billing_summarized_contact_info_visibility_validation() {
        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation(R.id.billingViewSummarizedComponent,
                false, false, billingContactInfo);
    }

    /**
     * This test verifies that the billing contact info presents the correct
     * content when pressing the billing edit button in returning shopper.
     */
    @Test
    public void billing_contact_info_content_validation() throws InterruptedException, IOException {
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());

        //verify info has been saved
        ContactInfoTesterCommon.contact_info_content_validation("billing_contact_info_content_validation", applicationContext, R.id.billingViewComponent, false, false);
    }

//    /**
//     * This test verifies that the summarized shipping contact info is displayed
//     * according to minimal billing with shipping when choosing an existing credit card in returning shopper.
//     */
//    @Test
//    public void shipping_summarized_contact_info_visibility_validation() {
//        ReturningShopperVisibilityTesterCommon.summarized_contact_info_visibility_validation(R.id.shippingViewSummarizedComponent,
//                true, false, shippingContactInfo);
//    }
//
//    /**
//     * This test verifies that the shipping contact info presents the correct
//     * content when pressing the billing edit button in returning shopper.
//     */
//    @Test
//    public void shipping_contact_info_content_validation() throws InterruptedException, IOException {
//        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
//
//        //verify info has been saved
//        ContactInfoTesterCommon.contact_info_content_validation(applicationContext, R.id.returningShoppershippingViewComponent, true, false);
//    }

}
