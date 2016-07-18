package com.droitfintech.workflow.internal.groovy;

import groovyjarjarantlr.collections.AST;

/**
 * This interface represents a Groovy code tree.
 * Created by barry on 11/25/15.
 */
public interface SourceTree {
    String[] getTokenNames();

    AST getAST();
}
