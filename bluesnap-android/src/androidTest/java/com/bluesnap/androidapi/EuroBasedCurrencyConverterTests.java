package com.bluesnap.androidapi;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.bluesnap.androidapi.models.PaymentRequest;
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
public class EuroBasedCurrencyConverterTests extends BSAndroidTestsBase {
    private static final String TAG = EuroBasedCurrencyConverterTests.class.getSimpleName();
    private static final String EUR = "EUR";
    private static final String ILS = "ILS";
    private static final String USD = "USD";

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
        changeToken(EUR);
        super.getToken();
        Log.i(TAG, "=============== Starting rates service tests ==================");
    }

    @Test
    public void convert_EUR_to_USD() throws InterruptedException, BSPaymentRequestException {

        PaymentRequest paymentRequest = new PaymentRequest();
        Double amount = 10D;
        paymentRequest.setAmount(amount);
        paymentRequest.setCurrencyNameCode(EUR);


        blueSnapService.setPaymentRequest(paymentRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, EUR, USD);
//        assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
        assertEquals("14.42", String.format("%.2f", convertedOncePrice));
    }

    @Test
    public void convert_EUR_to_ILS_to_USD() throws InterruptedException, BSPaymentRequestException {

        PaymentRequest paymentRequest = new PaymentRequest();
        Double amount = 10.7D;
        paymentRequest.setAmount(amount);
        paymentRequest.setCurrencyNameCode(EUR);


        blueSnapService.setPaymentRequest(paymentRequest);
        Double convertedOncePrice = blueSnapService.convertPrice(amount, EUR, ILS);
        Double convertedTwicePrice = blueSnapService.convertPrice(amount, ILS, USD);
//        assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
        assertEquals("14.42", String.format("%.2f", convertedTwicePrice));
    }



    @Test
    public void non_existing_currency_code() throws InterruptedException {

        PaymentRequest paymentRequest = new PaymentRequest();
        Double amount = 30.5D;
        paymentRequest.setAmount(amount);
        paymentRequest.setCurrencyNameCode("SOMETHING_BAD");

        try {
            blueSnapService.setPaymentRequest(paymentRequest);
            Double ILSPrice = blueSnapService.convertPrice(amount, "SOMETHING_BAD", ILS);
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("Currency not found", e.getMessage());
        }

    }

    @Test
    public void null_currency_code() throws InterruptedException {

        PaymentRequest paymentRequest = new PaymentRequest();
        Double amount = 30.5D;
        paymentRequest.setAmount(amount);

        try {
            blueSnapService.setPaymentRequest(paymentRequest);
            Double ILSPrice = blueSnapService.convertPrice(amount, "SOMETHING_BAD", ILS);
            fail("Should have trown exception");
        } catch (BSPaymentRequestException e) {
            assertEquals("Invalid currency", e.getMessage());
        }

    }
}