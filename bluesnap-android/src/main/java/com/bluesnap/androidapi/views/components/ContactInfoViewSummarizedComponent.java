package com.bluesnap.androidapi.views.components;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ContactInfoViewSummarizedComponent extends LinearLayout {
    public static final String TAG = ContactInfoViewSummarizedComponent.class.getSimpleName();
    private TextView countryTextView, zipTextView, stateTextView, cityTextView, addressTextView, emailTextView, nameTextView;
    LinearLayout forFullBillingLinearLayout;
    LinearLayout zipAndCountryLinearLayout;

    public ContactInfoViewSummarizedComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public ContactInfoViewSummarizedComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public ContactInfoViewSummarizedComponent(Context context) {
        super(context);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater == null) {
            Log.w(TAG, "inflater is null");
        } else {
            inflater.inflate(R.layout.contact_info_summerized_view_component, this);
        }

        countryTextView = findViewById(R.id.countryTextView);
        zipTextView = findViewById(R.id.zipTextView);
        stateTextView = findViewById(R.id.stateTextView);
        cityTextView = findViewById(R.id.cityTextView);
        addressTextView = findViewById(R.id.addressTextView);
        emailTextView = findViewById(R.id.emailTextView);
        nameTextView = findViewById(R.id.nameTextView);

        forFullBillingLinearLayout = findViewById(R.id.forFullBillingLinearLayout);
        zipAndCountryLinearLayout = findViewById(R.id.zipAndCountryLinearLayout);
    }

    /**
     * update resource with details
     *
     * @param contactInfo - {@link ContactInfo}
     */
    public void updateViewResourceWithDetails(@NonNull ContactInfo contactInfo) {
        updateViewResourceWithDetails(contactInfo.getFullName(), contactInfo.getAddress(), contactInfo.getCity(), contactInfo.getState(), contactInfo.getZip(), contactInfo.getCountry());
    }

    /**
     * update resource with details
     *
     * @param fullName - fullName
     * @param address  - address
     * @param state    - state
     * @param zip      - zip
     * @param country  - country
     */
    private void updateViewResourceWithDetails(String fullName, String address, String city, String state, String zip, String country) {
        fullName = stringify(fullName);
        address = stringify(address);
        city = stringify(city);
        state = stringify(state);
        zip = stringify(zip);
        country = stringify(country);

        if (!address.isEmpty())
            address += ",";
        else if (city.isEmpty() && state.isEmpty())
            forFullBillingLinearLayout.setVisibility(GONE);

        setCountryText(country);

        setZipText(zip);
        if (country.length() == 2)
            changeZipVisibilityAccordingToCountry(country.toUpperCase());

        setStateText(state);
        setCityText(city);
        setAddressText(address);
        setNameText(fullName);
    }

    private void setCountryText(String countryText) {
        this.countryTextView.setText(countryText.toUpperCase());
    }

    private void setZipText(String zipText) {
        this.zipTextView.setText(zipText);
    }

    private void setStateText(String stateText) {
        this.stateTextView.setText(stateText.toUpperCase());
    }

    private void setAddressText(String addressText) {
        this.addressTextView.setText(addressText);
    }

    private void setCityText(String cityText) {
        this.cityTextView.setText(cityText);
    }

    public void setEmailText(String emailText) {
        this.emailTextView.setText(emailText);
    }

    private void setNameText(String nameText) {
        this.nameTextView.setText(nameText);
    }

    public void setEmailVisibility(int visibility) {
        this.emailTextView.setVisibility(visibility);
    }

    /**
     * check if null, if so returns empty string
     *
     * @param s - String
     * @return same String or an empty one if String is Empty or null
     */
    static String stringify(String s) {
        if (s == null || s.isEmpty())
            return "";
        else return s;
    }

    /**
     * change Zip Visibility According To COUNTRIES_WITHOUT_ZIP {@link Constants}
     *
     * @param country - country upper case string ISO Alpha-2
     */
    void changeZipVisibilityAccordingToCountry(String country) {
        if (!BlueSnapValidator.checkCountryHasZip(country)) {
            zipTextView.setVisibility(View.GONE);
        } else {
            zipTextView.setVisibility(View.VISIBLE);
        }
    }

}
