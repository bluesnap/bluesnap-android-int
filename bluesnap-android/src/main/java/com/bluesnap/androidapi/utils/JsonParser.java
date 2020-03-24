package com.bluesnap.androidapi.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.models.*;

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
     * <p>
     * TODO: This method should be DKConiguration.fromJson()
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
            if (jsonObject.has("merchantId")) {
                sdkConfiguration.setMerchantId(jsonObject.getLong("merchantId"));
            }
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


            Shopper shopper = Shopper.fromJson(getOptionalObject(jsonObject, "shopper"));
            sdkConfiguration.setShopper(shopper);

            SupportedPaymentMethods supportedPaymentMethods = new SupportedPaymentMethods();
            JSONObject supportedPaymentTypeObject = jsonObject.getJSONObject("supportedPaymentMethods");
            if (supportedPaymentTypeObject != null) {
                JSONArray paymentMethodJsonArray = supportedPaymentTypeObject.getJSONArray("paymentMethods");
                for (int i = 0; i < paymentMethodJsonArray.length(); i++) {
                    supportedPaymentMethods.setPaymentMethod(paymentMethodJsonArray.getString(i));
                }

                if (supportedPaymentTypeObject.has("paypalCurrencies")) {
                    JSONArray paypalCurrenciesJsonArray = supportedPaymentTypeObject.getJSONArray("paypalCurrencies");
                    ArrayList<String> paypalCurrenciesList = new ArrayList<>();
                    for (int i = 0; i < paypalCurrenciesJsonArray.length(); i++) {
                        paypalCurrenciesList.add(paypalCurrenciesJsonArray.getString(i));
                    }
                    supportedPaymentMethods.setPaypalCurrencies(paypalCurrenciesList);
                }

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

                CreditCardTypeResolver.setCreditCardRegex(sdkConfiguration.getSupportedPaymentMethods().getCreditCardRegex());
            }

            sdkConfiguration.setCardinalToken(jsonObject.getString("threeDSecureJwt"));


        } catch (JSONException ex) {
            Log.e(TAG, "Error on parse sdk configuration " + ex.getMessage());
        }

        return sdkConfiguration;

    }

    public static String getOptionalString(@NonNull JSONObject jsonObject, @NonNull String name) {
        try {
            return jsonObject.getString(name);
        } catch (JSONException e) {
            //IGNORED
        }
        return "";
    }

    @Nullable
    public static JSONObject getOptionalObject(@NonNull JSONObject jsonObject, @NonNull String name) {
        try {
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * check If Not Null And Put In Json
     *
     * @param jsonObject  - the object to put inside
     * @param stringValue - the String to put
     * @param key         - the key code of the stringValue
     */
    public static void putJSONifNotNull(JSONObject jsonObject, String key, String stringValue) {
        if (null != stringValue && !"".equals(stringValue)) {
            try {
                jsonObject.put(key, stringValue);
            } catch (JSONException e) {
                Log.e(TAG, "Error on putJSONifNotNull " + e.getMessage());
            }
        }
    }

    public static void putJSONifNotNull(JSONObject jsonObject, String key, Integer intValue) {
        if (null != intValue) {
            try {
                jsonObject.put(key, intValue);
            } catch (JSONException e) {
                Log.e(TAG, "Error on putJSONifNotNull " + e.getMessage());
            }
        }
    }

    public static void putJSONifNotNull(JSONObject jsonObject, String key, JSONObject jsonObject1) {
        if (null != jsonObject1) {
            try {
                jsonObject.put(key, jsonObject1);
            } catch (JSONException e) {
                Log.e(TAG, "Error on putJSONifNotNull " + e.getMessage());
            }
        }
    }

    public static void putJSONifNotNull(JSONObject jsonObject, String key, JSONArray jsonArray) {
        if (null != jsonArray) {
            try {
                jsonObject.put(key, jsonArray);
            } catch (JSONException e) {
                Log.e(TAG, "Error on putJSONifNotNull " + e.getMessage());
            }
        }
    }

    /**
     * check If Not Null And Put In Json
     *
     * @param jsonObject  - the object to put inside
     * @param objectValue - the object to put
     * @param key         - the key code of the objectValue
     */
    public static void putJSONifNotNull(JSONObject jsonObject, String key, BSModel objectValue) {
        if (null != objectValue) {
            try {
                jsonObject.put(key, objectValue.toJson());
            } catch (JSONException e) {
                Log.e(TAG, "Error on putJSONifNotNull " + e.getMessage());
            }
        }
    }
}
