package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.LastPaymentInfo;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypes;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by oz on 12/2/15.
 */
public class BluesnapFragment extends Fragment implements BluesnapPaymentFragment {
    public static final String TAG = BluesnapFragment.class.getSimpleName();
    static int invalidNumberInputFlag = 0;
    private static FragmentManager fragmentManager;
    private final TextWatcher creditCardEditorWatcher = new creditCardNumberWatcher(), mExpDateTextWatcher = new CardExpDateTextWatcher(), nameEditorWatcher = new NameEditorWatcher();
    private LinearLayout zipFieldLayout, emailFieldLayout, couponLayout, creditCardNumberLayoutSpinner;
    private Spinner creditCardNumberSpinner;
    private TableRow emailBorderVanish, tableRowLineSeparator, stateAndCityTableRow;
    private TableLayout addressLineTableLayout;
    private Button buyNowButton, addressCountryButton;
    private TextView creditCardLabelTextView, shopperNameIconLabelTextView, cvvLabelTextView, expDateLabelTextView,
            subtotalValueTextView, taxValueTextView, zipTextView, emailTextView, billingCityLabelTextView, billingAddressLabelTextView, billingStateLabelTextView;
    private TextView invalidAddressMessageTextView, invaildCreditCardMessageTextView, invalidShopperName;
    private EditText creditCardNumberEditText, shopperFullNameEditText, cvvEditText, expDateEditText, zipEditText, emailEditText, billingAddressLineEditText, billingCityEditText, billingStateEditText;
    private ToggleButton couponButton;
    private Shopper shopper;
    //private PrefsStorage prefsStorage;
    private PaymentRequest paymentRequest;
    private ViewGroup subtotalView;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    public BluesnapFragment() {
        BlueSnapService.getBus().register(this);
    }

