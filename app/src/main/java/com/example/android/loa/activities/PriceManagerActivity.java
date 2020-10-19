package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProduct;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.PriceManagerAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportProduct;
import com.example.android.loa.network.models.ResponseData;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerModel;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;



public class PriceManagerActivity extends BaseActivity implements Paginate.Callbacks, OnSelectedItem , OnSelectedProduct {

    private RecyclerView mRecyclerView;
    private PriceManagerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    String mType;
    String mBrand;
    String mItem;
    String mModel;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private ImageView man;
    private ImageView woman;
    private ImageView boy;
    private ImageView accesories;
    private ImageView tecnico;
    private ImageView zapas;
    private ImageView luz;
    private ImageView oferta;
    private ImageView all;

    private RecyclerView mGridRecyclerView;
    private RecyclerView mGridRecyclerViewType;
    private RecyclerView mGridRecyclerViewModel;
    private ItemAdapter mGridAdapter;
    private ItemAdapterType mTypeGridAdapter;
    private ItemAdapterModel mModelGridAdapter;

    private GridLayoutManager gridlayoutmanager;

    private LinearLayout select_art;
    private LinearLayout select_brand;
    private LinearLayout select_model;
    private LinearLayout change_prices;

    private TextView art_name;
    private TextView brand_name;
    private TextView model_name;

    private TextView mQuantityPordByFilter;

    private TextView mEmptyRecyclerView;

    private ProgressDialog progress;

   // private Double mPercentege = 5d;

