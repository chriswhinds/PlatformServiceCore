package com.droitfintech.exceptions;

/**
 * Created by christopherwhinds on 7/7/16.
 */
public class NoSuchNamespaceException extends Exception {
    String namespaceName;

    public NoSuchNamespaceException(String namespace) {
        this.namespaceName = namespace;
    }
}

