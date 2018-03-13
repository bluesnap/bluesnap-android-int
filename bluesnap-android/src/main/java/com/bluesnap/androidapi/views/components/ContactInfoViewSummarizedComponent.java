package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.services.AndroidUtil;

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

        assert inflater != null;
        inflater.inflate(R.layout.contact_info_summerized_view_component, this);

        countryTextView = (TextView) findViewById(R.id.countryTextView);
        zipTextView = (TextView) findViewById(R.id.zipTextView);
        stateTextView = (TextView) findViewById(R.id.stateTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        forFullBillingLinearLayout = (LinearLayout) findViewById(R.id.forFullBillingLinearLayout);
        zipAndCountryLinearLayout = (LinearLayout) findViewById(R.id.zipAndCountryLinearLayout);
    }

    /**
     * update resource with details
     *
     * @param contactInfo - {@link ContactInfo}
     */
    public void updateResource(ContactInfo contactInfo) {
        updateResource(contactInfo.getFullName(), contactInfo.getAddress(), contactInfo.getCity(), contactInfo.getState(), contactInfo.getZip(), contactInfo.getCountry());
    }

    /**
     * update resource with details
     *
     * @param fullName
     * @param address
     * @param state
     * @param zip
     * @param country
     */
    private void updateResource(String fullName, String address, String city, String state, String zip, String country) {
        address = stringify(address);
        city = stringify(city);
        state = stringify(state);

        if(!address.isEmpty())
            address += ",";
        else if(address.isEmpty() && city.isEmpty() && state.isEmpty())
            forFullBillingLinearLayout.setVisibility(GONE);

        setCountryText(country);
        setZipText(zip);
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

    static String stringify(String s) {
        if (s == null || s.isEmpty())
            return "";
        else return s;
    }
}