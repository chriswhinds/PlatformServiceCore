package com.droitfintech.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


import com.droitfintech.model.TradeContext;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

@Entity
@DiscriminatorValue("ALWAYS_TRUE")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name="alwaysTrue")
public class AlwaysTrue extends LogicalPredicate {

    public AlwaysTrue() {
        super();
    }

    @Override
    public Predicate<TradeContext> getCorePredicate() {
        return Predicates.alwaysTrue();
    }
}
