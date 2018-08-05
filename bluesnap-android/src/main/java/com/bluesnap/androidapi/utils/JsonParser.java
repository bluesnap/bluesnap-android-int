package com.bluesnap.androidapi.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import com.bluesnap.androidapi.models.Currency;
import com.bluesnap.androidapi.models.Rates;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by oz
 */

public class JsonParser {

    public static final String TAG = JsonParser.class.getSimpleName();

    /**
     * SDKConfiguration Json mapping
     *
     * @param jsonData
     * @return
     */
    @NonNull
    public static SDKConfiguration parseSdkConfiguration(@NonNull String jsonData) {
        SDKConfiguration sdkConfiguration = new SDKConfiguration();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            sdkConfiguration.setKountMerchantId(jsonObject.getInt("kountMerchantId"));
            JSONObject ratesJsonObject = jsonObject.getJSONObject("rates");
            if (ratesJsonObject != null) {
                JSONArray exchangeRateJsonArray = ratesJsonObject.getJSONArray("exchangeRate");
                ArrayList<Currency> currencyList = new ArrayList<>();
                for (int i = 0; i < exchangeRateJsonArray.length(); i++) {
                    JSONObject currencyJsonObject = exchangeRateJsonArray.getJSONObject(i);
                    currencyList.add(new Currency(currencyJsonObject.getString("quoteCurrency"), currencyJsonObject.getString("quoteCurrencyName"),
                            currencyJsonObject.getDouble("fractionDigits"), currencyJsonObject.getDouble("conversionRate")));
                }
                Rates rates = new Rates(currencyList, ratesJsonObject.getString("baseCurrency"), ratesJsonObject.getString("baseCurrencyName"));
                sdkConfiguration.setRates(rates);
            }

            SupportedPaymentMethods supportedPaymentMethods = new SupportedPaymentMethods();
            JSONObject supportedPaymentTypeObject = jsonObject.getJSONObject("supportedPaymentMethods");
            if (supportedPaymentTypeObject != null) {
                JSONArray paymentMethodJsonArray = supportedPaymentTypeObject.getJSONArray("paymentMethods");
                ArrayList<String> paymentMethodList = new ArrayList<>();
                for (int i = 0; i < paymentMethodJsonArray.length(); i++) {
                    paymentMethodList.add(paymentMethodJsonArray.getString(i));
                }
                supportedPaymentMethods.setPaymentMethods(paymentMethodList);

                JSONArray paypalCurrenciesJsonArray = supportedPaymentTypeObject.getJSONArray("paypalCurrencies");
                ArrayList<String> paypalCurrenciesList = new ArrayList<>();
                for (int i = 0; i < paypalCurrenciesJsonArray.length(); i++) {
                    paypalCurrenciesList.add(paypalCurrenciesJsonArray.getString(i));
                }
                supportedPaymentMethods.setPaypalCurrencies(paypalCurrenciesList);

                JSONArray creditCardBrandsJsonArray = supportedPaymentTypeObject.getJSONArray("creditCardBrands");
                ArrayList<String> creditCardBrandsList = new ArrayList<>();
                for (int i = 0; i < creditCardBrandsJsonArray.length(); i++) {
                    creditCardBrandsList.add(creditCardBrandsJsonArray.getString(i));
                }
                supportedPaymentMethods.setCreditCardBrands(creditCardBrandsList);

                JSONArray creditCardTypesJsonArray = supportedPaymentTypeObject.getJSONArray("creditCardTypes");
                ArrayList<String> creditCardTypesList = new ArrayList<>();
                for (int i = 0; i < creditCardTypesJsonArray.length(); i++) {
                    creditCardTypesList.add(creditCardTypesJsonArray.getString(i));
                }
                supportedPaymentMethods.setCreditCardTypes(creditCardTypesList);

                JSONObject creditCardRegexJsonObject = supportedPaymentTypeObject.getJSONObject("creditCardRegex");
                LinkedHashMap<String, String> creditCardRegexMap = new LinkedHashMap<>();

                Iterator<String> keysItr = creditCardRegexJsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    creditCardRegexMap.put(key, creditCardRegexJsonObject.getString(key));
                }
                supportedPaymentMethods.setCreditCardRegex(creditCardRegexMap);

                sdkConfiguration.setSupportedPaymentMethods(supportedPaymentMethods);
            }
        } catch (JSONException ex) {
            Log.d(TAG, "Error on parse sdk configuration " + ex.getMessage());
        }

        return sdkConfiguration;

    }
}
