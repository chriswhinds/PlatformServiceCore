package com.droitfintech.workflow.exceptions;

public class MissingWorkflowAttributeException extends WorkflowClientException {

	private static final long serialVersionUID = 1L;
	
	private String attributeName;
	
	public MissingWorkflowAttributeException() {
		super();
	}

	public MissingWorkflowAttributeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingWorkflowAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingWorkflowAttributeException(String message, String attributeName) {
		super(message);
		this.attributeName = attributeName;
	}

	public MissingWorkflowAttributeException(Throwable cause) {
		super(cause);
	}

	public String getAttributeName() {
		return attributeName;
	}
}
