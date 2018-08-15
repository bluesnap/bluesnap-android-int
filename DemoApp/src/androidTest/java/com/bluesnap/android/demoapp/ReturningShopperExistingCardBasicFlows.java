package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;

import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by sivani on 14/08/2018.
 */
@RunWith(AndroidJUnit4.class)

public class ReturningShopperExistingCardBasicFlows extends BasicTransactionsFlows {
    @Test
    public void returning_shopper_minimal_billing_basic_flow_transaction() {
        isReturningShoppper = true;
        returning_shopper_card_basic_flow_transaction(0);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_minimal_billing_with_email_basic_flow_transaction() {
        isReturningShoppper = true;
        withEmail = true;
        returning_shopper_card_basic_flow_transaction(1);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_basic_flow_transaction() {
        isReturningShoppper = true;
        withShipping = true;
        returning_shopper_card_basic_flow_transaction(2);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_minimal_billing_with_shipping_with_email_basic_flow_transaction() {
        isReturningShoppper = true;
        withShipping = true;
        withEmail = true;
        returning_shopper_card_basic_flow_transaction(3);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_full_billing_basic_flow_transaction() {
        isReturningShoppper = true;
        fullInfo = true;
        returning_shopper_card_basic_flow_transaction(4);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_full_billing_with_email_basic_flow_transaction() {
        isReturningShoppper = true;
        fullInfo = true;
        withEmail = true;
        returning_shopper_card_basic_flow_transaction(5);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_basic_flow_transaction() {
        isReturningShoppper = true;
        fullInfo = true;
        withShipping = true;
        returning_shopper_card_basic_flow_transaction(6);
        get_shopper_after_transaction();
    }

    @Test
    public void returning_shopper_full_billing_with_shipping_with_email_basic_flow_transaction() {
        isReturningShoppper = true;
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        returning_shopper_card_basic_flow_transaction(7);
        get_shopper_after_transaction();
    }


    /**
     * This test does an end-to-end existing card of a returning shopper flow
     * for all 8 options: with/without full billing, shipping, email.
     */
    public void returning_shopper_card_basic_flow_transaction(int returningShopperIndex) {
        start_demo_purchase(returningShopperIndex);
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        existing_card_edit_info();

        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finish_demo_purchase(sdkResult, returningShopperIndex);
    }

    public void existing_card_edit_info() {
        //fill in info in billing and continue to shipping or paying
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, "Canada"); //TODO: Include the country value in contactInfo object (in addition to the key)
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountry(), fullInfo, withEmail, ContactInfoTesterCommon.editBillingContactInfo);
        TestUtils.go_back_to_credit_card_in_returning_shopper(true, R.id.returningShopperBillingFragmentButtonComponentView);

        if (withShipping) {
            if (billingCountryKey.equals("US")) //updating purchaseAmount to include tax
                purchaseAmount *= 1.05;

            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            ContactInfoTesterCommon.changeCountry(R.id.returningShoppershippingViewComponent, "United States");
            ContactInfoTesterCommon.fillInContactInfo(R.id.returningShoppershippingViewComponent, ContactInfoTesterCommon.editShippingContactInfo.getCountry(), true, false, ContactInfoTesterCommon.editShippingContactInfo);
            TestUtils.go_back_to_credit_card_in_returning_shopper(true, R.id.returningShopperShippingFragmentButtonComponentView);
        }

        onView(withId(R.id.buyNowButton)).perform(click());
    }

}
