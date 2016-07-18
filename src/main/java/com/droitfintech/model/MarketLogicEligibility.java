package com.droitfintech.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.droitfintech.dao.TradeRule;
import com.droitfintech.marketlogic.VenueResults;
import com.droitfintech.model.TradingDayHours;


public class MarketLogicEligibility extends HashMap<String, Object>{

    private static final long serialVersionUID = 1L;

    public MarketLogicEligibility(VenueResults venueResults) {

        this.put("isEligible", venueResults.isEligible());
        this.put("shortName", venueResults.getVenueShortName());

        List<Map<String, Object>> rules = new LinkedList<Map<String, Object>>();

        for (TradeRule ruleObject : venueResults.getFailedRules()) {

            Map<String, Object> ruleMap = new HashMap<String, Object>();

            ruleMap.put("name", ruleObject.getName());
            ruleMap.put("effectiveStartDate", ruleObject.getEffectiveStart());
            ruleMap.put("effectiveEndDate", ruleObject.getEffectiveEnd());
            ruleMap.put("description", ruleObject.getDescription());

            rules.add(ruleMap);
        }

        this.put("rules", rules);

        if (venueResults.getTradingHours()!=null) {
            String tradingHours = "";
            for (TradingDayHours hours : venueResults.getTradingHours()) {
                tradingHours += hours.toString() + " ";
            }
            this.put("startTimeBC", venueResults.getStartTimeBC());
            this.put("endTimeBC", venueResults.getEndTimeBC());
            this.put("tradingHours", tradingHours);
            this.put("clearingChoices", venueResults.getClearingChoices());
            this.put("executionStyles", venueResults.getExecutionStyles());
        }
    }
}
