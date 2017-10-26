package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Fragment to collect shipping information.
 */
public class ShippingFragment extends Fragment implements BluesnapPaymentFragment {
    public static final String AUTO_POPULATE_SHOPPER_NAME = "AUTO_POPULATE_SHOPPER_NAME";
    public static final String AUTO_POPULATE_ZIP = "AUTO_POPULATE_ZIP";
    public static final String AUTO_POPULATE_EMAIL = "AUTO_POPULATE_EMAIL";
    public static final String AUTO_POPULATE_ADDRESS = "AUTO_POPULATE_ADDRESS";
    public static final String AUTO_POPULATE_CITY = "AUTO_POPULATE_CITY";
    public static final String AUTO_POPULATE_STATE = "AUTO_POPULATE_STATE";
    public static final String AUTO_POPULATE_COUNTRY = "AUTO_POPULATE_COUNTRY";
    public static final String SHIPPING_TAG = String.valueOf(ShippingFragment.class.getSimpleName());
    static final String TAG = ShippingFragment.class.getSimpleName();
    private TextView totalAmountTextView;
    private EditText shippingNameEditText;
    private TextView invalidNameMessageTextView;
    private TextView shippingNameLabelTextView;
    private EditText shippingEmailEditText;
    private TextView invalidEmailMessageTextView;
    private TextView shippingEmailLabelTextView;
    private EditText shippingAddressLineEditText;
    private TextView invalidAddressMessageTextView;
    private TextView shippingAdressLabelTextView;
    private EditText shippingCityEditText;
    private TextView shippingCityLabelTextView;
    private EditText shippingStateEditText;
    private TextView shippingStateLabelTextView;
    private EditText shippingZipEditText;
    private TextView shippingZipLabelTextView;
    private LinearLayout shippingZipLinearLayout;
    private Button addressCountryButton;
    //private PrefsStorage prefsStorage;
    private ViewGroup subtotalView;
    private TextView subtotalValueTextView;
    private TextView taxValueTextView;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.bluesnap_shipping, container, false);
        shippingNameEditText = (EditText) inflate.findViewById(R.id.shippingNameEditText);
        shippingNameLabelTextView = (TextView) inflate.findViewById(R.id.shippingNameLabelTextView);
        invalidNameMessageTextView = (TextView) inflate.findViewById(R.id.invalidNameMessageTextView);
        shippingEmailEditText = (EditText) inflate.findViewById(R.id.shippingEmailEditText);
        shippingEmailLabelTextView = (TextView) inflate.findViewById(R.id.shippingEmailLabelTextView);
        invalidEmailMessageTextView = (TextView) inflate.findViewById(R.id.invalidShopperNameMessageTextView);
        shippingAddressLineEditText = (EditText) inflate.findViewById(R.id.shippingAddressLine);
        shippingAdressLabelTextView = (TextView) inflate.findViewById(R.id.addressLineLabelTextView);
        invalidAddressMessageTextView = (TextView) inflate.findViewById(R.id.invaildAddressMessageTextView);
        shippingCityEditText = (EditText) inflate.findViewById(R.id.shippingCityEditText);
        shippingCityLabelTextView = (TextView) inflate.findViewById(R.id.addressCityView);
        shippingStateEditText = (EditText) inflate.findViewById(R.id.shippingStateEditText);
        shippingStateLabelTextView = (TextView) inflate.findViewById(R.id.shippingStateLabelTextView);
        shippingZipEditText = (EditText) inflate.findViewById(R.id.shippingZipEditText);
        shippingZipLabelTextView = (TextView) inflate.findViewById(R.id.addressZipView);
        shippingZipLinearLayout = (LinearLayout) inflate.findViewById(R.id.shippingZipLinearLayout);
        addressCountryButton = (Button) inflate.findViewById(R.id.shippingAddressCountryButton);
        totalAmountTextView = (TextView) inflate.findViewById(R.id.shippingBuyNowButton);
        //prefsStorage = new PrefsStorage(inflate.getContext());
        subtotalView = (ViewGroup) inflate.findViewById(R.id.subtotal_tax_table_shipping);
        subtotalValueTextView = (TextView) inflate.findViewById(R.id.subtotalValueTextviewShipping);
        taxValueTextView = (TextView) inflate.findViewById(R.id.taxValueTextviewShipping);
        LinearLayout shippingFieldsLinearLayout = (LinearLayout) inflate.findViewById(R.id.shippingFieldsLinearLayout);
        AndroidUtil.hideKeyboardOnLayoutOfEditText(shippingFieldsLinearLayout);

        addressCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(inflate.getContext(), CountryActivity.class);
                newIntent.putExtra(getString(R.string.COUNTRY_STRING), getCountryText());
                startActivityForResult(newIntent, Activity.RESULT_FIRST_USER);
            }
        });
        return inflate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_FIRST_USER) {
            if (resultCode == Activity.RESULT_OK) {
                addressCountryButton.setText(data.getStringExtra("result"));
            }
        }
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            ActivateOnFocusValidation(shippingStateEditText);
        } else {
            shippingStateEditText.setOnFocusChangeListener(null);
        }
        changeZipTextAccordingToCountry();
    }

    @Subscribe
    public void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent) {
        String currencySymbol = AndroidUtil.getCurrencySymbol(currencyUpdatedEvent.newCurrencyNameCode);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        totalAmountTextView.setText(getResources().getString(R.string.pay) + " " + currencySymbol + " " + decimalFormat.format(currencyUpdatedEvent.updatedPrice));

        String taxValue = currencySymbol + String.valueOf(decimalFormat.format(currencyUpdatedEvent.updatedTax));
        taxValueTextView.setText(taxValue);
        String subtotal = currencySymbol + decimalFormat.format(currencyUpdatedEvent.updatedSubtotal);
        subtotalValueTextView.setText(subtotal);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PaymentRequest paymentRequest = BlueSnapService.getInstance().getPaymentRequest();
        Events.CurrencyUpdatedEvent currencyUpdatedEvent = new Events.CurrencyUpdatedEvent(paymentRequest.getAmount(), paymentRequest.getCurrencyNameCode(), paymentRequest.getTaxAmount(), paymentRequest.getSubtotalAmount());
        onCurrencyUpdated(currencyUpdatedEvent);

        boolean notax = (paymentRequest.getSubtotalAmount() == 0D || paymentRequest.getTaxAmount() == 0D);
        subtotalView.setVisibility(notax ? View.INVISIBLE : View.VISIBLE);

        totalAmountTextView.setOnClickListener(new ShippingSubmitClickListener());

        // ShippingInfo shippingInfo = (ShippingInfo) prefsStorage.getObject(Constants.SHIPPING_INFO, ShippingInfo.class);
        savedInstanceState = getArguments();
            shippingNameEditText.setText(savedInstanceState.getString(AUTO_POPULATE_SHOPPER_NAME));
            shippingZipEditText.setText(savedInstanceState.getString(AUTO_POPULATE_ZIP));
            shippingEmailEditText.setText(savedInstanceState.getString(AUTO_POPULATE_EMAIL));
            shippingAddressLineEditText.setText(savedInstanceState.getString(AUTO_POPULATE_ADDRESS));
            shippingCityEditText.setText(savedInstanceState.getString(AUTO_POPULATE_CITY));
            shippingStateEditText.setText(savedInstanceState.getString(AUTO_POPULATE_STATE));
            addressCountryButton.setText(
                    null != savedInstanceState.getString(AUTO_POPULATE_COUNTRY)
                            && !"".equals(savedInstanceState.getString(AUTO_POPULATE_COUNTRY))
                            ? savedInstanceState.getString(AUTO_POPULATE_COUNTRY)
                            : blueSnapService.getUserCountry(getActivity().getApplicationContext())
            );
            changeZipTextAccordingToCountry();

        ActivateOnFocusValidation(shippingNameEditText);
        ActivateOnFocusValidation(shippingAddressLineEditText);
        ActivateOnFocusValidation(shippingCityEditText);
        ActivateOnEditorActionListener(shippingCityEditText);
        ActivateOnFocusValidation(shippingZipEditText);
        ActivateOnFocusValidation(shippingEmailEditText);
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            ActivateOnFocusValidation(shippingStateEditText);
            ActivateOnEditorActionListener(shippingStateEditText);
        } else {
            shippingStateEditText.setOnFocusChangeListener(null);
        }

        AndroidUtil.setFocusOnLayoutOfEditText(shippingNameLabelTextView, shippingNameEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingEmailLabelTextView, shippingEmailEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingAdressLabelTextView, shippingAddressLineEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingZipLabelTextView, shippingZipEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingCityLabelTextView, shippingCityEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingStateLabelTextView, shippingStateEditText);
    }

    private boolean Validation(EditText editText) {
        if (editText.equals(shippingAddressLineEditText)) {
            return AndroidUtil.validateEditTextString(shippingAddressLineEditText, shippingAdressLabelTextView, invalidAddressMessageTextView);
        } else if (editText.equals(shippingCityEditText)) {
            return AndroidUtil.validateEditTextString(shippingCityEditText, shippingCityLabelTextView);
        } else if (editText.equals(shippingStateEditText)) {
            return AndroidUtil.validateEditTextString(shippingStateEditText, shippingStateLabelTextView);
        } else if (editText.equals(shippingZipEditText)) {
            return Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getCountryText())
                    || AndroidUtil.validateEditTextString(shippingZipEditText, shippingZipLabelTextView, AndroidUtil.ZIP_FIELD);
        } else if (editText.equals(shippingNameEditText)) {
            return AndroidUtil.validateEditTextString(shippingNameEditText, shippingNameLabelTextView, invalidNameMessageTextView, AndroidUtil.NAME_FIELD);
        } else if (editText.equals(shippingEmailEditText)) {
            return AndroidUtil.validateEditTextString(shippingEmailEditText, shippingEmailLabelTextView, invalidEmailMessageTextView, AndroidUtil.EMAIL_FIELD);
        }
        return false;
    }

    private void ActivateOnFocusValidation(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Validation(editText);
                }
            }
        });
    }

    private void ActivateOnEditorActionListener(final EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    Validation(editText);
                return false;
            }
        });
    }

    private boolean checkStateValidation() {
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            return Validation(shippingStateEditText);
        } else {
            shippingStateLabelTextView.setTextColor(Color.BLACK);
            return true;
        }
    }

    private String getCountryText() {
        return AndroidUtil.stringify(addressCountryButton.getText()).trim();
    }

    private void changeZipTextAccordingToCountry() {
        // check if usa if so change zip text to postal code otherwise billing zip
        if (Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getCountryText())) {
            shippingZipLinearLayout.setVisibility(View.INVISIBLE);
            shippingZipEditText.setText("");
        } else {
            shippingZipLinearLayout.setVisibility(View.VISIBLE);
            shippingZipLabelTextView.setText(
                    AndroidUtil.STATE_NEEDED_COUNTRIES[0].equals(getCountryText())
                            ? R.string.postal_code_hint
                            : R.string.zip
            );
        }
    }

    private void setFocusOnShippingFragmentEditText(final CreditCardFields checkWhichFieldIsInValid) {
        switch (checkWhichFieldIsInValid) {
            case SHIPPINGNAMEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingNameEditText);
                break;
            case SHIPPINGEMAILEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingEmailEditText);
                break;
            case SHIPPINGADDRESSLINEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingAddressLineEditText);
                break;
            case SHIPPINGZIPEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingZipEditText);
                break;
            case SHIPPINGCITYEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingCityEditText);
                break;
            case SHIPPINGSTATEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shippingStateEditText);
                break;
            default:
                break;
        }
    }

    private enum CreditCardFields {
        SHIPPINGNAMEEDITTEXT, SHIPPINGEMAILEDITTEXT, SHIPPINGADDRESSLINEEDITTEXT, SHIPPINGZIPEDITTEXT, SHIPPINGCITYEDITTEXT, SHIPPINGSTATEEDITTEXT, DEFAULT
    }

    private class ShippingSubmitClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CreditCardFields checkWhichFieldIsInValid = CreditCardFields.DEFAULT;

            boolean validInput = Validation(shippingNameEditText);
            if (!validInput) checkWhichFieldIsInValid = CreditCardFields.SHIPPINGNAMEEDITTEXT;
            validInput &= Validation(shippingEmailEditText);
            if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                checkWhichFieldIsInValid = CreditCardFields.SHIPPINGEMAILEDITTEXT;
            validInput &= Validation(shippingAddressLineEditText);
            if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                checkWhichFieldIsInValid = CreditCardFields.SHIPPINGADDRESSLINEEDITTEXT;
            validInput &= Validation(shippingZipEditText);
            if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                checkWhichFieldIsInValid = CreditCardFields.SHIPPINGZIPEDITTEXT;
            validInput &= Validation(shippingCityEditText);
            if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                checkWhichFieldIsInValid = CreditCardFields.SHIPPINGCITYEDITTEXT;
            validInput &= checkStateValidation();
            if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                checkWhichFieldIsInValid = CreditCardFields.SHIPPINGSTATEEDITTEXT;

            if (!checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
                setFocusOnShippingFragmentEditText(checkWhichFieldIsInValid);

            if (validInput) {
                ShippingInfo shippingInfo = new ShippingInfo();
                shippingInfo.setName(shippingNameEditText.getText().toString().trim());
                shippingInfo.setAddressLine(shippingAddressLineEditText.getText().toString().trim());
                shippingInfo.setShippingCity(shippingCityEditText.getText().toString().trim());
                shippingInfo.setState(shippingStateEditText.getText().toString().trim());
                shippingInfo.setCountry(getCountryText());
                shippingInfo.setZip(shippingZipEditText.getText().toString().trim());
                shippingInfo.setEmail(shippingEmailEditText.getText().toString().trim());
                BluesnapCheckoutActivity bluesnapCheckoutActivity = (BluesnapCheckoutActivity) getActivity();
                bluesnapCheckoutActivity.finishFromShippingFragment(shippingInfo);
            }
        }
    }
}
