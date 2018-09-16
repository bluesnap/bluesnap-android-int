package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

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
    private ScrollView scrollView;
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
        final View inflate = inflater.inflate(R.layout.returning_shopper_shipping_fragment, container, false);

        // get Shopper
        shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();

        ShippingContactInfo shippingContactInfo;

        // get shipping contact info
        if (savedInstanceState != null) { //restoring fragment
            shippingContactInfo = savedInstanceState.getParcelable("shipping contact info");
        } else { // new fragment
            shippingContactInfo = shopper.getShippingContactInfo();
        }

        // set Shipping Details
        shippingViewComponent = inflate.findViewById(R.id.returningShoppershippingViewComponent);
        shippingViewComponent.updateViewResourceWithDetails(shippingContactInfo);
        scrollView = inflate.findViewById(R.id.shippingViewComponentScrollView);

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
        // get Shipping Info
        ShippingContactInfo savedShippingContactInfo = new ShippingContactInfo(shopper.getShippingContactInfo());

        outState.putParcelable("shipping contact info", savedShippingContactInfo);
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
        } else
            scrollView.post(() -> scrollView.smoothScrollTo(0, shippingViewComponent.getFirstErrorEnabledOfTextInputEditTextTopPosition()));
        return isValid;
    }
}