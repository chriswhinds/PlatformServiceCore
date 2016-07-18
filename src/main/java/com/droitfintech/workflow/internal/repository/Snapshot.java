package com.droitfintech.workflow.internal.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;

public class Snapshot {

	private VersionMetadata metadata = new VersionMetadata();

	private List<String> moduleRefs = new LinkedList<String>();

	public Snapshot() {
	}

	public VersionMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(VersionMetadata metadata) {
		this.metadata = metadata;
	}

	public List<String> getModuleRefs() {
		return moduleRefs;
	}

	public void setModuleRefs(List<String> moduleRefs) {
		this.moduleRefs = moduleRefs;
	}

	@JsonIgnore
	public void setModuleRefsOutOfScope(List<String> moduleRefs) {
		; // just here so that we can 'comment out' certain modules
	}

	@JsonIgnore
	public void getModuleRefsOutOfScope() {
		; // just here so that we can 'comment out' certain modules
	}
}
