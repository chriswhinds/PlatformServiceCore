package com.droitfintech.workflow.exceptions;



/**
 * Created by barry on 2/12/16.
 */
public class UnknownModuleException  extends DroitClientException {

    private String moduleName;

    public UnknownModuleException(String message, String moduleName) {
        super(message);
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
}
