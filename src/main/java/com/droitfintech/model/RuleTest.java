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

import com.droitfintech.model.AbstractComparisonPredicate;
import com.droitfintech.model.TradeAttribute.AttributeType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuleTest {

    public String attributeName();
    public AttributeType attributeType();
    public Class<? extends AbstractComparisonPredicate> operator();
    public String customConverter() default "";
    String customRuleName() default "";
    String excludeFromLevel() default "";
    // If defined, constrain the migrations to the specified FinMktInfraVersionType
    String onlyAppliesToRuleType() default "";
}
