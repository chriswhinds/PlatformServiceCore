package com.droitfintech.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by roytruelove on 11/10/15.
 */
public class DecisionsConfiguration
{
    private String includedCcpsStr = null;
    private String includedSefsStr = null;

    //Allowing both sets and strings for config because this class might be init from
    //Spring properties (and so strings) or directly.GG
    private Set<String> includedCcps = new HashSet<String>();
    private Set<String> includedSefs= new HashSet<String>();

    // This is a hack.  See MarketLogicServiceImpl, fxEnhancementsFilter method
    private boolean fxEnhancementsEnabled = false;
    private boolean granularDecisionMetricsEnabled = false;

    private static Logger log = LoggerFactory.getLogger(DecisionsConfiguration.class);


    @PostConstruct
    public void initialize() {

        if (StringUtils.isNotBlank(includedCcpsStr)) {
            includedCcps = new HashSet<String>(Arrays.asList(StringUtils.split(includedCcpsStr, ",")));
            log.info("Including only the following CCPs: {}",includedCcpsStr);
        }

        if (StringUtils.isNotBlank(includedSefsStr)) {
            includedSefs = new HashSet<String>(Arrays.asList(StringUtils.split(includedSefsStr, ",")));
            log.info("Including only the following SEFs: {}",includedSefsStr);
        }

        if (fxEnhancementsEnabled) {
            log.info("Running with FX Enhancements enabled");
        }
    }

    public Set<String> getIncludedSefs() {
        return includedSefs;
    }

    public Set<String> getIncludedCcps() {
        return includedCcps;
    }

    public void setIncludedCcpsStr(String includedCcpsStr) {
        this.includedCcpsStr = includedCcpsStr;
    }

    public void setIncludedSefsStr(String includedSefsStr) {
        this.includedSefsStr = includedSefsStr;
    }

    public boolean isFxEnhancementsEnabled() {
        return fxEnhancementsEnabled;
    }

    public void setFxEnhancementsEnabled(boolean fxEnhancementsEnabled) {
        this.fxEnhancementsEnabled = fxEnhancementsEnabled;
    }

    public boolean isGranularDecisionMetricsEnabled() {
        return granularDecisionMetricsEnabled;
    }

    public void setGranularDecisionMetricsEnabled(boolean granularDecisionMetricsEnabled) {
        this.granularDecisionMetricsEnabled = granularDecisionMetricsEnabled;
    }
}
