package com.droitfintech.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.TradeAttribute.AttributeType;
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;
import com.droitfintech.model.ModelConversionUtil;

public class TradeContext implements Map<String, Object> {

    private final Logger log = LoggerFactory.getLogger(TradeContext.class);
    private static Logger attrLogger = LoggerFactory.getLogger("com.droitfintech.attributes");
    private final boolean logAttributes;

    private Map<String, TradeAttribute> registry = new HashMap<String, TradeAttribute>();

    // place to dump all non-product data that we'd like to have ride along w/ the Trade Context
    private Map<String, Object> metadata = new HashMap<String, Object>();

    private TradeDocument tradeDocument;

    private DefaultService defaultService;

    public TradeContext(DefaultService defaultService) {
        DroitException.assertThat(defaultService!=null, "Please supply a non-null instance of DefaultService when instantiating TradeContext");
        this.defaultService = defaultService;
        this.logAttributes = attrLogger.isTraceEnabled();
    }

    public TradeContext(DefaultService defaultService, TradeDocument document) {
        this(defaultService);
        this.tradeDocument = document;
    }

    public void resetDocument(TradeDocument document) {
        this.tradeDocument = document;
        for (TradeAttribute attr: this.registry.values()) {
            attr.resetValue();
        }
    }

    public void addAttribute(TradeAttribute attribute) {
        String attrName = attribute.getName();
        if (this.registry.containsKey(attrName)) {
            throw new RuntimeException("Attribute " + attrName + "already registered for context");
        }
        log.trace("Adding attribute '{}' to the registry", attrName);
        this.registry.put(attrName, attribute);
    }

    public TradeAttribute getAttribute(String attrName) {
        if (!this.registry.containsKey(attrName)) {
            throw new AttributeNotFoundException("Attribute " + attrName + " not found in registry", attrName);
        }
        return this.registry.get(attrName);
    }

    public void setAttributeValue(String attrName, String strVal) {
        TradeAttribute attr = getAttribute(attrName);
        if (attr.isCollection) {
            attr.setValue(ModelConversionUtil.makeSet(attr.getAttributeClass(), strVal));
        } else {
            attr.setValue(ClassConverter.getConverter(attr.getAttributeClass()).convert(strVal));
        }
    }

    @Override
    public String toString() {
        // TODO switch this to the JSON pretty printer once it's moved to
        // Droit core
        String res = "\n";
        for (String key: this.registry.keySet()) {
            try {
                res = res + key + "\t" + tradeDocument.get(key) + "\n";
            } catch (UnmetDependencyException e) {
                ;
            }
        }
        return res;
    }

    public static class AttributeNotFoundException extends DroitException {
        private String attrName;
        public AttributeNotFoundException(String msg, String attr) {
            super(msg);
            attrName = attr;
        }
        public String getErrorSourceAttribute() {
            return attrName;
        }
    }

    public TradeDocument getTradeDocument() {
        return tradeDocument;
    }

    public DefaultService getDefaultService() {
        return defaultService;
    }


    public int size() {
        return this.registry.size();
    }


    public boolean isEmpty() {
        return this.registry.isEmpty();
    }


    public boolean containsKey(Object key) {
        return this.registry.containsKey(key);
    }


    public boolean containsValue(Object value) {
        // Because the trade context is lazy, we generally cannot tell if the value exists.
        throw new DroitException("Map.containsValue() not implemented for TradeContext");
    }


    /**
     * This method will return null if it encounters UnmetDependencyException while accessing the
     * referenced attribute value.
     */
    public Object get(Object key) {

        Object answer = null;

        try {
            TradeAttribute attr = this.getAttribute((String)key);
            answer = attr.getValue();
        } catch (UnmetDependencyException e) {
            answer = null;
        }

        if (logAttributes) {
            String logMsg = String.format("%35s - %40s : %-100s", "TradeContext", key,
                    answer == null ? "null" : answer.toString());
            attrLogger.trace(logMsg);
        }

        return answer;
    }


    public Object put(String key, Object value) {
        Object res = get(key);
        this.getAttribute(key).setValue(value);
        return res;
    }


    public Object remove(Object key) {
        // We don't really want to allow this kind of access on TradeContext through its Map interface
        throw new DroitException("Map.remove() not implemented for TradeContext");
    }


    public void putAll(Map<? extends String, ? extends Object> m) {
        for (java.util.Map.Entry<? extends String, ? extends Object> e: m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }


    public void clear() {
        // We don't really want to allow this kind of access on TradeContext through its Map interface
        throw new DroitException("Map.clear() not implemented for TradeContext");
    }


    public Set<String> keySet() {
        return this.registry.keySet();
    }

    /**
     * This implementation of Map.values() will only return the values that have already been computed/memoized.
     */

    public Collection<Object> values() {
        Collection<Object> res = new LinkedList<Object>();
        for (String name: this.registry.keySet()) {
            if (getAttribute(name).isMemoized) {
                res.add(get(name));
            }
        }
        return res;
    }


    public Set<Map.Entry<String, Object>> entrySet() {
        Set<Map.Entry<String, Object>> res = new HashSet<Map.Entry<String, Object>> ();
        for (String name: this.registry.keySet()) {
            if (getAttribute(name).isMemoized) {
                res.add(new TradeAttributeEntry(name, get(name)));
            }
        }
        return res;
    }

    public static class TradeAttributeEntry implements Map.Entry<String, Object> {

        private String key;
        private Object value;

        public TradeAttributeEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }


        public String getKey() {
            return key;
        }


        public Object getValue() {
            return value;
        }


        public Object setValue(Object value) {
            this.value = value;
            return value;
        }
    }



    public Collection<ValidationResult> validate() {
        Collection<ValidationResult> res = new LinkedList<ValidationResult>();
        for (String s: this.tradeDocument.values()) {
            if (this.registry.containsKey(s)) {
                TradeAttribute t = this.getAttribute(s);
                res.add(t.validate());
            }
        }
        return res;
    }

    public Object putMetadata(String key, Object value) {
        return this.metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }
}

