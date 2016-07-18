package com.droitfintech.datadictionary.converters;

/**
 * Checked exception type used to report conversion errors.
 * Created by barry on 1/12/16.
 */
public class ConversionException extends Exception{

    public ConversionException(String message) {
        super(message);
    }
}
