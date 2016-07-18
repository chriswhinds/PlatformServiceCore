package com.droitfintech.dataframes;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;

import com.droitfintech.exceptions.DroitException;


public class Cell {

	public enum Cardinality {ZERO, ONE, MANY};

	public Cardinality cardinality;
	public List<Value> values;

	public Cell() {
		this.cardinality = Cardinality.ZERO;
	}
	
	public Cell(Value value) {
		this.cardinality = Cardinality.ONE;
		this.values = new LinkedList<Value>();
		this.values.add(value);
	}

	public Cell(List<Value> values) {
		this.cardinality = Cardinality.MANY;
		this.values = values;
	}
	
	public Cardinality getCardinality() {
		return this.cardinality;
	}
	
	public Value getValue() {
		if (cardinality != Cardinality.ONE) {
			throw new DroitException("Cell does not contain a singleton: " + cardinality);
		}
		return this.values.get(0);
	}
	
	public List<Value> getValues() {
		if (cardinality != Cardinality.MANY) {
			throw new DroitException("Cell does not contain a list: " + cardinality);
		}
		return this.values;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(15, 31)
			.append(this.values)
			.append(this.cardinality)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) { return false; }

		Cell rhs = (Cell) obj;
		return new EqualsBuilder()
			.append(this.values, rhs.values)
			.append(this.cardinality, rhs.cardinality)
			.isEquals();
	}

	// TODO: Escape commas
	// toString() result should always be parseable by Old Schema, TradePredicate
	public String toString() {
		switch (cardinality) {
		case ZERO: return "*";
		case ONE: return (values.get(0) == null) ? null : values.get(0).toString();
		case MANY: return StringUtils.join(values, ", ");
		default: return "???";
		}
	}
	
	public Object unbox() {

		if (cardinality == Cardinality.ONE) {
			return values.get(0).getUnderlying();
		}
		else if (cardinality == Cardinality.MANY) {
			List<Object> result = new LinkedList<Object>();
			for (Value value : values) {
				result.add(value.getUnderlying());
			}
			return result;
		}
		else {
			return null;
		}
	}
}
