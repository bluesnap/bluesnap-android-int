package com.bluesnap.androidapi.http;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created by oz
 */

public class BlueSnapHTTPResponse {


    private Map<String, List<String>> headers;
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

    public BlueSnapHTTPResponse(int responseCode, String responseString, Map<String, List<String>> headerFields) {
        this.responseCode = responseCode;
        this.responseString = responseString;
        this.headers = headerFields;
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

    @Nullable
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "BlueSnapHTTPResponse{" +
                "headers=" + headers +
                ", responseCode=" + responseCode +
                ", responseString='" + responseString + '\'' +
                ", errorResponseString='" + errorResponseString + '\'' +
                '}';
    }
}
