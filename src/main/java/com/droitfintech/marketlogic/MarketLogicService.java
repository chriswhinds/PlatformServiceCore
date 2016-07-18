package com.droitfintech.marketlogic;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import com.codahale.metrics.*;
import com.droitfintech.dao.TradeRule;
import com.droitfintech.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;

import com.droitfintech.config.DecisionsConfiguration;
import com.droitfintech.exceptions.DroitException;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.droitfintech.utils.BucketedMap;

import com.droitfintech.dao.EligibilityDAO;
import com.droitfintech.model.MarketLogicEligibility;
import com.droitfintech.model.FinMktInfraVersion.FinMktInfraVersionType;
import com.droitfintech.exceptions.DroitException;

/**
 *
 * The service that performs the eligibility, listed products, and MAT tests
 *
 *
 * Migrated from OldBox core functionality
 * @author jisoo
 *
 */
public class MarketLogicService {

    private static Logger log = LoggerFactory.getLogger(MarketLogicService.class);
    private static Logger performanceLog = LoggerFactory.getLogger(MarketLogicService.class.getCanonicalName() + ".performance");
    private final boolean isQuickStartup = false;

    private boolean initialized = false;
    private Map<FinMktInfraVersionType, Map<String, MultiMap<TradePredicate, TradeRule>>> ruleCache = new HashMap<FinMktInfraVersionType, Map<String, MultiMap<TradePredicate, TradeRule>>>();

    private AtomicInteger skippedRulesBecauseOfDate = new AtomicInteger(0);
    private AtomicInteger totalNumberOfRules = new AtomicInteger(0);


    private DecisionsConfiguration decisionsConfiguration;

    // Dependencies

    public EligibilityDAO getEligibilityDao() {
        return dao;
    }

    public void setEligibilityDao(EligibilityDAO dao) {
        this.dao = dao;
    }

    private EligibilityDAO dao;
    private MetricRegistry _metricRegistry;

    public MarketLogicService() {
       // this.isQuickStartup = System.getProperties().getProperty("com.droitfintech.marketlogic.quickStartup") != null;
    }

    /**
     * Main Entry Point
     * @param payload
     * @param decisionType
     * @return
     */
    public BucketedMap evaluateMarketLogic(BucketedMap payload, FinMktInfraVersionType decisionType) {
        return evaluateMarketLogic(payload, decisionType, null);
    }

    /**
     * Initalize the MarketLogic Service
     */
    public void initialize() {

        this.retrieveRulesCacheFromDB(false);
    }

    /**
     * File the cache From the DB
     * @param forceReset
     */
    public void retrieveRulesCacheFromDB(boolean forceReset) {
        performanceLog.trace("Market Logic Performance Logging Enabled");
        if (this.initialized && !forceReset) {
            log.warn("Skipping rule cache since it has already been initialized.");
            return;
        }
        log.info("Rules cache population START");
        // For each type...
        for (FinMktInfraVersionType type : FinMktInfraVersionType.values()) {
            if (this.isQuickStartup && !(type.equals(FinMktInfraVersionType.CCPMembership))) {
                log.warn("Quick start is on so we're not loading any Market Logic rules for '{}'",type.name());
                continue;
            }
            try {
                Collection<? extends FinMktInfra> venues = dao.getVenuesByType(type);
                //For each venue....
                for (FinMktInfra venue : venues) {
//					if (!isIncludedVenue(venue))	{
//						log.info("Not loading rules for venue '{}'; it's marked as excluded", venue.getShortName());
//						continue;
//					}
                    log.debug("Loading {} for venue {}", type, venue);
                    Collection<TradeRule> rules = dao.getAllRulesByVenueAndType(venue.getShortName(), type);
                    // For each rule
                    for (TradeRule tradeRule : rules) {
                        addRuleToCache(type, venue.getShortName(), tradeRule);
                    }
                }
            } catch (DroitException e) {
                log.debug("Not yet handling cases where some types don't have associated venues.  Type: {}",
                        type.name());
                log.trace("Stack trace for above message", e);
            }
        }
        this.initialized = true;
        log.info("Rules cache population END.  Total number of rules: {}", totalNumberOfRules);
        log.debug("Skipped caching {} rules due to date check", skippedRulesBecauseOfDate);
    }

