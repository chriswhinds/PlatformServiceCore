package com.droitfintech.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

/**
 * StringMustContain.java
 *
 * Class that represents the "MustContain" enum logic, which consists of a set of values and a logical operator
 *
 * COPYRIGHT (C) 2013 Droit Financial Technologies, LLC
 *
 * @author jisoo
 *
 */
public class MustContain<T> {

    public static final Pattern FIELD_PATTERN = Pattern.compile("((AND|OR)\\()?([^\\(\\)]+)\\)?");

    public enum Operator{ AND, OR };

    private Set<T> values = new LinkedHashSet<T>();

    private Operator operator = Operator.AND;

    @SuppressWarnings("unchecked")
    public static <E> MustContain<E> createMustContain(Class<E> mustContainType, String rawValues) {
        Matcher m = FIELD_PATTERN.matcher(rawValues.trim());

        if (m.find()) {
            MustContain<E> mustContain = new MustContain<E>();
            String optOperator = m.group(2);
            String mustContainValues = m.group(3);
            if (optOperator!=null) {
                Operator op = Operator.valueOf(optOperator);
                mustContain.setOperator(op);
            }
            if (mustContainType.equals(String.class)) {
                mustContain.setValues((Set<E>) MustContain.parseStringValues(mustContainValues));
            } else if (mustContainType.equals(BigDecimal.class)) {
                mustContain.setValues((Set<E>) MustContain.parseBigDecimalValues(mustContainValues));
            } else {
                throw new RuntimeException("MustContain type unrecognized!");
            }
            return mustContain;
        } else if (rawValues.trim().isEmpty()) {
            return new MustContain<E>();
        }

        throw new RuntimeException("MustContain raw value unparsable! " + rawValues);
    }

    public static Set<String> parseStringValues(String values) {
        Set<String> res = new LinkedHashSet<String>();
        String[] splitValues = values.split(",");
        for (String splitValue: splitValues) {
            if (! splitValue.trim().equals("")) {
                res.add(splitValue.trim());
            }
        }
        return res;
    }

    public static Set<BigDecimal> parseBigDecimalValues(String values) {
        Set<BigDecimal> res = new LinkedHashSet<BigDecimal>();
        String[] splitValues = values.split(",");
        for (String splitValue: splitValues) {
            if (! splitValue.trim().equals("")) {
                res.add(new BigDecimal(splitValue.trim()));
            }
        }
        return res;
    }

    public Set<T> getValues() {
        return values;
    }

    public void setValues(Set<T> values) {
        this.values = values;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public boolean evaluate(Collection<T> evalCollection) {
        boolean res = false;
        if (Operator.OR == operator) {
            res = ! Sets.intersection(new LinkedHashSet<T>(evalCollection), values).isEmpty();
        } else {
            res = evalCollection.containsAll(this.values);
        }
        return res;
    }
}

