package com.bluesnap.android.demoapp;

/**
 * Created by sivani on 28/07/2018.
 */

public class ShopperContactInfo {
    private String name;
    private String firstName;
    private String lastName;

    private String email;

    private String city;
    private String address;
    private String state;

    private String zip;
    private String country;

    public ShopperContactInfo(String name_, String email_, String city_, String address_,
                              String state_, String zip_, String country_) {
        name = name_;
        email = email_;
        city = city_;
        address = address_;
        state = state_;
        zip = zip_;
        country = country_;
    }

    public ShopperContactInfo(ShopperContactInfo contactInfo) {
        name = contactInfo.name;
        email = contactInfo.email;
        city = contactInfo.city;
        address = contactInfo.address;
        state = contactInfo.state;
        zip = contactInfo.zip;
        country = contactInfo.country;
    }

    public String getFirstName() {
        return name.substring(0, name.indexOf(" "));
    }

    public String getLastName() {
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}



