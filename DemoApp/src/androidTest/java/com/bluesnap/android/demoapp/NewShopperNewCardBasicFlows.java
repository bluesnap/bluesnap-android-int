package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;

import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by sivani on 14/08/2018.
 */

//@RunWith(AndroidJUnit4.class)

public class NewShopperNewCardBasicFlows extends BasicTransactionsFlows {
    public void minimal_billing_basic_flow_transaction() {
        new_card_basic_flow_transaction(0);
        get_shopper_after_transaction();
    }

    public void minimal_billing_with_email_basic_flow_transaction() {
        withEmail = true;
        new_card_basic_flow_transaction(1);
        get_shopper_after_transaction();
    }

    public void minimal_billing_with_shipping_basic_flow_transaction() {
        withShipping = true;
        new_card_basic_flow_transaction(2);
        get_shopper_after_transaction();
    }
    public void minimal_billing_with_shipping_with_email_basic_flow_transaction() {
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction(3);
        get_shopper_after_transaction();
    }

    public void full_billing_basic_flow_transaction() {
        fullInfo = true;
        new_card_basic_flow_transaction(4);
        get_shopper_after_transaction();
    }

    public void full_billing_with_email_basic_flow_transaction() {
        fullInfo = true;
        withEmail = true;
        new_card_basic_flow_transaction(5);
        get_shopper_after_transaction();
    }

    public void full_billing_with_shipping_basic_flow_transaction() {
        fullInfo = true;
        withShipping = true;
        new_card_basic_flow_transaction(6);
        get_shopper_after_transaction();
    }

    public void full_billing_with_shipping_with_email_basic_flow_transaction() {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        new_card_basic_flow_transaction(7);
        get_shopper_after_transaction();
    }

    public void shipping_same_as_billing_basic_flow_transaction() {
        fullInfo = true;
        withShipping = true;
        withEmail = true;
        shippingSameAsBilling = true;
        new_card_basic_flow_transaction(-1);
        get_shopper_after_transaction();
    }

    public void change_currency_twice_back_to_usd_espresso_test() {
        start_demo_purchase(-1);
        onView(withId(R.id.newCardButton)).perform(click());
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, billingCountryValue);
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountryKey, fullInfo, withEmail);

        CurrencyChangeTesterCommon.changeCurrency("CAD");
        CurrencyChangeTesterCommon.changeCurrency("ILS");
        CurrencyChangeTesterCommon.changeCurrency(checkoutCurrency);
        onView(withId(R.id.buyNowButton)).perform(click());


        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finish_demo_purchase(sdkResult, -1);
    }

    /**
     * This test does an end-to-end new card flow for all 8 options:
     * with/without full billing, shipping, email.
     */
    public void new_card_basic_flow_transaction(int returningShopperIndex) {
        start_demo_purchase(-1);
        onView(withId(R.id.newCardButton)).perform(click());
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        new_card_basic_fill_info();

        SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();
        finish_demo_purchase(sdkResult, returningShopperIndex);
    }

    public void new_card_basic_fill_info() {
        if (shippingSameAsBilling)
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //fill in info in billing and continue to shipping or paying
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, billingCountryValue);
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, billingCountryKey, fullInfo, withEmail);

        onView(withId(R.id.buyNowButton)).perform(click());

        if (withShipping) {
            if (billingCountryKey.equals("US")) //updating purchaseAmount to include tax
                purchaseAmount *= 1.05; //TODO: add comment
            if (!shippingSameAsBilling) {
                ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, shippingCountryValue);
                ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, shippingCountryKey, true, false);
                onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(R.id.shippingButtonComponentView)))).perform(click());
            }
        }
    }
}
