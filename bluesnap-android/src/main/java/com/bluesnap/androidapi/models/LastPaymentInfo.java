package com.bluesnap.androidapi.models;

import androidx.annotation.Nullable;
import com.bluesnap.androidapi.utils.JsonParser;
import org.json.JSONObject;


/**
 * Created by roy.biber on 07/11/2017.
 */

public class LastPaymentInfo extends CreditCardInfo{
    public static String CC_PAYMENT_METHOD = "CC";

    @Nullable
    //@SerializedName("paymentMethod")
    private String paymentMethod;

    @Nullable
    public static LastPaymentInfo fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        LastPaymentInfo lastPaymentInfo = new LastPaymentInfo();
        lastPaymentInfo.setPaymentMethod(JsonParser.getOptionalString(jsonObject, "paymentMethod"));
        return lastPaymentInfo;
    }

    @Nullable
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
