package com.droitfintech.datadictionary.converters;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Class used to convert dates from strings bu NamespaceBackedMap.
 * The code is a dup of that in ClassConverter.DateClassConverter used in the trade context conversions and is
 * intended to eventually replace that class,
 * Created by barry on 1/12/16.
 */

public class DateTypeConverter  implements TypeConverter{
    private static Logger log = LoggerFactory.getLogger(DateTypeConverter.class);
    List<DateTimeFormatter> noLongerSupportedFormats = new LinkedList<DateTimeFormatter>() {{
        add(DateTimeFormat.forPattern("MM/dd/yy"));
        add(DateTimeFormat.forPattern("MM/dd/yyyy"));
        add(DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss"));
        add(DateTimeFormat.forPattern("dd-MMM-yyyy"));
        add(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmZZ"));
    }};
    @Override
    public Object convert(String val) throws ConversionException {
        val = val.trim();

        DateTime date = null;
        // First check for miliseconds since jan 1 1970 UTC
        if(StringUtils.isNumeric(val)) {
            date = new DateTime(Long.parseLong(val));
        } else if(val.length() == 10) {
            // ISO Complete date: YYYY-MM-DD (eg 1997-07-16)
            try {
                date = ISODateTimeFormat.date().parseDateTime(val);
            } catch (IllegalArgumentException ignore) {
            }
        } else if(val.length() > 11 && val.charAt(10) == 'T') {
            // ISO Complete date plus hours, minutes, seconds and a decimal fraction of a second
            // YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
            try {
                date = ISODateTimeFormat.dateTime().parseDateTime(val);
            } catch (IllegalArgumentException ignore) {
            }
            // ISO Complete date plus hours, minutes and seconds:
            // YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
            if(date == null) {
                try {
                    date = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(val);
                } catch (IllegalArgumentException ignore) {
                }
            }
        }
        if (date == null) {
            // fallback formats.  At some point these need to be removed as they're
            for (DateTimeFormatter fmt: noLongerSupportedFormats) {
                try {
                    date = fmt.parseDateTime(val);
                    // XXX This needs to be at the warn level but we have so many dates in our system that break
                    // this rule that putting it to trace for now.  *Needs to be cleaned up!*
                    if (log.isTraceEnabled()) {
                        log.trace("Date {} is not in ISO format.  As some point this will be disabled; " +
                                "change your date format to ISO standards.  Was parsed to {}.  Stacktrace for this call:", val, date.toString());
                    }
                    //ExceptionUtils.getStackTrace(new DroitException("Stacktrace output"));
                    break;
                } catch (IllegalArgumentException e) {
                    ; // do nothing.
                }
            }
        }
        if(date == null) {
            throw new ConversionException("could not convert '" + val + "' to date");
        }
        return date.toDate();
    }
}
