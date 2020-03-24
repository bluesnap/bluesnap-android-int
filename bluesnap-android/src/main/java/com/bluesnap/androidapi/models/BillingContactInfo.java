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

public class BillingContactInfo extends ContactInfo implements Parcelable {
    private static final String EMAIL = "email";
    public static final String ADDRESS_1 = "address1";

    @Nullable
    //@SerializedName("email")
    private String email;

    protected BillingContactInfo(Parcel parcel) {
        super(parcel);
        email = parcel.readString();
    }

    public static final Creator<BillingContactInfo> CREATOR = new Creator<BillingContactInfo>() {
        @Override
        public BillingContactInfo createFromParcel(Parcel in) {
            return new BillingContactInfo(in);
        }

        @Override
        public BillingContactInfo[] newArray(int size) {
            return new BillingContactInfo[size];
        }
    };

    public BillingContactInfo() {
        super();
    }

    public BillingContactInfo(ContactInfo contactInfo) {
        setFullName(contactInfo.getFullName());
        setAddress(contactInfo.getAddress());
        setAddress2(contactInfo.getAddress2());
        setZip(contactInfo.getZip());
        setCity(contactInfo.getCity());
        setState(contactInfo.getState());
        setCountry(contactInfo.getCountry());
    }

    @Nullable
    public static BillingContactInfo fromJson(@Nullable JSONObject jsonObject) {
        if (jsonObject == null)
            return null;

        ContactInfo contactInfo = ContactInfo.fromJson(jsonObject);
        if (contactInfo == null) {
            return null;
        }
        BillingContactInfo billingContactInfo = new BillingContactInfo(contactInfo);
        billingContactInfo.setAddress(getOptionalString(jsonObject, ADDRESS_1));
        billingContactInfo.setEmail(getOptionalString(jsonObject, EMAIL));
        return billingContactInfo;

    }

    /**
     * create JSON object from Billing Contact Info
     *
     * @return JSONObject
     */
    @NonNull
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = super.toJson();
        putJSONifNotNull(jsonObject, ADDRESS_1, getAddress());
        putJSONifNotNull(jsonObject, EMAIL, getEmail());
        return jsonObject;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(email);
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }


}
