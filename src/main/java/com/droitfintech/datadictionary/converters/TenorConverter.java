package com.droitfintech.datadictionary.converters;

import com.droitfintech.regulatory.Tenor;

/**
 * Created by barry on 2/26/16.
 */
public class TenorConverter implements TypeConverter {


    public Object convert(String value) throws ConversionException {
        try {
            return Tenor.makeTenor(value);
        } catch (Exception ignore) {
            throw new ConversionException("could not convert '" + value + "' to Tenor");
        }
    }
}
