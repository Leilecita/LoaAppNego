package com.example.android.loa.activities.balances;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.activities.DeletedProductsActivity;
import com.example.android.loa.activities.ProductsActivity;
import com.example.android.loa.adapters.StockEventAdapterBalance;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.StockEvent;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class BalanceActivity extends BaseActivity implements Paginate.Callbacks{

    private RecyclerView mRecyclerViewStockEvents;
    private StockEventAdapterBalance mAdapterStockEvents;
    private RecyclerView.LayoutManager layoutManager;


    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout home;
    private LinearLayout options;

    public static void start(Context mContext, Product product){
        Intent i=new Intent(mContext, BalanceActivity.class);
        i.putExtra("ID",product.id);
        i.putExtra("TYPE",product.type);
        i.putExtra("BRAND",product.brand);
        mContext.startActivity(i);
    }
    private void clearView(){
        mCurrentPage = 0;
        mAdapterStockEvents.clear();
        hasMoreItems=true;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.balance_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // showBackArrow();


        options = findViewById(R.id.options);
        loadOptions();

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewStockEvents =  findViewById(R.id.list_stock_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewStockEvents.setLayoutManager(layoutManager);
        mAdapterStockEvents=new StockEventAdapterBalance(this,new ArrayList<StockEvent>());

        mRecyclerViewStockEvents.setAdapter(mAdapterStockEvents);

        Button generate_balance= findViewById(R.id.balance_item);
        generate_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance();
            }
        });

        TextView type= findViewById(R.id.type_product);
        type.setText(getIntent().getStringExtra("TYPE"));

        TextView brand=findViewById(R.id.brand_product);
        brand.setText(getIntent().getStringExtra("BRAND"));
        implementsPaginate();
    }
    private void balance(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BalanceActivity.this);
        LayoutInflater inflater = (LayoutInflater)BalanceActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.update_balance, null);
        builder.setView(dialogView);

        final TextView type= dialogView.findViewById(R.id.type);
        type.setText(getIntent().getStringExtra("TYPE"));
        final TextView brand= dialogView.findViewById(R.id.brand);
        brand.setText(getIntent().getStringExtra("BRAND"));
        final TextView stock_real= dialogView.findViewById(R.id.real_stock_balance);
        final TextView detail= dialogView.findViewById(R.id.detail);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockP=stock_real.getText().toString().trim();
                String detailP=detail.getText().toString().trim();

                if(!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)){

                    StockEvent s= new StockEvent(getIntent().getLongExtra("ID",-1),0,0,0,detailP,0.0,"","");
                    s.ideal_stock=0;
                    s.balance_stock=Integer.valueOf(stockP);

                    ApiClient.get().postStockEvent(s, "balance", new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {
                            clearView();
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," No se pudo guardar el balance",dialogView.getContext());
                        }
                    });
                    dialog.dismiss();
                }

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapterStockEvents.clear();
            hasMoreItems=true;
          //  list();
        }
    }


    private void implementsPaginate(){
        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerViewStockEvents,this)
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
        ApiClient.get().getSotckeventsByPage(mCurrentPage,getIntent().getLongExtra("ID",-1), new GenericCallback<List<StockEvent>>() {
            @Override
            public void onSuccess(List<StockEvent> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapterStockEvents.getItemCount();
                    mAdapterStockEvents.pushList(data);
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


    private void loadOptions(){
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(BalanceActivity.this, options);
                popup.getMenuInflater().inflate(R.menu.menu_balance, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_detail:
                                hideDetail();
                                return true;
                            case android.R.id.home:
                                finish();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void hideDetail(){
        if(mAdapterStockEvents.getHideDetail()){
            mAdapterStockEvents.setHideDetail(false);
            clearView();
        }else{
            mAdapterStockEvents.setHideDetail(true);
            clearView();
        }


    }
}

/*
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_balance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail:
                hideDetail();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
 */