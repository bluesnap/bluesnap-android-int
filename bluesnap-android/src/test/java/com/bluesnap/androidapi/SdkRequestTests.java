package com.bluesnap.androidapi;

import android.os.Parcel;
import android.util.Log;

import com.bluesnap.androidapi.models.SdkRequest;
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
public class SdkRequestTests {
    private static final String TAG = SdkRequestTests.class.getSimpleName();
    private static final double MINIMUM_AMOUNT = 0.00001D;
    private static final double MAXIMUM_AMOUNT = Double.MAX_VALUE / 2;
    private Random random = new Random();

    @Test
    public void testPaymentRequestParcel() {
        SdkRequest sdkRequest = new SdkRequest();
        sdkRequest.setShippingRequired(true);
        sdkRequest.setAmount(random.nextDouble());
        sdkRequest.setUserEmail("user@host.com");
        sdkRequest.setCurrencyNameCode("USD");
        Parcel parcel = Parcel.obtain();
        sdkRequest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        SdkRequest parceledRequest = (SdkRequest) SdkRequest.CREATOR.createFromParcel(parcel);

        assertEquals(sdkRequest.getAmount(), parceledRequest.getAmount());
        assertEquals(sdkRequest.getCurrencyNameCode(), parceledRequest.getCurrencyNameCode());
        assertEquals(sdkRequest.getUserEmail(), parceledRequest.getUserEmail());
        assertEquals(sdkRequest.isShippingRequired(), parceledRequest.isShippingRequired());
        //assertEquals(sdkRequest, parceledRequest);
    }


    @Test
    public void testOriginalPaymentRequest() {
        SdkRequest sdkRequest = new SdkRequest();
        sdkRequest.setShippingRequired(false);
        sdkRequest.setAmount(35.4D);
        sdkRequest.setUserEmail("user@host.com");
        sdkRequest.setCurrencyNameCode("USD");
        SdkRequest parceledRequest = parcelizePaymentRequset(sdkRequest);
        assertEquals(sdkRequest.getAmount(), parceledRequest.getBaseAmount());
//        assertEquals(sdkRequest, parceledRequest);
    }

    public double randomAmount() {
        double result = MINIMUM_AMOUNT + (random.nextDouble() * (MAXIMUM_AMOUNT - MINIMUM_AMOUNT));
        Log.d(TAG, "next amount" + result);
        return result;
    }

    public SdkRequest parcelizePaymentRequset(SdkRequest sdkRequest) {
        Parcel parcel = Parcel.obtain();
        sdkRequest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        SdkRequest parceledRequest = (SdkRequest) SdkRequest.CREATOR.createFromParcel(parcel);
        return parceledRequest;
    }

    @Test
    public void testPaymentRequest_ZeroPayment() {
        SdkRequest sdkRequest = new SdkRequest("USD");
        sdkRequest.setAmount(0D);
        SdkRequest parceled = parcelizePaymentRequset(sdkRequest);

        try {
            parceled.verify();
            fail("Should not verify zero payment");
        } catch (BSPaymentRequestException e) {
            assertEquals("Invalid amount 0.000000", e.getMessage());
        }
        assertNotNull(parceled.toString());
        //assertEquals("not equals", sdkRequest, parceled);
    }


    @Test
    public void testPaymentRequest_Tax() {
        SdkRequest sdkRequest = new SdkRequest("USD");
        sdkRequest.setAmount(randomAmount());
        sdkRequest.setTaxAmount(randomAmount());
        SdkRequest parceled = parcelizePaymentRequset(sdkRequest);
        assertEquals("tax lost", sdkRequest.getTaxAmount(), parceled.getTaxAmount());
        assertEquals("base tax lost", sdkRequest.getBaseTaxAmount(), parceled.getTaxAmount());
        // assertEquals("not equals", sdkRequest, parceled);
    }

    @Test
    public void testPaymentRequest_TaxAndSubtotal() {
        SdkRequest sdkRequest = new SdkRequest("USD");
        double subtotal = randomAmount();
        double randomTax = randomAmount();
        sdkRequest.setAmountWithTax(subtotal, randomTax);
        SdkRequest parceled = parcelizePaymentRequset(sdkRequest);
        assertEquals("tax lost", sdkRequest.getTaxAmount(), parceled.getTaxAmount());
        assertEquals(randomTax, parceled.getTaxAmount());
        assertEquals(subtotal+randomTax, parceled.getAmount());
        //assertEquals("not equals", sdkRequest, parceled);
    }

}