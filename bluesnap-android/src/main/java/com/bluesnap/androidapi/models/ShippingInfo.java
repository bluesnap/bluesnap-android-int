package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class ShippingInfo extends ContactInfo {
    @Nullable
    @SerializedName("phone")
    private String phone;

    private ShippingInfo(Parcel parcel) {
        super(parcel);
        phone = parcel.readString();
    }

    public ShippingInfo() {
        super();
    }

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
