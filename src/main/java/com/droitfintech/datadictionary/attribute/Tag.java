package com.droitfintech.datadictionary.attribute;


import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Tag {
	private static HashMap<String,Tag> defaultTagMap;
	static {
		defaultTagMap = new HashMap();

		// Note that they keys used below are intended to map 1-1 with a workflow module prefix
		defaultTagMap.put("amf", new Tag("amf", "AMF", "AMF"));
		defaultTagMap.put("asic", new Tag("asic", "ASIC", "ASIC"));
		defaultTagMap.put("cftc", new Tag("cftc", "Dodd-Frank", "Dodd-Frank"));
		defaultTagMap.put("emir", new Tag("emir", "EMIR", "EMIR"));
		defaultTagMap.put("esma", new Tag("esma", "MiFID II", "MiFID II"));
		defaultTagMap.put("fsfm", new Tag("fsfm", "FSFM", "FSFM"));
		defaultTagMap.put("jfsa", new Tag("jfsa", "JFSA", "JFSA"));
		defaultTagMap.put("mas", new Tag("mas", "MAS", "MAS"));
		defaultTagMap.put("msc", new Tag("msc", "MSC", "MSC"));
		defaultTagMap.put("osc", new Tag("osc", "OSC", "OSC"));
		defaultTagMap.put("sfc", new Tag("sfc", "SFC", "SFC"));
		defaultTagMap.put("cnbv", new Tag("cnbv", "CNBV", "CNBV")); // Mexico
		defaultTagMap.put("osfi", new Tag("osfi", "OSFI", "OSFI"));
		defaultTagMap.put("multijuris", new Tag("multijuris", "Canada", "Canada"));

		defaultTagMap.put("deprecated", new Tag("deprecated", "Deprecated", "Deprecated"));
		// The following are stopgaps.  We shouldn't be driving GUI information from tags
		defaultTagMap.put("NONE", new Tag("NONE", "NONE", "NONE"));
		defaultTagMap.put("Common Attr", new Tag("Common Attr", "Common Attr", "Common Attr"));
	}

	protected String id;
	protected String name;
	protected String description;

	public Tag(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public static Tag getTagById(String id) {
		return defaultTagMap.get(id);
	}

	public static Tag getTagByName(String name) {
		for(Tag t : defaultTagMap.values()) {
			if(name.equals(t.getName())) {
				return t;
			}
		}
		return null;
	}

	public static Set<String> getTagNames() {
		TreeSet<String> names = new TreeSet();
		for(Tag t : defaultTagMap.values()) {
			names.add(t.getName());
		}
		return names;
	}
}
