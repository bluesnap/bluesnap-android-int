package com.bluesnap.androidapi.views.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;

public class BlueSnapFragment extends Fragment {
    public void onActivitySavedInstanceState(Bundle outState) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (BlueSnapService.getInstance().getSdkRequest() == null) {
            this.getActivity().setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, "The checkout process was interrupted."));
            this.getActivity().finish();
            return null;
        }
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void onActivityRestoredInstanceState(Bundle savedInstanceState) {

    }

    public void onActivityBackPressed() {

    }
}
