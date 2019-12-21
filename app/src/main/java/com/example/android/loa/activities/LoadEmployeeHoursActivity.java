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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        name=  findViewById(R.id.name_employee);
        name.setText(getIntent().getStringExtra("NAME"));

        mItemEmployee = new Item_employee(getIntent().getLongExtra("ID", -1),0l,"","","",
                "","",0l,"","","");
        mItemEmployee.created=DateHelper.get().changeFormatDateUserToServer(DateHelper.get().getActualDate());

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

        mDate=findViewById(R.id.date);
        mDate.setText(DateHelper.get().getOnlyDate(DateHelper.get().getActualDate()));

        mEdithDate="";

        loadItem(DateHelper.get().changeFormatDateUserToServer(DateHelper.get().getActualDate()));

        ImageView date_picker= findViewById(R.id.date_picker);
        date_picker.setOnClickListener(new View.OnClickListener() {
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
                                mDate.setText(datePicker);
                                mEdithDate=datePicker;
                                loadItem(datePicker);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        final ImageView edith_morning=findViewById(R.id.edit_morning);
        edith_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithHourMorning(true);
            }
        });
        ImageView edith_afternoon=findViewById(R.id.edit_afternoon);
        edith_afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithHourMorning(false);
            }
        });
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
               }
           }
           @Override
           public void onError(Error error) {

           }
       });
    }


    private void loadDataItemEmployee(){

       mEntry.setText(mItemEmployee.entry);
       mFinish.setText(mItemEmployee.finish);
       mTime_worked.setText(getHourMinutes(mItemEmployee.time_worked));
       mObs.setText(mItemEmployee.observation);

       mEntryT.setText(mItemEmployee.entry_aft);
       mFinishT.setText(mItemEmployee.finish_aft);
       mTime_workedT.setText(getHourMinutes(mItemEmployee.time_worked_aft));
       mObsT.setText(mItemEmployee.obs_aft);

    }

    private void edithHourMorning(final boolean isMorning){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoadEmployeeHoursActivity.this);
        LayoutInflater inflater = (LayoutInflater)getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_hour, null);
        builder.setView(dialogView);

        final TextView obs= dialogView.findViewById(R.id.observation);
        final TextView time_worked= dialogView.findViewById(R.id.time_worked);
        final TextView title= dialogView.findViewById(R.id.title);
        final TextView entrada= dialogView.findViewById(R.id.entry);
        final TextView salida= dialogView.findViewById(R.id.out);
        final TextView date =dialogView.findViewById(R.id.date);
        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        if(isMorning){
            title.setText("Turno Mañana");
            obs.setText(String.valueOf(mItemEmployee.observation));
            entrada.setText(mItemEmployee.entry);
            salida.setText(mItemEmployee.finish);
            time_worked.setText(String.valueOf(mItemEmployee.time_worked));
        }else{
            title.setText("Turno Tarde");
            obs.setText(String.valueOf(mItemEmployee.obs_aft));
            entrada.setText(mItemEmployee.entry_aft);
            salida.setText(mItemEmployee.finish_aft);
            time_worked.setText(String.valueOf(mItemEmployee.time_worked_aft));
        }

        final String dateInfo=DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(mItemEmployee.created)));
        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(mItemEmployee.created))));

        date.setHintTextColor(getApplication().getResources().getColor(R.color.colorDialogButton));

        entrada.setOnClickListener(new View.OnClickListener() {
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
                                entrada.setText(dateInfo+" "+sHour + ":" + sMinute+":00");
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
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
                                salida.setText(dateInfo+" "+sHour + ":" + sMinute+":00");
                            }
                        }, hour, minutes, true);
                picker.show();

            }
        });


        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if( ValidatorHelper.get().isTypeDouble(time_worked.getText().toString().trim()) ){

                    if(isMorning){

                        mItemEmployee.observation=obs.getText().toString().trim();
                        mItemEmployee.entry=entrada.getText().toString().trim();
                        mItemEmployee.finish=salida.getText().toString().trim();

                        String timeInit=entrada.getText().toString().trim();
                        String timeFinish=salida.getText().toString().trim();

                        String hoursMinutesDifference=String.valueOf(differenceBetweenDate(timeInit,timeFinish));

                        mItemEmployee.time_worked=differenceBetweenDate(timeInit,timeFinish);

                        System.out.println("diferencia en minutos "+String.valueOf(mItemEmployee.time_worked));
                        System.out.println("diferencia hours"+gethours(differenceBetweenDate(timeInit,timeFinish)));
                        System.out.println("diferencia minut "+getMinutes(differenceBetweenDate(timeInit,timeFinish)));
                        System.out.println("diferencia hor minut "+getHourMinutes(differenceBetweenDate(timeInit,timeFinish)));


                        mEntry.setText(timeInit);
                        mFinish.setText(timeFinish);
                        mObs.setText(obs.getText().toString().trim());
                        mTime_worked.setText(getHourMinutes(differenceBetweenDate(timeInit,timeFinish)));

                    }else{
                        mItemEmployee.obs_aft=obs.getText().toString().trim();
                        mItemEmployee.entry_aft=entrada.getText().toString().trim();
                        mItemEmployee.finish_aft=salida.getText().toString().trim();

                        String timeInit=entrada.getText().toString().trim();
                        String timeFinish=salida.getText().toString().trim();

                        String hoursMinutesDifference=String.valueOf(differenceBetweenDate(timeInit,timeFinish));

                        mItemEmployee.time_worked_aft=differenceBetweenDate(timeInit,timeFinish);

                        mEntryT.setText(timeInit);
                        mFinishT.setText(timeFinish);
                        mObsT.setText(obs.getText().toString().trim());
                        mTime_workedT.setText(getHourMinutes(differenceBetweenDate(timeInit,timeFinish)));
                    }

                    clearView(mDate.getText().toString().trim());
                    dialog.dismiss();
                }else{
                    Toast.makeText(getBaseContext(),"Tipo de dato no valido",Toast.LENGTH_SHORT).show();
                }
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

    private String getHourMinutes(long minutes){

        int hoursf= (int)minutes / 60;
        int minutesf= (int) minutes % 60;

        return String.valueOf(hoursf)+"."+String.valueOf(minutesf);
    }

    private String gethours(long minutes){

        return String.valueOf((int)minutes / 60);
    }

    private String getMinutes(long minutes){
        return String.valueOf((int) minutes % 60);
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
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String date=mDate.getText().toString().trim();

                if(!mEdithDate.equals(""))
                    mItemEmployee.created=date;

                final ProgressDialog progress = ProgressDialog.show(this, "Cargando horas",
                        "Aguarde un momento", true);

                ApiClient.get().putItemEmployee(mItemEmployee, new GenericCallback<Item_employee>() {
                    @Override
                    public void onSuccess(Item_employee data) {
                        finish();
                        progress.dismiss();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al crear el usuario",LoadEmployeeHoursActivity.this);
                    }
                });


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
}
/*
 private String differenceBetweenHours(String time1, String time2){
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
            Date date1 = format.parse(time1);
            Date date2 = format.parse(time2);
            long mills = date2.getTime() - date1.getTime();
            Log.v("Data1", ""+date1.getTime());
            Log.v("Data2", ""+date2.getTime());

            System.out.println(date1.getTime());
            System.out.println(date2.getTime());

            int hours = (int) (mills/(1000 * 60 * 60));
            int mins = (int) (mills/(1000*60)) % 60;
            hours = (hours < 0 ? -hours : hours);

            String diff = hours + "." + mins; // updated value every1 second
            System.out.println(diff);
            return diff;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private void obtenerHora(final TextView t){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);

                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf("0" + minute):String.valueOf(minute);

                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "am";
                } else {
                    AM_PM = "pm";
                }


                t.setText(horaFormateada+"."+minutoFormateado);
            }
        }, hour, minute, false);
        recogerHora.show();
    }
 */