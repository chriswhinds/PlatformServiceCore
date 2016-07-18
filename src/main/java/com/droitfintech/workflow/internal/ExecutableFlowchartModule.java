package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.exceptions.*;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * SimpleModuleExecutable (C) 2014 Droit Financial Technologies, LLC
 * <p/>
 * A ModuleExecutable implementation that directly traverses the graph of Nodes
 * contained inside ModuleSource.
 *
 * @author nathanbrei
 */
public class ExecutableFlowchartModule implements ExecutableModule {

    private static Logger _logger = LoggerFactory
            .getLogger(ExecutableFlowchartModule.class);

    private String name;
    private FlowchartNode initNode;
    private StatelessScript defaults;
    private VersionMetadata versionMetadata;

    public ExecutableFlowchartModule(FlowchartModule source) {

        Map<String, FlowchartNode> nodeMap = new LinkedHashMap<String, FlowchartNode>();
        for (FlowchartNode n : source.getNodes())
            nodeMap.put(n.getName(), n);

        for (FlowchartNode node : nodeMap.values())
            node.wire(name, nodeMap);

        FlowchartNode initNode = nodeMap.get(source.getInitRef());

        if (initNode == null)
            throw new WorkflowException("Cannot find initNode "
                    + source.getInitRef() + " in " + name);

        this.initNode = initNode;

        this.defaults = StatelessScript.parse(source.getDefaults());

        this.name = source.getName();
        this.versionMetadata = source.getMetadata();

    }

