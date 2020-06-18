package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.TokenServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class OneLineCCEditComponent extends LinearLayout {
    public static final String TAG = OneLineCCEditComponent.class.getSimpleName();
    private CreditCard newCreditCard;
    private ImageView cardIconImageView;
    private ImageButton moveToCcImageButton;
    private boolean activateMoveToCcImageButton;
    private EditText creditCardNumberEditText, expEditText, cvvEditText;
    private TextView creditCardNumberErrorTextView, expErrorTextView, cvvErrorTextView;
    private LinearLayout expLinearLayout, cvvLinearLayout;
    private final TextWatcher creditCardNumberWatcher = new creditCardNumberWatcher();
    private final TextWatcher expTextWatcher = new expTextWatcher();

    public OneLineCCEditComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public OneLineCCEditComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public OneLineCCEditComponent(Context context) {
        super(context);
        initControl(context);
    }

    public EditText getCreditCardNumberEditText() {
        return creditCardNumberEditText;
    }

    public EditText getCvvEditText() {
        return cvvEditText;
    }

    /**
     * get credit card details
     */
    public CreditCard getNewCreditCard() {
        return newCreditCard;
    }

    /**
     * get credit card Resource from inputs
     *
     * @return {@link CreditCard}
     */
    public CreditCard getViewResourceDetails() {
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(AndroidUtil.stringify(getNewCreditCard().getNumber(), creditCardNumberEditText.getText().toString().trim()));
        creditCard.setExpDateFromString(expEditText.getText().toString().trim());
        creditCard.setCvc(cvvEditText.getText().toString().trim());
        return creditCard;
    }

    /**
     * Load component XML layout
     */
    private void initControl(Context context) {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Log.w(TAG, "inflater is null");
        } else {
            inflater.inflate(R.layout.one_line_cc_edit_component, this);
        }

        try {
            this.newCreditCard = new CreditCard();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        // layout is inflated, assign local variables to components
        cardIconImageView = findViewById(R.id.cardIconImageView);
        creditCardNumberEditText = findViewById(R.id.creditCardNumberEditText);
        moveToCcImageButton = findViewById(R.id.moveToCcImageButton);
        creditCardNumberErrorTextView = findViewById(R.id.creditCardNumberErrorTextView);
        creditCardNumberEditText.setOnFocusChangeListener(new creditCardNumberOnFocusChangeListener());
        creditCardNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                    expEditText.requestFocus();
                return false;
            }
        });

        moveToCcImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expEditText.requestFocus();
            }
        });

        expEditText = findViewById(R.id.expEditText);
        expErrorTextView = findViewById(R.id.expErrorTextView);
        expLinearLayout = findViewById(R.id.expLinearLayout);
        expEditText.addTextChangedListener(expTextWatcher);
        expEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    expValidation();
                } else {
                    expEditText.setSelection(expEditText.getText().length());
                    if (!activateMoveToCcImageButton)
                        activateMoveToCcImageButton = true;
                }
            }
        });
        expEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                cvvEditText.requestFocus();
                return false;
            }
        });

        cvvEditText = findViewById(R.id.cvvEditText);
        cvvErrorTextView = findViewById(R.id.cvvErrorTextView);
        cvvLinearLayout = findViewById(R.id.cvvLinearLayout);
        cvvEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cvvValidation();
                    BlueSnapLocalBroadcastManager.sendMessage(getContext(), BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH, TAG);
                } else
                    cvvEditText.setSelection(cvvEditText.getText().length());
            }
        });

        // flag for activation of the next button, relevant only for the second time
        activateMoveToCcImageButton = false;

        creditCardNumberEditText.requestFocus();
    }

    /**
     * update resource with details
     *
     * @param creditCard - {@link CreditCard}
     */
    public void updateViewResourceWithDetails(CreditCard creditCard) {
        newCreditCard = creditCard;
        if (!TextUtils.isEmpty(creditCard.getNumber())) {
            creditCardNumberEditText.setText(creditCard.getNumber());
            changeCardEditTextDrawable(CreditCardTypeResolver.getInstance().getType(creditCard.getNumber()));
            if (creditCard.getNumber().length() >= getResources().getInteger(R.integer.ccn_max_length_no_spaces))
                creditCardNumberOnLoseFocus();
        }
        if (!TextUtils.isEmpty(creditCard.getExpirationDateForEditTextAndSpinner()))
            expEditText.setText(creditCard.getExpirationDateForEditTextAndSpinner());
        if (!TextUtils.isEmpty(creditCard.getCvc()))
            cvvEditText.setText(creditCard.getCvc());
    }

    /**
     * Validating form inputs
     * (assumes card number has already been set from editText)
     *
     * @return boolean
     */
    public boolean validateInfo() {
        if (creditCardNumberEditText.hasFocus())
            cvvEditText.requestFocus();

        boolean isValid = cardNumberValidation(newCreditCard.getNumber());
        isValid &= cvvValidation();
        isValid &= expValidation();

        return isValid;
    }

    /**
     * Change received inputs(Red - invalid, black - valid)
     * and error text(visible - invalid, invisible - valid)
     * according to received validation result
     */
    private void changeInputColorAndErrorVisibility(EditText input, TextView error, boolean validationResult) {
        if (!validationResult) {
            error.setVisibility(View.VISIBLE);
            input.setTextColor(Color.RED);
        } else {
            error.setVisibility(View.INVISIBLE);
            input.setTextColor(Color.BLACK);
        }
    }

    /**
     * Validate and set card number input
     * will also change card icon drawable
     */
    private boolean setCardNumberFromEditTextAndValidate() {
        final String creditCardNumber = AndroidUtil.stringify(creditCardNumberEditText.getText());
        newCreditCard.setNumber(creditCardNumber);
        return cardNumberValidation(creditCardNumber);
    }

    /**
     * Validate card number input
     * will also change card icon drawable
     */
    private boolean cardNumberValidation(String creditCardNumber) {
        boolean validationResult = BlueSnapValidator.creditCardNumberValidation(AndroidUtil.stringify(creditCardNumber));
        changeInputColorAndErrorVisibility(creditCardNumberEditText, creditCardNumberErrorTextView, validationResult);
        return validationResult;
    }

    /**
     * Change credit card drawable according to received type
     */
    private void changeCardEditTextDrawable(String type) {
        cardIconImageView.setImageResource(CreditCardTypeResolver.getInstance().getCardTypeDrawable(type));
    }

    /**
     * return last four digits for view purposes
     */
    private String getCardLastFourDigitsForView(String number) {
        number = AndroidUtil.stringify(number);
        if (number.length() < 4) {
            return number;
        } else {
            return number.substring(number.length() - 4);
        }
    }

    /**
     * Handle credit card number spaces between 4 digit groups
     */
    private class creditCardNumberWatcher implements TextWatcher {
        private static final char space = ' ';


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() <= 2)
                return;

            try {
                final String ccNum = s.toString().trim();
                changeCardEditTextDrawable(CreditCardTypeResolver.getInstance().getType(ccNum));

                if (s.length() == getResources().getInteger(R.integer.ccn_max_length)) {
                    if (setCardNumberFromEditTextAndValidate()) {
                        expEditText.requestFocus();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "creditCardNumberWatcher error", e );
            }
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            changeInputColorAndErrorVisibility(creditCardNumberEditText, creditCardNumberErrorTextView, true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String ccNum = creditCardNumberEditText.getText().toString().trim();
            changeCardEditTextDrawable(CreditCardTypeResolver.getInstance().getType(ccNum));

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
        }
    }

    /**
     * change ccn to last four digits, validate, show and move to exp and show cvv if already showed
     */
    private void creditCardNumberOnLoseFocus() {
        if (activateMoveToCcImageButton)
            moveToCcImageButton.setVisibility(View.GONE);
        if (setCardNumberFromEditTextAndValidate()) {
            submitCCNumber();

            creditCardNumberEditText.setHint("");

            creditCardNumberEditText.post(new Runnable() {
                @Override
                public void run() {
                    creditCardNumberEditText.removeTextChangedListener(creditCardNumberWatcher);
                }
            });

            creditCardNumberEditText.setText(getCardLastFourDigitsForView(newCreditCard.getNumber()));
            setExpAndCvvVisible();
        }
    }

    /**
     * change ccn to full number on focus
     */
    private class creditCardNumberOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                creditCardNumberOnLoseFocus();
            } else {
                creditCardNumberEditText.setHint("1234 5678 9012 3456");

                String ccNumber = newCreditCard.getNumber();

                if (ccNumber != null) {
                    String ccNumberWithSpaces = "";
                    int lastInsertedIndex = 0;
                    // Insert space char where needed.
                    for (int i = 4; i < ccNumber.length(); i++) {
                        if ((i) % 4 == 0) {
                            lastInsertedIndex = i;
                            char c = ccNumber.charAt(i - 1);
                            // Only if its a digit where there should be a space we insert a space
                            if (Character.isDigit(c)) {
                                ccNumberWithSpaces = ccNumberWithSpaces + ccNumber.substring(i - 4, i) + " ";
                            }
                        }
                    }

                    ccNumber = ccNumberWithSpaces + ccNumber.substring(lastInsertedIndex);
                }

                creditCardNumberEditText.setText(ccNumber);
                creditCardNumberEditText.removeTextChangedListener(creditCardNumberWatcher);
                creditCardNumberEditText.addTextChangedListener(creditCardNumberWatcher);
                cvvLinearLayout.setVisibility(View.GONE);
                expLinearLayout.setVisibility(View.GONE);
                creditCardNumberEditText.setSelection(creditCardNumberEditText.getText().length());
                if (activateMoveToCcImageButton)
                    moveToCcImageButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Validate and set cvv input
     */
    private boolean cvvValidation() {
        final String cvv = AndroidUtil.stringify(cvvEditText.getText());
        boolean validationResult = BlueSnapValidator.creditCardCVVValidation(cvv, newCreditCard.getCardType());
        if (validationResult)
            newCreditCard.setCvc(cvv);
        changeInputColorAndErrorVisibility(cvvEditText, cvvErrorTextView, validationResult);
        return validationResult;
    }

    /**
     * show exp layout and request focus
     */
    private void setExpAndCvvVisible() {
        expLinearLayout.setVisibility(View.VISIBLE);
        cvvLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Validate and set expiration date input
     */
    private boolean expValidation() {
        final String exp = AndroidUtil.stringify(expEditText.getText());
        boolean validationResult = BlueSnapValidator.creditCardExpiryDateValidation(exp);
        if (validationResult)
            newCreditCard.setExpDateFromString(exp);
        changeInputColorAndErrorVisibility(expEditText, expErrorTextView, validationResult);
        return validationResult;
    }

    /**
     * Handle expiration date "/" sign between month and year
     */
    private class expTextWatcher implements TextWatcher {
        String newDateStr;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean dateMinimumChars = true;
            String datePart[] = expEditText.getText().toString().split("/");
            for (String datePartT : datePart) {
                if (datePartT.length() > 2)
                    dateMinimumChars = false;
            }

            if (!dateMinimumChars || count <= 0) {
                return;
            }
            if (((expEditText.getText().length()) % 2) == 0) {

                if (expEditText.getText().toString().split("/").length <= 1) {
                    expEditText.setText(expEditText.getText() + "/");
                    expEditText.setSelection(expEditText.getText().length());
                }
            }
            newDateStr = expEditText.getText().toString();

            if (s.length() == getResources().getInteger(R.integer.exp_max_length)) {
                cvvEditText.requestFocus();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            changeInputColorAndErrorVisibility(expEditText, expErrorTextView, true);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * check Credit Card Number In Server
     */
    private void submitCCNumber() {
        final BlueSnapService blueSnapService = BlueSnapService.getInstance();

        blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BlueSnapHTTPResponse response = blueSnapService.submitTokenizedCCNumber(newCreditCard.getNumber());
                    if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getResponseString());
                            final String ccType = jsonObject.getString("ccType");
                            if (!ccType.equals(newCreditCard.getCardType()))
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeCardEditTextDrawable(ccType);
                                    }
                                });

                        } catch (NullPointerException | JSONException e) {
                            String errorMsg = String.format("Service Error %s", e.getMessage());
                            Log.e(TAG, errorMsg, e);
                        }
                        //TODO check response on 400 , may be error response here
                    } else if (response.getResponseCode() == 400 && null != blueSnapService.getTokenProvider() && !"".equals(response.getResponseString())) {
                        try {
                            JSONObject errorResponse = new JSONObject(response.getResponseString());
                            JSONArray rs2 = (JSONArray) errorResponse.get("message");
                            JSONObject rs3 = (JSONObject) rs2.get(0);
                            if ("EXPIRED_TOKEN".equals(rs3.get("errorName"))) {
                                blueSnapService.getTokenProvider().getNewToken(new TokenServiceCallback() {
                                    @Override
                                    public void complete(String newToken) {
                                        blueSnapService.setNewToken(newToken);
                                        submitCCNumber();
                                    }
                                });
                            } else if ("CARD_TYPE_NOT_SUPPORTED".equals(rs3.get("errorName"))) {
                                final String description = rs3.get("description").toString();
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluesnapAlertDialog.setDialog(getContext(), description, "CARD NOT SUPPORTED");
                                        changeInputColorAndErrorVisibility(creditCardNumberEditText, creditCardNumberErrorTextView, false);
                                    }
                                });

                            } else {
                                String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                                Log.e(TAG, errorMsg);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing exception", e);
                        }
                    } else {
                        String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                        Log.e(TAG, errorMsg);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing exception", e);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding Exception", e);
                }
            }
        });
    }

    public void clear() {
        Log.d(TAG, "clear() was called");
        creditCardNumberEditText.removeTextChangedListener(creditCardNumberWatcher);
        expEditText.removeTextChangedListener(expTextWatcher);
    }

}
