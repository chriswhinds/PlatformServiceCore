package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.exceptions.MissingWorkflowAttributeException;
import com.droitfintech.workflow.exceptions.WorkflowException;

import java.util.HashMap;

public class WorkflowStrictMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private boolean locked = false;

	@Override
	public V get(Object key) {
		if (this.containsKey(key)) {
			return super.get(key);
		}
		throw new MissingWorkflowAttributeException(
				"Could not find attribute '" + key + "' for this map", (String) key);
	}

	@Override
	public V put(K key, V value) {
		if (locked) {
			throw new WorkflowException(
					"Attempted to modify immutable workflow results: " + key);
		}
		return super.put(key, value);
	}

	public void lock() {
		this.locked = true;
	}
}
