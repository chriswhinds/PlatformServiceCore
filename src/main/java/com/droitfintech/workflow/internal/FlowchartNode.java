package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.groovy.GroovyParser;
import com.droitfintech.workflow.internal.groovy.ReferenceCollector;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Map;

@JsonPropertyOrder({ "type", "name", "description", "expr", "trueRef",
		"falseRef", "formatting" })
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlowchartNode {

	public enum NodeType {
		condition, termination, continuation, chain
	};

	private NodeType type;
	private String name;
	private String description;
	private String expr;
	private String trueRef;
	private String falseRef;
	private Object formatting; // Only needed in the Javascript world

	private FlowchartNode trueNode, falseNode;
	private StatelessScript compiledExpr;

	public FlowchartNode next(Evaluator d, Object it) {

		if (type == NodeType.condition) {

			Object conditionAnswer = compiledExpr.execute(d, it);
			if (!(conditionAnswer instanceof Boolean)) {
				throw new WorkflowException("A condition node must return a boolean");
			}
			if (conditionAnswer == null) {
				throw new WorkflowException("A condition node must not return a null value.  A Boolean is required");
			}

			return (Boolean) conditionAnswer ? trueNode : falseNode;

		} else if (type == NodeType.chain) {
			return (Boolean) d.get(expr).get("continueWorkflow") ? trueNode
					: null;
		}
		return null;
	}

	public Map<String, Object> getResults(Evaluator d, Object it) {

		if (type == NodeType.chain) {
			return (Map<String, Object>) d.get(expr);
		} else if (type == NodeType.termination
				|| type == NodeType.continuation) {
			return (Map<String, Object>) compiledExpr.execute(d, it);
		}
		throw new WorkflowException("Not a terminating node!");
	}

	public void wire(String name, Map<String, FlowchartNode> nodes) {
		if (type != NodeType.chain) {
			compiledExpr = StatelessScript.parse(expr);
		}

		if (type == NodeType.chain) {
			// trueNode for chain nodes are now optional.
			trueNode = nodes.get(trueRef);
		}

		if (type == NodeType.condition) {
			trueNode = nodes.get(trueRef);
			falseNode = nodes.get(falseRef);
			if (trueNode == null || falseNode == null) {
				throw new WorkflowException("Cannot find node " + falseRef
						+ " in " + name);
			}
		}
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getTrueRef() {
		return trueRef;
	}

	public void setTrueRef(String trueRef) {
		this.trueRef = trueRef;
	}

	@JsonIgnore
	public FlowchartNode getTrueNode() {
		return this.trueNode;
	}

	@JsonIgnore
	public FlowchartNode getFalseNode() {
		return this.falseNode;
	}

	public String getFalseRef() {
		return falseRef;
	}

	public void setFalseRef(String falseRef) {
		this.falseRef = falseRef;
	}

	public Object getFormatting() {
		return formatting;
	}

	public void setFormatting(Object formatting) {
		this.formatting = formatting;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlowchartNode [type=").append(type).append(", name=")
				.append(name).append(", description=").append(description)
				.append(", expr=").append(expr).append(", trueRef=")
				.append(trueRef).append(", falseRef=").append(falseRef)
				.append(", formatting=").append(formatting)
				.append(", trueNode=").append(trueNode).append(", falseNode=")
				.append(falseNode).append(", compiledExpr=")
				.append(compiledExpr).append("]");
		return builder.toString();
	}

	/**
	 * Decide if a condition node supports evaluation of both paths when the
	 * property adept.workflow.collectEscalations=true is on. Currently this is
	 * supported if the only attribute references in the condition are to party variables.
	 * @return
	 */
	public boolean canEvaluateBothPaths() {
		if(type == NodeType.condition) {
			if(StringUtils.isNotBlank(expr)) {
				ReferenceCollector refCollector = new ReferenceCollector();
				try {
					for (ReferenceCollector.Reference ref : refCollector.getReferences(GroovyParser.parse(new ByteArrayInputStream(expr.getBytes())))) {
						if (!ref.getLhs().equals("counterparty") && !ref.getLhs().equals("contraparty")) {
							return false;
						}
					}
				} catch (Exception ignore) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
