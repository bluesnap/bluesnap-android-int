package com.bluesnap.androidapi.views.components;

import android.content.Context;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class OneLineCCViewComponent extends LinearLayout {
    public static final String TAG = OneLineCCViewComponent.class.getSimpleName();
    private TextView expTextView, ccLastFourDigitsTextView;
    private ImageView cardIconImageView;


    public OneLineCCViewComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public OneLineCCViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public OneLineCCViewComponent(Context context) {
        super(context);
        initControl(context);
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
            inflater.inflate(R.layout.one_line_cc_view_component, this);
        }

        ccLastFourDigitsTextView = findViewById(R.id.ccLastFourDigitsTextView);
        expTextView = findViewById(R.id.expTextView);
        cardIconImageView = findViewById(R.id.cardIconImageView);
    }

    /**
     * update OneLineCCViewComponent
     *
     * @param creditCard - {@link CreditCard}
     */
    public void updateViewResourceWithDetails(CreditCard creditCard) {
        updateViewResourceWithDetails(creditCard.getCardLastFourDigits(), creditCard.getExpirationDateForEditTextAndSpinner(), creditCard.getCardType());
    }

    /**
     * update OneLineCCViewComponent
     *
     * @param lastFourDigits - lastFourDigits
     * @param expDateString - expDateString
     * @param type - type
     */
    public void updateViewResourceWithDetails(String lastFourDigits, String expDateString, String type) {
        setCCLastFourDigitsText(lastFourDigits);
        setExpText(expDateString);
        setCardIconImage(type);
    }

    private void setExpText(String expDateString) {
        this.expTextView.setText(expDateString);
    }

    private void setCCLastFourDigitsText(String lastFourDigits) {
        this.ccLastFourDigitsTextView.setText(lastFourDigits);
    }

    /**
     * set image resource according to credit card type
     * @param type - Credit Card Type {@link CreditCardTypeResolver}
     */
    private void setCardIconImage(String type) {
        this.cardIconImageView.setImageResource(CreditCardTypeResolver.getInstance().getCardTypeDrawable(type));
    }
}
