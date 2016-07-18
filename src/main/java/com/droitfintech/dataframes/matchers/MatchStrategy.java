package com.droitfintech.dataframes.matchers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.droitfintech.dataframes.Dataframe;
import com.droitfintech.dataframes.Row;

public interface MatchStrategy {

	public static class MatchResult {

		public boolean matchFound;
		public int matchedRowIndex = -1;
		public Row matchedRow;
		public Map<String, Object> inputBucket = new LinkedHashMap<String, Object>();
		public Map<String, Object> outputBucket = new LinkedHashMap<String, Object>();
	}

	public MatchResult match(Dataframe dataFrame, Map<String, Object> inputBucket);

}
