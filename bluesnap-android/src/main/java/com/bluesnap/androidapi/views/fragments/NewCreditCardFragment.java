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

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.activities.CreditCardActivity;
import com.bluesnap.androidapi.views.components.AmountTaxShippingComponent;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.OneLineCCEditComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class NewCreditCardFragment extends BlueSnapFragment {
    public static final String TAG = NewCreditCardFragment.class.getSimpleName();
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private BillingViewComponent billingViewComponent;
    private OneLineCCEditComponent oneLineCCEditComponent;

    private SdkRequest sdkRequest;
    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;

    public static NewCreditCardFragment newInstance(Activity activity, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        NewCreditCardFragment bsFragment = (NewCreditCardFragment) fragmentManager.findFragmentByTag(TAG);

        if (bsFragment == null) {
            bsFragment = new NewCreditCardFragment();
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
        final View inflate = inflater.inflate(R.layout.new_credit_card_fragment, container, false);

        // get Shopper
        shopper = blueSnapService.getsDKConfiguration().getShopper();

        //get SDK Request
        sdkRequest = blueSnapService.getSdkRequest();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        billingViewComponent = inflate.findViewById(R.id.billingViewComponent);
        oneLineCCEditComponent = inflate.findViewById(R.id.oneLineCCEditComponent);

        amountTaxShippingComponentView = inflate.findViewById(R.id.amountTaxShippingComponentView);
        buttonComponentView = inflate.findViewById(R.id.newCCNFragmentButtonComponentView);

        if (!sdkRequest.isShippingRequired()) {
            finishFromFragmentNoShipping();
        } else {
            finishFromFragmentWithShipping();
        }

        return inflate;
    }


    /**
     * invoked when the activity may be temporarily destroyed, save the instance state here
     */
    @Override
    public void onActivitySavedInstanceState() {
        // get Credit Card Info
        shopper.setNewCreditCardInfo(getViewResourceDetails());
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     */
    @Override
    public void onActivityRestoredInstanceState() {
        updateViewResourceWithDetails(shopper.getNewCreditCardInfo());
    }

    @Override
    public void unregisterBlueSnapLocalBroadcastReceiver() {
        billingViewComponent.unregisterBlueSnapLocalBroadcastReceiver();
    }

    @Override
    public void registerBlueSnapLocalBroadcastReceiver() {
        billingViewComponent.registerBlueSnapLocalBroadcastReceiver();
    }

    /**
     * validate And Set Credit Card Info And Billing Info to new Credit Card Info {@link Shopper}
     *
     * @return boolean - validation success or failure
     */
    public boolean validateAndSetCreditCardInfoAndBillingInfo() {
        boolean isValid = oneLineCCEditComponent.validateInfo();
        isValid &= billingViewComponent.validateInfo();
        if (isValid) {
            newCreditCardInfo.setBillingContactInfo(billingViewComponent.getViewResourceDetails());
            newCreditCardInfo.setCreditCard(oneLineCCEditComponent.getNewCreditCard());
        }
        return isValid;
    }

    /**
     * get Credit Card Info from
     * {@link OneLineCCEditComponent}
     * {@link BillingViewComponent}
     *
     * @return {@link CreditCardInfo}
     */
    public CreditCardInfo getViewResourceDetails() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        creditCardInfo.setCreditCard(oneLineCCEditComponent.getViewResourceDetails());
        creditCardInfo.setBillingContactInfo(billingViewComponent.getViewResourceDetails());
        return creditCardInfo;
    }

    /**
     * set Credit Card in view - {@link OneLineCCEditComponent}, {@link BillingViewComponent}
     *
     * @param creditCardInfo - {@link CreditCardInfo}
     */
    public void updateViewResourceWithDetails(CreditCardInfo creditCardInfo) {
        oneLineCCEditComponent.updateViewResourceWithDetails(creditCardInfo.getCreditCard());
        billingViewComponent.updateViewResourceWithDetails(creditCardInfo.getBillingContactInfo());
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
     * validates Credit Card And Billing Info
     * activate the finishFromFragment function
     */
    private void finishFromFragmentNoShipping() {
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        BlueSnapService.getInstance().updateTax(billingViewComponent.getUserCountry(), billingViewComponent.getState(), getActivity());
        amountTaxShippingComponentView.setAmountTaxVisibility(View.VISIBLE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSetCreditCardInfoAndBillingInfo()) {
                    if (sdkRequest.isShippingRequired() && amountTaxShippingComponentView.isShippingSameAsBilling())
                        shopper.setShippingContactInfo(billingViewComponent.getViewResourceDetails());
                    finishFromFragment();
                }
            }
        });
    }

    /**
     * finish From Fragment assumes no shipping
     * validates Credit Card And Billing Info and moves to shipping
     */
    public void finishFromFragmentWithShipping() {
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        BlueSnapService.getInstance().updateTax("", "", getActivity());
        if (!sdkRequest.isBillingRequired())
            amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.GONE);
        amountTaxShippingComponentView.setAmountTaxVisibility(View.GONE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.SHIPPING, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSetCreditCardInfoAndBillingInfo()) {
                    shopper.setNewCreditCardInfo(newCreditCardInfo);
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.NEW_CARD_SHIPPING_CHANGE, TAG);
                    unregisterBlueSnapLocalBroadcastReceiver();
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
                buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY);
            } else if (BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH.equals(event)) {
                billingViewComponent.requestFocusOnNameInput();
            } else {
                boolean isShippingSameAsBilling = intent.getBooleanExtra(BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, false);
                billingViewComponent.setShippingSameAsBilling(isShippingSameAsBilling);
                if (isShippingSameAsBilling) {
                    finishFromFragmentNoShipping();
                } else {
                    finishFromFragmentWithShipping();
                }
            }
        }
    };
}
