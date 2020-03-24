package com.bluesnap.androidapi.views.components;

import android.content.Context;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.services.BlueSnapService;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ButtonComponent extends LinearLayout {
    private Button buyNowButton;

    /**
     * change ButtonComponentText to Enum
     */
    public enum ButtonComponentText {
        PAY, DONE, SHIPPING
    }

    public ButtonComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public ButtonComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public ButtonComponent(Context context) {
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
        inflater.inflate(R.layout.button_component, this);

        buyNowButton = findViewById(R.id.buyNowButton);
    }

    /**
     * set Buy Now Button Text and Click Listener
     *
     * @param onClickListener - View.OnClickListener function
     */
    public void setBuyNowButton(View.OnClickListener onClickListener) {
        buyNowButton.setOnClickListener(onClickListener);
    }

    /**
     * set Buy Now Button Text and Click Listener
     *
     * @param buttonComponentText - {@link ButtonComponentText}
     * @param onClickListener     - View.OnClickListener function
     */
    public void setBuyNowButton(ButtonComponentText buttonComponentText, View.OnClickListener onClickListener) {
        setBuyNowButton(buttonComponentText);
        buyNowButton.setOnClickListener(onClickListener);
    }

    /**
     * set Buy Now Button Text and Click Listener
     *
     * @param buttonComponentText - {@link ButtonComponentText}
     */
    public void setBuyNowButton(ButtonComponentText buttonComponentText) {

        if (buttonComponentText.equals(ButtonComponentText.PAY)) {
            buyNowButton.setText(BlueSnapService.getInstance().getSdkRequest().getBuyNowButtonText(this));
        } else if (buttonComponentText.equals(ButtonComponentText.DONE)) {
            buyNowButton.setText(getResources().getString(R.string.done));
        } else if (buttonComponentText.equals(ButtonComponentText.SHIPPING)) {
            buyNowButton.setText(getResources().getString(R.string.shipping));
        }
    }
}
