package com.bluesnap.androidapi.services;

import com.bluesnap.androidapi.models.PriceDetails;

/**
 * Created by shevie.chen on 3/14/2018.
 *
 * This interface may be implemented by merchants that have different tax calculation logic
 * depending on the selected shipping country.
 * The 'updateTax' method will be called when there is a change in the shopper's shipping country,
 * and it should update the 'priceDetails' object's taxAmount property according to the amount and
 * shipping country/state details.
 */

public interface TaxCalculator {

    void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails);
}
