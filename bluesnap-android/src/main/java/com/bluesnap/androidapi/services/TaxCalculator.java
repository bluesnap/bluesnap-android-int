package com.bluesnap.androidapi.services;

import com.bluesnap.androidapi.models.PriceDetails;

/**
 * Created by shevie.chen on 3/14/2018.
 */

public interface TaxCalculator {

    void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails);
}
