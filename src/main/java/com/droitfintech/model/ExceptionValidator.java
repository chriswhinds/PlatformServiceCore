package com.droitfintech.model;

/**
 * Stub validator used for generic exceptions encountered during validation
 * @author jisoo
 *
 */
public class ExceptionValidator extends Validator {

    @Override
    public boolean doValidate(String attr, Object value) {
        return false;
    }

}
