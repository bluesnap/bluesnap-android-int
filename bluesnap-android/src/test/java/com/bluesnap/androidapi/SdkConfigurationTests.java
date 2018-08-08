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
        assertNotNull("Rates parsing error", sdkConfiguration.getRates());
        assertNotNull("KountMerchantId parsing error", sdkConfiguration.getKountMerchantId());
        assertNotNull("Shopper parsing error", sdkConfiguration.getShopper());
        assertNotNull("SupportedPaymentMethods parsing error", sdkConfiguration.getSupportedPaymentMethods());

    }

    @Test
    public void sdkconfiguration_shopper_tests() {
        assertNotNull(sdkConfiguration);
        Shopper shopper = sdkConfiguration.getShopper();
        assertEquals("aaa2", shopper.getFirstName());
        assertNotNull("paymentSources is null", shopper.getPreviousPaymentSources());
        assertEquals("missing Country", "ca", shopper.getCountry());
        assertEquals("bad vaulted shopper id", 22868797, shopper.getVaultedShopperId());
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
