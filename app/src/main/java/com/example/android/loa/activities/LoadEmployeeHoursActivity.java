package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoadEmployeeHoursActivity extends BaseActivity {

    private Item_employee mItemEmployee;

    private TextView mDate;
    private TextView day;
    private TextView month;
    private String mEdithDate;
    private TextView name;
    private TextView mEntry;
    private TextView mFinish;
    private TextView mTime_worked;
    private TextView mObs;

    private TextView mEntryT;
    private TextView mFinishT;
    private TextView mTime_workedT;
    private TextView mObsT;

    private TextView mTime_workedTotal;

    private TextView mDateMornIn;
    private TextView mDateMornOut;
    private TextView mDateAftIn;
    private TextView mDateAftOut;

    private TextView mMonthMornIn;
    private TextView mMonthMornOut;
    private TextView mMonthAftIn;
    private TextView mMonthAftOut;

    private String currentDate;
    private String currentDateAft;
    private RelativeLayout currentDateView;

    private LinearLayout cancel;
    private LinearLayout save;

    Calendar c = Calendar.getInstance();
    // Get the system current hour and minute
    Integer hour = c.get(Calendar.HOUR_OF_DAY);
    Integer minute = c.get(Calendar.MINUTE);

    public static void start(Context mContext, Employee employee) {
        Intent i = new Intent(mContext, LoadEmployeeHoursActivity.class);
        i.putExtra("IDEMPLOYEE", employee.id);
        i.putExtra("NAME", employee.getName());
        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_load_employee_hours;
    }



    private String getExpandedDate(){

        String date= DateHelper.get().actualDateExtractions();
        String time= DateHelper.get().getOnlyTime(date);

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            //Date date1 = sdf.parse("19:28:00");
            Date date1 = sdf.parse(time);
            //Date date2 = sdf.parse("21:13:00");
            Date date2 = sdf.parse("04:13:00");

            // Outputs -1 as date1 is before date2
            System.out.println(date1.compareTo(date2));

            if(date1.compareTo(date2) < 0){
                System.out.println(date1.compareTo(date2));

                return DateHelper.get().getPreviousDay(date);
            }else{
                return date;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        name=  findViewById(R.id.name_employee);
        name.setText(getIntent().getStringExtra("NAME"));

        mItemEmployee = new Item_employee(getIntent().getLongExtra("ID", -1),0l,"","","",
                "","",0l,"","","");
       // mItemEmployee.created=DateHelper.get().changeFormatDateUserToServer(DateHelper.get().getActualDate());
        mItemEmployee.created=getExpandedDate();

        mEntry=  findViewById(R.id.entry);
        mFinish=  findViewById(R.id.finish);
        mTime_worked=  findViewById(R.id.time_worked);
        mTime_worked.setText(String.valueOf(0));
        mObs=  findViewById(R.id.observation);

        mEntryT=  findViewById(R.id.entryT);
        mFinishT=  findViewById(R.id.finishT);
        mTime_workedT=  findViewById(R.id.time_workedT);
        mTime_workedT.setText(String.valueOf(0));
        mObsT=  findViewById(R.id.observationT);

        mTime_workedTotal = findViewById(R.id.time_workedTotal);

        mDateMornIn = findViewById(R.id.dayinmorning);
        mDateMornOut = findViewById(R.id.dayoutmorning);
        mDateAftIn = findViewById(R.id.dayaftin);
        mDateAftOut = findViewById(R.id.dayaftout);

        mMonthMornIn = findViewById(R.id.monthinmorning);
        mMonthMornOut = findViewById(R.id.monthoutmorning);
        mMonthAftIn = findViewById(R.id.monthaftin);
        mMonthAftOut = findViewById(R.id.monthaftout);

        mDate= findViewById(R.id.date);
        day= findViewById(R.id.day);
        month= findViewById(R.id.month);

        currentDateView=findViewById(R.id.datecurrent);

        mDate.setText(DateHelper.get().getOnlyDate(DateHelper.get().getActualDate2()));
        day.setText(DateHelper.get().numberDay(DateHelper.get().getActualDate2()));
        month.setText(DateHelper.get().getNameMonth2(DateHelper.get().getActualDate2()));

        currentDate=DateHelper.get().getOnlyDate(DateHelper.get().getActualDate2());
        currentDateAft=DateHelper.get().getOnlyDate(DateHelper.get().getActualDate2());

        loadDates(currentDate+" ");

        mEdithDate="";

        loadItem(DateHelper.get().changeFormatDateUserToServer(DateHelper.get().getActualDate()));

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(LoadEmployeeHoursActivity.this, R.style.datepicker,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String sdayOfMonth = String.valueOf(dayOfMonth);
                                if (sdayOfMonth.length() == 1) {
                                    sdayOfMonth = "0" + dayOfMonth;
                                }

                                String smonthOfYear = String.valueOf(monthOfYear + 1);
                                if (smonthOfYear.length() == 1) {
                                    smonthOfYear = "0" + smonthOfYear;
                                }

                                String time=DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                                String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                                mDate.setText(DateHelper.get().getOnlyDate(datePicker));

                                day.setText(DateHelper.get().numberDay(datePicker));

                                month.setText(DateHelper.get().getNameMonth2(datePicker).substring(0,3));

                                currentDate=year + "-" + smonthOfYear + "-" +  sdayOfMonth;
                                currentDateAft=year + "-" + smonthOfYear + "-" +  sdayOfMonth;

                                loadDates(currentDate+" ");

                                mEdithDate=datePicker;

                                loadItem(datePicker);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        currentDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(LoadEmployeeHoursActivity.this, R.style.datepicker,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String sdayOfMonth = String.valueOf(dayOfMonth);
                                if (sdayOfMonth.length() == 1) {
                                    sdayOfMonth = "0" + dayOfMonth;
                                }

                                String smonthOfYear = String.valueOf(monthOfYear + 1);
                                if (smonthOfYear.length() == 1) {
                                    smonthOfYear = "0" + smonthOfYear;
                                }

                                currentDateAft=year + "-" + smonthOfYear + "-" +  sdayOfMonth;

                                mMonthAftOut.setText(DateHelper.get().getNameMonth2(currentDateAft+" "));
                                mDateAftOut.setText(DateHelper.get().numberDay(currentDateAft+" "));

                                String dateCurrentAft=  mItemEmployee.finish_aft;
                                mItemEmployee.finish_aft = currentDateAft+" "+DateHelper.get().getOnlyTime(dateCurrentAft);

                                mTime_workedT.setText(getHourMinutes(differenceBetweenDate(mItemEmployee.entry_aft,mItemEmployee.finish_aft))+" hs");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(LoadEmployeeHoursActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                mEntry.setText(sHour + ":" + sMinute+" hs");
                                mItemEmployee.entry=currentDate+" "+sHour + ":" + sMinute+":00";

                                mMonthMornIn.setText(DateHelper.get().getNameMonth2(mItemEmployee.entry).substring(0,3));
                                mDateMornIn.setText(DateHelper.get().numberDay(mItemEmployee.entry));

                                mTime_worked.setText(getHourMinutes(differenceBetweenDate(mItemEmployee.entry,mItemEmployee.finish)));
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(LoadEmployeeHoursActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                mFinish.setText(sHour + ":" + sMinute+" hs");

                                mItemEmployee.finish=currentDate+" "+sHour + ":" + sMinute+":00";

                                mItemEmployee.time_worked=differenceBetweenDate(mItemEmployee.entry,mItemEmployee.finish);

                                mTime_worked.setText(getHourMinutes(differenceBetweenDate(mItemEmployee.entry,mItemEmployee.finish))+" hs");

                                mMonthMornOut.setText(DateHelper.get().getNameMonth2(mItemEmployee.finish).substring(0,3));
                                mDateMornOut.setText(DateHelper.get().numberDay(mItemEmployee.finish));

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        mEntryT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(LoadEmployeeHoursActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                mEntryT.setText( sHour + ":" + sMinute+" hs");

                                mItemEmployee.entry_aft=currentDate+" "+sHour + ":" + sMinute+":00";

                                mMonthAftIn.setText(DateHelper.get().getNameMonth2(mItemEmployee.entry_aft).substring(0,3));
                                mDateAftIn.setText(DateHelper.get().numberDay(mItemEmployee.entry_aft));

                                mTime_workedT.setText(getHourMinutes(differenceBetweenDate(mItemEmployee.entry_aft,mItemEmployee.finish_aft))+" hs");

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        mFinishT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                picker = new TimePickerDialog(LoadEmployeeHoursActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {

                                mFinishT.setText(sHour + ":" + sMinute+" hs");

                                mItemEmployee.finish_aft=currentDateAft+" "+sHour + ":" + sMinute+":00";

                                mItemEmployee.time_worked_aft=differenceBetweenDate(mItemEmployee.entry_aft,mItemEmployee.finish_aft);

                                mTime_workedT.setText(getHourMinutes(differenceBetweenDate(mItemEmployee.entry_aft,mItemEmployee.finish_aft))+" hs");

                                mMonthAftOut.setText(DateHelper.get().getNameMonth2(mItemEmployee.finish_aft).substring(0,3));
                                mDateAftOut.setText(DateHelper.get().numberDay(mItemEmployee.finish_aft));

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date=mDate.getText().toString().trim();
                if(!mEdithDate.equals(""))
                    mItemEmployee.created=date;

                mItemEmployee.observation=mObsT.getText().toString().trim();

                ApiClient.get().putItemEmployee(mItemEmployee, new GenericCallback<Item_employee>() {
                    @Override
                    public void onSuccess(Item_employee data) {

                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al crear el usuario",LoadEmployeeHoursActivity.this);
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCancelOrder();
            }
        });

    }

    private void loadDates(String date){
        mDateAftOut.setText(DateHelper.get().numberDay(date));
        mDateMornOut.setText(DateHelper.get().numberDay(date));
        mDateMornIn.setText(DateHelper.get().numberDay(date));
        mDateAftIn.setText(DateHelper.get().numberDay(date));

        mMonthAftOut.setText(DateHelper.get().getNameMonth2(date).substring(0,3));
        mMonthAftIn.setText(DateHelper.get().getNameMonth2(date).substring(0,3));
        mMonthMornOut.setText(DateHelper.get().getNameMonth2(date).substring(0,3));
        mMonthMornIn.setText(DateHelper.get().getNameMonth2(date).substring(0,3));
    }

    private void clearView(String date){
       // loadItem(date);
        //loadDataItemEmployee();
    }
    private void loadItem(String date){

       ApiClient.get().getItemsEmployeeByEmployeeIdByMonth(getIntent().getLongExtra("ID", -1) ,
               DateHelper.get().getOnlyDateComplete(date) ,
               DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(date)),
                       new GenericCallback<List<Item_employee>>() {
           @Override
           public void onSuccess(List<Item_employee> data) {
               if(data.size()>0){
                   mItemEmployee=data.get(0);
                   mItemEmployee.id=data.get(0).id;

                   loadDataItemEmployee();
               }else{
                   cleanDataItemEmploye();

               }
           }
           @Override
           public void onError(Error error) {

           }
       });
    }


    private void loadDataItemEmployee(){


       mEntry.setText(DateHelper.get().getOnlyTime(mItemEmployee.entry));
       mFinish.setText(DateHelper.get().getOnlyTime(mItemEmployee.finish));

       mTime_worked.setText(getHourMinutes(mItemEmployee.time_worked));

       mEntryT.setText(DateHelper.get().getOnlyTime(mItemEmployee.entry_aft));
       mFinishT.setText(DateHelper.get().getOnlyTime(mItemEmployee.finish_aft));

       mTime_workedT.setText(getHourMinutes(mItemEmployee.time_worked_aft));

       mObsT.setText(mItemEmployee.obs_aft);

       loadDates(DateHelper.get().onlyDate(mItemEmployee.entry)+" ");

        if(!mItemEmployee.finish_aft.equals("")){
            currentDateAft=DateHelper.get().onlyDate(mItemEmployee.finish_aft);

            mMonthAftOut.setText(DateHelper.get().getNameMonth2(mItemEmployee.finish_aft).substring(0,3));
            mDateAftOut.setText(DateHelper.get().numberDay(mItemEmployee.finish_aft));
        }
    }
    private void cleanDataItemEmploye(){
        mEntry.setText("");
        mFinish.setText("");
        mTime_worked.setText("");

        mEntryT.setText("");

        mFinishT.setText("");
        mTime_workedT.setText("");
        mObsT.setText("");
    }



    private String getHourMinutes(long minutes){

        int hoursf= (int)minutes / 60;
        int minutesf= (int) minutes % 60;

        return String.valueOf(hoursf)+"."+String.valueOf(minutesf);
    }


    private long differenceBetweenDate(String monthSince,String monthTo){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date dateTo = dateFormat.parse(monthTo);
            Date dateSince = dateFormat.parse(monthSince);

            long diff = dateTo.getTime() - dateSince.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            return minutes;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return 0l;
    }

    private void showDialogCancelOrder(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_cancel_save, null);
        builder.setView(dialogView);

        TextView cancel= dialogView.findViewById(R.id.cancel);
        final TextView ok= dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_jours, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_options:

                HoursHistoryEmployeeActivity.start(this,mItemEmployee.employee_id,name.getText().toString().trim());

                return true;

            case android.R.id.home:

                showDialogCancelOrder();
               // finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String parseHours(String cantHours) {
        String[] parts = cantHours.split(":");
        String part1 = parts[0]; // hora
        String part2 = parts[1]; // minuto
        return part1+"."+part2;
    }


    private void selectDate(final TextView t){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(this,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }
                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }
                        t.setText(year+"-"+smonthOfYear+"-"+sdayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


}