    /**
     *
     * @param finMktInfraType
     * @param venueShortName
     * @return
     */
    private boolean isIncludedVenue(String finMktInfraType, String venueShortName) {
        boolean answer = true;
        Set<String> includedSefs = decisionsConfiguration.getIncludedSefs();
        Set<String> includedCcps = decisionsConfiguration.getIncludedCcps();
        if (finMktInfraType.equals("SEF")) {
            answer = (includedSefs.isEmpty()) || includedSefs.contains(venueShortName);
        } else if (finMktInfraType.equals("CCP")) {
            answer = (includedCcps.isEmpty()) || includedCcps.contains(venueShortName);
        }
        return answer;
    }

    /**
     *
     * @param type
     * @param venueShortName
     * @param rule
     */
    private void addRuleToCache(FinMktInfraVersionType type, String venueShortName, TradeRule rule) {
        this.totalNumberOfRules.incrementAndGet();
        DateTime targetDate = (new DateTime()).minusDays(7);
        DateTime endDate = new DateTime(rule.getEffectiveEnd());
        if (targetDate.isAfter(endDate)) {
            this.skippedRulesBecauseOfDate.incrementAndGet();
            return;
        }
        Map<String, MultiMap<TradePredicate, TradeRule>> mapByVenue = ruleCache.get(type);
        if (mapByVenue == null) {
            mapByVenue = new HashMap<String, MultiMap<TradePredicate, TradeRule>>();
            ruleCache.put(type, mapByVenue);
        }
        MultiMap<TradePredicate, TradeRule> mapByTradeKey = mapByVenue.get(venueShortName);
        if (mapByTradeKey == null) {
            mapByTradeKey = new MultiValueMap<TradePredicate, TradeRule>();
            mapByVenue.put(venueShortName, mapByTradeKey);
        }
        mapByTradeKey.put(rule.getTradeKey(), rule);
    }

    /**
     *
     * @param payload
     * @param decisionType
     * @param optionalAsOfDate
     * @return
     */
    public BucketedMap evaluateMarketLogic(BucketedMap payload, FinMktInfraVersionType decisionType, Date optionalAsOfDate) {
        DroitException.assertThat(payload != null && decisionType != null,"evaluateMarketLogic(): payload and decisionType must not be null");
        Map<String, Object> tradeBucket = payload.getAllValuesForBucket("trade");
        DroitException.assertThat(tradeBucket != null && tradeBucket instanceof TradeContext,"MarketLogicService expects a 'trade' bucket that contains a TradeContext");
        TradeContext trade = (TradeContext) tradeBucket;
        Collection<VenueResults> results = evaluateMarketLogicForAllVenues(decisionType, trade, optionalAsOfDate);
        return packageResults(decisionType, payload, results);

    }

