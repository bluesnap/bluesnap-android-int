package com.bluesnap.androidapi.models.returningshopper;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roy.biber on 12/11/2017.
 */

public class BillingInfo extends ContactInfo {
    @Nullable
    @SerializedName("email")
    private String email;

    private BillingInfo(Parcel parcel) {
        super(parcel);
        email = parcel.readString();
    }

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
