package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Collection of billing details
 */
public class BillingInfo implements Parcelable {
    public static final Creator<BillingInfo> CREATOR = new Creator<BillingInfo>() {
        @Override
        public BillingInfo createFromParcel(Parcel in) {
            return new BillingInfo(in);
        }

        @Override
        public BillingInfo[] newArray(int size) {
            return new BillingInfo[size];
        }
    };
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipCode;


    protected BillingInfo(Parcel parcel) {
        addressLine = parcel.readString();
        city = parcel.readString();
        state = parcel.readString();
        country = parcel.readString();
        zipCode = parcel.readString();
    }

    public BillingInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(addressLine);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(country);
        parcel.writeString(zipCode);
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getBillingCity() {
        return city;
    }

    public void setBillingCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingInfo that = (BillingInfo) o;

        if (!addressLine.equals(that.addressLine)) return false;
        if (!city.equals(that.city)) return false;
        if (!zipCode.equals(that.zipCode)) return false;
        return (!country.equals(that.country));

    }

    @Override
    public int hashCode() {
        int result = addressLine.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + zipCode.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BillingInfo{" +
                "addressLine='" + addressLine + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
