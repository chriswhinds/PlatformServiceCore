package com.droitfintech.model;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.regulatory.Tenor;

public class TenorValidator extends Validator {
    @Override
    public boolean doValidate(String attr, Object value) {
        if (value == null) {
            throw new ValidationException("Type validation failed for attribute: " + attr + ". "
                    + "Cannot convert null to Tenor.");
        }
        if (value instanceof Tenor) {
            return true;
        }
        try {
            Tenor.makeTenor((String) value);
            return true;
        } catch (Exception ignore) {
            throw new ValidationException("Type validation failed for attribute: " + attr + ". "
                    + "Could not convert \"" + value + "\" to Tenor.");
        }
    }
}
