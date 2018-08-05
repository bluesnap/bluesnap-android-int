package com.bluesnap.androidapi;

import android.os.Handler;
import android.os.Looper;
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

import static com.bluesnap.androidapi.TestDoken.*;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static junit.framework.Assert.fail;


/**
 * Created by oz on 10/29/17.
 */

public class BSAndroidTestsBase {

    private static final String TAG = BSAndroidTestsBase.class.getSimpleName();
    BlueSnapService blueSnapService;
    private String merchantToken;
    private TokenProvider tokenProvider;
    private String baseCurrency;

    BSAndroidTestsBase(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    BSAndroidTestsBase() {
        this.baseCurrency = "USD";
    }

    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

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
                blueSnapService.setup(merchantToken, tokenProvider, baseCurrency, null, new BluesnapServiceCallback() {

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
                        fail("failed to get rates");
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

//    public static String extractTokenFromHeaders(Header[] headers) {
//        String token = null;
//        for (Header hr : headers) {
//            BufferedHeader bufferedHeader = (BufferedHeader) hr;
//            if (bufferedHeader.getName().equals("Location")) {
//                String path = bufferedHeader.getValue();
//                token = path.substring(path.lastIndexOf('/') + 1);
//            }
//        }
//        return token;
//    }

}
