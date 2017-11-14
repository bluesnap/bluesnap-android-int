package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class BillingInfo extends ContactInfo implements Parcelable{
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
