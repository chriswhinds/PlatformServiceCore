package com.droitfintech.workflow.exceptions;

/**
 * Workflow exception class intended for reporting errors back to the GUI.
 */
public class WorkflowClientException extends DroitClientException {

	private static final long serialVersionUID = 1L;
	private String moduleName;

	public WorkflowClientException() {
		super();
	}

	public WorkflowClientException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WorkflowClientException(String message, String moduleName, Throwable cause) {
		super(message, cause);
		this.moduleName = moduleName;
	}

	public WorkflowClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowClientException(String message) {
		super(message);
	}

	public WorkflowClientException(Throwable cause) {
		super(cause);
	}

	public String getModuleName() {
		return moduleName;
	}

}
