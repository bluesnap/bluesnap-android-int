package com.bluesnap.androidapi.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProcessingInfo implements Serializable {

    public String cvvResponseCode;
    public String avsResponseCodeZip;
    public String avsResponseCodeAddress;
    public String avsResponseCodeName;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 8309570729030941519L;

    /**
     * No args constructor for use in serialization
     */
    public ProcessingInfo() {
    }

    /**
     * @param cvvResponseCode
     * @param avsResponseCodeAddress
     * @param avsResponseCodeZip
     * @param avsResponseCodeName
     */
    public ProcessingInfo(String cvvResponseCode, String avsResponseCodeZip, String avsResponseCodeAddress, String avsResponseCodeName) {
        super();
        this.cvvResponseCode = cvvResponseCode;
        this.avsResponseCodeZip = avsResponseCodeZip;
        this.avsResponseCodeAddress = avsResponseCodeAddress;
        this.avsResponseCodeName = avsResponseCodeName;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}