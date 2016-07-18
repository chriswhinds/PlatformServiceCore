package com.droitfintech.model;

/**
 * Created by christopherwhinds on 7/8/16.
 */
import java.util.Collection;
import java.util.HashSet;

import com.droitfintech.exceptions.DroitException;

public interface Validatable {
    ValidationResult validate();

}