    public static BluesnapFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
        BluesnapFragment bsFragment = (BluesnapFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new BluesnapFragment();
            bsFragment.setArguments(bundle);
        }
        return bsFragment;
    }

    //TODO: extract to common, reuse for shipping
    public static boolean isValidUserFullName(CharSequence fieldText) {
        if (TextUtils.isEmpty(fieldText))
            return false;

        String[] splittedNames = fieldText.toString().trim().split(" ");
        if (splittedNames.length < 2)
            return false;

        if (splittedNames[0].length() < 2)
            return false;

        return splittedNames[1].length() >= 2;
        //        && android.util.Patterns..matcher(target).matches()
    }

    public boolean processUserNameField(String name) {


        if (!isValidUserFullName(name)) {
            shopperNameIconLabelTextView.setTextColor(Color.RED);
            invalidShopperName.setVisibility(View.VISIBLE);
            return false;
        } else {
            shopperNameIconLabelTextView.setTextColor(Color.BLACK);
            invalidShopperName.setVisibility(View.GONE);
            shopperFullNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        paymentRequest = BlueSnapService.getInstance().getPaymentRequest();
        Events.CurrencyUpdatedEvent currencyUpdatedEvent = new Events.CurrencyUpdatedEvent(paymentRequest.getAmount(), paymentRequest.getCurrencyNameCode(), paymentRequest.getTaxAmount(), paymentRequest.getSubtotalAmount());
        onCurrencyUpdated(currencyUpdatedEvent);
        boolean notax = (paymentRequest.getSubtotalAmount() == 0D || paymentRequest.getTaxAmount() == 0D);
        subtotalView.setVisibility(notax ? View.INVISIBLE : View.VISIBLE);

        if (paymentRequest.isEmailRequired()) {
            emailFieldLayout.setVisibility(View.VISIBLE);
            emailBorderVanish.setVisibility(View.VISIBLE);
        }

        if (paymentRequest.isBillingRequired()) {
            addressLineTableLayout.setVisibility(View.VISIBLE);
            stateAndCityTableRow.setVisibility(View.VISIBLE);
        }

        if (paymentRequest.isShippingRequired()) {
            buyNowButton.setText(getResources().getString(R.string.shipping));

            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                drawable = getResources().getDrawable(R.drawable.ic_forward_white_24dp, null);
            else
                drawable = getResources().getDrawable(R.drawable.ic_forward_white_24dp);
            buyNowButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

            subtotalView.setVisibility(View.INVISIBLE);
        }
        buyNowButton.setOnClickListener(new buyButtonClickListener());
        buyNowButton.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            shopperFullNameEditText.setText(savedInstanceState.getString("shopperFullNameEditText"));
        }
        creditCardNumberEditText.addTextChangedListener(creditCardEditorWatcher);
        expDateEditText.addTextChangedListener(mExpDateTextWatcher);
        shopperFullNameEditText.addTextChangedListener(nameEditorWatcher);

        shopperFullNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) nameValidation();
            }
        });
        creditCardNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) cardNumberValidation();
            }
        });

        expDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) expiryDateValidation();
                if (hasFocus) {
                    if (View.INVISIBLE == cvvEditText.getVisibility() && View.INVISIBLE == cvvLabelTextView.getVisibility()) {
                        expDateEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    } else {
                        expDateEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        expDateEditText.setNextFocusDownId(R.id.cvvEditText);
                    }
                }
            }
        });

        zipEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    zipFieldValidation();
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    AndroidUtil.validateEditTextString(emailEditText, emailTextView, AndroidUtil.EMAIL_FIELD);
            }
        });

        billingAddressLineEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    AndroidUtil.validateEditTextString(billingAddressLineEditText, billingAddressLabelTextView, AndroidUtil.ADDRESS_FIELD);
            }
        });

        billingCityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    AndroidUtil.validateEditTextString(billingCityEditText, billingCityLabelTextView, AndroidUtil.CITY_FIELD);
            }
        });

        addressCountryButton.setText(blueSnapService.getUserCountry(getActivity().getApplicationContext()));
        changeZipTextAccordingToCountry();

        AndroidUtil.setFocusOnLayoutOfEditText(cvvLabelTextView, cvvEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(expDateLabelTextView, expDateEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shopperNameIconLabelTextView, shopperFullNameEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(zipTextView, zipEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(emailTextView, emailEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(creditCardLabelTextView, creditCardNumberEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(billingAddressLabelTextView, billingAddressLineEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(billingCityLabelTextView, billingCityEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(billingStateLabelTextView, billingStateEditText);

    }

    private boolean nameValidation() {
        String formattedName = AndroidUtil.stringify(shopperFullNameEditText.getText());
        return processUserNameField(formattedName);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View inflate = inflater.inflate(R.layout.bluesnap_checkout_creditcard, container, false);
        buyNowButton = (Button) inflate.findViewById(R.id.buyNowButton);

        subtotalView = (ViewGroup) inflate.findViewById(R.id.subtotal_tax_table);
        creditCardLabelTextView = (TextView) inflate.findViewById(R.id.creditCardLabelTextView);
        creditCardNumberLayoutSpinner = (LinearLayout) inflate.findViewById(R.id.creditCardNumberLayoutSpinner);
        creditCardNumberSpinner = (Spinner) inflate.findViewById(R.id.creditCardNumberSpinner);
        shopperNameIconLabelTextView = (TextView) inflate.findViewById(R.id.carHolderNameLabelTextView);
        cvvLabelTextView = (TextView) inflate.findViewById(R.id.cvvLabelTextView);
        expDateLabelTextView = (TextView) inflate.findViewById(R.id.expDateLabelTextView);
        cvvEditText = (EditText) inflate.findViewById(R.id.cvvEditText);
        couponLayout = (LinearLayout) inflate.findViewById(R.id.linearLayout_coupon);
        invaildCreditCardMessageTextView = (TextView) inflate.findViewById(R.id.invaildCreditCardMessageTextView);
        invalidShopperName = (TextView) inflate.findViewById(R.id.invalidShopperNameMessageTextView);
        zipFieldLayout = (LinearLayout) inflate.findViewById(R.id.zipFieldLayout);
        zipTextView = (TextView) inflate.findViewById(R.id.zipTextView);
        zipEditText = (EditText) inflate.findViewById(R.id.zipEditText);
        addressCountryButton = (Button) inflate.findViewById(R.id.addressCountryButton);
        addressCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(inflate.getContext(), CountryActivity.class);
                newIntent.putExtra(getString(R.string.COUNTRY_STRING), getCountryText());
                startActivityForResult(newIntent, Activity.RESULT_FIRST_USER);
            }
        });

        if (blueSnapService.doesCountryhaveZip(getActivity().getApplicationContext())) {
            zipFieldLayout.setVisibility(View.VISIBLE);
        }

        emailFieldLayout = (LinearLayout) inflate.findViewById(R.id.emailLinearLayout);
        emailBorderVanish = (TableRow) inflate.findViewById(R.id.emailBorderVanish);
        emailTextView = (TextView) inflate.findViewById(R.id.emailTextView);
        emailEditText = (EditText) inflate.findViewById(R.id.emailEditText);

        addressLineTableLayout = (TableLayout) inflate.findViewById(R.id.addressLineTableLayout);
        stateAndCityTableRow = (TableRow) inflate.findViewById(R.id.stateAndCityTableRow);
        billingAddressLineEditText = (EditText) inflate.findViewById(R.id.billingAddressLineEditText);
        billingAddressLabelTextView = (TextView) inflate.findViewById(R.id.billingAddressLineTextView);
        billingStateEditText = (EditText) inflate.findViewById(R.id.billingStateEditText);
        billingStateLabelTextView = (TextView) inflate.findViewById(R.id.billingStateLabelTextView);
        billingCityEditText = (EditText) inflate.findViewById(R.id.billingCityEditText);
        billingCityLabelTextView = (TextView) inflate.findViewById(R.id.billingCityTextView);

        shopperFullNameEditText = (EditText) inflate.findViewById(R.id.cardHolderNameEditText);
        expDateEditText = (EditText) inflate.findViewById(R.id.expDateEditText);
        creditCardNumberEditText = (EditText) inflate.findViewById(R.id.creditCardNumberEditText);
        tableRowLineSeparator = (TableRow) inflate.findViewById(R.id.tableRowLineSeparator);
        //prefsStorage = new PrefsStorage(inflate.getContext());
        subtotalValueTextView = (TextView) inflate.findViewById(R.id.subtotalValueTextview);
        taxValueTextView = (TextView) inflate.findViewById(R.id.taxValueTextview);
        LinearLayout cardFieldsLinearLayout = (LinearLayout) inflate.findViewById(R.id.cardFieldsLinearLayout);
        AndroidUtil.hideKeyboardOnLayoutOfEditText(cardFieldsLinearLayout);
        //couponButton.setOnClickListener(new couponBtnClickListener()); //TODO: coupon

        initPrefs(inflate.getContext());

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
            billingStateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        AndroidUtil.validateEditTextString(billingStateEditText, billingStateLabelTextView, AndroidUtil.STATE_FIELD);
                }
            });
        } else {
            billingStateEditText.setOnFocusChangeListener(null);
        }
        changeZipTextAccordingToCountry();
    }

    @Subscribe
    public void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent) {
        String currencySymbol = AndroidUtil.getCurrencySymbol(currencyUpdatedEvent.newCurrencyNameCode);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        if (!BlueSnapService.getInstance().getPaymentRequest().isShippingRequired()) {
            buyNowButton.setText(getResources().getString(R.string.pay)
                    + " " + currencySymbol + " " + decimalFormat.format(currencyUpdatedEvent.updatedPrice));
        }
        String taxValue = currencySymbol + String.valueOf(decimalFormat.format(currencyUpdatedEvent.updatedTax));
        taxValueTextView.setText(taxValue);
        String subtotal = currencySymbol + decimalFormat.format(currencyUpdatedEvent.updatedSubtotal);
        subtotalValueTextView.setText(subtotal);
    }

    private String getCountryText() {
        return AndroidUtil.stringify(addressCountryButton.getText()).trim();
    }

    private void changeZipTextAccordingToCountry() {
        // check if usa if so change zip text to postal code otherwise billing zip
        if (Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getCountryText())) {
            zipFieldLayout.setVisibility(View.INVISIBLE);
            zipEditText.setText("");
        } else {
            zipFieldLayout.setVisibility(View.VISIBLE);
            zipTextView.setText(
                    AndroidUtil.STATE_NEEDED_COUNTRIES[0].equals(getCountryText())
                            ? R.string.postal_code_hint
                            : R.string.billing_zip
            );
        }
    }

    private void initPrefs(Context context) {
        try {
            Shopper shopper = blueSnapService.getsDKConfiguration().getShopper();
            if (null != shopper && null != shopper.getPreviousPaymentSources() && null != shopper.getPreviousPaymentSources().getPreviousCreditCardInfos()) {
                this.shopper = shopper;
                populateFromCard(context);
            } else if (null == shopper)
                this.shopper = new Shopper();

        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException", e);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
    }

    private void populateFromCard(Context context) throws NullPointerException {
        creditCardNumberEditText.setVisibility(View.GONE);
        creditCardNumberLayoutSpinner.setVisibility(View.VISIBLE);
        cvvEditText.setVisibility(View.INVISIBLE);
        cvvLabelTextView.setVisibility(View.INVISIBLE);

        assert shopper.getPreviousPaymentSources() != null;
        ArrayList<CreditCardInfo> previousCreditCardInfoArray = shopper.getPreviousPaymentSources().getPreviousCreditCardInfos();
        LastPaymentInfo lastPaymentInfo = shopper.getLastPaymentInfo();

        //create a ArrayList<CreditCardInfo> for the spinner.
        ArrayList<CreditCardInfo> filteredCreditCardInfosArray = new ArrayList<>();

        assert lastPaymentInfo != null;
        assert previousCreditCardInfoArray != null;
        for (CreditCardInfo previousCreditCardInfo : previousCreditCardInfoArray) {
            if (!lastPaymentInfo.getCreditCard().getCardLastFourDigits().equals(previousCreditCardInfo.getCreditCard().getCardLastFourDigits())
                    && !lastPaymentInfo.getCreditCard().getCardType().equals(previousCreditCardInfo.getCreditCard().getCardType())) {
                filteredCreditCardInfosArray.add(previousCreditCardInfo);
            } else {
                //add last payment as first in line
                filteredCreditCardInfosArray.add(0, previousCreditCardInfo);
            }
        }

        //create an adapter to describe how the items are displayed.
        CustomCreditCardSpinnerAdapter adapter = new CustomCreditCardSpinnerAdapter(this.getActivity(), filteredCreditCardInfosArray);
        //set the spinners adapter to the previously created one.
        creditCardNumberSpinner.setAdapter(adapter);

        //creditCardNumberEditText.setHint("•••• •••• •••• " + shopper.getNewCreditCardInfo().getCreditCard().getCardLastFourDigits());
        //changeCardEditTextDrawable(shopper.getNewCreditCardInfo().getCreditCard().getCardType());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    private void setFocusOnCCFragmentEditText(final CreditCardFields checkWhichFieldIsInValid) {
        switch (checkWhichFieldIsInValid) {
            case NAMEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(shopperFullNameEditText);
                break;
            case CREDITCARDNUMBEREDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(creditCardNumberEditText);
                break;
            case EXPDATEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(expDateEditText);
                break;
            case CVVEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(cvvEditText);
                break;
            case ZIPEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(zipEditText);
                break;
            case EMAILEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(emailEditText);
                break;
            case BILLINGADDRESSLINEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(billingAddressLineEditText);
                break;
            case BILLINGCITYEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(billingCityEditText);
                break;
            case BILLINGSTATEEDITTEXT:
                AndroidUtil.setFocusOnFirstErrorInput(billingStateEditText);
                break;
            default:
                break;
        }
    }

    private boolean ProcessCardFields() {
        boolean validInput = true;
        CreditCardFields checkWhichFieldIsInValid = CreditCardFields.DEFAULT;

        validInput &= nameValidation();
        if (!validInput) checkWhichFieldIsInValid = CreditCardFields.NAMEEDITTEXT;

        validInput &= emailFieldValidation();
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.EMAILEDITTEXT;

        validInput &= cardNumberValidation();
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.CREDITCARDNUMBEREDITTEXT;
        validInput &= expiryDateValidation();
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.EXPDATEEDITTEXT;
        validInput &= cvvValidation();
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.CVVEDITTEXT;

        validInput &= zipFieldValidation();
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.ZIPEDITTEXT;

        validInput &= billingValidation(AndroidUtil.ADDRESS_FIELD);
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.BILLINGADDRESSLINEEDITTEXT;
        validInput &= billingValidation(AndroidUtil.CITY_FIELD);
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.BILLINGCITYEDITTEXT;
        validInput &= billingValidation(AndroidUtil.STATE_FIELD);
        if (!validInput && checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            checkWhichFieldIsInValid = CreditCardFields.BILLINGSTATEEDITTEXT;

        if (!checkWhichFieldIsInValid.equals(CreditCardFields.DEFAULT))
            setFocusOnCCFragmentEditText(checkWhichFieldIsInValid);

        if (shopper.getNewCreditCardInfo().getCreditCard().validateAll())
            validInput &= true;

        return validInput;
    }

    private boolean billingValidation(String inputType) {
        if (!paymentRequest.isBillingRequired())
            return true;
        else if (AndroidUtil.ADDRESS_FIELD.equals(inputType))
            return AndroidUtil.validateEditTextString(billingAddressLineEditText, billingAddressLabelTextView, AndroidUtil.ADDRESS_FIELD);
        else if (AndroidUtil.CITY_FIELD.equals(inputType))
            return AndroidUtil.validateEditTextString(billingCityEditText, billingCityLabelTextView, AndroidUtil.CITY_FIELD);
        else {
            return !AndroidUtil.checkCountryForState(getCountryText()) || AndroidUtil.validateEditTextString(billingStateEditText, billingStateLabelTextView, AndroidUtil.STATE_FIELD);
        }
    }

    private boolean zipFieldValidation() {
        String cardType = CreditCardTypes.getType(creditCardNumberEditText.getText().toString().trim());
        if (!Arrays.asList(Constants.COUNTRIES_WITHOUT_ZIP).contains(getCountryText()) && (cardType.equals(CreditCardTypes.VISA) || cardType.equals(CreditCardTypes.DISCOVER))) {
            return AndroidUtil.validateEditTextString(zipEditText, zipTextView, AndroidUtil.ZIP_FIELD);
        } else {
            zipTextView.setTextColor(Color.BLACK);
            return true;
        }
    }

    private boolean emailFieldValidation() {
        return !paymentRequest.isEmailRequired()
                || AndroidUtil.validateEditTextString(emailEditText, emailTextView, AndroidUtil.EMAIL_FIELD);
    }

    private boolean cvvValidation() {
        if (!shopper.getNewCreditCardInfo().getCreditCard().validateCVC()) {
            cvvLabelTextView.setTextColor(Color.RED);
            return false;
        } else {
            cvvLabelTextView.setTextColor(Color.BLACK);
        }
        return true;
    }

    private boolean expiryDateValidation() {
        String date = AndroidUtil.stringify(expDateEditText.getText());
        if (!CreditCard.validateExpiryDate(date)) {
            expDateLabelTextView.setTextColor(Color.RED);
            return false;
        } else {
            expDateLabelTextView.setTextColor(Color.BLACK);
        }
        return true;
    }

    private boolean cardNumberValidation() {
        boolean valid = true;
        String formattedName = AndroidUtil.stringify(shopperFullNameEditText.getText());
        if (!processUserNameField(formattedName)) {
            valid = false;
        }

        if (!shopper.getNewCreditCardInfo().getCreditCard().requireValidation()) {
            valid = true;
        }
        if (!shopper.getNewCreditCardInfo().getCreditCard().isModified() && shopper.getNewCreditCardInfo().getCreditCard().validForReuse())
            valid = true;
        else {
            shopper.getNewCreditCardInfo().getBillingContactInfo().setZip(zipEditText.getText().toString().trim());
            shopper.getNewCreditCardInfo().getBillingContactInfo().setFullName(formattedName);
            shopper.getNewCreditCardInfo().getCreditCard().update(
                    creditCardNumberEditText.getText().toString().trim(),
                    expDateEditText.getText().toString().trim(),
                    cvvEditText.getText().toString().trim()
            );

            if (!shopper.getNewCreditCardInfo().getCreditCard().validateNumber()) {
                creditCardLabelTextView.setTextColor(Color.RED);
                //creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_invalid_cc, 0);
                invaildCreditCardMessageTextView.setVisibility(View.VISIBLE);
                valid = false;
            } else {
                creditCardLabelTextView.setTextColor(Color.BLACK);
                invaildCreditCardMessageTextView.setVisibility(View.GONE);
                creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            }
        }
        changeCardEditTextDrawable(shopper.getNewCreditCardInfo().getCreditCard().getCardType());
        return valid;
    }

    private void changeCardEditTextDrawable(String type) {
        creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, CreditCardTypes.getCardTypeDrawable(type), 0);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("shopperFullNameEditText", shopperFullNameEditText.getText().toString());
    }

    private void cardModified() {
        shopper.getNewCreditCardInfo().getCreditCard().setModified();
        cvvEditText.setVisibility(View.VISIBLE);
        cvvLabelTextView.setVisibility(View.VISIBLE);
        creditCardNumberEditText.setHint("");
        if (creditCardNumberEditText.getText().length() == 0)
            changeCardEditTextDrawable(CreditCardTypes.UNKNOWN);
    }


    private enum CreditCardFields {
        NAMEEDITTEXT, CREDITCARDNUMBEREDITTEXT, EXPDATEEDITTEXT, CVVEDITTEXT, ZIPEDITTEXT, EMAILEDITTEXT, BILLINGADDRESSLINEEDITTEXT, BILLINGCITYEDITTEXT, BILLINGSTATEEDITTEXT, DEFAULT
    }

    private class buyButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (!ProcessCardFields())
                return;

            creditCardLabelTextView.setTextColor(Color.BLACK);
            shopperNameIconLabelTextView.setTextColor(Color.BLACK);
            invaildCreditCardMessageTextView.setVisibility(View.GONE);
            invalidShopperName.setVisibility(View.GONE);

            BluesnapCheckoutActivity bluesnapCheckoutActivity = (BluesnapCheckoutActivity) getActivity();
            bluesnapCheckoutActivity.setShopper(shopper);

            PaymentResult paymentResult = BlueSnapService.getInstance().getPaymentResult();
            BillingInfo billingInfo = shopper.getNewCreditCardInfo().getBillingContactInfo();

            paymentResult.setLast4Digits(shopper.getNewCreditCardInfo().getCreditCard().getCardLastFourDigits());
            paymentResult.setExpDate(shopper.getNewCreditCardInfo().getCreditCard().getExpirationDate());

            billingInfo.setFullName(shopperFullNameEditText.getText().toString().trim());
            billingInfo.setZip(shopper.getNewCreditCardInfo().getBillingContactInfo().getZip());
            billingInfo.setCountry(getCountryText());

            if (paymentRequest.isEmailRequired())
                billingInfo.setEmail(emailEditText.getText().toString().trim());

            if (paymentRequest.isBillingRequired()) {
                billingInfo.setAddress(billingAddressLineEditText.getText().toString().trim());
                billingInfo.setCity(billingCityEditText.getText().toString().trim());
                billingInfo.setState(billingStateEditText.getText().toString().trim());
            }
            bluesnapCheckoutActivity.setBillingContactInfo(billingInfo);

            if (paymentRequest.isShippingRequired()) {
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString(ShippingFragment.AUTO_POPULATE_SHOPPER_NAME, AndroidUtil.stringify(shopperFullNameEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_ZIP, AndroidUtil.stringify(zipEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_EMAIL, AndroidUtil.stringify(emailEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_ADDRESS, AndroidUtil.stringify(billingAddressLineEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_CITY, AndroidUtil.stringify(billingCityEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_STATE, AndroidUtil.stringify(billingStateEditText.getText()));
                bundle.putString(ShippingFragment.AUTO_POPULATE_COUNTRY, getCountryText());

                ShippingFragment shippingFragment = bluesnapCheckoutActivity.createShippingFragment(bundle);
                fragmentTransaction.replace(R.id.fraglyout, shippingFragment);
                if (!shippingFragment.isAdded()) {
                    fragmentTransaction.addToBackStack(ShippingFragment.TAG);
                }
                fragmentTransaction.commit();
            } else {
                bluesnapCheckoutActivity.finishFromFragment();
                invalidNumberInputFlag = 0;
            }
        }
    }

    private class creditCardNumberWatcher implements TextWatcher {
        private static final char space = ' ';


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (shopper.getNewCreditCardInfo().getCreditCard() != null)
                shopper.getNewCreditCardInfo().getCreditCard().setModified();

            if (s.length() <= 2)
                return;

            final String ccNum = s.toString();
            changeCardEditTextDrawable(CreditCardTypes.getType(ccNum));
            creditCardLabelTextView.setTextColor(Color.BLACK);
            invaildCreditCardMessageTextView.setVisibility(View.GONE);
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (invalidNumberInputFlag == 1) {
                creditCardLabelTextView.setTextColor(Color.RED);
                invaildCreditCardMessageTextView.setVisibility(View.VISIBLE);

            } else {
                creditCardLabelTextView.setTextColor(Color.BLACK);
                invaildCreditCardMessageTextView.setVisibility(View.GONE);
                invalidNumberInputFlag = 0;
            }
        }


        @Override
        public void afterTextChanged(Editable s) {

            String ccNum = creditCardNumberEditText.getText().toString().trim();
            if (AndroidUtil.isBlank(ccNum)) { // User cleared the text
                /*zipFieldLayout.setVisibility(View.GONE);
                zipFieldBorderVanish.setVisibility(View.GONE);*/
                invaildCreditCardMessageTextView.setVisibility(View.GONE);
                creditCardLabelTextView.setTextColor(Color.BLACK);
            }

            changeCardEditTextDrawable(CreditCardTypes.getType(ccNum));

            /*if (CreditCardTypes.getType(ccNum).equals(CreditCardTypes.VISA)) {
                zipFieldLayout.setVisibility(View.VISIBLE);
                zipFieldBorderVanish.setVisibility(View.VISIBLE);
            }*/
            invalidNumberInputFlag = 0;

            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
            creditCardNumberEditText.setHint("");
            cardModified();
        }
    }

    private class couponBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (couponButton.isChecked()) {
                couponLayout.setVisibility(View.VISIBLE);
                tableRowLineSeparator.setVisibility(View.VISIBLE);
            } else {
                couponLayout.setVisibility(View.INVISIBLE);
                tableRowLineSeparator.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class CardExpDateTextWatcher implements TextWatcher {
        String newDateStr;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            boolean dateMinimumChars = true;
            String datePart[] = expDateEditText.getText().toString().split("/");
            for (String datePartT : datePart) {
                if (datePartT.length() > 2)
                    dateMinimumChars = false;
            }

            if (!dateMinimumChars || count <= 0) {
                return;
            }
            if (((expDateEditText.getText().length()) % 2) == 0) {

                if (expDateEditText.getText().toString().split("/").length <= 1) {
                    expDateEditText.setText(expDateEditText.getText() + "/");
                    expDateEditText.setSelection(expDateEditText.getText().length());
                }
            }
            newDateStr = expDateEditText.getText().toString();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (expDateEditText.hasFocus())
                cardModified();
        }
    }

    private class NameEditorWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (shopperFullNameEditText.hasFocus())
                cardModified();
        }
    }
}
