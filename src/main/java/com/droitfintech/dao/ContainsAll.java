package com.droitfintech.dao;

import com.droitfintech.model.AbstractComparisonPredicate;
import com.droitfintech.model.TradeAttribute;
import com.droitfintech.model.TradeContext;

import java.util.Collection;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@Entity
@DiscriminatorValue("CONTAINS_ALL")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="predicate")
@XmlType(propOrder={"tradeAttribute", "ruleType", "comparisonDataType", "children", "comparisonValuesAsSetXML"})
public class ContainsAll extends AbstractComparisonPredicate {

    public ContainsAll(String tradeAttributes, String comparisonValues, TradeAttribute.AttributeType comparisonDataType) {
        super(tradeAttributes, comparisonValues, comparisonDataType) ;
    }

    public ContainsAll() {
        super();
    }

    /**
     * ContainsAll can be optimized using containsAll, so we override.
     */
    @Override
    public boolean apply(TradeContext inputTrade) {
        try {
            Set<?> compValues = getComparisonValuesAsSet();
            TradeAttribute attr = inputTrade.getAttribute(this.getTradeAttribute());
            if (!(attr.getValueAsCollection()).containsAll(compValues)) {
                return false;
            }
            return true;
        } catch (TradeAttribute.UnmetDependencyException e) {
            throw new TradeAttribute.BypassRuleException("Recommended that we bypass this rule, due to unmet dependency: " + this, e);
        }
    }

}
