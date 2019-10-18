package com.example.android.loa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.Interfaces.OnExtractionsAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ExtractionAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Extraction;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class ExtractionsActivity extends BaseActivity implements Paginate.Callbacks,OnExtractionsAmountChange {

    private RecyclerView mRecyclerView;
    private ExtractionAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String mExtractionDate;
    private String mExtractionNextDate;

    private TextView mDate;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private TextView mTotalAmoount;

    public static void start(Context mContext, String date,String nextDate){
        Intent i=new Intent(mContext, ExtractionsActivity.class);
        i.putExtra("DATE",date);
        i.putExtra("NEXTDATE",nextDate);

        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_extractions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Extracciones");

        mExtractionDate= getIntent().getStringExtra("DATE");
        mExtractionNextDate= getIntent().getStringExtra("NEXTDATE");
        mDate=findViewById(R.id.date);
        mDate.setText(DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(mExtractionDate)));

        mRecyclerView =  findViewById(R.id.list_extractions);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTotalAmoount=findViewById(R.id.totalAmount);
        mAdapter= new ExtractionAdapter(this,new ArrayList<Extraction>(),true);
        mAdapter.setOnExtractionsAmountCangeListener(this);

        mRecyclerView.setAdapter(mAdapter);
        amountExtractions();

        implementsPaginate();
    }

    public void reloadExtractionsAmount(){
        amountExtractions();
        //clearView();
    }

    private void amountExtractions(){

        ApiClient.get().getTotalExtractionAmount(DateHelper.get().getOnlyDateComplete(mExtractionDate),
                DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(mExtractionDate)), new GenericCallback<AmountResult>() {
                    @Override
                    public void onSuccess(AmountResult data) {
                        mTotalAmoount.setText(String.valueOf(data.total));
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
            list();
            amountExtractions();
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
        ApiClient.get().getExtractionsByPageAndDate(mCurrentPage, DateHelper.get().getOnlyDateComplete(mExtractionDate),
                DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(mExtractionDate)),new GenericCallback<List<Extraction>>() {
            @Override
            public void onSuccess(List<Extraction> data) {
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