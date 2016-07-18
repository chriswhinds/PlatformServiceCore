package com.droitfintech.dataframes.converters;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.droitfintech.dataframes.Cell;
import com.droitfintech.dataframes.Column;
import com.droitfintech.dataframes.Constraint;
import com.droitfintech.dataframes.Dataframe;
import com.droitfintech.dataframes.Row;
import com.droitfintech.dataframes.Value;
import com.droitfintech.dataframes.Value.ValueType;

public class DslConverter {


	public static Dataframe fromDsl(List<String> attributes, List<ValueType> types, List<Constraint> operators, List<Column.Mode> modes, List<List<String>> rows){

		List<Column> newColumns = new LinkedList<Column>();
		List<Row> newRows = new LinkedList<Row>();
		Map<String, Column> columnIndex = new HashMap<String, Column>();
		
		for (int i=0; i<attributes.size(); i++) {
			Column newColumn = new Column(attributes.get(i), types.get(i), operators.get(i), modes.get(i));
			newColumns.add(newColumn);
			columnIndex.put(newColumn.getName(), newColumn);
		}
		
	
		for (List<String> stringRow : rows) {

			Map<Column, Cell> newRow = new LinkedHashMap<Column, Cell>();
			for (int i=0; i < newColumns.size(); i++) {
				String stringCell = stringRow.get(i);
				Column column = newColumns.get(i);
				if (stringCell.equals("!ANY") || stringCell.equals("") || stringCell.equals("*")) {
					newRow.put(column, new Cell());
				}
				else if (operators.get(i) == Constraint.IN) {
					newRow.put(column, new Cell(Value.makeValueList(stringRow.get(i), types.get(i))));
				}
				else {						
					newRow.put(column, new Cell(new Value(stringRow.get(i), types.get(i))));
				}
			}
			newRows.add(new Row(newRow, columnIndex));
		}
		return new Dataframe(newColumns, newRows);
	}
}
