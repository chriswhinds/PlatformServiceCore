package com.droitfintech.dataframes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.dataframes.Column.Mode;
import com.droitfintech.dataframes.Value.ValueType;


public class JoinOperation {

	
	public static Dataframe join(Dataframe lhFrame, Dataframe rhFrame) {
				
		List<Map<String, SuchThat>> merged = new LinkedList<Map<String, SuchThat>>();

		for (Row lhRow : lhFrame.getRows()) {
			boolean matchFound = false;
			for (Row rhRow : rhFrame.getRows()) {
				Map<String, SuchThat> mergedRow = mergeRow(lhRow, rhRow);
				if (mergedRow != null) {
					matchFound = true;
					merged.add(mergedRow);
				}
			}
			if (!matchFound) {
				System.out.println("Unable to match row: " + lhRow);
			}
		}
		List<Column> newColumns = calculateColumns(lhFrame.getColumns(), rhFrame.getColumns(), merged);
		List<Row> newRows = calculateRows(newColumns, merged);
		String name = "(" + lhFrame.getName() +" + " + rhFrame.getName() + ")";
		Dataframe frame = new Dataframe(newColumns, newRows);
		frame.setName(name);
		return frame;

	}
	
	
	public static Map<String, SuchThat> mergeRow(Row lhs, Row rhs) {
		
		Map<String, SuchThat> constraints = new HashMap<String, SuchThat>();
		
		for (Column oldColumn : lhs.keySet()) {
			if (constraints.get(oldColumn.attribute) == null) {
				constraints.put(oldColumn.attribute, new SuchThat());
			}
			boolean consistent = constraints.get(oldColumn.attribute).add(oldColumn.operator, lhs.get(oldColumn));
			if (!consistent) return null;
		}
		for (Column oldColumn : rhs.keySet()) {
			if (constraints.get(oldColumn.attribute) == null) {
				constraints.put(oldColumn.attribute, new SuchThat());
			}
			boolean consistent = constraints.get(oldColumn.attribute).add(oldColumn.operator, rhs.get(oldColumn));
			if (!consistent) return null;
		}
		return constraints;
	}


	
	private static List<Column> calculateColumns(List<Column> lhCols, List<Column> rhCols, List<Map<String, SuchThat>> body) {
			
		Map<String, Mode> modes = new LinkedHashMap<String, Mode>();
		Map<String, ValueType> types = new HashMap<String, ValueType>();

		for (Column col : lhCols) {
			Mode lastMode = modes.get(col.attribute);
			if (lastMode == null || lastMode == Mode.INPUT) {
				modes.put(col.attribute, col.mode);
			}
			types.put(col.attribute, col.type);
		}
		for (Column col : rhCols) {
			Mode lastMode = modes.get(col.attribute);
			if (lastMode == null || lastMode == Mode.INPUT) {
				modes.put(col.attribute, col.mode);
			}
			ValueType lhType = types.get(col.attribute);
			if (lhType == null) {
				types.put(col.attribute, col.type);
			} 
			else if (lhType != col.type) {
				throw new DroitException("Incompatible types for attibute " + col.attribute + ": " + lhType + " vs " + col.type);
			}
		}

		List<Column> columns = new LinkedList<Column>();
		LinkedHashMap<String, Set<Constraint>> columnOperations = new LinkedHashMap<String, Set<Constraint>>();
		
		for (String attribute : modes.keySet()) {
			columnOperations.put(attribute, new HashSet<Constraint>());
		}
		for (Map<String, SuchThat> row : body) {
			for (String attribute : row.keySet()) {
				columnOperations.get(attribute).addAll(row.get(attribute).getAll().keySet());
			}
		}
		for (String attribute : columnOperations.keySet()) {
			for (Constraint operator : Constraint.values()) {
				if (columnOperations.get(attribute).contains(operator)) {
					columns.add(new Column(attribute, types.get(attribute), operator, modes.get(attribute)));
				}
			}
		}
		return columns;
	}
	
	private static List<Row> calculateRows(List<Column> columns, List<Map<String, SuchThat>> body) {

		Map<String, Column> index = new HashMap<String, Column>();
		for (Column column : columns) {
			index.put(column.getName(), column);
		}
		
		List<Row> rows = new LinkedList<Row>();
		for (Map<String, SuchThat> oldRow : body) {
			Map<Column, Cell> newRow = new HashMap<Column, Cell>();
			for (Column column : columns) {
				Cell cell = oldRow.get(column.attribute).get(column.operator);
				if (cell == null) {
					cell = new Cell();
				}
				newRow.put(column, cell);
			}
			rows.add(new Row(newRow, index));	
		}
		return rows;
	}
}

