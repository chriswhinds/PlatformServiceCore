package com.droitfintech.model;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.droitfintech.utils.ThreadSafeSimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class DynamicDocument implements TradeDocument {

    private ThreadSafeSimpleDateFormat formatter =
            new ThreadSafeSimpleDateFormat("yyyy-MM-dd'T'HH:mmZZ");

    private Map documentMap;

    public DynamicDocument(Map documentMap) {
        this.documentMap = documentMap;
    }


    public String get(String attributeName) throws TradeAttribute.UnmetDependencyException {
        if (!documentMap.containsKey(attributeName)) {
            throw new TradeAttribute.UnmetDependencyException(
                    "Key not found or document map does not contain the specified key",
                    attributeName);
        }
        Object o = documentMap.get(attributeName);
        if (o == null) {
            return ClassConverter.NULL_SPECIAL_VALUE;
        } else if (o instanceof Date) {
            return formatter.format((Date) o);
        } else {
            return o.toString();
        }
    }


    public String source(String attributeName) {
        if (documentMap.containsKey(attributeName)) {
            return attributeName;
        }
        return null;
    }


    public Collection<String> values() {
        return null;
    }
}

