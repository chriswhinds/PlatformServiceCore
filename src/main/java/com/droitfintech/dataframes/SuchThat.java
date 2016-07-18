package com.droitfintech.dataframes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.droitfintech.dataframes.Cell.Cardinality;

public class SuchThat {

	private Map<Constraint, Cell> constraintMap = new HashMap<Constraint, Cell>();

	public Cell get(Constraint operator) {
		return constraintMap.get(operator);
	}
	
	public Map<Constraint, Cell> getAll() {
		return constraintMap;
	}

	public boolean add(Constraint operator, Cell cell) {

		if (cell.cardinality == Cardinality.ZERO) {
			return true;
		}
		if (constraintMap.get(operator) == null) {
			constraintMap.put(operator, cell);
		}
		Cell lhs = cell;
		if (operator == Constraint.EQ) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqEq(lhs, rhs))  return false; break;
				case IN:  if (!resolveEqIn(lhs, rhs))  return false; break;
				case MIN: if (!resolveEqMin(lhs, rhs)) return false; break;
				case MAX: if (!resolveEqMax(lhs, rhs)) return false; break;
				case LT:  if (!resolveEqLt(lhs, rhs))  return false; break;
				case GT:  if (!resolveEqGt(lhs, rhs))  return false; break;
				}			
			}
		}
		else if (operator == Constraint.IN) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqIn(rhs, lhs))  return false; break;
				case IN:  if (!resolveInIn(lhs, rhs))  return false; break;
				case MIN: if (!resolveInMin(lhs, rhs)) return false; break;
				case MAX: if (!resolveInMax(lhs, rhs)) return false; break;
				case LT:  if (!resolveInLt(lhs, rhs))  return false; break;
				case GT:  if (!resolveInGt(lhs, rhs))  return false; break;
				}
			}
		}
		else if (operator == Constraint.MIN) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqMin(rhs, lhs))  return false; break;
				case IN:  if (!resolveInMin(rhs, lhs))  return false; break;
				case MIN: if (!resolveMinMin(lhs, rhs)) return false; break;
				case MAX: if (!resolveMinMax(lhs, rhs)) return false; break;
				case LT:  if (!resolveMinLt(lhs, rhs))  return false; break;
				case GT:  if (!resolveMinGt(lhs, rhs))  return false; break;
				}
			}
		}
		else if (operator == Constraint.MAX) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqMax(rhs, lhs))  return false; break;
				case IN:  if (!resolveInMax(rhs, lhs))  return false; break;
				case MIN: if (!resolveMinMax(rhs, lhs)) return false; break;
				case MAX: if (!resolveMaxMax(lhs, rhs)) return false; break;
				case LT:  if (!resolveMaxLt(lhs, rhs))  return false; break;
				case GT:  if (!resolveMaxGt(lhs, rhs))  return false; break;
				}
			}
		}
		else if (operator == Constraint.LT) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqLt(rhs, lhs))  return false; break;
				case IN:  if (!resolveInLt(rhs, lhs))  return false; break;
				case MIN: if (!resolveMinLt(rhs, lhs)) return false; break;
				case MAX: if (!resolveMaxLt(rhs, lhs)) return false; break;
				case LT:  if (!resolveLtLt(lhs, rhs))  return false; break;
				case GT:  if (!resolveLtGt(lhs, rhs))  return false; break;
				}
			}
		}		
		else if (operator == Constraint.GT) {
			for (Constraint o : constraintMap.keySet()) {
				Cell rhs = constraintMap.get(o);
				switch(o) { 
				case EQ:  if (!resolveEqGt(rhs, lhs))  return false; break;
				case IN:  if (!resolveInGt(rhs, lhs))  return false; break;
				case MIN: if (!resolveMinGt(rhs, lhs)) return false; break;
				case MAX: if (!resolveMaxGt(rhs, lhs)) return false; break;
				case LT:  if (!resolveLtGt(rhs, lhs))  return false; break;
				case GT:  if (!resolveGtGt(lhs, rhs))  return false; break;
				}
			}
		}
		purgeRedundant();
		return true;
	}
	
	protected void purgeRedundant() {
		if (constraintMap.containsKey(Constraint.EQ)) {
			Cell eqCell = constraintMap.get(Constraint.EQ);
			constraintMap.clear();
			constraintMap.put(Constraint.EQ, eqCell);
		}
		else if (constraintMap.containsKey(Constraint.IN)) {
			Cell inCell = constraintMap.get(Constraint.IN);
			constraintMap.clear();
			constraintMap.put(Constraint.IN, inCell);
		}
		else {
			if (constraintMap.containsKey(Constraint.MIN)) {
				constraintMap.remove(Constraint.GT);
			}
			if (constraintMap.containsKey(Constraint.MAX)) {
				constraintMap.remove(Constraint.LT);
			}
		}
	}

	protected boolean resolveEqEq(Cell c1, Cell c2) {
		return c1.getValue().equals(c2.getValue());
	}

	protected boolean resolveEqIn(Cell eqCell, Cell inCell) {
		return inCell.getValues().contains(eqCell.getValue());
	}
	
	protected boolean resolveEqMin(Cell eqCell, Cell minCell) {
		return minCell.getValue().compareTo(eqCell.getValue()) <= 0;
	}

	protected boolean resolveEqMax(Cell eqCell, Cell maxCell) {
		return maxCell.getValue().compareTo(eqCell.getValue()) >= 0;
	}

	protected boolean resolveEqLt(Cell eqCell, Cell ltCell) {
		return ltCell.getValue().compareTo(eqCell.getValue()) > 0;
	}

	protected boolean resolveEqGt(Cell eqCell, Cell gtCell) {
		return gtCell.getValue().compareTo(eqCell.getValue()) < 0;
	}

	protected boolean resolveInIn(Cell c1, Cell c2) {
		List<Value> intersection = new LinkedList<Value>();
		intersection.addAll(c1.getValues());
		intersection.retainAll(c2.getValues());
		if (intersection.isEmpty()) {
			return false;
		}
		constraintMap.put(Constraint.IN, new Cell(intersection));				
		return true;
	}
	
	protected boolean resolveInMin(Cell in, Cell min) {
		List<Value> filtered = new LinkedList<Value>();
		Value minValue = min.getValue();
		for (Value value : in.getValues()) {
			if (value.compareTo(minValue) >= 0) filtered.add(value);
		}
		if (filtered.isEmpty()) return false;
		constraintMap.put(Constraint.IN, new Cell(filtered));
		return true;
	}
	
	protected boolean resolveInMax(Cell in, Cell max) {
		List<Value> filtered = new LinkedList<Value>();
		Value maxValue = max.getValue();
		for (Value value : in.getValues()) {
			if (value.compareTo(maxValue) <= 0) filtered.add(value);
		}
		if (filtered.isEmpty()) return false;
		constraintMap.put(Constraint.IN, new Cell(filtered));
		return true;
	}

	protected boolean resolveInLt(Cell in, Cell lt) {
		List<Value> filtered = new LinkedList<Value>();
		Value ltValue = lt.getValue();
		for (Value value : in.getValues()) {
			if (value.compareTo(ltValue) < 0) filtered.add(value);
		}
		if (filtered.isEmpty()) return false;
		constraintMap.put(Constraint.IN, new Cell(filtered));
		return true;
	}

	protected boolean resolveInGt(Cell in, Cell gt) {
		List<Value> filtered = new LinkedList<Value>();
		Value gtValue = gt.getValue();
		for (Value value : in.getValues()) {
			if (value.compareTo(gtValue) > 0) filtered.add(value);
		}
		if (filtered.isEmpty()) return false;
		constraintMap.put(Constraint.IN, new Cell(filtered));
		return true;
	}

	protected boolean resolveMinMin(Cell c1, Cell c2) {
		if (c1.getValue().compareTo(c2.getValue()) > 0) {
			constraintMap.put(Constraint.MIN, c1);
		} else {
			constraintMap.put(Constraint.MIN, c2);			
		}
		return true;
	}

	protected boolean resolveMinMax(Cell min, Cell max) {
		return min.getValue().compareTo(max.getValue()) <= 0;
	}

	protected boolean resolveMinLt(Cell min, Cell lt) {
		return min.getValue().compareTo(lt.getValue()) < 0;
	}
	
	protected boolean resolveMinGt(Cell min, Cell gt) {
		return min.getValue().compareTo(gt.getValue()) > 0;
	}

	protected boolean resolveMaxMax(Cell c1, Cell c2) {
		if (c1.getValue().compareTo(c2.getValue()) < 0) {
			constraintMap.put(Constraint.MAX, c1);
		} else {
			constraintMap.put(Constraint.MAX, c2);			
		}
		return true;
	}

	protected boolean resolveMaxLt(Cell max, Cell lt) {
		return max.getValue().compareTo(lt.getValue()) < 0;
	}
	
	protected boolean resolveMaxGt(Cell maxCell, Cell gtCell) {
		return gtCell.getValue().compareTo(maxCell.getValue()) < 0;
	}
	
	protected boolean resolveLtLt(Cell c1, Cell c2) {
		if (c1.getValue().compareTo(c2.getValue()) < 0) {
			constraintMap.put(Constraint.LT, c1);
		} else {
			constraintMap.put(Constraint.LT, c2);			
		}
		return true;
	}

	protected boolean resolveLtGt(Cell lt, Cell gt) {
		return lt.getValue().compareTo(gt.getValue()) > 0;
	}
	
	protected boolean resolveGtGt(Cell c1, Cell c2) {
		if (c1.getValue().compareTo(c2.getValue()) > 0) {
			constraintMap.put(Constraint.GT, c1);
		} else {
			constraintMap.put(Constraint.GT, c2);			
		}
		return true;
	}
}