package com.droitfintech.datadictionary;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.datadictionary.Dictionary;
import com.droitfintech.datadictionary.attribute.Attribute;
import com.droitfintech.datadictionary.attribute.Cardinality;
import com.droitfintech.datadictionary.attribute.DefaultServiceLookup;
import com.droitfintech.datadictionary.attribute.Namespace;
import com.droitfintech.datadictionary.rule.DictionaryConstants;
import com.droitfintech.model.*;
import com.droitfintech.workflow.repository.WorkflowRepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by barry on 10/8/15.
 */

public class DictionaryTradeAttributeRegistry  {
    private final static Logger logger = LoggerFactory.getLogger(DictionaryTradeAttributeRegistry.class);


    private DefaultService defaultService;

    private WorkflowRepositoryService workflowRepositoryService;

    private DictionaryTradeAttributeRegSupport support;


    public TradeContext initializeContext(TradeDocument document) {
        final String[] namespaces = {"trade"};
        return initializeContext(document, namespaces);
    }

    public Dictionary getDataDictionary() {
        return workflowRepositoryService.getDataDictionaryForLiveSnapshot();
    }

    public TradeContext initializeContext(TradeDocument document, String[] namespaces){
        // validators could become class members constructed once but I want change to the data dictionary to propagate immediately so
        // leaving them allocated here for now.

        TradeContext context = new TradeContext(this.defaultService, document);
        ArrayList<String> scopes = new ArrayList<String>(2);

        scopes.add(DictionaryConstants.SCOPE_DROIT);
        scopes.add(DictionaryConstants.SCOPE_CLIENT);

        Dictionary dictionary = workflowRepositoryService.getDataDictionaryForLiveSnapshot();

        HashMap<String, Validator> validators = support.populateValidators(dictionary);

        // for every namespace..
        for(int nsIdx = 0; nsIdx < namespaces.length; nsIdx++) {

            // for every scope..
            for(String scope : scopes) {
                Namespace namespace = dictionary.getNamespaceByInstanceName(scope, namespaces[nsIdx]);
                if("droit".equals(scope)) {
                    DroitException.assertNotNull(namespace, "Could not find namespace '" + namespaces[nsIdx] +
                            "' in scope '" + scope + "'");
                } else if(namespace == null) {
                    continue;
                }
                for (Attribute dictAttr : namespace.getAttributes().values()) {
                    try {

                        String[] validationParts = StringUtils.split(dictAttr.getValidation(), ':');
                        Validator validator1 = null;

                        if (validationParts.length == 2) {
                            validator1 = validators.get(validationParts[1]);
                        } else if ("TenorType".equals(dictAttr.getType())) {
                            validator1 = new TenorValidator();
                        }
                        Validator validator = validator1;

                        // This POS adds itself to the TradeContext in the constructor.
                        StandardAttribute attr = new StandardAttribute(
                                dictAttr.getName(), context, dictAttr.getTypeClass(), //support.getClassFor(dictAttr.getType().className),
                                dictAttr.getCardinality() == Cardinality.Many,
                                validator,
                                dictAttr);

                        if (!(dictAttr.getCachedLinkedDefaultField() == null)) {
                            attr.addLinkedAttributes(dictAttr.getCachedLinkedDefaultField());
                        } else {
                            if (!StringUtils.isBlank(dictAttr.getLinkedDefaultField())) {
                                Set<String> cashedlinkedDefaultField = ModelConversionUtil.makeSet(String.class, dictAttr.getLinkedDefaultField());
                                dictAttr.setCachedLinkedDefaultField(
                                        cashedlinkedDefaultField);
                                attr.addLinkedAttributes(cashedlinkedDefaultField);
                            }
                        }
                        if (!StringUtils.isBlank(dictAttr.getCalculationFunction())) {
                            if(dictAttr.getCachedCalculationFunction() != null) {
                                attr.addCalculationFunction((StandardAttributeFunction) dictAttr.getCachedCalculationFunction());
                            } else {
                                try {
                                    StandardAttributeFunction func = support.createCalculationFunction(dictAttr);
                                    attr.addCalculationFunction(func);
                                    dictAttr.setCachedCalculationFunction(func);
                                } catch (Exception ex) {
                                    // TODO ! This needs to throw an error, not just warn.
                                    // See https://www.pivotaltracker.com/story/show/107992438
                                    logger.warn("Did not add a calc function for {} : {}.  Continuing",
                                            dictAttr.getName(), attr.getName());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw new DroitException("Error creating trade attribute", ex);
                    }
                }
            }
        }
        return context;
    }


}
