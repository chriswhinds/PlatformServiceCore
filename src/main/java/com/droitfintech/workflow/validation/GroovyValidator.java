package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.FSDictionary;
import com.droitfintech.workflow.exceptions.ParseDateWorkflowException;
import com.droitfintech.workflow.exceptions.WorkflowCircularReferenceException;
import com.droitfintech.workflow.internal.StatelessScript;
import com.droitfintech.workflow.internal.groovy.GroovyParser;
import com.droitfintech.workflow.internal.groovy.ReferenceCollector;
import com.droitfintech.workflow.internal.repository.Module;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to compile and execute groovy code for module validation. It reports errors back in a format that can be displayed
 * in the ace editor widget. Exceptions thrown in groovy code executions are translated into hopefully meaningfull
 * errors displayed back in the editor.
 */
public class GroovyValidator {
    public static final Logger logger = LoggerFactory.getLogger(GroovyValidator.class);

    private String snapshotId;
    private FSDictionary dictionary;
    private ModuleValidator repository;
    private GroovyShell shell = new GroovyShell();
    private static GroovyClassLoader gcl = new GroovyClassLoader();
    private Map<String, ValidationResponse> moduleCache = new HashMap<String, ValidationResponse>();
    private String moduleTaxonomy;
    private String moduleName;

    public GroovyValidator(Module module, FSDictionary dictionary, String snapshotId, ModuleValidator repository) {
        this.dictionary = dictionary;
        this.snapshotId = snapshotId;
        this.repository = repository;
        this.moduleTaxonomy = ModuleUtils.getTaxonomy(module);
        this.moduleName = module.getName();
    }

    private int getLineNumberFronStackTrace(Exception ex) {
        StackTraceElement[] stack = ex.getStackTrace();
        if(stack != null) {
            for(int idx = 0; idx < stack.length; idx++) {
                if("doExecute".equals(stack[idx].getMethodName())) {
                    return stack[idx].getLineNumber() - 2;
                }
            }
        }
        return(0);
    }

    public SnipitResponse validateSnipit(String code) {
        SnipitResponse ret = new SnipitResponse();
        try {
            String script = StatelessScript.createStatelessScriptWithExpression(code);
            shell.parse(code);
            Class clazz = gcl.parseClass(script);
            StatelessScript sScript =  (StatelessScript) clazz.newInstance();
            Object output = sScript.doExecute(new ValidationEvaluator(dictionary, snapshotId, repository, moduleCache), null);
            ret.setSnipetReturn(output);
            if(StringUtils.isNotBlank(moduleTaxonomy)) {
                ReferenceCollector reCollector = new ReferenceCollector();
                for( ReferenceCollector.Reference ref : reCollector.getReferences(GroovyParser.parse(new ByteArrayInputStream(code.getBytes())))) {
                    if(!ref.getLhs().equals("trade") && !ref.getLhs().equals("counterparty") &&  !ref.getLhs().equals("contraparty")) {
                        if(this.moduleName.equals(ref.getLhs())) {
                            ret.addNodeGroovyError(ref.getLine()-1, ref.getColumn()-1, "Circular reference to the current module", "error", true, true);
                        } else {
                            ValidationResponse vResp = moduleCache.get(ref.getLhs());
                            if (vResp == null) {
                                Module module = repository.findLatestModuleVersionInSnapshot(snapshotId, ref.getLhs() + ".");
                                if (module != null) {
                                    vResp = repository.validateModule(module, dictionary, snapshotId);
                                    moduleCache.put(ref.getLhs(), vResp);
                                }
                            }
                            if (vResp != null) {
                                if (!ModuleUtils.isInModulesTaxonomy(moduleTaxonomy, vResp.moduleTaxonomyPoint)) {
                                    ret.addNodeGroovyError(ref.getLine() -1, ref.getColumn() -1, "Module " + ref.getLhs() + " is outside of current modules taxonomy", "error", false, true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (MultipleCompilationErrorsException ex) {
            logger.trace("Compile error ", ex);
            Object message = ex.getErrorCollector().getErrors().iterator().next();
            if (message instanceof SyntaxErrorMessage) {
                SyntaxErrorMessage sem = (SyntaxErrorMessage) message;
                SyntaxException se = sem.getCause();
                ret.addNodeGroovyError(se.getLine() - 1, se.getStartColumn() - 1, se.getMessage(), "error", true, false);
            }
        } catch (Exception ex) {
            if(ex instanceof MissingPropertyException) {
                MissingPropertyException mpe = (MissingPropertyException)ex;
                int line = 0;
                String[] lines = code.split("\n");
                for(; line < lines.length; line++) {
                    if(lines[line].contains(mpe.getProperty()))
                        break;
                }
                ret.addNodeGroovyError(line, 0, "Invalid property '" + mpe.getProperty() + "' Reference payload properties using 'd.<object>.<property>'", "error", false, true);
            }
            else if(ex instanceof MissingMethodException) {
                ret.addNodeGroovyError(getLineNumberFronStackTrace(ex), 0, ex.getMessage(), "error", false, true);
            } else if(ex instanceof ParseDateWorkflowException) {
                ParseDateWorkflowException pde = (ParseDateWorkflowException)ex;
                int line = 0;
                String[] lines = code.split("\n");
                for(; line < lines.length; line++) {
                    if(lines[line].contains(pde.getDateString()))
                        break;
                }
                ret.addNodeGroovyError(line, 0, "Invalid value " + pde.getDateString() + " passed to parseDate", "error", false, true);
            }
            else if(ex instanceof WorkflowCircularReferenceException) {
                ret.addNodeGroovyError(0,0, ex.getMessage(), "error", true, true);
                ret.setHasCircularReference(true);
            }
            else {
                logger.debug("Unprocessed runtime exception ", ex);
                ret.addNodeGroovyError(getLineNumberFronStackTrace(ex), 0, ex.getMessage(), "error", false, true);
            }
        }
        return ret;
    }
}
