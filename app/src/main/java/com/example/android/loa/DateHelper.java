package com.example.android.loa;


import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by leila on 15/11/17.
 */

public class DateHelper {

    private static DateHelper INSTANCE = new DateHelper();

    private DateHelper() {

    }

    public static DateHelper get() {
        return INSTANCE;
    }

    public String getActualDate() {
        return actualDate();
    }

    public String getOnlyDate(String date) {
        return onlyDate(date);
    }

    public String getOnlyDateComplete(String date) {
        return onlyDateComplete(date);
    }

    public String getOnlyTime(String date) {
        return onlyTime(date);
    }

    public String getNextDay(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getNextDayBox(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getNextMonth(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.MONTH, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getActualDateEmployee() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }


    private String actualDate() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }

    public String serverToUser(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return format1.format(date1);
        } catch (ParseException e) {

        }
        return "";
    }


    public String serverToUserFormatted(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return changeFormatDate(format1.format(date1));
        } catch (ParseException e) {

        }
        return "";
    }

    public String changeFormatDate(String date) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String changeFormatDateUserToServer(String date) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String userToServer(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getDefault());
            Date date1 = format1.parse(date);

            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format1.format(date1);
        } catch (ParseException e) {

        }
        return "";
    }


    private String onlyDate(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1;
    }

    private String onlyDateComplete(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1 + " 00:00:00";
    }

    public String onlyDayMonth(String date) {
        String[] parts = date.split("/");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part = parts[2]; // año
        return part1 + "/" + part2;
    }

    public String getOnlymonth(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part = parts[2]; // año
        return part + "-" + part2 + "-00 00:00:00";
    }

    public String getOnlyYear(String date) {
        String[] parts = date.split("/");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // año
        return part3;
    }


    private String onlyTime(String date) {
        String[] parts = date.split(" ");
        if (parts.length > 1) {
            return parts[1]; // time
        } else {
            return "";
        }
    }

    public String getNameMonth(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MM").format(cal.getTime());

            String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            return monthNames[Integer.valueOf(monthName)];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
