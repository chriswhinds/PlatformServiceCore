package com.droitfintech.datadictionary.attribute;

/**
 * Created by barry on 10/7/15.
 */
public enum DefaultServiceLookup {
    NONE,
    // FILL_BY_NAME = use the StandardDefaultService which pulls from
    // `./droitHome/marketLogic/tradeAttributes/defaultable.tradeattrs` and
    // ./droitHome/marketLogic/defaults/*
    FILL_BY_NAME,
    // Pulls from explicit value provided by data dictionary
    FILL_WITH_DEFAULT
}
