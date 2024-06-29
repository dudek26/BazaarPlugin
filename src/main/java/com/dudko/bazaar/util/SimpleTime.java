package com.dudko.bazaar.util;

import com.dudko.bazaar.Bazaar;

public class SimpleTime {

    private final long year;
    private final long month;
    private final long day;
    private final long hour;
    private final long minute;
    private final long second;

    public SimpleTime(long second) {
        this.year = second / (60 * 60 * 24 * 365);
        this.month = second / (60 * 60 * 24 * 30);
        this.day = second / (60 * 60 * 24);
        this.hour = second / (60 * 60);
        this.minute = second / 60;
        this.second = second;
    }

    public String parse() {
        if (year >= 1) {
            return timeParsed(TimeUnit.YEAR, year);
        }
        else if (month >= 1) {
            return timeParsed(TimeUnit.MONTH, month);
        }
        else if (day >= 1) {
            return timeParsed(TimeUnit.DAY, day);
        }
        else if (hour >= 1) {
            return timeParsed(TimeUnit.HOUR, hour);
        }
        else if (minute >= 1) {
            return timeParsed(TimeUnit.MINUTE, minute);
        }
        else {
            return timeParsed(TimeUnit.SECOND, second);
        }
    }

    private static String timeParsed(TimeUnit timeUnit, long value) {
        String plural = value > 1 ? "s" : "";
        String key = "time." + timeUnit.toString().toLowerCase() + plural;

        return Bazaar.getPlugin().translatedString(key).replace("<time>", Long.toString(value));
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public long getDay() {
        return day;
    }

    public long getHour() {
        return hour;
    }

    public long getMinute() {
        return minute;
    }

    public long getSecond() {
        return second;
    }

    private enum TimeUnit {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }
}
