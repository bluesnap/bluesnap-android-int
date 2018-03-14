package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class AmountTaxShippingComponent extends LinearLayout {
    private static final String TAG = AmountTaxShippingComponent.class.getSimpleName();
    private RelativeLayout shippingSameAsBillingRelativeLayout;
    private Switch shippingSameAsBillingSwitch;
    private LinearLayout amountTaxLinearLayout;
    private TextView amountTextView, taxTextView;
    private boolean isShippingSameAsBilling = false;

    public AmountTaxShippingComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    public AmountTaxShippingComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public AmountTaxShippingComponent(Context context) {
        super(context);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    private void initControl(final Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.amount_tax_shipping_component, this);

        shippingSameAsBillingRelativeLayout = (RelativeLayout) findViewById(R.id.shippingSameAsBillingRelativeLayout);
        shippingSameAsBillingSwitch = (Switch) findViewById(R.id.shippingSameAsBillingSwitch);
        amountTaxLinearLayout = (LinearLayout) findViewById(R.id.amountTaxLinearLayout);
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        taxTextView = (TextView) findViewById(R.id.taxTextView);

        setAmountTaxShipping();

        shippingSameAsBillingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShippingSameAsBilling = isChecked;
                BlueSnapLocalBroadcastManager.sendMessage(context, BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, isChecked, TAG);
            }
        });
    }

    /**
     * set Sub Total and Tax Amount or hide it
     * also, show/hide Shipping same as billing switch
     */
    public void setAmountTaxShipping() {
        SdkRequest sdkRequest = BlueSnapService.getInstance().getSdkRequest();

        assert sdkRequest != null;
        if (sdkRequest.isShippingRequired())
            shippingSameAsBillingRelativeLayout.setVisibility(VISIBLE);
        else
            shippingSameAsBillingRelativeLayout.setVisibility(GONE);

        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        if (priceDetails.isSubtotalTaxSet()) {
            amountTaxLinearLayout.setVisibility(VISIBLE);
            amountTextView.setText(setTextForAmountTaxView(priceDetails.getCurrencyNameCode(), priceDetails.getSubtotalAmount()));
            taxTextView.setText(setTextForAmountTaxView(priceDetails.getCurrencyNameCode(), priceDetails.getTaxAmount()));
        } else {
            amountTaxLinearLayout.setVisibility(GONE);
        }
    }

    /**
     * set Text For Sub Total and Tax Amount
     *
     * @param currencySymbol - The ISO 4217 currency name
     * @param amount         - amount
     * @return textView for displaying $0.00
     */
    public static String setTextForAmountTaxView(String currencySymbol, Double amount) {
        return String.format("%s %s",
                AndroidUtil.getCurrencySymbol(currencySymbol),
                AndroidUtil.getDecimalFormat().format(amount));
    }

    /**
     * check if isShippingSameAsBilling Switch is checked or no
     *
     * @return boolean
     */
    public boolean isShippingSameAsBilling() {
        return isShippingSameAsBilling;
    }

    public void setShippingSameAsBillingVisibility(int visibility) {
        this.shippingSameAsBillingRelativeLayout.setVisibility(visibility);
    }
}
