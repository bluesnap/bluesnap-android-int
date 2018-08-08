package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import org.json.JSONObject;

import static com.bluesnap.androidapi.utils.JsonParser.getOptionalString;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class BillingContactInfo extends ContactInfo implements Parcelable {
    public static final String BILLINGFIRSTNAME = "billingFirstName";
    public static final String BILLINGLASTNAME = "billingLastName";
    public static final String BILLINGCOUNTRY = "billingCountry";
    public static final String BILLINGSTATE = "billingState";
    public static final String BILLINGCITY = "billingCity";
    public static final String BILLINGADDRESS = "billingAddress";
    public static final String BILLINGZIP = "billingZip";
    public static final String EMAIL = "email";

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
        BillingContactInfo billingContactInfo = null;
        billingContactInfo = new BillingContactInfo(contactInfo);
        billingContactInfo.setEmail(getOptionalString(jsonObject, "email"));
        return billingContactInfo;

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
