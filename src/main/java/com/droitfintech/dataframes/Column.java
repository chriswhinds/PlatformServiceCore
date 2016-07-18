package com.droitfintech.dataframes;

import com.droitfintech.dataframes.Value.ValueType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Column {
	
	public enum Mode { INPUT, OUTPUT, BOTH }

	public String attribute;
	public ValueType type;
	public Constraint operator;
	public Mode mode;
	

	public Column(String attribute, ValueType type, Constraint operator, Mode mode) {	
		this.attribute = attribute;
		this.type = type;
		this.operator = operator;
		this.mode = mode;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(15, 31)
			.append(this.attribute)
			.append(this.type)
			.append(this.operator)
			.append(this.mode)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		Column rhs = (Column) obj;
		return new EqualsBuilder()
			.append(this.attribute, rhs.attribute)
			.append(this.type, rhs.type)
			.append(this.operator, rhs.operator)
			.append(this.mode, rhs.mode)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return attribute + ":" + type + ":" + operator + ":" + mode;
	}
	
	public String getName() {
		return attribute + operator.getHumanName();
	}
}

