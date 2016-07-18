package com.droitfintech.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeepCopyIgnore {

    public enum Type {
        NULL,
        REFERENCE
    }

    public Type type();

}
