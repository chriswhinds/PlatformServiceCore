package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;


import com.google.common.base.Joiner;

@XmlRootElement(namespace="http://www.droitfintech.com")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TradingWeekHoursSet implements Comparable<TradingWeekHoursSet>{

    private TradingWeekHours singleBcTradingHours;

    private TradingWeekHours multipleBcTradingHours;

    public TradingWeekHours getSingleBcTradingHours() {
        return singleBcTradingHours;
    }

    public void setSingleBcTradingHours(TradingWeekHours singleBcTradingHours) {
        this.singleBcTradingHours = singleBcTradingHours;
    }

    public TradingWeekHours getMultipleBcTradingHours() {
        return multipleBcTradingHours;
    }

    public void setMultipleBcTradingHours(TradingWeekHours multipleBcTradingHours) {
        this.multipleBcTradingHours = multipleBcTradingHours;
    }

    @Override
    public String toString() {
        String single = "singleBc::: " + singleBcTradingHours.toString();
        String multiple = "multipleBc::: " + multipleBcTradingHours.toString();
        return Joiner.on("; ").join(single, multiple);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        TradingWeekHoursSet rhs = (TradingWeekHoursSet) obj;
        return new EqualsBuilder()
                .append(this.toString(), rhs.toString())
                .isEquals();
    }


    public int compareTo(TradingWeekHoursSet rhs) {
        return new CompareToBuilder()
                .append(this.toString(), rhs.toString())
                .toComparison();
    }

}
