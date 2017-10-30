package com.bluesnap.androidapi;

import android.os.Parcel;
import android.util.Log;

import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PaymentRequestTests {
    private static final String TAG = PaymentRequestTests.class.getSimpleName();
    private static final double MINIMUM_AMOUNT = 0.00001D;
    private static final double MAXIMUM_AMOUNT = Double.MAX_VALUE / 2;
    private Random random = new Random();

    @Test
    public void testPaymentRequestParcel() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setShippingRequired(true);
        paymentRequest.setAmount(random.nextDouble());
        paymentRequest.setUserEmail("user@host.com");
        paymentRequest.setCurrencyNameCode("USD");
        Parcel parcel = Parcel.obtain();
        paymentRequest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        PaymentRequest parceledRequest = (PaymentRequest) PaymentRequest.CREATOR.createFromParcel(parcel);

        assertEquals(paymentRequest.getAmount(), parceledRequest.getAmount());
        assertEquals(paymentRequest.getCurrencyNameCode(), parceledRequest.getCurrencyNameCode());
        assertEquals(paymentRequest.getUserEmail(), parceledRequest.getUserEmail());
        assertEquals(paymentRequest.isShippingRequired(), parceledRequest.isShippingRequired());
        //assertEquals(paymentRequest, parceledRequest);
    }


    @Test
    public void testOriginalPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setShippingRequired(false);
        paymentRequest.setAmount(35.4D);
        paymentRequest.setUserEmail("user@host.com");
        paymentRequest.setCurrencyNameCode("USD");
        PaymentRequest parceledRequest = parcelizePaymentRequset(paymentRequest);
        assertEquals(paymentRequest.getAmount(), parceledRequest.getBaseAmount());
//        assertEquals(paymentRequest, parceledRequest);
    }

    public double randomAmount() {
        double result = MINIMUM_AMOUNT + (random.nextDouble() * (MAXIMUM_AMOUNT - MINIMUM_AMOUNT));
        Log.d(TAG, "next amount" + result);
        return result;
    }

    public PaymentRequest parcelizePaymentRequset(PaymentRequest paymentRequest) {
        Parcel parcel = Parcel.obtain();
        paymentRequest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        PaymentRequest parceledRequest = (PaymentRequest) PaymentRequest.CREATOR.createFromParcel(parcel);
        return parceledRequest;
    }

    @Test
    public void testPaymentRequest_ZeroPayment() {
        PaymentRequest paymentRequest = new PaymentRequest("USD");
        paymentRequest.setAmount(0D);
        PaymentRequest parceled = parcelizePaymentRequset(paymentRequest);

        try {
            parceled.verify();
            fail("Should not verify zero payment");
        } catch (BSPaymentRequestException e) {
            assertEquals("Invalid amount 0.000000", e.getMessage());
        }
        assertNotNull(parceled.toString());
        //assertEquals("not equals", paymentRequest, parceled);
    }


    @Test
    public void testPaymentRequest_Tax() {
        PaymentRequest paymentRequest = new PaymentRequest("USD");
        paymentRequest.setAmount(randomAmount());
        paymentRequest.setTaxAmount(randomAmount());
        PaymentRequest parceled = parcelizePaymentRequset(paymentRequest);
        assertEquals("tax lost", paymentRequest.getTaxAmount(), parceled.getTaxAmount());
        assertEquals("base tax lost", paymentRequest.getBaseTaxAmount(), parceled.getTaxAmount());
        // assertEquals("not equals", paymentRequest, parceled);
    }

    @Test
    public void testPaymentRequest_TaxAndSubtotal() {
        PaymentRequest paymentRequest = new PaymentRequest("USD");
        double subtotal = randomAmount();
        double randomTax = randomAmount();
        paymentRequest.setAmountWithTax(subtotal, randomTax);
        PaymentRequest parceled = parcelizePaymentRequset(paymentRequest);
        assertEquals("tax lost", paymentRequest.getTaxAmount(), parceled.getTaxAmount());
        assertEquals(randomTax, parceled.getTaxAmount());
        assertEquals(subtotal+randomTax, parceled.getAmount());
        //assertEquals("not equals", paymentRequest, parceled);
    }

}