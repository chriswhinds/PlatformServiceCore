package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.internal.repository.VersionMetadata;


/**
 * ExecutableModule (C) 2014 Droit Financial Technologies, LLC
 * 
 * @author nathanbrei
 */
public interface ExecutableModule {

	public WorkflowStrictMap<String, Object> execute(Evaluator d, boolean collectEscalations);
	public String getName();
	public VersionMetadata getVersionMetadata();

}
