package com.droitfintech.exceptions;

public enum DroitExceptionStatusCodes {
    STATUS_MISSING_AUDIT_RECORD(2001),

    // MODULE - CounterpartySourceIntegrationService ( Status Code 100 - 199 )
    INVALID_PARTY_IDENTIFIER(100),
    INVALID_ATTEMPT_TO_FIND_PARTY(101);



    private int numVal;

    DroitExceptionStatusCodes(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
