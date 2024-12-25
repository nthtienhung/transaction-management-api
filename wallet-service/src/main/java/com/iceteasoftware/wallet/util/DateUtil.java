/*
 * DateUtil.java
 *
 * Copyright (C) 2019 by Tech4vn. All right reserved. This software is the confidential and
 * proprietary information of Tech4vn
 *
 */
package com.iceteasoftware.wallet.util;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The Class DateUtil.
 */
public class DateUtil {

    /**
     * The Constant ISO_8601_PATTERN.
     */
    public static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * The Constant LONG_DATE_PATTERN.
     */
    public static final String LONG_DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

    public static final String LONG_DATE_PATTERN_DASH = "dd-MM-yyyy HH:mm:ss";

    /**
     * The Constant LONG_TIME_PATTERN.
     */
    public static final String LONG_TIME_PATTERN = "HH:mm";

    /**
     * The Constant SHORT_DATE_PATTERN.
     */
    public static final String SHORT_DATE_PATTERN = "dd/MM/yyyy";

    public static final String SHORT_DATE_PATTERN_DASH = "dd-MM-yyyy";

    public static final String LONG_TIMESTAMP_PATTERN = "yyyyMMddHHmmss";

    public static final String SHORT_TIMESTAMP_PATTERN = "yyyy-MM-dd";

    /**
     * Compare to.
     *
     * @param date1 the date 1
     * @param date2 the date 2
     * @return the int
     */
    public static int compareTo(Date date1, Date date2) {
        return compareTo(date1, date2, false);
    }

    /**
     * Compare to.
     *
     * @param date1              the date 1
     * @param date2              the date 2
     * @param ignoreMilliseconds the ignore milliseconds
     * @return the int
     */
    public static int compareTo(Date date1, Date date2, boolean ignoreMilliseconds) {

        // Workaround for bug in JDK 1.5.x. This bug is fixed in JDK 1.5.07. See
        // http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=6207898 for
        // more information.
        if (date1 != null && date2 == null) {
            return -1;
        } else if (date1 == null && date2 != null) {
            return 1;
        } else if (date1 == null && date2 == null) {
            return 0;
        }

        long time1 = date1.getTime();
        long time2 = date2.getTime();

        if (ignoreMilliseconds) {
            time1 = time1 / TimeUtil.SECOND;
            time2 = time2 / TimeUtil.SECOND;
        }

        if (time1 == time2) {
            return 0;
        } else if (time1 < time2) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Equals.
     *
     * @param date1 the date 1
     * @param date2 the date 2
     * @return true, if successful
     */
    public static boolean equals(Date date1, Date date2) {
        if (compareTo(date1, date2) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Equals.
     *
     * @param date1              the date 1
     * @param date2              the date 2
     * @param ignoreMilliseconds the ignore milliseconds
     * @return true, if successful
     */
    public static boolean equals(Date date1, Date date2, boolean ignoreMilliseconds) {

        if (!ignoreMilliseconds) {
            return equals(date1, date2);
        }

        long deltaTime = date1.getTime() - date2.getTime();

        if (deltaTime > -1000 && deltaTime < 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Format date date.
     *
     * @param date   the date
     * @param format the format
     * @return the date
     */
    public static Date formatDateDate(Date date, String format) {
        try {
            if (date != null) {

                SimpleDateFormat formater = new SimpleDateFormat(format);

                String strd = formater.format(date);

                return formater.parse(strd);
            }
        } catch (ParseException ex) {
            return null;
        }

        return null;
    }

    /**
     * Format date long date.
     *
     * @param date the date
     * @return the date
     */
    public static Date formatDateLongDate(Date date) {
        return formatDateDate(date, LONG_DATE_PATTERN);
    }

    /**
     * Format date long time.
     *
     * @param date the date
     * @return the date
     */
    public static Date formatDateLongTime(Date date) {
        return formatDateDate(date, LONG_TIME_PATTERN);
    }

    /**
     * Format date short date.
     *
     * @param date the date
     * @return the date
     */
    public static Date formatDateShortDate(Date date) {
        return formatDateDate(date, SHORT_DATE_PATTERN);
    }

    /**
     * Format date string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDateString(Date date) {
        if (date != null) {
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
            return formater.format(date);
        } else {
            return "";
        }

    }

    /**
     * Format string long date.
     *
     * @param date the date
     * @return the string
     */
    public static String formatStringLongDate(Date date) {
        return getDate(date, LONG_DATE_PATTERN);
    }

    /**
     * Format string long time.
     *
     * @param date the date
     * @return the string
     */
    public static String formatStringLongTime(Date date) {
        return getDate(date, LONG_TIME_PATTERN);
    }

    /**
     * Format string long timestamp
     *
     * @param date the date
     * @return the string in format yyyyMMddHHmmss
     */
    public static String formatStringLongTimestamp(Date date) {
        return getDate(date, LONG_TIMESTAMP_PATTERN);
    }

    /**
     * Format string short date.
     *
     * @param date the date
     * @return the string
     */
    public static String formatStringShortDate(Date date) {
        return getDate(date, SHORT_DATE_PATTERN);
    }

    /**
     * @param date
     * @return
     */
    public static String formatStringShortTimestamp(Date date) {
        return getDate(date, SHORT_TIMESTAMP_PATTERN);
    }

    /**
     * Gets the current date.
     *
     * @param pattern the pattern
     * @param locale  the locale
     * @return the current date
     */
    public static String getCurrentDate(String pattern, Locale locale) {
        return getDate(new Date(), pattern, locale);
    }

    /**
     * Gets the current date.
     *
     * @param pattern  the pattern
     * @param locale   the locale
     * @param timeZone the time zone
     * @return the current date
     */
    public static String getCurrentDate(String pattern, Locale locale, TimeZone timeZone) {

        return getDate(new Date(), pattern, locale, timeZone);
    }

    /**
     * Gets the date.
     *
     * @param date    the date
     * @param pattern the pattern
     * @return the date
     */
    public static String getDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(pattern);

        return dateFormat.format(date);
    }

    /**
     * Gets the date.
     *
     * @param date    the date
     * @param pattern the pattern
     * @param locale  the locale
     * @return the date
     */
    public static String getDate(Date date, String pattern, Locale locale) {
        if (date == null) {
            return null;
        }

        Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(pattern, locale);

        return dateFormat.format(date);
    }

    /**
     * Gets the date.
     *
     * @param date     the date
     * @param pattern  the pattern
     * @param locale   the locale
     * @param timeZone the time zone
     * @return the date
     */
    public static String getDate(Date date, String pattern, Locale locale, TimeZone timeZone) {
        if (date == null) {
            return null;
        }

        Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(pattern, locale, timeZone);

        return dateFormat.format(date);
    }

    /**
     * Gets the date after.
     *
     * @param date     the date
     * @param afterDay the after day
     * @return the date after
     */
    public static Date getDateAfter(Date date, int afterDay) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.DAY_OF_YEAR, afterDay);

        return cal.getTime();
    }

    /**
     * Gets the date after.
     *
     * @param afterDay the after day
     * @return the date after
     */
    public static Date getDateAfter(int afterDay) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, afterDay);

        return cal.getTime();
    }

