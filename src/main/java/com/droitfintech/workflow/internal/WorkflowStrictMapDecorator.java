package com.droitfintech.workflow.internal;

import com.droitfintech.datadictionary.NamespaceBackedMap;
import com.droitfintech.workflow.exceptions.MissingWorkflowAttributeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class WorkflowStrictMapDecorator<K, V> implements Map<K, V> {

	private Map<K, V> underlyingMap;
	private static Logger attrLogger = LoggerFactory.getLogger("com.droitfintech.attributes");
	private boolean logAttributes = false;
	private String mapName;

	public WorkflowStrictMapDecorator(Map<K, V> underlyingMap, String mapName) {
		super();
		if (underlyingMap instanceof WorkflowStrictMapDecorator) {
			// Don't decorate a decorator!
			this.underlyingMap = ((WorkflowStrictMapDecorator)underlyingMap).underlyingMap;
		} else {
			this.underlyingMap = underlyingMap;
		}

		// it's expensive to see if trade is enabled, so this way we do it once per
		// object creation
		if (attrLogger.isTraceEnabled()) {
			logAttributes = true;
		}

		this.mapName = mapName;
	}


	public V get(Object key) {
		V val = underlyingMap.get(key);
		if (val == null && !underlyingMap.containsKey(key)) {
			if(underlyingMap instanceof NamespaceBackedMap) {
				val = (V)((NamespaceBackedMap) underlyingMap).getDefault(key);
			}
			if(val == null) {
				throw new MissingWorkflowAttributeException(
						"Could not find attribute '" + key + "' for this map", (String) key);
			}
		}

		if (logAttributes) {

			String logMsg = String.format("%35s - %40s : %-100s", mapName, key,
					val == null ? "null" : val.toString());
			attrLogger.trace(logMsg);
		}

		return val;
	}

	// **** START Delegated methods

	/**
	 * @return
	 * @see Map#size()
	 */
	public int size() {
		return underlyingMap.size();
	}

	/**
	 * @return
	 * @see Map#isEmpty()
	 */
	public boolean isEmpty() {
		return underlyingMap.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 * @see Map#containsKey(Object)
	 */
	public boolean containsKey(Object key) {
		return underlyingMap.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 * @see Map#containsValue(Object)
	 */
	public boolean containsValue(Object value) {
		return underlyingMap.containsValue(value);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see Map#put(Object, Object)
	 */
	public V put(K key, V value) {
		return underlyingMap.put(key, value);
	}

	/**
	 * @param key
	 * @return
	 * @see Map#remove(Object)
	 */
	public V remove(Object key) {
		return underlyingMap.remove(key);
	}

	/**
	 * @param m
	 * @see Map#putAll(Map)
	 */
	public void putAll(Map<? extends K, ? extends V> m) {
		underlyingMap.putAll(m);
	}

	/**
	 *
	 * @see Map#clear()
	 */
	public void clear() {
		underlyingMap.clear();
	}

	/**
	 * @return
	 * @see Map#keySet()
	 */
	public Set<K> keySet() {
		return underlyingMap.keySet();
	}

	/**
	 * @return
	 * @see Map#values()
	 */
	public Collection<V> values() {
		return underlyingMap.values();
	}

	/**
	 * @return
	 * @see Map#entrySet()
	 */
	public Set<Entry<K, V>> entrySet() {
		return underlyingMap.entrySet();
	}

	/**
	 * @param o
	 * @return
	 * @see Map#equals(Object)
	 */
	public boolean equals(Object o) {
		return underlyingMap.equals(o);
	}

	/**
	 * @return
	 * @see Map#hashCode()
	 */
	public int hashCode() {
		return underlyingMap.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return underlyingMap.toString();
	}
}
