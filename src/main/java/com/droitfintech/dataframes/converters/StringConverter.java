package com.droitfintech.dataframes.converters;

import java.util.List;

import com.droitfintech.dataframes.Column;
import com.droitfintech.dataframes.Dataframe;
import com.droitfintech.dataframes.Row;

public class StringConverter {

	public static String toString(Dataframe frame) {
		
		List<Column> columns = frame.getColumns();
		List<Row> rows = frame.getRows();
		
		StringBuilder result = new StringBuilder();
		result.append("\n");
		int intraCellWidth = 12;
		int interCellWidth = 2;
		String dashes = multiply("-", intraCellWidth);
		String spaces = multiply(" ", interCellWidth);

		for (Column column : columns) {
			result.append(formatCell(column.attribute, intraCellWidth) + spaces); 
		}
		result.append("\n");
		for (Column column : columns) {
			result.append(formatCell(column.type.toString().toLowerCase(), intraCellWidth) + spaces); 
		}
		result.append("\n");
		for (Column column : columns) {
			result.append(formatCell(column.operator.toString().toLowerCase(), intraCellWidth) + spaces); 
		}
		result.append("\n");
		for (Column column : columns) {
			result.append(formatCell(column.mode.toString().toLowerCase(), intraCellWidth) + spaces); 
		}
		result.append("\n");
		result.append(multiply(dashes+spaces, columns.size()));
		result.append("\n");
		
		for (Row row : rows) {
			for (Column column : columns) {
				String cell;
				if (row.get(column) == null) {
					cell = "*";
				} else {
					cell = row.get(column).toString();
				}
				result.append(formatCell(cell, intraCellWidth) + spaces);
			}
			result.append("\n");
		}
		result.append("\n");
		return result.toString();
	}
	
	private static String formatCell(String string, int len) {
		String s = new String(string);
		int spaces = len-s.length();
		for (int i=0; i<spaces; i++) {
			s += " ";
		}
		if (s.length() > len) {
			s = s.substring(0, len-1) + "_";				
		}
		return s;
	}
	
	private static String multiply(String s, int n) {
		StringBuilder b = new StringBuilder();
		for (int i=0; i<n; i++) {
			b.append(s);
		}
		return b.toString();
	}
}
