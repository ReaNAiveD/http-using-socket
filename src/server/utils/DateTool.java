package server.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateTool {
    /**
     * US locale - all HTTP dates are in english
     */
    public final static Locale LOCALE_US = Locale.US;

    /**
     * GMT timezone - all HTTP dates are on GMT
     */
    public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    /**
     * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    public final static String RFC1123_PATTERN =
            "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Format for http response header date field
     */
    public static final String HTTP_RESPONSE_DATE_HEADER =
            "EEE, dd MMM yyyy HH:mm:ss zzz";

    // format for RFC 1036 date string -- "Sunday, 06-Nov-94 08:49:37 GMT"
    private final static String RFC1036_PATTERN =
            "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

    // format for C asctime() date string -- "Sun Nov  6 08:49:37 1994"
    private final static String ASCTIME_PATTERN =
            "EEE MMM d HH:mm:ss yyyyy";

    /**
     * Pattern used for old cookies
     */
    public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    public static DateFormat getRfc1123Format(){
        DateFormat result = new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);
        result.setTimeZone(GMT_ZONE);
        return result;
    }

    public static DateFormat getHttpRespondDateHeaderFormat(){
        DateFormat result = new SimpleDateFormat(HTTP_RESPONSE_DATE_HEADER, LOCALE_US);
        result.setTimeZone(GMT_ZONE);
        return result;
    }

    public static DateFormat getRfc1036Format(){
        DateFormat result = new SimpleDateFormat(RFC1036_PATTERN, LOCALE_US);
        result.setTimeZone(GMT_ZONE);
        return result;
    }
}
