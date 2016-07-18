package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import java.util.Collection;
import java.util.LinkedList;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class ValidationResult {

    public boolean isValid = true;

    private Collection<ValidationException> exceptions = new LinkedList<ValidationException>();

    public Collection<ValidationException> getExceptions() {
        return exceptions;
    }

    public void addAllExceptions(Collection<ValidationException> moreExceptions) {
        exceptions.addAll(moreExceptions);
    }

    public void addException(ValidationException exception) {
        exceptions.add(exception);
    }

    @Override
    public String toString() {
        String message = "validation result: valid=" + this.isValid + ", exception count: " + exceptions.size() + "\n";
        for (ValidationException e: exceptions) {
            message += e.getMessage() + "\n";
        }
        return message;
    }

    public static boolean allValid(Collection<ValidationResult> results) {
        for (ValidationResult r : results) {
            if (!r.isValid) {
                return false;
            }
        }
        return true;
    }
}
