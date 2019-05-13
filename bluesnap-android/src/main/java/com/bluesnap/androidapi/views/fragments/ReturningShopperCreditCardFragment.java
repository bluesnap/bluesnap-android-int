package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
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
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BlueSnapValidator;
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
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();

    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    public BillingViewSummarizedComponent billingViewSummarizedComponent;
    public ShippingViewSummarizedComponent shippingViewSummarizedComponent;
    private TextView shippingViewSummarizedTextView;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;

    public static ReturningShopperCreditCardFragment newInstance(Activity activity, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null)
            return null;


        final View inflate = inflater.inflate(R.layout.returning_shopper_credit_card_fragment, container, false);

        // get Shopper
        shopper = blueSnapService.getsDKConfiguration().getShopper();

        //get SDK Request
        final SdkRequestBase sdkRequest = blueSnapService.getSdkRequest();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        // set Credit Card View Component details
        OneLineCCViewComponent oneLineCCViewComponent = inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateViewResourceWithDetails(newCreditCardInfo.getCreditCard());

        billingViewSummarizedComponent = inflate.findViewById(R.id.billingViewSummarizedComponent);
        billingViewSummarizedComponent.updateViewResourceWithDetails(newCreditCardInfo.getBillingContactInfo());

        // set Summarized Shipping details or hide Shipping View
        shippingViewSummarizedTextView = inflate.findViewById(R.id.shippingViewSummarizedTextView);
        shippingViewSummarizedComponent = inflate.findViewById(R.id.shippingViewSummarizedComponent);
        final ShippingContactInfo shippingContactInfo = shopper.getShippingContactInfo();
        if (!sdkRequest.getShopperCheckoutRequirements().isShippingRequired()) {
            setVisibilityForShippingView(View.INVISIBLE);
        } else {
            shippingViewSummarizedComponent.updateViewResourceWithDetails(shippingContactInfo);
            setVisibilityForShippingView(View.VISIBLE);
            BlueSnapLocalBroadcastManager.registerReceiver(inflater.getContext(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        }

        amountTaxShippingComponentView = inflate.findViewById(R.id.amountTaxShippingComponentView);
        amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.INVISIBLE);
        amountTaxShippingComponentView.setStoreCardVisibility(View.GONE);

        buttonComponentView = inflate.findViewById(R.id.returningShppoerCCNFragmentButtonComponentView);
        buttonComponentView.setBuyNowButton(buttonComponentText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BlueSnapValidator.billingInfoValidation(newCreditCardInfo.getBillingContactInfo(), sdkRequest.getShopperCheckoutRequirements().isEmailRequired(), sdkRequest.getShopperCheckoutRequirements().isBillingRequired())) {
                    BlueSnapLocalBroadcastManager.sendMessage(inflater.getContext(), BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_EDIT, TAG);
                    return;
                }
                Log.d(TAG, "BillingContactInfo is Valid");

                if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired() && !BlueSnapValidator.shippingInfoValidation(shippingContactInfo)) {
                    BlueSnapLocalBroadcastManager.sendMessage(inflater.getContext(), BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_EDIT, TAG);
                    return;
                }
                Log.d(TAG, "ShippingContactInfo is Valid");

                CreditCardActivity creditCardActivity = (CreditCardActivity) getActivity();
                creditCardActivity.finishFromFragment(shopper);

            }
        });

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);

        if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired()) {
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
                billingViewSummarizedComponent.updateViewResourceWithDetails(newCreditCardInfo.getBillingContactInfo());
            else if (BlueSnapLocalBroadcastManager.SUMMARIZED_SHIPPING_CHANGE.equals(event))
                shippingViewSummarizedComponent.updateViewResourceWithDetails(shopper.getShippingContactInfo());
            else if (BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT.equals(event)) {
                amountTaxShippingComponentView.setAmountTaxShipping();
                amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.INVISIBLE);
                amountTaxShippingComponentView.setStoreCardVisibility(View.INVISIBLE);
                buttonComponentView.setBuyNowButton(buttonComponentText);
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