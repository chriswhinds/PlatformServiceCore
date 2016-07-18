package com.droitfintech.datadictionary.attribute;


import com.droitfintech.datadictionary.converters.*;

/**
 * Types used in the data dictionary. This replaces @TradeAttribute.AttributeType and has many less values since all
 * string attributes with validation tables are handled by the new EnumeratedString type.
 */
public enum Type {
	BigDecimal("java.math.BigDecimal", new BigDecimalConverter()),
	Boolean("java.lang.Boolean", new BooleanConverter()),
	DateType("java.util.Date", new DateTypeConverter()),
	Integer("java.lang.Integer", null),
	ProductMasterType("com.droitfintech.tdss.model.eligibility.ProductMaster", null),
	EnumeratedString("java.lang.String", null),
	String("java.lang.String", null),
	TenorType("com.droitfintech.core.regulatory.Tenor", new TenorConverter()),
	TradingHoursType("com.droitfintech.tdss.model.basic.TradingWeekHoursSet", null);

	public final String className;
	public final TypeConverter converter;

	Type(String className, TypeConverter converter) {
		this.className = className;
		this.converter = converter;
	}

	public static Type getTypeForObject(Object o) {
		if(o != null) {
			String cName = o.getClass().getName();
			if (cName != null) {
				if (cName.equals(BigDecimal.className))
					return BigDecimal;
				if (cName.equals(Boolean.className))
					return Boolean;
				if (cName.equals(DateType.className))
					return DateType;
				if (cName.equals(Integer.className))
					return Integer;
				if (cName.equals(ProductMasterType.className))
					return ProductMasterType;
				if (cName.equals(EnumeratedString.className))
					return EnumeratedString;
				if (cName.equals(String.className))
					return String;
				if (cName.equals(TenorType.className))
					return TenorType;
				if (cName.equals(TradingHoursType.className))
					return TradingHoursType;
			}
		}
		return null;
	}

	public TypeConverter getConverter() {
		return converter;
	}
}
