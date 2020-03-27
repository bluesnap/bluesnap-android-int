package com.bluesnap.androidapi;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.test.platform.app.InstrumentationRegistry;
import android.util.Base64;
import android.util.Log;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import junit.framework.Assert;
import org.junit.Before;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static com.bluesnap.androidapi.SandboxToken.*;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static junit.framework.Assert.fail;


/**
 * Created by oz on 10/29/17.
 */

public class BSAndroidIntegrationTestsBase {

    private static final String TAG = BSAndroidIntegrationTestsBase.class.getSimpleName();
    BlueSnapService blueSnapService;
    protected String merchantToken;
    protected TokenProvider tokenProvider;
    protected String baseCurrency;

    BSAndroidIntegrationTestsBase(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    BSAndroidIntegrationTestsBase() {
        this.baseCurrency = "USD";
    }

    protected void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        BlueSnapHTTPResponse post = HTTPOperationController.post(SANDBOX_URL + SANDBOX_TOKEN_CREATION, null, "application/json", "application/json", headerParams);
        if (post.getResponseCode() == HTTP_CREATED && post.getHeaders() != null) {
            String location = post.getHeaders().get("Location").get(0);
            merchantToken = location.substring(location.lastIndexOf('/') + 1);
            tokenServiceInterface.onServiceSuccess();

        } else {
            fail("Cannot get token for tests: " + post.getResponseCode());
            tokenServiceInterface.onServiceFailure();
        }

    }

    @Before
    public void getToken() throws InterruptedException {

        final Semaphore semaphore = new Semaphore(1);
        tokenProvider = new TokenProvider() {
            @Override
            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {

                merchantTokenService(new TokenServiceInterface() {
                    @Override
                    public void onServiceSuccess() {
                        //change the expired token
                        Log.i(TAG, "Got token");
                        tokenServiceCallback.complete(merchantToken);
                    }

                    @Override
                    public void onServiceFailure() {
                        Log.e(TAG, "Token failure");
                        fail("Token failure");
                        tokenServiceCallback.complete(null);
                    }
                });
            }
        };

        do {
            semaphore.acquire();
            tokenProvider.getNewToken(new TokenServiceCallback() {
                @Override
                public void complete(String newToken) {
                    Log.i(TAG, "Got token");
                    merchantToken = newToken;
                    semaphore.release();
                }
            });
            while (!semaphore.tryAcquire()) {
                Thread.sleep(1000);
                Log.i(TAG, "Waiting for test token");
            }
        } while (merchantToken == null);

        blueSnapService = BlueSnapService.getInstance();
        final Semaphore semaphore2 = new Semaphore(1);
        semaphore2.acquire();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                blueSnapService.setup(merchantToken, tokenProvider, baseCurrency, getTestContext() , new BluesnapServiceCallback() {

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
                        fail("failed to setup sdk");
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

    /**
     * Get a context for tests, requires android support libs
     * @return
     */
    Context getTestContext() {
      Context context = InstrumentationRegistry.getInstrumentation().getContext();
      return context;
    }

}
