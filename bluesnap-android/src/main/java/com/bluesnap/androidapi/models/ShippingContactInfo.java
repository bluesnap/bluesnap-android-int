package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;
import static com.bluesnap.androidapi.utils.JsonParser.putJSONifNotNull;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class ShippingContactInfo extends ContactInfo implements Parcelable {
    public static final String PHONE = "phone";
    public static final String ADDRESS_1 = "address1";

    @Nullable
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

        ContactInfo contactInfo = ContactInfo.fromJson(jsonObject);
        if (contactInfo == null) {
            return null;
        }

        ShippingContactInfo shippingContactInfo = new ShippingContactInfo(contactInfo);
        shippingContactInfo.setPhone(getOptionalString(jsonObject, PHONE));
        shippingContactInfo.setAddress(getOptionalString(jsonObject, ADDRESS_1));

        return shippingContactInfo;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(phone);
    }

    /**
     * create JSON object from Shipping Contact Info
     *
     * @return JSONObject
     */
    @NonNull
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        putJSONifNotNull(jsonObject, ADDRESS_1, getAddress());
        putJSONifNotNull(jsonObject, PHONE, getPhone());
        return jsonObject;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }


}
