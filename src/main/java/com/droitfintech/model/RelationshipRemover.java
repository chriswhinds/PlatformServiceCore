package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationshipRemover {
    Class<?> paramClass();
}
