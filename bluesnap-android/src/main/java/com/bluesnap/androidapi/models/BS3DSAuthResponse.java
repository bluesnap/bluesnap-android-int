package com.bluesnap.androidapi.models;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;

/**
 * A representation of API auth response, matches Cardinal V1
 */
public class BS3DSAuthResponse extends  BSModel{

    private String enrollmentStatus;
    private String acsUrl;
    private String payload;
    private String transactionId;
    private String threeDSVersion;

    @Nullable
    public static BS3DSAuthResponse fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)  return null;
        BS3DSAuthResponse response = new BS3DSAuthResponse();
        response.setEnrollmentStatus(getOptionalString(jsonObject, "enrollmentStatus"));
        response.setAcsUrl(getOptionalString(jsonObject, "acsUrl"));
        response.setPayload(getOptionalString(jsonObject, "payload"));
        response.setTransactionId(getOptionalString(jsonObject, "transactionId"));
        response.setThreeDSVersion(getOptionalString(jsonObject, "threeDSVersion"));
        return response;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getThreeDSVersion() {
        return threeDSVersion;
    }

    public void setThreeDSVersion(String threeDSVersion) {
        this.threeDSVersion = threeDSVersion;
    }

    @Override
    public String toString() {
        return "BS3DSAuthResponse{" +
                "enrollmentStatus='" + enrollmentStatus + '\'' +
                ", acsUrl='" + acsUrl + '\'' +
                ", payload='" + payload + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", threeDSVersion='" + threeDSVersion + '\'' +
                '}';
    }
}
