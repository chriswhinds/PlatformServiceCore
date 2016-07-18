package com.droitfintech.model;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.comparators.NullComparator;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;
import com.droitfintech.model.TradeContext;
import com.droitfintech.model.TradeContext.AttributeNotFoundException;

public class AttributeConsistencyFunction implements CustomPredicateFunction {


    public boolean apply(CustomPredicate predicate, TradeContext context) {

        Set<?> attributeNames = predicate.getComparisonValuesAsSet();
        Set<Object> allAttributeValues = new HashSet<Object>();
        for (Object attrName: attributeNames) {
            Object attrVal = null;
            try {
                attrVal = context.getAttribute((String)attrName).getValue();
                allAttributeValues.add(attrVal);
            } catch (UnmetDependencyException e) {
                ;	// We ignore unmet dependencies here.
            }
        }
        // If all the attributes are equal, we should have one value.
        // Maybe none, if none of the referenced attributes have met their dependencies.
        return allAttributeValues.size() <= 1;
    }


    public Set<String> getAttributeSignature(CustomPredicate predicate) {
        Set<?> attributeNames = predicate.getComparisonValuesAsSet();
        return (Set<String>) (attributeNames==null? Collections.emptySet(): attributeNames);
    }

}
