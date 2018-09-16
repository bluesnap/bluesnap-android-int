package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.OneLineCCViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperBillingFragment extends BlueSnapFragment {
    public static final String TAG = ReturningShopperBillingFragment.class.getSimpleName();
    private BillingViewComponent billingViewComponent;
    private CreditCardInfo newCreditCardInfo;
    private ScrollView scrollView;

    public static ReturningShopperBillingFragment newInstance(Activity activity, Bundle bundle) {
        FragmentManager fragmentManager = activity.getFragmentManager();
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

        // get Credit Card Info
        Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();
        newCreditCardInfo = shopper.getNewCreditCardInfo();

        BillingContactInfo newCreditCardBillingContactInfo;

        // get billing contact info
        if (savedInstanceState != null) { //restoring fragment
            newCreditCardBillingContactInfo = savedInstanceState.getParcelable("billing contact info");
        } else if (newCreditCardInfo != null) { // new fragment
            newCreditCardBillingContactInfo = newCreditCardInfo.getBillingContactInfo();
        } else {
            return null;
        }

        // set Billing Details
        billingViewComponent = inflate.findViewById(R.id.billingViewComponent);
        billingViewComponent.updateViewResourceWithDetails(newCreditCardBillingContactInfo);
        scrollView = inflate.findViewById(R.id.billingViewComponentScrollView);

        // set Credit Card View Component details
        OneLineCCViewComponent oneLineCCViewComponent = inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateViewResourceWithDetails(newCreditCardInfo.getCreditCard());

        ButtonComponent buttonComponentView = inflate.findViewById(R.id.returningShopperBillingFragmentButtonComponentView);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.DONE, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndUpdate())
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, TAG);
            }
        });

        return inflate;
    }

    /**
     * invoked when the activity may be temporarily destroyed, save the instance state here
     */
    @Override
    public void onActivitySavedInstanceState(Bundle outState) {
        // get Credit Card Info
        //Shopper shopper = BlueSnapService.getInstance().getsDKConfiguration().getShopper();
        BillingContactInfo savedBillingContactInfo = new BillingContactInfo(newCreditCardInfo.getBillingContactInfo());

        outState.putParcelable("billing contact info", savedBillingContactInfo);
    }

    /**
     * get Credit Card Info from
     * {@link BillingViewComponent}
     *
     * @return {@link BillingContactInfo}
     */
    public BillingContactInfo getViewResourceDetails() {
        return billingViewComponent.getViewResourceDetails();
    }

    /**
     * validate and Update BillingViewComponent
     *
     * @return boolean
     */
    public boolean validateAndUpdate() {
        boolean isValid = billingViewComponent.validateInfo();
        if (isValid) {
            newCreditCardInfo.setBillingContactInfo(billingViewComponent.getViewResourceDetails());
        } else
            scrollView.post(() -> scrollView.smoothScrollTo(0, billingViewComponent.getFirstErrorEnabledOfTextInputEditTextTopPosition()));
        return isValid;
    }

}