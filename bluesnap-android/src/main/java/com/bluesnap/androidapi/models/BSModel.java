package com.bluesnap.androidapi.models;


import org.json.JSONObject;

public abstract class BSModel {

    /**
     * create JSON object from Class
     *
     * @return JSONObject
     */
    public abstract JSONObject toJson();

    /**
     * return Class to Stringified JSON Object
     *
     * @return JSON Object String
     */
    @Override
    public String toString() {
        JSONObject jsonObject = this.toJson();
        if (null == jsonObject)
            return "";
        else
            return this.toJson().toString();
    }

}
