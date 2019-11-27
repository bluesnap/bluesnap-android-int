package com.bluesnap.androidapi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by oz on 10/30/17.
 */

public class TokenExpirationTests extends BSAndroidTestsBase {
    private static final String TAG = TokenExpirationTests.class.getSimpleName();
    private static final String EXPIRED_TOKEN = "11b1bc4ac154e17f702ea5f8f63ca245c30ff8c79c74af0f37c8f2c7c579330b_";

    @Test
    public void sdk_init_tokenize_expired_test() throws BSPaymentRequestException, InterruptedException {

        final Double amount = 2D;
        final String currency = "USD";
        SdkRequest sdkRequest = new SdkRequest(amount, currency);
        blueSnapService.setSdkRequest(sdkRequest);

        final Semaphore semaphore2 = new Semaphore(1);
        semaphore2.acquire();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                blueSnapService.setup(EXPIRED_TOKEN, tokenProvider, baseCurrency, getTestContext(), new BluesnapServiceCallback() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Got rates callback");
                        ArrayList<Currency> ratesArray = blueSnapService.getRatesArray();
                        Assert.assertNotNull(ratesArray.get(0));
                        semaphore2.release();
                    }

                    @Override
                    public void onFailure() {
                        semaphore2.release();
                        Assert.fail("failed to setup sdk");
                    }
                });
            }
        }, 100);


        do {
            Thread.sleep(1000);
            Log.i(TAG, "Waiting for SDK configuration to finish");
        } while (!semaphore2.tryAcquire());

        Log.i(TAG, "Done");


    }

    /*private BlueSnapHTTPResponse createCreditCardTransaction(Double amount, String currency) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", amount);
            jsonObject.put("recurringTransaction", "ECOMMERCE");
            jsonObject.put("softDescriptor", "DescTest");
            jsonObject.put("currency", currency);
            jsonObject.put("pfToken", "d871c5f688caac79cbb63c555be3f7e205b8ef8b03a2f812a39a1ebbe3d7ec68_");
            jsonObject.put("cardTransactionType", "AUTH_CAPTURE");
        } catch (JSONException e) {
            fail("Exceptions while parsing response");
        }

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        return HTTPOperationController.post(SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, jsonObject.toString(), "application/json", "application/json", headerParams);
    }*/

}



