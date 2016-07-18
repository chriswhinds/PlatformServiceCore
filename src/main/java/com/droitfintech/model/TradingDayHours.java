package com.droitfintech.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

/**
 * Describes the hours a SEF is open on a certain day of the week.
 * See productCurrencyExecutionType.TradingDayHours in ListedProducts.xsd
 *
 * @author nathanbrei
 *
 */

@XmlRootElement(namespace="http://www.droitfintech.com")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder={"dayOfWeekAsString", "startTimeAsString", "endTimeAsString"})
public class TradingDayHours implements Comparable<TradingDayHours> {

    private int dayOfWeek;
    private Time startTime;
    private Time endTime;

    // Follows the JodaTime day of week ordering
    private static final String[] DAY_NAMES = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public TradingDayHours() {
        ;
    }

    public TradingDayHours(int dayOfWeek, int startHours, int startMins, int endHours, int endMins){
        this.dayOfWeek = dayOfWeek;
        this.startTime = new Time(startHours, startMins);
        this.endTime = new Time(endHours, endMins);
    }

    public String toString() {
        return "(" + DAY_NAMES[dayOfWeek-1] + "," + startTime + "," + endTime + ")";
    }

    @XmlTransient
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    @XmlElement(name="dayOfWeek")
    public String getDayOfWeekAsString() {
        return DAY_NAMES[dayOfWeek-1];
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @XmlTransient
    public Time getStartTime() {
        return startTime;
    }

    @XmlElement(name="startTime")
    public String getStartTimeAsString() {
        return startTime.toString();
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    @XmlTransient
    public Time getEndTime() {
        return endTime;
    }

    @XmlElement(name="endTime")
    public String getEndTimeAsString() {
        return endTime.toString();
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    /**
     * Tests the input instance to see if it falls within the closest possible range
     * that can be created using this object's day of the week and start-end ranges.
     *
     * @param instance
     * @return -1 if this range is prior to the instance; 0 if the instance falls within the range;
     * 			1 if the range occurs after the instance.
     */
    public int compareToInstance(DateTime instance, DateTimeZone startTimeZone, DateTimeZone endTimeZone) {

        DateTime startTime = new DateTime(instance.getYear(), instance.getMonthOfYear(), instance.getDayOfMonth(),
                this.startTime.getHour(), this.startTime.getMinute(), startTimeZone);
        startTime = getNearestDayOfWeek(startTime, this.dayOfWeek);


        DateTime endTime = new DateTime(instance.getYear(), instance.getMonthOfYear(), instance.getDayOfMonth(),
                this.endTime.getHour(), this.endTime.getMinute(), endTimeZone);
        endTime = getNearestDayOfWeek(endTime, this.dayOfWeek);
        // Some conventions may state the end of the range as x:59,
        // but the JodaTime interval evaluation is exclusive of the end; so we increment
        // the end time by one minute to cover the full intended range for evaluation.
        if (endTime.getMinuteOfHour() == 59) {
            endTime = endTime.plusMinutes(1);
        }

        Interval testInterval = new Interval(startTime, endTime);
        if (testInterval.isBefore(instance)) return -1;
        if (testInterval.contains(instance)) return 0;
        return 1;
    }

    private static DateTime getNearestDayOfWeek(DateTime t0, int dow) {
        DateTime t1 = t0.withDayOfWeek(dow);
        if (t1.isBefore(t0.minusDays(3)))       return t1.plusWeeks(1);
        else if (t1.isAfter(t0.plusDays(3)))    return t1.minusWeeks(1);
        else return t1;
    }

    public static final Map<String, String> CALENDAR_TZ_MAP = new HashMap<String, String>() {{
        put("USNY", "America/New_York");
        put("GBLO", "Europe/London");
        put("JPTO", "Asia/Tokyo");
        put("FRPA","Europe/Paris");
    }};

    /**
     *
     * @param submissionDate
     * @return
     */
    public static Boolean failsStartEndTime(Date submissionDate,
                                            String startTimeBC, String endTimeBC, TreeSet<TradingDayHours> tradingHoursSet) {
        if (submissionDate == null) return false;
        DateTime submissionDateTime = new DateTime(submissionDate);
        int submissionDayOfWeek = submissionDateTime.getDayOfWeek();

        String startTimeTzName = CALENDAR_TZ_MAP.get(startTimeBC);
        String endTimeTzName = CALENDAR_TZ_MAP.get(endTimeBC);

        if (startTimeTzName == null || endTimeTzName == null) {
            throw new RuntimeException("One of the following calendars needs to be mapped to a timezone: {" +
                    startTimeBC + ", " + endTimeBC + "}");
        }

        DateTimeZone startTz = DateTimeZone.forID(startTimeTzName);
        DateTimeZone endTz = DateTimeZone.forID(endTimeTzName);

        TradingDayHours tradingHours = getMatchingTradingHours(submissionDayOfWeek, tradingHoursSet);
        int comparisonResult;
        int priorResult = 0;
        while (tradingHours != null) {
            comparisonResult = tradingHours.compareToInstance(submissionDateTime, startTz, endTz);

            // Do we have a match?
            if (comparisonResult == 0) return false;

            // If the direction of the mismatch changes, we've missed the range altogether. "Fail" the test.
            if (priorResult != 0 && priorResult != comparisonResult) return true;

            // Otherwise, take note of the direction we're going, and keep looking.
            priorResult = comparisonResult;
            tradingHours = getNextHours(tradingHours, tradingHoursSet, comparisonResult);
        }
        return true; // No hours match. We fail the test.
    }

    private static TradingDayHours getNextHours(TradingDayHours hours, TreeSet<TradingDayHours> hoursSet, int comparisonResult) {
        if (comparisonResult < 0) {
            // If the evaluation puts the range before the instance, then we should try to grab a later range.
            TradingDayHours res = hoursSet.higher(hours);
            return res!=null ? res : hoursSet.first();
        } else {
            // Otherwise, we return one before.
            TradingDayHours res = hoursSet.lower(hours);
            return res!=null ? res : hoursSet.last();
        }
    }

    /**
     * Returns the closest matching trading day hours, by day of week.
     * @param dayOfWeek
     * @param tradingHoursSet
     * @return The closest range that matches the day of the week. May return null if there are no hours.
     */
    private static TradingDayHours getMatchingTradingHours(int dayOfWeek, Set<TradingDayHours> tradingHoursSet) {
        int daysOffset = 7;
        TradingDayHours closestMatch = null;
        for (TradingDayHours hours: tradingHoursSet) {
            if (hours.getDayOfWeek() == dayOfWeek) {
                return hours;
            } else {
                int currentOffset = Math.abs(hours.getDayOfWeek()-dayOfWeek);
                if (daysOffset > currentOffset) {
                    closestMatch = hours;
                    daysOffset = currentOffset;
                }
            }
        }
        return closestMatch;
    }


    public int compareTo(TradingDayHours o) {
        return new CompareToBuilder()
                .append(this.dayOfWeek, o.dayOfWeek)
                .append(this.startTime.getHour(), o.startTime.getHour())
                .append(this.startTime.getMinute(), o.startTime.getMinute())
                .append(this.endTime.getHour(), o.endTime.getHour())
                .append(this.endTime.getMinute(), o.endTime.getMinute())
                .toComparison();
    }


}
