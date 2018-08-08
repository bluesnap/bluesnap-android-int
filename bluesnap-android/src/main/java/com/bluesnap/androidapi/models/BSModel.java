package com.bluesnap.androidapi.models;

import android.support.annotation.NonNull;
import org.json.JSONObject;

public abstract class BSModel {


    @NonNull
    public abstract JSONObject toJson();

    @Override
    public String toString() {
        return this.toJson().toString();
    }

}
