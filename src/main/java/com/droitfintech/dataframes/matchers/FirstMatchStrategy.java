package com.droitfintech.dataframes.matchers;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.dataframes.Column;
import com.droitfintech.dataframes.Dataframe;
import com.droitfintech.dataframes.Row;
import com.droitfintech.dataframes.Column.Mode;


public class FirstMatchStrategy implements MatchStrategy {

	public static Logger logger = LoggerFactory.getLogger(FirstMatchStrategy.class);
	
	public MatchResult match(Dataframe frame, Map<String, Object> context) {
		
		logger.trace("Running match on dataframe {} for context {}", frame.getName(), context);
		
		MatchResult result = new MatchResult();
		
		for (Column column : frame.getColumns()) {
			if (column.mode != Mode.OUTPUT) {
				result.inputBucket.put(column.attribute, context.get(column.attribute));
			}
		}

		int rowIndex = 0;
		for (Row row : frame.getRows()) {
			boolean rowMatches = true;
			for (Column column : frame.getColumns()) {
				//logger.trace("Visiting cell {} = {}", column, row.get(column));
				if (column.mode != Mode.OUTPUT) {
					boolean cellMatches = column.operator.apply(context.get(column.attribute), row.get(column));
					if (! cellMatches) {
						rowMatches = false;
						break;
					}
				}
			}
			if (rowMatches) {
				result.matchFound = true;
				result.matchedRow = row;
				result.matchedRowIndex = rowIndex;
				for (Column column : row.keySet()) {
					if (column.mode == Mode.OUTPUT) {
						result.outputBucket.put(column.attribute, row.get(column).unbox());
					}
				}
				result.inputBucket.putAll(context);
				return result;
			}
			++rowIndex;
		}
		result.matchFound = false;
		return result;
	}
}