    /**
     *
     * @param decisionType
     * @param payload
     * @param results
     * @return
     */
    private BucketedMap packageResults(FinMktInfraVersionType decisionType, BucketedMap payload, Collection<VenueResults> results) {
        if (decisionType.equals(FinMktInfraVersionType.MAT)) {
            for (VenueResults r : results) {
                payload.setValue(r.getVenueShortName().toLowerCase() + "_mat", "isMAT", r.isEligible());
                payload.setValue(r.getVenueShortName().toLowerCase() + "_mat", "matEffectiveDate", r.getLatestMatchingRuleDate());
            }
        } else if (decisionType.equals(FinMktInfraVersionType.CCPEligibility)) {
            List<String> eligibleShortNames = new LinkedList<String>();
            List<MarketLogicEligibility> ccpEligibility = new LinkedList<MarketLogicEligibility>();
            for (VenueResults venue : results) {
                if (venue.isEligible()) {
                    eligibleShortNames.add(venue.getVenueShortName());
                }
                if (isIncludedVenue("CCP", venue.getVenueShortName())) {
                    ccpEligibility.add(new MarketLogicEligibility(venue));
                }
            }
            payload.setValue("ccp_eligibility", "shortNameList", eligibleShortNames);
            payload.setValue("ccp_eligibility", "fullList", ccpEligibility);
        } else if (decisionType.equals(FinMktInfraVersionType.ListedProduct)) {
            boolean sefListed = false;
            boolean sefOpen = false;
            List<MarketLogicEligibility> sefs = new LinkedList<MarketLogicEligibility>();
            for (VenueResults r : results) {
                sefListed = sefListed || r.isEligible();
                if (isIncludedVenue("SEF", r.getVenueShortName())) {
                    sefs.add(new MarketLogicEligibility(r));
                }
                sefOpen = sefOpen || (r.isEligible() && r.isVenueOpen());
            }
            payload.setValue("sef_eligibility", "isEligible", sefListed);
            payload.setValue("sef_eligibility", "isOpen", sefOpen);
            payload.setValue("sef_eligibility", "fullList", sefs);
        } else {
            throw new DroitException("MarketLogicService is not equipped to handle " + decisionType + " right now.");
        }
        payload.setValue("metadata", "marketLogicVersionId", dao.getMarketLogicVersion());
        return payload;
    }

    /**
     *
     * @param decisionType
     * @param trade
     * @param optionalAsOfDate
     * @return
     */
    public Collection<VenueResults> evaluateMarketLogicForAllVenues(FinMktInfraVersionType decisionType,
                                                                    TradeContext trade, Date optionalAsOfDate) {
        Collection<VenueResults> res = new LinkedList<VenueResults>();
        Map<String, MultiMap<TradePredicate, TradeRule>> rulesPerVenue = ruleCache.get(decisionType);
        if (rulesPerVenue == null) {
            if (this.isQuickStartup) {
                log.warn("In quickStartup mode - Not running decision type '{}'", decisionType);
                return res;
            }
            // there are no rules for this decision type. if we have code attempting to call it for this decision type,
            // we should raise it as a serious issue.
            throw new DroitException("Code attempted to call MarketLogicService for a type " + decisionType +
                    ", which has no associated rules.");
        }

        com.codahale.metrics.Timer.Context eachMarketLogicCtx = null;

        for (Map.Entry<String, MultiMap<TradePredicate, TradeRule>> entry : rulesPerVenue.entrySet()) {

            String venue = entry.getKey();

            if (this.decisionsConfiguration.isGranularDecisionMetricsEnabled()) {
                eachMarketLogicCtx = getMetricRegistry().timer(MetricRegistry.name("MarketLogic",
                        decisionType.name(), venue)).time();
            }

            try {
                MultiMap<TradePredicate, TradeRule> rules = entry.getValue();

                log.debug("Evaluating Market Logic for decision type'{}', venue'{}'", decisionType.name(), venue);
                VenueResults results = evaluateMarketLogicForVenue(venue, rules, trade, optionalAsOfDate);
                res.add(results);
            } finally {
                if (eachMarketLogicCtx != null) {
                    eachMarketLogicCtx.stop();
                    eachMarketLogicCtx = null;
                }
            }
        }

        return res;
    }

    // Nov 10 2015 - Roy - it seems we're calling this only from a test but it looks pretty material.  Should it
    // stick around?
    public VenueResults evaluateMarketLogicForVenue(FinMktInfraVersionType decisionType, String venue,
                                                    TradeContext trade, Date optionalAsOfDate) {

        VenueResults answer = null;

        Map<String, MultiMap<TradePredicate, TradeRule>> rulesPerVenue = this.getAllRulesForDecisionType(decisionType);

        if (rulesPerVenue == null) {
            // there are no rules for this decision type. if we have code attempting to call it for this decision type,
            // we should raise it as a serious issue.
            throw new DroitException("Code attempted to call MarketLogicService for a type " + decisionType +
                    ", which has no associated rules.");
        }

        MultiMap<TradePredicate, TradeRule> rulesForVenue = rulesPerVenue.get(venue);

        if (rulesForVenue == null) {
            throw new DroitException(String.format("No rules found for venue '%s', decision type '%s'.  Are you sure this is a valid venue?", venue, decisionType.toString()));
        }

        answer = evaluateMarketLogicForVenue(venue, rulesForVenue, trade, optionalAsOfDate);

        return answer;
    }

