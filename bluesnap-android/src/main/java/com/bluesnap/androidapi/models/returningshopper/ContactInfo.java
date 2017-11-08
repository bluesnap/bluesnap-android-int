package com.bluesnap.androidapi.models.returningshopper;

import android.support.annotation.Nullable;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class ContactInfo {
    private static final String TAG = ContactInfo.class.getSimpleName();
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String ADDRESS = "address1";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIP = "zip";
    private static final String COUNTRY = "country";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";


    private String firstName;
    private String lastName;
    private String address;
    private String city;
    @Nullable
    private String state;
    @Nullable
    private String zip;
    private String country;
    @Nullable
    private String phone;
    @Nullable
    private String email;

    public ContactInfo(@Nullable JSONObject shopperRepresentation) {
        if (null != shopperRepresentation) {
            firstName = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, FIRSTNAME, TAG);
            lastName = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, LASTNAME, TAG);
            address = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, ADDRESS, TAG);
            city = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, CITY, TAG);
            state = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, STATE, TAG);
            zip = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, ZIP, TAG);
            country = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, COUNTRY, TAG);
            phone = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, PHONE, TAG);
            email = (String) AndroidUtil.getObjectFromJsonObject(shopperRepresentation, EMAIL, TAG);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Nullable
    public String getState() {
        return state;
    }

    public void setState(@Nullable String state) {
        this.state = state;
    }

    @Nullable
    public String getZip() {
        return zip;
    }

    public void setZip(@Nullable String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }
}
