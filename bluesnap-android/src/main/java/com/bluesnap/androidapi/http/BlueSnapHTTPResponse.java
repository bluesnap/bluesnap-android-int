package com.bluesnap.androidapi.http;

/**
 * Created by oz
 */

public class BlueSnapHTTPResponse {


    private int responseCode;
    private String responseString;
    private String errorResponseString;

    public BlueSnapHTTPResponse(int responseCode, String responseString) {
        this.responseCode = responseCode;
        this.responseString = responseString;
    }

    public BlueSnapHTTPResponse(int responseCode, String responseString, String errorResponseString) {
        this.responseCode = responseCode;
        this.responseString = responseString;
        this.errorResponseString = errorResponseString;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public String getErrorResponseString() {
        return errorResponseString;
    }
}
