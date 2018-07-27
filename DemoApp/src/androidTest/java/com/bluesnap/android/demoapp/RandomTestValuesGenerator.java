package com.bluesnap.android.demoapp;

import android.content.Context;

import com.bluesnap.androidapi.models.ContactInfo;
import com.bluesnap.androidapi.services.AndroidUtil;

import java.util.Random;

/**
 *
 */
public class RandomTestValuesGenerator {
    final double MINIMUM_AMOUNT = 0.01D;
    final double MINIMUM_TAX_PRECENT_AMOUNT = 0;
    double MAXIMUM_AMOUNT = 1000;
    Random random = new Random();

    public Double randomDemoAppPrice() {
        double result = MINIMUM_AMOUNT + (random.nextDouble() * (MAXIMUM_AMOUNT - MINIMUM_AMOUNT));
        return result;
    }

    public Double randomTaxPercentage() {
        double result = random.nextInt(20);
        return result;
    }

    //TODO: check if can be implemented

//    public String randomCheckoutCurrency(Context context) {
//        String[] currency_key_array = context.getResources().getStringArray(com.bluesnap.androidapi.R.array.currency_key_array);
//
//        String result = currency_key_array[random.nextInt(currency_key_array.length)];
//        return result;
//    }

//    public Double randomTaxPercentage() {
//        double result = MINIMUM_AMOUNT + (random.nextInt() * (100 - MINIMUM_TAX_PRECENT_AMOUNT));
//        return result;
//    }

    public String getAmountWithTaxString(Double amount, Double taxPrecentage) {
        double total = amount + amount * (taxPrecentage / 100);
        return AndroidUtil.getDecimalFormat().format(total);
    }

}
