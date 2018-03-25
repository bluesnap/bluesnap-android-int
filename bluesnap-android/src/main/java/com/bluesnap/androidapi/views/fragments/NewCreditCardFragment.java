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
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.activities.CreditCardActivity;
import com.bluesnap.androidapi.views.components.AmountTaxShippingComponent;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.OneLineCCEditComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class NewCreditCardFragment extends Fragment {
    public static final String TAG = NewCreditCardFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private final BlueSnapService blueSnapService = BlueSnapService.getInstance();
    private BillingViewComponent billingViewComponent;
    private ShippingViewComponent shippingViewComponent;
    private LinearLayout shippingViewComponentLinearLayout, billingViewComponentLinearLayout;
    private OneLineCCEditComponent oneLineCCEditComponent;

    private SdkRequest sdkRequest;
    private SdkResult sdkResult;
    private Shopper shopper;
    private CreditCardInfo newCreditCardInfo;

    private AmountTaxShippingComponent amountTaxShippingComponentView;
    private ButtonComponent buttonComponentView;

    public static NewCreditCardFragment newInstance(Activity activity, Bundle bundle) {
        fragmentManager = activity.getFragmentManager();
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
        final View inflate = inflater.inflate(R.layout.new_credit_card_fragment, container, false);

        // get Shopper
        shopper = new Shopper();

        //get SDK Request
        sdkRequest = blueSnapService.getSdkRequest();

        // get Credit Card Info
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        billingViewComponent = (BillingViewComponent) inflate.findViewById(R.id.billingViewComponent);
        billingViewComponentLinearLayout = (LinearLayout) inflate.findViewById(R.id.billingViewComponentLinearLayout);
        shippingViewComponent = (ShippingViewComponent) inflate.findViewById(R.id.shippingViewComponent);
        shippingViewComponentLinearLayout = (LinearLayout) inflate.findViewById(R.id.shippingViewComponentLinearLayout);
        oneLineCCEditComponent = (OneLineCCEditComponent) inflate.findViewById(R.id.oneLineCCEditComponent);

        amountTaxShippingComponentView = (AmountTaxShippingComponent) inflate.findViewById(R.id.amountTaxShippingComponentView);
        buttonComponentView = (ButtonComponent) inflate.findViewById(R.id.buttonComponentView);

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.ONE_LINE_CC_EDIT_FINISH, broadcastReceiver);

        BlueSnapLocalBroadcastManager.registerReceiver(getActivity(), BlueSnapLocalBroadcastManager.CURRENCY_UPDATED_EVENT, broadcastReceiver);
        if (!sdkRequest.isShippingRequired()) {
            finishFromFragmentNoShipping();
        } else {
            finishFromFragmentWithShipping();
            BlueSnapLocalBroadcastManager.registerReceiver(inflater.getContext(), BlueSnapLocalBroadcastManager.SHIPPING_SWITCH_ACTIVATED, broadcastReceiver);
        }

        return inflate;
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
            newCreditCardInfo.setBillingContactInfo(billingViewComponent.getResource());
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
    public CreditCardInfo getCreditCardInfo() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        creditCardInfo.setCreditCard(oneLineCCEditComponent.getNewCreditCard());
        creditCardInfo.setBillingContactInfo(billingViewComponent.getResource());
        return creditCardInfo;
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
        amountTaxShippingComponentView.setAmountTaxVisibility(View.VISIBLE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.PAY, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSetCreditCardInfoAndBillingInfo()) {
                    if (sdkRequest.isShippingRequired() && amountTaxShippingComponentView.isShippingSameAsBilling())
                        shopper.setShippingContactInfo(billingViewComponent.getResource());
                    finishFromFragment();
                }
            }
        });
    }

    /**
     * finish From Fragment assumes no shipping
     * validates Credit Card And Billing Info and moves to shipping
     */
    private void finishFromFragmentWithShipping() {
        amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.VISIBLE);
        amountTaxShippingComponentView.setAmountTaxVisibility(View.GONE);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.SHIPPING, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSetCreditCardInfoAndBillingInfo()) {
                    ((CreditCardActivity) getActivity()).setHeaderTextView(ReturningShopperShippingFragment.TAG);
                    amountTaxShippingComponentView.setShippingSameAsBillingVisibility(View.GONE);
                    amountTaxShippingComponentView.setAmountTaxVisibility(View.VISIBLE);
                    billingViewComponentLinearLayout.setVisibility(View.GONE);
                    oneLineCCEditComponent.setVisibility(View.GONE);
                    shippingViewComponentLinearLayout.setVisibility(View.VISIBLE);
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
                if (isShippingSameAsBilling) {
                    finishFromFragmentNoShipping();

                } else {
                    finishFromFragmentWithShipping();
                }
            }
        }
    };
}
