package com.example.android.loa.activities;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.adapters.GeneralStockEventAdapter;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ItemProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.GeneralStock;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;

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
        clearView();
    }

    public void onSelectedProductItem(String item){
        mItem=item;
        clearView();
        clearTypes();
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
        loadingInProgress = true;

        ApiClient.get().getGeneralStockEvents(mCurrentPage, mItem, mType, new GenericCallback<List<GeneralStock>>() {
            @Override
            public void onSuccess(List<GeneralStock> data) {
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



    private void topBarListener(View bottomSheet){

        //filters Type
        mGridRecyclerView =  bottomSheet.findViewById(R.id.list_types);
        gridlayoutmanager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerView.setLayoutManager(gridlayoutmanager);
        mGridAdapter = new ItemAdapterType(this, new ArrayList<SpinnerType>());
        mGridRecyclerView.setAdapter(mGridAdapter);
        mGridAdapter.setOnSelectedItem(this);

        //filtersItem

        mGridRecyclerViewItem =  bottomSheet.findViewById(R.id.list_items);
        gridlayoutmanagerItem=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerViewItem.setLayoutManager(gridlayoutmanagerItem);
        mGridProductAdapter = new ItemProductAdapter(this, new ArrayList<SpinnerItem>());
        mGridRecyclerViewItem.setAdapter(mGridProductAdapter);
        mGridProductAdapter.setOnSelectedProductItem(this);


/*
        man=bottomSheet.findViewById(R.id.man);
        woman=bottomSheet.findViewById(R.id.woman);
        boy=bottomSheet.findViewById(R.id.boy);
        tecnico=bottomSheet.findViewById(R.id.tecnico);
        zapas=bottomSheet.findViewById(R.id.zapas);
        accesories=bottomSheet.findViewById(R.id.acces);
        luz=bottomSheet.findViewById(R.id.luz);
        oferta=bottomSheet.findViewById(R.id.oferta);
        all=bottomSheet.findViewById(R.id.all);


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Todos";
                changeCircleSelected();
                all.setImageResource(R.drawable.ball);
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                changeCircleSelected();
                woman.setImageResource(R.drawable.bwom);
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                changeCircleSelected();
                man.setImageResource(R.drawable.bman);
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                changeCircleSelected();
                boy.setImageResource(R.drawable.bnin);
            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorio";
                changeCircleSelected();
                accesories.setImageResource(R.drawable.bacc);
            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                changeCircleSelected();
                tecnico.setImageResource(R.drawable.btec);
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Calzado";
                changeCircleSelected();
                zapas.setImageResource(R.drawable.bcal);
            }
        });

        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Luz";
                changeCircleSelected();
                luz.setImageResource(R.drawable.bluz);
            }
        });

        oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Oferta";
                changeCircleSelected();
                oferta.setImageResource(R.drawable.bofer);
            }
        });

        */
    }

    /*
     private void changeCircleSelected(){

        woman.setImageResource(R.drawable.bwomcl);
        boy.setImageResource(R.drawable.bnincl);
        man.setImageResource(R.drawable.bmancl);
        tecnico.setImageResource(R.drawable.btecl);
        zapas.setImageResource(R.drawable.bcalcl);
        accesories.setImageResource(R.drawable.bacccl);
        luz.setImageResource(R.drawable.bluzcl);
        oferta.setImageResource(R.drawable.bofercl);
        all.setImageResource(R.drawable.ballcl);

        mType="Todos";
        clearView();
        clearTypes();


    }
     */


}
