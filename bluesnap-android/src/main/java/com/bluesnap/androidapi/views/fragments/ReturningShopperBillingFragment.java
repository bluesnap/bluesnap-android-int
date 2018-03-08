package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BillingInfo;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.OneLineCCViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperBillingFragment extends Fragment {
    public static final String TAG = ReturningShopperBillingFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private BillingViewComponent billingViewComponent;

    public static ReturningShopperBillingFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
        ReturningShopperBillingFragment bsFragment = (ReturningShopperBillingFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new ReturningShopperBillingFragment();
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
        final View inflate = inflater.inflate(R.layout.returning_shopper_billing_fragment, container, false);

        // get Shopper
        Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();

        // get Credit Card Info
        assert shopper != null;
        CreditCardInfo creditCardInfo = shopper.getNewCreditCardInfo();

        // set Billing Details
        billingViewComponent = (BillingViewComponent) inflate.findViewById(R.id.billingViewComponent);
        billingViewComponent.updateResource(creditCardInfo.getBillingContactInfo());

        // set Credit Card View Component details
        OneLineCCViewComponent oneLineCCViewComponent = (OneLineCCViewComponent) inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateResource(creditCardInfo.getCreditCard());

        return inflate;
    }
}