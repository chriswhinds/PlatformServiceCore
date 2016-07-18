package com.droitfintech.licensing;

import com.droitfintech.model.FinMktInfraVersion;
import com.droitfintech.services.RequestContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.List;
import java.util.Map;
import java.util.Set;




public class License {
    public enum WorkflowEditorLicense { NONE, CLIENT, ALL /* All is ONLY to be used in internal installations*/}
    private String clientName = "";
    private WorkflowEditorLicense workflowEditingAllowed = WorkflowEditorLicense.NONE;
    private Map<RequestContext.ApplicationType, List<String>> workflowWhitelist;
    private Map<RequestContext.ApplicationType, List<FinMktInfraVersion.FinMktInfraVersionType>> marketLogicWhitelist;
    private Map<RequestContext.ApplicationType, Set<String>> assetClassWhitelist;



    public Map<RequestContext.ApplicationType, List<String>> getWorkflowWhitelist() {
        return workflowWhitelist;
    }

    public void setWorkflowWhitelist(Map<RequestContext.ApplicationType, List<String>> workflowWhitelist) {
        this.workflowWhitelist = workflowWhitelist;
    }

    public Map<RequestContext.ApplicationType, List<FinMktInfraVersion.FinMktInfraVersionType>> getMarketLogicWhitelist() {
        return marketLogicWhitelist;
    }

    public void setMarketLogicWhitelist(Map<RequestContext.ApplicationType, List<FinMktInfraVersion.FinMktInfraVersionType>> marketLogicWhitelist) {
        this.marketLogicWhitelist = marketLogicWhitelist;
    }

    public Map<RequestContext.ApplicationType, Set<String>> getAssetClassWhitelist() {
        return assetClassWhitelist;
    }

    public void setAssetClassWhitelist(Map<RequestContext.ApplicationType, Set<String>> assetClassWhitelist) {
        this.assetClassWhitelist = assetClassWhitelist;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public WorkflowEditorLicense getWorkflowEditingAllowed() {
        return workflowEditingAllowed;
    }

    public void setWorkflowEditingAllowed(WorkflowEditorLicense workflowEditingAllowed) {
        this.workflowEditingAllowed = workflowEditingAllowed;
    }
}