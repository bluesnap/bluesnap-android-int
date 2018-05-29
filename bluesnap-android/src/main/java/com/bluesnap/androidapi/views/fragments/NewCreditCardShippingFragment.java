package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingInfo;
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

public class NewCreditCardShippingFragment extends Fragment {
    public static final String TAG = NewCreditCardShippingFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private ShippingViewComponent shippingViewComponent;
    private LinearLayout shippingViewComponentLinearLayout;

    private SdkRequest sdkRequest;
    private SdkResult sdkResult;
    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;

    public static NewCreditCardShippingFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
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

//        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);
        final View inflate = inflater.inflate(R.layout.new_credit_card_shipping_fragment, container, false);

        // get Shopper
        shopper = blueSnapService.getsDKConfiguration().getShopper();

        //get SDK Request
        sdkRequest = blueSnapService.getSdkRequest();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        shippingViewComponent = (ShippingViewComponent) inflate.findViewById(R.id.returningShoppershippingViewComponent);
        shippingViewComponentLinearLayout = (LinearLayout) inflate.findViewById(R.id.shippingViewComponentLinearLayout);

        amountTaxShippingComponentView = (AmountTaxShippingComponent) inflate.findViewById(R.id.shippingAmountTaxShippingComponentView);
        buttonComponentView = (ButtonComponent) inflate.findViewById(R.id.shippingButtonComponentView);

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);

        finishFromFragmentWithShipping();

        return inflate;
    }

    /**
     * get ShippingInfo from
     * {@link ShippingViewComponent}
     *
     * @return {@link ShippingInfo}
     */
    public ShippingInfo getShippingInfo() {
        return shippingViewComponent.getResource();
    }

    /**
     * activate finish from fragment function {@link CreditCardActivity}
     * with the shopper created in this fragment
     */
    private void finishFromFragment() {
        Log.d(TAG, "getCreditCard: " + newCreditCardInfo.getCreditCard());
        Log.d(TAG, "getBillingContactInfo: " + newCreditCardInfo.getBillingContactInfo());
        Log.d(TAG, "getShippingContactInfo: " + shopper.getShippingContactInfo());
        ((CreditCardActivity) getActivity()).finishFromFragment(shopper);
    }

    /**
     * finish From Fragment assumes no shipping
     * validates Credit Card And Billing Info and moves to shipping
     */
    private void finishFromFragmentWithShipping() {
        ((CreditCardActivity) getActivity()).setHeaderTextView(TAG);
        amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.GONE);
        amountTaxShippingComponentView.setAmountTaxVisibility(View.VISIBLE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingViewComponent.validateInfo()) {
                    shopper.setShippingContactInfo(shippingViewComponent.getResource());
                    finishFromFragment();
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
                buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY);
            }
        }
    };
}
