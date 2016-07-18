package com.droitfintech.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeContext;
import com.droitfintech.model.ValidationException;
import com.droitfintech.model.Validator;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.ValidationResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;

@Entity
@DiscriminatorValue("CUSTOM")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="predicate")
@XmlType(propOrder={"tradeAttribute", "ruleType", "customPredicateName", "comparisonDataType", "children", "comparisonValuesAsSetXML"})
public class CustomPredicate extends AbstractComparisonPredicate {

    private String customPredicateName;

    protected CustomPredicate() {
        super();
    }

    public CustomPredicate(String tradeAttribute, String comparisonValues,
                           AttributeType comparisonDataType, String customPredicateName) {
        this(tradeAttribute, comparisonValues, comparisonDataType);
        this.customPredicateName = customPredicateName;
    }

    protected CustomPredicate(String tradeAttribute, String comparisonValues, AttributeType comparisonDataType) {
        super(tradeAttribute, comparisonValues, comparisonDataType);
    }

    @Override
    public boolean apply(TradeContext context) {
        DroitException.assertThat(this.getCustomPredicateFunction()!=null,
                "We cannot call apply() on a null CustomPredicateFunction: " + this.toString());
        return this.getCustomPredicateFunction().apply(this, context);
    }

    @XmlAttribute(name="customRuleName")
    public String getCustomPredicateName() {
        return customPredicateName;
    }

    public void setCustomPredicateName(String customPredicateName) {
        this.customPredicateName = customPredicateName;
    }

    @Override
    public Set<String> getAttributeSignature() {
        if (this.getCustomPredicateFunction() == null) {
            return Collections.emptySet();
        }
        return this.getCustomPredicateFunction().getAttributeSignature(this);
    }

    @Override
    public TradePredicate copy() {
        try {
            TradePredicate res = this.getClass()
                    .getConstructor(String.class, String.class, AttributeType.class, String.class)
                    .newInstance(this.tradeAttribute, this.comparisonValues, this.comparisonDataType, this.customPredicateName);
            for (TradePredicate child: this.getChildren()) {
                res.addChild(child.copy());
            }
            AbstractComparisonPredicate acp = (AbstractComparisonPredicate) res;
            acp.setOriginalKey(this.originalKey);
            acp.setOriginalEffectiveStart(this.originalEffectiveStart);
            acp.setOriginalEffectiveEnd(this.originalEffectiveEnd);
            return res;
        } catch (Exception e) {
            throw new DroitException(e);
        }
    }

    @Override
    public String toString() {
        String attr = this.customPredicateName != null? this.customPredicateName: "NULL";
        String values = "NULL";
        if (this.comparisonValues != null) {
            if (this.comparisonValues.length() > 200) {
                values = this.comparisonValues.substring(0, 200) + "...";
            } else {
                values = this.comparisonValues;
            }
        }
        values = "["+values+"]";

        return String.format("%s %s %s", attr, this.getClass().getSimpleName(), values);
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
                .append(comparisonDataType, rhs.comparisonDataType)
                .append(getComparisonValuesAsSet(), rhs.getComparisonValuesAsSet())
                .append(tradeAttribute, rhs.tradeAttribute)
                .isEquals();
    }

