package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of server exchange rate.
 */
public class PaymentSources {

    @Nullable
    //@SerializedName("creditCardInfo")
    private List<CreditCardInfo> previousCreditCardInfos;

    @Nullable
    public static PaymentSources fromJson(@Nullable JSONObject previousPaymentSources) {
        if (previousPaymentSources == null) {
            return null;

        }
        PaymentSources paymentSources = new PaymentSources();
        paymentSources.previousCreditCardInfos = new ArrayList<>();
        try {

            if (previousPaymentSources.getJSONArray("creditCardInfo").length() > 0) {
                JSONArray creditCardInfosJA = previousPaymentSources.getJSONArray("creditCardInfo");
                for (int i = 0; i < creditCardInfosJA.length(); i++) {
                    CreditCardInfo ccinfo = CreditCardInfo.fromJson(creditCardInfosJA.getJSONObject(i));
                    paymentSources.previousCreditCardInfos.add(ccinfo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return paymentSources;
    }

    @Nullable
    public List<CreditCardInfo> getPreviousCreditCardInfos() {
        return previousCreditCardInfos;
    }

    public void setPreviousCreditCardInfos(@Nullable ArrayList<CreditCardInfo> previousCreditCardInfos) {
        this.previousCreditCardInfos = previousCreditCardInfos;
    }
}
