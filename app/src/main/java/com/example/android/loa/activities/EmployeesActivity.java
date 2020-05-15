package com.example.android.loa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.adapters.EmployeeAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class EmployeesActivity extends BaseActivity implements Paginate.Callbacks{


    private RecyclerView mRecyclerView;
    private EmployeeAdapter mAdapter;
    private GridLayoutManager gridlayoutmanager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout button;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_employee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Team");
        mRecyclerView = findViewById(R.id.list_employees);
        gridlayoutmanager=new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(gridlayoutmanager);
        mAdapter = new EmployeeAdapter(this, new ArrayList<Employee>());
        mRecyclerView.setAdapter(mAdapter);

        button= findViewById(R.id.fab_agregarTod);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmployee();
            }
        });

        implementsPaginate();

    }

    private void listEmployees(){
        loadingInProgress=true;

        ApiClient.get().getEmployeesByPage(mCurrentPage, new GenericCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        gridlayoutmanager.scrollToPosition(0);
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



    private void createEmployee(){
        startActivity(new Intent(this, CreateEmployeeActivity.class));
    }

    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView, this)
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

    @Override
    public void onLoadMore() {
        listEmployees();
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
