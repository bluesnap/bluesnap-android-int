package com.bluesnap.android.demoapp.ShopperConfigUITests;

import android.support.test.rule.ActivityTestRule;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.UIAutoTestingBlueSnapService;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapCreatePaymentActivity;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;

import static java.lang.Thread.sleep;

/**
 * Created by sivani on 06/09/2018.
 */

public class CreatePaymentEspressoBasedTester {
    String RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING = "22973121";
    protected String checkoutCurrency;
    protected double purchaseAmount;

    protected TestingShopperCheckoutRequirements shopperCheckoutRequirements;

    @Rule
    public ActivityTestRule<BluesnapCreatePaymentActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCreatePaymentActivity.class, false, false);

    protected UIAutoTestingBlueSnapService<BluesnapCreatePaymentActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

    public CreatePaymentEspressoBasedTester() {
        checkoutCurrency = uIAutoTestingBlueSnapService.getCheckoutCurrency();
        purchaseAmount = uIAutoTestingBlueSnapService.getPurchaseAmount();
    }

    protected void createPaymentSetup() throws BSPaymentRequestException, InterruptedException, JSONException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);

        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, true, RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING);
    }

    /**
     * This test does a full billing with email and shipping
     * end-to-end create payment flow for the chosen card.
     * <p>
     * pre-condition: chosen card is TestingShopperCreditCard.VISA_CREDIT_CARD;
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws BSPaymentRequestException
     */
    @Test
    public void full_billing_with_email_with_shipping_create_payment_flow() throws InterruptedException, JSONException, BSPaymentRequestException {
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, false, true);
        createPaymentSetup();

        sleep(10000);

        uIAutoTestingBlueSnapService.createPaymentTransaction();
        //Assert.assertEquals("SDKResult credit card was charged", uIAutoTestingBlueSnapService.getTransactions().getCardLastFourDigits(), TestingShopperCreditCard.VISA_CREDIT_CARD.getCardLastFourDigits());

    }



}
