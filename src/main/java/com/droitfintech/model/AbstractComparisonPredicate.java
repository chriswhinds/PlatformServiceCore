package com.droitfintech.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.TradeAttribute.BypassRuleException;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;

import com.droitfintech.model.ValidationException;
import com.droitfintech.model.ValidationResult;
import com.droitfintech.model.Validator;
import com.droitfintech.model.ModelConversionUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@XmlTransient
public abstract class AbstractComparisonPredicate extends TradePredicate {

    protected String tradeAttribute;

    protected String comparisonValues;

    @Column(insertable = false, updatable = false)
    protected String predicateType;

    @Enumerated(EnumType.STRING)
    protected AttributeType comparisonDataType;

    @Transient
    protected TradePredicate originalKey;

    @Transient
    protected Date originalEffectiveStart;

    @Transient
    protected Date originalEffectiveEnd;

    public AbstractComparisonPredicate() {
        super();
    }

    public AbstractComparisonPredicate(String tradeAttribute, String comparisonValues, AttributeType comparisonDataType) {
        this.tradeAttribute = tradeAttribute;
        this.comparisonValues = comparisonValues;
        this.comparisonDataType = comparisonDataType;
    }

    @Transient
    private Set<?> comparisonValuesAsSet;

    @Transient
    private boolean comparisonValuesInitialized = false;

    @JsonIgnore
    @XmlTransient
    public Set<?> getComparisonValuesAsSet() {
        // Account for Hibernate trying to stick uninitialized entities in hashmaps
        if (comparisonDataType == null) {
            return null;
        }
        if (!comparisonValuesInitialized) {
            try {
                comparisonValuesAsSet = ModelConversionUtil.makeSet(
                        comparisonDataType.clazz,
                        getComparisonValues());
                if (getComparisonValues() == null) {
                    comparisonValuesAsSet.add(null);
                }
                comparisonValuesInitialized = true;
            } catch (Exception e) {
                throw new DroitException("Problem encountered when trying to materialize the values for predicate " + this, e);
            }
        }
        return comparisonValuesAsSet;
    }

    @JsonIgnore
    @XmlElement(name="value", nillable=true)
    public Set<?> getComparisonValuesAsSetXML() {
        Class<?> localclass = comparisonDataType.clazz;
        if (localclass.equals(Date.class)) {
            localclass = XMLGregorianCalendar.class;
        }
        comparisonValuesAsSet = ModelConversionUtil.makeSet(localclass, getComparisonValues());
        if (getComparisonValues() == null) {
            comparisonValuesAsSet.add(null);
        }
        return comparisonValuesAsSet;
    }

    @JsonIgnore
    @XmlAttribute(name="type")
    public String getRuleType() {
        return this.getClass().getSimpleName();
    }

    @XmlAttribute
    public String getTradeAttribute() {
        return tradeAttribute;
    }

    public void setTradeAttribute(String tradeAttribute) {
        this.tradeAttribute = tradeAttribute;
    }

    @XmlTransient
    public String getComparisonValues() {
        return comparisonValues;
    }

    public void setComparisonValues(String comparisonValues) {
        this.comparisonValues = comparisonValues;
        comparisonValuesInitialized = false;
    }

    @XmlAttribute
    public AttributeType getComparisonDataType() {
        return comparisonDataType;
    }

    public void setComparisonDataType(AttributeType comparisonDataType) {
        this.comparisonDataType = comparisonDataType;
    }

    /**
     * Default implementation of apply. The operator is performed between the
     * trade attribute and each of the comparison values; returns the conjuction of all
     * comparisons.
     */

    public boolean apply(TradeContext inputTrade) {
        try {
            Set<?> compValues = (Set<?>) getComparisonValuesAsSet();
            Object attrVal =
                    inputTrade.getAttribute(getTradeAttribute()).getValue();
            Collection<Object> attrVals = null;
            if (!(attrVal instanceof Collection)) {
                attrVals = new LinkedList<Object>();
                attrVals.add(attrVal);
            } else {
                attrVals = (Collection<Object>)attrVal;
            }
            for (Object val: compValues) {
                for (Object attributeVal: attrVals)
                    if (!testCompare(attributeVal, val)) {
                        return false;
                    }
            }
            return true;
        } catch (UnmetDependencyException e) {
            throw new BypassRuleException("Recommended that we bypass this rule, due to unmet dependency: " + this, e);
        }
    }

    /**
     * This method is called by the default implementation of apply(). Subclasses must override this
     * method if they are using the default apply() implementation.
     *
     * @param tradeVal
     * @param testVal
     * @return
     */
    protected boolean testCompare(Object tradeVal, Object testVal) {
        throw new DroitException("You need to override the default apply() or testCompare() implementation!");
    }

