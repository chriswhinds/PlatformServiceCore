package com.droitfintech.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.TradeAttribute.BypassRuleException;
import com.droitfintech.model.TradeContext;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;

@Entity
@DiscriminatorValue("MEMBER_OF")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="predicate")
@XmlType(propOrder={"tradeAttribute", "ruleType", "comparisonDataType", "children", "comparisonValuesAsSetXML"})
public class MemberOf extends AbstractComparisonPredicate {

    public MemberOf() {
        super();
    }

    public MemberOf(String tradeAttribute,
                    String comparisonValues,
                    AttributeType comparisonDataType) {
        super(tradeAttribute, comparisonValues, comparisonDataType) ;
    }

    /**
     * MemberOf does not quite work like most other operators, so we override apply().
     */
    @Override
    public boolean apply(TradeContext inputTrade) {
        try {
            Set<?> compValues = getComparisonValuesAsSet();
            TradeAttribute attr = inputTrade.getAttribute(getTradeAttribute());
            if (attr.isCollection()) {
                for (Object attrVal: attr.getValueAsCollection()) {
                    if (!compValues.contains(attrVal)) {
                        return false;
                    }
                }
            } else {
                if (!compValues.contains(attr.getValue())) {
                    return false;
                }
            }
            return true;
        } catch (UnmetDependencyException e) {
            throw new BypassRuleException("Recommended that we bypass this rule, due to unmet dependency: " + this, e);
        }
    }

}
