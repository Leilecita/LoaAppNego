package com.example.android.loa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CurrentValuesHelper;
import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.adapters.ClientAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientsActivity extends BaseActivity implements Paginate.Callbacks, OnAmountChange {

    private RecyclerView mRecyclerView;
    private ClientAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView mTotalAmoount;
    private Button mOrderBy;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    private LinearLayout button;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_clients;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mRecyclerView = findViewById(R.id.list_users);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ClientAdapter(this, new ArrayList<Client>());
        mAdapter.setOnAmountCangeListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTotalAmoount=findViewById(R.id.totalAmount);
        button= findViewById(R.id.fab_agregarTod);

        setTitle("Ficha deudores");
        // registerForContextMenu(mRecyclerView);

       // setHasOptionsMenu(true);

        mOrderBy=findViewById(R.id.orderClientBy);
        mOrderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderBy();
                clearView();
            }
        });

        final SearchView searchView= findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        searchView.setQueryHint("Buscar");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.trim().toLowerCase().equals(mQuery)) {
                    mCurrentPage = 0;
                    mAdapter.clear();
                    listClients(newText.trim().toLowerCase());
                }
                return false;
            }
        });

        loadOperationAcum(true);
        implementsPaginate();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext(), CreateClientActivity.class));
            }
        });
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
        //  listClients(mQuery);
    }

    private void changeOrderBy(){
        if(CurrentValuesHelper.get().getmOrderClientBy().equals("name")){
            CurrentValuesHelper.get().setmOrderClientBy("debt");
            mOrderBy.setText("A-B");
        }else{
            CurrentValuesHelper.get().setmOrderClientBy("name");
            mOrderBy.setText("> $");
        }
    }

    @Override
    public void loadOperationAcum(boolean refreshOperations) {
        ApiClient.get().getTotalAmount(new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                mTotalAmoount.setText(ValuesHelper.get().getIntegerQuantityByLei(data.total));
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
        }
    }

    public void listClients(final String query){
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;
        ApiClient.get().searchClients(query, mCurrentPage, CurrentValuesHelper.get().getmOrderClientBy(),new GenericCallback<List<Client>>() {
            @Override
            public void onSuccess(List<Client> data) {
                if(token.equals(newToken)){
                    Log.e("TOKEN", "Llega token: " + newToken);
                    System.out.println("IMPRIME"+mCurrentPage+" data size "+data.size());
                    if (query == mQuery) {

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
                }else{
                    Log.e("TOKEN", "Descarta token: " + newToken);
                }
            }

            @Override
            public void onError(Error error) {
                DialogHelper.get().showMessage("Error",error.message+" "+error.result,getBaseContext());

                loadingInProgress = false;
            }
        });
    }
    private void activityAddClient(){
        startActivity(new Intent(this, CreateClientActivity.class));
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
        listClients(mQuery);
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }



    private void startHistoryEventsActivity(){
        startActivity(new Intent(this, EventHistoryActivity.class));
    }

}
