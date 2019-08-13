package com.bluesnap.androidapi.models;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.*;

public class BS3DSAuthRequest extends  BSModel{
    private String currencyCode;
    private Double amount;
    private String jwt;

    public BS3DSAuthRequest(String currencyCode, Double amount, String jwt) {
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.jwt = jwt;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, "currency", currencyCode);
        putJSONifNotNull(jsonObject, "amount", amount.toString());
        putJSONifNotNull(jsonObject, "jwt", jwt);
        return jsonObject;
    }


}
