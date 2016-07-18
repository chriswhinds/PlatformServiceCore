package com.droitfintech.workflow.exceptions;

/**
 * Created by barry on 12/16/15.
 */
public class ParseDateWorkflowException extends WorkflowException {
    private String dateString;

    public ParseDateWorkflowException(String msg, String date) {
        super(msg);
        dateString = date;
    }

    public String getDateString() {
        return dateString;
    }
}
