package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import com.droitfintech.exceptions.DroitException;

import com.droitfintech.exceptions.DroitException;

public class ValidationException extends DroitException {

    private static final long serialVersionUID = 3904170130340800363L;

    private Validator generatingValidator;

    public ValidationException(Validator validator) {
        super();
        this.generatingValidator = validator;
    }

    public ValidationException(String message, Exception e) {
        super(message, e);
        this.generatingValidator = new ExceptionValidator();
    }

    public ValidationException(String message, Validator validator) {
        super(message);
        this.generatingValidator = validator;
    }

    public ValidationException(String message) {
        super(message);
        this.generatingValidator = new ExceptionValidator();
    }

    public Validator getGeneratingValidator() {
        return generatingValidator;
    }
}
