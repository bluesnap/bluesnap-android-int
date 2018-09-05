package com.bluesnap.android.demoapp;

import android.content.Context;

import java.util.Arrays;
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

    public int randomReturningShopperCardPosition() {
        int result = random.nextInt(7);
        return result;
    }

    public String[] randomReturningShopperCountry(Context context) {
        String[] result = new String[2];
        String[] countryKeyArray = context.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_key_array);
        String[] countryValueArray = context.getResources().getStringArray(com.bluesnap.androidapi.R.array.country_value_array);
        String countryKey = countryKeyArray[random.nextInt(countryKeyArray.length)];
        String countryValue = countryValueArray[Arrays.asList(countryKeyArray).indexOf(countryKey)];

        result[0] = countryKey;
        result[1] = countryValue;

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
        return TestUtils.getDecimalFormat().format(total);
    }

}
