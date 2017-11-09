package com.bluesnap.androidapi.models.returningshopper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.json.JSONObject;

/**
 * Created by roy.biber on 07/11/2017.
 */

public class ContactInfo implements Parcelable {
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
    @Nullable
    private String address;
    @Nullable
    private String city;
    @Nullable
    private String state;
    @Nullable
    private String zip;
    @Nullable
    private String country;
    @Nullable
    private String phone;
    @Nullable
    private String email;

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(address);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(zip);
        parcel.writeString(country);
        parcel.writeString(phone);
        parcel.writeString(email);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        return firstName.equals(that.firstName)
                && (!lastName.equals(that.lastName))
                /*&& (!address.equals(that.address))
                && (!city.equals(that.city))
                && (!state.equals(that.state))
                && (!zip.equals(that.zip))
                && (!country.equals(that.country))
                && (!phone.equals(that.phone))
                && (!email.equals(that.email))*/
                ;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        /*result = 31 * result + address.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + zip.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + email.hashCode();*/
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "firstName:'" + firstName + '\'' +
                ", lastName:'" + lastName + '\'' +
                ", address:'" + address + '\'' +
                ", city:'" + city + '\'' +
                ", state:'" + state + '\'' +
                ", zip:'" + zip + '\'' +
                ", country:'" + country + '\'' +
                ", phone:'" + phone + '\'' +
                ", email:'" + email + '\'' +
                '}';
    }

    private ContactInfo(Parcel parcel) {
        firstName = parcel.readString();
        lastName = parcel.readString();
        address = parcel.readString();
        city = parcel.readString();
        state = parcel.readString();
        zip = parcel.readString();
        country = parcel.readString();
        phone = parcel.readString();
        email = parcel.readString();
    }

    public ContactInfo(@Nullable JSONObject shopper) {
        firstName = (String) AndroidUtil.getObjectFromJsonObject(shopper, FIRSTNAME, TAG);
        lastName = (String) AndroidUtil.getObjectFromJsonObject(shopper, LASTNAME, TAG);
        address = (String) AndroidUtil.getObjectFromJsonObject(shopper, ADDRESS, TAG);
        city = (String) AndroidUtil.getObjectFromJsonObject(shopper, CITY, TAG);
        state = (String) AndroidUtil.getObjectFromJsonObject(shopper, STATE, TAG);
        zip = (String) AndroidUtil.getObjectFromJsonObject(shopper, ZIP, TAG);
        country = (String) AndroidUtil.getObjectFromJsonObject(shopper, COUNTRY, TAG);
        phone = (String) AndroidUtil.getObjectFromJsonObject(shopper, PHONE, TAG);
        email = (String) AndroidUtil.getObjectFromJsonObject(shopper, EMAIL, TAG);
    }

    public ContactInfo() {

    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        String[] nameFieldParts = fullName.trim().split(" ");
        this.firstName = nameFieldParts[0];
        if (nameFieldParts.length > 1)
            this.lastName = nameFieldParts[1];
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

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getCity() {
        return city;
    }

    public void setCity(@Nullable String city) {
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

    @Nullable
    public String getCountry() {
        return country;
    }

    public void setCountry(@Nullable String country) {
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
