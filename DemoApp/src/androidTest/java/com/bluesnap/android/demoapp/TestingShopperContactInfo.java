package com.bluesnap.android.demoapp;

/**
 * Created by sivani on 28/07/2018.
 */

public class TestingShopperContactInfo {
    private String name;
    private String email;

    private String city;
    private String address;
    private String state;

    private String zip;
    private String countryKey;


    private String countryValue;


    public TestingShopperContactInfo(String name, String email, String city, String address,
                                     String state, String zip, String countryKey, String countryValue) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.address = address;
        this.state = state;
        this.zip = zip;
        this.countryKey = countryKey;
        this.countryValue = countryValue;

    }

    public TestingShopperContactInfo(TestingShopperContactInfo contactInfo) {
        name = contactInfo.name;
        email = contactInfo.email;
        city = contactInfo.city;
        address = contactInfo.address;
        state = contactInfo.state;
        zip = contactInfo.zip;
        countryKey = contactInfo.countryKey;
        countryValue = contactInfo.countryValue;

    }

    public String getFirstName() {
        if (this.name.isEmpty())
            return "";
        return name.substring(0, name.indexOf(" "));
    }

    public String getLastName() {
        if (this.name.isEmpty())
            return "";
        return name.substring(name.indexOf(" ") + 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountryKey() {
        return countryKey;
    }

    public void setCountryKey(String countryKey) {
        this.countryKey = countryKey;
    }

    public String getCountryValue() {
        return countryValue;
    }

    public void setCountryValue(String countryValue) {
        this.countryValue = countryValue;
    }

    public void resetAllFields() {
        this.setName("");
        this.setEmail("");
        this.setZip("");
        this.setState("");
        this.setCity("");
        this.setAddress("");
    }

    public void resetFullBillingFields() {
        this.setState("");
        this.setCity("");
        this.setAddress("");
    }
}