    // Adds some safety around getting all rules for given decision type.
    private Map<String, MultiMap<TradePredicate, TradeRule>> getAllRulesForDecisionType(FinMktInfraVersionType decisionType) {

        Map<String, MultiMap<TradePredicate, TradeRule>> allRules = ruleCache.get(decisionType);

        if (allRules == null) {
            // there are no rules for this decision type. if we have code attempting to call it for this decision type,
            // we should raise it as a serious issue.
            throw new DroitException("Code attempted to call MarketLogicService for a type " + decisionType +
                    ", which has no associated rules.");
        }

        return allRules;
    }


    public VenueResults evaluateMarketLogicForVenue(String venueShortName, MultiMap<TradePredicate, TradeRule> allRules,
                                                    TradeContext trade, Date optionalAsOfDate) {

        DroitException.assertNotNull(allRules, "No rules found for this combination");

        VenueResults res = new VenueResults(venueShortName);
        Map<TestPredicatePrecedenceSimilarityKey, TradeRule> precedenceHash = new HashMap<TestPredicatePrecedenceSimilarityKey, TradeRule>();

        Date asOfDate = optionalAsOfDate;
        if (asOfDate == null) {
            asOfDate = (Date) trade.get("submissiondate");
            if (asOfDate == null) {
                throw new DroitException("submissiondate must not be null");
            }
        }

        boolean atLeastOneRuleMatchedKeys = false;
        Set<TradePredicate> keyPredicates = allRules.keySet();

        for (TradePredicate keyPredicate : keyPredicates) {

            boolean ruleApplies = doesRuleApply(trade, keyPredicate);

            if (ruleApplies) {
                Collection<TradeRule> allRulesForKey = (Collection<TradeRule>) allRules.get(keyPredicate);

                for (TradeRule rule : allRulesForKey) {

                    if (log.isTraceEnabled()) {
                        TradePredicate tradeTest = rule.getTradeTest();
                        String predicateString = StringUtils.replace(tradeTest.toString(), "\n", "");
                        log.trace("   Attempting to test trade against:" + predicateString);
                    }

                    if (rule.isEffectiveFor(asOfDate)) {
                        atLeastOneRuleMatchedKeys |= evaluateSingleRule(trade,
                                res, precedenceHash, rule);
                    } else {
                        if (log.isTraceEnabled()) {
                            log.trace("      Skipping rule because it's not current effective.  Is effective between "
                                    + rule.getEffectiveStart() + " and " + rule.getEffectiveEnd());
                        }
                    }
                }
            } else {
                if (performanceLog.isTraceEnabled()) {
                    Collection<TradeRule> allRulesForKey = (Collection<TradeRule>) allRules.get(keyPredicate);
                }
            }
        }

        if (!atLeastOneRuleMatchedKeys) {
            log.trace("No keys match at all, so we are failing with a blanket 'Product Listing' check");
            res.addFailedRule(new TradeRule(new AlwaysTrue(), new CustomPredicate("product", null, TradeAttribute.AttributeType.StringType, "BasicProductListingCheck"),
                    null, new Date(), null, null, null));
        }

        if (log.isTraceEnabled()) {

            if (res.isEligible()) {
                log.trace("   Full trade matches!");
            } else {
                log.trace("   Trade failed with " + res.getFailedRules().size() +
                        " failed tests, " + res.getUnmetRules().size() + " Unmet tests");
            }
        }

//		performanceLog.trace("venue: {}, date: {}, total key predicates: {}, total key predicdates that applied: {}, total rules evaluated: {}, total rules not evaled because of date: {}, total rules not evals because of key: {}", venueShortName, optionalAsOfDate, totalKeyPredicates, totalKeyPredicatesApplies, totalRulesActuallyEvaluated, totalRulesNotEvaluatedBecauseOfDate, totalRulesNotEvaluatedBecauseOfKey);

        return res;
    }

