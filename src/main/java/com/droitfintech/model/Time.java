package com.droitfintech.model;
import java.util.Calendar;


public class Time {
    private int hour;
    private int minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    /**
     * Returns a String parseable by ModelConversionUtil and hence usable for the DB
     */
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }


    /**
     * Apply the time to a provided Calendar object.
     * @param cal the initial Calendar object
     * @return a copy of cal with updated time fields
     */
    public Calendar makeCalendar(Calendar cal) {
        cal = (Calendar) cal.clone();
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.HOUR, hour);
        return cal;
    }
}