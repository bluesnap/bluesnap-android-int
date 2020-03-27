package com.bluesnap.androidapi;

import android.util.Log;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.KountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Semaphore;

import static java.net.HttpURLConnection.HTTP_OK;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntegrationTestsHelper {
    private static String TAG;
    BlueSnapService blueSnapService = BlueSnapService.getInstance();

    public IntegrationTestsHelper(String tag) {
        TAG = tag;
    }

    public void endToEndCreditCardCheckoutFlow(Double amount, String currencyNameCode, String creditCard) throws InterruptedException {

        // Initialize billing info
        final BillingContactInfo billingContactInfo = new BillingContactInfo();
        billingContactInfo.setFullName("John Doe");

        // Initialize card info
        final CreditCard card = new CreditCard();
        String number = creditCard;
        card.update(number, "11/25", "123");

        // Initialize PurchaseDetails
        final PurchaseDetails purchaseDetails = new PurchaseDetails();
        purchaseDetails.setBillingContactInfo(billingContactInfo);
        purchaseDetails.setCreditCard(card);
        purchaseDetails.setStoreCard(true);

        //assertTrue("this should be a valid luhn", BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
//        assertTrue(BlueSnapValidator.creditCardNumberValidation(CARD_NUMBER_VALID_LUHN_UNKNOWN_TYPE));
        assertTrue(BlueSnapValidator.creditCardFullValidation(card));
        assertNotNull(card.getCardType());
        assertFalse(card.getCardType().isEmpty());

        try {
            BlueSnapHTTPResponse blueSnapHTTPResponse = blueSnapService.submitTokenizedDetails(purchaseDetails);
            assertEquals(HTTP_OK, blueSnapHTTPResponse.getResponseCode());

            JSONObject jsonObject = new JSONObject(blueSnapHTTPResponse.getResponseString());
            String Last4 = jsonObject.getString("last4Digits");
            String ccType = jsonObject.getString("ccType");
            assertEquals("MASTERCARD", ccType);
            assertEquals("1116", Last4);

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
            fail("Exceptions while parsing response");
        }

        SdkResult sdkResult = blueSnapService.getSdkResult();
        sdkResult.setAmount(amount);
        sdkResult.setCurrencyNameCode(currencyNameCode);
        sdkResult.setToken(blueSnapService.getBlueSnapToken().getMerchantToken());
        sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());

        final Semaphore semaphore2 = new Semaphore(1);
        semaphore2.acquire();

        // making a transaction
        IntegrationTestsDemoTransactions transactions = IntegrationTestsDemoTransactions.getInstance();
        transactions.createCreditCardTransaction(sdkResult, new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Transaction success");
                semaphore2.release();
            }

            @Override
            public void onFailure() {
                semaphore2.release();
                fail("Failed to make a transaction");
            }
        });

        do {
            Thread.sleep(1000);
            Log.i(TAG, "Waiting for card transaction to finish");
        } while (!semaphore2.tryAcquire());

        Log.i(TAG, "Done");


    }
}
