package com.droitfintech.datadictionary.converters;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by barry on 2/26/16.
 */
public class BooleanConverter implements TypeConverter {


    public Object convert(String value) throws ConversionException {
        // Boolean.valueOf() returns false for all values other the "true" but we only want to accept
        // true and false to prevent clients from failing to realise they did not supply a valid value.
        if(StringUtils.isNotBlank(value)) {
            value = value.trim();
            if(value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if(value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
        }
        throw new ConversionException("could not convert '" + (value != null ? value : "null") + "' to Boolean");
    }
}
