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
import android.widget.ScrollView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkRequestShopperRequirements;
import com.bluesnap.androidapi.models.SdkRequestSubscriptionCharge;
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
    private ScrollView scrollView;

    private SdkRequestBase sdkRequest;
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
        Log.d(TAG, "onDestroyView() was called");
        super.onDestroyView();
        oneLineCCEditComponent.clear();
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null)
            return null;
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
        scrollView = inflate.findViewById(R.id.billingScrollView);

        amountTaxShippingComponentView = inflate.findViewById(R.id.amountTaxShippingComponentView);
        buttonComponentView = inflate.findViewById(R.id.billingButtonComponentView);

        if (!sdkRequest.getShopperCheckoutRequirements().isShippingRequired() || amountTaxShippingComponentView.isShippingSameAsBilling()) {
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
    public void onActivitySavedInstanceState(Bundle outState) {
        // get Credit Card Info
        shopper.setNewCreditCardInfo(getViewResourceDetails());
        outState.putBoolean("isShippingSameAsBilling", amountTaxShippingComponentView.isShippingSameAsBilling());
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState(). We restore some state in onCreate(), while we can optionally restore
     * other state here, possibly usable after onStart() has completed.
     * The savedInstanceState Bundle is same as the one used in onCreate().
     */
    @Override
    public void onActivityRestoredInstanceState(Bundle savedInstanceState) {
        updateViewResourceWithDetails(shopper.getNewCreditCardInfo());
        // Returns the value associated with the given key, or false if no mapping of the desired type exists for the given key.
        amountTaxShippingComponentView.sendShippingSameAsBillingBroadcast(savedInstanceState.getBoolean("isShippingSameAsBilling"));
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
        boolean isBillingValid = billingViewComponent.validateInfo();
        isValid &= isBillingValid;
        if (isValid) {
            newCreditCardInfo.setBillingContactInfo(billingViewComponent.getViewResourceDetails());
            newCreditCardInfo.setCreditCard(oneLineCCEditComponent.getNewCreditCard());
        } else if (!isBillingValid) {
            scrollView.post(() -> scrollView.smoothScrollTo(0, billingViewComponent.getFirstErrorEnabledOfTextInputEditTextTopPosition()));
        }
        return isValid;
    }

    /**
     * validate store card
     *
     * @return boolean - validation success or failure
     */
    public boolean validateStoreCard() {
        if (!sdkRequest.isHideStoreCardSwitch()) { //storeCard switch is visible
            boolean isValid = amountTaxShippingComponentView.validateStoreCard(sdkRequest instanceof SdkRequestShopperRequirements, sdkRequest instanceof SdkRequestSubscriptionCharge);

            if (isValid) {
                shopper.setStoreCard(amountTaxShippingComponentView.isStoreCard());
            }

            return isValid;
        } else { //storeCard switch is hidden
            return true;
        }
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
        //Log.d(TAG, "getCreditCard: " + newCreditCardInfo.getCreditCard());
        //Log.d(TAG, "getBillingContactInfo: " + newCreditCardInfo.getBillingContactInfo());
        //Log.d(TAG, "getShippingContactInfo: " + shopper.getShippingContactInfo());
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
        buttonComponentView.setBuyNowButton(buttonComponentText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oneLineCCEditComponent.getCreditCardNumberEditText().hasFocus())
                    oneLineCCEditComponent.getCvvEditText().requestFocus();
                if (validateAndSetCreditCardInfoAndBillingInfo() && validateStoreCard()) {
                    shopper.setNewCreditCardInfo(newCreditCardInfo);
                    if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired() && amountTaxShippingComponentView.isShippingSameAsBilling())
                        shopper.setShippingContactInfo(billingViewComponent.getViewResourceDetails());
                    finishFromFragment();
                }
            }
        });
    }

    /**
     * finish From Fragment assumes with shipping
     * validates Credit Card And Billing Info and moves to shipping
     */
    public void finishFromFragmentWithShipping() {
        BlueSnapLocalBroadcastManager.unregisterReceiver(getActivity(), broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH, broadcastReceiver);
        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        BlueSnapService.getInstance().updateTax("", "", getActivity());
        amountTaxShippingComponentView.setAmountTaxVisibility(View.GONE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.SHIPPING, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oneLineCCEditComponent.getCreditCardNumberEditText().hasFocus())
                    oneLineCCEditComponent.getCvvEditText().requestFocus();
                if (validateAndSetCreditCardInfoAndBillingInfo() && validateStoreCard()) {
                    unregisterBlueSnapLocalBroadcastReceiver();
                    shopper.setNewCreditCardInfo(newCreditCardInfo);
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.NEW_CARD_SHIPPING_CHANGE, TAG);
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
                amountTaxShippingComponentView.setAmountTaxView();
                buttonComponentView.setBuyNowButton(buttonComponentText);
            } else if (BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH.equals(event)) {
                // billingViewComponent.requestFocusOnNameInput();
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
