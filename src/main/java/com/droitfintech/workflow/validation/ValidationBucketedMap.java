package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.attribute.Attribute;
import com.droitfintech.datadictionary.attribute.Cardinality;
import com.droitfintech.datadictionary.attribute.Namespace;
import com.droitfintech.datadictionary.attribute.Type;
import groovy.lang.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by barry on 4/2/16.
 */
public class ValidationBucketedMap<K, V> implements Map<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(ValidationBucketedMap.class);
    private Namespace droitNamespace = null;
    private Namespace clientNamespace = null;
    private Set<DecisionElement> moduleOutputs = null;

    public ValidationBucketedMap() {
    }

    public ValidationBucketedMap(Set<DecisionElement>  moduleOutputs) {
        this.moduleOutputs = moduleOutputs;
    }

    public ValidationBucketedMap(Namespace droitNamespace, Namespace clientNamespace) {
        this.droitNamespace = droitNamespace;
        this.clientNamespace = clientNamespace;
    }


    public int size() {
        return 0;
    }


    public boolean isEmpty() {
        return true;
    }


    public boolean containsKey(Object key) {
        return false;
    }


    public boolean containsValue(Object value) {
        return false;
    }


    public V get(Object key) {
        String field = key.toString();
        logger.trace("Calling ValidationBucketedMap get(" + key.toString() + ")");
        Attribute attr = null;
        Type varType = Type.Boolean; // Selected so unknown values will work in conditions.
        if(droitNamespace != null) {
            attr = droitNamespace.getAttribute(field);
            if(attr != null)
                varType = attr.getType();
        }
        if(attr == null && clientNamespace != null) {
            attr = clientNamespace.getAttribute(field);
            if(attr != null)
                varType = attr.getType();
        }
        // If attribute was in one of the namespaces then do not allow it to be null.
        if((droitNamespace != null || clientNamespace != null) && attr == null) {
            throw new MissingPropertyException(field, field, this.getClass());
        }
        if(attr != null && attr.getCardinality() == Cardinality.Many) {
            return (V)new ArrayList<V>();
        }
        if(moduleOutputs != null) {
            for (DecisionElement el : moduleOutputs) {
                if (el.getVariableName().equals(key)) {
                    if(el.getCardinality() == Cardinality.Many) {
                        return (V)new ArrayList<V>();
                    }
                    if(el.getVariableType() != null) {
                        varType = el.getVariableType();
                    }
                    if(el.getVariableValue() != null) {
                        return (V)el.getVariableValue();
                    }
                    break;
                }
            }
        }
        // boolean does not have a zero arg constructor so just pick one.
        if (varType.className.equals("java.lang.Boolean")) {
            return (V) Boolean.FALSE;
        }
        if (varType.className.equals("java.math.BigDecimal")) {
            return (V) BigDecimal.ZERO;
        }
        try {
            Class clazz = Class.forName(varType.className);
            return (V) clazz.newInstance();
        } catch (Exception ignore) {
            logger.trace("Can't create type instance");
        }
        return (V)"";
    }


    public V put(K key, V value) {
        return null;
    }


    public V remove(Object key) {
        return null;
    }


    public void putAll(Map<? extends K, ? extends V> m) {

    }


    public void clear() {

    }


    public Set<K> keySet() {
        return new HashSet();
    }


    public Collection<V> values() {
        return new ArrayList<V>();
    }


    public Set<Entry<K, V>> entrySet() {
        return new HashSet();
    }

}
