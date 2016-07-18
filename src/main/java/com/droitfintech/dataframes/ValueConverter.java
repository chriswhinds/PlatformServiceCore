package com.droitfintech.dataframes;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.regulatory.Tenor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by christopherwhinds on 7/7/16. Copied from OLDBox base line code
 */
public abstract class ValueConverter<T> {

    public static String NULL_SPECIAL_VALUE = "!NULL";

    public T convert(String val) {

        T answer = null;

        val = val.trim();

        if (StringUtils.isBlank(val) || val.equals(NULL_SPECIAL_VALUE)) {
            return null;
        }
        try {
            answer = convertInner(val);
        } catch (Exception e) {

            throw new DroitException("Could not convert value '" + val + "' using " +
                    this.getClass().getCanonicalName());
        }

        return answer;
    }

    protected abstract T convertInner(String val);

    public String toString(Object o){
        return o.toString();
    }

    public static String toStringStatic(Object o) {
        return getConverter(o.getClass()).toString(o);
    }

    @SuppressWarnings("unchecked")
    public static <E> ValueConverter<E> getConverter(Class<E> inClazz) {
        if (!CONVERTERS.containsKey(inClazz)) {
            throw new DroitException("Converter not found for class " + inClazz);
        }
        return (ValueConverter<E>) CONVERTERS.get(inClazz);
    }

    private static Map<Class<?>, ValueConverter<?>> CONVERTERS = new HashMap<Class<?>, ValueConverter<?>>() {{
        put(String.class, new ValueConverter<String>() {
            @Override
            public String convertInner(String val) {
                return val;
            }
        });
        put(Boolean.class, new ValueConverter<Boolean>() {
            @Override
            public Boolean convertInner(String val) {
                return Boolean.parseBoolean(val);
            }
        });

        put(LocalDate.class, new ValueConverter<LocalDate>(){
            private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
            @Override
            public LocalDate convertInner(String o) {
                return fmt.parseLocalDate(o);
            }
            @Override
            public String toString(Object o){
                LocalDate d = (LocalDate) o;
                return fmt.print(d);
            };
        });

        put(Date.class, new ValueConverter<Date>(){
            DateTimeFormatter canonical = DateTimeFormat.forPattern("yyyy-MM-dd");
            List<DateTimeFormatter> formats = new LinkedList<DateTimeFormatter>() {{
                add(DateTimeFormat.forPattern("MM/dd/yy"));
                add(DateTimeFormat.forPattern("MM/dd/yyyy"));
                add(DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss"));
                add(DateTimeFormat.forPattern("dd-MMM-yyyy"));
                add(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmZZ"));
                add(canonical);
            }};

            @Override
            public Date convertInner(String val) {
                LocalDate date = null;
                for (DateTimeFormatter fmt: formats) {
                    try {
                        date = fmt.parseLocalDate(val.trim());
                        break;
                    } catch (IllegalArgumentException e) {
                        ; // do nothing.
                    }
                }
                DroitException.assertThat(date!=null, "This date could not be parsed properly: " + val);
                return date.toDate();
            }
            @Override
            public String toString(Object o){
                Date d = (Date) o;
                return canonical.print(new LocalDate(d));
            };
        });

        put(Integer.class, new ValueConverter<Integer>(){
            @Override
            public Integer convertInner(String o) {
                return Integer.parseInt(o);
            }
        });

        put(BigDecimal.class, new ValueConverter<BigDecimal>(){
            @Override
            public BigDecimal convertInner(String o) {
                return new BigDecimal(o.replaceAll(",", ""));
            }
        });


        put(Tenor.class, new ValueConverter<Tenor>(){
            @Override
            public Tenor convertInner(String item) {
                return Tenor.makeTenor(item);
            }

            @Override
            public String toString(Object o) {
                return ((Tenor)o).toDbValue();
            }
        });

    }};

    public static <T> Set<T> makeSet(Class<T> clazz, String val) {
        Set<T> l = new LinkedHashSet<T>();
        addToCollection(clazz, val, l);
        return l;
    }

    public static <T> void addToCollection(Class<T> clazz, String strVal, Collection<T> col) {
        if (strVal == null || strVal.trim().isEmpty()) return;

        // Negative lookback for escape forward slash
        String[] strings = strVal.split("(?<!\\\\),");
        for (String item: strings) {
            item = item.trim();
            if (!item.isEmpty()) {
                col.add(clazz.cast(ValueConverter.getConverter(clazz).convert(item.replaceAll("\\\\,",","))));
            }
        }
    }

}
