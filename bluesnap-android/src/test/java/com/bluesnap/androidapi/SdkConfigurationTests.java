package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.SDKConfiguration;
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

    private final JsonParser parser;
    private String mockResponse;

    public SdkConfigurationTests() throws IOException {
        parser = new JsonParser();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("sdkConfiguration.json");
        mockResponse = read(in);
    }

    public SDKConfiguration parseSdkConfiguration() {
        SDKConfiguration sdkConfiguration = JsonParser.parseSdkConfiguration(mockResponse);
        return sdkConfiguration;
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    @Test
    public void sdkconfiguration_deserialization_test() {

        SDKConfiguration sdkConfiguration = parseSdkConfiguration();
        assertNotNull(sdkConfiguration);
        assertNotNull("Rates parsing error", sdkConfiguration.getRates());
        assertNotNull("KountMerchantId parsing error", sdkConfiguration.getKountMerchantId());
        assertNotNull("Shopper parsing error", sdkConfiguration.getShopper());
        assertNotNull("SupportedPaymentMethods parsing error", sdkConfiguration.getSupportedPaymentMethods());

    }

    @Test
    public void sdkconfiguration_shopper_tests() {

        SDKConfiguration sdkConfiguration = parseSdkConfiguration();
        assertNotNull(sdkConfiguration);
        Shopper shopper = sdkConfiguration.getShopper();
        assertEquals("aaa2", shopper.getFirstName());
        assertNotNull("paymentSources is null", shopper.getPreviousPaymentSources());
        //assertNotNull("lastPaymentInfo is null", shopper.getLastPaymentInfo());

    }


}
