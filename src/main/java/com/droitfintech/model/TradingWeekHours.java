package com.droitfintech.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.TreeSet;

import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.ModelConversionUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

@XmlRootElement(namespace="http://www.droitfintech.com")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TradingWeekHours {

    private TreeSet<TradingDayHours> tradingDayHours;

    private String singleBC;

    private String startTimeBC;

    private String endTimeBC;

    public TreeSet<TradingDayHours> getTradingDayHours() {
        return tradingDayHours;
    }

    public void setTradingDayHours(TreeSet<TradingDayHours> tradingDayHours) {
        this.tradingDayHours = tradingDayHours;
    }

    public String getSingleBC() {
        return singleBC;
    }

    public void setSingleBC(String singleBC) {
        this.singleBC = singleBC;
    }

    public String getStartTimeBC() {
        if (startTimeBC == null && endTimeBC != null) {
            return endTimeBC;
        }
        return startTimeBC;
    }

    public void setStartTimeBC(String startTimeBC) {
        this.startTimeBC = startTimeBC;
    }

    public String getEndTimeBC() {
        if (endTimeBC == null && startTimeBC != null) {
            return startTimeBC;
        }
        return endTimeBC;
    }

    public void setEndTimeBC(String endTimeBC) {
        this.endTimeBC = endTimeBC;
    }

    @Override
    public String toString() {
        String hours = Joiner.on(", ").join(tradingDayHours);
        String startBC = startTimeBC==null?"":startTimeBC;
        try {
            String bc = singleBC==null? Joiner.on(", ").join(getStartTimeBC(), getEndTimeBC()): singleBC;
            return Joiner.on(" :: ").join(hours, bc);
        } catch (NullPointerException e) {
            throw new DroitException("Something wrong in TradingWeekHours.toString()", e);
        }
    }

    public TradingWeekHours(String strRepr) {

        List<String> weekParts = Splitter.on("::").splitToList(strRepr);

        tradingDayHours = ModelConversionUtil.getTradingDayHoursSet(weekParts.get(0));

        List<String> calendars = Splitter.on(",").trimResults().splitToList(weekParts.get(1));
        if (calendars.size() > 1) {
            startTimeBC = calendars.get(0);
            endTimeBC = calendars.get(1);
        } else {
            singleBC = calendars.get(0);
        }
    }

    public TradingWeekHours() {
        ;
    }



}
