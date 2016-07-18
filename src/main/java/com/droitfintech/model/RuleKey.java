package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.droitfintech.model.TradeAttribute.AttributeType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuleKey {

    public String attributeName();
    public AttributeType attributeType();
    public String explicitNull() default "";

}