    /**
     * Gets the date after.
     *
     * @param afterMinute
     * @return
     */
    public static Date getDateAfterMinute(Date date, int afterMinute) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MINUTE, afterMinute);

        return cal.getTime();
    }

    /**
     * Gets the date after.
     *
     * @param afterMinute
     * @return
     */
    public static Date getDateAfterMinute(int afterMinute) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, afterMinute);

        return cal.getTime();
    }

    /**
     * Gets the date after.
     *
     * @param afterSecond
     * @return
     */
    public static Date getDateAfterSecond(Date date, int afterSecond) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.SECOND, afterSecond);

        return cal.getTime();
    }

    /**
     * Gets the date after.
     *
     * @param afterSecond
     * @return
     */
    public static Date getDateAfterSecond(int afterSecond) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.SECOND, afterSecond);

        return cal.getTime();
    }

    /**
     * Gets the date before.
     *
     * @param date      the date
     * @param beforeDay the before day
     * @return the date before
     */
    public static Date getDateBefore(Date date, int beforeDay) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.DAY_OF_YEAR, -beforeDay);

        return cal.getTime();
    }

    /**
     * Gets the date before.
     *
     * @param beforeDay the before day
     * @return the date before
     */
    public static Date getDateBefore(int beforeDay) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, -beforeDay);

        return cal.getTime();
    }


    /**
     * Gets the date before.
     *
     * @param beforeMinute
     * @return
     */
    public static Date getDateBeforeMinute(Date date, int beforeMinute) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MINUTE, -beforeMinute);

        return cal.getTime();
    }

    /**
     * Gets the date before.
     *
     * @param beforeMinute
     * @return
     */
    public static Date getDateBeforeMinute(int beforeMinute) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, -beforeMinute);

        return cal.getTime();
    }

    /**
     * Gets the date before.
     *
     * @param beforeSecond
     * @return
     */
    public static Date getDateBeforeSecond(Date date, int beforeSecond) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.SECOND, -beforeSecond);

        return cal.getTime();
    }

    /**
     * Gets the date before.
     *
     * @param beforeSecond
     * @return
     */
    public static Date getDateBeforeSecond(int beforeSecond) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.SECOND, -beforeSecond);

        return cal.getTime();
    }

    /**
     * Gets the days between.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the days between
     */
    public static int getDaysBetween(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();

        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();

        endCal.setTime(endDate);

        int daysBetween = 0;

        while (CalendarUtil.beforeByDay(startCal.getTime(), endCal.getTime())) {
            startCal.add(Calendar.DAY_OF_MONTH, 1);

            daysBetween++;
        }

        return daysBetween;
    }

    /**
     * Gets the days between.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param timeZone  the time zone
     * @return the days between
     */
    public static int getDaysBetween(Date startDate, Date endDate, TimeZone timeZone) {

        int offset = timeZone.getRawOffset();

        Calendar startCal = Calendar.getInstance(timeZone);

        startCal.setTime(startDate);
        startCal.add(Calendar.MILLISECOND, offset);

        Calendar endCal = Calendar.getInstance(timeZone);

        endCal.setTime(endDate);
        endCal.add(Calendar.MILLISECOND, offset);

        int daysBetween = 0;

        while (CalendarUtil.beforeByDay(startCal.getTime(), endCal.getTime())) {
            startCal.add(Calendar.DAY_OF_MONTH, 1);

            daysBetween++;
        }

        return daysBetween;
    }

    /**
     * Gets the ISO 8601 format.
     *
     * @return the ISO 8601 format
     */
    public static DateFormat getISO8601Format() {
        return (DateFormat) FastDateFormatFactoryUtil.getSimpleDateFormat(ISO_8601_PATTERN);
    }

    /**
     * Gets the ISO format.
     *
     * @return the ISO format
     */
    public static DateFormat getISOFormat() {
        return getISOFormat(StringPool.BLANK);
    }

    /**
     * Gets the ISO format.
     *
     * @param text the text
     * @return the ISO format
     */
    public static DateFormat getISOFormat(String text) {
        String pattern = StringPool.BLANK;

        if (text.length() == 8) {
            pattern = "yyyyMMdd";
        } else if (text.length() == 12) {
            pattern = "yyyyMMddHHmm";
        } else if (text.length() == 13) {
            pattern = "yyyyMMdd'T'HHmm";
        } else if (text.length() == 14) {
            pattern = "yyyyMMddHHmmss";
        } else if (text.length() == 15) {
            pattern = "yyyyMMdd'T'HHmmss";
        } else if (text.length() > 8 && text.charAt(8) == 'T') {
            pattern = "yyyyMMdd'T'HHmmssz";
        } else {
            pattern = "yyyyMMddHHmmssz";
        }

        return (DateFormat) FastDateFormatFactoryUtil.getSimpleDateFormat(pattern);
    }

    /**
     * Gets the minutes after.
     *
     * @param date     the date
     * @param afterMin the after min
     * @return the minutes after
     */
    public static Date getMinutesAfter(Date date, int afterMin) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MINUTE, afterMin);

        return cal.getTime();
    }

    /**
     * Gets the minutes after.
     *
     * @param afterMin the after min
     * @return the minutes after
     */
    public static Date getMinutesAfter(int afterMin) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, afterMin);

        return cal.getTime();
    }

    /**
     * Gets the minutes before.
     *
     * @param date      the date
     * @param beforeMin the before min
     * @return the minutes before
     */
    public static Date getMinutesBefore(Date date, int beforeMin) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MINUTE, -beforeMin);

        return cal.getTime();
    }

    /**
     * Gets the minutes before.
     *
     * @param beforeMin the before min
     * @return the minutes before
     */
    public static Date getMinutesBefore(int beforeMin) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, -beforeMin);

        return cal.getTime();
    }

    /**
     * Gets the next birth day.
     *
     * @param birthDay the birth day
     * @return the next birth day
     */
    public static Date getNextBirthDay(Date birthDay) {
        Calendar calBd = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();

        calBd.setTime(birthDay);

        cal.set(Calendar.MONTH, calBd.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, calBd.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Gets the time.
     *
     * @param date the date
     * @return the time
     */
    public static long getTime(Date date) {
        if (date == null) {
            return -1;
        }

        return date.getTime();
    }

    /**
     * Gets the UTC format.
     *
     * @return the UTC format
     */
    public static DateFormat getUTCFormat() {
        return getUTCFormat(StringPool.BLANK);
    }

    /**
     * Gets the UTC format.
     *
     * @param text the text
     * @return the UTC format
     */
    public static DateFormat getUTCFormat(String text) {
        String pattern = StringPool.BLANK;

        if (text.length() == 8) {
            pattern = "yyyyMMdd";
        } else if (text.length() == 12) {
            pattern = "yyyyMMddHHmm";
        } else if (text.length() == 13) {
            pattern = "yyyyMMdd'T'HHmm";
        } else if (text.length() == 14) {
            pattern = "yyyyMMddHHmmss";
        } else if (text.length() == 15) {
            pattern = "yyyyMMdd'T'HHmmss";
        } else {
            pattern = "yyyyMMdd'T'HHmmssz";
        }

        return (DateFormat) FastDateFormatFactoryUtil.getSimpleDateFormat(pattern);
    }

    /**
     * New date.
     *
     * @return the date
     */
    public static Date newDate() {
        return new Date();
    }

    /**
     * New date.
     *
     * @param date the date
     * @return the date
     */
    public static Date newDate(long date) {
        return new Date(date);
    }

    /**
     * New time.
     *
     * @return the long
     */
    public static long newTime() {
        Date date = new Date();

        return date.getTime();
    }

    /**
     * Parse date from string with input format
     *
     * @param date
     * @param format
     * @return
     */
    public static Date parse(String date, String format) {
        try {
            if (Validator.isNotNull(date)) {

                SimpleDateFormat formater = new SimpleDateFormat(format);

                String strd = formater.format(date);

                return formater.parse(strd);
            }
        } catch (ParseException ex) {
            return null;
        }

        return null;
    }

}
