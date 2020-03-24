package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.activities.CreditCardActivity;
import com.bluesnap.androidapi.views.components.AmountTaxShippingComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class NewCreditCardShippingFragment extends BlueSnapFragment {
    public static final String TAG = NewCreditCardShippingFragment.class.getSimpleName();
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private ShippingViewComponent shippingViewComponent;
    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;
    private ScrollView scrollView;

    public static NewCreditCardShippingFragment newInstance(Activity activity, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        NewCreditCardShippingFragment bsFragment = (NewCreditCardShippingFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new NewCreditCardShippingFragment();
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
    public void onDestroyView() {
        super.onDestroyView();
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null)
            return null;

//        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);
        final View inflate = inflater.inflate(R.layout.new_credit_card_shipping_fragment, container, false);

        // get Shopper
        shopper = blueSnapService.getsDKConfiguration().getShopper();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        shippingViewComponent = inflate.findViewById(R.id.newShoppershippingViewComponent);
        scrollView = inflate.findViewById(R.id.shippingScrollView);

        amountTaxShippingComponentView = inflate.findViewById(R.id.shippingAmountTaxShippingComponentView);
        buttonComponentView = inflate.findViewById(R.id.shippingButtonComponentView);

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);

        finishFromFragmentWithShipping();

        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onActivityRestoredInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityBackPressed() {
        // get Shipping Info
        shopper.setShippingContactInfo(getShippingInfo());
    }

    /**
     * invoked when the activity may be temporarily destroyed, save the instance state here
     */
    @Override
    public void onActivitySavedInstanceState(Bundle outState) {
        // get Shipping Info
        shopper.setShippingContactInfo(getShippingInfo());
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     */
    @Override
    public void onActivityRestoredInstanceState(Bundle savedInstanceState) {
        updateViewResourceWithDetails(shopper.getShippingContactInfo());
    }

    /**
     * get ShippingContactInfo from
     * {@link ShippingViewComponent}
     *
     * @return {@link ShippingContactInfo}
     */
    public ShippingContactInfo getShippingInfo() {
        return shippingViewComponent.getViewResourceDetails();
    }

    /**
     * set Shipping Info in view - {@link ShippingViewComponent}
     *
     * @param shippingContactInfo - {@link ShippingContactInfo}
     */
    public void updateViewResourceWithDetails(ShippingContactInfo shippingContactInfo) {
        shippingViewComponent.updateViewResourceWithDetails(shippingContactInfo);
    }

    /**
     * activate finish from fragment function {@link CreditCardActivity}
     * with the shopper created in this fragment
     */
    private void finishFromFragment() {
        Log.d(TAG, "getCreditCard: " + newCreditCardInfo.getCreditCard());
        Log.d(TAG, "getBillingContactInfo: " + newCreditCardInfo.getBillingContactInfo());
        Log.d(TAG, "getShippingContactInfo: " + shopper.getShippingContactInfo());
        Activity activity = getActivity();
        if (activity instanceof CreditCardActivity) {
            ((CreditCardActivity) activity).finishFromFragment(shopper);
        } else {
            Log.w(TAG, "activity is NOT instanceof CreditCardActivity");
        }
    }

    /**
     * finish From Fragment assumes no shipping
     * validates Credit Card And Billing Info and moves to shipping
     */
    private void finishFromFragmentWithShipping() {
        ((CreditCardActivity) getActivity()).setHeaderTextView(TAG);
        amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.GONE);
        amountTaxShippingComponentView.setAmountTaxVisibility(View.VISIBLE);
        amountTaxShippingComponentView.setStoreCardVisibility(View.GONE);

        buttonComponentView.setBuyNowButton(buttonComponentText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingViewComponent.validateInfo()) {
                    shopper.setShippingContactInfo(shippingViewComponent.getViewResourceDetails());
                    finishFromFragment();
                } else {
                    scrollView.post(() -> scrollView.smoothScrollTo(0, shippingViewComponent.getFirstErrorEnabledOfTextInputEditTextTopPosition()));
                }
            }
        });
    }

    /**
     * Broadcast Receiver for Credit Card Activity
     * Handles actions
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String event = intent.getAction();
            Log.d(TAG, event);
            if (BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT.equals(event)) {
                amountTaxShippingComponentView.setAmountTaxShipping();
                amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.GONE);
                amountTaxShippingComponentView.setStoreCardVisibility(View.GONE);
                buttonComponentView.setBuyNowButton(buttonComponentText);
            }
        }
    };
}
