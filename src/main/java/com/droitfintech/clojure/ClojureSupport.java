package com.droitfintech.clojure;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handy utils for java people trying to use clojure.
 * Created by roytruelove on 12/14/15.
 */
public class ClojureSupport {

    private static Logger log = LoggerFactory.getLogger(ClojureSupport.class);

    private static IFn requireFn = Clojure.var("clojure.core", "require");

    public static <T> T getInterfaceInstance(String namespace, String factoryFn, Class<T> targetInterface) {

        T answer;

        IFn factory = getFunction(namespace, factoryFn);

        log.debug("Creating spring bean of type '{}' from clojure.  Namespace: {}, factoryFn: {}",
                targetInterface.getCanonicalName(), namespace, factoryFn);

        answer = (T) factory.invoke();

        return answer;

    }

    public static Keyword stringToKeyword(String toKeyword) {
        IFn keywordizer = getFunction("clojure.core", "keyword");
        return (Keyword) keywordizer.invoke(toKeyword);
    }

    public static Object invokeClojureFunction(String namespace, String fnName, Object... vars) {

        Object answer = null;

        IFn fn = getFunction(namespace, fnName);

        if (vars == null || vars.length == 0)  {
           return fn.invoke();
        } else {
            return fn.invoke(vars);
        }
    }

    public static IFn getFunction(String namespace, String fnName) {

        loadNamespace(namespace);
        return Clojure.var(namespace, fnName);
    }

    private static void loadNamespace(String namespace) {

        log.trace("Loading {} namespace started...", namespace);

        // Revisit - do we need to require every time?  OK for now since this is
        // only really used at startup / tests
        requireFn.invoke(Clojure.read(namespace));

        log.trace("Loading {} namespace finished", namespace);

    }
}
