package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapValidator;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;


/**
 * Created by roy.biber on 07/11/2017.
 */

public class ContactInfo extends BSModel implements Parcelable {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String ADDRESS_2 = "address2";
    public static final String STATE = "state";
    public static final String ZIP = "zip";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    private String firstName;
    private String lastName;
    @Nullable
    private String address;
    @Nullable
    private String address2;
    @Nullable
    private String city;
    @Nullable
    private String state;
    @Nullable
    private String zip;
    @Nullable
    private String country;

    public ContactInfo() {
    }

    public ContactInfo(ContactInfo contactInfo) {
        setFirstName(contactInfo.getFirstName());
        setLastName(contactInfo.getLastName());
        setAddress(contactInfo.getAddress());
        setAddress2(contactInfo.getAddress2());
        setZip(contactInfo.getZip());
        setCity(contactInfo.getCity());
        setState(contactInfo.getState());
        setCountry(contactInfo.getCountry());
    }

    public String getFullName() {
        return AndroidUtil.stringify(firstName) + " " + AndroidUtil.stringify(lastName);
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
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(@Nullable String address2) {
        this.address2 = address2;
    }

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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        return firstName.equals(that.firstName)
                && (!lastName.equals(that.lastName));
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }

    protected ContactInfo(Parcel parcel) {
        firstName = parcel.readString();
        lastName = parcel.readString();
        address = parcel.readString();
        city = parcel.readString();
        state = parcel.readString();
        zip = parcel.readString();
        country = parcel.readString();
    }

    @Nullable
    public static ContactInfo fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setFirstName(getOptionalString(jsonObject, FIRST_NAME));
        contactInfo.setLastName(getOptionalString(jsonObject, LAST_NAME));
        contactInfo.setAddress(getOptionalString(jsonObject, ADDRESS));
        contactInfo.setAddress2(getOptionalString(jsonObject, ADDRESS_2));
        contactInfo.setState(getOptionalString(jsonObject, STATE));
        contactInfo.setZip(getOptionalString(jsonObject, ZIP));
        contactInfo.setCountry(getOptionalString(jsonObject, COUNTRY));
        contactInfo.setCity(getOptionalString(jsonObject, CITY));
        return contactInfo;
    }

    /**
     * create JSON object from Contact Info
     *
     * @return JSONObject
     */
    @Override
    @NonNull
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        putJSONifNotNull(jsonObject, FIRST_NAME, getFirstName());
        putJSONifNotNull(jsonObject, LAST_NAME, getLastName());
        putJSONifNotNull(jsonObject, COUNTRY, getCountry());
        if (BlueSnapValidator.checkCountryHasState(getCountry()))
            putJSONifNotNull(jsonObject, STATE, getState());
        putJSONifNotNull(jsonObject, ADDRESS, getAddress());
        putJSONifNotNull(jsonObject, ADDRESS_2, getAddress2());
        putJSONifNotNull(jsonObject, CITY, getCity());
        putJSONifNotNull(jsonObject, ZIP, getZip());

        return jsonObject;
    }
}
