package com.droitfintech.workflow.internal;

import com.droitfintech.datadictionary.converters.BigDecimalConverter;
import com.droitfintech.datadictionary.converters.DateTypeConverter;
import com.droitfintech.datadictionary.converters.TenorConverter;
import com.droitfintech.dataframes.*;
import com.droitfintech.dataframes.matchers.MatchStrategy;
import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Replacement for ExecutableTableModule that adds the operators available in data frames.
 */
public class ExecutableDataFrame implements ExecutableModule {

    private Logger logger = LoggerFactory.getLogger(ExecutableDataFrame.class);
    private String name;
    private Dataframe dataFrame;
    WorkflowStrictMap<String, Object> defaults;
    private VersionMetadata versionMetadata;

    private ExecutableDataFrame() {
        // Construct defaults (output map used when no match found)
        WorkflowStrictMap<String, Object> defaultsMap = new WorkflowStrictMap<String, Object>();
        defaultsMap.put("match", false);
        defaultsMap.put("matchedRow", -1);
        // this is needed for tables called from chain nodes as was supplied in the old implementation to prevent exceptions.
        defaultsMap.put("continueWorkflow", true);
        this.defaults = defaultsMap;
    }

    public ExecutableDataFrame(CsvModule csvModule) {
        this(); // call no parm constructor.
        try {
            this.name = csvModule.getName();
            this.versionMetadata = csvModule.getMetadata();
            ArrayList<Column> columns = new ArrayList<Column>();
            ArrayList<Row> rows = new ArrayList<Row>();
            Map<String, Column> columnIndex = new HashMap<String, Column>();
            CsvModuleData moduleData = csvModule.getCsvData();
            Column newColumn;
            // create inputs out of csv headers.
            for(String inputName : moduleData.getHeaders()) {
                String[] parts = inputName.split("\\.",2);
                if(parts.length < 2) {
                    throw new WorkflowException("invalid header " + inputName + " in module " + versionMetadata.getId());
                }
                newColumn = new Column(inputName,
                        Value.ValueType.STRING,
                        Constraint.EQ, Column.Mode.INPUT);
                columns.add(newColumn);
                columnIndex.put(inputName, newColumn);
            }
            // Add the hard coded output column
            newColumn = new Column("match",
                    Value.ValueType.BOOLEAN,
                    Constraint.EQ, Column.Mode.OUTPUT);
            columns.add(newColumn);
            columnIndex.put("match", newColumn);
            for(List<String> row : moduleData.getData()) {
                int colIdx = 0;
                Map<Column, Cell> newRow = new HashMap<Column, Cell>();
                for(String rowValue : row) {
                    if(StringUtils.isBlank(rowValue)) {
                        newRow.put(columns.get(colIdx), new Cell());
                    } else {
                        newRow.put(columns.get(colIdx), new Cell(new Value(rowValue, columns.get(colIdx).type)));
                    }
                    colIdx++;
                }
                // add in the hard coded output value.
                newRow.put(columns.get(colIdx), new Cell(new Value(true, columns.get(colIdx).type)));
                rows.add(new Row(newRow, columnIndex));
            }
            dataFrame = new Dataframe(columns, rows);
        } catch (Exception ex) {
            logger.error("Unable to load csv module to data frame.", ex);
        }
    }

