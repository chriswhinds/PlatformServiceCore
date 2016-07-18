package com.droitfintech.dataframes.converters;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.dataframes.Column;
import com.droitfintech.dataframes.Constraint;
import com.droitfintech.dataframes.Dataframe;
import com.droitfintech.dataframes.Row;
import com.droitfintech.dataframes.Value.ValueType;

public class CsvLoader {


	public static void persist(Dataframe dataFrame, OutputStream destination) {

		try {
			int width = dataFrame.getColumns().size();
			String[] attributes = new String[width];
			String[] valueTypes = new String[width];
			String[] operators = new String[width];
			String[] modes = new String[width];

			int i = 0;
			for (Column column : dataFrame.getColumns()) {
				attributes[i] = column.attribute;
				valueTypes[i] = column.type.toString();
				operators[i] = column.operator.toString();
				modes[i] = column.mode.toString();
				i += 1;
			}

			List<String[]> body = new LinkedList<String[]>();
			body.add(attributes);
			body.add(valueTypes);
			body.add(operators);
			body.add(modes);

			for (Row row : dataFrame.getRows()) {
				String[] stringRow = new String[width];
				i = 0;
				for (Column column : dataFrame.getColumns()) {
					if (row.get(column) == null) {
						System.out.println("Unable to find column " + column + " in row " + row);
					}
					stringRow[i] = row.get(column).toString();
					i += 1;
				}
				body.add(stringRow);
			}
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(destination));
			writer.writeAll(body);
			writer.close();		
		}
		catch (Exception e) {
			throw new DroitException("Unable to persist CSV", e);
		}
	}


	public static Dataframe retrieve(InputStream source) throws IOException {
        DroitException.assertNotNull(source, "Dataframe source cannot be null");

        CSVReader csvReader = new CSVReader(new InputStreamReader(source));
        List<String[]> csvRows = csvReader.readAll();
        csvReader.close();

        List<String> attributes = Arrays.asList(csvRows.get(0));

        List<ValueType> types = new LinkedList<ValueType>();
        for (String typeString : csvRows.get(1)) {
            types.add(ValueType.valueOf(typeString));
        }

        List<Constraint> operators = new LinkedList<Constraint>();
        for (String operatorString : csvRows.get(2)) {
            operators.add(Constraint.valueOf(operatorString));
        }

        List<Column.Mode> modes = new LinkedList<Column.Mode>();
        for (String modeString : csvRows.get(3)) {
            modes.add(Column.Mode.valueOf(modeString));
        }

        List<List<String>> rows = new LinkedList<List<String>>();
        for (int i=4; i<csvRows.size(); i++) {
            rows.add(Arrays.asList(csvRows.get(i)));
        }

        return Dataframe.fromDsl(attributes, types, operators, modes, rows);
	}
}

