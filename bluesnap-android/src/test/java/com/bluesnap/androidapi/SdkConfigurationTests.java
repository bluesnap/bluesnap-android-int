package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.utils.JsonParser;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SdkConfigurationTests extends TestCase {

    private SDKConfiguration sdkConfiguration;

    public SdkConfigurationTests() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("sdkConfiguration.json");
        String mockResponse = read(in);
        sdkConfiguration = JsonParser.parseSdkConfiguration(mockResponse);
    }


    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    @Test
    public void sdkconfiguration_deserialization_test() {

        assertNotNull(sdkConfiguration);
        assertNotNull("Shopper parsing error", sdkConfiguration.getShopper());
        assertNotNull("KountMerchantId parsing error", sdkConfiguration.getKountMerchantId());
        assertNotNull("Rates parsing error", sdkConfiguration.getRates());

        assertNotNull("SupportedPaymentMethods parsing error", sdkConfiguration.getSupportedPaymentMethods());

    }

    @Test
    public void sdkconfiguration_shopper_tests() {
        assertNotNull(sdkConfiguration);
        Shopper shopper = sdkConfiguration.getShopper();
        assertNotNull("Shopper parsing error", shopper);


        assertEquals("wrong first name", "aaa2", shopper.getFirstName());
        assertEquals("wrong last name", "bbb2", shopper.getLastName());
        assertEquals("wrong email", "two@two.com", shopper.getEmail());
        assertEquals("wrong Country", "ca", shopper.getCountry());
        assertEquals("wrong state", "ON", shopper.getState());
        assertEquals("wrong address", "billing address2", shopper.getAddress());
        assertEquals("wrong address2", "", shopper.getAddress2());
        assertEquals("wrong city", "billing city2", shopper.getCity());
        assertEquals("wrong zip", "123452", shopper.getZip());
        assertEquals("wrong shopperCurrency", "USD", shopper.getShopperCurrency());
        assertEquals("wrong vaulted shopper id", 22868797, shopper.getVaultedShopperId());

        assertEquals("wrong shopperCurrency", "USD", shopper.getShopperCurrency());


        //here

        assertNotNull("paymentSources is null", shopper.getPreviousPaymentSources());
        //assertNotNull("lastPaymentInfo is null", shopper.getLastPaymentInfo());

    }

    @Test
    public void sdkconfiguration_shipping_contact_info_tests() {
        assertNotNull(sdkConfiguration);
        Shopper shopper = sdkConfiguration.getShopper();
        ShippingContactInfo shippingContactInfo = shopper.getShippingContactInfo();
        assertNotNull("missing shipping contact info", shippingContactInfo);
        assertEquals("Missing country", "us", shippingContactInfo.getCountry());
    }



}
