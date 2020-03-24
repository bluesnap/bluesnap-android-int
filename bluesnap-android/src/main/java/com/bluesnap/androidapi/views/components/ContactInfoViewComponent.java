package com.bluesnap.androidapi.views.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ContactInfoViewComponent extends LinearLayout {
    public static final String TAG = ContactInfoViewComponent.class.getSimpleName();
    TextInputEditText inputName, inputEmail, inputZip, inputState, inputCity, inputAddress;
    TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutZip, inputLayoutState, inputLayoutCity, inputLayoutAddress;
    ImageButton countryImageButton;
    //boolean hasAlreadyRequestedFocus;

    private String userCountry;

    public ContactInfoViewComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public ContactInfoViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public ContactInfoViewComponent(Context context) {
        super(context);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    void initControl(final Context context) {
        //hasAlreadyRequestedFocus = false;
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Log.w(TAG, "inflater is null");
        } else {
            inflater.inflate(R.layout.contact_info_view_component, this);
        }

        // layout is inflated, assign local variables to components
        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutZip = findViewById(R.id.input_layout_zip);
        inputLayoutState = findViewById(R.id.input_layout_state);
        inputLayoutCity = findViewById(R.id.input_layout_city);
        inputLayoutAddress = findViewById(R.id.input_layout_address);

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputZip = findViewById(R.id.input_zip);

        inputState = findViewById(R.id.input_state);
        inputState.setInputType(InputType.TYPE_NULL);
        inputState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.STATE_CHANGE_REQUEST,
                        getResources().getString(R.string.COUNTRY_STRING), getUserCountry(),
                        getResources().getString(R.string.STATE_STRING), getState(),
                        TAG);
            }
        });

        unregisterBlueSnapLocalBroadcastReceiver();
        registerBlueSnapLocalBroadcastReceiver();

        inputCity = findViewById(R.id.input_city);
        inputAddress = findViewById(R.id.input_address);

        countryImageButton = findViewById(R.id.countryImageButton);
        setUserCountry("");
        // activate all on focus out event listeners
        setOnFocusChangeListenerForInputs();
        // activate all on editor action listener IME_ACTION_NEXT
        setOnEditorActionListenerForInputs();

        countryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_REQUEST, getUserCountry(), TAG);
            }
        });
    }

    /**
     * Called when a child view is removed from this ViewGroup. Overrides should always
     * call super.onViewRemoved.
     *
     * @param child the removed child view
     */
    @Override
    public void onViewRemoved(View child) {
        unregisterBlueSnapLocalBroadcastReceiver();
        super.onViewRemoved(child);
    }

    /**
     * unregister broadcastReceiver for BlueSnap Local Broadcast Manager
     */
    public void unregisterBlueSnapLocalBroadcastReceiver() {
        BlueSnapLocalBroadcastManager.unregisterReceiver(getContext(), broadcastReceiver);
    }

    /**
     * register broadcastReceiver for BlueSnap Local Broadcast Manager
     */
    public void registerBlueSnapLocalBroadcastReceiver() {
        BlueSnapLocalBroadcastManager.registerReceiver(getContext(), BlueSnapLocalBroadcastManager.STATE_CHANGE_RESPONSE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getContext(), BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_RESPONSE, broadcastReceiver);
    }

    /**
     * update resource with details
     *
     * @param contactInfo - {@link ContactInfo}
     */
    public void updateViewResourceWithDetails(ContactInfo contactInfo) {
        inputName.setText(AndroidUtil.stringify(contactInfo.getFullName()));
        inputZip.setText(AndroidUtil.stringify(contactInfo.getZip()));
        inputCity.setText(AndroidUtil.stringify(contactInfo.getCity()));
        inputAddress.setText(AndroidUtil.stringify(contactInfo.getAddress()));
        setUserCountry(AndroidUtil.stringify(contactInfo.getCountry()));
        setStateVisibilityByUserCountry(AndroidUtil.stringify(contactInfo.getState()));
    }

    /**
     * get ContactInfo Resource from inputs
     *
     * @return contact info
     */
    public ContactInfo getViewResourceDetails() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setFullName(inputName.getText().toString().trim());
        //contactInfo.setEmail(inputEmail.getText().toString().trim());
        contactInfo.setZip(inputZip.getText().toString().trim());
        contactInfo.setState(inputState.getText().toString().trim());
        contactInfo.setCity(inputCity.getText().toString().trim());
        contactInfo.setAddress(inputAddress.getText().toString().trim());
        contactInfo.setCountry(getUserCountry());
        return contactInfo;
    }

    /**
     * Validating form inputs
     *
     * @return boolean
     */
    public boolean validateInfo() {
        boolean validInput = validateField(inputName, inputLayoutName, BlueSnapValidator.EditTextFields.NAME_FIELD);
        //validInput &= validateField(inputEmail, inputLayoutEmail, BlueSnapValidator.EditTextFields.EMAIL_FIELD);
        if (isCountryRequiresZip())
            validInput &= validateField(inputZip, inputLayoutZip, BlueSnapValidator.EditTextFields.ZIP_FIELD);
        if (BlueSnapValidator.checkCountryHasState(getUserCountry()))
            validInput &= validateField(inputState, inputLayoutState, BlueSnapValidator.EditTextFields.STATE_FIELD);
        validInput &= validateField(inputCity, inputLayoutCity, BlueSnapValidator.EditTextFields.CITY_FIELD);
        validInput &= validateField(inputAddress, inputLayoutAddress, BlueSnapValidator.EditTextFields.ADDRESS_FIELD);
        return validInput;
    }

    /**
     * get First Error Enabled of TextInputEditText
     *
     * @return TextInputLayout.getTop() or -1 if null
     */
    public int getFirstErrorEnabledOfTextInputEditTextTopPosition() {
        if (inputLayoutName.isErrorEnabled())
            return inputLayoutName.getTop();
        else if (isCountryRequiresZip() && inputLayoutZip.isErrorEnabled())
            return inputLayoutZip.getTop();
        else if (BlueSnapValidator.checkCountryHasState(getUserCountry()) && inputLayoutState.isErrorEnabled())
            return inputLayoutState.getTop();
        else if (inputLayoutCity.isErrorEnabled())
            return inputLayoutCity.getTop();
        else if (inputLayoutAddress.isErrorEnabled())
            return inputLayoutAddress.getTop();
        else
            return inputLayoutName.getTop();
    }

    /**
     * get Error Message
     *
     * @param validationType - {@link BlueSnapValidator.EditTextFields}
     * @return error resource string
     */
    private String getErrorMsg(BlueSnapValidator.EditTextFields validationType) {
        Context context = getContext();
        String errorMsg = context.getString(R.string.err_msg_name);
        if (validationType.equals(BlueSnapValidator.EditTextFields.EMAIL_FIELD))
            errorMsg = context.getString(R.string.err_msg_email);
        else if (validationType.equals(BlueSnapValidator.EditTextFields.ZIP_FIELD))
            errorMsg = context.getString(R.string.err_msg_zip);
        else if (validationType.equals(BlueSnapValidator.EditTextFields.STATE_FIELD))
            errorMsg = context.getString(R.string.err_msg_state);
        else if (validationType.equals(BlueSnapValidator.EditTextFields.CITY_FIELD))
            errorMsg = context.getString(R.string.err_msg_city);
        else if (validationType.equals(BlueSnapValidator.EditTextFields.ADDRESS_FIELD))
            errorMsg = context.getString(R.string.err_msg_address);
        return errorMsg;
    }

    /**
     * validate input field
     *
     * @param editText        - input field
     * @param textInputLayout - containing input layout
     * @param validationType  - {@link BlueSnapValidator.EditTextFields}
     * @return boolean
     */
    boolean validateField(TextInputEditText editText, TextInputLayout textInputLayout, BlueSnapValidator.EditTextFields validationType) {
        if (!BlueSnapValidator.validateEditTextString(editText.getText().toString(), validationType)) {
            //TODO: This may throw exception to the log if not checked for empty strings
            textInputLayout.setError(getErrorMsg(validationType));
            /*if (!hasAlreadyRequestedFocus) {
                hasAlreadyRequestedFocus = true;
                AndroidUtil.setFocusOnFirstErrorInput(textInputLayout);
            }*/
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * set On Focus Change Listener For All Inputs
     */
    void setOnFocusChangeListenerForInputs() {

        inputName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateField(inputName, inputLayoutName, BlueSnapValidator.EditTextFields.NAME_FIELD);
                }
            }
        });

        inputZip.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateField(inputZip, inputLayoutZip, BlueSnapValidator.EditTextFields.ZIP_FIELD);
                }
            }
        });

        setStateVisibilityByUserCountry();

        inputCity.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateField(inputCity, inputLayoutCity, BlueSnapValidator.EditTextFields.CITY_FIELD);
                }
            }
        });

        inputAddress.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    validateField(inputAddress, inputLayoutAddress, BlueSnapValidator.EditTextFields.ADDRESS_FIELD);
            }
        });
    }

    /**
     * set On Focus Change Listener For State Input according to Country
     */
    void setStateVisibilityByUserCountry() {
        setStateVisibilityByUserCountry("");
    }

    /**
     * set On Focus Change Listener For State Input according to Country
     */
    void setStateVisibilityByUserCountry(String state) {
        if (BlueSnapValidator.checkCountryHasState(getUserCountry())) {
            setStateVisibility(VISIBLE);
            setState(state);
        } else {
            setStateVisibility(GONE);
        }
    }

    /**
     * This should be overridden for shipping contact details
     */
    protected void updateTaxOnCountryStateChange() {

    }

    /**
     * set Email Visibility
     *
     * @param visibility - GONE, VISIBLE, INVISIBLE
     */
    void setEmailVisibility(int visibility) {
        this.inputLayoutEmail.setVisibility(visibility);
    }

    /**
     * set State Visibility
     *
     * @param visibility - GONE, VISIBLE, INVISIBLE
     */
    void setStateVisibility(int visibility) {
        this.inputLayoutState.setVisibility(visibility);
    }

    /**
     * set City Visibility
     *
     * @param visibility - GONE, VISIBLE, INVISIBLE
     */
    void setCityVisibility(int visibility) {
        this.inputLayoutCity.setVisibility(visibility);
    }

    /**
     * set Address Visibility
     *
     * @param visibility - GONE, VISIBLE, INVISIBLE
     */
    void setAddressVisibility(int visibility) {
        this.inputLayoutAddress.setVisibility(visibility);
    }

    /**
     * set Country Drawable
     *
     * @param countryString - country string ISO Alpha-2
     * @param context       - {@link Context}
     */
    void setCountryDrawable(String countryString, Context context) {
        int countryId = getResources().getIdentifier(countryString.toLowerCase(), "drawable", context.getPackageName());
        // int The associated resource identifier.  Returns 0 if no such resource was found.  (0 is not a valid resource ID.)
        if (countryId > 0)
            countryImageButton.setImageDrawable(getResources().getDrawable(countryId));
        else
            countryImageButton.setImageDrawable(getResources().getDrawable(R.drawable.unknown));
    }

    /**
     * get User Country
     *
     * @return country string ISO Alpha-2
     */
    public String getUserCountry() {
        return userCountry.toUpperCase();
    }

    /**
     * set User Country
     *
     * @param userCountry - country string ISO Alpha-2
     */
    public void setUserCountry(String userCountry) {
        this.userCountry = AndroidUtil.stringify(userCountry, BlueSnapService.getInstance().getUserCountry(getContext()));
        onCountryChange();
    }

    /**
     * is Country Requires Zip
     *
     * @return boolean
     */
    boolean isCountryRequiresZip() {
        return (BlueSnapValidator.checkCountryHasZip(getUserCountry()));
    }

    /**
     * change Zip Hint And Text According To Country
     * USA -> Postal Code
     * Other -> Zip
     */
    void changeZipHintAndTextAccordingToCountry() {
        if (!isCountryRequiresZip()) {
            inputLayoutZip.setVisibility(View.GONE);
        } else {
            inputLayoutZip.setVisibility(View.VISIBLE);
            // check if usa if so change zip text to postal code otherwise billing zip
            inputLayoutZip.setHint(BlueSnapValidator.STATE_NEEDED_COUNTRIES[0].equals(getUserCountry())
                    ? getResources().getString(R.string.postal_code_hint)
                    : getResources().getString(R.string.zip));
        }
    }

    /**
     * on Country Change set Country Drawable and activate change Zip Hint And Text
     */
    void onCountryChange() {
        setCountryDrawable(getUserCountry(), getContext());
        changeZipHintAndTextAccordingToCountry();
        updateTaxOnCountryStateChange();
    }

    /**
     * request Focus On Name Input
     */
    public void requestFocusOnNameInput() {
        inputName.requestFocus();
    }

    /**
     * set On Editor Action Listener IME_ACTION_NEXT For All Inputs
     */
    void setOnEditorActionListenerForInputs() {
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

        inputZip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    checkTextInputLayoutVisibilityArray(new TextInputLayout[]{inputLayoutCity, inputLayoutAddress});
                    return true;
                }
                return false;
            }
        });

        /*inputState.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    checkTextInputLayoutVisibilityArray(new TextInputLayout[]{inputLayoutCity, inputLayoutAddress});
                    return true;
                }
                return false;
            }
        });*/
        inputCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    checkTextInputLayoutVisibilityArray(new TextInputLayout[]{inputLayoutAddress});
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * check TextInputLayout Visibility Array
     *
     * @param textInputLayouts - array of TextInputLayout to check
     */
    void checkTextInputLayoutVisibilityArray(TextInputLayout[] textInputLayouts) {
        for (TextInputLayout textInputLayout : textInputLayouts) {
            if (textInputLayout.getVisibility() == VISIBLE) {
                textInputLayout.getChildAt(0).requestFocus();
                break;
            }
        }
    }

    /**
     * Broadcast Receiver for Credit Card Activity
     * Handles actions
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String event = intent.getAction();
            Log.d(TAG, event);
            if (BlueSnapLocalBroadcastManager.COUNTRY_CHANGE_RESPONSE.equals(event)) {
                setUserCountry(intent.getStringExtra(event));
                setStateVisibilityByUserCountry();
            } else {
                setState(intent.getStringExtra(event));
                updateTaxOnCountryStateChange();
            }
        }
    };

    public String getState() {
        return inputState.getText().toString().trim();
    }

    public void setState(String state) {
        this.inputState.setText(state);
        if (!"".equals(state))
            validateField(this.inputState, this.inputLayoutState, BlueSnapValidator.EditTextFields.STATE_FIELD);

    }
}

