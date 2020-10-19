package com.example.android.loa.activities.balances;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.adapters.GeneralStockEventAdapter;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ItemProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.GeneralStock;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;

import com.example.android.loa.types.Constants;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;


public class GeneralBalanceActivity extends BaseActivity implements Paginate.Callbacks, OnSelectedItem, OnSelectedProductItem {

    private RecyclerView mRecyclerView;
    private GeneralStockEventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private String mItem;
    private String mType;

    private LinearLayout bottomSheet;

    private ItemAdapterType mGridAdapter;
    private RecyclerView mGridRecyclerView;
    private RecyclerView.LayoutManager gridlayoutmanager;

    private ItemProductAdapter mGridProductAdapter;
    private RecyclerView mGridRecyclerViewItem;
    private RecyclerView.LayoutManager gridlayoutmanagerItem;

    @Override
    public int getLayoutRes() {
        return R.layout.general_balance_all_activity;
    }

    public void onSelectedItem(String brand, String type, String selection){
        mType=type;
        clearTypes();
        clearView();
    }

    public void onSelectedProductItem(String item){
        System.out.println("item"+item);

        if(mItem.equals(Constants.ITEM_TODOS)){
            mType=Constants.ITEM_TODOS;
        }
        mItem=item;
        clearView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Stock producto general");

        mRecyclerView = findViewById(R.id.list_general_stock_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GeneralStockEventAdapter(this, new ArrayList<GeneralStock>());
        mRecyclerView.setAdapter(mAdapter);

        mItem="Todos";
        mType="Todos";

        bottomSheet = this.findViewById(R.id.bottomSheet);

        topBarListener(bottomSheet);
        listTypes();
        listItems();

        implementsPaginate();
    }



    private void implementsPaginate() {
        loadingInProgress = false;
        mCurrentPage = 0;
        hasMoreItems = true;

        paginate = Paginate.with(mRecyclerView, this)
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

    private void listTypes(){

        ApiClient.get().getTypes(mItem, new GenericCallback<List<SpinnerType>>() {
            @Override
            public void onSuccess(List<SpinnerType> data) {

                mGridAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });


    }

    private void listItems(){

        ApiClient.get().getItems(new GenericCallback<List<SpinnerItem>>() {
            @Override
            public void onSuccess(List<SpinnerItem> data) {
                SpinnerItem s=new SpinnerItem("Todos");
                data.add(0,s);
                mGridProductAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void list() {

        System.out.println("acaaa");
        loadingInProgress = true;

        ApiClient.get().getGeneralStockEvents(mCurrentPage, mItem, mType, new GenericCallback<List<GeneralStock>>() {
            @Override
            public void onSuccess(List<GeneralStock> data) {
                System.out.println("acaaa"+data.size());
                if (data.size() == 0) {
                    hasMoreItems = false;
                } else {
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if (prevSize == 0) {
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

    private void clearTypes() {
        mGridAdapter.clear();
        listTypes();
    }

    private void clearItems() {
        mGridProductAdapter.clear();
        listItems();
    }



    private void clearView() {
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems = true;
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



    private void topBarListener(View bottomSheet) {

        //filters Type
        mGridRecyclerView = bottomSheet.findViewById(R.id.list_types);
        gridlayoutmanager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerView.setLayoutManager(gridlayoutmanager);
        mGridAdapter = new ItemAdapterType(this, new ArrayList<SpinnerType>());
        mGridRecyclerView.setAdapter(mGridAdapter);
        mGridAdapter.setOnSelectedItem(this);

        //filtersItem

        mGridRecyclerViewItem = bottomSheet.findViewById(R.id.list_items);
        gridlayoutmanagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerViewItem.setLayoutManager(gridlayoutmanagerItem);
        mGridProductAdapter = new ItemProductAdapter(this, new ArrayList<SpinnerItem>());
        mGridRecyclerViewItem.setAdapter(mGridProductAdapter);
        mGridProductAdapter.setOnSelectedProductItem(this);
    }
}