    private TextView selected_products_number;
    private LinearLayout select_all;
    private TextView text_select_all;
    private Boolean selectAll = false;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_price_product;
    }

    private void cleanInfo(){
        mBrand="Todos";
        mType="Todos";
        mModel="Todos";

        brand_name.setText("");
        art_name.setText("");
        model_name.setText("");
        brand_name.setHint("Marca");
        art_name.setHint("Articulo");
        model_name.setHint("Modelo");
    }

    public void onSelectedProduct(Integer val){
        selected_products_number.setText(String.valueOf(val));
    }

    public void onSelectedItem(String brand, String type, String selection){

        if(selection.equals("brand")){
            mBrand=brand;
            brand_name.setText(brand);
            mGridAdapter.clear();
        }

        if(selection.equals("type")){
            mType=type;
            art_name.setText(type);
            mTypeGridAdapter.clear();
        }

        if(selection.equals("model")){
            mModel=type;
            model_name.setText(type);
            mModelGridAdapter.clear();
        }

        clearView();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Administrador de precios");


        select_all =  findViewById(R.id.select_all);
        text_select_all =  findViewById(R.id.text_select);
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectAll){
                    mAdapter.unSelectAll();
                    text_select_all.setText("Seleccionar todo");
                    selectAll = false;
                    selected_products_number.setText(String.valueOf(mAdapter.getSelectedProducts().size()));
                }else{
                    mAdapter.selectAll();
                    text_select_all.setText("Limpiar selección");
                    selectAll = true;
                    selected_products_number.setText(String.valueOf(mAdapter.getSelectedProducts().size()));
                }

            }
        });

        selected_products_number =  findViewById(R.id.number);
        change_prices =  findViewById(R.id.change_prices);
        change_prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrices();
            }
        });

        mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new PriceManagerAdapter(this,new ArrayList<ReportProduct>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnSelectedProduct(this);

        mGridRecyclerView =  findViewById(R.id.recycler_view_grid);
        gridlayoutmanager=new GridLayoutManager(this,5);
        mGridRecyclerView.setLayoutManager(gridlayoutmanager);

        mGridAdapter = new ItemAdapter(this, new ArrayList<SpinnerData>());
        mGridRecyclerView.setAdapter(mGridAdapter);
        mGridAdapter.setOnSelectedItem(this);

        mGridRecyclerViewType =  findViewById(R.id.recycler_view_grid_type);
        gridlayoutmanager=new GridLayoutManager(this,5);
        mGridRecyclerViewType.setLayoutManager(gridlayoutmanager);
        mTypeGridAdapter=new ItemAdapterType(this,new ArrayList<SpinnerType>());

        mGridRecyclerViewType.setAdapter(mTypeGridAdapter);
        mTypeGridAdapter.setOnSelectedItem(this);

//model
        mGridRecyclerViewModel =  findViewById(R.id.recycler_view_grid_model);
        gridlayoutmanager=new GridLayoutManager(this,5);
        mGridRecyclerViewModel.setLayoutManager(gridlayoutmanager);
        mModelGridAdapter=new ItemAdapterModel(this,new ArrayList<SpinnerModel>());

        mGridRecyclerViewModel.setAdapter(mModelGridAdapter);
        mModelGridAdapter.setOnSelectedItem(this);
//model

        select_art=findViewById(R.id.select_art);
        select_brand=findViewById(R.id.select_brand);
        select_model=findViewById(R.id.select_model);

        art_name=findViewById(R.id.art_name);
        brand_name=findViewById(R.id.brand_name);
        model_name=findViewById(R.id.model_name);

        mQuantityPordByFilter=findViewById(R.id.quantity_prod);

        mType="Todos";
        mBrand="Todos";
        mItem="Todos";
        mModel="Todos";

        select_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mGridAdapter.getList().size() > 0 ){
                    mGridAdapter.clear();
                }else{
                    mTypeGridAdapter.clear();
                    mModelGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, "Todos", "Todos", "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            mGridAdapter.pushList(data.brands);
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        select_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTypeGridAdapter.getList().size() > 0){
                    mTypeGridAdapter.clear();
                }else{
                    mGridAdapter.clear();
                    mModelGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, "Todos", "Todos", "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            mTypeGridAdapter.pushList(data.types);
                        }
                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        select_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mModelGridAdapter.getList().size() > 0){
                    mModelGridAdapter.clear();
                }else{
                    mGridAdapter.clear();
                    mTypeGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, mBrand, mType, "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            mModelGridAdapter.pushList(data.models);
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        mEmptyRecyclerView=findViewById(R.id.empty);

        topBarListener();

       // loadSumAllStockByProduct();

        implementsPaginate();
    }


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

        listProdListener();

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();

        clearView();

        //Limpia lista de productos seleccionados para cambiar precio
        mAdapter.clearSelectedProducts();
    }

    private void topBarListener(){
        man=findViewById(R.id.man);
        woman=findViewById(R.id.woman);
        boy=findViewById(R.id.boy);
        tecnico=findViewById(R.id.tecnico);
        zapas=findViewById(R.id.zapas);
        accesories=findViewById(R.id.acces);
        luz=findViewById(R.id.luz);
        oferta=findViewById(R.id.oferta);
        all=findViewById(R.id.all);

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
    }


    private void listProdListener(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void clearView(){
      //  if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            //loadSumAllStockByProduct();
       // }
    }

    private void clearAndList(){
        clearView();
        list2();
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

    public void list2(){
        loadingInProgress=true;

        ApiClient.get().getProductsByPageByItemByBrandAndTypePriceManager(mCurrentPage, mItem, mBrand, mType,mModel,"false", new GenericCallback<List<ReportProduct>>() {
            @Override
            public void onSuccess(List<ReportProduct> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                    select_all.setVisibility(View.VISIBLE);
                }else{
                    select_all.setVisibility(View.GONE);
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }
                }
                loadingInProgress = false;

                if(mCurrentPage == 0 && data.size()==0){
                    mEmptyRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyRecyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
    }

    @Override
    public void onLoadMore() {
        list2();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    private void changePrices(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.change_prices, null);
        builder.setView(dialogView);

        final TextView info= dialogView.findViewById(R.id.info);
        final RelativeLayout add= dialogView.findViewById(R.id.add);
        final ImageView im_add= dialogView.findViewById(R.id.image_add);
        final ImageView im_less= dialogView.findViewById(R.id.image_less);
        final RelativeLayout less= dialogView.findViewById(R.id.less);
        final EditText per = dialogView.findViewById(R.id.porcentaje);

        per.setText(String.valueOf(5d));
        per.setSelection(per.getText().length());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im_add.setBackground(getResources().getDrawable(R.drawable.circle_selected_prod));
                im_add.setImageDrawable(getResources().getDrawable(R.drawable.positive_white));

                im_less.setBackground(getResources().getDrawable(R.drawable.circle_product));
                im_less.setImageDrawable(getResources().getDrawable(R.mipmap.lessstock));


                Double d = Double.valueOf(per.getText().toString().trim());
                if(d < 0){
                    d= d*(-1);
                }
                per.setText(String.valueOf(d));
            }
        });

        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                im_less.setBackground(getResources().getDrawable(R.drawable.circle_selected_prod));
                im_less.setImageDrawable(getResources().getDrawable(R.drawable.negative_white));

                im_add.setBackground(getResources().getDrawable(R.drawable.circle_product));
                im_add.setImageDrawable(getResources().getDrawable(R.mipmap.addstock));

                Double d = Double.valueOf(per.getText().toString().trim());
                if(d > 0){
                    d= d*(-1);
                }
                per.setText(String.valueOf(d));
            }
        });

        info.setText(mItem+" "+mType+" "+mBrand+" "+mModel);

        final TextView cant= dialogView.findViewById(R.id.cant);
        final EditText price= dialogView.findViewById(R.id.price);
        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);

        cant.setText(String.valueOf(mAdapter.getSelectedProducts().size()));

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiClient.get().updatePrices(generateString(mAdapter.getSelectedProducts()), Double.valueOf(per.getText().toString().trim()), new GenericCallback<ResponseData>() {
                    @Override
                    public void onSuccess(ResponseData data) {
                        mAdapter.clearSelectedProducts();
                        selected_products_number.setText("0");
                        clearAndList();
                        dialog.dismiss();

                        Toast.makeText(getBaseContext() ,"Los precios han sido cambiados", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
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

    private String generateString(List<Long> list){
        String s="";
        for (int i = 0; i < list.size() ; ++i){

            if(i == list.size()-1){
                s = s+list.get(i);
            }else{
                s = s+list.get(i)+";";
            }
        }
        return s;
    }
}