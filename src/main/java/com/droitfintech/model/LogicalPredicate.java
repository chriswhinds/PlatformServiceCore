package com.droitfintech.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeContext;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Predicate;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlTransient
public abstract class LogicalPredicate extends TradePredicate {

    @JsonIgnore
    protected abstract Predicate<TradeContext> getCorePredicate();

    public LogicalPredicate(TradePredicate... children) {
        super(children);
    }


    public boolean apply(TradeContext inputTrade) {
        return this.getCorePredicate().apply(inputTrade);
    }

    @Override
    public TradePredicate copy() {
        try {
            TradePredicate res = this.getClass().newInstance();
            for (TradePredicate child: this.getChildren()) {
                res.addChild(child.copy());
            }
            return res;
        } catch (Exception e) {
            throw new DroitException(e);
        }
    }

}
