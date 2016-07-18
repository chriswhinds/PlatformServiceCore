package com.droitfintech.datadictionary;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.StandardAttributeFunction;
import com.droitfintech.model.Validator;
import com.droitfintech.regulatory.Tenor;
import com.droitfintech.datadictionary.Dictionary;
import com.droitfintech.datadictionary.attribute.Attribute;
import com.droitfintech.datadictionary.rule.DictionaryConstants;
import com.droitfintech.model.IsBound;
import com.droitfintech.model.ModelConversionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Most of these methods were in the original DictionaryTradeAttributeRegistry, but they had to be moved
 * here because of this: http://stackoverflow.com/a/10347208/295797
 */
public class DictionaryTradeAttributeRegSupport {

    private static Logger log = LoggerFactory.getLogger(DictionaryTradeAttributeRegSupport.class);


    //    @Cacheable("dictionaryTradeAttributeRegistry.createCalculationFunction")
    public StandardAttributeFunction createCalculationFunction(Attribute dictAttr)
            throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<? extends StandardAttributeFunction> funcClass =
                (Class<? extends StandardAttributeFunction>) getClassFor(dictAttr.getCalculationFunction());
        StandardAttributeFunction f;
        String calculationParameters = dictAttr.getCalculationParameters();
        String[] parmArray = calculationParameters.split("\"");
        if (parmArray.length > 1) {
            // Many funcs take a list of attribute names as a constructor arg
            String val = parmArray[1];
            List<String> parameters = ModelConversionUtil.makeList(String.class, val);
            if (funcClass.equals(IsBound.class)) {
                // IsBound may take additional optional inputs
                String returnValueIfBound = null;
                String anyOrAll = null;
                if (parmArray.length > 2 && !StringUtils.isBlank(parmArray[2])) {
                    String[] additionalParms = parmArray[2].trim().split("\\s+");
                    if (additionalParms.length > 0)
                        returnValueIfBound = additionalParms[0];
                    if (additionalParms.length > 1)
                        anyOrAll = additionalParms[1];
                }
                f = new IsBound(parameters, returnValueIfBound, anyOrAll);
            } else {
                // Most funcs take one arg, of type List<String>
                f = funcClass.getConstructor(List.class).newInstance(parameters);
            }
        } else {
            // Some funcs do not need any args
            f = funcClass.newInstance();
        }
        return f;
    }


    public HashMap<String, Validator> populateValidators(Dictionary dictionary) {
        HashMap<String, Validator> validators = new HashMap<String, Validator>();
        for(String type : dictionary.getTypeNames(DictionaryConstants.SCOPE_DROIT)) {
            validators.put(type, new DataDictionaryValidator(type,dictionary.getEnumeratedStringValues(DictionaryConstants.SCOPE_DROIT, type)));
        }
        return validators;
    }

    // This code is run many many times and can run slowly, so we're hardcoding
    // the majority of the calls, and then caching whatever's left over.  If
    // more functions are added they should be listed here to avoid cache hits.
    public Class<?> getClassFor(String className) {
        Class answer = null;
        if (answer == null) {
            try {
                answer = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new DroitException("Could not find class named " + className, e);
            }
        }
        return answer;
    }


}
