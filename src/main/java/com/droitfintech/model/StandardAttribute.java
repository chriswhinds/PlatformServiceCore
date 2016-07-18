package com.droitfintech.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.droitfintech.datadictionary.attribute.Attribute;
import com.droitfintech.datadictionary.attribute.DefaultServiceLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.DefaultService.CircularDefaultReferenceException;
import com.droitfintech.model.ModelConversionUtil;

public class StandardAttribute extends TradeAttribute {

    Logger log = LoggerFactory.getLogger(StandardAttribute.class);

    protected List<StandardAttributeFunction> calculationFunctions = new LinkedList<StandardAttributeFunction>();
    protected List<String> linkedAttributes = new LinkedList<String>();
    private Attribute dataDictionaryAttribute;

    public StandardAttribute(String name, TradeContext tradeContext, AttributeType attrType, boolean isCollection) {
        super(name, tradeContext, attrType, isCollection);
    }

    /**
     * Full constructor designed for use by the data dictionary. Use of the others should be eliminated over time.
     * @param name
     * @param tradeContext
     * @param attrType
     * @param isCollection
     * @param validator Pass null if no validation.
     */
    public StandardAttribute(String name, TradeContext tradeContext, Class<?> attrType, boolean isCollection,
                             Validator validator, Attribute dataDictionaryAttribute) {
        super(name, tradeContext, attrType, isCollection, validator);
        this.dataDictionaryAttribute = dataDictionaryAttribute;
    }

    public void addLinkedAttributes(Collection<String> inputLinkedAttributes) {
        this.linkedAttributes.addAll(inputLinkedAttributes);
    }

    public void addCalculationFunction(StandardAttributeFunction calculationFunction) {
        calculationFunctions.add(calculationFunction);
    }

    private void traceCalculations(String source, Object result) {
        log.trace("Calculating attribute '{}' .  Using '{}'.  Result: {}", this.getName(), source, result);
    }

    @Override
    protected Object calculate() throws UnmetDependencyException {

        // First we try to look the value up in the source document.
        try {
            String rawValue = this.getTradeContext().getTradeDocument().get(this.name);
            this.source = this.getTradeContext().getTradeDocument().source(this.name);
            traceCalculations("underlying source", rawValue);
            return convertRawValue(rawValue);
        } catch (UnmetDependencyException e) {
            ; // No big deal, we try the lookup sources instead.
        } catch (Exception e) {
            String attrName = this.source != null ? this.source : this.name;
            throw new DroitException(
                    "Problem while pulling value for "+ attrName +" from source map ", e);
        }

        // Then we try to look up the value from a other attributes.
        for (String source: linkedAttributes) {
            try {
                Object value = getAttr(source).getValue();
                if (log.isTraceEnabled()) {
                    traceCalculations("linked value '" + source + "'", value);
                }
                return value;
            } catch (UnmetDependencyException e) {
                ; // No big deal. Simply move on to the next source, if any.
            }
        }

        // Then we try to run assigned functions, if any.
        for (StandardAttributeFunction func: this.calculationFunctions) {
            try {
                Object value = func.calculate(this);
                if (log.isTraceEnabled()) {
                    traceCalculations("calculation function " + func.getClass().getCanonicalName(), value);
                }
                return func.calculate(this);
            } catch (UnmetDependencyException e) {
                ; // No big deal. Simply move on to the next func, if any.
            }
        }

        // If this attribute if defaultable using the Data Dictionary...
        if (DefaultServiceLookup.FILL_WITH_DEFAULT.equals(this.dataDictionaryAttribute.getDefaultServiceLookup()))  {
            Object answer = this.dataDictionaryAttribute.getDefaultValueNative();
            traceCalculations("data dictionary defaults", answer);
            return answer;
        }

        // If this attribute is defaultable using the defaulting service... (eg NOT the default that's specified by
        // the Data Dictionary
        if (DefaultServiceLookup.FILL_BY_NAME.equals(this.dataDictionaryAttribute.getDefaultServiceLookup())) {
            try {
                String defaultValue = this.getTradeContext().getDefaultService().getDefaultValue(this);
                traceCalculations("defaulting service", defaultValue);
                return convertRawValue(defaultValue);
            } catch (CircularDefaultReferenceException e) {
                ; // The default service uses this attribute as a key. Catch and ignore so that we do not have a stack overflow.
            }
        }
        throw new UnmetDependencyException("No sources or calculation function returned a value", this.name);
    }

    protected Object convertRawValue(String rawValue) {
        if (isCollection) {
            return ModelConversionUtil.makeSet(attributeClass, rawValue);
        } else {
            return ClassConverter.getConverter(attributeClass).convert(rawValue);
        }
    }

}
