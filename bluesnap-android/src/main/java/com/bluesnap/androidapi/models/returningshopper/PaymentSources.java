package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A representation of server exchange rate.
 */
public class PaymentSources {
    private static final String TAG = PaymentSources.class.getSimpleName();
    private static final String PAYMENTSOURCES = "paymentSources";
    private static final String CREDITCARDINFO = "creditCardInfo";

    @Nullable
    private ArrayList<CreditCardInfo> creditCardInfos;

    public PaymentSources(@Nullable JSONObject paymentSources) {
        try {
            if (null != paymentSources && !paymentSources.isNull(PAYMENTSOURCES) && !paymentSources.getJSONObject(PAYMENTSOURCES).isNull(CREDITCARDINFO)) {
                JSONArray creditCardInfo = (JSONArray) AndroidUtil.getObjectFromJsonObject(paymentSources, CREDITCARDINFO, TAG);
                Log.d(TAG, String.valueOf(creditCardInfo));
                assert creditCardInfo != null;
                creditCardInfos = new ArrayList<>();
                for (int i = 0; i < creditCardInfo.length(); i++) {
                    CreditCardInfo creditCardInfoTemp = new CreditCardInfo(creditCardInfo.getJSONObject(i));
                    Log.d(TAG, String.valueOf(creditCardInfoTemp));
                    creditCardInfos.add(creditCardInfoTemp);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "json parsing exception", e);
        }
    }

    @Nullable
    public ArrayList<CreditCardInfo> getCreditCardInfos() {
        return creditCardInfos;
    }
}
