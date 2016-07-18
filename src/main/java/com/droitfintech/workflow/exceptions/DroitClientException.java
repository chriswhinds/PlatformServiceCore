package com.droitfintech.workflow.exceptions;


import com.droitfintech.exceptions.DroitException;

public class DroitClientException extends DroitException {

	public DroitClientException() {
		super();
	}

	public DroitClientException(String message, Throwable cause,
								boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DroitClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public DroitClientException(String message) {
		super(message);
	}

	public DroitClientException(String message, int statusCode) {
		super(message);
		super.setStatusCode(statusCode);
	}

	public DroitClientException(Throwable cause) {
		super(cause);
	}
	
	public static void assertThat(boolean exp, String msg) {
		
		if (!exp) {
			throw new DroitClientException("Assertion Failed: " + msg);
		}
	}
}
