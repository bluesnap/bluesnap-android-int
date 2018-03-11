package com.bluesnap.androidapi.views.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;

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
        //inputEmail.setVisibility(GONE);

        /*BlueSnapLocalBroadcastManager.registerReceiver(context, BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE_RESPONSE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (validateInfo()) {
                    // get Shopper
                    Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();
                    assert shopper != null;
                    shopper.setShippingContactInfo(getResource());
                }
            }
        });*/
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
}