package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;

import java.util.Calendar;
import java.util.List;


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

        mItemEmployee = new Item_employee(getIntent().getLongExtra("ID", -1),0.0,"","","",
                "","",0.0,"","","");
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
                  // Toast.makeText(LoadEmployeeHoursActivity.this,"Horas iniciadas", Toast.LENGTH_LONG).show();
               }else{
                  // Toast.makeText(LoadEmployeeHoursActivity.this,"Inciando carga", Toast.LENGTH_LONG).show();
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
       mTime_worked.setText(String.valueOf(MathHelper.get().getIntegerQuantity(mItemEmployee.time_worked)));
       mObs.setText(mItemEmployee.observation);

       mEntryT.setText(mItemEmployee.entry_aft);
       mFinishT.setText(mItemEmployee.finish_aft);
       mTime_workedT.setText(String.valueOf(MathHelper.get().getIntegerQuantity(mItemEmployee.time_worked_aft)));
       mObsT.setText(mItemEmployee.obs_aft);

    }

    private void edithHourMorning(final boolean isMorning){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoadEmployeeHoursActivity.this);
        LayoutInflater inflater = (LayoutInflater)getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_hour, null);
        builder.setView(dialogView);

        final TextView obs= dialogView.findViewById(R.id.observation);
        final TextView title= dialogView.findViewById(R.id.title);
        final TextView entrada= dialogView.findViewById(R.id.entry);
        final TextView salida= dialogView.findViewById(R.id.out);
        final TextView time_worked= dialogView.findViewById(R.id.time_worked);
        final TextView date =dialogView.findViewById(R.id.date);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        if(isMorning){
            title.setText("Turno Mañana");
            obs.setText(String.valueOf(mItemEmployee.observation));
            entrada.setText(mItemEmployee.entry);
            salida.setText(mItemEmployee.finish);
            time_worked.setText(String.valueOf(MathHelper.get().getIntegerQuantity(mItemEmployee.time_worked)));
        }else{
            title.setText("Turno Tarde");
            obs.setText(String.valueOf(mItemEmployee.obs_aft));
            entrada.setText(mItemEmployee.entry_aft);
            salida.setText(mItemEmployee.finish_aft);
            time_worked.setText(String.valueOf(MathHelper.get().getIntegerQuantity(mItemEmployee.time_worked_aft)));
        }

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(mItemEmployee.created))));
        date.setHintTextColor(getApplication().getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidatorHelper.get().isTypeDouble(time_worked.getText().toString().trim())){

                    if(isMorning){
                        mItemEmployee.observation=obs.getText().toString().trim();
                        mItemEmployee.entry=entrada.getText().toString().trim();
                        mItemEmployee.finish=salida.getText().toString().trim();
                        mItemEmployee.time_worked=Double.valueOf(time_worked.getText().toString().trim());

                        mEntry.setText(entrada.getText().toString().trim());
                        mFinish.setText(salida.getText().toString().trim());
                        mObs.setText(obs.getText().toString().trim());
                        mTime_worked.setText(String.valueOf(time_worked.getText().toString().trim()));
                    }else{
                        mItemEmployee.obs_aft=obs.getText().toString().trim();
                        mItemEmployee.entry_aft=entrada.getText().toString().trim();
                        mItemEmployee.finish_aft=salida.getText().toString().trim();
                        mItemEmployee.time_worked_aft=Double.valueOf(time_worked.getText().toString().trim());

                        mEntryT.setText(entrada.getText().toString().trim());
                        mFinishT.setText(salida.getText().toString().trim());
                        mObsT.setText(obs.getText().toString().trim());
                        mTime_workedT.setText(String.valueOf(time_worked.getText().toString().trim()));
                    }

                    clearView(mDate.getText().toString().trim());
                    dialog.dismiss();
                }else{
                    Toast.makeText(LoadEmployeeHoursActivity.this,"Tipo no valido",Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                //turno mañana
/*
                String entryTS=mEntry.getText().toString().trim();
                String finishTS=mFinish.getText().toString().trim();
                Double time_workedTS=Double.valueOf(mTime_worked.getText().toString().trim());
                String obsTS=mObs.getText().toString().trim();

                //turno tarde
                String entryTST=mEntryT.getText().toString().trim();
                String finishTST=mFinishT.getText().toString().trim();
                Double time_workedTST= Double.valueOf(mTime_workedT.getText().toString().trim());
                String obsTST=mObsT.getText().toString().trim();
*/
                String date=mDate.getText().toString().trim();

                /*final Item_employee i = new Item_employee(mItemEmployee.id,time_workedTS,"",date,obsTS,entryTS,finishTS,
                    time_workedTST,obsTST,entryTST,finishTST);*/

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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
