package com.droitfintech.model;

import java.util.Collections;
import java.util.Set;

import java.util.Collections;
import java.util.Set;

import com.droitfintech.model.TradeContext;

public class AlwaysTrueFunction implements CustomPredicateFunction {

    public boolean apply(CustomPredicate predicate, TradeContext context) {
        return true;
    }
    public Set<String> getAttributeSignature(CustomPredicate predicate) {
        return Collections.emptySet();
    }

}
