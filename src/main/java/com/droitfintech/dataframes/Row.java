package com.droitfintech.dataframes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.droitfintech.exceptions.DroitException;

public class Row {

	/*
	 * THOUGHTS
	 * Implementation details should be hidden from end-user. I'm tempted
	 * to extend HashMap instead of enclosing it, to avoid the extra indirection, but
	 * doing so ultimately gives the end-user the wrong impression about what 
	 * they are allowed to do, not to mention curtails future modifications.
	 * 
	 * I still haven't decided whether I really like using Maps everywhere. I feel like
	 * this scenario is perfect for a plain old array. The only reasons I haven't cut over
	 * already are:
	 *    - I don't like the end user having to use an old-style for loop.
	 *    - Column ordering is unstable across JOINs.
	 *
	 * So this is still sort of an option:
	 *    private Map<String, Integer> index;
	 *    private ArrayList<Cell> underlying;
	 *    
	 * or even
	 *    private ArrayList<Set<Value>> underlying;
	 *    
	 * Do we even need Cell anymore?
	 */    
	
	private Map<Column, Cell> underlying;
	private Map<String, Column> index;
	
	public Row(Map<Column, Cell> underlying, Map<String, Column> index) {
		this.underlying = underlying;
		this.index = index;
	}

	public Cell getCell(String columnName) {
		Column column = index.get(columnName);
		if (column == null) {
			throw new DroitException("Unable to find column " + columnName + 
					". Valid columnNames are " + index.keySet());
		}
		return underlying.get(column);
	}

	public Set<Column> keySet() {
		return underlying.keySet();
	}

	public Cell get(Column column) {
		return underlying.get(column);
	}
	
	/*
	 * Convenience methods for interacting with Rows
	 */
	public Value getValue(String columnName) {
		return getCell(columnName).getValue();
	}
	
	public List<Value> getValues(String columnName) {
		return getCell(columnName).getValues();
	}
	
	public String getString(String columnName) {
		return getCell(columnName).getValue().toString();
	}

	public Integer getInteger(String columnName) {
		return (Integer) getCell(columnName).getValue().getUnderlying();
	}

	public Date getDate(String columnName) {
		return (Date) getCell(columnName).getValue().getUnderlying();
	}
	
	public BigDecimal getBigDecimal(String columnName) {
		return (BigDecimal) getCell(columnName).getValue().getUnderlying();		
	}
	
	public List<Object> getList(String columnName) {
		List<Object> result = new LinkedList<Object>();
		for (Value value : getCell(columnName).getValues()) {
			result.add(value.getUnderlying());
		}
		return result;
	}

	public Map<String, Object> asBucket() {
		return null;
	}
	
	public String toString() {
		return underlying.toString();
	}

}

