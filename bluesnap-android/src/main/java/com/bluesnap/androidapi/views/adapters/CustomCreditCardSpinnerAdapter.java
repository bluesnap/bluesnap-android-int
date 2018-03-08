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
 * Created by roy.biber on 14/06/2016.
 */
public class CustomCreditCardSpinnerAdapter extends BaseAdapter {

    private final Activity context;
    private ArrayList<CreditCardInfo> creditCardInfos;

    public CustomCreditCardSpinnerAdapter(Activity context, ArrayList<CreditCardInfo> creditCardInfos) {
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
            convertView = inflater.inflate(R.layout.creditcard_spinner_view, null);
        }

        TextView spinnerLastFourDigitsTextView = (TextView) convertView.findViewById(R.id.spinnerLastFourDigitsTextView);
        TextView spinnerExpDateTextView = (TextView) convertView.findViewById(R.id.spinnerExpDateTextView);
        ImageView spinnerCardTypeImageView = (ImageView) convertView.findViewById(R.id.spinnerCardTypeImageView);
        CreditCard creditCard = creditCardInfos.get(position).getCreditCard();

        spinnerCardTypeImageView.setImageResource(CreditCardTypeResolver.getCardTypeDrawable(CreditCardTypeResolver.getCardTypeResource(creditCard.getCardType())));

        if (!CreditCardTypeResolver.NEWCARD.equals(creditCard.getCardType())) {
            spinnerLastFourDigitsTextView.setVisibility(View.VISIBLE);
            spinnerLastFourDigitsTextView.setText(creditCard.getCardLastFourDigits());
            spinnerExpDateTextView.setText(creditCard.getExpirationDateForEditTextAndSpinner());
        } else {
            spinnerLastFourDigitsTextView.setVisibility(View.GONE);
            spinnerExpDateTextView.setText(R.string.newCardText);
        }


        return convertView;

    }

}
