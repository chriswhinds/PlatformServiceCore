package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.internal.repository.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by barry on 11/6/15.
 */

@JsonPropertyOrder({ "metadata", "type", "name", "label", "description",
        "version", "tags", "inputs", "outputs", "csvfile" })
public class CsvModule extends Module {
    private String csvfile = "";

    @JsonIgnore
    private CsvModuleData csvData;

    public  String getCsvfile() {
        return csvfile;
    }

    public  void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }

    public CsvModuleData getCsvData() {
        return csvData;
    }

    public void setCsvData(CsvModuleData csvData) {
        this.csvData = csvData;
    }
}
