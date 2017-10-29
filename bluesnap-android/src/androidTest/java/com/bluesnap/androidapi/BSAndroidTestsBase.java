package com.bluesnap.androidapi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bluesnap.androidapi.models.ExchangeRate;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.junit.Before;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BufferedHeader;

import static com.bluesnap.androidapi.TestDoken.SANDBOX_PASS;
import static com.bluesnap.androidapi.TestDoken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.androidapi.TestDoken.SANDBOX_URL;
import static com.bluesnap.androidapi.TestDoken.SANDBOX_USER;
import static junit.framework.Assert.fail;


/**
 * Created by oz on 10/29/17.
 */

public class BSAndroidTestsBase {

    private static final String TAG = BSAndroidTestsBase.class.getSimpleName();
    private BlueSnapService blueSnapService;
    private String merchantToken;
    private TokenProvider tokenProvider;


    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

        final AsyncHttpClient httpClient = new SyncHttpClient();
        httpClient.setMaxRetriesAndTimeout(2, 20000);
        httpClient.setBasicAuth(SANDBOX_USER, SANDBOX_PASS);
        httpClient.post(SANDBOX_URL + SANDBOX_TOKEN_CREATION, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString, throwable);
                fail("Cannot get token for tests:" + throwable.getMessage());
                tokenServiceInterface.onServiceFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                merchantToken = extractTokenFromHeaders(headers);
                tokenServiceInterface.onServiceSuccess();
            }

        });


    }

    @Before
    public void getToken() throws InterruptedException {

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
                    }
                });
            }
        };
//

        while (merchantToken == null) {
            Thread.sleep(2000);
            Log.i(TAG, "Waiting for test token");
            tokenProvider.getNewToken(new TokenServiceCallback() {
                @Override
                public void complete(String newToken) {
                    Log.i(TAG, "Got token");
                    merchantToken = newToken;
                }
            });
        }

        blueSnapService = BlueSnapService.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                blueSnapService.setup(merchantToken);
                blueSnapService.updateRates(new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Got rates callback");
                        ArrayList<ExchangeRate> ratesArray = blueSnapService.getRatesArray();
                        Assert.assertNotNull(ratesArray.get(0));
                    }

                    @Override
                    public void onFailure() {
                        fail("failed to get rates");
                    }
                });
            }
        }, 100);


        while (blueSnapService.getRatesArray() == null) {
            Thread.sleep(20000);
            Log.i(TAG, "Waiting for rates");

        }

    }

    public static String extractTokenFromHeaders(Header[] headers) {
        String token = null;
        for (Header hr : headers) {
            BufferedHeader bufferedHeader = (BufferedHeader) hr;
            if (bufferedHeader.getName().equals("Location")) {
                String path = bufferedHeader.getValue();
                token = path.substring(path.lastIndexOf('/') + 1);
            }
        }
        return token;
    }


}
