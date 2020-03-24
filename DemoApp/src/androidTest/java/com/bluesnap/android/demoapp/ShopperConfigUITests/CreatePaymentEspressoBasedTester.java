package com.bluesnap.android.demoapp.ShopperConfigUITests;

import androidx.test.rule.ActivityTestRule;

import com.bluesnap.android.demoapp.BlueSnapCheckoutUITests.WebViewUITests.PayPalWebViewTests;
import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.android.demoapp.UIAutoTestingBlueSnapService;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.views.activities.BluesnapCreatePaymentActivity;

import org.json.JSONException;
import org.junit.Rule;

/**
 * Created by sivani on 06/09/2018.
 */

public class CreatePaymentEspressoBasedTester {
    String RETURNING_SHOPPER_FULL_BILLING_WITH_SHIPPING_CREDIT_CARD = "22973121";
    String RETURNING_SHOPPER_PAY_PAL = "23071553";

    protected String checkoutCurrency;
    protected double purchaseAmount;

    protected TestingShopperCheckoutRequirements shopperCheckoutRequirements;

    PayPalWebViewTests payPalWebViewTests = new PayPalWebViewTests();

    @Rule
    public ActivityTestRule<BluesnapCreatePaymentActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCreatePaymentActivity.class, false, false);

    protected UIAutoTestingBlueSnapService<BluesnapCreatePaymentActivity> uIAutoTestingBlueSnapService = new UIAutoTestingBlueSnapService<>(mActivityRule);

    public CreatePaymentEspressoBasedTester() {
        checkoutCurrency = uIAutoTestingBlueSnapService.getCheckoutCurrency();
        purchaseAmount = uIAutoTestingBlueSnapService.getPurchaseAmount();
    }

    protected void createPaymentSetup(String VaultedShopperID) throws BSPaymentRequestException, InterruptedException, JSONException {
        SdkRequest sdkRequest = new SdkRequest(purchaseAmount, checkoutCurrency);
        uIAutoTestingBlueSnapService.setSdk(sdkRequest, shopperCheckoutRequirements);

        uIAutoTestingBlueSnapService.setupAndLaunch(sdkRequest, true, VaultedShopperID);
    }
}
