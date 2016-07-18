package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SimpleModuleExecutable (C) 2014 Droit Financial Technologies, LLC
 * 
 * A ModuleExecutable implementation that directly traverses the graph of Nodes
 * contained inside ModuleSource.
 * 
 * @author nathanbrei
 * 
 */
public class ExecutableParameterizedModule implements ExecutableModule {

	private static Logger _logger = LoggerFactory
			.getLogger(ExecutableParameterizedModule.class);

	private String name;
	private FlowchartNode initNode;
	private StatelessScript defaults;
	private VersionMetadata versionMetadata;
	private Map<String, StatelessScript> params;

	public ExecutableParameterizedModule(ParameterizedModule source) {

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

		this.params = new HashMap<String, StatelessScript>();
		for (Entry<String, String> p : source.getParams().entrySet()) {
			this.params.put(p.getKey(), StatelessScript.parse(p.getValue()));
		}
		
		this.name = source.getName();
		this.versionMetadata = source.getMetadata();

	}

	// Run the workflow, assembling the entire map of workflow results
	public WorkflowStrictMap<String, Object> execute(Evaluator d, boolean collect) {
		
		WorkflowStrictMap<String, Object> results = new WorkflowStrictMap<String, Object>();
		
		for (String parameterName : params.keySet()) {
			Object it = params.get(parameterName).execute(d, null);
			results.put(parameterName, executeSingle(d, it));
		}
		return results;
	}
	
	public WorkflowStrictMap<String, Object> executeSingle(Evaluator d, Object it) {

		WorkflowStrictMap<String, Object> results = new WorkflowStrictMap<String, Object>();
		LinkedList<String> debugResults = new LinkedList<String>();

		try {
			results.putAll((Map<String, Object>) defaults.execute(d, it));
		} catch (Exception e) {
			_logger.error("Error evaluating defaults: " + name + ": "
					+ e.getMessage());
			throw new WorkflowException("Error evaluating defaults: " + name, e);
		}
		if (initNode == null) {
			return results;
		}
		FlowchartNode current = initNode;
		FlowchartNode lookahead = initNode;
		try {
			while (lookahead != null) {
				
				current = lookahead;
				debugResults.add(current.getName());
				lookahead = lookahead.next(d, it);
			}
			results.putAll(current.getResults(d, it));
			results.put("path", debugResults);
			results.lock();
			return results;
		} catch (Exception e) {
			_logger.warn(name + " died. Debuglog=" + debugResults);
			throw new WorkflowException("Internal error in " + name + ":"
					+ current.getName() + ": " + e.getMessage(), e);
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public VersionMetadata getVersionMetadata() {
		return this.versionMetadata;
	}
}
