package com.droitfintech.workflow.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by barry on 4/2/16.
 */
public class SnipitResponse {
    private ArrayList<ValidationResponse.GroovyError> errors;
    private Object snipetReturn = null;
    private boolean  hasSyntaxError = false;
    private boolean  hasRuntimeError = false;
    private boolean  hasCircularReference = false;

    public void addNodeGroovyError(int row, int column, String text, String aceType, boolean syntaxError, boolean runtimeError) {
        if(errors == null)
            errors = new ArrayList<ValidationResponse.GroovyError>();
        errors.add(new ValidationResponse.GroovyError(row, column,text, aceType));
        if(syntaxError)
            hasSyntaxError = true;
        if(runtimeError)
            hasRuntimeError = true;
    }

    public boolean getHasCircularReference() {
        return hasCircularReference;
    }

    public void setHasCircularReference(boolean hasCircularReference) {
        this.hasCircularReference = hasCircularReference;
    }

    public Collection<ValidationResponse.GroovyError> getErrors() {
        return errors;
    }

    public boolean hasSyntaxError() {
        return this.hasSyntaxError;
    }

    public void setSnipetReturn(Object snipetReturn) {
        this.snipetReturn = snipetReturn;
    }

    public boolean hasOutput() {
        return snipetReturn != null;
    }

    public Map<String, Object> getOutputMap() {
        if(snipetReturn != null && snipetReturn instanceof Map) {
            return (Map<String, Object>)snipetReturn;
        } else {
            return null;
        }
    }

    public Boolean getOutputBoolean() {
        if(snipetReturn != null && snipetReturn instanceof Boolean) {
            return (Boolean)snipetReturn;
        } else {
            return null;
        }
    }

}
