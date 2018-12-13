package com.example.android.loa;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by leila on 15/11/17.
 */

public class DateHelper {

    private static DateHelper INSTANCE = new DateHelper();

    private DateHelper(){

    }

    public static DateHelper get(){
        return INSTANCE;
    }

    public String getActualDate(){return actualDate();}
    public String getOnlyDate(String date){return onlyDate(date);}
    public String getOnlyTime(String date){return  onlyTime(date);}


    private String actualDate(){
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }

    public String serverToUser(String date){

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return format1.format(date1);
        }catch (ParseException e){

        }
        return "";
    }


    public String serverToUserFormatted(String date){

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return changeFormatDate(format1.format(date1));
        }catch (ParseException e){

        }
        return "";
    }

    public String changeFormatDate(String date){
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

    public String changeFormatDateUserToServer(String date){
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

    public String userToServer(String date){

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getDefault());
            Date date1 = format1.parse(date);

            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format1.format(date1);
        }catch (ParseException e){

        }
        return "";
    }


    private String onlyDate(String date){
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1;
    }

    private String onlyTime(String date){
        String[] parts = date.split(" ");
        if(parts.length > 1){
            return parts[1]; // time
        }else{
            return "";
        }

    }

}
