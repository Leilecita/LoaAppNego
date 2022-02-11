package com.example.android.loa.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.Events.RefreshListHours;
import com.example.android.loa.Interfaces.OnAmountHoursChange;
import com.example.android.loa.R;
import com.example.android.loa.adapters.HourEmployeeAdapter;
import com.example.android.loa.adapters.ReportHourEmployeeAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;
import com.example.android.loa.network.models.ReportItemEmployee;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HoursHistoryEmployeeActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ReportHourEmployeeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView mUserName;
    private Long mEmployeeId;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout home;
    private TextView title;

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
       // showBackArrow();

        mEmployeeId= getIntent().getLongExtra("ID",-1);
        String name=getIntent().getStringExtra("EMPLOYEENAME");

        home = findViewById(R.id.line_home);
        title = findViewById(R.id.title);

        title.setText(name);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView =  findViewById(R.id.list_transactionEmployee);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ReportHourEmployeeAdapter(this,new ArrayList<ReportItemEmployee>());
        mRecyclerView.setAdapter(mAdapter);

        mUserName= findViewById(R.id.user_trans);
        mUserName.setText(name);


        if(SessionPrefs.get(this).getName().equals("santi") || SessionPrefs.get(this).getName().equals("lei")){

            // Add the sticky headers decoration
            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            mRecyclerView.addItemDecoration(headersDecor);

            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override public void onChanged() {
                    headersDecor.invalidateHeaders();
                }
            });
        }

        EventBus.getDefault().register(this);

        implementsPaginate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                clearView();

                return true;

            case android.R.id.home:

                 finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        ApiClient.get().getHoursByMonth(mCurrentPage, mEmployeeId, new GenericCallback<List<ReportItemEmployee>>() {
            @Override
            public void onSuccess(List<ReportItemEmployee> data) {

                System.out.println("entra aca");
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
                System.out.println("sale por error");
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

    @Subscribe
    public void onEvent(RefreshListHours event){
        Toast.makeText(this,"Se actualizo lista",Toast.LENGTH_SHORT).show();
        clearView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
/*


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

        mSelectMonth.setText(DateHelper.get().getNameMonth2(monthSince));
        System.out.println(monthDate);
        System.out.println(nextMonth);
        System.out.println(finalNextMonth);

    }
 */