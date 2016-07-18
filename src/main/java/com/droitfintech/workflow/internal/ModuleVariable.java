package com.droitfintech.workflow.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "module", "name", "type", "operator", "label", "description", "tags" })
@JsonIgnoreProperties(ignoreUnknown=true)
public class ModuleVariable {

	private String module, name, type, label, description;
	private List<String> tags;
	private String operator; // added to expand original table module to use data frames for implementation.

	public ModuleVariable() {
	}

	public ModuleVariable(String module, String name, String type) {
		this.module = module;
		this.name = name;
		this.type = type;
	}

	/**
	 * Convert primitive JSON types into their actual types (e.g. Date, Tenor)
	 * so we can hashcode-compare against Payload inputs.
	 * 
	 * @param rawJsonType
	 * @return
	 */
	public Object typify(Object rawJsonType) {
		return rawJsonType;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
