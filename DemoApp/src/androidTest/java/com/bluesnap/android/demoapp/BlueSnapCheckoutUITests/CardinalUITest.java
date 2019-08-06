package com.bluesnap.android.demoapp.BlueSnapCheckoutUITests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.bluesnap.android.demoapp.TestingShopperCheckoutRequirements;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.CardinalManager;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CardinalUITest extends CheckoutEspressoBasedTester {

    private static final String TAG = CardinalUITest.class.getSimpleName();

    static final String CARD_NUMBER_3DS_CARDIANL_CARD = "4000000000000002"; //Other card numbers fail in tests
    static final String CARDINAL_CARD_CVV = "123";
    static final String CARDINAL_CARD_EXP = "01/2022";

    @Test
    public void cardinal_tx_test() throws Exception {
        Log.d(TAG, "starting test");
        shopperCheckoutRequirements = new TestingShopperCheckoutRequirements(true, true, true, false);

        checkoutSetup();

//        Activity mActivity = mActivityRule.getActivity();
        Context thisTestContext = mActivityRule.getActivity().getApplicationContext();
        assertNotNull(thisTestContext);

        final PurchaseDetails purchaseDetails = new PurchaseDetails();
        final BillingContactInfo billingContactInfo = new BillingContactInfo();
        purchaseDetails.setBillingContactInfo(billingContactInfo);
        billingContactInfo.setFullName("John Doe");
        final CreditCard card = new CreditCard();
        Double amount = 30.5D;
        String currency = "USD";
        card.update(CARD_NUMBER_3DS_CARDIANL_CARD, CARDINAL_CARD_EXP, CARDINAL_CARD_CVV);
        purchaseDetails.setCreditCard(card);
        CardinalManager cardinalManager = CardinalManager.getInstance();
        cardinalManager.configureCardinal(thisTestContext);

        SdkRequest sdkRequest = new SdkRequest(amount, currency);
        uIAutoTestingBlueSnapService.blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.setCardinalJWT();
        cardinalManager.initCardinal(purchaseDetails.getCreditCard(), mActivityRule.getActivity());
        BlueSnapHTTPResponse blueSnapHTTPResponse = uIAutoTestingBlueSnapService.blueSnapService.submitTokenizedDetails(purchaseDetails);
        assertEquals(HTTP_OK, blueSnapHTTPResponse.getResponseCode());
        JSONObject jsonObject = new JSONObject(blueSnapHTTPResponse.getResponseString());
        String Last4 = jsonObject.getString("last4Digits");
        String ccType = jsonObject.getString("ccType");
        assertEquals("VISA", ccType);
        assertEquals("0002", Last4);
        BS3DSAuthResponse authResponse = cardinalManager.authWith3DS("USD", amount);
        Log.d(TAG, "Got auth response");
        assertEquals("CHALLENGE_REQUIRED", authResponse.getEnrollmentStatus());
        assertNotNull("No transactionID from cardinal", authResponse.getTransactionId());
        // assertNotNull("test activity is null", mActivity);

        AtomicBoolean waitingForIntent = new AtomicBoolean(true);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got broadcastReceiver intent");
                waitingForIntent.set(false);
            }
        };

        BlueSnapLocalBroadcastManager.registerReceiver(thisTestContext, CardinalManager.CARDINAL_VALIDATED, broadcastReceiver);

        cardinalManager.process(authResponse,mActivityRule.getActivity() , purchaseDetails);

        while (waitingForIntent.get()) {
            Log.d(TAG, "Waiting for br");
            Thread.sleep(500);
        }
        assertFalse(waitingForIntent.get());
    }
}
