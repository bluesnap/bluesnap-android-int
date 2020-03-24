package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkRequestShopperRequirements;
import com.bluesnap.androidapi.models.SdkRequestSubscriptionCharge;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class AmountTaxShippingComponent extends LinearLayout {

    private static final String TAG = AmountTaxShippingComponent.class.getSimpleName();
    private RelativeLayout shippingSameAsBillingRelativeLayout;
    private LinearLayout amountTaxLinearLayout;
    private TextView amountTextView, taxTextView;
    private SdkRequestBase sdkRequest;
    private Switch shippingSameAsBillingSwitch;
    private boolean isShippingSameAsBilling = false;
    private RelativeLayout storeCardRelativeLayout;
    private Switch storeCardSwitch;
    private boolean isStoreCard = false;
    private TextView storeCardTextView;

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
        if (inflater == null) {
            Log.w(TAG, "Cannot get inflater from context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)");
        } else {
            inflater.inflate(R.layout.amount_tax_shipping_component, this);
        }

        shippingSameAsBillingRelativeLayout = findViewById(R.id.shippingSameAsBillingRelativeLayout);
        shippingSameAsBillingSwitch = findViewById(R.id.shippingSameAsBillingSwitch);
        amountTaxLinearLayout = findViewById(R.id.amountTaxLinearLayout);
        amountTextView = findViewById(R.id.amountTextView);
        taxTextView = findViewById(R.id.taxTextView);

        storeCardRelativeLayout = findViewById(R.id.storeCardRelativeLayout);
        storeCardSwitch = findViewById(R.id.storeCardSwitch);
//        storeCardTextView = findViewById(R.id.storeCardTextView);

        setAmountTaxShipping();

        shippingSameAsBillingSwitch.setTextColor(Color.GRAY);
        shippingSameAsBillingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShippingSameAsBilling = isChecked;
                BlueSnapLocalBroadcastManager.sendMessage(getContext(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, isChecked, TAG);
            }
        });

        storeCardSwitch.setTextColor(Color.GRAY);
        storeCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isStoreCard = isChecked;
                storeCardSwitch.setTextColor(Color.GRAY);
            }
        });
    }

    /**
     * set Sub Total and Tax Amount or hide it
     * also, show/hide Shipping same as billing switch, show/hide Store card switch
     */
    public void setAmountTaxShipping() {
        sdkRequest = BlueSnapService.getInstance().getSdkRequest();
        Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();

        if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired() && sdkRequest.getShopperCheckoutRequirements().isBillingRequired() && shopper.getShippingContactInfo().getFirstName() == null) {
            shippingSameAsBillingRelativeLayout.setVisibility(VISIBLE);
            shippingSameAsBillingSwitch.setChecked(true);
            isShippingSameAsBilling = true;
        } else
            shippingSameAsBillingRelativeLayout.setVisibility(GONE);

        if (sdkRequest.isHideStoreCardSwitch())
            storeCardRelativeLayout.setVisibility(GONE);
        else
            storeCardRelativeLayout.setVisibility(VISIBLE);

        setAmountTaxView();
    }

    /**
     * set Sub Total and Tax Amount
     */
    public void setAmountTaxView() {
        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        if (sdkRequest instanceof SdkRequestShopperRequirements || (sdkRequest instanceof SdkRequestSubscriptionCharge && priceDetails == null) || !priceDetails.isSubtotalTaxSet()) {
            amountTaxLinearLayout.setVisibility(GONE);
        } else {
            amountTaxLinearLayout.setVisibility(VISIBLE);
            amountTextView.setText(setTextForAmountTaxView(priceDetails.getCurrencyCode(), priceDetails.getSubtotalAmount()));
            taxTextView.setText(setTextForAmountTaxView(priceDetails.getCurrencyCode(), priceDetails.getTaxAmount()));
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
        if (GONE == visibility || INVISIBLE == visibility || sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
            this.shippingSameAsBillingRelativeLayout.setVisibility(visibility);
    }

    public void setAmountTaxVisibility(int visibility) {
        if (sdkRequest instanceof SdkRequestShopperRequirements || (sdkRequest instanceof SdkRequestSubscriptionCharge && sdkRequest.getPriceDetails() == null))
            amountTaxLinearLayout.setVisibility(GONE);
        else if (GONE == visibility || INVISIBLE == visibility || sdkRequest.getPriceDetails().isSubtotalTaxSet())
            this.amountTaxLinearLayout.setVisibility(visibility);
    }

    /**
     * check if storeCard Switch is checked or not
     *
     * @return boolean
     */
    public boolean isStoreCard() {
        return isStoreCard;
    }

    public void setStoreCardVisibility(int visibility) {
        if (GONE == visibility || INVISIBLE == visibility || !sdkRequest.isHideStoreCardSwitch())
            this.storeCardRelativeLayout.setVisibility(visibility);
    }

    public void sendShippingSameAsBillingBroadcast(boolean isShippingSameAsBilling) {
        shippingSameAsBillingSwitch.setChecked(isShippingSameAsBilling);
    }

    public boolean validateStoreCard(boolean isShopperRequirements, boolean isSubscriptionCharge) {
        if (!BlueSnapValidator.validateStoreCard(isShopperRequirements, isSubscriptionCharge, isStoreCard)) {
//            storeCardSwitch.setBackgroundColor(Color.RED);
            storeCardSwitch.setTextColor(Color.RED);
            return false;
        } else {
            return true;
        }
    }
}
