package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bluesnap.androidapi.services.AndroidUtil;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("creditCardInfo")
    private ArrayList<CreditCardInfo> creditCardInfos;

    @Nullable
    public ArrayList<CreditCardInfo> getCreditCardInfos() {
        return creditCardInfos;
    }

    public void setCreditCardInfos(@Nullable ArrayList<CreditCardInfo> creditCardInfos) {
        this.creditCardInfos = creditCardInfos;
    }
}
