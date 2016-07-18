package com.droitfintech.datadictionary;

import com.droitfintech.datadictionary.attribute.*;
import com.droitfintech.exceptions.NoSuchNamespaceException;

import java.util.Collection;
import java.util.Set;


/**
 * Created by christopherwhinds on 7/7/16.
 */
public interface Dictionary {

    Attribute findAttribute(String Scope, String namespace, String attribute) throws AttributeUndefinedError, NoSuchNamespaceException;

    Attribute findAttributeByInstanceName(String Scope, String namespace, String attribute);

    Collection<Attribute> findAttributesTagged(String tag);

    Attribute defineAttribute(String scope, String namespace, String name, Type type, Cardinality cardinality);

    Collection<Namespace> listNamespaces();

    Collection<String> listTags();

    Collection<Attribute> listAttributes(String scope, String namespace);

    Namespace getNamespaceByInstanceName(String scope, String instanceName);

    Namespace getNamespace(String scope, String namespace);

    Set<String> getTypeNames(String scope);

    Set<String> getEnumeratedStringValues(String scope, String typeName);

    Collection<TaxonomyPoint> getTaxonomyPoints();

}