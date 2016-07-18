package com.droitfintech.datadictionary;


import com.droitfintech.datadictionary.attribute.Attribute;
import com.droitfintech.datadictionary.attribute.Cardinality;
import com.droitfintech.datadictionary.attribute.Namespace;
import com.droitfintech.datadictionary.converters.ConversionException;
import com.droitfintech.datadictionary.converters.TypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
* Created by christopherwhinds on 7/7/16 Created of of the OLdBox code base
*/
public class NamespaceBackedMap<K,V> implements Map<K, V> {

    private HashMap<K,V> map;
    private Namespace droitNS;
    private Namespace clientNS;

    private Logger log = LoggerFactory.getLogger(NamespaceBackedMap.class);

    public NamespaceBackedMap(Namespace droitNS, Namespace clientNS, int mapSize) {
        this.droitNS = droitNS;
        this.clientNS = clientNS;
        if(droitNS == clientNS) {
            throw new RuntimeException("droit and client namespaces must be different");
        }
        map = new HashMap<K,V>(mapSize);
    }


    public int size() {
        return map.size();
    }


    public boolean isEmpty() {
        return map.isEmpty();
    }


    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }


    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }


    public V get(Object key) {
        return map.get(key);
    }

    public V getDefault(Object key) {

        V answer = null;

        Attribute at = droitNS.getAttribute((String) key);
        if(at != null) {
            answer = (V)at.getDefaultValueNative();
        }

        // null if they're no attribute in the namespace
        return answer;
    }


    public V put(K key, V value) {
        return map.put(key, value);
    }


    public V remove(Object key) {
        return map.remove(key);
    }


    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);

    }


    public void clear() {
        map.clear();
    }


    public Set<K> keySet() {
        return map.keySet();
    }


    public Collection<V> values() {
        return map.values();
    }


    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public Namespace getNamespace() {
        return droitNS;
    }

    public int validateAndAddAttributes(Map<String, Object> sourceMap, Map<String, Object> clientMap, String idField, boolean allowNullAttributeValues) {
        return validateAndAddAttributes(sourceMap, clientMap, idField, allowNullAttributeValues, true);
    }

    public int validateAndAddAttributes(Map<String, Object> sourceMap, Map<String, Object> clientMap, String idField,
                                        boolean allowNullAttributeValues, boolean applyDefaults) {
        int errorCount = 0;
        String objectId = (String) sourceMap.get(idField);
        for (Attribute attr : droitNS.getAttributes().values()) {
            errorCount += validateAndAddAttribute(sourceMap, attr, objectId,allowNullAttributeValues, applyDefaults);
        }

        if(clientNS != null && clientNS.getAttributes() != null) {
            for (Attribute attr : clientNS.getAttributes().values()) {
                errorCount += validateAndAddAttribute(clientMap != null ? clientMap : sourceMap, attr, objectId, allowNullAttributeValues, applyDefaults);
            }
        }
        if(sourceMap.size() > 0) {
            // each validate call above removed the property from the map so now copy the unknown attributes from the droit map
            // into the source map. This should be eliminated at some point by adding then internally created maps like capabilities
            // to the dictionary.
            map.putAll((Map<K,V>)sourceMap);
        }
        return errorCount;
    }

    private int validateAndAddAttribute(Map<String, Object> sourceMap, Attribute attr, String objectId,
                                        boolean allowNullAttributeValues, boolean applyDefaults) {
        int errorCount = 0;
        boolean attrValueIsValid = true;
        boolean nullValueNotAllowed = false;
        String attrName = attr.getName();
        Object val = sourceMap.get(attrName);
        if (val != null) {
            // Do all validation
            if(attr.getCardinality() == Cardinality.Many) {
                // Start of support for list type attributes.
                if(val instanceof String && attr.getType().className.equals("java.lang.String")) {
                    String[] values = ((String)val).split(",");
                    ArrayList<String> valuesAsList = new ArrayList<String>(values.length);
                    for(int idx = 0; idx < values.length; idx++) {
                        valuesAsList .add(values[idx].trim());
                    }
                    map.put((K) attrName, (V) valuesAsList);
                } else if(val instanceof Collection) {
                    if(((Collection) val).size() > 0) {
                        Object firstVal  = ((Collection) val).iterator().next();
                        if (!firstVal.getClass().getName().equals(attr.getType().className)) {
                            log.warn("Field {} is wrong type, expected `{}` but got `{}` for ID `{}`",
                                    attrName,
                                    attr.getType().className,
                                    firstVal.getClass().getName(), objectId);
                        }
                    }
                    // empty collections are allowed.
                } else {
                    log.warn("Field {} is wrong type, expected collection of `{}` but got `{}` for ID `{}`",
                            attrName,
                            attr.getType().className,
                            val.getClass().getName(), objectId);
                    attrValueIsValid = false;
                }
            }
            else if (!val.getClass().getName().equals(attr.getType().className)) {
                if((val instanceof CharSequence) && attr.getType().getConverter() != null) {
                    TypeConverter converter = attr.getType().getConverter();
                    try{
                        Object convVal = converter.convert((String)val);
                        val = convVal;
                        map.put((K) attrName, (V)val);
                    } catch (ConversionException ex ) {
                        String errMessage = String.format("Field %s failed conversion from '%s' to %s for ID '%s'",
                                attrName, val,
                                attr.getType().className,
                                objectId);
                        log.warn(errMessage);
                        addError(errMessage);
                        attrValueIsValid = false;
                    }
                } else {
                    log.warn("Field {} is wrong type, expected `{}` but got `{}` for ID `{}`",
                            attrName,
                            attr.getType().className,
                            val.getClass().getName(), objectId);

                    attrValueIsValid = false;
                }
            } else if ((val instanceof CharSequence) && StringUtils.isBlank((CharSequence) val)) {
                if(allowNullAttributeValues) {
                    map.put((K) attrName, (V)val);
                } else {
                    nullValueNotAllowed = true;
                }
            } else {
                map.put((K) attrName, (V)val);
            }
        } else if(sourceMap.containsKey(attrName)){ // value is null.
            if(allowNullAttributeValues) {
                map.put((K) attrName, null);
            } else {
                nullValueNotAllowed = true;
            }
        }
        // If no value supplied at all add default if it exists.
        if(!sourceMap.containsKey(attrName) ) {
            if(applyDefaults) {
                setFromNoDependencyDefault(attr);
            }
        }
        // Try replacing supplied null with default
        else if(nullValueNotAllowed || val == null || ((val instanceof CharSequence) && StringUtils.isBlank((CharSequence) val)) ) {
            // remove all known attributes so we are left with unknown ones at the end.
            sourceMap.remove(attrName);
            if(applyDefaults) {
                if (!setFromNoDependencyDefault(attr)) {
                    if (nullValueNotAllowed) {
                        attrValueIsValid = false;
                        log.warn("Null value not permitted for field {}, in {} with ID {} ", attrName, droitNS.getName(), objectId);
                    }
                }
            }
        }
        else {
            // remove all known attributes so we are left with unknown ones at the end.
            sourceMap.remove(attrName);
        }
        if(attrValueIsValid == false) ++errorCount;

        return errorCount;
    }

    private boolean setFromNoDependencyDefault(Attribute attr) {
        if(StringUtils.isNotBlank(attr.getDefaultValueAsString())) {
            String attrName = attr.getName();
            map.put((K)attrName, (V)attr.getDefaultValueNative());
            if(!map.containsKey("_defaulted")) {
                map.put((K)"_defaulted", (V)new TreeSet<String>());
            }
            Set<String> defaultsFields = (Set<String>)map.get("_defaulted");
            defaultsFields.add(attrName);
            return true;
        }
        return false;
    }

    private void addError(String message) {
        if(!map.containsKey("_errorList")) {
            map.put((K)"_errorList", (V)new TreeSet<String>());
        }
        Set<String> defaultsFields = (Set<String>)map.get("_errorList");
        defaultsFields.add(message);
    }
}
