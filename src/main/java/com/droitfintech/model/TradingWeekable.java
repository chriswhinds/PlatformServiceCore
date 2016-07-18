package com.droitfintech.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.droitfintech.model.TradingWeekHoursSet;

public interface TradingWeekable {

    public TradingWeekHoursSet getTradingWeekHoursSet();

}