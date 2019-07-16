package com.bluesnap.androidapi;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BS3DSAuthResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CardinalJWT;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.CardinalManager;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oz on 10/30/17.
 */

public class CardinalAPITest extends BSAndroidTestsBase {
    private static final String TAG = CardinalAPITest.class.getSimpleName();

    static final String CARD_NUMBER_3DS_CARDIANL_CARD = "4000000000000002"; //Other card numbers fail in tests
    static final String CARDINAL_CARD_CVV = "123";
    static final String CARDINAL_CARD_EXP = "01/2022";

    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, true);
    private Instrumentation mInstrumentation;
    private BluesnapCheckoutActivity mActivity;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityRule.getActivity();
    }


    @Test
    public void cardinal_token_tests() throws Exception {
        CardinalJWT cardinalJWT;
        CardinalManager cardinalManager = CardinalManager.getInstance();
        final CreditCard card = new CreditCard();

        card.update(CARD_NUMBER_3DS_CARDIANL_CARD, CARDINAL_CARD_EXP, CARDINAL_CARD_CVV);
        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD");
        blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.configureCardinal(getTestContext());
        cardinalJWT = cardinalManager.createCardinalJWT();
        cardinalManager.initCardinal(card);
        //TODO: wait for callback/event after calling initCardinal()
        assertTrue(cardinalJWT.getJWT().length() > 10);
//        cardinalManager.init(cardinalJWT);
    }


//    @Test(timeout = 20000)
    @Test
    public void cardinal_tx_test() throws Exception {
        Log.d(TAG, "starting test");
        Context thisTestContext = mActivity.getApplicationContext();
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
        blueSnapService.setSdkRequest(sdkRequest);
        cardinalManager.createCardinalJWT();
        cardinalManager.initCardinal(purchaseDetails.getCreditCard());
        //TODO: wait for callback/event after calling initCardinal()
        BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
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

        AtomicBoolean waitingForIntent = new AtomicBoolean(false);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got broadcastReceiver intent");
                    waitingForIntent.set(false);
            }
        };

        BlueSnapLocalBroadcastManager.registerReceiver(getTestContext(), CardinalManager.CARDINAL_VALIDATED, broadcastReceiver);

        cardinalManager.process(authResponse,mActivity , purchaseDetails);

        while (!waitingForIntent.get()) {
            Log.d(TAG, "Waiting for br");
            Thread.sleep(500);
        }

    }
}



