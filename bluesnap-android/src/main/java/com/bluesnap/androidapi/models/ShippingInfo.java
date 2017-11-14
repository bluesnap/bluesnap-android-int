package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class ShippingInfo extends ContactInfo implements Parcelable {
    @Nullable
    @SerializedName("phone")
    private String phone;

    protected ShippingInfo(Parcel parcel) {
        super(parcel);
        phone = parcel.readString();
    }

    public ShippingInfo() {
        super();
    }

    public static final Creator<ShippingInfo> CREATOR = new Creator<ShippingInfo>() {
        @Override
        public ShippingInfo createFromParcel(Parcel in) {
            return new ShippingInfo(in);
        }

        @Override
        public ShippingInfo[] newArray(int size) {
            return new ShippingInfo[size];
        }
    };

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(phone);
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
                ", phone='" + phone + '\'' +
                '}';
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }


}