    @Override
    public int compareTo(TradePredicate rhs) {
        int superComp = super.compareTo(rhs);
        if (superComp != 0) return superComp;
        CustomPredicate cp = (CustomPredicate)rhs;
        return new CompareToBuilder()
                .append(customPredicateName, cp.customPredicateName)
                .toComparison();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 39)
                .append(getClass())
                .append(tradeAttribute)
                .append(customPredicateName)
                .append(getComparisonValuesAsSet())
                .toHashCode();
    }

    @Override
    public ValidationResult validate() {
        ValidationResult res = new ValidationResult();
        if (this.getCustomPredicateFunction() == null) {
            res.isValid = false;
            res.addException(new ValidationException("Custom function missing for " + this));
        } else if (this.getCustomPredicateFunction() instanceof AlwaysTrueFunction) {
            res.isValid = false;
            res.addException(new ValidationException(
                    "We need to replace the stub AlwaysTrueFunction with a proper implementation of a custom function for " + this));
        }
        return res;
    }

    @JsonIgnore
    @XmlTransient
    public CustomPredicateFunction getCustomPredicateFunction() {
        return CUSTOM_FUNCTION_REGISTRY.get(this.customPredicateName);
    }

    private static Map<String, ? extends CustomPredicateFunction> CUSTOM_FUNCTION_REGISTRY = new HashMap<String, CustomPredicateFunction>(){{
        put("SwapLegBusinessDayConventionConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapLegCalendarsConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapFloatLegFrequencyConsistencyCmeCheck", new AlwaysTrueFunction());
        put("SwapFloatLegFrequencyConsistencyLchCheck", new AlwaysTrueFunction());
        put("SwapFloatLegFrequencyConsistencyGenericCheck", new AlwaysTrueFunction());
        put("SwapFloatLegFrequencyConsistencyStrictCheck", new AttributeConsistencyFunction());
        put("BasisSwapFloatLegFrequencyConsistencyStrictCheck", new AttributeConsistencyFunction());
        put("SwapFixedLegFrequencyConsistencyCheck", new AlwaysTrueFunction());
        put("SwapFixedLegFrequencyConsistencyStrictCheck", new AttributeConsistencyFunction());
        put("SwapLegsNotionalConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapLegsEffectiveDateConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapLegsTerminationDateConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapLaterEffectiveDateCheck", new AlwaysTrueFunction());
        put("SwapFeePaymentCurrencyCheck",
                new AttributeConsistencyFunction());
        put("SwapStubCurrencyCheck", new AttributeConsistencyFunction());
        put("SwapFloatLegFinalStubLinearInterpCheck", new AlwaysTrueFunction());
        put("SwapFeePaymentDateCalendarCheck", new AlwaysTrueFunction());
        put("SwapSimultaneousInitialFinalStubCheck", new AlwaysTrueFunction());
        put("SwapCompoundingStubCheck", new AlwaysTrueFunction());
        put("SwapBasisCombinationUnspecifiedCheck", new AlwaysTrueFunction());
        put("SwapAsxMaxInitialStubCheck", new AlwaysTrueFunction());
        put("SwapAsxMaxFinalStubCheck", new AlwaysTrueFunction());
        put("SwapAsxOisMaxResidualTermCheck", new AlwaysTrueFunction());
        put("FraBusinessCalendarsConsistencyCheck", new AttributeConsistencyFunction());

        put("SwapZcStubCheck", new AlwaysTrueFunction());
        put("SwapZcEffectiveDateCheck", new AlwaysTrueFunction());
        put("SwapFloatLegStubInterpRangeCheck", new AlwaysTrueFunction());
        put("SwapCompoundingConstantNotionalCheck", new AlwaysTrueFunction());
        put("SwapNotionalStepDateConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapNotionalStepFrequencyConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapNotionalStepAmountConsistencyCheck", new AttributeConsistencyFunction());
        put("SwapImmPaymentFrequencyCheck", new AlwaysTrueFunction());
        put("SwapNotionalStepCalculationFrequencyCheck", new AlwaysTrueFunction());
        put("LchAdditionalPaymentCalendarConsistencyCheck", new AttributeConsistencyFunction());
        put("ImmRollConventionImmDateCheck", new AlwaysTrueFunction());
        put("NoSimultaneousInitialFinalStubCheck", new AlwaysTrueFunction());
        put("LchFrequencyIndexTenorConsistencyCheck", new AttributeConsistencyFunction());
        put("BrlValuationCalendarCheck", new AlwaysTrueFunction());

        put("TradingHoursCheck", new TradingHoursCheckFunction());

        put("CalcTermBCConsistencyCheck", new AttributeConsistencyFunction());
        put("CalcTermBDCConsistencyCheck", new AttributeConsistencyFunction());
        put("LchStubCombinationsCheck", new AlwaysTrueFunction());
        put("TermPaymentCalcTerminationBDCCheck", new AlwaysTrueFunction());
        put("TermPaymentFrequencyConstantNotionalCheck", new AlwaysTrueFunction());
        put("TermPaymentCalcTerminationBCCheck", new AlwaysTrueFunction());
        put("ConstantNotional12mPaymentFreqCheck", new AlwaysTrueFunction());
        put("checkNotionalIncrement", new AlwaysTrueFunction());

    }};
}
