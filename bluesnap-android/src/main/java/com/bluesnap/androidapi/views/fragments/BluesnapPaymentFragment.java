package com.bluesnap.androidapi.views.fragments;

import com.bluesnap.androidapi.models.Events;

/**
 * Interface for all fragments that are aware of payment details such as currency change or method change
 */
public interface BluesnapPaymentFragment {

    void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent);
}
