package com.droitfintech.dao;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.AbstractComparisonPredicate;
import com.droitfintech.model.TradeAttribute;
import org.apache.commons.lang3.ObjectUtils;


@Entity
@DiscriminatorValue("MIN")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="predicate")
@XmlType(propOrder={"tradeAttribute", "ruleType", "comparisonDataType", "children", "comparisonValuesAsSetXML"})
public class Min extends AbstractComparisonPredicate {

    public Min(String tradeAttributes,
               String comparisonValues,
               TradeAttribute.AttributeType comparisonDataType) {
        super(tradeAttributes, comparisonValues, comparisonDataType);
        DroitException.assertThat(Comparable.class.isAssignableFrom(comparisonDataType.clazz),
                "Please make sure that the AttributeType.clazz implements Comparable! " + comparisonDataType);
    }

    public Min() {
        super();
    }

    @Override
    protected boolean testCompare(Object tradeVal, Object testVal) {
        return ObjectUtils.compare((Comparable)tradeVal, (Comparable)testVal) >= 0;
    }

}