package com.droitfintech.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.AttributeType;

public class EnumerationValidator extends Validator {

    final Logger logger = LoggerFactory.getLogger(EnumerationValidator.class);

    private Set<Object> allowedValues = new HashSet<Object>();
    private AttributeType type;

    public EnumerationValidator(AttributeType type) {
        this.type = type;
        String enumFileName = type.toString() + ".enum";
        InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("enums/"+enumFileName);
        if (fileStream != null) {
            Reader input = new InputStreamReader(fileStream);
            BufferedReader bufRead = new BufferedReader(input);
            try {
                String line = bufRead.readLine();
                while (line != null) {
                    String validValue = StringUtils.stripToEmpty(line);
                    Object convertedValue = ClassConverter.getConverter(type.clazz).convert(validValue);
                    logger.trace("Converted valid enum value '{}' to '{}'", validValue, convertedValue);
                    if (convertedValue.getClass() == String.class) {
                        convertedValue = StringUtils.stripToEmpty((String)convertedValue);
                    }
                    allowedValues.add(convertedValue);
                    line = bufRead.readLine();
                }
            } catch (IOException e) {
                throw new DroitException("Problems while reading enum values for validator " + enumFileName, e);
            }
        } else {
            throw new DroitException("No enum file found for " + enumFileName);
        }
    }

    public EnumerationValidator() {}

    public void setAllowedValues(Set<Object> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public void setAttributeType(AttributeType attributeType) {
        this.type = attributeType;
    }

    public Set<Object> getEnumeration() {
        return this.allowedValues;
    }

    public String pprintAllowedValues() {

        // show all attributes on the exception if trace is enabled.
        int howManyAttribsToShow = logger.isTraceEnabled() ? Integer.MAX_VALUE : 5;

        Set<Object> allowed = new HashSet<Object>();
        Iterator<Object> iter = this.allowedValues.iterator();
        int i = 0;
        while (iter.hasNext() && i < howManyAttribsToShow) {
            Object next = iter.next();
            if (next == null) {
                //allowed.add(ClassConverter.NULL_SPECIAL_VALUE);
                allowed.add("(null)");
            } else {
                allowed.add(String.valueOf(next));
            }
            i++;
        }
        String str = StringUtils.join(allowed, " ");
        if (this.allowedValues.size() > howManyAttribsToShow) {
            str += "...";
        }
        return str;
    }

    @Override
    public boolean doValidate(String attr, Object value) {
        if (logger.isTraceEnabled()) {
            logger.trace("Validating attribute '{}'.  Value '{}'.  Possible values '{}'", attr,
                    value, pprintAllowedValues());
        }
        if (this.allowedValues.contains(value)) {
            return true;
        }
        throw new ValidationException("Enumeration validation failed for attribute: " + attr
                + " Expected type: " + this.type
                + ", value " + value + ". "
                + "Allowed values: (" + this.pprintAllowedValues() + ")", this);
    }
}
