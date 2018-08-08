package com.bluesnap.androidapi.http;

/**
 * Created by oz
 */

public class CustomHTTPParams {

    private String key;
    private String value;

    public CustomHTTPParams(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
