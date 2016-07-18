package com.droitfintech.dao;



public interface SwapCalendarFeatureConstrainedLevel {
    String getCurrency();

    SwapCurrencyRules getImmSwapCurrencyRules();

    void setImmSwapCurrencyRules(SwapCurrencyRules immSwapCurrencyRules);

    void removeImmSwapCurrencyRules(SwapCurrencyRules immSwapCurrencyRules);

    SwapCurrencyRules getStandardSwapCurrencyRules();

    void setStandardSwapCurrencyRules(
            SwapCurrencyRules standardSwapCurrencyRules);

    void removeStandardSwapCurrencyRules(SwapCurrencyRules standardSwapCurrencyRules);

    SwapCurrencyRules getCustomSwapCurrencyRules();

    void setCustomSwapCurrencyRules(
            SwapCurrencyRules customSwapCurrencyRules);

    void removeCustomSwapCurrencyRules(SwapCurrencyRules customSwapCurrencyRules);
}
