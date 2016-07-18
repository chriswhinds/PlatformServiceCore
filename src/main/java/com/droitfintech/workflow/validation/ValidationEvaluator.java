package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.FSDictionary;
import com.droitfintech.datadictionary.attribute.Namespace;
import com.droitfintech.datadictionary.rule.DictionaryConstants;
import com.droitfintech.workflow.exceptions.WorkflowCircularReferenceException;
import com.droitfintech.workflow.internal.Evaluator;
import com.droitfintech.workflow.internal.repository.Module;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Evaluator used in workflow evaluation performed during module validation.
 * It supplies sample values based on dictionary types
 */
public class ValidationEvaluator implements Evaluator {

    private ValidationBucketedMap bMap = new ValidationBucketedMap<String, Object>();
    private FSDictionary dictionary;
    private String snapshotId;
    private ModuleValidator repository;
    private Map<String, ValidationResponse> moduleCache;
    private String lastCollectionAccessed = "";

    public ValidationEvaluator(FSDictionary dictionary, String snapshotId, ModuleValidator repository,
                               Map<String, ValidationResponse> moduleCache) {
        this.dictionary = dictionary;
        this.snapshotId = snapshotId;
        this.repository = repository;
        this.moduleCache =  moduleCache;
    }


    public String getLastCollectionAccessed() {
        return lastCollectionAccessed;
    }


    public Map<String, Object> get(String moduleName) {
        lastCollectionAccessed = moduleName;
        //logger.debug("Calling ValidationEvaluator get(" + moduleName + ")");
        for(Namespace ns : dictionary.listNamespaces()) {
            if(ns.getScope().equals(DictionaryConstants.SCOPE_DROIT)) {
                for(String name : ns.getInstanceNames()) {
                    if(moduleName.equals(name) ) {
                        return new ValidationBucketedMap<String, Object>(
                                ns,
                                dictionary.getNamespaceByInstanceName(DictionaryConstants.SCOPE_CLIENT, name));
                    }
                }
            }
        }
        ValidationResponse vr = moduleCache.get(moduleName);
        if(vr == null) {
            Module module = repository.findLatestModuleVersionInSnapshot(snapshotId, moduleName + ".");
            if (module != null) {
                vr = repository.validateModule(module, dictionary, snapshotId);
                moduleCache.put(moduleName, vr);
                // re throw any circular reference exception so it propagates up to the top level of the validation.
                if(vr.hasCircularReference) {
                    throw new WorkflowCircularReferenceException("Sub module " + moduleName + " contains circular reference");
                }
                return new ValidationBucketedMap<String, Object>(vr.getOutputs());
            }
        } else {
            return new ValidationBucketedMap<String, Object>(vr.getOutputs());
        }
        return bMap;
    }


    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        return amount;
    }
}
