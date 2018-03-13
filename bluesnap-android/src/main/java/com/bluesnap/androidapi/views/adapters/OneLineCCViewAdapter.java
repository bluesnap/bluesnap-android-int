package com.bluesnap.androidapi.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;

import java.util.ArrayList;

/**
 * Created by roy.biber on 04/03/2018.
 */

public class OneLineCCViewAdapter extends BaseAdapter {
    private TextView expTextView, ccLastFourDigitsTextView;
    private ImageView cardIconImageView;
    private final Activity context;
    private ArrayList<CreditCardInfo> creditCardInfos;

    public OneLineCCViewAdapter(Activity context, ArrayList<CreditCardInfo> creditCardInfos) {
        this.context = context;
        this.creditCardInfos = creditCardInfos;
    }

    @Override
    public int getCount() {
        return creditCardInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return creditCardInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return creditCardInfos.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.one_line_cc_view_component, null);
        }

        ccLastFourDigitsTextView = (TextView) convertView.findViewById(R.id.ccLastFourDigitsTextView);
        expTextView = (TextView) convertView.findViewById(R.id.expTextView);
        cardIconImageView = (ImageView) convertView.findViewById(R.id.cardIconImageView);
        CreditCard creditCard = creditCardInfos.get(position).getCreditCard();

        cardIconImageView.setImageResource(CreditCardTypeResolver.getCardTypeDrawable(CreditCardTypeResolver.getCardTypeResource(creditCard.getCardType())));
        ccLastFourDigitsTextView.setText(creditCard.getCardLastFourDigits());
        expTextView.setText(creditCard.getExpirationDateForEditTextAndSpinner());

        return convertView;

    }
}