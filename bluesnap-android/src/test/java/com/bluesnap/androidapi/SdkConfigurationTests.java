package com.bluesnap.androidapi;

import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.PaymentSources;
import com.bluesnap.androidapi.models.Rates;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SdkConfigurationTests extends TestCase {
    public final int NUMBER_OF_PAYMENT_METHODS = 8;
    public final int NUMBER_OF_CURRENCIES = 97;
    public final int NUMBER_OF_PAYPAL_CURRENCIES = 4;
    public final int NUMBER_OF_CREDIT_CARD_BRANDS = 13;
    public final int NUMBER_OF_CREDIT_CARD_TYPES = 3;
    public final int NUMBER_OF_CREDIT_CARD_REGEX = 15;

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
    public void sdkConfiguration_supportedPaymentMethods_tests() {
        assertNotNull(sdkConfiguration);
        SupportedPaymentMethods supportedPaymentMethods = sdkConfiguration.getSupportedPaymentMethods();
        assertNotNull("supportedPaymentMethods is null", supportedPaymentMethods);
        sdkConfiguration_paymentMethods_test(supportedPaymentMethods.getPaymentMethods());
        sdkConfiguration_paypalCurrencies_test(supportedPaymentMethods.getPaypalCurrencies());
        sdkConfiguration_creditCardBrands_test(supportedPaymentMethods.getCreditCardBrands());
        sdkConfiguration_creditCardTypes_test(supportedPaymentMethods.getCreditCardTypes());
        sdkConfiguration_creditCardRegex_test(supportedPaymentMethods.getCreditCardRegex());

    }

    public void sdkConfiguration_paymentMethods_test(ArrayList<String> paymentMethods) {
        assertNotNull("paymentMethods is null", paymentMethods);
        assertEquals("wrong number of payment methods", NUMBER_OF_PAYMENT_METHODS, paymentMethods.size());
        assertEquals("wrong payment method number 1", "APPLE_PAY", paymentMethods.get(0));
        assertEquals("wrong payment method number 2", "CC", paymentMethods.get(1));
        assertEquals("wrong payment method number 3", "PAYPAL", paymentMethods.get(2));
        assertEquals("wrong payment method number 4", "WIRE", paymentMethods.get(3));
        assertEquals("wrong payment method number 5", "ECP", paymentMethods.get(4));
        assertEquals("wrong payment method number 6", "MONEYBOOKERS", paymentMethods.get(5));
        assertEquals("wrong payment method number 7", "BANKTRANSFER_COMMON", paymentMethods.get(6));
        assertEquals("wrong payment method number 8", "PAYSAFECARD", paymentMethods.get(7));
    }

    public void sdkConfiguration_paypalCurrencies_test(ArrayList<String> paypalCurrencies) {
        assertNotNull("paypalCurrencies is null", paypalCurrencies);
        assertEquals("wrong number of paypal currencies", NUMBER_OF_PAYPAL_CURRENCIES, paypalCurrencies.size());
        assertEquals("wrong currency number 1", "EUR", paypalCurrencies.get(0));
        assertEquals("wrong currency number 2", "CAD", paypalCurrencies.get(1));
        assertEquals("wrong currency number 3", "USD", paypalCurrencies.get(2));
        assertEquals("wrong currency number 4", "GBP", paypalCurrencies.get(3));
    }

    public void sdkConfiguration_creditCardBrands_test(ArrayList<String> creditCardBrands) {
        assertNotNull("creditCardBrands is null", creditCardBrands);
        assertEquals("wrong number of credit card brands", NUMBER_OF_CREDIT_CARD_BRANDS, creditCardBrands.size());
        assertEquals("wrong credit card brand number 1", "VISA", creditCardBrands.get(0));
        assertEquals("wrong credit card brand number 2", "MASTERCARD", creditCardBrands.get(1));
        assertEquals("wrong credit card brand number 3", "AMEX", creditCardBrands.get(2));
        assertEquals("wrong credit card brand number 4", "DISCOVER", creditCardBrands.get(3));
        assertEquals("wrong credit card brand number 5", "DINERS", creditCardBrands.get(4));
        assertEquals("wrong credit card brand number 6", "CHINA_UNION_PAY", creditCardBrands.get(5));
        assertEquals("wrong credit card brand number 7", "CARTE_BLEUE", creditCardBrands.get(6));
        assertEquals("wrong credit card brand number 8", "CABAL", creditCardBrands.get(7));
        assertEquals("wrong credit card brand number 9", "TARJETASHOPPING", creditCardBrands.get(8));
        assertEquals("wrong credit card brand number 10", "NARANJA", creditCardBrands.get(9));
        assertEquals("wrong credit card brand number 11", "CENCOSUD", creditCardBrands.get(10));
        assertEquals("wrong credit card brand number 12", "HIPERCARD", creditCardBrands.get(11));
        assertEquals("wrong credit card brand number 13", "ELO", creditCardBrands.get(12));
    }

    public void sdkConfiguration_creditCardTypes_test(ArrayList<String> creditCardTypes) {
        assertNotNull("creditCardTypes is null", creditCardTypes);
        assertEquals("wrong number of credit card types", NUMBER_OF_CREDIT_CARD_TYPES, creditCardTypes.size());
        assertEquals("wrong credit card type number 1", "CREDIT", creditCardTypes.get(0));
        assertEquals("wrong credit card type number 2", "DEBIT", creditCardTypes.get(1));
        assertEquals("wrong credit card type number 3", "PREPAID", creditCardTypes.get(2));
    }

    public void sdkConfiguration_creditCardRegex_test(LinkedHashMap<String, String> creditCardRegex) {
        assertNotNull("creditCardRegex is null", creditCardRegex);
        assertEquals("wrong number of credit card regex", NUMBER_OF_CREDIT_CARD_REGEX, creditCardRegex.size());
        assertEquals("wrong credit card regex number 1", "^(40117[8-9]|431274|438935|451416|457393|45763[1-2]|504175|506699|5067[0-6][0-9]|50677[0-8]|509[0-9][0-9][0-9]|636368|636369|636297|627780).*", creditCardRegex.get("ELO"));
        assertEquals("wrong credit card regex number 2", "^(606282|637095).*", creditCardRegex.get("HIPERCARD"));
        assertEquals("wrong credit card regex number 3", "^603493.*", creditCardRegex.get("CENCOSUD"));
        assertEquals("wrong credit card regex number 4", "^589562.*", creditCardRegex.get("NARANJA"));
        assertEquals("wrong credit card regex number 5", "^(603488|(27995[0-9])).*", creditCardRegex.get("TARJETASHOPPING"));
        assertEquals("wrong credit card regex number 6", "^(501105).*", creditCardRegex.get("ARGENCARD"));
        assertEquals("wrong credit card regex number 7", "^((627170)|(589657)|(603522)|(604((20[1-9])|(2[1-9][0-9])|(3[0-9]{2})|(400)))).*", creditCardRegex.get("CABAL"));
        assertEquals("wrong credit card regex number 8", "^4.+", creditCardRegex.get("VISA"));
        assertEquals("wrong credit card regex number 9", "^(5(([1-5])|(0[1-5]))|2(([2-6])|(7(1|20)))|6((0(0[2-9]|1[2-9]|2[6-9]|[3-5]))|(2((1(0|2|3|[5-9]))|20|7[0-9]|80))|(60|3(0|[3-9]))|(4[0-2]|[6-8]))).+", creditCardRegex.get("MASTERCARD"));
        assertEquals("wrong credit card regex number 10", "^3(24|4[0-9]|7|56904|379(41|12|13)).+", creditCardRegex.get("AMEX"));
        assertEquals("wrong credit card regex number 11", "^(3[8-9]|(6((01(1|300))|4[4-9]|5))).+", creditCardRegex.get("DISCOVER"));
        assertEquals("wrong credit card regex number 12", "^(3(0([0-5]|9|55)|6)).*", creditCardRegex.get("DINERS"));
        assertEquals("wrong credit card regex number 13", "^(2131|1800|35).*", creditCardRegex.get("JCB"));
        assertEquals("wrong credit card regex number 14", "(^62(([4-6]|8)[0-9]{13,16}|2[2-9][0-9]{12,15}))$", creditCardRegex.get("CHINA_UNION_PAY"));
        assertEquals("wrong credit card regex number 15", "^((3(6[1-4]|77451))|(4(059(?!34)|150|201|561|562|533|556|97))|(5(0[1-4]|13|30066|341[0-1]|587[0-2]|6|8))|(6(27244|390|75[1-6]|799999998))).*", creditCardRegex.get("CARTE_BLEUE"));
    }

    @Test
    public void sdkConfiguration_shopper_tests() {
        assertNotNull(sdkConfiguration);
        Shopper shopper = sdkConfiguration.getShopper();
        assertNotNull("Shopper is null", shopper);


        assertEquals("wrong first name", "aaa2", shopper.getFirstName());
        assertEquals("wrong last name", "bbb2", shopper.getLastName());
        assertEquals("wrong email", "two@two.com", shopper.getEmail());
        assertEquals("wrong country", "ca", shopper.getCountry());
        assertEquals("wrong state", "ON", shopper.getState());
        assertEquals("wrong address", "billing address2", shopper.getAddress());
        assertEquals("wrong address2", "", shopper.getAddress2());
        assertEquals("wrong city", "billing city2", shopper.getCity());
        assertEquals("wrong zip", "123452", shopper.getZip());
        assertEquals("wrong shopper currency", "USD", shopper.getShopperCurrency());
        assertEquals("wrong vaulted shopper id", 22868797, shopper.getVaultedShopperId());
        assertNull("lastPaymentInfo is not null", shopper.getLastPaymentInfo());

        sdkConfiguration_payment_sources_test(shopper.getPreviousPaymentSources());
        assertEquals("wrong newCreditCardInfo", shopper.getNewCreditCardInfo(), shopper.getPreviousPaymentSources().getPreviousCreditCardInfos().get(0));
    }

    public void sdkConfiguration_payment_sources_test(PaymentSources paymentSources) {
        assertNotNull("PreviousPaymentSources is null", paymentSources);
        assertNotNull("previousCreditCardInfos is null", paymentSources.getPreviousCreditCardInfos());
        sdkConfiguration_credit_card_info_test(paymentSources.getPreviousCreditCardInfos().get(0));
    }

    public void sdkConfiguration_credit_card_info_test(CreditCardInfo creditCardInfo) {
        assertNotNull("creditCardInfo is null", creditCardInfo);
        BillingContactInfo billingContactInfo = creditCardInfo.getBillingContactInfo();
        CreditCard creditCard = creditCardInfo.getCreditCard();

        sdkConfiguration_billing_info_test(billingContactInfo);
        sdkConfiguration_credit_card_test(creditCard);
    }

    public void sdkConfiguration_billing_info_test(BillingContactInfo billingContactInfo) {
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

    public void sdkConfiguration_credit_card_test(CreditCard creditCard) {
        assertNotNull("creditCard is null", creditCard);

        assertEquals("wrong card last four digits", "1111", creditCard.getCardLastFourDigits());
        assertEquals("wrong card type", "VISA", creditCard.getCardType());
        assertEquals("wrong card sub type", "CREDIT", creditCard.getCardSubType());
        assertEquals("wrong expiration month", new Integer(12), creditCard.getExpirationMonth());
        assertEquals("wrong expiration year", new Integer(2020), creditCard.getExpirationYear());
    }

    @Test
    public void sdkConfiguration_kount_tests() {
        assertNotNull(sdkConfiguration);
        assertEquals("wrong kount merchant Id", 700000, sdkConfiguration.getKountMerchantId());
    }

    @Test
    public void sdkConfiguration_rates_tests() {
        assertNotNull(sdkConfiguration);
        Rates rates = sdkConfiguration.getRates();
        assertNotNull("Rates is null", rates);
        assertEquals("wrong merchant store currency", "USD", rates.getMerchantStoreCurrency());
        assertEquals("wrong merchant store currency name", "US Dollar", rates.getMerchantStoreCurrencyName());

        ArrayList<Currency> currenciesArrayList = rates.getCurrencies();
        assertNotNull("Currencies is null", currenciesArrayList);
        assertEquals("wrong number of currencies", NUMBER_OF_CURRENCIES, currenciesArrayList.size());

        sdkConfiguration_currency_test(currenciesArrayList.get(0));
    }

    public void sdkConfiguration_currency_test(Currency firstCurrency) {
        assertEquals("wrong quote currency", "EUR", firstCurrency.getQuoteCurrency());
        assertEquals("wrong quote currency name", "Euro", firstCurrency.getQuoteCurrencyName());
        assertEquals("wrong fraction digits", Double.parseDouble("2"), firstCurrency.getFractionDigits());
        assertEquals("wrong conversion rate", Double.parseDouble("0.776349"), firstCurrency.getConversionRate());
    }

    @Test
    public void sdkConfiguration_shipping_contact_info_tests() {
        ShippingContactInfo shippingContactInfo = sdkConfiguration.getShopper().getShippingContactInfo();
        assertNotNull("shippingContactInfo is null", shippingContactInfo);
        assertEquals("wrong first name", "aaa2", shippingContactInfo.getFirstName());
        assertEquals("wrong last name", "bbb2", shippingContactInfo.getLastName());
        assertEquals("wrong address", "shipping address2", shippingContactInfo.getAddress());
        assertEquals("wrong address2", "", shippingContactInfo.getAddress2());
        assertEquals("wrong state", "NY", shippingContactInfo.getState());
        assertEquals("wrong zip", "123452", shippingContactInfo.getZip());
        assertEquals("wrong Country", "us", shippingContactInfo.getCountry());
        assertEquals("wrong city", "shipping city2", shippingContactInfo.getCity());
    }


}
