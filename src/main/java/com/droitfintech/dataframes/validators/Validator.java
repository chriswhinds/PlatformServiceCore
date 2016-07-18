package com.droitfintech.dataframes.validators;

import com.droitfintech.dataframes.Constraint;
import com.droitfintech.dataframes.Dataframe;

public interface Validator {

	public static class ValidationResult {
		public String message = "Successfully validated.";
		public boolean isValid = true;
		public String tableName;
		public String attribute;
		public Constraint operator;
		public int rowNumber;
		public Class<? extends Validator> failure;
	}
	
	public ValidationResult validate(Dataframe dataframe);

}
