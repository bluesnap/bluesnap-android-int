/*
 *
 *  * Copyright © 2019 CardinalCommerce. All rights reserved.
 *
 */

package com.bluesnap.androidapi.models;

import org.json.JSONException;
import org.json.JSONObject;

public class CardinalJWT {

    private String jwt;

    public String getJWT() {
        return jwt;
    }

    public void setJWT(String JWT) {
        this.jwt = JWT;
    }

    public String parse(String jwt) {
        try {
            JSONObject jwtJson = new JSONObject(jwt);
            return jwtJson.getString("CardinalJWT");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}