package com.droitfintech.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.droitfintech.model.TradingDayHours;
import com.droitfintech.model.TradingWeekHours;
import com.droitfintech.model.TradingWeekHoursSet;
import com.droitfintech.model.TradeContext;

public class TradingHoursCheckFunction implements CustomPredicateFunction {


    public boolean apply(CustomPredicate predicate, TradeContext context) {
        Set<?> tradingHoursSet = predicate.getComparisonValuesAsSet();
        TradingWeekHoursSet hoursSet = null;
        for (Object val: tradingHoursSet) {
            hoursSet = (TradingWeekHoursSet) val;
        }
        TradingWeekHours hours = hoursSet.getMultipleBcTradingHours();
        Date submissionDate = (Date)context.get("submissiondate");

        return !TradingDayHours.failsStartEndTime(
                submissionDate, hours.getStartTimeBC(), hours.getEndTimeBC(), hours.getTradingDayHours());
    }


    public Set<String> getAttributeSignature(CustomPredicate predicate) {
        Set<String> res = new HashSet<String>();
        res.add("submissiondate");
        return res;
    }

}
