package com.droitfintech.clojure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.Map;

public class TypeConverter {

    static {
        IFn load = Clojure.var("clojure.core", "load");
        load.invoke("/tyconv");
    }

    public static Map convertMap(Map input) {
        return (Map) convertObject(input);
    }

    public static Object convertObject(Object input) {
        IFn conv = Clojure.var("tyconv.core", "convert");
        if (conv == null) {
            return input;
        }
        return conv.invoke(input);
    }
 }
