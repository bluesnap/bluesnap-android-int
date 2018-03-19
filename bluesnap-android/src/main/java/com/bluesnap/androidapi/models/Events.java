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
        public PriceDetails updatedPriceDetails;

        public CurrencyUpdatedEvent(PriceDetails updatedPriceDetails) {
            this.updatedPriceDetails = updatedPriceDetails;
        }
    }

    public static class LanguageChangeEvent {
        public final String newLanguage;

        public LanguageChangeEvent(String languagePick) {
            newLanguage = languagePick;
        }
    }
}
