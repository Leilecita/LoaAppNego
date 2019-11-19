package com.example.android.loa.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;


import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.adapters.EventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Event;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by leila on 23/11/17.
 */

public class EventHistoryActivity extends BaseActivity implements Paginate.Callbacks{

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private Long mClientId;


    public static void startHistoryEvents(Context mContext, Long client_id,String name){
        Intent i=new Intent(mContext, EventHistoryActivity.class);
        i.putExtra("ID",client_id);
        i.putExtra("CLIENTNAME",name);
        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.event_history_activity_log;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Eventos");
        //TextView title= findViewById(R.id.title);
        //title.setText("Historial");
        //  ImageView icon= findViewById(R.id.icon);
        // icon.setVisibility(View.INVISIBLE);

        mClientId= getIntent().getLongExtra("ID",-1);

        mRecyclerView =  findViewById(R.id.list_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new EventAdapter(this, new ArrayList<Event>());

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

    private void listEventsByClientId(){
        loadingInProgress=true;
        ApiClient.get().getEventsByPageByClientId(mCurrentPage, mClientId, new GenericCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> data) {
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

    private void listEvents(){
        loadingInProgress=true;
        ApiClient.get().getEventsByPage(mCurrentPage, new GenericCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> data) {
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
        if(mClientId != -1){

            listEventsByClientId();
        }else{
            listEvents();
        }


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
