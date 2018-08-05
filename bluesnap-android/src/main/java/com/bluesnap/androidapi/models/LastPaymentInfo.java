package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;


/**
 * Created by roy.biber on 07/11/2017.
 */

public class LastPaymentInfo extends CreditCardInfo{
    public static String CC_PAYMENT_METHOD = "CC";

    @Nullable
    //@SerializedName("paymentMethod")
    private String paymentMethod;

    @Nullable
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@Nullable String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
