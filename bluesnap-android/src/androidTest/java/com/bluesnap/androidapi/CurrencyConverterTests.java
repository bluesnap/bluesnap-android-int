package com.bluesnap.androidapi;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
public class CurrencyConverterTests extends BSAndroidIntegrationTestsBase {


    private static final String TAG = CurrencyConverterTests.class.getSimpleName();


//    @After
//    public void keepRunning() throws InterruptedException {
//        Thread.sleep(1000);
//    }

    //    public CurrencyConverterTests() {
    ////        ShadowLog.stream = System.out;
    //        System.setProperty("robolectric.logging", "stdout");
    //    }


    @Before
    public void setup() {
        // No need to call getToken - it is already done by the parent class
        // super.getToken();
        Log.i(TAG, "=============== Starting CurrencyConverter tests ==================");
    }

    @Test
    public void convert_USD_to_ILS_and_Back() throws BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD", false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();
        blueSnapService.convertPrice(priceDetails, "ILS");
        Double convertedOncePrice = priceDetails.getAmount();
        blueSnapService.convertPrice(priceDetails, "USD");
        Double reconvertedPrice = priceDetails.getAmount();
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedPrice));
    }

    @Test
    public void convert_ILS_to_EUR_and_Back() throws BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "ILS", false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();
        blueSnapService.convertPrice(priceDetails, "EUR");
        Double convertedOncePRice = priceDetails.getAmount();
        blueSnapService.convertPrice(priceDetails, "ILS");
        Double reconvertedPrice = priceDetails.getAmount();
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedPrice));
    }

    @Test
    public void convert_ILS_to_EUR_to_GBP_and_Back() throws BSPaymentRequestException {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "ILS", false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();

        blueSnapService.convertPrice(priceDetails, "EUR");
        Double convertedOncePrice = priceDetails.getAmount();
        blueSnapService.convertPrice(priceDetails, "GBP");
        Double convertedTwicePrice = priceDetails.getAmount();
        blueSnapService.convertPrice(priceDetails, "ILS");
        Double reconvertedUSDPrice = priceDetails.getAmount();
        assertEquals(String.format("%.2f", amount), String.format("%.2f", reconvertedUSDPrice));
    }

    @Test
    public void convert_EUR_to_USD() throws BSPaymentRequestException {

        Double amount = 10D;
        SdkRequest sdkRequest = new SdkRequest(amount, "EUR", false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();

        blueSnapService.convertPrice(priceDetails, "USD");
        Double convertedOncePrice = priceDetails.getAmount();
//        assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
        assertEquals("12.88", String.format("%.2f", convertedOncePrice));
    }

    @Test
    public void convert_EUR_to_ILS_to_USD() throws BSPaymentRequestException {

        Double amount = 10.7D;
        SdkRequest sdkRequest = new SdkRequest(amount, "EUR", false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();

        blueSnapService.convertPrice(priceDetails, "ILS");
        Double convertedOncePrice = priceDetails.getAmount();
        blueSnapService.convertPrice(priceDetails, "USD");
        Double convertedTwicePrice = priceDetails.getAmount();
        assertEquals("13.78", String.format("%.2f", convertedTwicePrice));
    }


    @Test
    public void non_existing_currency_code() {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "SOMETHING_BAD", false, false, false);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            blueSnapService.convertPrice(priceDetails, "ILS");
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("null sdkRequest", e.getMessage());
        } catch (IllegalArgumentException e) {
            assertEquals("not an ISO 4217 compatible 3 letter currency representation", e.getMessage());
        }
    }

    @Test
    public void non_existing_new_currency_code() {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD", false, false, false);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            blueSnapService.convertPrice(priceDetails, "SOMETHING_BAD");
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("null sdkRequest", e.getMessage());
        } catch (IllegalArgumentException e) {
            assertEquals("not an ISO 4217 compatible 3 letter currency representation", e.getMessage());
        }
    }

    @Test
    public void null_currency_code() {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, null, false, false, false);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            blueSnapService.convertPrice(priceDetails, "ILS");
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("null sdkRequest", e.getMessage());
        } catch (IllegalArgumentException e) {
            assertEquals("not an ISO 4217 compatible 3 letter currency representation", e.getMessage());
        }
    }


    @Test
    public void null_new_currency_code() {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "USD", false, false, false);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            blueSnapService.convertPrice(priceDetails, null);
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("null sdkRequest", e.getMessage());
        } catch (IllegalArgumentException e) {
            assertEquals("not an ISO 4217 compatible 3 letter currency representation", e.getMessage());
        }
    }

}