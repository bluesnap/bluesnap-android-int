package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class OneLineCCEditComponent  extends LinearLayout {
    public static final String TAG = OneLineCCEditComponent.class.getSimpleName();
    private CreditCard newCreditCard;
    private ImageView cardIconImageView;
    private ImageButton moveToCcImageButton;
    private boolean activateMoveToCcImageButton;
    private EditText creditCardNumberEditText, expEditText, cvvEditText;
    private TextView creditCardNumberErrorTextView, expErrorTextView, cvvErrorTextView;
    private LinearLayout expLinearLayout, cvvLinearLayout;
    private final TextWatcher creditCardNumberWatcher = new creditCardNumberWatcher(), expTextWatcher = new expTextWatcher();

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

    /**
     * get credit card details
     */
    public CreditCard getNewCreditCard() {
        return newCreditCard;
    }

    /**
     * Load component XML layout
     */
    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.one_line_cc_edit_component, this);

        try {
            this.newCreditCard = new CreditCard();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        // layout is inflated, assign local variables to components
        cardIconImageView = (ImageView) findViewById(R.id.cardIconImageView);
        creditCardNumberEditText = (EditText) findViewById(R.id.creditCardNumberEditText);
        moveToCcImageButton = (ImageButton) findViewById(R.id.moveToCcImageButton);
        creditCardNumberErrorTextView = (TextView) findViewById(R.id.creditCardNumberErrorTextView);
        creditCardNumberEditText.setOnFocusChangeListener(new creditCardNumberOnFocusChangeListener());
        creditCardNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT)
                    creditCardNumberOnLoseFocus();
                return false;
            }
        });
        moveToCcImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditCardNumberOnLoseFocus();
            }
        });

        expEditText = (EditText) findViewById(R.id.expEditText);
        expErrorTextView = (TextView) findViewById(R.id.expErrorTextView);
        expLinearLayout = (LinearLayout) findViewById(R.id.expLinearLayout);
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

        cvvEditText = (EditText) findViewById(R.id.cvvEditText);
        cvvErrorTextView = (TextView) findViewById(R.id.cvvErrorTextView);
        cvvLinearLayout = (LinearLayout) findViewById(R.id.cvvLinearLayout);
        cvvEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    cvvValidation();
                else
                    cvvEditText.setSelection(cvvEditText.getText().length());
            }
        });

        // flag for activation of the next button, relevant only for the second time
        activateMoveToCcImageButton = false;
    }

    /**
     * Validating form inputs
     * (assumes card number has already been set from editText)
     *
     * @return boolean
     */
    public boolean validateInfo() {
        return cardNumberValidation(newCreditCard.getNumber()) && cvvValidation() && expValidation();
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
        cardIconImageView.setImageResource(CreditCardTypeResolver.getCardTypeDrawable(type));
    }

    /**
     * return last four digits for view purposes
     */
    private String getCardLastFourDigitsForView(String number) {
        number = AndroidUtil.stringify(number);
        if (number.length() < 4) {
            return number;
        } else {
            return number.substring(number.length() - 4, number.length());
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

            final String ccNum = s.toString().trim();
            changeCardEditTextDrawable(CreditCardTypeResolver.getType(ccNum));

            if (s.length() == getResources().getInteger(R.integer.ccn_max_length)) {
                creditCardNumberOnLoseFocus();
            }
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            changeInputColorAndErrorVisibility(creditCardNumberEditText, creditCardNumberErrorTextView, true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String ccNum = creditCardNumberEditText.getText().toString().trim();
            changeCardEditTextDrawable(CreditCardTypeResolver.getType(ccNum));

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
        setCardNumberFromEditTextAndValidate();
        creditCardNumberEditText.setHint("");
        creditCardNumberEditText.removeTextChangedListener(creditCardNumberWatcher);
        creditCardNumberEditText.setText(getCardLastFourDigitsForView(newCreditCard.getNumber()));
        focusOnExpEditTextAndSetCvvVisible();
    }

    /**
     * change ccn to full number on focus
     */
    private class creditCardNumberOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                creditCardNumberEditText.setHint("1234 5678 9012 3456");
                creditCardNumberEditText.setText(newCreditCard.getNumber());
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
    private void focusOnExpEditTextAndSetCvvVisible() {
        expLinearLayout.setVisibility(View.VISIBLE);
        cvvLinearLayout.setVisibility(View.VISIBLE);
        expEditText.requestFocus();
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

}
