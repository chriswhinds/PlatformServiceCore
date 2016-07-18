package com.droitfintech.marketlogic;

import com.droitfintech.dao.TradeRule;
import com.droitfintech.model.TradingDayHours;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

public class VenueResults {
    private String venueShortName;
    private boolean isVenueOpen = true;
    private Date latestMatchingRuleDate = null;

    private Collection<TradeRule> failedRules = new LinkedList<TradeRule>();
    private Collection<TradeRule> unmetRules = new LinkedList<TradeRule>();

    private String startTimeBC;
    private String endTimeBC;
    private Set<TradingDayHours> tradingHours;
    private String clearingChoices;
    private String executionStyles;

    public VenueResults(String shortName) {
        setVenueShortName(shortName);
    }

    public void addFailedRule(TradeRule failedRule) {
        this.getFailedRules().add(failedRule);
    }

    public String getVenueShortName() {
        return venueShortName;
    }

    public void setVenueShortName(String venueShortName) {
        this.venueShortName = venueShortName;
    }

    public boolean isEligible() {
        return failedRules.isEmpty();
    }

    public Collection<TradeRule> getFailedRules() {
        return failedRules;
    }

    public void setFailedRules(Collection<TradeRule> failedRules) {
        this.failedRules = failedRules;
    }

    public boolean isVenueOpen() {
        return isVenueOpen;
    }

    public void setVenueOpen(boolean isVenueOpen) {
        this.isVenueOpen = isVenueOpen;
    }

    public void addUnmetRule(TradeRule rule) {
        this.unmetRules.add(rule);
    }

    public Collection<TradeRule> getUnmetRules() {
        return this.unmetRules;
    }

    public boolean removeFailedRule(TradeRule failedRule) {
        return this.failedRules.remove(failedRule);
    }

    public String getStartTimeBC() {
        return startTimeBC;
    }

    public void setStartTimeBC(String startTimeBC) {
        this.startTimeBC = startTimeBC;
    }

    public String getEndTimeBC() {
        return endTimeBC;
    }

    public void setEndTimeBC(String endTimeBC) {
        this.endTimeBC = endTimeBC;
    }

    public Set<TradingDayHours> getTradingHours() {
        return tradingHours;
    }

    public void setTradingHours(Set<TradingDayHours> tradingHours) {
        this.tradingHours = tradingHours;
    }

    public String getClearingChoices() {
        return clearingChoices;
    }

    public void setClearingChoices(String clearingChoices) {
        this.clearingChoices = clearingChoices;
    }

    public String getExecutionStyles() {
        return executionStyles;
    }

    public void setExecutionStyles(String executionStyles) {
        this.executionStyles = executionStyles;
    }

    @Override
    public String toString() {
        return "Venue Results for " + this.venueShortName + " ... eligible: " + this.isEligible() + ", open: " + this.isVenueOpen + ":\n"
                + "Failed: " + this.failedRules + "\n"
                + "Unmet: " + this.unmetRules;
    }

    public Date getLatestMatchingRuleDate() {
        return latestMatchingRuleDate;
    }

    public void setLatestMatchingRuleDate(Date latestMatchingRuleDateIn) {
        // Set latestMatchingRuleDate only if existing val is null, or input is after existing
        if (this.latestMatchingRuleDate == null ||
                (latestMatchingRuleDateIn != null &&
                        latestMatchingRuleDateIn.compareTo(this.latestMatchingRuleDate) > 0)) {
            this.latestMatchingRuleDate = latestMatchingRuleDateIn;
        }
    }
}
