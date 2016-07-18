package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.regulatory.Tenor;

import com.droitfintech.model.TradingWeekHoursSet;
import com.droitfintech.model.ProductMaster;
import com.droitfintech.model.TradeContext.AttributeNotFoundException;
import com.droitfintech.model.ModelConversionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TradeAttribute implements Validatable {

    public enum AttributeType {
        BigDecimalType(BigDecimal.class),
        BlockEntryType(String.class),
        BooleanType(Boolean.class),
        BusinessCenter(String.class),
        BusinessDayConvention(String.class),
        CompoundingMethod(String.class),
        CountryCode(String.class),
        Currency(String.class),
        DateType(Date.class),
        DayCountFraction(String.class),
        DayOfWeek(String.class),
        DayType(String.class),
        EarlyTerminationType(String.class),
        ExecutionStyle(String.class),
        Family(String.class),
        FinMktInfraType(String.class),
        FloatLegIndex(String.class),
        FraDiscounting(String.class),
        IndexTenor(Tenor.class),
        InflationInterpolationMethod(String.class),
        InflationSource(String.class),
        IntegerType(Integer.class),
        LegType(String.class),
        OptionStyle(String.class),
        PayRelativeTo(String.class),
        ProductMasterType(ProductMaster.class),
        ProductStructure(String.class),
        ProductVariant(String.class),
        RollConvention(String.class),
        SettlementMethod(String.class),
        StringType(String.class),
        StubType(String.class),
        TenorType(Tenor.class),
        TradingHoursType(TradingWeekHoursSet.class),
        TransactionType(String.class),
        ExecutionTypeType(String.class),
        PackageLegTypeType(String.class);

        public static AttributeType forName(String name) {
            return Enum.valueOf(AttributeType.class, name);
        }

        public final Class<?> clazz;

        private AttributeType(Class<?> clazz) {
            this.clazz = clazz;
        }

    }

    public static class NoMemoizedValueException extends Exception {
        private static final long serialVersionUID = 50286075862169136L;

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

    }

    public static class UnmetDependencyException extends Exception {

        private static final long serialVersionUID = -2895311033887725829L;

        private String errorSourceAttribute;
        private String coreMessage;

        public UnmetDependencyException(String message, String errorSourceAttribute) {
            super();
            this.errorSourceAttribute = errorSourceAttribute;
        }

        public String getErrorSourceAttribute() {
            return this.errorSourceAttribute;
        }

        @Override
        public String getMessage() {
            return (coreMessage + " ... from " + errorSourceAttribute);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    public static class BypassRuleException extends DroitException {
        private static final long serialVersionUID = -8086023812343770250L;

        public BypassRuleException() {
            super();
        }
        public BypassRuleException(String message, Exception e) {
            super(message, e);
        }
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    protected boolean isMemoized = false;
    protected Object memoizedValue;

    protected String name;
    protected String source;
    protected Class<?> attributeClass;
    protected boolean isCollection;
    private  Validator singleValueValidator;

    private TradeContext tradeContext;	// deliberately limited access; use setTradeContext().

    // TO be removed after data dict integration is complete.
    protected TradeAttribute(String name, TradeContext tradeContext, AttributeType type, boolean isCollection) {
        this(name,tradeContext,type.clazz,isCollection, null);
    }

    protected TradeAttribute(String name, TradeContext tradeContext, Class<?> type, boolean isCollection, Validator validator) {
        this.name = name;
        this.attributeClass = type;
        this.isCollection = isCollection;
        this.setTradeContext(tradeContext);
        this.singleValueValidator = validator;
    }

    protected abstract Object calculate() throws UnmetDependencyException;

    protected Object calculateValue() throws UnmetDependencyException {
        if (this.isMemoized) throw new RuntimeException(
                "Attribute "+name+" is already memoized with value "+memoizedValue);
        return this.calculate();
    }

    public TradeAttribute getAttr(String attrName) {
        return this.tradeContext.getAttribute(attrName);
    }

    protected Object getMemoizedValue() throws NoMemoizedValueException {
        if (this.isMemoized) return this.memoizedValue;
        throw new NoMemoizedValueException();
    }

    public String getName() {
        return name;
    }

    public TradeContext getTradeContext() {
        return tradeContext;
    }

    /**
     * Return the value of this trade attribute, throwing UnmetDependencyException if
     * any of its dependencies are not available for calculation.
     * @return
     * @throws UnmetDependencyException
     */
    public Object getValue() throws UnmetDependencyException {

        Object res = null;
        try {
            res = this.getMemoizedValue();
        } catch (NoMemoizedValueException e) {
            res = this.calculateValue();
            memoizedValue = res;
            isMemoized = true;
        }

        return res;
    }

    public Collection<?> getValueAsCollection() throws UnmetDependencyException {
        if (!isCollection) {
            throw new DroitException(this.name + ".getValueAsCollection() can only be called if it is isCollection() ");
        }
        try {
            return (Collection<?>)getValue();
        } catch (ClassCastException e) {
            throw new DroitException(this.name + ".getValueAsCollection() needs a Collection as value, but instead sees " + getValue());
        }
    }

    public boolean isMemoized() {
        return isMemoized;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void setTradeContext(TradeContext context) {
        resetValue();
        this.tradeContext = context;
        context.addAttribute(this);
    }

    /**
     * Explicitly sets the memoized attribute value. All subclasses of TradeAttribute
     * by default inherit this ability.
     *
     * @param value the value to be memoized.
     */
    public void setValue(Object value) {
        this.memoizedValue = value;
        this.isMemoized = true;
    }

    /**
     * Reset the memoized value to null and isMemoized to false
     */
    public void resetValue() {
        this.isMemoized = false;
        this.memoizedValue = null;
    }

    public Class<?> getAttributeClass() { return attributeClass; }

    public void setAttributeType(AttributeType attributeType) {
        this.attributeClass = attributeType.clazz;
    }

    public boolean isCollection() {
        return isCollection;
    }

    @Override
    public String toString() {
        if (this.name == null) {
            return "EMPTY RULE";
        }
        String valueStr = "";
        try {
            valueStr = this.getValue()==null? "Value NULL": this.getValue().toString();
        } catch (UnmetDependencyException e) {
            valueStr = "Value undefined";
        }  catch (BypassRuleException e) {
            valueStr = "BYPASS";
        } catch (DroitException e) {
            valueStr = "Unknown exception in getValue() ... " + e.getMessage();
        }
        return this.name + "\t" + valueStr;
    }

    /**
     * Potentially an expensive operation, since it calls getValue().
     *
     */
    public ValidationResult validate() {
        ValidationResult res = new ValidationResult();
        try {
            if (this.isCollection) {
                for (Object value: getValueAsCollection()) {
                    validateSingle(res, value);
                }
            } else {
                validateSingle(res, this.getValue());
            }
        } catch (UnmetDependencyException e) {
            ; // An unmet dependency is not a validation error
        } catch (AttributeNotFoundException e) {
            res.addException(new ValidationException("Bad reference to an unregistered attribute in " + this, e));
        } catch (DroitException e) {
            res.addException(new ValidationException("Unanticipated exception for " + this, e));
        }
        return res;
    }

    private void validateSingle(ValidationResult res, Object value) {
        if(singleValueValidator != null) {
            try {
                singleValueValidator.validate(this.source, value);
            } catch (ValidationException e) {
                res.isValid = false;
                res.addException(e);
            }
        }
    }

}
