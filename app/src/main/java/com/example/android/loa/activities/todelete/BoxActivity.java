package com.example.android.loa.activities.todelete;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.adapters.BoxAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class BoxActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private BoxAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String mBoxDate;
    private String mBoxNextDate;

    private TextView mDate;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    public static void start(Context mContext, String date, String nextDate){
        Intent i=new Intent(mContext, BoxActivity.class);
        i.putExtra("DATE",date);
        i.putExtra("NEXTDATE",nextDate);

        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_box;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mBoxDate= getIntent().getStringExtra("DATE");
        mBoxNextDate= getIntent().getStringExtra("NEXTDATE");


        mRecyclerView =  findViewById(R.id.list_box);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter= new BoxAdapter(this,new ArrayList<Box>());

        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            list();
        }
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

        String d=DateHelper.get().getOnlyDateComplete(mBoxDate);
        String d2= DateHelper.get().getOnlyDateComplete(mBoxNextDate);

        ApiClient.get().getBoxesByPageAndDate(mCurrentPage, DateHelper.get().getOnlyDateComplete(mBoxDate),
                DateHelper.get().getOnlyDateComplete((mBoxNextDate)), new GenericCallback<List<Box>>() {
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
