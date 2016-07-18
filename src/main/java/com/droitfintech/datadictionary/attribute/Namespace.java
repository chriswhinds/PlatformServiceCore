package com.droitfintech.datadictionary.attribute;



import java.util.*;


public class Namespace {

	protected Long id;
	protected String name;
	protected String scope;
	protected Map<String, Attribute> attributes = new TreeMap<String,Attribute>();
	protected Set<String> instances = new HashSet<String>();
	protected String description;

	public Namespace(String scope, String name) {
		this.scope = scope;
		this.name = name;
	}

	protected Namespace() {}

	public void addAttribute(Attribute attribute) {
		String name = attribute.getName();
		if (attributes.containsKey(name)) {
			// warn about redefinition...?
		}
		this.attributes.put(name, attribute);
		attribute.setNamespace(this);
	}

	public Attribute removeAttribute(Attribute attribute) {
		String name = attribute.getName();
		if (attributes.containsKey(name)) {
			return attributes.remove(name);
		}
		return null;
	}

	public boolean containsAttribute(String attributeName) {
		return this.attributes.containsKey(attributeName);
	}

	public Attribute getAttribute(String attributeName) {
		return this.attributes.get(attributeName);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInstances(String[] names) {
		instances.addAll(Arrays.asList(names));
	}

	public Collection<String> getInstanceNames() { return instances; }
}
