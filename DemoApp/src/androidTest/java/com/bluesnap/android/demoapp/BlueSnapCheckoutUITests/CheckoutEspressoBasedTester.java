package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests;

import android.content.Context;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShoppersFactory;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.UIAutoTestingBlueSnapService;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;


/**
 *
 */

public class CheckoutEspressoBasedTester {
    public Context applicationContext;

    protected String defaultCountryKey;
    protected String defaultCountryValue;
    protected String checkoutCurrency;
    protected double purchaseAmount;
    private double taxPercent;
    protected double taxAmount;
    protected ReturningShoppersFactory.TestingShopper returningShopper;
    protected TestingShopperCheckoutRequirements shopperCheckoutRequirements;

    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, false);

    protected UIAutoTestingBlueSnapService<BluesnapCheckoutActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

    public CheckoutEspressoBasedTester() {
        checkoutCurrency = uIAutoTestingBlueSnapService.getCheckoutCurrency();
        purchaseAmount = uIAutoTestingBlueSnapService.getPurchaseAmount();
        taxPercent = uIAutoTestingBlueSnapService.getTaxPercent();
        taxAmount = uIAutoTestingBlueSnapService.getTaxAmount();
    }


    protected void checkoutSetup() throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(false, "", true);
    }

    protected void checkoutSetup(boolean forReturningShopper) throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(forReturningShopper, "", true);
    }

    protected void checkoutSetup(boolean forReturningShopper, String returningShopperId, boolean allowCurrencyChange) throws BSPaymentRequestException, InterruptedException, JSONException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setAllowCurrencyChange(allowCurrencyChange);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);
        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, forReturningShopper, returningShopperId);
        returningShopper = uIAutoTestingBlueSnapService.getReturningShopper();
        applicationContext = uIAutoTestingBlueSnapService.applicationContext;
        defaultCountryKey = uIAutoTestingBlueSnapService.getDefaultCountryKey();
        defaultCountryValue = uIAutoTestingBlueSnapService.getDefaultCountryValue();
    }

    public void new_card_basic_flow_transaction() throws InterruptedException {
        //ƒintending(hasExtraWithKey(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT));

        int buttonComponent = (shopperCheckoutRequirements.isShippingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;
        //onView(withId(R.id.newCardButton)).perform(click());
        new_card_basic_fill_info();
        ViewInteraction viewInteraction = onView(allOf(withId(R.id.buyNowButton), isDescendantOfA(withId(buttonComponent))));
        viewInteraction.perform(click());
//        sdkResult = BlueSnapService.getInstance().getSdkResult();
        uIAutoTestingBlueSnapService.finish_demo_purchase(shopperCheckoutRequirements);
    }

    public void new_card_basic_fill_info() {
        if (shopperCheckoutRequirements.isShippingSameAsBilling())
            onView(withId(R.id.shippingSameAsBillingSwitch)).perform(swipeRight());

        //fill in info in billing and continue to shipping or paying
        CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryKey(), shopperCheckoutRequirements.isFullBillingRequired(), shopperCheckoutRequirements.isEmailRequired());


        if (shopperCheckoutRequirements.isShippingRequired()) {
            if (shopperCheckoutRequirements.isShippingSameAsBilling()) { //updating roundedPurchaseAmount to include tax since billing country is US
                updatePurchaseAmountForTax();
            } else { //continue to fill in shipping
                onView(withId(R.id.buyNowButton)).perform(click());
                ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryValue());
                ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryKey(), true, false);
            }
        }
    }

    /**
     * This test does an end-to-end existing card of a returning shopper flow
     * for all 8 options: with/without full billing, shipping, email.
     */
    public void returning_shopper_card_basic_flow_transaction() throws InterruptedException {
        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());

        //onView(withId(R.id.newCardButton)).perform(click());
        existing_card_edit_info();
        onView(withId(R.id.buyNowButton)).perform(click());
//        sdkResult = blueSnapService.getSdkResult();
        uIAutoTestingBlueSnapService.finish_demo_purchase(shopperCheckoutRequirements);
    }

    public void existing_card_edit_info() {
        //fill in info in billing and continue to shipping or paying
        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryKey(), shopperCheckoutRequirements.isFullBillingRequired(), shopperCheckoutRequirements.isEmailRequired(), ContactInfoTesterCommon.editBillingContactInfo);
        TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperBillingFragmentButtonComponentView);

        if (shopperCheckoutRequirements.isShippingRequired()) {
//            if (defaultCountryKey.equals("US") || isReturningShopper) //updating roundedPurchaseAmount to include tax
            updatePurchaseAmountForTax();

            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            ContactInfoTesterCommon.changeCountry(R.id.returningShoppershippingViewComponent, ContactInfoTesterCommon.editShippingContactInfo.getCountryValue());
            ContactInfoTesterCommon.fillInContactInfo(R.id.returningShoppershippingViewComponent, ContactInfoTesterCommon.editShippingContactInfo.getCountryKey(), true, false, ContactInfoTesterCommon.editShippingContactInfo);
            TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperShippingFragmentButtonComponentView);
        }
    }

    private void updatePurchaseAmountForTax() {
        purchaseAmount = purchaseAmount * (1 + taxPercent); //TODO: add comment
    }

    public void returning_shopper_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        //make transaction to create a new shopper
        new_card_basic_flow_transaction();

        //setup sdk for the returning shopper
        uIAutoTestingBlueSnapService.returningShopperSetUp(shopperCheckoutRequirements);

        //make a transaction with the returning shopper
        returning_shopper_card_basic_flow_transaction();
    }

}
