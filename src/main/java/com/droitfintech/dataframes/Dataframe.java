package com.droitfintech.dataframes;

/**
 * Created by christopherwhinds on 7/7/16 from the OldBox code base
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;



import com.droitfintech.dataframes.converters.CsvLoader;
import com.droitfintech.dataframes.converters.DslConverter;
import com.droitfintech.dataframes.converters.StringConverter;
import com.droitfintech.dataframes.matchers.FirstMatchStrategy;
import com.droitfintech.dataframes.matchers.MatchStrategy;
import com.droitfintech.dataframes.matchers.MatchStrategy.MatchResult;
import com.droitfintech.dataframes.validators.Validator;
import com.droitfintech.dataframes.validators.Validator.ValidationResult;

public class Dataframe {

    private static Logger log = LoggerFactory.getLogger(Dataframe.class);

    private List<Column> columns = new LinkedList<Column>();
    private List<Row> rows = new LinkedList<Row>();
    private List<Validator> validators = new LinkedList<Validator>();
    private MatchStrategy matchStrategy = new FirstMatchStrategy();
    private String name;

    public Dataframe(List<Column> columns, List<Row> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }
    public void addValidator(Validator validator) {
        this.validators.add(validator);
    }
    public void setMatchStrategy(MatchStrategy matchStrategy) {

        this.matchStrategy = matchStrategy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MatchResult match(Map<String, Object> context) {
        return matchStrategy.match(this, context);
    }

    public static Dataframe fromDsl(
            List<String> attributes,
            List<Value.ValueType> types,
            List<Constraint> operators,
            List<Column.Mode> modes,
            List<List<String>> cells) {
        return DslConverter.fromDsl(attributes, types, operators, modes, cells);
    }

    public List<ValidationResult> validate() {
        List<ValidationResult> results = new LinkedList<ValidationResult>();
        for (Validator validator : validators) {
            ValidationResult result = validator.validate(this);
            if (!result.isValid) {
                results.add(result);
            }
        }
        return results;
    }

    public static Dataframe fromCsv(InputStream source) throws IOException {
        return CsvLoader.retrieve(source);
    }

    public void toCsv(OutputStream destination) {
        CsvLoader.persist(this, destination);
    }

    public String toString() {
        return StringConverter.toString(this);
    }

}
