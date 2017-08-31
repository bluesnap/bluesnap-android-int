package com.bluesnap.androidapi.models;

public class Events {

    public static class CurrencySelectionEvent {
        public final String newCurrencyNameCode;

        public CurrencySelectionEvent(String newCurrencyNameCode) {
            this.newCurrencyNameCode = newCurrencyNameCode;
        }
    }

    public static class TokenUpdatedEvent {

        public TokenUpdatedEvent() {
        }
    }

    public static class CurrencyUpdatedEvent {
        public String newCurrencyNameCode;
        public Double updatedPrice;
        public Double updatedTax;
        public Double updatedSubtotal;


        public CurrencyUpdatedEvent(Double newPrice, String newCurrencyNameCode, Double newTaxValue, Double newSubtotalAmount) {
            this.updatedPrice = newPrice;
            this.newCurrencyNameCode = newCurrencyNameCode;
            this.updatedTax = newTaxValue;
            this.updatedSubtotal = newSubtotalAmount;
        }
    }

    public static class LanguageChangeEvent {
        public final String newLanguage;

        public LanguageChangeEvent(String languagePick) {
            newLanguage = languagePick;
        }
    }
}
