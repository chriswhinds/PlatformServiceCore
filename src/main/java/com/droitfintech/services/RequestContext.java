package com.droitfintech.services;

/**
 * Object passed to services like the workflow service to identify the application that is requesting use of the service.
 * Encapsulates the metadata necessary for validating licensing and where supported overriding default behavior by session.
 * @author roytruelove
 *
 */
public class RequestContext {
    public enum ApplicationType {WEBTOOL, BATCHREPORT, ETRADING};

    private ApplicationType requestType = ApplicationType.ETRADING;
    private boolean disableAuditing = false;

    private String sessionId = null; // currently used by GUI to pass session ID down to workflow editor for dev snapshot usage.

    public ApplicationType getRequestType() {
        return requestType;
    }
    public void setRequestType(ApplicationType requestType) {
        this.requestType = requestType;
    }
    public boolean isAuditingDisabled() {
        return disableAuditing;
    }
    public void setDisableAuditing(boolean disableAuditing) {
        this.disableAuditing = disableAuditing;
    }

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}