    @Override
    public String toString() {
        String attr = this.tradeAttribute != null? this.tradeAttribute: "(none)";
        String values = "(none)";
        if (this.comparisonValues != null) {
            if (this.comparisonValues.length() > 200) {
                values = this.comparisonValues.substring(0, 200) + "...";
            } else {
                values = this.comparisonValues;
            }
        }
        values = "["+values+"]";

        // Roy removing the prefixed carriage return as I can't see a reason for it,
        // put back if there's something I'm missing.  Took it out becaues it makes logging
        // difficult
        //return String.format("\n%s %s %s", attr, this.getClass().getSimpleName(), values);
        return String.format("%s %s %s", attr, this.getClass().getSimpleName(), values);
    }

    @JsonIgnore
    public String getPredicateType() {
        return predicateType;
    }


    public TradePredicate copy() {
        try {
            TradePredicate res = this.getClass().getConstructor(String.class, String.class, AttributeType.class)
                    .newInstance(this.tradeAttribute, this.comparisonValues, this.comparisonDataType);
            for (TradePredicate child : this.getChildren()) {
                res.addChild(child.copy());
            }
            AbstractComparisonPredicate acp = (AbstractComparisonPredicate) res;
            acp.setOriginalKey(this.originalKey);
            acp.setOriginalEffectiveStart(this.originalEffectiveStart);
            acp.setOriginalEffectiveEnd(this.originalEffectiveEnd);
            return res;
        } catch ( Exception e){
            throw new DroitException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AbstractComparisonPredicate rhs = (AbstractComparisonPredicate) obj;
        return new EqualsBuilder()
                .append(tradeAttribute, rhs.tradeAttribute)
                .append(comparisonDataType, rhs.comparisonDataType)
                .append(getComparisonValuesAsSet(), rhs.getComparisonValuesAsSet())
                .isEquals();
    }

    private static enum KeyOrder {
        product, currency, other;
        public static KeyOrder get(String s) {
            try {
                return KeyOrder.valueOf(s);
            }
            catch (Exception e) {
                return KeyOrder.other;
            }
        }
    }

    @Override
    public int compareTo(TradePredicate rhs) {
        if (!(rhs instanceof AbstractComparisonPredicate)) {
            return super.compareTo(rhs);
        }

        // By this point we are pretty sure this is another AbstractComparisonPredicate
        AbstractComparisonPredicate acp = (AbstractComparisonPredicate)rhs;
        return new CompareToBuilder()
                .append(KeyOrder.get(tradeAttribute), KeyOrder.get(acp.tradeAttribute))
                .append(tradeAttribute, acp.tradeAttribute)
                .append(comparisonDataType, acp.comparisonDataType)
                .append(getClass().getName(), rhs.getClass().getName())
                .append(getComparisonValuesAsSet().toArray(), acp.getComparisonValuesAsSet().toArray())
                .toComparison();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 39)
                .append(getClass())
                .append(tradeAttribute)
                .append(getComparisonValuesAsSet())
                .toHashCode();
    }

    public Set<String> getAttributeSignature() {
        Set<String> res = new HashSet<String>();
        res.add(this.tradeAttribute);
        return res;
    }

    @XmlTransient
    public TradePredicate getOriginalKey() {
        return originalKey;
    }

    public void setOriginalKey(TradePredicate originalKey) {
        this.originalKey = originalKey;
    }

    @Override
    public ValidationResult validate() {
        ValidationResult res = new ValidationResult();
        Collection<?> values = null;
        try {
            values = this.getComparisonValuesAsSet();
        } catch (Exception e) {
            res.isValid = false;
            res.addException(new ValidationException("", e));
            return res;
        }
        for (Object val: values) {
            try {
                Validator.validate(this.comparisonDataType, this.tradeAttribute ,val);
            } catch (ValidationException e) {
                res.isValid = false;
                res.addException(e);
            }
        }
        return res;
    }

    @JsonIgnore
    public Set<Object> getEnumeration() {
        return Validator.enumerate(this.comparisonDataType);
    }

    @XmlTransient
    public Date getOriginalEffectiveStart() {
        return originalEffectiveStart;
    }

    public void setOriginalEffectiveStart(Date originalEffectiveDateIn) {
        this.originalEffectiveStart = originalEffectiveDateIn;
    }

    @XmlTransient
    public Date getOriginalEffectiveEnd() {
        return originalEffectiveEnd;
    }

    public void setOriginalEffectiveEnd(Date originalEffectiveEndIn) {
        this.originalEffectiveEnd = originalEffectiveEndIn;
    }

}