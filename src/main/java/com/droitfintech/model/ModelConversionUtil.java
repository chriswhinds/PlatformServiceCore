package com.droitfintech.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.droitfintech.model.MustContain;
import com.droitfintech.regulatory.Tenor;
import com.droitfintech.model.TradingDayHours;
import com.droitfintech.model.TradingWeekHoursSet;
import com.droitfintech.model.ProductMaster;
import com.droitfintech.exceptions.DroitException;
import com.droitfintech.model.ClassConverter;

/**
 * ModelConversionUtil.java
 * COPYRIGHT (C) 2013 Droit Financial Technologies, LLC
 *
 * @author jisoo
 *
 */
public class ModelConversionUtil {

    private ConcurrentMap<String, ConcurrentMap<String[], Object>> cacheStore= new ConcurrentHashMap<String, ConcurrentMap<String[], Object>>();

    public static final Pattern TERM_LIMITS_BY_TENOR = Pattern.compile("\\(([^\\(\\)]+)\\)");

    private static final Map<String, Integer> DAY_NAMES_TO_JODATIME_DAYOFWEEK = new HashMap<String, Integer>() {{
        put("monday", DateTimeConstants.MONDAY);
        put("tuesday", DateTimeConstants.TUESDAY);
        put("wednesday", DateTimeConstants.WEDNESDAY);
        put("thursday", DateTimeConstants.THURSDAY);
        put("friday", DateTimeConstants.FRIDAY);
        put("saturday", DateTimeConstants.SATURDAY);
        put("sunday", DateTimeConstants.SUNDAY);
    }};

    public static Tenor getTermLimitsByTenor(String termLimit, Tenor t) {
        if (termLimit == null) {
            return null;
        }
        Map<Tenor, Tenor> tenorMap = getTermLimitsByTenorMap(termLimit);
        if (tenorMap == null) {
            return ModelConversionUtil.makeTenor(termLimit);
        }
        // We can simply return null if t is not found;
        // the invalid index tenor will be caught elsewhere
        return tenorMap.get(t);
    }

    public static Map<Tenor, Tenor> getTermLimitsByTenorMap(String mapString) {
        Matcher m = TERM_LIMITS_BY_TENOR.matcher(mapString);
        if (m.matches()) {
            mapString = m.group(1);
            String[] perTenors = StringUtils.split(mapString, ';');
            Map<Tenor, Tenor> res = new HashMap<Tenor, Tenor>();
            for (String perTenor: perTenors) {
                String[] entry = StringUtils.split(perTenor,',');
                Tenor key = ModelConversionUtil.makeTenor(entry[0].trim());
                Tenor value = ModelConversionUtil.makeTenor(entry[1].trim());
                res.put(key, value);
            }
            return res;
        }
        return null;
    }

