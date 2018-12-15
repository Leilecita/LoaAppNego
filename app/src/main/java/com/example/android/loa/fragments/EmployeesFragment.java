package com.example.android.loa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.activities.CreateClientActivity;
import com.example.android.loa.activities.CreateEmployeeActivity;
import com.example.android.loa.adapters.ClientAdapter;
import com.example.android.loa.adapters.EmployeeAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmployeesFragment  extends BaseFragment implements Paginate.Callbacks{

    private RecyclerView mRecyclerView;
    private EmployeeAdapter mAdapter;
    //private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridlayoutmanager;
    private View mRootView;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    public void onClickButton(){ createEmployee(); }
    public int getIconButton(){
        return R.drawable.addperson;
    }

    public int getVisibility(){
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_employee, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_employees);
        gridlayoutmanager=new GridLayoutManager(getActivity(),3);
        mRecyclerView.setLayoutManager(gridlayoutmanager);
        mAdapter = new EmployeeAdapter(getActivity(), new ArrayList<Employee>());

        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();
        return mRootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            listEmployees();
        }
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
        startActivity(new Intent(getContext(), CreateEmployeeActivity.class));
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
