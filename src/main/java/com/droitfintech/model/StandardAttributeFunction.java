package com.droitfintech.model;

import com.droitfintech.model.TradeAttribute.UnmetDependencyException;

public interface StandardAttributeFunction {

    /**
     * The method that gets called when a StandardAttribute delegates its calculate()
     * to this.calculationFunction.calculate(this)
     *
     * @param self
     * @return some value
     * @throws UnmetDependencyException
     */
    Object calculate(StandardAttribute self) throws UnmetDependencyException;

}
