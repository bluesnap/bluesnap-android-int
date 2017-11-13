package com.bluesnap.androidapi.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class ProcessingInfo {

    @Nullable
    @SerializedName("cvvResponseCode")
    private String cvvResponseCode;
    @Nullable
    @SerializedName("avsResponseCodeZip")
    private String avsResponseCodeZip;
    @Nullable
    @SerializedName("avsResponseCodeAddress")
    private String avsResponseCodeAddress;
    @Nullable
    @SerializedName("avsResponseCodeName")
    private String avsResponseCodeName;

    @Nullable
    public String getCvvResponseCode() {
        return cvvResponseCode;
    }

    public void setCvvResponseCode(@Nullable String cvvResponseCode) {
        this.cvvResponseCode = cvvResponseCode;
    }

    @Nullable
    public String getAvsResponseCodeZip() {
        return avsResponseCodeZip;
    }

    public void setAvsResponseCodeZip(@Nullable String avsResponseCodeZip) {
        this.avsResponseCodeZip = avsResponseCodeZip;
    }

    @Nullable
    public String getAvsResponseCodeAddress() {
        return avsResponseCodeAddress;
    }

    public void setAvsResponseCodeAddress(@Nullable String avsResponseCodeAddress) {
        this.avsResponseCodeAddress = avsResponseCodeAddress;
    }

    @Nullable
    public String getAvsResponseCodeName() {
        return avsResponseCodeName;
    }

    public void setAvsResponseCodeName(@Nullable String avsResponseCodeName) {
        this.avsResponseCodeName = avsResponseCodeName;
    }
}
