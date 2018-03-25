package com.bluesnap.androidapi.views.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.BlueSnapService;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ShippingViewComponent extends ContactInfoViewComponent {
    public static final String TAG = ShippingViewComponent.class.getSimpleName();

    public ShippingViewComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShippingViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShippingViewComponent(Context context) {
        super(context);
    }

    @Override
    void initControl(final Context context) {
        super.initControl(context);
        setEmailVisibility(GONE);
    }

    /**
     * update resource with details
     *
     * @param shippingInfo - {@link ShippingInfo}
     */
    public void updateResource(ShippingInfo shippingInfo) {
        super.updateResource(shippingInfo);
    }

    /**
     * get ShippingInfo Resource from inputs
     *
     * @return shipping info
     */
    public ShippingInfo getResource() {
        return new ShippingInfo(super.getResource());
    }

    /**
     * Validating form inputs
     *
     * @return boolean
     */
    @Override
    public boolean validateInfo() {
        return super.validateInfo();
    }

    @Override
    protected void updateTaxOnCountryStateChange() {

        BlueSnapService.getInstance().updateTax(getUserCountry(), inputState.getText().toString(), getContext());
    }

}