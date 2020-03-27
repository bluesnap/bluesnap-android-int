package com.bluesnap.androidapi;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
@RunWith(AndroidJUnit4.class)
public class EuroBasedCurrencyConverterTests extends BSAndroidIntegrationTestsBase {
    private static final String TAG = EuroBasedCurrencyConverterTests.class.getSimpleName();
    private static final String EUR = "EUR";
    private static final String ILS = "ILS";
    private static final String USD = "USD";

    public EuroBasedCurrencyConverterTests() {
        super(EUR);
    }

//    @After
//    public void keepRunning() throws InterruptedException {
//        Thread.sleep(1000);
//    }
//

    @Before
    public void setup() {
        // No need to call getToken - it is already done by the parent class
        // super.getToken();
        Log.i(TAG, "=============== Starting EuroBasedCurrencyConverter tests ==================");
    }


    @Test
    public void convert_EUR_to_USD() throws BSPaymentRequestException {

        Double amount = 10D;
        SdkRequest sdkRequest = new SdkRequest(amount, EUR, false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);

        PriceDetails priceDetails = sdkRequest.getPriceDetails();
        Double usdRate = getCurrencyRate(USD);
        double expectedPrice = priceDetails.getAmount() * usdRate;

        blueSnapService.convertPrice(priceDetails, USD);
        Double convertedOncePrice = priceDetails.getAmount();

//        assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
//        assertEquals("14.42", String.format("%.2f", convertedOncePrice));
        assertEquals(String.format("%.2f", expectedPrice), String.format("%.2f", convertedOncePrice));
    }

    @Test
    public void convert_EUR_to_ILS_to_USD() throws BSPaymentRequestException {

        Double amount = 10.7D;
        SdkRequest sdkRequest = new SdkRequest(amount, EUR, false, false, false);

        blueSnapService.setSdkRequest(sdkRequest);
        PriceDetails priceDetails = sdkRequest.getPriceDetails();
        Double ilsRate = getCurrencyRate(ILS);
        Double usdRate = getCurrencyRate(USD);
        double expectedPrice1 = priceDetails.getAmount() * ilsRate;
        double expectedPrice2 = expectedPrice1 / ilsRate * usdRate;

        blueSnapService.convertPrice(priceDetails, ILS);
        Double convertedOncePrice = priceDetails.getAmount();
        assertEquals(String.format("%.2f", expectedPrice1), String.format("%.2f", convertedOncePrice));

        blueSnapService.convertPrice(priceDetails, USD);
        Double convertedTwicePrice = priceDetails.getAmount();
        //assertEquals("14.42", new BigDecimal(convertedOncePrice).setScale(2, RoundingMode.HALF_UP).toString());
        //assertEquals("15.43", String.format("%.2f", convertedTwicePrice));
        assertEquals(String.format("%.2f", expectedPrice2), String.format("%.2f", convertedTwicePrice));
    }


    @Test
    public void non_existing_currency_code() {

        Double amount = 30.5D;
        SdkRequest sdkRequest = new SdkRequest(amount, "SOMETHING_BAD", false, false, false);

        try {
            blueSnapService.setSdkRequest(sdkRequest);
            PriceDetails priceDetails = sdkRequest.getPriceDetails();
            blueSnapService.convertPrice(priceDetails, ILS);
            Double ILSPrice = priceDetails.getAmount();
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

    private double getCurrencyRate(String currencyCode) {

        ArrayList<Currency> ratesArray = blueSnapService.getRatesArray();
        Double usdRate = null;
        for (Currency currency : blueSnapService.getRatesArray()) {
            if (currency.getQuoteCurrency().equals(currencyCode)) {
                usdRate = currency.getConversionRate();
                break;
            }
        }
        assertNotNull("usdRate should not be null", usdRate);
        return usdRate.doubleValue();
    }
}