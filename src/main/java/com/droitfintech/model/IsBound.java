package com.droitfintech.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.StandardAttribute;
import com.droitfintech.model.StandardAttributeFunction;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;
import com.droitfintech.model.TradeContext;

public class IsBound implements StandardAttributeFunction {

    private List<String> attrNames;
    private boolean returnValueIfBound = true;
    private AnyOrAll evalType = AnyOrAll.ANY;

    private interface BooleanCollectionEvaluator {
        boolean evaluate(Collection<Boolean> assertions);
    }

    public enum AnyOrAll {

        ANY(new BooleanCollectionEvaluator() {
            public boolean evaluate(Collection<Boolean> assertions) {
                return assertions.contains(true);
            }
        }),

        ALL(new BooleanCollectionEvaluator() {
            public boolean evaluate(Collection<Boolean> assertions) {
                return !assertions.contains(false);
            }
        });

        public final BooleanCollectionEvaluator evalType;

        public boolean evaluate(Collection<Boolean> assertions) {
            return this.evalType.evaluate(assertions);
        }

        private AnyOrAll(BooleanCollectionEvaluator evalType) {
            this.evalType = evalType;
        }
    }


    public Object calculate(StandardAttribute self) throws UnmetDependencyException {
        // We should be able to short-circuit ANY, but it's annoying to do without the
        // ability to pass functions as args. Groovy ...
        Collection<Boolean> results = new LinkedList<Boolean>();
        for (String attrName: attrNames) {
            results.add(isAttributeBound(self.getTradeContext(), attrName));
        }
        return this.evalType.evaluate(results) == returnValueIfBound;
    }

    private boolean isAttributeBound(TradeContext context, String attributeName) {
        try {
            if (context.getAttribute(attributeName).getValue() != null)
                return true;
        }
        catch (UnmetDependencyException e) {
            ;
        }
        return false;
    }

    /**
     *
     * @param attributes	Required the list of attribute names to check for bound values
     * @param returnValueIfBound	Optional string (true/false) indicating the derived value if the binding evaluation is true. Defaults to true.
     * @param anyOrAll	Optional string (ANY/ALL) indicating whether all or any of the specified attributes need to be bound for a binding eval to return true. Defaults to ANY
     */
    public IsBound(List<String> attributes, String returnValueIfBound, String anyOrAll) {
        if (attributes.size() < 1)
            throw new DroitException("The IsBound calculation function takes at least one attribute argument!");

        this.attrNames = attributes;

        if (returnValueIfBound != null) {
            this.returnValueIfBound = Boolean.parseBoolean(returnValueIfBound);
        }

        if (anyOrAll != null) {
            this.evalType = Enum.valueOf(AnyOrAll.class, anyOrAll);
        }
    }

}
