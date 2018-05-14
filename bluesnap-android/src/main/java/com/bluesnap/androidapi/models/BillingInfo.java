package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class BillingInfo extends ContactInfo implements Parcelable {
    public static final String BILLINGFIRSTNAME = "billingFirstName";
    public static final String BILLINGLASTNAME = "billingLastName";
    public static final String BILLINGCOUNTRY = "billingCountry";
    public static final String BILLINGSTATE = "billingState";
    public static final String BILLINGCITY = "billingCity";
    public static final String BILLINGADDRESS = "billingAddress";
    public static final String BILLINGZIP = "billingZip";
    public static final String EMAIL = "email";

    @Nullable
    @SerializedName("email")
    private String email;

    protected BillingInfo(Parcel parcel) {
        super(parcel);
        email = parcel.readString();
    }

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

    public BillingInfo() {
        super();
    }

    public BillingInfo(ContactInfo contactInfo) {
        setFullName(contactInfo.getFullName());
        setAddress(contactInfo.getAddress());
        setAddress2(contactInfo.getAddress2());
        setZip(contactInfo.getZip());
        setCity(contactInfo.getCity());
        setState(contactInfo.getState());
        setCountry(contactInfo.getCountry());
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(email);
    }

    @Override
    public String toString() {
        return "{" +
                "firstName='" + super.getFirstName() + '\'' +
                ", lastName='" + super.getLastName() + '\'' +
                ", address='" + super.getAddress() + '\'' +
                ", city='" + super.getCity() + '\'' +
                ", state='" + super.getState() + '\'' +
                ", zip='" + super.getZip() + '\'' +
                ", country='" + super.getCountry() + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }


}
