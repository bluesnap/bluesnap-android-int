package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.BillingViewSummarizedComponent;
import com.bluesnap.androidapi.views.components.OneLineCCViewComponent;
import com.bluesnap.androidapi.views.components.ShippingViewSummarizedComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperCreditCardFragment extends Fragment {
    public static final String TAG = ReturningShopperCreditCardFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private TextView shippingViewSummarizedTextView;
    private ShippingViewSummarizedComponent shippingViewSummarizedComponent;

    public static ReturningShopperCreditCardFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
        ReturningShopperCreditCardFragment bsFragment = (ReturningShopperCreditCardFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new ReturningShopperCreditCardFragment();
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
        final View inflate = inflater.inflate(R.layout.returning_shopper_credit_card_fragment, container, false);

        // get Shopper
        Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();
        assert shopper != null;

        // get Credit Card Info
        CreditCardInfo creditCardInfo = shopper.getNewCreditCardInfo();

        // set Billing Details
        BillingViewSummarizedComponent billingViewSummarizedComponent = (BillingViewSummarizedComponent) inflate.findViewById(R.id.billingViewSummarizedComponent);
        billingViewSummarizedComponent.updateResource(creditCardInfo.getBillingContactInfo());

        // set Shipping details or hide Shipping View
        shippingViewSummarizedTextView = (TextView) inflate.findViewById(R.id.shippingViewSummarizedTextView);
        shippingViewSummarizedComponent = (ShippingViewSummarizedComponent) inflate.findViewById(R.id.shippingViewSummarizedComponent);
        if (!BlueSnapService.getInstance().getSdkRequest().isShippingRequired()) {
            setVisibilityForShippingView(View.INVISIBLE);
        } else {
            shippingViewSummarizedComponent.updateResource(shopper.getShippingContactInfo());
            setVisibilityForShippingView(View.VISIBLE);
            BlueSnapLocalBroadcastManager.registerReceiver(inflater.getContext(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean isShippingSameAsBilling = intent.getBooleanExtra(BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, false);
                    if (isShippingSameAsBilling)
                        setVisibilityForShippingView(View.INVISIBLE);
                    else
                        setVisibilityForShippingView(View.VISIBLE);
                }
            });
        }

        // set Credit Card View Component details
        OneLineCCViewComponent oneLineCCViewComponent = (OneLineCCViewComponent) inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateResource(creditCardInfo.getCreditCard());

        return inflate;
    }

    /**
     * set Visibility For Shipping View
     *
     * @param visibility - View.INVISIBLE, View.VISIBLE, View.GONE
     */
    private void setVisibilityForShippingView(int visibility) {
        shippingViewSummarizedTextView.setVisibility(visibility);
        shippingViewSummarizedComponent.setVisibility(visibility);
    }
}