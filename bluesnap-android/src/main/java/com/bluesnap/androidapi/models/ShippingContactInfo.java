package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;

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

    @Nullable
    public static ShippingContactInfo fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;
        ShippingContactInfo shippingContactInfo = new ShippingContactInfo();
        shippingContactInfo.setPhone(getOptionalString(jsonObject, "phone"));
        shippingContactInfo.setFirstName(getOptionalString(jsonObject, "firstName"));
        shippingContactInfo.setLastName(getOptionalString(jsonObject, "lastName"));
        shippingContactInfo.setAddress(getOptionalString(jsonObject, "address1"));
        shippingContactInfo.setAddress2(getOptionalString(jsonObject, "address2"));
        shippingContactInfo.setCity(getOptionalString(jsonObject, "city"));
        shippingContactInfo.setState(getOptionalString(jsonObject, "state"));
        shippingContactInfo.setZip(getOptionalString(jsonObject, "zip"));
        shippingContactInfo.setCountry(getOptionalString(jsonObject, "country"));
        shippingContactInfo.setFullName(getOptionalString(jsonObject, "fullname"));
        return shippingContactInfo;
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
