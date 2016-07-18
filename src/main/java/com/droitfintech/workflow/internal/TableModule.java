package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.internal.repository.Module;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({ "metadata", "type", "name", "label", "description",
		"version", "tags", "inputs", "outputs", "defaults", "table" })
public class TableModule extends Module {

	private List<Map<String, Object>> table;

	public Map<String, Object> defaults;

	public List<Map<String, Object>> getTable() {
		return table;
	}

	public void setTable(List<Map<String, Object>> table) {
		this.table = table;
	}

	public Map<String, Object> getDefaults() {
		return defaults;
	}

	public void setDefaults(Map<String, Object> defaults) {
		this.defaults = defaults;
	}
}
