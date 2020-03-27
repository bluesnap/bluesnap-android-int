package com.bluesnap.androidapi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by oz on 10/30/17.
 */

public class TokenExpirationTests extends BSAndroidIntegrationTestsBase {
    private static final String TAG = TokenExpirationTests.class.getSimpleName();
    final String CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED = "5568111111111116";
    final IntegrationTestsHelper IntegrationTestsHelper = new IntegrationTestsHelper(TAG);

    @Test
    public void sdk_init_tokenize_expired_test() throws BSPaymentRequestException, InterruptedException {

        makeTokenExpired();
        String token = blueSnapService.getBlueSnapToken().getMerchantToken();

        final Semaphore semaphore2 = new Semaphore(1);
        semaphore2.acquire();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                blueSnapService.setup(token, tokenProvider, baseCurrency, getTestContext(), new BluesnapServiceCallback() {

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

    public void makeTokenExpired() throws InterruptedException {

        IntegrationTestsHelper.endToEndCreditCardCheckoutFlow(55.0, "USD", CARD_NUMBER_VALID_LUHN_MASTERCARD_FAKED);
    }

}



