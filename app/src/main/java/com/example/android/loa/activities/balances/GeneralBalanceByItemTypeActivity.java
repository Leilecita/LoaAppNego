package com.example.android.loa.activities.balances;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.adapters.GeneralStockEventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.GeneralStock;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class GeneralBalanceByItemTypeActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private GeneralStockEventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private String mItem;
    private String mType;
    private String mBrand; //nuevo
    private TextView mQuantityGeneralProduct;
    private TextView mItemText;
    private TextView mTypeText;
    private TextView mBrandText;

    private LinearLayout home;


    @Override
    public int getLayoutRes() {
        return R.layout.general_balance_activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showBackArrow();

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setTitle("Stock producto general");

        mRecyclerView = findViewById(R.id.list_general_stock_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GeneralStockEventAdapter(this, new ArrayList<GeneralStock>());
        mRecyclerView.setAdapter(mAdapter);

        Button generate_balance= findViewById(R.id.balance_item);
        generate_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                balance();
            }
        });

        mBrand = "";
        mItem=getIntent().getStringExtra("ITEM");
        mType=getIntent().getStringExtra("TYPE");
        mBrand=getIntent().getStringExtra("BRAND"); //nuevo

        mQuantityGeneralProduct = findViewById(R.id.quantity_prod);
        mQuantityGeneralProduct.setText(getIntent().getStringExtra("CANT"));

        mItemText= findViewById(R.id.item_product);
        mItemText.setText(getIntent().getStringExtra("ITEM"));

        mTypeText=findViewById(R.id.type_product);
        mTypeText.setText(getIntent().getStringExtra("TYPE"));

        mBrandText=findViewById(R.id.product_brand);
        mBrandText.setText(getIntent().getStringExtra("BRAND"));

        implementsPaginate();
    }

    private void balance(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GeneralBalanceByItemTypeActivity.this);
        LayoutInflater inflater = (LayoutInflater) GeneralBalanceByItemTypeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.load_general_balance, null);
        builder.setView(dialogView);

        final TextView type= dialogView.findViewById(R.id.type);
        type.setText(mType);
        final TextView item= dialogView.findViewById(R.id.item);
        item.setText(mItem);
        final TextView brand= dialogView.findViewById(R.id.brand);
        item.setText(mBrand);
        final TextView quantity= dialogView.findViewById(R.id.quantity);
        quantity.setText(mQuantityGeneralProduct.getText().toString().trim());
        final EditText stock_real= dialogView.findViewById(R.id.stock);

        final TextView dif= dialogView.findViewById(R.id.dif);

        stock_real.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(!stock_real.getText().toString().trim().equals("") ){

                    Integer i=Integer.valueOf(stock_real.getText().toString().trim());
                    Integer i2=Integer.valueOf(mQuantityGeneralProduct.getText().toString().trim());

                    dif.setText(String.valueOf(i-i2));
                }else{

                }
            }
        });



        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockP=stock_real.getText().toString().trim();
                String difP=dif.getText().toString().trim();
                String resultP;
                if(Integer.valueOf(difP) == 0 ){
                    resultP="bien";
                }else{
                    resultP="mal";
                }

                GeneralStock g=new GeneralStock(mItem,mType,Integer.valueOf(mQuantityGeneralProduct.getText().toString().trim()),Integer.valueOf(stockP),resultP,Integer.valueOf(difP));
                g.brand = mBrand;
                ApiClient.get().postGeneralStockEvent(g, new GenericCallback<GeneralStock>() {
                    @Override
                    public void onSuccess(GeneralStock data) {
                        clearView();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
                    dialog.dismiss();
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


}