    private boolean evaluateSingleRule(
            TradeContext trade,
            VenueResults res,
            Map<TestPredicatePrecedenceSimilarityKey, TradeRule> precedenceHash,
            TradeRule rule) {
        res.setLatestMatchingRuleDate(rule.getEffectiveStart());
        TradePredicate testPredicate = rule.getTradeTest();
		/*
		 * Look into the previously fired rules for anything we should be
		 * superseding with the current rule.
		 */
        TradeRule priorRule = precedenceHash.get(new TestPredicatePrecedenceSimilarityKey(testPredicate));
        if (priorRule != null) {
            if (ruleSupersedesPriorRule(rule, priorRule)) {
                log.trace("      This test supersedes prior test.  Removing the prior test from results");
                res.removeFailedRule(priorRule);
            } else {
                log.trace("      There is a prior rule but this rule does not supersede it.  Running rule.");
            }
        } else if (log.isTraceEnabled()) {
            log.trace("      No prior rule for this test");
        }
        try {
            TradePredicate test = rule.getTradeTest();

            if (test instanceof AbstractComparisonPredicate) {
                AbstractComparisonPredicate acp = (AbstractComparisonPredicate) test;
                // Scrape the clearing choices
                if ("clearingchoices".equals(acp.getTradeAttribute())) {
                    res.setClearingChoices(acp.getComparisonValues());
                }

                // Scrape the execution styles
                if ("executionstyle".equals(acp.getTradeAttribute())) {
                    res.setExecutionStyles(acp.getComparisonValues());
                }
            }

            if (test instanceof CustomPredicate
                    && ((CustomPredicate) test).getCustomPredicateFunction() instanceof TradingHoursCheckFunction) {

                // If the test is the trading hours test, we report the result outside of the overall eligibility decision, and scrape the trading hours into the result
                TradingWeekHoursSet tradingHoursSet = (TradingWeekHoursSet) (((CustomPredicate) test).getComparisonValuesAsSet().toArray()[0]);
                TradingWeekHours tradingHours = tradingHoursSet.getMultipleBcTradingHours();
                res.setStartTimeBC(tradingHours.getStartTimeBC());
                res.setEndTimeBC(tradingHours.getEndTimeBC());
                res.setTradingHours(tradingHours.getTradingDayHours());
                // Apply the actual rule
                boolean isSefOpen = test.apply(trade);
                res.setVenueOpen(isSefOpen);
                log.trace("      SEF Trading hours check: {}", isSefOpen);
                return true;
            } else {

                // All non-TradingHours rules get fired and reported as a part of the eligibility decision
                if (!rule.getTradeTest().apply(trade)) {

                    log.trace("      NO - Test does not match. RULE FAILED");

                    res.addFailedRule(rule);
                    precedenceHash.put(new TestPredicatePrecedenceSimilarityKey(testPredicate), rule);
                } else {
                    log.trace("      YES - Test does match");
                }
                return true;
            }
        } catch (TradeAttribute.BypassRuleException e) {
			/*
			 * For a variety of reasons, the test may throw a BypassRuleException, which indicates that the rule
			 * should be skipped. Usually, it is the case that the referenced attribute is throwing
			 * an UnmetDependencyException; we assume then that the test is not applicable.
			 */
            if (log.isTraceEnabled()) {
                log.trace("      Bypassing test due to : " + e.getMessage());
            }
            res.addUnmetRule(rule);
            return false;
        } catch (Exception e) {
            //throw new DroitException("Failed evaluating value predicate of rule.", e);
            // Note that we are getting errors here that need to be revisited.  Just logging at debug level
            // now as to not freak customers out.
            log.debug("Failed evaluating value predicate of rule. Run trace level for full stacktrace.  Message:{}", e.getMessage());
            log.trace("Failed evaluating value predicate of rule.", e);
            res.addUnmetRule(rule);
            return false;
        }
    }

