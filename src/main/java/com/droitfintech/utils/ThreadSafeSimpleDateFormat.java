package com.droitfintech.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Does what SimpleDateFormat does except using Joda to make it threadsafe
 *
 * @author roytruelove
 *
 */
public class ThreadSafeSimpleDateFormat {

    private DateTimeFormatter formatter;

    public ThreadSafeSimpleDateFormat(String pattern) {
        formatter = DateTimeFormat.forPattern(pattern);
        formatter = formatter.withZone(DateTimeZone.getDefault());
    }

    public ThreadSafeSimpleDateFormat(DateTimeFormatter formatter) {
        formatter = formatter.withZone(DateTimeZone.getDefault());
        this.formatter = formatter;
    }

    public void setTimeZone(TimeZone tz) {
        formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
    }

    public Date parse(String value) throws ParseException {
        Date answer = null;
        try {
            DateTime jodaDateTime = formatter.parseDateTime(value);
            answer = jodaDateTime.toDate();
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return answer;
    }

    public String format(Date input) {
        return formatter.print(new DateTime(input));
    }

    public String getTimeZone() {
        return formatter.getZone().getID();
    }
}