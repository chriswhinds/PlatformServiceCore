package com.droitfintech.model;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;

public interface DefaultService {

    /**
     * For default-able attributes, this gets called in calculate() when all other efforts
     * to derive the value are exhausted.
     *
     * @param attrToDefault
     * @return
     * @throws UnmetDependencyException
     */
    String getDefaultValue(StandardAttribute attrToDefault) throws UnmetDependencyException;

    public static class CircularDefaultReferenceException extends DroitException {

        private static final long serialVersionUID = 8746391437845541914L;

        public CircularDefaultReferenceException (String message) {
            super(message);
        }
    }
}
