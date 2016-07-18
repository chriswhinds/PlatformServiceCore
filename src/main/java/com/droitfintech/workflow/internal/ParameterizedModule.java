package com.droitfintech.workflow.internal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

/**
 * FlowchartModule.java (C) 2014 Droit Financial Technologies, LLC
 * 
 * @author nathanbrei
 */
@JsonPropertyOrder({ "metadata", "type", "name", "label", "description",
		"version", "tags", "inputs", "outputs", "params", "defaults", "initRef",
		"boundingBox", "nodes" })

public class ParameterizedModule extends FlowchartModule {

	private Map<String, String> params;


	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}
