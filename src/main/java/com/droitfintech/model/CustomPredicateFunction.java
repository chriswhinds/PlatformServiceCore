package com.droitfintech.model;

import java.util.HashSet;
import java.util.Set;

import com.droitfintech.model.TradeContext;

/**
 * This interface defines the structure for the "lambda" inside a CustomPredicate.
 * The Guava Function was not used, since it only takes a single argument.
 *
 * LambdaJ is another possibility, but may be overkill for our limited purposes.
 *
 * Java native Lambdas might be a good fit, if we ever move to Java 8.
 *
 * @author jisoo
 *
 */
public interface CustomPredicateFunction {
    boolean apply(CustomPredicate predicate, TradeContext context);

    /**
     * Because custom predicates may refer to some arbitrary set of trade attributes,
     * we must return the dependencies through the custom function that has knowledge of
     * which attributes it uses in apply()
     *
     * @param predicate
     * @return The set of attribute names referenced in this custom predicate function
     */
    Set<String> getAttributeSignature(CustomPredicate predicate) ;
}
