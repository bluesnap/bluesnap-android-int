package com.bluesnap.androidapi.services;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.models.ExchangeRate;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.models.returningshopper.Shopper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SDKInitData {
    private static final String TAG = SDKInitData.class.getSimpleName();

    private static final String KOUNTMERCHANTID = "kountMerchantId";

    private static final String RATES = "rates";
    private static final String BASECURRENCY = "baseCurrency";
    private static final String EXCHANGERATE = "exchangeRate";

    private static final String SHOPPER = "shopper";

    private static final String SUPPORTEDPAYMENTMETHODS = "supportedPaymentMethods";

    protected int kountMerchantId;
    protected String baseCurrency;
    protected HashMap<String, ExchangeRate> ratesMap;
    protected ArrayList<ExchangeRate> ratesArray;
    @Nullable
    protected Shopper shopper;
    protected SupportedPaymentMethods supportedPaymentMethods;

    SDKInitData(JSONObject sdkInitData) {
        kountMerchantId = (int) AndroidUtil.getObjectFromJsonObject(sdkInitData, KOUNTMERCHANTID, TAG);
        Log.d(TAG, String.valueOf(kountMerchantId));
        shopper = new Shopper((JSONObject) AndroidUtil.getObjectFromJsonObject(sdkInitData, SHOPPER, TAG));
        Log.d(TAG, String.valueOf(shopper));
        supportedPaymentMethods = new SupportedPaymentMethods((JSONObject) AndroidUtil.getObjectFromJsonObject(sdkInitData, SUPPORTEDPAYMENTMETHODS, TAG));
        Log.d(TAG, String.valueOf(supportedPaymentMethods));

        //TODO: change base currency payment request to this base but need to check change rate will do as needed
        baseCurrency = (String) AndroidUtil.getObjectFromJsonObject((JSONObject) AndroidUtil.getObjectFromJsonObject(sdkInitData, RATES, TAG), BASECURRENCY, TAG);
        Log.d(TAG, String.valueOf(baseCurrency));
        updateRates(sdkInitData);

    }

    /**
     * Update the Conversion rates map from the server response data.
     * The rates are merchant specific, the merchantToken is used to identify the merchant.
     */
    private void updateRates(JSONObject sdkInitData) {
        JSONArray exchangeRate = (JSONArray) AndroidUtil.getObjectFromJsonObject((JSONObject) AndroidUtil.getObjectFromJsonObject(sdkInitData, RATES, TAG), EXCHANGERATE, TAG);
        //TODO: this can be optimized to create the ratesMap directly from the response
        ratesArray = new Gson().fromJson(exchangeRate.toString(), new TypeToken<List<ExchangeRate>>() {
        }.getType());
        ratesMap = new HashMap<>(ratesArray.size() + 1);
        ExchangeRate usdExchangeRate = new ExchangeRate();
        usdExchangeRate.setConversionRate(1.0);
        usdExchangeRate.setQuoteCurrency(SupportedPaymentMethods.USD);
        ratesMap.put(SupportedPaymentMethods.USD, usdExchangeRate);
        for (ExchangeRate r : ratesArray) {
            ratesMap.put(r.getQuoteCurrency(), r);
        }
    }

    public boolean isReturningShopper() {
        return (null != getShopper());
    }

    @Nullable
    public Shopper getShopper() {
        return shopper;
    }

    public SupportedPaymentMethods getSupportedPaymentMethods() {

        return supportedPaymentMethods;
    }

}
