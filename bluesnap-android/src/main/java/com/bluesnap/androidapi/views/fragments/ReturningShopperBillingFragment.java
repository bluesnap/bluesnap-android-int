package com.bluesnap.androidapi.views.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.ButtonComponent;
import com.bluesnap.androidapi.views.components.OneLineCCViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class ReturningShopperBillingFragment extends Fragment {
    public static final String TAG = ReturningShopperBillingFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private BillingViewComponent billingViewComponent;
    private ButtonComponent buttonComponentView;
    private CreditCardInfo newCreditCardInfo;

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

        // get Credit Card Info
        newCreditCardInfo = BlueSnapService.getInstance().getsDKConfiguration().getShopper().getNewCreditCardInfo();

        // set Billing Details
        billingViewComponent = (BillingViewComponent) inflate.findViewById(R.id.billingViewComponent);
        billingViewComponent.updateResource(newCreditCardInfo.getBillingContactInfo());

        // set Credit Card View Component details
        OneLineCCViewComponent oneLineCCViewComponent = (OneLineCCViewComponent) inflate.findViewById(R.id.oneLineCCViewComponent);
        oneLineCCViewComponent.updateResource(newCreditCardInfo.getCreditCard());

        buttonComponentView = (ButtonComponent) inflate.findViewById(R.id.buttonComponentView);
        buttonComponentView.setBuyNowButton(ButtonComponent.ButtonComponentText.DONE, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAndUpdate())
                    BlueSnapLocalBroadcastManager.sendMessage(getActivity(), BlueSnapLocalBroadcastManager.SUMMARIZED_BILLING_CHANGE, TAG);
            }
        });

        return inflate;
    }

    /**
     * validate and Update BillingViewComponent
     *
     * @return boolean
     */
    public boolean validateAndUpdate() {
        boolean isValid = billingViewComponent.validateInfo();
        if (isValid) {
            newCreditCardInfo.setBillingContactInfo(billingViewComponent.getResource());
        }
        return isValid;
    }

}