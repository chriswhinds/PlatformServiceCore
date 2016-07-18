package com.droitfintech.datadictionary.attribute;

import com.droitfintech.exceptions.DroitException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

import java.util.Objects;

import java.util.Set;


public class Attribute {
	private static Logger log = LoggerFactory.getLogger(Attribute.class);
	private static final Logger logger = LoggerFactory.getLogger(Attribute.class);
	int cachedHash = Integer.MIN_VALUE;
	protected Long id;
	protected String scope;
	protected Namespace namespace;
	protected String name;
	protected Set<Tag> tags = new HashSet<Tag>(0);
	protected Type type;
	protected Cardinality cardinality;
	protected String description = "";
	protected String tenantIdentifier;
	private	String validation = "";  // For enums contains TypeName.class, for stringSet comma separated list of values, regular string java RE if not empty.
	private String validationError = "";
	private String displayName = "";
	private String source = "";
	private String notes = "";
	private String calculationFunction = "";
	private String calculationParameters = "";
	private String linkedDefaultField = "";
	private DefaultServiceLookup defaultServiceLookup = DefaultServiceLookup.NONE;
	private Class<?>  resolvedClass;
	private Object cachedCalculationFunction;
	private String defaultValueAsString = "";
	private Object defaultValueNative = null;
	private Collection<String> cachedLinkedDefaultField;
	private TaxonomyPoint referencingTaxonomyPoints = new TaxonomyPoint();

	protected Attribute() {}

	public Attribute(String scope, String name, Type type, Cardinality cardinality) {
		this.scope = scope;
		this.name = name;
		this.type = type;
		this.cardinality = cardinality;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
		resetHash();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		resetHash();
	}

	public Type getType() {
		return type;
	}

	public Class<?> getTypeClass() {
		if(resolvedClass == null) {
			try {
				resolvedClass = Class.forName(type.className);
				//log.debug("Resolved type {} ", type.className);
			} catch (ClassNotFoundException e) {
				throw new DroitException("Could not find class named " + type.className, e);
			}
		}
		return resolvedClass;
	}

	public Object getCachedCalculationFunction() {
		return cachedCalculationFunction;
	}

	public void setCachedCalculationFunction(Object cachedCalculationFunction) {
		this.cachedCalculationFunction = cachedCalculationFunction;
	}

	public void setType(Type type) {
		this.type = type;
		resetHash();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		resetHash();
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
		resetHash();
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
		resetHash();
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
		resetHash();
	}

	public String getTenantIdentifier() {
		return tenantIdentifier;
	}

	public void setTenantIdentifier(String tenantIdentifier) {
		this.tenantIdentifier = tenantIdentifier;
		resetHash();
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
		resetHash();
	}

	public String getValidationError() {
		return validationError;
	}

	public void setValidationError(String validationError) {
		this.validationError = validationError;
		resetHash();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		resetHash();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		resetHash();
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
		resetHash();
	}

	public String getCalculationFunction() {
		return calculationFunction;
	}

	public void setCalculationFunction(String calculationFunction) {
		this.calculationFunction = calculationFunction;
		resetHash();
	}

	public String getCalculationParameters() {
		return calculationParameters;
	}

	public void setCalculationParameters(String calculationParameters) {
		this.calculationParameters = calculationParameters;
		resetHash();
	}

	public String getLinkedDefaultField() {
		return linkedDefaultField;
	}

	public void setLinkedDefaultField(String linkedDefaultField) {
		this.linkedDefaultField = linkedDefaultField;
		resetHash();
	}

	public DefaultServiceLookup getDefaultServiceLookup() {
		return defaultServiceLookup;
	}

	public void setDefaultServiceLookup(DefaultServiceLookup defaultServiceLookup) {
		this.defaultServiceLookup = defaultServiceLookup;
		resetHash();
	}

	public Collection<String> getCachedLinkedDefaultField() {
		return cachedLinkedDefaultField;
	}

	public void setCachedLinkedDefaultField(Collection<String> cachedLinkedDefaultField) {
		this.cachedLinkedDefaultField = cachedLinkedDefaultField;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Attribute attribute = (Attribute) o;
		return Objects.equals(scope, attribute.scope) &&
			   Objects.equals(namespace, attribute.namespace) &&
			   Objects.equals(name, attribute.name) &&
			   Objects.equals(cardinality, attribute.cardinality);
	}

	// This can all go away if we make Attribute immutable
	private void resetHash() {
		cachedHash = Integer.MIN_VALUE;
	}

	public String getDefaultValueAsString() {
		return defaultValueAsString;
	}

	public void setDefaultValueAsString(String defaultValueAsString) {
		this.defaultValueAsString = defaultValueAsString;
	}

	public Object getDefaultValueNative() {
		if(defaultValueNative == null && StringUtils.isNotBlank(defaultValueAsString)) {
			try {
				Class clazz = Class.forName(type.className);
				defaultValueNative = clazz.getConstructor(String.class).newInstance(defaultValueAsString);

				// commented out until we're able to differentiate between party fields that are
				// in the dictionary and those that are actually used.
//				if (log.isWarnEnabled()) {
//					log.warn("Attribute '{}' in namespace '{}' was defaulted to '{}' as defined by the Data Dictionary.  " +
//									"This could result in incorrect results.  Please amend your source data to provide this value.",
//							this.getName(), this.getNamespace().getName(), defaultValueNative);
//				}

			} catch (Exception ignore) {
				logger.info("Unable to find single ard constructor for {} to create default value of {}",
						type.className, defaultValueAsString);
				// clear out strig value so we don't try this again.
				defaultValueAsString = "";
			}
		}
		return defaultValueNative;
	}

	@Override
	public int hashCode() {

		if (cachedHash != Integer.MIN_VALUE) {
			return cachedHash;
		} else{
			cachedHash = Objects.hash(scope, type, name, cardinality);
		}

		return cachedHash;
	}

	public TaxonomyPoint getReferencingTaxonomyPoints() {
		return referencingTaxonomyPoints;
	}

	public void setReferencingTaxonomyPoints(TaxonomyPoint referencingTaxonomyPoints) {
		this.referencingTaxonomyPoints = referencingTaxonomyPoints;
	}
}