package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.internal.repository.Module;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * FlowchartModule.java (C) 2014 Droit Financial Technologies, LLC
 * 
 * @author nathanbrei
 */
@JsonPropertyOrder({ "metadata", "type", "name", "label", "description",
		"version", "tags", "inputs", "outputs", "defaults", "initRef",
		"boundingBox", "nodes" })
public class FlowchartModule extends Module {

	private String initRef;
	private String defaults;
	private List<FlowchartNode> nodes = new LinkedList<FlowchartNode>();
	private List<Integer> boundingBox;

	public String getInitRef() {
		return initRef;
	}

	public void setInitRef(String initRef) {
		this.initRef = initRef;
	}

	public String getDefaults() {
		return defaults;
	}

	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}

	public List<FlowchartNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<FlowchartNode> nodes) {
		this.nodes = nodes;
	}

	public List<Integer> getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(List<Integer> boundingBox) {
		this.boundingBox = boundingBox;
	}
}