    private boolean doesRuleApply(TradeContext trade,
                                  TradePredicate keyPredicate) {
        boolean ruleApplies = false;

        try {

            if (log.isTraceEnabled()) {
                String predicateString = StringUtils.replace(keyPredicate.toString(), "\n", "");
                log.trace("*** Testing match for key: " + predicateString);
            }

            ruleApplies = keyPredicate.apply(trade);

            if (log.isTraceEnabled()) {
                if (ruleApplies) {
                    log.trace("   YES - Key matches");
                } else {
                    log.trace("   NO - Key does not match");
                }
            }

        } catch (DroitException e) {
			/*
			 * We ignore exceptions caused by UnmetDependencyExceptions, since the key fields
			 * for one product may not exist in another products. Other types of exceptions
			 * should be re-thrown.
			 */
            if (!(e.getCause() instanceof TradeAttribute.UnmetDependencyException)) {
                throw e;
            } else {
                if (log.isTraceEnabled()) {
                    TradeAttribute.UnmetDependencyException ude = (TradeAttribute.UnmetDependencyException) e.getCause();
                    log.trace("   Could not evaluate key: " + ude.getMessage());
                }
            }
        }
        return ruleApplies;
    }

    /**
     * A simple precedence resolution method: returns true if the rule has a trade key
     * that is longer (i.e. more specific) than the one in PrecedenceValue
     *
     * @param rule
     * @return true if the rule should supersede/override the one in PrecedenceValue
     */
    private boolean ruleSupersedesPriorRule(TradeRule rule, TradeRule priorRule) {
        Set<String> currentSig = rule.getTradeKey().getAttributeSignature();
        Set<String> priorSig = priorRule.getTradeKey().getAttributeSignature();
        return currentSig.size() > priorSig.size();
    }

    public MetricRegistry getMetricRegistry() {

        // Just in case we don't already have one in the app Context.
        // Ideally this should be using DroitMetricRegistry, but currently that
        // is not available to the Market Logic lib
        if (_metricRegistry == null) {
            synchronized (this) {
                if (_metricRegistry == null) {
                    _metricRegistry = new MetricRegistry();
                }
            }
        }

        return _metricRegistry;
    }

    public void setMetricRegistry(MetricRegistry metricRegistry) {
        this._metricRegistry = metricRegistry;
    }

    public void setDecisionsConfiguration(DecisionsConfiguration decisionsConfiguration) {
        this.decisionsConfiguration = decisionsConfiguration;
    }

    private static class TestPredicatePrecedenceSimilarityKey {
        private Class<?> clazz;
        private String attributeName;
        private String customPredicateName;

        public TestPredicatePrecedenceSimilarityKey(TradePredicate p) {
            this.clazz = p.getClass();

            if (p instanceof AbstractComparisonPredicate) {
                this.attributeName = ((AbstractComparisonPredicate) p).getTradeAttribute();
            } else {
                this.attributeName = "";
            }

            if (p instanceof CustomPredicate) {
                this.customPredicateName = ((CustomPredicate) p).getCustomPredicateName();
            } else {
                this.customPredicateName = "";
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(13, 77).
                    append(clazz).
                    append(attributeName).
                    append(customPredicateName).
                    toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            TestPredicatePrecedenceSimilarityKey rhs = (TestPredicatePrecedenceSimilarityKey) obj;
            return new EqualsBuilder()
                    .append(clazz, rhs.clazz)
                    .append(attributeName, rhs.attributeName)
                    .append(customPredicateName, rhs.customPredicateName)
                    .isEquals();
        }
    }
}




