package com.bluesnap.androidapi;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
public class CurrencyConverterTests extends BSAndroidTestsBase {


    private static final String TAG = CurrencyConverterTests.class.getSimpleName();


    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    //    public CurrencyConverterTests() {
    ////        ShadowLog.stream = System.out;
    //        System.setProperty("robolectric.logging", "stdout");
    //    }


    @Before
    public void setup() throws InterruptedException {
        super.getToken();
        Log.i(TAG, "=============== Starting rates service tests ==================");
    }


    @Test
    public void convert_USD_to_ILS_and_Back() throws InterruptedException, BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD");

        blueSnapService.setSdkRequest(sdkRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, "USD", "ILS");
        Double reconvertedPrice = blueSnapService.convertPrice(convertedOncePrice, "ILS", "USD");
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedPrice));

    }

    @Test
    public void convert_ILS_to_EUR_and_Back() throws InterruptedException, BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "ILS");

        blueSnapService.setSdkRequest(sdkRequest);
        Double convertedOncePRice = blueSnapService.convertPrice(amount, "ILS", "EUR");
        Double reconvertedPrice = blueSnapService.convertPrice(convertedOncePRice, "EUR", "ILS");
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedPrice));
    }

    @Test
    public void convert_ILS_to_EUR_to_GBP_and_Back() throws InterruptedException, BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "ILS");

        sdkRequest.setBase();
        blueSnapService.setSdkRequest(sdkRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, "ILS", "EUR");
        Double convertedTwicePrice = blueSnapService.convertPrice(convertedOncePrice, "EUR", "GBP");
        Double reconvertedUSDPrice = blueSnapService.convertPrice(convertedTwicePrice, "GBP", "ILS");
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedUSDPrice));
    }

    @Test
    public void convert_EUR_to_USD() throws InterruptedException, BSPaymentRequestException {

        Double amount = 10D;
        SdkRequest sdkRequest = new SdkRequest(amount, "EUR");

        sdkRequest.setBase();
        blueSnapService.setSdkRequest(sdkRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, "EUR", "USD");
//        assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
        assertEquals("12.88", String.format("%.2f", convertedOncePrice));
    }

    @Test
    public void convert_EUR_to_ILS_to_USD() throws InterruptedException, BSPaymentRequestException {

        Double amount = 10.7D;
        SdkRequest sdkRequest = new SdkRequest(amount, "EUR");

        sdkRequest.setBase();
        blueSnapService.setSdkRequest(sdkRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, "EUR", "ILS");
        Double convertedTwicePrice = blueSnapService.convertPrice(convertedOncePrice, "ILS", "USD");
        assertEquals("13.78", String.format("%.2f", convertedTwicePrice));
    }



    @Test
    public void non_existing_currency_code() throws InterruptedException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "SOMETHING_BAD");

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            Double ILSPrice = blueSnapService.convertPrice(amount, "SOMETHING_BAD", "ILS");
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("Currency not found", e.getMessage());
        }

    }

    @Test
    public void null_currency_code() throws InterruptedException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, null);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            Double ILSPrice = blueSnapService.convertPrice(amount, "SOMETHING_BAD", "ILS");
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("Invalid currency", e.getMessage());
        }

    }
}