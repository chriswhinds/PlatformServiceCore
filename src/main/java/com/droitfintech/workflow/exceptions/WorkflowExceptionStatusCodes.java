package com.droitfintech.workflow.exceptions;

/**
 * Workflow Exceptions take up chunck 1001 - 1100
 */
public enum WorkflowExceptionStatusCodes {

    STATUS_MISSING_ATTRIBUTE(1001),
    STATUS_MISSING_MODULE(1002);

    private int numVal;

    WorkflowExceptionStatusCodes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
