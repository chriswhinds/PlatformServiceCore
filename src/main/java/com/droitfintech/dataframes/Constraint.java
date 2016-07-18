package com.droitfintech.dataframes;

import com.droitfintech.dataframes.Cell.Cardinality;

import java.util.Collection;

public enum Constraint {

	EQ ("=", ""), 
	IN ("<-", "list"), // cell contains any value from tested field.
	MIN (">=", "min"),
	GT (">", "above"),
	MAX ("<=", "max"),
	LT ("<", "below"),
	SUBSET("c","subset"); // values in cell are a subset of tested field. In our case a full match is included.
	
	private String symbol, humanName;

	Constraint(String symbol, String humanName) {
		this.symbol = symbol;
		this.humanName = humanName;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getHumanName() {
		return humanName;
	}

	public boolean apply(Object lhs, Cell rhs) {

		if (rhs.cardinality == Cardinality.ZERO) {
			return true;
		}
		switch (this) {
		case EQ: return rhs.getValue().getUnderlying() == null ? lhs == null : rhs.getValue().equals(lhs);
		case IN: return lhs != null && lhs instanceof Collection ? rhs.getValues().containsAll((Collection) lhs) : rhs.getValues().contains(lhs);
		case SUBSET: return lhs != null && lhs instanceof Collection ? ((Collection) lhs).containsAll(rhs.getValues()) : rhs.getValues().size() == 1 && rhs.getValues().contains(lhs);
		case MIN: return lhs != null ? rhs.getValue().compareTo(lhs) <= 00 : false;
		case MAX: return lhs != null ? rhs.getValue().compareTo(lhs) >= 00 : false;
		case LT: return lhs != null ? rhs.getValue().compareTo(lhs) > 00 : false;
		case GT: return lhs != null ? rhs.getValue().compareTo(lhs) < 0 : false;
		default: return false;
		}
	}
	
//	public boolean apply(Cell lhs, Cell rhs) {
//
//		if (rhs.cardinality == Cardinality.ZERO) {
//			return true;
//		}
//		switch (this) {
//		case EQ: return lhs.getValue().equals(rhs.getValue());
//		case IN: return rhs.getValues().contains(lhs.getValue());
//		case MIN: return lhs.getValue().compareTo(rhs.getValue()) >= 0;
//		case MAX: return lhs.getValue().compareTo(rhs.getValue()) <= 0;
//		case LT: return lhs.getValue().compareTo(rhs.getValue()) < 0;
//		case GT: return lhs.getValue().compareTo(rhs.getValue()) > 0;
//		default: return false;
//		}
//	}
}

