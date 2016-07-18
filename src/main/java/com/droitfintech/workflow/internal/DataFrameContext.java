package com.droitfintech.workflow.internal;


import com.droitfintech.dataframes.Value;

import java.util.*;

/**
 * Created from OLdBox code base by CWH 07/07/16
 */
public class DataFrameContext implements Map<String,Object> {
    Evaluator evaluator;
    HashMap<String, Object> converted = new HashMap<String, Object>();

    public DataFrameContext(Evaluator evaluator) {
        this.evaluator = evaluator;
    }


    public int size() {
        return 0;//evaluator.getPayload().getUnderlyingMap().size();
    }


    public boolean isEmpty() {
        return true; //evaluator.getPayload().getUnderlyingMap().isEmpty();
    }


    public boolean containsKey(Object key) {
        return false;
    }


    public boolean containsValue(Object value) {
        return false;
    }


    public Object get(Object key) {
        if(converted.containsKey(key)) {
            return converted.get(key);
        }
        String[] parts = ((String)key).split("\\.");
        Object rawVal = null;
        Map<String,Object> m1 = null;
        if(parts.length > 0) {
            m1 = evaluator.get(parts[0]);
        }
        if(parts.length > 1) {
            rawVal =  m1 != null ? m1.get(parts[1]) : null;
        } else {
            rawVal = m1;
        }
        if(rawVal != null) {
            if(rawVal instanceof Collection) {
                ArrayList<Value> boxedList = new ArrayList<Value>();
                Iterator rawIter = ((Collection) rawVal).iterator();
                while(rawIter.hasNext()) {
                    Object nextRawValue = rawIter.next();
                    Value.ValueType valueType = Value.getValueTypeForObject(nextRawValue);
                    if (valueType != null) {
                        Value boxed = new Value(nextRawValue, valueType);
                        boxedList.add(boxed);
                    }
                }
                converted.put((String) key, boxedList);
                return boxedList;
            } else {
                Value.ValueType valueType = Value.getValueTypeForObject(rawVal);
                if (valueType != null) {
                    Value boxed = new Value(rawVal, valueType);
                    converted.put((String) key, boxed);
                    return boxed;
                }
            }
        }
        return rawVal;
    }


    public Object put(String key, Object value) {
        return null;
    }


    public Object remove(Object key) {
        return null;
    }


    public void putAll(Map<? extends String, ?> m) {

    }


    public void clear() {

    }


    public Set<String> keySet() {
        return new HashSet<String>();//evaluator.getPayload().getUnderlyingMap().keySet();
    }


    public Collection<Object> values() {
        // return null;
        //return evaluator.getPayload().getUnderlyingMap().values();
        return new ArrayList<Object>();
    }


    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
