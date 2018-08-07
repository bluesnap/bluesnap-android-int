package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperShippingFragment extends BlueSnapFragment {
    public static final String TAG = ReturningShopperShippingFragment.class.getSimpleName();
    private ShippingViewComponent shippingViewComponent;
    private Shopper shopper;

    public static ReturningShopperShippingFragment newInstance(Activity activity, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        ReturningShopperShippingFragment bsFragment = (ReturningShopperShippingFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new ReturningShopperShippingFragment();
            bsFragment.setArguments(bundle);
        }
        return bsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null)
            return null;

        final View inflate = inflater.inflate(R.layout.returning_shopper_shipping_fragment, container, false);

        // get Shopper
        shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();

        // set Shipping Details
        shippingViewComponent = inflate.findViewById(R.id.returningShoppershippingViewComponent);
        shippingViewComponent.updateViewResourceWithDetails(shopper.getShippingContactInfo());

        ButtonComponent buttonComponentView = inflate.findViewById(R.id.returningShopperShippingFragmentButtonComponentView);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.DONE, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndUpdate()) {
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, TAG);
                } else {
                    Log.d(TAG, "Invalid shopper contact info");
                }
            }
        });

        return inflate;
    }

    /**
     * invoked when the activity may be temporarily destroyed, save the instance state here
     */
    @Override
    public void onActivitySavedInstanceState(Bundle outState) {
        // get Credit Card Info
        shopper.setShippingContactInfo(getViewResourceDetails());
    }

    /**
     * get Credit Card Info from
     * {@link ShippingViewComponent}
     *
     * @return {@link ShippingContactInfo}
     */
    public ShippingContactInfo getViewResourceDetails() {
        return shippingViewComponent.getViewResourceDetails();
    }

    /**
     * validate and Update ShippingViewComponent
     *
     * @return boolean
     */
    public boolean validateAndUpdate() {
        boolean isValid = shippingViewComponent.validateInfo();
        if (isValid) {
            shopper.setShippingContactInfo(shippingViewComponent.getViewResourceDetails());
        }
        return isValid;
    }
}