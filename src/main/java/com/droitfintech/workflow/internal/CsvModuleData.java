package com.droitfintech.workflow.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barry on 11/7/15.
 */
public class CsvModuleData {

    private ArrayList<String> headers;
    private ArrayList<List<String>> data;

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

    public ArrayList<List<String>> getData() {
        return data;
    }

    public void setData(ArrayList<List<String>> data) {
        this.data = data;
    }

}
