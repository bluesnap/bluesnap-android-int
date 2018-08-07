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

import java.util.List;

/**
 * Created by roy.biber on 04/03/2018.
 */

public class OneLineCCViewAdapter extends BaseAdapter {

    private final Activity context;
    private List<CreditCardInfo> creditCardInfos;

    public OneLineCCViewAdapter(Activity context, List<CreditCardInfo> creditCardInfos) {
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

        TextView ccLastFourDigitsTextView = convertView.findViewById(R.id.ccLastFourDigitsTextView);
        TextView expTextView = convertView.findViewById(R.id.expTextView);
        ImageView cardIconImageView = convertView.findViewById(R.id.cardIconImageView);
        CreditCard creditCard = creditCardInfos.get(position).getCreditCard();

        CreditCardTypeResolver creditCardTypeResolver = CreditCardTypeResolver.getInstance();
        cardIconImageView.setImageResource(creditCardTypeResolver.getCardTypeDrawable(creditCardTypeResolver.getCardTypeResource(creditCard.getCardType())));
        ccLastFourDigitsTextView.setText(creditCard.getCardLastFourDigits());
        expTextView.setText(creditCard.getExpirationDateForEditTextAndSpinner());

        return convertView;

    }
}
