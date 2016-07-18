package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.attribute.Cardinality;
import com.droitfintech.datadictionary.attribute.Type;
import com.droitfintech.workflow.internal.TableModule;
import com.droitfintech.workflow.internal.repository.Module;

import java.util.*;

/**
 * Serialized version of this class is returned by validateModule.do to the angular controller so it can report errors.
 * All members are public so avoid having to generate getters/setters
 * Created by barry on 10/15/15.
 */
public class ValidationResponse {
    // This is the exact format required to display errors and warnings in the ace code editor
    public static class GroovyError {
        public int row;
        public int column;
        public String text;
        public String type;

        public GroovyError(int row, int column, String text, String type) {
            this.row = row;
            this.column = column;
            this.text = text;
            this.type = type;
        }
    }

    public class NodeError {
        public String nodeName;
        public ArrayList<GroovyError> groovyErrors = new ArrayList<GroovyError>();
        public boolean missingTrueRef = false;
        public boolean missingFalseRef = false;
        public boolean missingDescription = false;
        public boolean duplicateNodeName = false; // normally handled by the GUI but double checked in the backend.
        public boolean circularReference = false;

        public NodeError(String nodeName) {
            this.nodeName = nodeName;
        }
    }

    public static class TableRowError {
        public int rowNumber;
        public String errorMessage;

        public TableRowError(int rowNumber, String errorMessage) {
            this.rowNumber = rowNumber;
            this.errorMessage = errorMessage;
        }
    }
    public String  moduleName; // name without branch and version
    public String  moduleTaxonomyPoint = "";
    public boolean okToSave = false;
    public boolean noErrorsFound = false;
    public ArrayList<GroovyError> defOutputErrors = new ArrayList<GroovyError>();
    public HashMap<String, NodeError> errorsByNode = new HashMap<String, NodeError>();
    public ArrayList<TableRowError> tableModuleErrors = new ArrayList<TableRowError>();
    public boolean missingTitle  = false;
    public boolean missingDescription = false;
    public boolean missingStartNode = false;
    public boolean missingEmbeddedModule = false;
    public boolean hasCircularReference = false;
    TreeSet<DecisionElement> outputVariables;

    public ValidationResponse(boolean okToSave, Module module) {
        this.moduleName = module.getMetadata().getEntityName();
        this.moduleTaxonomyPoint = ModuleUtils.getTaxonomy(module);
        this.okToSave = okToSave;
    }

    /**
     * This sets noErrorsFound to true if there was nothing wrong and it's safe to include the module in the
     * snapshot.
     */
    public void setOkToActivate(Module module) {
        if(module instanceof TableModule) {
            if(missingDescription || missingTitle || !tableModuleErrors.isEmpty()) {
                noErrorsFound = false;
            } else {
                noErrorsFound = true;
            }
            okToSave = true; // always OK to save these.
        }
        else {
            if (missingDescription || missingEmbeddedModule || missingStartNode || missingTitle
                    || !defOutputErrors.isEmpty() || !errorsByNode.isEmpty()) {
                noErrorsFound = false;
            } else {
                noErrorsFound = true;
            }
        }
    }

    public void addNodeGroovyError(String nodeName, int row, int column, String text, String type) {
        NodeError ne = errorsByNode.get(nodeName);
        if(ne == null) {
            ne = new NodeError(nodeName);
            errorsByNode.put(nodeName, ne);
        }
        ne.groovyErrors.add(new GroovyError(row, column,text, type));
    }

    public void addNodeGroovyError(String nodeName, Collection<GroovyError> errorList) {
        if(errorList != null) {
            NodeError ne = errorsByNode.get(nodeName);
            if(ne == null) {
                ne = new NodeError(nodeName);
                errorsByNode.put(nodeName, ne);
            }
            ne.groovyErrors.addAll(errorList);
        }
    }

    /**
     * Add table module errors capping the list at 10 to prevent filling the screen displaying the errors.
     * @param err
     */
    public void addTableModuleError(TableRowError err) {
        if(tableModuleErrors.size() <= 10) {
            tableModuleErrors.add(err);
        }
    }

    public void addDefaultOutputError(int row, int column, String text, String type) {
        defOutputErrors.add(new GroovyError(row, column,text, type));
    }

    public void addDefaultOutputError(Collection<GroovyError> errorList) {
        if(errorList != null)
            defOutputErrors.addAll(errorList);
    }

    public NodeError getNodeError(String nodeName) {
        NodeError ne = errorsByNode.get(nodeName);
        if(ne == null) {
            ne = new NodeError(nodeName);
            errorsByNode.put(nodeName, ne);
        }
        return ne;
    }

    public void addOutputVariables(Map<String, Object> outputMap) {
        if(outputVariables == null) {
            outputVariables = new TreeSet();
        }
        if(outputMap != null) {
            for (String var : outputMap.keySet()) {
                Object value = outputMap.get(var);
                if(value != null) {
                    Cardinality cardinality = Cardinality.One;
                    Object valueForType = value;
                    if(value instanceof Collection) {
                        cardinality = Cardinality.Many;
                        Collection c = (Collection)value;
                        if(c.iterator().hasNext()) {
                            valueForType = c.iterator().next();
                        }
                    }
                    DecisionElement de = new DecisionElement(moduleName, var, cardinality, Type.getTypeForObject(valueForType), value);
                    de.setElementTaxonomyPoint(moduleTaxonomyPoint);
                    // Don't replace empty strings.
                    if(value instanceof String) {
                        if( (((String) value).length() == 0 && !outputVariables.contains(de) )
                            || ((String) value).length() > 0) {
                            outputVariables.remove(de);
                            outputVariables.add(de);
                        }
                    } else {
                        outputVariables.add(de);
                    }
                }
            }
        }
    }

    public Set<DecisionElement> getOutputs() {
        return outputVariables;
    }
}
