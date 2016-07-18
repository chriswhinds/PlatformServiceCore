package com.droitfintech.datadictionary.converters;

import java.math.BigDecimal;

/**
 * Created by barry on 2/26/16.
 */
public class BigDecimalConverter implements TypeConverter {
    @Override
    public Object convert(String value) throws ConversionException {
        try {
            return new BigDecimal(value);
        } catch(Exception ex) {
            return null;
        }
    }
}
