package com.droitfintech.workflow.internal.repository;

import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Module.java (C) 2014 Droit Financial Technologies, LLC
 * 
 * @author nathanbrei
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = FlowchartModule.class, name = "flowchart"),
		@Type(value = TableModule.class, name = "table"),
		@Type(value = ParameterizedModule.class, name = "parameterized"),
		@Type(value = CsvModule.class, name ="csv")
})
public class Module {

	private String name, label, description;
	private List<String> tags;

	private List<ModuleVariable> inputs = new LinkedList<ModuleVariable>();
	private List<ModuleVariable> outputs = new LinkedList<ModuleVariable>();

	private String version;
	private VersionMetadata metadata;

	public ExecutableModule createExecutable() {

		if (this instanceof ParameterizedModule) {
			return new ExecutableParameterizedModule((ParameterizedModule) this);
		}
		else if (this instanceof FlowchartModule) {
			return new ExecutableFlowchartModule((FlowchartModule) this);
		}
		else if (this instanceof TableModule) {
			return new ExecutableDataFrame((TableModule) this);
		}
		else if (this instanceof CsvModule) {
			return new ExecutableDataFrame((CsvModule) this);
		}
		else {
			throw new WorkflowException("Unrecognized module type: " + this.getName());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<ModuleVariable> getInputs() {
		return inputs;
	}

	public void setInputs(List<ModuleVariable> inputs) {
		this.inputs = inputs;
	}

	public List<ModuleVariable> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ModuleVariable> outputs) {
		this.outputs = outputs;
	}

	public VersionMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(VersionMetadata metadata) {
		this.metadata = metadata;
	}

	@JsonIgnore
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@JsonIgnore
	public String getTaxonomy() {
		if(tags != null && tags.size() > 0 ) {
			String first = tags.get(0);
			return first.equals("parent") ? "" : first;
		}
		return "";
	}
}