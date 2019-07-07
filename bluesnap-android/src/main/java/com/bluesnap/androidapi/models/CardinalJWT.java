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

    /*
        Extract the jwt from a bluesnap http response

     */
    public String parse(String responseString) {
        try {
            JSONObject jwtJson = new JSONObject(responseString);
            jwt = jwtJson.getString("jwt");
            return jwt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}