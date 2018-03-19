package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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

        assert inflater != null;
        inflater.inflate(R.layout.one_line_cc_view_component, this);

        ccLastFourDigitsTextView = (TextView) findViewById(R.id.ccLastFourDigitsTextView);
        expTextView = (TextView) findViewById(R.id.expTextView);
        cardIconImageView = (ImageView) findViewById(R.id.cardIconImageView);
    }

    /**
     * update OneLineCCViewComponent
     *
     * @param creditCard - {@link CreditCard}
     */
    public void updateResource(CreditCard creditCard) {
        updateResource(creditCard.getCardLastFourDigits(), creditCard.getExpirationDateForEditTextAndSpinner(), creditCard.getCardType());
    }

    /**
     * update OneLineCCViewComponent
     *
     * @param lastFourDigits
     * @param expDateString
     * @param type
     */
    public void updateResource(String lastFourDigits, String expDateString, String type) {
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