    private void evaluateDefaults(Evaluator d, Map<String, Object> results) {
        try {
            results.putAll((Map<String, Object>) defaults.execute(d, null));
        } catch (MissingWorkflowAttributeException ae) {
            String errMsg = "Workflow default outputs has unavailable attribute reference in module: " +  versionMetadata.getId()
                    + "', attribute: " + d.getLastCollectionAccessed() + "." + ae.getAttributeName();
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ae );
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_ATTRIBUTE.getNumVal());
            throw wfe;
        }
        catch (UnknownModuleException ume) {
            String errMsg = "Workflow module " + versionMetadata.getId() + " contains unknown module reference 'd." + ume.getModuleName() + "' in default outputs";
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ume);
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_MODULE.getNumVal());
            throw wfe;

        } catch(WorkflowClientException wce) {
            throw wce; // calls to this can be recursive!!
        }
        catch (Exception e) {
            _logger.error("Error evaluating defaults: " + name + ": "
                    + e.getMessage());
            throw new WorkflowException("Error evaluating defaults: " + name, e);
        }
    }

    public WorkflowStrictMap<String, Object> execute(Evaluator d, boolean collect) {
        if (collect) {
            _logger.trace("Executing module with collection...");
            return executeAndCollect(d);
        } else {
            _logger.trace("Executing module normally...");
            return executeNormally(d);
        }
    }

    // Run the workflow, assembling the entire map of workflow results
    private WorkflowStrictMap<String, Object> executeNormally(Evaluator d) {

        WorkflowStrictMap<String, Object> results = new WorkflowStrictMap<String, Object>();
        LinkedList<String> debugResults = new LinkedList<String>();

        evaluateDefaults(d, results);

        if (initNode == null) {
            return results;
        }
        FlowchartNode current = initNode;
        FlowchartNode lookahead = initNode;
        try {
            while (lookahead != null) {

                current = lookahead;
                debugResults.add(current.getName());
                lookahead = lookahead.next(d, null);
            }
            results.putAll(current.getResults(d, null));
            results.put("path", debugResults);
            results.lock();
            return results;
        } catch (MissingWorkflowAttributeException ae) {
            String errMsg = "Workflow has unavailable attribute reference in module: " +  versionMetadata.getId()
                    + ", node name/desc: " + current.getName() + "/'"  + current.getDescription()
                    + "', attribute: " + d.getLastCollectionAccessed() + "." + ae.getAttributeName();
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ae );
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_ATTRIBUTE.getNumVal());
            throw wfe;
        }
        catch (UnknownModuleException ume) {
            String errMsg = "Workflow module " + versionMetadata.getId() + " contains unknown module reference 'd." + ume.getModuleName() + "' in"
                    + ", node name/desc: " + current.getName() + "/'"  + current.getDescription() + "'";
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ume);
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_MODULE.getNumVal());
            throw wfe;

        } catch(WorkflowClientException wce) {
            throw wce; // calls to this can be recursive!!
        }
        catch (Exception e) {
            _logger.warn(name + " died. Debuglog=" + debugResults);
            throw new WorkflowException("Internal error in " + name + ":"
                    + current.getName() + ": " + e.getMessage(), e);
        }
    }

    private boolean isEscalationNode(FlowchartNode n, Evaluator d) {
        boolean retValue = false;
        if (n.getType() == FlowchartNode.NodeType.termination) {
            Map<String, Object> res = n.getResults(d, null);
            if (res.containsKey("tradeAllowed"))
                retValue = ((Boolean) res.get("tradeAllowed")).booleanValue();

        }
        return retValue;
    }

    private String concatMessages(List<Map<String, Object>> accumulated) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (Map<String, Object> res : accumulated) {
            String s = String.valueOf(res.get("message"));
            sb.append(delimiter);
            sb.append(s);
            delimiter = ", ";
        }
        return sb.toString();
    }

    // HACK: Run the workflow, accumulating escalations as they occur.
    private WorkflowStrictMap<String, Object> executeAndCollect(Evaluator d) {
        WorkflowStrictMap<String, Object> results = new WorkflowStrictMap<String, Object>();
        LinkedList<String> debugResults = new LinkedList<String>();
        LinkedList<Map<String, Object>> accumulated = new LinkedList<Map<String, Object>>();

        evaluateDefaults(d, results);

        if (initNode == null) {
            return results;
        }

        FlowchartNode previous = initNode;
        FlowchartNode current = initNode;
        FlowchartNode lookahead = initNode;
        try {
            while (lookahead != null) {

                current = lookahead;
                debugResults.add(current.getName());
                lookahead = current.next(d, null);

                if (isEscalationNode(current, d)) {
                    if(previous.canEvaluateBothPaths()) {
                        if (current.equals(previous.getTrueNode())) {
                            lookahead = previous.getFalseNode();
                            debugResults.add(previous.getName());
                        } else if (current.equals(previous.getFalseNode())) {
                            lookahead = previous.getTrueNode();
                            debugResults.add(previous.getName());
                        }
                    }
                    accumulated.add(current.getResults(d, null));
                }
                previous = current;
            }

            if (accumulated.isEmpty()) {
                results.putAll(current.getResults(d, null));
            } else {
                Map<String, Object> firstEscalation = accumulated.getFirst();
                results.putAll(firstEscalation); // start by using all outputs from the first escalation
                // Override the message and add all collected escalations
                //results.put("tradeAllowed", false); replaced with result of first escalation.
                results.put("escalations", accumulated);
                results.put("message", concatMessages(accumulated));
            }
            results.put("path", debugResults);
            results.lock();
            return results;
        } catch (MissingWorkflowAttributeException ae) {
            String errMsg = "Workflow has unavailable attribute reference in module: " +  versionMetadata.getId()
                    + ", node name/desc: " + current.getName() + "/'"  + current.getDescription()
                    + "', attribute: " + d.getLastCollectionAccessed() + "." + ae.getAttributeName();
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ae );
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_ATTRIBUTE.getNumVal());
            throw wfe;
        }
        catch (UnknownModuleException ume) {
            String errMsg = "Workflow module " + versionMetadata.getId() + " contains unknown module reference 'd." + ume.getModuleName() + "' in"
                    + ", node name/desc: " + current.getName() + "/'"  + current.getDescription() + "'";
            WorkflowClientException wfe = new WorkflowClientException(errMsg, versionMetadata.getId(), ume);
            wfe.setStatusCode(WorkflowExceptionStatusCodes.STATUS_MISSING_MODULE.getNumVal());
            throw wfe;

        } catch(WorkflowClientException wce) {
            throw wce; // calls to this can be recursive!!
        }
        catch (Exception e) {
            _logger.warn(name + " died. Debuglog=" + debugResults);
            throw new WorkflowException("Internal error in " + name + ":"
                    + current.getName() + ":" + e.getMessage(), e);
        }
    }


    public String getName() {
        return name;
    }


    public VersionMetadata getVersionMetadata() {
        return this.versionMetadata;
    }
}
