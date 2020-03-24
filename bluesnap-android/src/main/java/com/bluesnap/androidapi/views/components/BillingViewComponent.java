package com.bluesnap.androidapi.views.components;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.SdkRequestBase;
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
        final SdkRequestBase sdkRequest = BlueSnapService.getInstance().getSdkRequest();
        isFullBillingRequiredRequired = sdkRequest.getShopperCheckoutRequirements().isBillingRequired();

        super.initControl(context);

        isEmailRequired = sdkRequest.getShopperCheckoutRequirements().isEmailRequired();
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

        if (!isFullBillingRequiredRequired) {
            setFullBillingVisibility(GONE);
            inputZip.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        setInputNameNextFocusAndActionListener();
    }

    /**
     * update resource with details
     *
     * @param billingContactInfo - {@link BillingContactInfo}
     */
    public void updateViewResourceWithDetails(BillingContactInfo billingContactInfo) {
        super.updateViewResourceWithDetails(billingContactInfo);
        if (isEmailRequired)
            inputEmail.setText(AndroidUtil.stringify(billingContactInfo.getEmail()));
    }

    /**
     * get BillingContactInfo Resource from inputs
     *
     * @return billing info
     */
    public BillingContactInfo getViewResourceDetails() {
        BillingContactInfo billingContactInfo = new BillingContactInfo(super.getViewResourceDetails());
        if (isEmailRequired) {
            String email = inputEmail.getText().toString().trim();
            billingContactInfo.setEmail(email);
        }
        return billingContactInfo;
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
     * get First Error Enabled of TextInputEditText
     *
     * @return TextInputLayout.getTop() or -1 if null
     */
    @Override
    public int getFirstErrorEnabledOfTextInputEditTextTopPosition() {
        if (inputLayoutName.isErrorEnabled())
            return inputLayoutName.getTop();
        else if (isEmailRequired && inputLayoutEmail.isErrorEnabled())
            return inputLayoutEmail.getTop();
        else if (isCountryRequiresZip() && inputLayoutZip.isErrorEnabled())
            return inputLayoutZip.getTop();
        else if (isFullBillingRequiredRequired) {
            if (BlueSnapValidator.checkCountryHasState(getUserCountry()) && inputLayoutState.isErrorEnabled())
                return inputLayoutState.getTop();
            else if (inputLayoutCity.isErrorEnabled())
                return inputLayoutCity.getTop();
            else if (inputLayoutAddress.isErrorEnabled())
                return inputLayoutAddress.getTop();
            else
                return inputLayoutName.getTop();
        } else
            return inputLayoutName.getTop();
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
        setStateVisibilityByUserCountry("");
    }

    @Override
    void setStateVisibilityByUserCountry(String state) {
        if (isFullBillingRequiredRequired && BlueSnapValidator.checkCountryHasState(getUserCountry())) {
            setStateVisibility(VISIBLE);
            setState(state);
        } else
            setStateVisibility(GONE);
    }

}
