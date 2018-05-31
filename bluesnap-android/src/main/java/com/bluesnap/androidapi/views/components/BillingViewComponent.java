package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class BillingViewComponent extends ContactInfoViewComponent {
    public static final String TAG = BillingViewComponent.class.getSimpleName();
    private boolean isEmailRequired;
    private boolean isFullBillingRequiredRequired;
    private boolean isShippingSameAsBilling = false;

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

        final SdkRequest sdkRequest = BlueSnapService.getInstance().getSdkRequest();

        isEmailRequired = sdkRequest.isEmailRequired();
        if (isEmailRequired) {
            inputEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        validateField(inputEmail, inputLayoutEmail, BlueSnapValidator.EditTextFields.EMAIL_FIELD);
                    }
                }
            });

            inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        checkTextInputLayoutVisibilityArray(new TextInputLayout[]{inputLayoutZip, inputLayoutCity, inputLayoutAddress});
                        return true;
                    }
                    return false;
                }
            });
        } else {
            setEmailVisibility(GONE);
        }

        isFullBillingRequiredRequired = sdkRequest.isBillingRequired();
        if (!isFullBillingRequiredRequired) {
            setFullBillingVisibility(GONE);
            inputZip.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        setInputNameNextFocusAndActionListener();
    }

    /**
     * update resource with details
     *
     * @param billingInfo - {@link BillingInfo}
     */
    public void updateResource(BillingInfo billingInfo) {
        super.updateResource(billingInfo);
        if (isEmailRequired)
            inputEmail.setText(AndroidUtil.stringify(billingInfo.getEmail()));
        setStateVisibilityByUserCountry();
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
        if (isCountryRequiresZip())
            validInput &= validateField(inputZip, inputLayoutZip, BlueSnapValidator.EditTextFields.ZIP_FIELD);
        if (isFullBillingRequiredRequired) {
            if (BlueSnapValidator.checkCountryHasState(getUserCountry()))
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


    /**
     * in Billing View if Email is required,
     * move focus from name to email
     */
    void setInputNameNextFocusAndActionListener() {
        inputName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateField(inputName, inputLayoutName, BlueSnapValidator.EditTextFields.NAME_FIELD);
                } else {
                    if (!isEmailRequired && !isFullBillingRequiredRequired && !isCountryRequiresZip())
                        inputName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    else
                        inputName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
            }
        });
        inputName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    checkTextInputLayoutVisibilityArray(new TextInputLayout[]{inputLayoutEmail, inputLayoutZip, inputLayoutCity, inputLayoutAddress});
                    return true;
                }
                return false;
            }
        });
    }

    public void setShippingSameAsBilling(boolean shippingSameAsBilling) {
        isShippingSameAsBilling = shippingSameAsBilling;
    }

    @Override
    protected void updateTaxOnCountryStateChange() {
        if (isShippingSameAsBilling)
            BlueSnapService.getInstance().updateTax(getUserCountry(), inputState.getText().toString(), getContext());
    }

    @Override
    void setStateVisibilityByUserCountry() {
        if (isFullBillingRequiredRequired && BlueSnapValidator.checkCountryHasState(getUserCountry()))
            setStateVisibility(VISIBLE);
        else
            setStateVisibility(GONE);
    }
}
