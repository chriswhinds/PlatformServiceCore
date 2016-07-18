package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */
import com.droitfintech.model.TradeAttribute.UnmetDependencyException;

import java.util.Collection;

/**
 * The interface for wrappers around various trade sources.
 * The wrappers should be lightweight so that they can be created and discarded for each trade in the request.
 *
 * @author jisoo
 *
 */
public interface TradeDocument {

    /**
     * Fetch a value in the trade document corresponding to the key supplied. If the value is not found,
     * this method should throw an exception.
     *
     * @param attributeName the canonical Droit name of the attribute
     * @return the matching value, if any
     * @throws UnmetDependencyException if the key does not exist in the underlying document
     */
    String get(String attributeName) throws UnmetDependencyException;

    /**
     * Return attribute source location information given an attribute name.
     * This is useful for reporting errors when messages are received with
     * different external languages.
     *
     * @param attributeName the canonical Droit name of the attribute
     * @return the source information, as a string.
     */
    String source(String attributeName);

    /**
     * Retrieve the collection of Droit attributes that have been bound in this document.
     *
     * @return the collection of bound values.
     */
    Collection<String> values();
}

