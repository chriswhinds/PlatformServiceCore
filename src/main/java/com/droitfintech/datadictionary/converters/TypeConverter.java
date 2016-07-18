package com.droitfintech.datadictionary.converters;

/**
 * Interface used by NamespaceBackedMap to convert strings received to internalTypes.
 * Created by barry on 1/12/16.
 */
public interface TypeConverter {
     Object convert(String value) throws ConversionException;
}
