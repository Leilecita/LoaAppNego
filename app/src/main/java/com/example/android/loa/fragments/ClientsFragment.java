package com.example.android.loa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.loa.CurrentValuesHelper;
import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.activities.CreateClientActivity;
import com.example.android.loa.activities.EventHistoryActivity;
import com.example.android.loa.adapters.ClientAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientsFragment extends BaseFragment implements Paginate.Callbacks, OnAmountChange{

    private RecyclerView mRecyclerView;
    private ClientAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;
    private TextView mTotalAmoount;
    private Button mOrderBy;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    public void onClickButton(){ activityAddClient(); }
    public int getIconButton(){
        return R.drawable.add_file;
    }

    public int getVisibility(){
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_clients, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_users);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ClientAdapter(getActivity(), new ArrayList<Client>());
        mAdapter.setOnAmountCangeListener(this);

        mTotalAmoount=mRootView.findViewById(R.id.totalAmount);

       // registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        mOrderBy=mRootView.findViewById(R.id.orderClientBy);
        mOrderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderBy();
                clearView();

            }
        });

        final SearchView searchView= mRootView.findViewById(R.id.searchView);
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

        return mRootView;
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
        listClients(mQuery);
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
            listClients("");
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
                loadingInProgress = false;
            }
        });
    }
    private void activityAddClient(){
        startActivity(new Intent(getContext(), CreateClientActivity.class));
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


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_info, menu);

        final MenuItem item = menu.findItem(R.id.action_info);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startHistoryEventsActivity();

                return false;
            }
        });
    }

    private void startHistoryEventsActivity(){

        startActivity(new Intent(getContext(), EventHistoryActivity.class));

    }

}
