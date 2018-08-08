package com.bluesnap.androidapi.models;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by shevie.chen on 3/14/2018.
 */

public class RatesTest {

    @Test
    public void testRates1() {

        String baseCurrencyCode = "USD";
        String baseCurrencyName = "United Stated Dollar";

        ArrayList<Currency> currencies = new ArrayList<>();

        Rates rates = new Rates(currencies, baseCurrencyCode, baseCurrencyName);

        Assert.assertEquals(baseCurrencyCode, rates.getMerchantStoreCurrency());
        Assert.assertEquals(baseCurrencyName, rates.getMerchantStoreCurrencyName());

        Set<String> currencyCodes = rates.getCurrencyCodes();
        Assert.assertEquals(1, currencyCodes.size());
        Assert.assertEquals(baseCurrencyCode, currencyCodes.iterator().next());

        Currency currencyByCode = rates.getCurrencyByCode(baseCurrencyCode);
        Assert.assertEquals(1D, currencyByCode.getConversionRate());
        Assert.assertEquals(baseCurrencyCode, currencyByCode.getQuoteCurrency());
        Assert.assertEquals(baseCurrencyName, currencyByCode.getQuoteCurrencyName());

        currencyByCode = rates.getCurrencyByCode("EUR");
        Assert.assertEquals(null, currencyByCode);
    }

    @Test
    public void testRates2() {

        String baseCurrencyCode = "USD";
        String baseCurrencyName = "United Stated Dollar";

        Currency currency = new Currency();
        currency.setConversionRate(2.0);
        currency.setQuoteCurrency("EUR");
        currency.setQuoteCurrencyName("EURO");

        ArrayList<Currency> currencies = new ArrayList<>();
        currencies.add(currency);

        Rates rates = new Rates(currencies, baseCurrencyCode, baseCurrencyName);

        Set<String> currencyCodes = rates.getCurrencyCodes();
        Assert.assertEquals(2, currencyCodes.size());

        final Iterator<String> iterator = currencyCodes.iterator();
        Assert.assertEquals("EUR", iterator.next());
        Assert.assertEquals(baseCurrencyCode, iterator.next());

        Currency currencyByCode = rates.getCurrencyByCode(baseCurrencyCode);
        Assert.assertEquals(1D, currencyByCode.getConversionRate());
        Assert.assertEquals(baseCurrencyCode, currencyByCode.getQuoteCurrency());
        Assert.assertEquals(baseCurrencyName, currencyByCode.getQuoteCurrencyName());

        currencyByCode = rates.getCurrencyByCode("EUR");
        Assert.assertEquals(2D, currencyByCode.getConversionRate());
        Assert.assertEquals("EUR", currencyByCode.getQuoteCurrency());
        Assert.assertEquals("EURO", currencyByCode.getQuoteCurrencyName());
    }

}
