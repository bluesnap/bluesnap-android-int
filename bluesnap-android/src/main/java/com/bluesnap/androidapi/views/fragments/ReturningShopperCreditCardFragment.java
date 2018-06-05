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
import android.widget.TextView;

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
import com.bluesnap.androidapi.views.components.BillingViewSummarizedComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.OneLineCCViewComponent;
import com.bluesnap.androidapi.views.components.ShippingViewSummarizedComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperCreditCardFragment extends BlueSnapFragment {
    public static final String TAG = ReturningShopperCreditCardFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    private SdkRequest sdkRequest;
    private SdkResult sdkResult;
    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    private OneLineCCViewComponent oneLineCCViewComponent;

    public BillingViewSummarizedComponent billingViewSummarizedComponent;
    public ShippingViewSummarizedComponent shippingViewSummarizedComponent;
    private TextView shippingViewSummarizedTextView;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;

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
    public void onDestroyView() {
        super.onDestroyView();
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View inflate = inflater.inflate(R.layout.returning_shopper_credit_card_fragment, container, false);

        // get Shopper
        shopper = blueSnapService.getsDKConfiguration().getShopper();

        //get SDK Request
        sdkRequest = blueSnapService.getSdkRequest();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        // set Credit Card View Component details
        oneLineCCViewComponent = (OneLineCCViewComponent) inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateResource(newCreditCardInfo.getCreditCard());

        billingViewSummarizedComponent = (BillingViewSummarizedComponent) inflate.findViewById(R.id.billingViewSummarizedComponent);
        billingViewSummarizedComponent.updateResource(newCreditCardInfo.getBillingContactInfo());

        // set Summarized Shipping details or hide Shipping View
        shippingViewSummarizedTextView = (TextView) inflate.findViewById(R.id.shippingViewSummarizedTextView);
        shippingViewSummarizedComponent = (ShippingViewSummarizedComponent) inflate.findViewById(R.id.shippingViewSummarizedComponent);
        final ShippingInfo shippingContactInfo = shopper.getShippingContactInfo();
        if (!sdkRequest.isShippingRequired()) {
            setVisibilityForShippingView(View.INVISIBLE);
        } else {
            shippingViewSummarizedComponent.updateResource(shippingContactInfo);
            setVisibilityForShippingView(View.VISIBLE);
            BlueSnapLocalBroadcastManager.registerReceiver(inflater.getContext(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        }

        amountTaxShippingComponentView = (AmountTaxShippingComponent) inflate.findViewById(R.id.amountTaxShippingComponentView);
        amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.INVISIBLE);

        buttonComponentView = (ButtonComponent) inflate.findViewById(R.id.returningShppoerCCNFragmentButtonComponentView);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getCreditCard: " + newCreditCardInfo.getCreditCard());
                Log.d(TAG, "getBillingContactInfo: " + newCreditCardInfo.getBillingContactInfo());
                Log.d(TAG, "getShippingContactInfo: " + shippingContactInfo);
                CreditCardActivity creditCardActivity = (CreditCardActivity) getActivity();
                creditCardActivity.finishFromFragment(shopper);

            }
        });

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);

        if (sdkRequest.isShippingRequired()) {
            // calculate tax according to shipping country
            BlueSnapService.getInstance().updateTax(shippingContactInfo.getCountry(), shippingContactInfo.getState(), inflater.getContext());
        }

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

    /**
     * Broadcast Receiver for Credit Card Activity
     * Handles actions
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String event = intent.getAction();
            Log.d(TAG, event);

            if (BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE.equals(event))
                billingViewSummarizedComponent.updateResource(newCreditCardInfo.getBillingContactInfo());
            else if (BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE.equals(event))
                shippingViewSummarizedComponent.updateResource(shopper.getShippingContactInfo());
            else if (BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT.equals(event)) {
                amountTaxShippingComponentView.setAmountTaxShipping();
                amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.INVISIBLE);
                buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY);
            } /*else {
                boolean isShippingSameAsBilling = intent.getBooleanExtra(BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, false);
                if (isShippingSameAsBilling)
                    setVisibilityForShippingView(View.INVISIBLE);
                else
                    setVisibilityForShippingView(View.VISIBLE);
            }*/
        }
    };
}