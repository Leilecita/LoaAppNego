package com.example.android.loa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.activities.balances.GeneralBalanceActivity;
import com.example.android.loa.adapters.OperationAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Operation;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class OperationHistoryClientActivity extends BaseActivity implements Paginate.Callbacks,OnAmountChange {

    private RecyclerView mRecyclerView;
    private OperationAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView mUserName;
    private TextView mOperationAcum;
    private Long mClientId;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout home;
    private TextView title;
    private LinearLayout options;

    public static void start(Context mContext, Client client){
        Intent i=new Intent(mContext, OperationHistoryClientActivity.class);
        i.putExtra("ID",client.getId());
        i.putExtra("CLIENTNAME",client.getName());
        i.putExtra("USERID",client.getId());
        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_history_item_file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // showBackArrow();

        home = findViewById(R.id.line_home);
        title = findViewById(R.id.title);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        options = findViewById(R.id.options);
        loadOptions();

        mClientId= getIntent().getLongExtra("ID",-1);
        String name=getIntent().getStringExtra("CLIENTNAME");

        title.setText("Historial "+name);

        mRecyclerView =  findViewById(R.id.list_transaction);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mUserName= findViewById(R.id.user_trans);
        mOperationAcum= findViewById(R.id.acum);
        mUserName.setText(name);

        mAdapter=new OperationAdapter(this,new ArrayList<Operation>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnAmountCangeListener(this);

        implementsPaginate();
        loadOperationAcum(false);
    }

    public void reloadOperations(){
        mCurrentPage=0;
        mAdapter.clear();
        loadingInProgress=false;
        hasMoreItems = true;
        listOperations();
    }


    public void loadOperationAcum(boolean refreshOperations){
        if(refreshOperations){
            reloadOperations();
        }
       /* ApiClient.get().getOperationAcum(mClientId, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                if(data.total<0){
                    mOperationAcum.setText(String.valueOf(data.total));
                }else{
                    mOperationAcum.setText("+"+String.valueOf(data.total));
                }

            }

            @Override
            public void onError(Error error) {
            }
        });
*/
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

    public void listOperations(){
        loadingInProgress=true;
        ApiClient.get().getItemsByClientIdByPage(mCurrentPage, mClientId, new GenericCallback<List<Operation>>() {
            @Override
            public void onSuccess(List<Operation> data) {
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
        listOperations();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


    private void loadOptions(){
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(OperationHistoryClientActivity.this, options);
                popup.getMenuInflater().inflate(R.menu.menu_add_product, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_info:
                                startHistoryEvents();
                                return true;

                            case android.R.id.home:
                                finish();
                                // NavUtils.navigateUpFromSameTask(this);
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void startHistoryEvents(){
        EventHistoryActivity.startHistoryEvents(this, getIntent().getLongExtra("ID",-1),getIntent().getStringExtra("CLIENTNAME"));
    }
}
/*
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
               startHistoryEvents();
                return true;

            case android.R.id.home:
                finish();
               // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

 */