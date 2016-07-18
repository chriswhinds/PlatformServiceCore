package com.droitfintech.services;

/**
 * Created by christopherwhinds on 6/21/16.
 */
public class RegistrationException extends Exception {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }


}
