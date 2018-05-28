package com.bluesnap.androidapi.services;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * A Collection of static methods used in the Android UI parts.
 */
public class AndroidUtil {
    private static final String TAG = AndroidUtil.class.getSimpleName();
    protected static DecimalFormat decimalFormat;

    public static AndroidUtil getInstance() {
        return AndroidUtilHOLDER.INSTANCE;
    }

    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        //Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
    }

    /**
     * get Currency Symbol according to Currency name code
     *
     * @param newCurrencyNameCode
     * @return Currency Symbol
     */
    public static String getCurrencySymbol(String newCurrencyNameCode) {
        try {
            String symbol = Currency.getInstance(newCurrencyNameCode).getSymbol();
            return symbol;
        } catch (Exception e) {
            return newCurrencyNameCode;
        }

    }

    public static void setVisabilityRecursive(ViewGroup vg, int visability) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setVisibility(visability);
            if (child instanceof ViewGroup) {
                setVisabilityRecursive((ViewGroup) child, visability);
            }
        }
    }

    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#############.##");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMinimumIntegerDigits(1);
        }
        return decimalFormat;
    }

    public static String stringify(Object main, Object secondary) {
        String mainString = stringify(main);
        return (!"".equals(mainString) ? mainString : stringify(secondary));
    }

    /**
     * check if null, if so returns empty string
     * also trim and replace white spaces
     *
     * @param s
     * @return trimmed String
     */
    public static String stringify(Object s) {
        if (s == null || s.toString().isEmpty())
            return "";

        return s.toString().trim().replaceAll("\\s+", " ");
    }

    private static void textValidChanges(TextView textView) {
        textView.setTextColor(Color.BLACK);
    }

    private static void textValidChanges(TextView textView, TextView optionalInvalidStatement) {
        textView.setTextColor(Color.BLACK);
        optionalInvalidStatement.setVisibility(View.GONE);
    }

    private static void textInValidChanges(TextView textView) {
        textView.setTextColor(Color.RED);
    }

    private static void textInValidChanges(TextView textView, TextView optionalInvalidStatement) {
        textView.setTextColor(Color.RED);
        optionalInvalidStatement.setVisibility(View.VISIBLE);
    }

    private static class AndroidUtilHOLDER {
        public static final AndroidUtil INSTANCE = new AndroidUtil();
    }

    public static void hideKeyboardOnLayoutOfEditText(final View baseView) {
        setFocusOnLayoutOfEditText(baseView, null);
    }

    public static void setFocusOnLayoutOfEditText(final View baseView, final View targetView) {
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetView != null)
                    setFocusOnFirstErrorInput(targetView);
                else
                    setKeyboardStatus(baseView, false);
            }
        });
    }

    public static void setFocusOnFirstErrorInput(final View view) {
        view.requestFocus();
        setKeyboardStatus(view, true);
    }

    private static void setKeyboardStatus(final View view, final boolean showKey) {
        final InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            Log.w(TAG, "inputMethodManager is null");
        } else {
            if (showKey)
                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            else
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
