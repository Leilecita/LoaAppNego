package com.example.android.loa.activities.todelete;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.adapters.ReportBoxMonthAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;

import com.example.android.loa.network.models.ReportMonthBox;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class BoxByMonthActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ReportBoxMonthAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyRecyclerView;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_box_by_month;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Cajas mensuales");

        mRecyclerView =  findViewById(R.id.list_box);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter= new ReportBoxMonthAdapter(this,new ArrayList<ReportMonthBox>());

        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout =  findViewById(R.id.swipeRefreshLayout);
        mEmptyRecyclerView=findViewById(R.id.empty);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void
            onRefresh() {

                clearView();
            }
        });

        implementsPaginate();
    }


    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
        list();
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
        if(mAdapter.getItemCount()==0){
            swipeRefreshLayout.setRefreshing(true);
        }

        ApiClient.get().getTotalMonthBoxes(mCurrentPage, new GenericCallback<List<ReportMonthBox>>() {
            @Override
            public void onSuccess(List<ReportMonthBox> data) {
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
                if(mCurrentPage == 0 && data.size()==0){
                    mEmptyRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyRecyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
                swipeRefreshLayout.setRefreshing(false);
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
