package com.bluesnap.android.demoapp.ShopperConfigUITests;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.ContactInfoTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardLineTesterCommon;
import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.CheckoutCommonTesters.CreditCardVisibilityTesterCommon;
import com.bluesnap.android.demoapp.R;
import com.bluesnap.android.demoapp.TestUtils;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.TestingShopperCreditCard;
import com.bluesnap.android.demoapp.UIAutoTestingBlueSnapService;
import com.bluesnap.androidapi.models.SdkRequestShopperRequirements;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapChoosePaymentMethodActivity;

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
 * Created by sivani on 30/08/2018.
 */

public class ChoosePaymentMethodEspressoBasedTester {
    protected String defaultCountryKey;
    protected String defaultCountryValue;
    protected String checkoutCurrency;

    protected TestingShopperCheckoutRequirements shopperCheckoutRequirements;

    @Rule
    public ActivityTestRule<BluesnapChoosePaymentMethodActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapChoosePaymentMethodActivity.class, false, false);

    protected UIAutoTestingBlueSnapService<BluesnapChoosePaymentMethodActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

    protected void choosePaymentSetup(boolean createShopper, boolean withCreditCard) throws BSPaymentRequestException, InterruptedException, JSONException {
        if (createShopper)
            uIAutoTestingBlueSnapService.createVaultedShopper(withCreditCard);

        SdkRequestShopperRequirements sdkRequest = new SdkRequestShopperRequirements();
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);

        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, true, uIAutoTestingBlueSnapService.getVaultedShopperId());
        defaultCountryKey = uIAutoTestingBlueSnapService.getDefaultCountryKey();
        defaultCountryValue = uIAutoTestingBlueSnapService.getDefaultCountryValue();
        checkoutCurrency = uIAutoTestingBlueSnapService.getCheckoutCurrency();
    }

    void chooseNewCardPaymentMethod(TestingShopperCreditCard creditCard) throws InterruptedException {
        //choose new card
        onView(ViewMatchers.withId(R.id.newCardButton)).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryValue());
        CreditCardLineTesterCommon.fillInCCLineWithValidCard(TestingShopperCreditCard.MASTERCARD_CREDIT_CARD);
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.billingContactInfo.getCountryKey(), shopperCheckoutRequirements.isFullBillingRequired(),
                shopperCheckoutRequirements.isEmailRequired());

        CreditCardVisibilityTesterCommon.check_store_card_mandatory("check_store_card_mandatory");

        onView(withId(R.id.storeCardSwitch)).perform(swipeRight());

        if (shopperCheckoutRequirements.isShippingRequired()) { //continue to shipping
            TestUtils.setShippingSameAsBillingSwitch(false);
            TestUtils.pressBuyNowButton();
            ContactInfoTesterCommon.changeCountry(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryValue());
            ContactInfoTesterCommon.fillInContactInfo(R.id.newShoppershippingViewComponent, ContactInfoTesterCommon.shippingContactInfo.getCountryKey(), true, false);
        }

        //submit the choice
        int buttonComponent = shopperCheckoutRequirements.isShippingRequired() ? R.id.shippingButtonComponentView : R.id.billingButtonComponentView;
        //onView(withId(R.id.newCardButton)).perform(click());
        TestUtils.pressBuyNowButton(buttonComponent);
        uIAutoTestingBlueSnapService.chosenPaymentMethodValidationInServer(shopperCheckoutRequirements, creditCard, false);

    }

    void chooseExistingCardPaymentMethod(TestingShopperCreditCard creditCard) throws InterruptedException {
        //choose existing credit card
//        onData((hasItem(hasItem(hasToString(containsString(cardNum)))))).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).perform(click());
//        String firstCard = uIAutoTestingBlueSnapService.blueSnapService.getsDKConfiguration()
//                .getShopper().getPreviousPaymentSources().getCreditCardInfos().get(0).getCreditCard().getCardLastFourDigits();
//
//        int cardIndex = firstCard.equals(creditCard.getCardLastFourDigits()) ? 0 : 1;

        onData(anything()).inAdapterView(withId(R.id.oneLineCCViewComponentsListView)).atPosition(0).perform(click());

        onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.billingViewSummarizedComponent)))).perform(click());
        ContactInfoTesterCommon.changeCountry(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryValue());
        ContactInfoTesterCommon.fillInContactInfo(R.id.billingViewComponent, ContactInfoTesterCommon.editBillingContactInfo.getCountryKey(),
                shopperCheckoutRequirements.isFullBillingRequired(), shopperCheckoutRequirements.isEmailRequired(), ContactInfoTesterCommon.editBillingContactInfo);
        TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperBillingFragmentButtonComponentView);

        if (shopperCheckoutRequirements.isShippingRequired()) { //continue to shipping
            onView(Matchers.allOf(withId(R.id.editButton), isDescendantOfA(withId(R.id.shippingViewSummarizedComponent)))).perform(click());
            ContactInfoTesterCommon.changeCountry(R.id.returningShoppershippingViewComponent, ContactInfoTesterCommon.editShippingContactInfo.getCountryValue());
            ContactInfoTesterCommon.fillInContactInfo(R.id.returningShoppershippingViewComponent, ContactInfoTesterCommon.editShippingContactInfo.getCountryKey(),
                    true, false, ContactInfoTesterCommon.editShippingContactInfo);
            TestUtils.goBackToCreditCardInReturningShopper(true, R.id.returningShopperShippingFragmentButtonComponentView);
        }

        //submit the choice
        TestUtils.pressBuyNowButton(R.id.returningShppoerCCNFragmentButtonComponentView);
        uIAutoTestingBlueSnapService.chosenPaymentMethodValidationInServer(shopperCheckoutRequirements, creditCard, false);
    }

    void choosePayPalPaymentMethod() throws InterruptedException {
        //choose paypal
        onView(withId(R.id.payPalButton)).perform(click());

        uIAutoTestingBlueSnapService.chosenPaymentMethodValidationInServer(false);

    }

}
