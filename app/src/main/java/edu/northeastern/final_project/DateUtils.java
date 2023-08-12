package edu.northeastern.final_project;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    public static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(calendar.getTime());
    }
}

