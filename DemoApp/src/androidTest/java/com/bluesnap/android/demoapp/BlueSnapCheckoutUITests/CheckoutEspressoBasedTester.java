package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests;

import android.content.Context;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutReturningShopperTests.ReturningShoppersFactory;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.android.demoapp.UIAutoTestingBlueSnapService;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Rule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
    protected double taxPercent;
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

    // basic new shopper checkout
    protected void checkoutSetup() throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(false, "", true, false, false, false);
    }

    // basic returning shopper checkout
    protected void checkoutSetup(boolean forReturningShopper) throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(forReturningShopper, "", true, false, false, false);
    }

    // all merchant configurations
    protected void checkoutSetup(boolean allowCurrencyChange, boolean hideStoreCard, boolean disableGooglePay, boolean activate3DS) throws BSPaymentRequestException, InterruptedException, JSONException {
        checkoutSetup(false, "", allowCurrencyChange, hideStoreCard, disableGooglePay, activate3DS);
    }

    protected void checkoutSetup(boolean forReturningShopper, String returningShopperId, boolean allowCurrencyChange,
                                 boolean hideStoreCard, boolean disableGooglePay, boolean activate3DS) throws BSPaymentRequestException, InterruptedException, JSONException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        sdkRequest.setAllowCurrencyChange(allowCurrencyChange);
        sdkRequest.setHideStoreCardSwitch(hideStoreCard);
        sdkRequest.setGooglePayActive(!disableGooglePay);
        sdkRequest.setActivate3DS(activate3DS);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);
        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, forReturningShopper, returningShopperId);
        returningShopper = uIAutoTestingBlueSnapService.getReturningShopper();
        applicationContext = uIAutoTestingBlueSnapService.applicationContext;
        defaultCountryKey = uIAutoTestingBlueSnapService.getDefaultCountryKey();
        defaultCountryValue = uIAutoTestingBlueSnapService.getDefaultCountryValue();
    }

    public void new_card_basic_flow_transaction() throws InterruptedException {
        new_card_basic_flow_transaction(false);
    }

    public void new_card_basic_flow_transaction(boolean storeCard) throws InterruptedException {
        //ƒintending(hasExtraWithKey(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT));

        int buttonComponent = (shopperCheckoutRequirements.isShippingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling()) ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;
        //onView(withId(R.id.newCardButton)).perform(click());

        new_card_basic_fill_info(storeCard);

        TestUtils.pressBuyNowButton(buttonComponent);
//        sdkResult = BlueSnapService.getInstance().getSdkResult();
        uIAutoTestingBlueSnapService.finishDemoPurchase(shopperCheckoutRequirements, storeCard);
    }

    public void new_card_basic_fill_info(boolean storeCard) {
        new_card_basic_fill_info(storeCard, false, null);
    }

    public void new_card_basic_fill_info(TestingShopperCreditCard creditCard) {
        new_card_basic_fill_info(false, false, creditCard);
    }

    public void new_card_basic_fill_info(boolean storeCard, boolean storeCardIsMandatory, TestingShopperCreditCard creditCard) {
        if (shopperCheckoutRequirements.isShippingRequired() && shopperCheckoutRequirements.isFullBillingRequired() && !shopperCheckoutRequirements.isShippingSameAsBilling())
            TestUtils.setShippingSameAsBillingSwitch(false);

        //fill in info in billing and continue to shipping or paying
        if (creditCard != null) {
            CreditCardLineTesterCommon.fillInCCLineWithValidCard(creditCard);
        } else {
            CreditCardLineTesterCommon.fillInCCLineWithValidCard();
        }

        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryKey(), shopperCheckoutRequirements.isFullBillingRequired(), shopperCheckoutRequirements.isEmailRequired());

        if (storeCardIsMandatory) {
            CreditCardVisibilityTesterCommon.check_store_card_mandatory("check_store_card_mandatory");
        }

        if (storeCard) {
            onView(withId(R.id.storeCardSwitch)).perform(swipeRight());
        }

        if (shopperCheckoutRequirements.isShippingRequired()) {
            if (shopperCheckoutRequirements.isShippingSameAsBilling()) { //updating roundedPurchaseAmount to include tax since billing country is US
                updatePurchaseAmountForTax();
            } else { //continue to fill in shipping
                TestUtils.pressBuyNowButton();
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

        existing_card_edit_info();
        TestUtils.pressBuyNowButton(R.id.returningShppoerCCNFragmentButtonComponentView);
//        sdkResult = blueSnapService.getSdkResult();
        uIAutoTestingBlueSnapService.finishDemoPurchase(shopperCheckoutRequirements, true);
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
        //updating purchase amount to include tax
        uIAutoTestingBlueSnapService.setPurchaseAmount(uIAutoTestingBlueSnapService.getPurchaseAmount() * (1 + taxPercent));
    }

    public void returning_shopper_with_existing_credit_card_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        //make transaction to create a new shopper
        uIAutoTestingBlueSnapService.createVaultedShopper(true);

        //setup sdk for the returning shopper
        uIAutoTestingBlueSnapService.returningShopperSetUp(shopperCheckoutRequirements, true);

        //make a transaction with the returning shopper
        returning_shopper_card_basic_flow_transaction();
    }

    public void returning_shopper_with_new_credit_card_basic_flow_transaction() throws BSPaymentRequestException, InterruptedException, JSONException {
        //create a new shopper without cc
        uIAutoTestingBlueSnapService.createVaultedShopper(false);

        //setup sdk for the returning shopper
        uIAutoTestingBlueSnapService.returningShopperSetUp(shopperCheckoutRequirements, false);

        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());

        //make a transaction with the returning shopper
        new_card_basic_flow_transaction();
    }

}
