package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.attribute.Cardinality;
import com.droitfintech.datadictionary.attribute.Type;
import com.droitfintech.workflow.internal.repository.Module;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Represents a reference to a variable parsed from an expression
 * Created by barry on 8/26/15.
 */
public class DecisionElement implements Comparable {

    private String elementName;
    private String variableName;
    private TreeMap<String, Module> referencingModules;
    private Type variableType = null;
    private Cardinality cardinality = Cardinality.One;
    private Object variableValue = null;
    private String elementTaxonomyPoint = "";

    public DecisionElement(String elementName, String variableName) {
        this.elementName = elementName;
        this.variableName = variableName;
        referencingModules = new TreeMap<String, Module>();
    }

    public DecisionElement(String elementName, String variableName, Cardinality cardinality, Type variableType, Object variableValue) {
        this.elementName = elementName;
        this.variableName = variableName;
        referencingModules = new TreeMap<String, Module>();
        this.cardinality = cardinality;
        this.variableType = variableType;
        this.variableValue = variableValue;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void addReferencingModule(Module module) {
        referencingModules.put(module.getMetadata().getId(), module);
    }

    public Collection<String> getReferencingModuleNames() {
        return referencingModules.keySet();
    }

    public TreeMap<String, Module> getReferencingModules(){
        return referencingModules;
    }

    public Type getVariableType() {
        return variableType;
    }

    public void setVariableType(Type variableType) {
        this.variableType = variableType;
    }

    public Object getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(Object variableValue) {
        this.variableValue = variableValue;
    }

    public String getElementTaxonomyPoint() {
        return elementTaxonomyPoint;
    }

    public void setElementTaxonomyPoint(String elementTaxonomyPoint) {
        this.elementTaxonomyPoint = elementTaxonomyPoint;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DecisionElement that = (DecisionElement) o;

        if (!elementName.equals(that.elementName)) return false;
        return variableName.equals(that.variableName);

    }

    @Override
    public int hashCode() {
        int result = elementName.hashCode();
        result = 31 * result + variableName.hashCode();
        return result;
    }


    public int compareTo(Object o) {
        if (this == o) return -1;
        if (o == null || getClass() != o.getClass()) return 1;
        DecisionElement that = (DecisionElement) o;
        int rc = elementName.compareTo(that.elementName);
        if(rc == 0) {
            rc = variableName.compareTo(that.variableName);
        }
        return rc;
    }
}
