package com.droitfintech.dao;

import java.math.BigDecimal;
import java.util.Set;

import com.droitfintech.regulatory.Tenor;
import com.droitfintech.model.ProductMaster;

public interface DefaultsInterestRateInterface {

    public abstract ProductMaster getProductMaster();

    public abstract String getCurrency();

    public abstract String getFloatIndex();

    public abstract Tenor getIndexTenorAsTenor();

    public abstract Tenor getFloatPayFrequencyAsTenor();

    public abstract Tenor getFixedPayFrequencyAsTenor();

    public abstract String getFloatDayCount();

    public abstract String getFixedDayCount();

    public abstract String getBusinessDayConvention();

    public abstract Set<String> getHolidayCalendarsAsSet();

    public abstract Tenor getMaturityAsTenor();

    public abstract BigDecimal getNotional();

    public abstract BigDecimal getFixedCoupon();

}