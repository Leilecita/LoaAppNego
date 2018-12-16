package com.example.android.loa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.adapters.HourEmployeeAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class HoursHistoryEmployeeActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private HourEmployeeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView mUserName;
    private TextView mHoursAcum;
    private Long mEmployeeId;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    public static void start(Context mContext, Employee employee){
        Intent i=new Intent(mContext, HoursHistoryEmployeeActivity.class);
        i.putExtra("ID",employee.getId());
        i.putExtra("EMPLOYEENAME",employee.getName());
        i.putExtra("USERID",employee.getId());
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

        mUserName= findViewById(R.id.user_trans);
        mHoursAcum= findViewById(R.id.acum);
        mUserName.setText(name);

        mAdapter=new HourEmployeeAdapter(this,new ArrayList<Item_employee>());
        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();
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

    public void list(){
        loadingInProgress=true;
        ApiClient.get().getItemsEmployeeByPageByEmployeeId(mCurrentPage, mEmployeeId, new GenericCallback<List<Item_employee>>() {
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
        list();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


}
