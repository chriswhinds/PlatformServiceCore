package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.FSDictionary;
import com.droitfintech.workflow.internal.FlowchartModule;
import com.droitfintech.workflow.internal.FlowchartNode;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/***
 * Created by Chris Hinds  , Imported from OldBox code base
 */
public class FlowchartModuleValidator {
    public static ValidationResponse validate(FlowchartModule module, FSDictionary dict, String snapshotId, ModuleValidator repository) {
        ValidationResponse ret = new ValidationResponse(true, module);
        GroovyValidator validator = new GroovyValidator(module, dict, snapshotId, repository);
        final Pattern validEscalationFormat = Pattern.compile("^[a-zA-Z0-9_]+:[a-zA-Z0-9_]+:[0-9]+$");

        String code = module.getDefaults();
        if(code != null && !code.isEmpty()) {
            SnipitResponse resp = validator.validateSnipit(code);
            ret.addDefaultOutputError(resp.getErrors());
            if(resp.hasSyntaxError() || resp.getHasCircularReference())
                ret.okToSave = false;
            else {
                ret.addOutputVariables(resp.getOutputMap());
            }
            // record any circular reference at the module level.
            ret.hasCircularReference = resp.getHasCircularReference();
        } else {
            ret.addDefaultOutputError(0, 0, "default outputs must be specified", "warning");
        }
        HashSet<String> nodenames = new HashSet<String>();
        for(FlowchartNode fn :  module.getNodes()) {
            code = fn.getExpr();
            SnipitResponse resp = null;
            if (fn.getType() != FlowchartNode.NodeType.chain && StringUtils.isNotBlank(code)) {
                resp = validator.validateSnipit(code);
                ret.addNodeGroovyError(fn.getName(), resp.getErrors());
                if(resp.getHasCircularReference()) {
                    ret.okToSave = false;
                    // record any circular reference at the module level.
                    // if called from validation of a parent module this will cause the caller to throw
                    // a CircularReference exception.
                    ret.hasCircularReference = true;
                }
            } else {
                if (fn.getType() == FlowchartNode.NodeType.condition) {
                    ret.addNodeGroovyError(fn.getName(), 0, 0, "condition must be specified", "warning");

                }
                if (fn.getType() == FlowchartNode.NodeType.termination)
                    ret.addNodeGroovyError(fn.getName(), 0, 0, "output must be specified", "warning");
            }
            // condition nodes need both true and false
            if (fn.getType() == FlowchartNode.NodeType.condition) {
                if (resp != null && resp.hasOutput()) {
                    Boolean outputValue = resp.getOutputBoolean();
                    if (outputValue == null) {
                        ret.addNodeGroovyError(fn.getName(), 0, 0, "output of condition node must be a boolean value.", "error");
                    }
                } else {
                    ret.addNodeGroovyError(fn.getName(), 0, 0, "condition node must end with a boolean expression", "warning");
                }
                if (StringUtils.isBlank(fn.getTrueRef())) {
                    ret.getNodeError(fn.getName()).missingTrueRef = true;
                }
                if (StringUtils.isBlank(fn.getFalseRef())) {
                    ret.getNodeError(fn.getName()).missingFalseRef = true;
                }
            } else if (fn.getType() == FlowchartNode.NodeType.termination) {
                if (resp != null && resp.hasOutput()) {
                    Map<String, Object> outputValue = resp.getOutputMap();
                    if (outputValue == null) {
                        ret.addNodeGroovyError(fn.getName(), 0, 0, "termination node must end with a map containing string keys.", "error");
                    } else {
                        ret.addOutputVariables(outputValue);
                        if(outputValue.containsKey("escalation")) {
                            String escalationString = (String)outputValue.get("escalation");
                            Matcher matcher = validEscalationFormat.matcher(escalationString);
                            if (!matcher.find()) {
                                reportEscalationFormatError(fn, "escalation", ret, "escalation format invalid. Format is '<reg>:<mandate>:<#>'");
                            }
                        }
                    }
                }
            }
            // all nodes need these
            if (StringUtils.isBlank(fn.getDescription())) {
                ret.getNodeError(fn.getName()).missingDescription = true;
            }
            if(nodenames.contains(fn.getName())) {
                ret.getNodeError(fn.getName()).duplicateNodeName = true;
                ret.okToSave = false; // this is serious so block saving entirely.
            } else {
                nodenames.add(fn.getName());
            }
        }
        if(StringUtils.isBlank(module.getInitRef())) {
            ret.missingStartNode = true;
        }
        return ret;
    }

    private static void reportEscalationFormatError(FlowchartNode fn, String toMatch, ValidationResponse vr, String message) {
        if(fn.getExpr().contains(toMatch)) {
            String[] lines = fn.getExpr().split("\\n");
            for(int lIdx = 0; lIdx < lines.length; lIdx++) {
                if(lines[lIdx].contains(toMatch)) {
                    vr.addNodeGroovyError(fn.getName(), lIdx,1, message, "warning");
                }
            }
        }
    }



}
