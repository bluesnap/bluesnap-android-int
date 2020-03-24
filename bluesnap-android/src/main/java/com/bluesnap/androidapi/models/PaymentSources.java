package com.bluesnap.androidapi.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * A representation of server exchange rate.
 */
public class PaymentSources extends BSModel {

    public static final String CREDIT_CARD_INFO = "creditCardInfo";
    @Nullable
    private List<CreditCardInfo> creditCardInfos;

    public PaymentSources(@NonNull CreditCardInfo creditCardInfo) {
        this.creditCardInfos = new ArrayList<>();
        this.creditCardInfos.add(creditCardInfo);
    }

    public PaymentSources() {
    }

    @Nullable
    public static PaymentSources fromJson(@Nullable JSONObject previousPaymentSources) {
        if (previousPaymentSources == null) {
            return null;

        }
        PaymentSources paymentSources = new PaymentSources();
        paymentSources.creditCardInfos = new ArrayList<>();
        try {

            if (previousPaymentSources.getJSONArray(CREDIT_CARD_INFO).length() > 0) {
                JSONArray creditCardInfosJA = previousPaymentSources.getJSONArray(CREDIT_CARD_INFO);
                for (int i = 0; i < creditCardInfosJA.length(); i++) {
                    CreditCardInfo ccinfo = CreditCardInfo.fromJson(creditCardInfosJA.getJSONObject(i));
                    paymentSources.creditCardInfos.add(ccinfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return paymentSources;
    }

    @Nullable
    public List<CreditCardInfo> getCreditCardInfos() {
        return creditCardInfos;
    }

    public void setCreditCardInfos(@Nullable ArrayList<CreditCardInfo> creditCardInfos) {
        this.creditCardInfos = creditCardInfos;
    }

    @NonNull
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        List<CreditCardInfo> creditCardInfos = getCreditCardInfos();
        JSONArray jsonArray = new JSONArray();
        if (null != creditCardInfos)
            for (CreditCardInfo creditCardInfo : creditCardInfos) {
                jsonArray.put(creditCardInfo.toJson());
            }
        putJSONifNotNull(jsonObject, CREDIT_CARD_INFO, jsonArray);
        return jsonObject;
    }
}
