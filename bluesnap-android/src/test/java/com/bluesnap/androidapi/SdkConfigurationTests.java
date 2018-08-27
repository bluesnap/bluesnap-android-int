package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.models.PaymentSources;
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
        assertEquals("wrong country", "ca", shopper.getCountry());
        assertEquals("wrong state", "ON", shopper.getState());
        assertEquals("wrong address", "billing address2", shopper.getAddress());
        assertEquals("wrong address2", "", shopper.getAddress2());
        assertEquals("wrong city", "billing city2", shopper.getCity());
        assertEquals("wrong zip", "123452", shopper.getZip());
        assertEquals("wrong shopperCurrency", "USD", shopper.getShopperCurrency());
        assertEquals("wrong vaulted shopper id", 22868797, shopper.getVaultedShopperId());

        sdkconfiguration_payment_sources_tests(shopper.getPreviousPaymentSources());


        //here

        //assertNotNull("lastPaymentInfo is null", shopper.getLastPaymentInfo());

    }

    public void sdkconfiguration_payment_sources_tests(PaymentSources paymentSources) {
        assertNotNull("PreviousPaymentSources is null", paymentSources);
        assertNotNull("previousCreditCardInfos is null", paymentSources.getCreditCardInfos());
        sdkconfiguration_credit_card_info_tests(paymentSources.getCreditCardInfos().get(0));
    }

    public void sdkconfiguration_credit_card_info_tests(CreditCardInfo creditCardInfo) {
        assertNotNull("creditCardInfo is null", creditCardInfo);
        BillingContactInfo billingContactInfo = creditCardInfo.getBillingContactInfo();
        CreditCard creditCard = creditCardInfo.getCreditCard();

        sdkconfiguration_billing_info_tests(billingContactInfo);
        sdkconfiguration_credit_card_tests(creditCard);
    }

    public void sdkconfiguration_billing_info_tests(BillingContactInfo billingContactInfo) {
        assertNotNull("billingContactInfo is null", billingContactInfo);

        assertEquals("wrong first name", "billingFirstName", billingContactInfo.getFirstName());
        assertEquals("wrong last name", "billingLastName", billingContactInfo.getLastName());
        assertEquals("wrong address", "10 Main St", billingContactInfo.getAddress());
        assertEquals("wrong address2", "Apt 1", billingContactInfo.getAddress2());
        assertEquals("wrong state", "MA", billingContactInfo.getState());
        assertEquals("wrong zip", "01752", billingContactInfo.getZip());
        assertEquals("wrong Country", "us", billingContactInfo.getCountry());
        assertEquals("wrong city", "", billingContactInfo.getCity());
        assertEquals("wrong email", "", billingContactInfo.getEmail());
    }

    public void sdkconfiguration_credit_card_tests(CreditCard creditCard) {
        assertNotNull("creditCard is null", creditCard);

        assertEquals("wrong card last four digits", "1111", creditCard.getCardLastFourDigits());
        assertEquals("wrong card type", "VISA", creditCard.getCardType());
        assertEquals("wrong card sub type", "CREDIT", creditCard.getCardSubType());
        assertEquals("wrong expiration month", 12, creditCard.getExpirationMonth().intValue());
        assertEquals("wrong expiration year", 2020, creditCard.getExpirationYear().intValue());
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
    public void create_json_shopper_for_update_shopper_server_test() {
        assertNotNull(sdkConfiguration);
        try {
            Shopper shopper = sdkConfiguration.getShopper();

            // since test only populate previous used and we want to check the chosen card (New) than we populate new payment method
            shopper.setNewPaymentSources(shopper.getPreviousPaymentSources());

            // add full credit card details
            shopper.getNewCreditCardInfo().getCreditCard().setCvc("123");
            shopper.getNewCreditCardInfo().getCreditCard().setNumber("4111 1111 1111 1111");

            // get shopper json
            JSONObject shopperJsonObject = shopper.toJson();
            assertNotNull(shopperJsonObject);

            // check contact info details in shopper
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

            // get paymentSources from shopper
            JSONObject paymentSourcesJsonObject = (JSONObject) shopperJsonObject.get("paymentSources");
            assertNotNull(paymentSourcesJsonObject);

            // get creditCardInfo Array from paymentSources
            JSONArray creditCardInfosJsonObject = (JSONArray) paymentSourcesJsonObject.get("creditCardInfo");
            assertNotNull(creditCardInfosJsonObject);
            assertEquals(creditCardInfosJsonObject.length(), 1);

            // get first and only creditCardInfo from creditCardInfo Array
            JSONObject creditCardInfoJsonObject = creditCardInfosJsonObject.getJSONObject(0);
            assertNotNull(creditCardInfoJsonObject);

            // get billingContactInfo from creditCardInfo
            JSONObject billingContactInfoJsonObject = (JSONObject) creditCardInfoJsonObject.get("billingContactInfo");
            assertNotNull(billingContactInfoJsonObject);
            assertEquals(billingContactInfoJsonObject.get("firstName"), "billingFirstName");
            assertEquals(billingContactInfoJsonObject.get("lastName"), "billingLastName");
            assertEquals(billingContactInfoJsonObject.get("address1"), "10 Main St");
            assertEquals(billingContactInfoJsonObject.get("address2"), "Apt 1");
            assertEquals(billingContactInfoJsonObject.get("state"), "MA");
            assertEquals(billingContactInfoJsonObject.get("zip"), "01752");
            assertEquals(billingContactInfoJsonObject.get("country"), "us");

            // get creditCard from creditCardInfo
            JSONObject creditCardJsonObject = (JSONObject) creditCardInfoJsonObject.get("creditCard");
            assertNotNull(creditCardJsonObject);
            assertEquals(creditCardJsonObject.get("cardLastFourDigits"), "1111");
            assertEquals(creditCardJsonObject.get("cardType"), CreditCardTypeResolver.VISA);
            assertEquals(creditCardJsonObject.get("cardSubType"), "CREDIT");
            assertEquals(creditCardJsonObject.get("expirationMonth"), 12);
            assertEquals(creditCardJsonObject.get("expirationYear"), 2020);

            // check added values from the beginning
            assertEquals(creditCardJsonObject.get("cardNumber"), "4111111111111111");
            assertEquals(creditCardJsonObject.get("securityCode"), "123");

            // get shippingContactInfo from shopper
            JSONObject shippingContactInfoJsonObject = (JSONObject) shopperJsonObject.get("shippingContactInfo");
            assertNotNull(shippingContactInfoJsonObject);
            assertEquals(shippingContactInfoJsonObject.get("firstName"), "aaa2");
            assertEquals(shippingContactInfoJsonObject.get("lastName"), "bbb2");
            assertEquals(shippingContactInfoJsonObject.get("address1"), "shipping address2");
            assertEquals(shippingContactInfoJsonObject.get("country"), "us");
            assertEquals(shippingContactInfoJsonObject.get("state"), "NY");
            assertEquals(shippingContactInfoJsonObject.get("zip"), "123452");
            assertEquals(shippingContactInfoJsonObject.get("city"), "shipping city2");

            // get chosenPaymentMethod from shopper
            JSONObject chosenPaymentMethodJsonObject = (JSONObject) shopperJsonObject.get("chosenPaymentMethod");
            assertNotNull(chosenPaymentMethodJsonObject);

            // check if  chosenPaymentMethodType is Credit Card
            assertEquals(chosenPaymentMethodJsonObject.get("chosenPaymentMethodType"), "CC");

            // get creditCard from chosenPaymentMethodType
            JSONObject creditCard2JsonObject = (JSONObject) chosenPaymentMethodJsonObject.get("creditCard");
            assertNotNull(creditCard2JsonObject);
            assertEquals(creditCardJsonObject.get("cardLastFourDigits"), "1111");
            assertEquals(creditCardJsonObject.get("cardType"), CreditCardTypeResolver.VISA);
            assertEquals(creditCardJsonObject.get("cardSubType"), "CREDIT");
            assertEquals(creditCardJsonObject.get("expirationMonth"), 12);
            assertEquals(creditCardJsonObject.get("expirationYear"), 2020);

        } catch (JSONException e) {
            fail("create json shopper for update shopper server fail:" + e.getMessage());
        }

    }
}
