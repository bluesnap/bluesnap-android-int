package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.utils.JsonParser;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    @Test
    public void create_shopper_json_test() {
        assertNotNull(sdkConfiguration);
        try {
            Shopper shopper = sdkConfiguration.getShopper();
            shopper.setNewPaymentSources(shopper.getPreviousPaymentSources());
            JSONObject shopperJsonObject = shopper.toJson();

            assertNotNull(shopperJsonObject);
            assertEquals(shopperJsonObject.get("vaultedShopperId"), 22868797);
            assertEquals(shopperJsonObject.get("firstName"), "aaa2");
            assertEquals(shopperJsonObject.get("lastName"), "bbb2");
            assertEquals(shopperJsonObject.get("email"), "two@two.com");
            assertEquals(shopperJsonObject.get("country"), "ca");
            assertEquals(shopperJsonObject.get("state"), "ON");
            assertEquals(shopperJsonObject.get("zip"), "123452");
            assertEquals(shopperJsonObject.get("address"), "billing address2");
            assertEquals(shopperJsonObject.get("city"), "billing city2");
            assertEquals(shopperJsonObject.get("shopperCurrency"), "USD");

            JSONObject paymentSourcesJsonObject = (JSONObject) shopperJsonObject.get("paymentSources");
            assertNotNull(paymentSourcesJsonObject);

            JSONArray creditCardInfosJsonObject = (JSONArray) paymentSourcesJsonObject.get("creditCardInfo");
            assertNotNull(creditCardInfosJsonObject);
            assertEquals(creditCardInfosJsonObject.length(), 1);

            JSONObject creditCardInfoJsonObject = creditCardInfosJsonObject.getJSONObject(0);
            assertNotNull(creditCardInfoJsonObject);

            JSONObject billingContactInfoJsonObject = (JSONObject) creditCardInfoJsonObject.get("billingContactInfo");
            assertNotNull(billingContactInfoJsonObject);
            assertEquals(billingContactInfoJsonObject.get("firstName"), "billingFirstName");
            assertEquals(billingContactInfoJsonObject.get("lastName"), "billingLastName");
            assertEquals(billingContactInfoJsonObject.get("address1"), "10 Main St");
            assertEquals(billingContactInfoJsonObject.get("address2"), "Apt 1");
            assertEquals(billingContactInfoJsonObject.get("state"), "MA");
            assertEquals(billingContactInfoJsonObject.get("zip"), "01752");
            assertEquals(billingContactInfoJsonObject.get("country"), "us");

            JSONObject creditCardJsonObject = (JSONObject) creditCardInfoJsonObject.get("creditCard");
            assertNotNull(creditCardJsonObject);
            assertEquals(creditCardJsonObject.get("cardLastFourDigits"), "1111");
            assertEquals(creditCardJsonObject.get("cardType"), "VISA");
            assertEquals(creditCardJsonObject.get("cardSubType"), "CREDIT");
            assertEquals(creditCardJsonObject.get("expirationMonth"), 12);
            assertEquals(creditCardJsonObject.get("expirationYear"), 2020);

            JSONObject shippingContactInfoJsonObject = (JSONObject) shopperJsonObject.get("shippingContactInfo");
            assertNotNull(shippingContactInfoJsonObject);
            assertEquals(shippingContactInfoJsonObject.get("firstName"), "aaa2");
            assertEquals(shippingContactInfoJsonObject.get("lastName"), "bbb2");
            assertEquals(shippingContactInfoJsonObject.get("address1"), "shipping address2");
            assertEquals(shippingContactInfoJsonObject.get("country"), "us");
            assertEquals(shippingContactInfoJsonObject.get("state"), "NY");
            assertEquals(shippingContactInfoJsonObject.get("zip"), "123452");
            assertEquals(shippingContactInfoJsonObject.get("city"), "shipping city2");

            JSONObject chosenPaymentMethodJsonObject = (JSONObject) shopperJsonObject.get("chosenPaymentMethod");
            assertNotNull(chosenPaymentMethodJsonObject);
            assertEquals(chosenPaymentMethodJsonObject.get("chosenPaymentMethodType"), "CC");

            JSONObject creditCard2JsonObject = (JSONObject) creditCardInfoJsonObject.get("creditCard");
            assertNotNull(creditCard2JsonObject);
            assertEquals(creditCardJsonObject.get("cardLastFourDigits"), "1111");
            assertEquals(creditCardJsonObject.get("cardType"), "VISA");
            assertEquals(creditCardJsonObject.get("cardSubType"), "CREDIT");
            assertEquals(creditCardJsonObject.get("expirationMonth"), 12);
            assertEquals(creditCardJsonObject.get("expirationYear"), 2020);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
