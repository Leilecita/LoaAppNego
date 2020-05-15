package com.example.android.loa.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.Interfaces.OnAmountHoursChange;
import com.example.android.loa.R;
import com.example.android.loa.adapters.HourEmployeeAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HoursHistoryEmployeeActivity extends BaseActivity implements Paginate.Callbacks, OnAmountHoursChange {

    private RecyclerView mRecyclerView;
    private HourEmployeeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView mUserName;
    private TextView mHoursAcum;
    private TextView mSelectMonth;
    private Long mEmployeeId;

    private String monthSince;
    private String monthTo;


    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    public static void start(Context mContext, Long id,String name){
        Intent i=new Intent(mContext, HoursHistoryEmployeeActivity.class);
        i.putExtra("ID",id);
        i.putExtra("EMPLOYEENAME",name);
        i.putExtra("USERID",id);
        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_history_hours;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mEmployeeId= getIntent().getLongExtra("ID",-1);
        String name=getIntent().getStringExtra("EMPLOYEENAME");

        mRecyclerView =  findViewById(R.id.list_transactionEmployee);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mSelectMonth=findViewById(R.id.select_month);

        mUserName= findViewById(R.id.user_trans);
        mHoursAcum= findViewById(R.id.totalAmount);
        mUserName.setText(name);
        monthSince="";
        monthTo="";

        mAdapter=new HourEmployeeAdapter(this,new ArrayList<Item_employee>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnAmountHoursListener(this);

        refactorToMonth(DateHelper.get().getActualDateEmployee());
        loadAmountHours();

        mSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month();
            }
        });

        implementsPaginate();
    }
    public void onAmountHoursChange(){
        loadAmountHours();
    }

    private void loadAmountHours(){

    ApiClient.get().getAmountHoursByMonth(monthSince, monthTo, mEmployeeId, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                //mHoursAcum.setText(String.valueOf(data.total));

                long v2 = Math.round(data.total);
                mHoursAcum.setText( getHourMinutes(v2));
            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            listByMonth();
        }
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
        listByMonth();
    }
    private void month(){
            final DatePickerDialog datePickerDialog;
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(HoursHistoryEmployeeActivity.this,R.style.datepicker,
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

                            String datePicker=sdayOfMonth+"-"+smonthOfYear +"-"+year+" "+time;
                            System.out.println("ACA FECHA"+datePicker);
                            refactorToMonth(datePicker);
                            clearView();
                            loadAmountHours();

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();

    }

    private String getHourMinutes(long minutes){

        int hoursf= (int)minutes / 60;
        int minutesf= (int) minutes % 60;

        return String.valueOf(hoursf)+"."+String.valueOf(minutesf);
    }

    private void refactorToMonth(String month){
        String monthDate=DateHelper.get().getOnlymonth(DateHelper.get().getOnlyDate(month));
        String nextMonth=DateHelper.get().getNextMonth(month);
        String finalNextMonth=DateHelper.get().getOnlymonth(DateHelper.get().getOnlyDate(nextMonth));

        monthSince=monthDate;
        monthTo=finalNextMonth;
        System.out.println(monthSince);

        mSelectMonth.setText(DateHelper.get().getNameMonth(monthSince));
        System.out.println(monthDate);
        System.out.println(nextMonth);
        System.out.println(finalNextMonth);

    }
    private void implementsPaginate(){
        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView,this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 0;
                    }
                })
                .build();
    }

    public void listByMonth(){

        loadingInProgress=true;
        ApiClient.get().getItemsEmployeeByPageByEmployeeIdByMonth(mCurrentPage, mEmployeeId, monthSince, monthTo, new GenericCallback<List<Item_employee>>() {
            @Override
            public void onSuccess(List<Item_employee> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }
                }
                loadingInProgress = false;
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
    }

    @Override
    public void onLoadMore() {
        listByMonth();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
