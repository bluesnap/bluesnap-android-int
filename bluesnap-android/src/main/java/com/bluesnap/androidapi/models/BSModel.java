package com.bluesnap.androidapi.models;


import org.json.JSONObject;

public abstract class BSModel {

    /**
     * create JSON object from Class
     *
     * @return JSONObject
     */
    public abstract JSONObject toJson();

}
