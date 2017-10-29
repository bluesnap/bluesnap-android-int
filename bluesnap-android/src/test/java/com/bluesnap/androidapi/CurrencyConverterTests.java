package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.ExchangeRate;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.loopj.android.http.AsyncHttpClient;

import junit.framework.Assert;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BufferedHeader;

import static com.bluesnap.androidapi.TestDoken.SANDBOX_PASS;
import static com.bluesnap.androidapi.TestDoken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.androidapi.TestDoken.SANDBOX_USER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CurrencyConverterTests {


    private static final String TAG = CurrencyConverterTests.class.getSimpleName();
    private BlueSnapService blueSnapService;
    private String merchantToken;
    private TokenProvider tokenProvider;

    public CurrencyConverterTests() {
        ShadowLog.stream = System.out;
        System.setProperty("robolectric.logging", "stdout");
    }


    public static String extractTokenFromHeaders(cz.msebera.android.httpclient.Header[] headers) {
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

    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:80");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                };


    }

    final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setBasicAuth(SANDBOX_USER,SANDBOX_PASS);
        httpClient.post("http://localhost/"+SANDBOX_TOKEN_CREATION,new

    TextHttpResponseHandler() {

        @Override
        public void onFailure ( int statusCode, Header[] headers, String responseString, Throwable
        throwable){
            //Log.e(TAG, responseString, throwable);
            ShadowLog.e(TAG, "get sandbox token failed" + responseString, throwable);
            tokenServiceInterface.onServiceFailure();
        }

        @Override
        public void onSuccess ( int statusCode, Header[] headers, String responseString){
            //merchantToken = DemoTransactions.extractTokenFromHeaders(headers);
            ShadowLog.i(TAG, "Got token request from server, extracting token.");
            merchantToken = extractTokenFromHeaders(headers);
            tokenServiceInterface.onServiceSuccess();
        }

    });


}


    @Before
    public void getToken() {
        ShadowLog.i(TAG, "starting token provider");
        tokenProvider = new TokenProvider() {
            @Override
            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {

                merchantTokenService(new TokenServiceInterface() {
                    @Override
                    public void onServiceSuccess() {
                        //change the expired token
                        ShadowLog.i(TAG, "Got token");
                        tokenServiceCallback.complete(merchantToken);
                    }

                    @Override
                    public void onServiceFailure() {
                        ShadowLog.e(TAG, "Token failure");
                    }
                });
            }
        };
//


    }

    @Test(timeout = 120000)
    public void TestConversionService() throws InterruptedException {
        while (merchantToken == null) {
            Thread.sleep(2000);
            ShadowLog.i(TAG, "Waiting for token");
//            ShadowApplication.runBackgroundTasks();
//            Robolectric.flushBackgroundThreadScheduler(); //from 3.0


            tokenProvider.getNewToken(new TokenServiceCallback() {
                @Override
                public void complete(String newToken) {
                    ShadowLog.i(TAG, "Got token");
                    merchantToken = newToken;
                }
            });
        }

        blueSnapService = BlueSnapService.getInstance();
        blueSnapService.setup(merchantToken);
        blueSnapService.updateRates(new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                ShadowLog.d(TAG, "Got rates callback");
                ArrayList<ExchangeRate> ratesArray = blueSnapService.getRatesArray();
                Assert.assertNotNull(ratesArray.get(0));
            }

            @Override
            public void onFailure() {
                fail("failed to get rates");
            }
        });
        while (blueSnapService.getRatesArray() == null) {
            Thread.sleep(2000);
            ShadowLog.i(TAG, "Waiting for rates");

        }
        Double usdPrice = 30.5;
        Double ILSPrice = blueSnapService.convertPrice(usdPrice, "USD", "ILS");
        Double reconvertedUSDPrice = blueSnapService.convertPrice(ILSPrice, "ILS", "USD");
        assertEquals(usdPrice, reconvertedUSDPrice);

    }
}