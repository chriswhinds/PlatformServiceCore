package com.droitfintech.datadictionary;

import com.droitfintech.model.ClassConverter;
import com.droitfintech.model.ValidationException;
import com.droitfintech.model.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class DataDictionaryValidator extends Validator {

    private static final Logger logger = LoggerFactory.getLogger(DataDictionaryValidator.class);

    String typeName;
    Set<String> allowedValues;
    boolean nullAllowed ;

    public DataDictionaryValidator(String typeName, Set<String> allowedValues) {
        this.typeName = typeName;
        this.allowedValues = allowedValues;
        if(this.allowedValues.contains(ClassConverter.NULL_SPECIAL_VALUE)) {
            this.nullAllowed = true;
        } else {
            this.nullAllowed = false;
        }
    }

    public boolean doValidate(String attr, Object value) {
        // Dislike the use of exceptions but fixing that will have to wait for later.
        if(value == null && nullAllowed) {
            return true;
        }
        else if(value != null && this.allowedValues.contains(value)) {
            return true;
        }
        throw new ValidationException("Enumeration validation failed for attribute: " + attr
                + " Expected type: " + this.typeName
                + ", value " + (value != null ? value : "null") + ". "
                + "Allowed allowedValues: (" + this.pprintAllowedValues() + (nullAllowed ? ") or null" : ")"), this);
    }

    public String pprintAllowedValues() {

        // show all attributes on the exception if trace is enabled.
        int howManyAttribsToShow = logger.isTraceEnabled() ? Integer.MAX_VALUE : 10;

        Set<Object> allowed = new HashSet<Object>();
        Iterator<String> iter = this.allowedValues.iterator();
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
}
