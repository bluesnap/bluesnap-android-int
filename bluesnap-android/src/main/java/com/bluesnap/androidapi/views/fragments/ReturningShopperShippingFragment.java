package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperShippingFragment extends Fragment {
    public static final String TAG = ReturningShopperShippingFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private ShippingViewComponent shippingViewComponent;
    private ButtonComponent buttonComponentView;
    private Shopper shopper;

    public static ReturningShopperShippingFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
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

        // set Shipping Details
        shippingViewComponent = (ShippingViewComponent) inflate.findViewById(R.id.shippingViewComponent);
        assert shopper != null;
        shippingViewComponent.updateResource(shopper.getShippingContactInfo());

        buttonComponentView = (ButtonComponent) inflate.findViewById(R.id.buttonComponentView);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.DONE, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndUpdate())
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, TAG);
            }
        });

        return inflate;
    }

    /**
     * validate and Update ShippingViewComponent
     *
     * @return boolean
     */
    public boolean validateAndUpdate() {
        boolean isValid = shippingViewComponent.validateInfo();
        if (isValid) {
            shopper.setShippingContactInfo(shippingViewComponent.getResource());
        }
        return isValid;
    }
}