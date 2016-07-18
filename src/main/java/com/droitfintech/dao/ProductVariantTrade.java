package com.droitfintech.dao;

import com.droitfintech.regulatory.Tenor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;



public interface ProductVariantTrade {

    // I'd rather call this isSpotStarting but drools prop access doesn't like it
    boolean getSpotStarting();
    String getCurrency();
    Tenor getTerm();
    Date getEffectiveDate();
    BigDecimal getFixedLegCoupon();
    Set<Tenor> getFloatLegIndexTenors();
    Set<Tenor> getFixedLegPaymentFrequencies();
    Set<Tenor> getFloatLegPaymentFrequencies();
    Set<String> getPaymentDateBusinessDayConventions();
    Set<String> getBusinessCenters();
    Set<Set<String>> getBusinessCenterCombos();
    String getFixedLegDayCountFraction();
    String getFloatLegDayCountFraction();
    String getFloatLegCompoundingMethod();
    String getRollConvention();
    Integer getForwardStartingDays();

}