    public ExecutableDataFrame(TableModule tableModule) {
        this(); // call no parm constructor.
        try {
            this.name = tableModule.getName();
            this.versionMetadata = tableModule.getMetadata();
            ArrayList<Column> columns = new ArrayList<Column>();
            ArrayList<Row> rows = new ArrayList<Row>();
            ArrayList<ModuleVariable> sourceColumns = new ArrayList<ModuleVariable>();
            Map<String, Column> columnIndex = new HashMap<String, Column>();
            for (ModuleVariable var : tableModule.getInputs()) {
                String colName = var.getModule() + "." + var.getName();
                String op = var.getOperator();
                if(StringUtils.isBlank(op)) {
                    op = "EQ";
                }
                Column newColumn = new Column(colName,
                        Value.ValueType.valueOf(var.getType()),
                        Constraint.valueOf(op), Column.Mode.INPUT);
                columns.add(newColumn);
                sourceColumns.add(var);
                columnIndex.put(colName, newColumn);
            }
            for (ModuleVariable var : tableModule.getOutputs()) {
                String op = var.getOperator();
                if(StringUtils.isBlank(op)) {
                    op = "EQ";
                }
                columns.add(new Column(var.getName(),
                        Value.ValueType.valueOf(var.getType()),
                        Constraint.valueOf(op), Column.Mode.OUTPUT));
                sourceColumns.add(var);
            }
            for (Map<String, Object> rowMap : tableModule.getTable()) {
                Map<Column, Cell> newRow = new HashMap<Column, Cell>();
                for (int colIdx = 0; colIdx < columns.size(); colIdx++) {
                    ModuleVariable sourceVar = sourceColumns.get(colIdx);
                    String colMapName = sourceVar.getModule() + "." + sourceVar.getName();
                    if(!"output".equals(sourceVar.getModule()) && StringUtils.isNotBlank(sourceVar.getOperator())) {
                        // input column names in the table map contain the operator so we can have the same input with
                        // 2 different operators. Output columns do not use the operator but will have it set to IN for array output columns
                        colMapName += sourceVar.getOperator();
                    }
                    Object var = rowMap.get(colMapName);
                    if(var == null || (var instanceof String && StringUtils.isBlank((String)var))) {
                        newRow.put(columns.get(colIdx), new Cell());
                    } else {
                        try {
                            if("IN".equals(sourceVar.getOperator()) || "SUBSET".equals(sourceVar.getOperator())) {
                                if(var instanceof Collection) {
                                    ArrayList<Value> valueArray = new ArrayList<Value>();
                                    Iterator iter = ((Collection) var).iterator();
                                    while (iter.hasNext()) {
                                        Object arrayValue = iter.next();
                                        valueArray.add(new Value(arrayValue, columns.get(colIdx).type));
                                    }
                                    newRow.put(columns.get(colIdx), new Cell(valueArray));
                                }
                            } else {
                                if ("TENOR".equals(sourceVar.getType()) && var instanceof String) {
                                    TenorConverter tc = new TenorConverter();
                                    var = tc.convert((String) var);
                                }
                                if ("DECIMAL".equals(sourceVar.getType()) && var instanceof String) {
                                    BigDecimalConverter dc = new BigDecimalConverter();
                                    var = dc.convert((String) var);
                                }
                                if ("INTEGER".equals(sourceVar.getType()) && var instanceof String) {
                                    try {
                                        var = new Integer((String) var);
                                    } catch (Exception ex) {
                                        var = null;
                                    }
                                }
                                if ("DATE".equals(sourceVar.getType()) && var instanceof String) {
                                    DateTypeConverter dc = new DateTypeConverter();
                                    var = dc.convert((String) var);
                                }
                                newRow.put(columns.get(colIdx), new Cell(new Value(var, columns.get(colIdx).type)));
                            }
                        } catch (Exception ex) {
                            logger.error("Unable to create column.", ex);
                        }
                    }
                }
                rows.add(new Row(newRow, columnIndex));
            }
            dataFrame = new Dataframe(columns, rows);
        } catch (Exception ex) {
            logger.error("Unable to load table module to data frame.", ex);
        }
    }


    public WorkflowStrictMap<String, Object> execute(Evaluator d, boolean collectEscalations) {
        WorkflowStrictMap<String, Object> retMap = new WorkflowStrictMap<String, Object>();
        MatchStrategy.MatchResult result = dataFrame.match(new DataFrameContext(d));
        if(result != null && result.matchFound) {
            retMap.putAll(result.outputBucket);
            retMap.put("match", true);
            retMap.put("matchedRow", result.matchedRowIndex);
            return retMap;
        } else {
            return defaults;
        }

    }


    public String getName() {
        return this.name;
    }


    public VersionMetadata getVersionMetadata() {
        return versionMetadata;
    }
}
