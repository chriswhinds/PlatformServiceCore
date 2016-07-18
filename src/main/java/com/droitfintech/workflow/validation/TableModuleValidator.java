package com.droitfintech.workflow.validation;

import com.droitfintech.datadictionary.converters.BigDecimalConverter;
import com.droitfintech.datadictionary.converters.BooleanConverter;
import com.droitfintech.datadictionary.converters.DateTypeConverter;
import com.droitfintech.datadictionary.converters.TenorConverter;
import com.droitfintech.workflow.internal.ModuleVariable;
import com.droitfintech.workflow.internal.TableModule;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by barry on 3/11/16.
 */
public class TableModuleValidator {
    public static ValidationResponse validate(TableModule module) {
        ValidationResponse ret = new ValidationResponse(true, module);
        // validate that outputs are present in every row
        int rowNumber = 0;
        for(Map<String, Object> rowMap : module.getTable()) {
            ++rowNumber;
            int columnNumber = module.getInputs().size() + 1;
            for(ModuleVariable output : module.getOutputs())  {
                Object value = rowMap.get(output.getModule() + "." + output.getName());
                if(value == null || (value instanceof String && StringUtils.isBlank((String)value))) {
                    ret.addTableModuleError(new ValidationResponse.TableRowError(rowNumber,
                            String.format("Row %d, All outputs must contain values", rowNumber)));
                    break;
                }
                else {
                    // if value is supplied check that it is convertible into the proper type and if so
                    // add it to the list of output's for the module. This is the list used to provide autocomplete
                    // for the module.
                    Object res = validateTypeConversion(output, value);
                    if(res != null) {
                        Map<String, Object> map = new HashMap<String,Object>();
                        map.put(output.getName(), res);
                        ret.addOutputVariables(map);
                    } else {
                        ret.addTableModuleError(new ValidationResponse.TableRowError(rowNumber,
                                String.format("Row %d, Can not convert output value in column %s to %s",
                                        rowNumber, columnNumber, output.getType())));
                    }
                }
                ++columnNumber;
            }
            // check inputs for proper type conversion.
            columnNumber = 1;
            for(ModuleVariable input : module.getInputs())  {
                String rowKey = input.getModule() + "." + input.getName();
                if(input.getOperator() != null) {
                    rowKey += input.getOperator();
                    Object value = rowMap.get(rowKey);
                    if(value != null && ! (value instanceof String && StringUtils.isBlank((String)value))) {
                        Object res = validateTypeConversion(input, value);
                        if(res == null) {
                            ret.addTableModuleError(new ValidationResponse.TableRowError(rowNumber,
                                    String.format("Row %d, Can not convert input value in column %s to %s",
                                            rowNumber, columnNumber, input.getType())));
                        }
                    }
                }
                ++columnNumber;
            }
        }
        // validate that there are no duplicate rows.
        if(module.getTable().size() > 1) {
            for (int idx = 0; idx < module.getTable().size() - 1; idx++) {
                Map<String, Object> rowMap = module.getTable().get(idx);
                // Start comparing with the row below
                for(int comRowIdx = idx + 1; comRowIdx < module.getTable().size(); comRowIdx++) {
                    Map<String, Object> rowMapComp = module.getTable().get(comRowIdx);
                    boolean noMatch = false;
                    boolean wildcardOverridesValue = false;
                    for(String key : rowMap.keySet()) {
                        Object v1 = rowMap.get(key);
                        Object v2 = rowMapComp.get(key);
                        // convert blank strings to nulls
                        if(v1 != null && v1 instanceof String && StringUtils.isBlank((String)v1))
                            v1 = null;
                        if(v2 != null && v2 instanceof String && StringUtils.isBlank((String)v2))
                            v2 = null;
                        if(v1 == null && v2 == null){

                        }
                        else if(v1 != null && v2 == null) {
                            noMatch = true;
                        }
                        else if(v1 == null && v2 != null) {
                            // this row may override the one we are comparing against.
                            wildcardOverridesValue = true;
                        }
                        else {
                            if(v1 instanceof Boolean) {
                                noMatch = ! ((Boolean)v1) == ((Boolean)v2);
                            }
                            else if(v1 instanceof String) {
                                noMatch = ! ((String)v1).equals(v2);
                            }
                            else if(v1 instanceof Collection) {
                                if(v2 instanceof Collection) {
                                    Collection c1 = (Collection)v1;
                                    Collection c2 = (Collection)v2;
                                    if(c1.size() != c2.size()) {
                                        noMatch = true;
                                    } else {
                                        Iterator c1Iter = c1.iterator();
                                        Iterator c2Iter = c2.iterator();
                                        while(c1Iter.hasNext() && c2Iter.hasNext()) {
                                            v1 = c1Iter.next();
                                            v2 = c2Iter.next();
                                            if(v1 instanceof Boolean) {
                                                noMatch = ! ((Boolean)v1) == ((Boolean)v2);
                                            }
                                            else if(v1 instanceof String) {
                                                noMatch = ! ((String)v1).equals(v2);
                                            }
                                            if(noMatch) { // exit on first diff
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    noMatch = true;
                                }
                            }
                        }
                        if(noMatch) { // exit on first diff
                            break;
                        }
                    }
                    if(!noMatch) {
                        if(wildcardOverridesValue) {
                            ret.addTableModuleError(new ValidationResponse.TableRowError(idx + 1,
                                    String.format("Row %d and %d contain duplicate input values. move row %d above row %d if you want the more specific values to match",
                                            idx + 1, comRowIdx + 1, comRowIdx + 1, idx + 1)));
                        } else {
                            ret.addTableModuleError(new ValidationResponse.TableRowError(idx + 1,
                                    String.format("Row %d and %d contain duplicate input values.", idx + 1, comRowIdx + 1)));
                        }
                    }
                }

            }
        }
        return ret;
    }

    private static Object validateTypeConversion(ModuleVariable mv, Object value) {
        if(value == null || (value instanceof String && StringUtils.isBlank((String)value))) {
            return null; // OK
        }
        if("STRING".equals(mv.getType()) ) {
            return value.toString();
        }
        try {
            if ("BOOLEAN".equals(mv.getType())) {
                if (value instanceof Boolean) {
                    return value;
                }
                return new BooleanConverter().convert(value.toString());
            }
            if ("INTEGER".equals(mv.getType())) {
                return new Integer(value.toString());
            }
            if ("DATE".equals(mv.getType())) {
                return new DateTypeConverter().convert(value.toString());
            }
            if ("DECIMAL".equals(mv.getType())) {
                return new BigDecimalConverter().convert(value.toString());
            }
            if ("TENOR".equals(mv.getType())) {
                return new TenorConverter().convert(value.toString());
            }
        } catch (Exception ignore) { }
        return null;
    }
}
