package com.fenix.app.util;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public final class DateUtil {

    private static final String ISO_DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final Locale ISO_DATETIME_LOCALE = Locale.UK;

    public static String toISO(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATETIME_FORMAT_PATTERN, ISO_DATETIME_LOCALE);
        return sdf.format(date);
    }

    public static Date fromISO(String isoDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATETIME_FORMAT_PATTERN, ISO_DATETIME_LOCALE);
        Date date;
        try {
            date = sdf.parse(isoDate);
        } catch (ParseException ignore) {
            return null;
        }
        return date;
    }

    public static Date addMinutes(Date date, int minutes) {
        LocalDateTime localTime = LocalDateTime.now();
        localTime = localTime.plusMinutes(minutes);
        return Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
