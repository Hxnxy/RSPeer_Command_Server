package org.hxnry.rsp.watcher_server.api.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hxnry
 * @since September 05, 2018
 */
public class Clock {

    public static DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss a");

    public static Date cachedDate;

    public static DateFormat DATE_FORMAT = new SimpleDateFormat("MM/d/Y");

    public static String getTime() {
        cachedDate = new Date();
        return TIME_FORMAT.format(cachedDate);
    }

    public static String getDate() {
        cachedDate = new Date();
        return DATE_FORMAT.format(cachedDate);
    }

    public static Date getCachedDate() {
        return cachedDate;
    }

    public static boolean hasExpired(long time, long amount) {
        return System.currentTimeMillis() - time >= amount;
    }

    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(timestamp);
    }

    public static String formatTime(DateFormat format, long timestamp) {
        return format.format(timestamp);
    }
}
