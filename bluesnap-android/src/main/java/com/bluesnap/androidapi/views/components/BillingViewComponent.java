package com.bluesnap.androidapi.views.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class BillingViewComponent extends ContactInfoViewComponent {
    public static final String TAG = BillingViewComponent.class.getSimpleName();
    private boolean isEmailRequired;
    private boolean isFullBillingRequiredRequired;

    public BillingViewComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BillingViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BillingViewComponent(Context context) {
        super(context);
    }

    @Override
    void initControl(final Context context) {
        super.initControl(context);

        final BlueSnapService blueSnapService = BlueSnapService.getInstance();
        final SdkRequest sdkRequest = blueSnapService.getSdkRequest();
        assert sdkRequest != null;

        isEmailRequired = blueSnapService.getSdkRequest().isEmailRequired();
        if (isEmailRequired) {
            changeInputNameNextFocusToInputEmail();
            inputEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        validateField(inputEmail, inputLayoutEmail, BlueSnapValidator.EditTextFields.EMAIL_FIELD);
                        inputZip.requestFocus();
                    }
                }
            });
        } else {
            setEmailVisibility(GONE);
        }

        isFullBillingRequiredRequired = blueSnapService.getSdkRequest().isBillingRequired();
        if (!isFullBillingRequiredRequired) {
            setFullBillingVisibility(GONE);
            changeInputZipNextFocusToDone();
        }
    }

    /**
     * update resource with details
     *
     * @param billingInfo - {@link BillingInfo}
     */
    public void updateResource(BillingInfo billingInfo) {
        super.updateResource(billingInfo);
        if (isEmailRequired)
            inputEmail.setText(billingInfo.getEmail());
    }

    /**
     * get BillingInfo Resource from inputs
     *
     * @return billing info
     */
    public BillingInfo getResource() {
        BillingInfo billingInfo = new BillingInfo(super.getResource());
        if (isEmailRequired)
            billingInfo.setEmail(inputEmail.getText().toString().trim());
        return billingInfo;
    }

    /**
     * Validating form inputs
     *
     * @return boolean
     */
    @Override
    public boolean validateInfo() {
        boolean validInput = validateField(inputName, inputLayoutName, BlueSnapValidator.EditTextFields.NAME_FIELD);
        validInput &= validateField(inputZip, inputLayoutZip, BlueSnapValidator.EditTextFields.ZIP_FIELD);
        if (isFullBillingRequiredRequired) {
            validInput &= validateField(inputState, inputLayoutState, BlueSnapValidator.EditTextFields.STATE_FIELD);
            validInput &= validateField(inputCity, inputLayoutCity, BlueSnapValidator.EditTextFields.CITY_FIELD);
            validInput &= validateField(inputAddress, inputLayoutAddress, BlueSnapValidator.EditTextFields.ADDRESS_FIELD);
        }
        if (isEmailRequired)
            validInput &= validateField(inputEmail, inputLayoutEmail, BlueSnapValidator.EditTextFields.EMAIL_FIELD);
        return validInput;
    }

    /**
     * set Full Billing Visibility
     *
     * @param visibility - GONE, VISIBLE, INVISIBLE
     */
    private void setFullBillingVisibility(int visibility) {
        setStateVisibility(visibility);
        setCityVisibility(visibility);
        setAddressVisibility(visibility);
    }
}
