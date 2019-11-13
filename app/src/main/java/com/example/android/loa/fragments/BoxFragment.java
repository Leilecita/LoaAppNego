package com.example.android.loa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.R;
import com.example.android.loa.activities.BoxActivity;
import com.example.android.loa.activities.CreateBoxActivity;
import com.example.android.loa.adapters.BoxAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.models.Box;
import com.paginate.Paginate;
import com.example.android.loa.network.GenericCallback;
import com.paginate.recycler.LoadingListItemSpanLookup;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BoxFragment extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private BoxAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;

    private String mSelectDate;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int CREATE_BOX_REQUEST_CODE=2020;

    public void onClickButton(){

        Intent intent = new Intent(getContext(), CreateBoxActivity.class);

        if(mAdapter.getList().size() >0){
            intent.putExtra("RESTBOX", String.valueOf(mAdapter.getList().get(0).rest_box));
        }else{
            intent.putExtra("RESTBOX", String.valueOf(0.0));
        }

        startActivityForResult(intent,CREATE_BOX_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_BOX_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){
                clearView();
            }

        }
    }
    public int getIconButton(){
        return R.drawable.add_white;
    }

    public int getVisibility(){
        return 0;
    }

    @Subscribe
    public void onEvent(RefreshBoxesEvent event){

        Toast.makeText(getActivity(),event.mMessage,Toast.LENGTH_SHORT).show();
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

    private void clearView(){
        if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_box, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_box);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BoxAdapter(getActivity(), new ArrayList<Box>());

        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        mSelectDate=DateHelper.get().getActualDate();

        swipeRefreshLayout =  mRootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void
            onRefresh() {
                clearView();
            }
        });

        implementsPaginate();
        EventBus.getDefault().register(this);

        return mRootView;
    }

    private void listBoxes(){

        loadingInProgress=true;
        if(mAdapter.getItemCount()==0){
            swipeRefreshLayout.setRefreshing(true);
        }

        ApiClient.get().getBoxesByPage2(mCurrentPage,new GenericCallback<List<Box>>() {
            @Override
            public void onSuccess(List<Box> data) {
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
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Error error) {
                System.out.println("entra aca2");
                swipeRefreshLayout.setRefreshing(false);
                loadingInProgress = false;
            }
        });

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
            listBoxes();
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectDate();
                return false;
            }
        });
    }

    private void selectDate(){
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
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
                                mSelectDate=datePicker;

                                Intent intent= new Intent(getContext(), BoxActivity.class);
                                intent.putExtra("DATE",mSelectDate);
                                intent.putExtra("NEXTDATE",DateHelper.get().getNextDay(mSelectDate));
                                startActivity(intent);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
}
