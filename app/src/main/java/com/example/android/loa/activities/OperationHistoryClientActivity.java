package com.example.android.loa.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.adapters.OperationAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Item_employee;
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
        showBackArrow();
       // TextView title= findViewById(R.id.title);
        //title.setText("Historial");

        mClientId= getIntent().getLongExtra("ID",-1);
        String name=getIntent().getStringExtra("CLIENTNAME");

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


}
