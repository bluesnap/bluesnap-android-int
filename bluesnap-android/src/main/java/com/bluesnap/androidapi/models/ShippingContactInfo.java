package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class ShippingContactInfo extends ContactInfo implements Parcelable {
    public static final String SHIPPINGFIRSTNAME = "shippingFirstName";
    public static final String SHIPPINGLASTNAME = "shippingLastName";
    public static final String SHIPPINGCOUNTRY = "shippingCountry";
    public static final String SHIPPINGSTATE = "shippingState";
    public static final String SHIPPINGCITY = "shippingCity";
    public static final String SHIPPINGADDRESS = "shippingAddress";
    public static final String SHIPPINGZIP = "shippingZip";
    public static final String PHONE = "phone";

    @Nullable
    //@SerializedName("phone")
    private String phone;

    protected ShippingContactInfo(Parcel parcel) {
        super(parcel);
        phone = parcel.readString();
    }

    public ShippingContactInfo() {
        super();
    }

    public ShippingContactInfo(ContactInfo contactInfo) {
        setFullName(contactInfo.getFullName());
        setAddress(contactInfo.getAddress());
        setAddress2(contactInfo.getAddress2());
        setZip(contactInfo.getZip());
        setCity(contactInfo.getCity());
        setState(contactInfo.getState());
        setCountry(contactInfo.getCountry());
    }

    public static final Creator<ShippingContactInfo> CREATOR = new Creator<ShippingContactInfo>() {
        @Override
        public ShippingContactInfo createFromParcel(Parcel in) {
            return new ShippingContactInfo(in);
        }

        @Override
        public ShippingContactInfo[] newArray(int size) {
            return new ShippingContactInfo[size];
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