    public static Tenor makeTenor(String val) {
        if (val == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("(-?[0-9]{1,7})([DWMYT])");
        Matcher matcher = pattern.matcher(val.toUpperCase());
        if (matcher.find()) {
            int multiplier = Integer.parseInt(matcher.group(1));
            String period = matcher.group(2);
            return createTenor(multiplier, period);
        }
        return null;
    }

    public static Tenor createTenor(int multiplier, String period) {
        Tenor tenor = new Tenor();
        tenor.setMultiplier(multiplier);
        tenor.setPeriod(period);
        return tenor;
    }

    public static TreeSet<TradingDayHours> getTradingDayHoursSet (String tradingDayHours) {

        TreeSet<TradingDayHours> set = new TreeSet<TradingDayHours>();
        if (tradingDayHours == null) return set;

        Pattern p = Pattern.compile("([a-zA-Z]{3,9}),\\s{0,2}(\\d{1,2}):(\\d{2})(:\\d{2})?,\\s{0,2}(\\d{1,2}):(\\d{2})(:\\d{2})?");
        Matcher m = p.matcher(tradingDayHours);

        while (m.find()) {

            int day = DAY_NAMES_TO_JODATIME_DAYOFWEEK.get(m.group(1).toLowerCase());
            int startHour = Integer.parseInt(m.group(2));
            int startMin = Integer.parseInt(m.group(3));

            int endHour = Integer.parseInt(m.group(5));
            int endMin = Integer.parseInt(m.group(6));

            set.add(new TradingDayHours(day, startHour, startMin, endHour, endMin));
        }

        return set;
    }

    public static Tenor getDateDiffAsTenor(Date date1, Date date2, Integer date1OffsetInDays) {
        long diff = date2.getTime() - date1.getTime();
        int inDays = (int)TimeUnit.MILLISECONDS.toDays(diff) + 1;
        if (date1OffsetInDays != null) {
            inDays = inDays - date1OffsetInDays;
        }
        return createTenor(inDays, "D");
    }

    public static Set<Tenor> getTenorSet(String concatVal) {
        Set<Tenor> set = new LinkedHashSet<Tenor>();
        if (concatVal != null) {
            String[] vals = StringUtils.split(concatVal, ',');
            for (String val: vals) {
                set.add(makeTenor(val.trim()));
            }
        }
        return set;
    }

    public static <T extends Enum<T>> Set<T> getEnumSet(String concatVal, Class<T> clazz) {
        Set<T> set = new LinkedHashSet<T>();
        if (concatVal != null && !concatVal.trim().isEmpty()) {
            String[] vals = StringUtils.split(concatVal, ',');
            for (String val: vals) {
                set.add(makeEnum(val.trim(), clazz));
            }
        }
        return set;
    }

    public static <T extends Enum<T>> T makeEnum(String val, Class<T> clazz) {

        try {
            Method m = clazz.getDeclaredMethod("fromValue", String.class);
            T value = (T)m.invoke(null, val);
            return value;
        } catch (Exception  e) {
           T value = Enum.valueOf(clazz, val);
            return value;
        }
    }



    public static Set<BigDecimal> getDecimalSet(String concatVal) {
        return makeSet(BigDecimal.class, concatVal);
    }

    public static Set<Integer> getIntegerSet(String concatVal) {
        Set<Integer> set = new LinkedHashSet<Integer>();
        if (concatVal != null) {
            String[] vals = StringUtils.split(concatVal, ',');
            for (String val: vals) {
                set.add(Integer.parseInt(val.trim()));
            }
        }
        return set;
    }

    public static Set<String> getStringSet(String concatVal) {
        return makeSet(String.class, concatVal);
    }

    public static Set<Date> getDateSet(String concatVal) {
        return makeSet(Date.class, concatVal);
    }

    public static Map<String, String> getStringMap(String rawVal) {
        Map<String, String> res = new HashMap<String, String>();
        if (rawVal != null) {
            String[] vals = StringUtils.split(rawVal, ';');
            for (String pair: vals) {
                String[] keyval = StringUtils.split(pair.trim(), ',');
                if (keyval.length != 2) {
                    throw new RuntimeException("getStringMap got "
                            + rawVal + ", but it expects the format of key, val; key, val; ...");
                }
                res.put(keyval[0].trim(), keyval[1].trim());
            }
        }
        return res;
    }

    public static Map<String, Boolean> getStringBooleanMap(String rawVal) {
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        if (rawVal != null) {
            String[] vals = StringUtils.split(rawVal, ';');
            for (String pair: vals) {
                String[] keyval = StringUtils.split(pair.trim(), ',');
                if (keyval.length != 2) {
                    throw new RuntimeException("getStringMap got "
                            + rawVal + ", but it expects the format of key, val; key, val; ...");
                }
                res.put(keyval[0].trim(), Boolean.valueOf(keyval[1].trim()));
            }
        }
        return res;
    }

    public static MustContain<String> getStringMustContain(String rawVal) {
        if (rawVal == null) {
            return null;
        }
        return MustContain.createMustContain(String.class, rawVal);
    }

    public static MustContain<BigDecimal> getBigDecimalMustContain(String rawVal) {
        if (rawVal == null) {
            return null;
        }
        return MustContain.createMustContain(BigDecimal.class, rawVal);
    }

    public static Date makeDate(String val) {
        if (val == null) {
            return null;
        }

        List<DateTimeFormatter> formats = new LinkedList<DateTimeFormatter>();
        formats.add(DateTimeFormat.forPattern("MM/dd/yy"));
        formats.add(DateTimeFormat.forPattern("MM/dd/yyyy"));
        formats.add(DateTimeFormat.forPattern("yyyy-MM-dd"));

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

    public static Boolean makeBool(String val) {
        if (val == null) {
            return null;
        }
        String trimmed = val.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if ("True".equalsIgnoreCase(trimmed)) {
            return Boolean.TRUE;
        } else if ("False".equalsIgnoreCase(trimmed)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("Unrecognized boolean literal: " + val);
        }
    }

    public static BigDecimal makeDecimal(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim().replaceAll(",", "");
        if ("".equals(input))
        {
            return null;
        }
        return new BigDecimal(input);
    }

    public static Integer makeInt(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim().replaceAll(",", "");
        if ("".equals(input))
        {
            return null;
        }
        return Integer.parseInt(input);
    }

    public static <T> List<T> makeList(Class<T> clazz, String val) {
        List<T> l = new ArrayList<T>();
        addToCollection(clazz, val, l);
        return l;
    }

    public static <T> Set<T> makeSet(Class<T> clazz, String val) {

        Set<T> answer = fromCache("makeSet", clazz, val);

        if (Comparable.class.isAssignableFrom(clazz)) {
            answer = new TreeSet<T>(new NullComparator<T>(false));
        } else {
            answer = new LinkedHashSet<T>();
        }
        addToCollection(clazz, val, answer);
        return answer;
    }

    private static <T> Set<T> fromCache(String makeSet, Class<T> clazz, String val) {
        return null;
    }

    public static <T> void addToCollection(Class<T> clazz, String strVal, Collection<T> col) {
        if (strVal == null || strVal.trim().isEmpty()) return;

        // Intercept certain types
        if (clazz.equals(TradingWeekHoursSet.class)) {
            col.add(clazz.cast(ClassConverter.getConverter(clazz).convert(strVal)));
            return;
        }

        // Negative lookback for escape forward slash
        String[] strings = strVal.split("(?<!\\\\),");
        for (String item: strings) {
            item = item.trim();
            if (!item.isEmpty()) {
                col.add(clazz.cast(ClassConverter.getConverter(clazz).convert(item.replaceAll("\\\\,",","))));
            }
        }
    }

    public static void main(String[] args) {
        Set<Tenor> tenors = new HashSet<Tenor>();
        tenors.add(makeTenor("6M"));
        DroitException.assertThat(tenors.contains(makeTenor("6M")), "ya");
    }

}
