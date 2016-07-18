package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.FSDictionary;
import com.droitfintech.workflow.exceptions.WorkflowCircularReferenceException;
import com.droitfintech.workflow.internal.FlowchartModule;
import com.droitfintech.workflow.internal.TableModule;
import com.droitfintech.workflow.internal.repository.Module;
import com.droitfintech.workflow.repository.FilesystemWorkflowRepositoryService;
import org.apache.commons.lang3.StringUtils;

/**
 * This class implements module validation and wraps module loading for the recursive  validation process.
 * The module passed into the constructor is the one being validated. As other modules are referenced
 * in the validation process it checks for references back to the original module and throws a
 * WorkflowCircularReferenceException if it finds a reference back to the original module.
 */

public class ModuleValidator {
    FilesystemWorkflowRepositoryService repository;
    Module startOfValidation;

    public ModuleValidator(FilesystemWorkflowRepositoryService repository, Module startOfValidation) {
        this.repository = repository;
        this.startOfValidation = startOfValidation;
    }

    /**
     * Find the latest version of a module in a droit or client snapshot
     * @param dataDict
     * @param snapshotId client or droit snapshot id.
     * @param modulePrefix Module name including the trailing dot but without branch and version
     * @return
     */
    public Module findLatestModuleVersionInSnapshot(String snapshotId, String modulePrefix) {
        if(modulePrefix.equals(startOfValidation.getMetadata().getEntityName() + ".")) {
            throw new WorkflowCircularReferenceException("Expression contains circular reference to module "
                + startOfValidation.getMetadata().getEntityName());
        }
        return repository.findLatestModuleVersionInSnapshot(snapshotId, modulePrefix);

    }

    /**
     * Perform full module validation returning errors, warnings and the full set of outputs the module produces.
     * @param module
     * @param dict
     * @return
     */
    public ValidationResponse validateModule(Module module, FSDictionary dict, String snapshotId) {
        ValidationResponse ret = new ValidationResponse(true, module);

        if(module instanceof TableModule) {
            ret = TableModuleValidator.validate((TableModule) module);
        }
        else if(module instanceof FlowchartModule) {
            ret = FlowchartModuleValidator.validate((FlowchartModule) module, dict, snapshotId, this);
        }
        if (StringUtils.isBlank(module.getLabel())) {
            ret.missingTitle = true;
        }
        ret.setOkToActivate(module);
        return ret;
    }

}
