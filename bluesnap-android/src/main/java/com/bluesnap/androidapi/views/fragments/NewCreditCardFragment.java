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
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.views.components.BillingViewComponent;
import com.bluesnap.androidapi.views.components.OneLineCCEditComponent;
import com.bluesnap.androidapi.views.components.ShippingViewComponent;

/**
 * Created by roy.biber on 20/02/2018.
 */

public class NewCreditCardFragment extends Fragment {
    public static final String TAG = NewCreditCardFragment.class.getSimpleName();
    private static FragmentManager fragmentManager;
    private BillingViewComponent billingViewComponent;
    private ShippingViewComponent shippingViewComponent;
    private OneLineCCEditComponent oneLineCCEditComponent;
    private Shopper shopper;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View inflate = inflater.inflate(R.layout.new_credit_card_fragment, container, false);

        billingViewComponent = (BillingViewComponent) inflate.findViewById(R.id.billingViewComponent);
        oneLineCCEditComponent = (OneLineCCEditComponent) inflate.findViewById(R.id.oneLineCCEditComponent);

        return inflate;
    }

    public boolean validateCreditCardInfoAndBillingInfo() {
        return oneLineCCEditComponent.validateInfo() && billingViewComponent.validateInfo() ;
    }

    public CreditCardInfo getCreditCardInfo() {
        CreditCardInfo creditCardInfo = new CreditCardInfo();
        creditCardInfo.setCreditCard(oneLineCCEditComponent.getNewCreditCard());
        creditCardInfo.setBillingContactInfo(billingViewComponent.getResource());
        return creditCardInfo;
    }
}
