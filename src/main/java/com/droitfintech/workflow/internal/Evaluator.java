package com.droitfintech.workflow.internal;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Decision.java (C) 2014 Droit Financial Technologies, LLC
 * 
 * @author nathanbrei
 * 
 *         Decision holds all (memoized) workflow results for a single trade
 *         request. It serves as the gateway for all data accessed from inside
 *         the workflows, including trade and cpty information.
 */

public interface Evaluator {
	Map<String, Object> get(String moduleName);
    BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency);
	String getLastCollectionAccessed();  // name of last parameter to get for better error reporting.
}
