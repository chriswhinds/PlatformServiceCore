package com.droitfintech.workflow.exceptions;

import com.droitfintech.exceptions.DroitException;

/**
 * Exception thrown when module validation detects a reference back to the module being validated.
 */
public class WorkflowCircularReferenceException extends DroitException {
    /**
     * Constructs a new runtime exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public WorkflowCircularReferenceException(String message) {
        super(message);
    }
}
