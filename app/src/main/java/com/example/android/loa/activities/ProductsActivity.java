package com.example.android.loa.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;

import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.example.android.loa.network.models.ResponseData;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerModel;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductsActivity extends BaseActivity implements Paginate.Callbacks, OnChangeViewStock, OnSelectedItem {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
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
    private Integer checkSpinner;

    private ImageView man;
    private ImageView woman;
    private ImageView boy;
    private ImageView accesories;
    private ImageView tecnico;
    private ImageView zapas;
    private ImageView luz;
    private ImageView oferta;
    private ImageView all;

 /*   private TextView textMan;
    private TextView textWoman;
    private TextView textBoy;
    private TextView textTec;
    private TextView textZap;
    private TextView textAcc;
    private TextView textLuz;
    private TextView textOferta;
    private TextView textAll;
    */

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

    private TextView art_name;
    private TextView brand_name;
    private TextView model_name;

    //para crear products
    private List<String> mTypes;
    private List<String> mBrands;
    private List<String> mItems;
    private List<String> mModels;

    private TextView mQuantityPordByFilter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyRecyclerView;

    private Boolean mViewModel;


    public void OnChangeViewStock(){
        loadSumAllStockByProduct();
    }

    public void scrollToPosition(Integer position){
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_prod_3;
    }

    public void onReloadTotalQuantityStock(){
        loadSumAllStockByProduct();
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

    public void onSelectedItem(String brand, String type, String selection){

        if(selection.equals("brand")){
            if(!brand.equals("Nuevo")){
                mBrand=brand;
                brand_name.setText(brand);
            }
            if(brand.equals("Nuevo")){
                addProduct();
            }
            mGridAdapter.clear();
        }

        if(selection.equals("type")){
            if(!type.equals("Nuevo")){
                mType=type;
                art_name.setText(type);
            }
            if(type.equals("Nuevo")){
                addProduct();
            }
            mTypeGridAdapter.clear();
        }

        if(selection.equals("model")){
            if(!brand.equals("Nuevo")){
                mModel=type;
                model_name.setText(type);
            }
            if(brand.equals("Nuevo")){
                addProduct();
            }
            mModelGridAdapter.clear();
        }

        loadSumAllStockByProduct();
        clearView();
    }

    private void getClients(){
        ApiClient.get().getClients(new GenericCallback<List<ReportSimpelClient>>() {
            @Override
            public void onSuccess(List<ReportSimpelClient> data) {
                mAdapter.setClients(data);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void loadSumAllStockByProduct(){
        ApiClient.get().getSumStockByFilterProducts( mItem, mBrand, mType, mModel,"false", new GenericCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                mQuantityPordByFilter.setText(String.valueOf(data));
            }
            @Override
            public void onError(Error error) {

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mViewModel=false;
        setTitle("Productos");
        mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ProductAdapter(this,new ArrayList<Product>());
        mAdapter.setOnChangeViewStock(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setExtendedDate(getExpandedDate());

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
                                SpinnerData sp1=new SpinnerData("Nuevo","#64B5F6");
                                data.brands.add(sp1);
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
                                SpinnerType sp1=new SpinnerType("Nuevo","#64B5F6");
                                data.types.add(sp1);
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
                            SpinnerModel sp1=new SpinnerModel("Nuevo");
                            data.models.add(sp1);
                            mModelGridAdapter.pushList(data.models);
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        loadSpinners();

        swipeRefreshLayout =  findViewById(R.id.swipeRefreshLayout);
        mEmptyRecyclerView=findViewById(R.id.empty);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void
            onRefresh() {

             clearAndList();
            }
        });

        topBarListener();

        loadSumAllStockByProduct();

        getClients();

        implementsPaginate();
    }


    private void loadSpinners(){
        ApiClient.get().getSpinners(mItem, mBrand, mType, mModel,"false",new GenericCallback<Spinners>() {
            @Override
            public void onSuccess(Spinners data) {
                mItems=createArrayItem(data.items);
                mBrands=createArrayBrand(data.brands);
                mTypes=createArrayType(data.types);
                mModels=createArrayModel(data.models);
            }
            @Override
            public void onError(Error error) {

            }
        });
    }


    private void changeCircleSelected(){

        viewModel();

     /*   woman.setBackgroundResource(R.drawable.circle_unselected);
        boy.setBackgroundResource(R.drawable.circle_unselected);
        man.setBackgroundResource(R.drawable.circle_unselected);
        tecnico.setBackgroundResource(R.drawable.circle_unselected);
        zapas.setBackgroundResource(R.drawable.circle_unselected);
        accesories.setBackgroundResource(R.drawable.circle_unselected);
        luz.setBackgroundResource(R.drawable.circle_unselected);
        oferta.setBackgroundResource(R.drawable.circle_unselected);
        all.setBackgroundResource(R.drawable.circle_unselected);

        */

     /*   textZap.setTextColor(getResources().getColor(R.color.word_clear));
        textTec.setTextColor(getResources().getColor(R.color.word_clear));
        textMan.setTextColor(getResources().getColor(R.color.word_clear));
        textWoman.setTextColor(getResources().getColor(R.color.word_clear));
        textBoy.setTextColor(getResources().getColor(R.color.word_clear));
        textAcc.setTextColor(getResources().getColor(R.color.word_clear));
        textLuz.setTextColor(getResources().getColor(R.color.word_clear));
        textOferta.setTextColor(getResources().getColor(R.color.word_clear));
        textAll.setTextColor(getResources().getColor(R.color.word_clear));

        textZap.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textTec.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textMan.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textLuz.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textOferta.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textAll.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
*/
        listProdListener();

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();

        clearView();

        //ver que onda
        loadSpinners();
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
/*
        textAcc=findViewById(R.id.textAcc);
        textMan=findViewById(R.id.textMan);
        textWoman=findViewById(R.id.textWoman);
        textZap=findViewById(R.id.textZapas);
        textTec=findViewById(R.id.textTec);
        textBoy=findViewById(R.id.textBoy);
        textLuz=findViewById(R.id.textLuz);
        textOferta=findViewById(R.id.textOferta);
        textAll=findViewById(R.id.textAll);
        */

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Todos";
                mViewModel=false;
                changeCircleSelected();
                //all.setBackgroundResource(R.drawable.circle);
              //  textAll.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
               // textAll.setTextColor(getResources().getColor(R.color.word));
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                mViewModel=false;
                changeCircleSelected();
                //woman.setBackgroundResource(R.drawable.circle);
               // textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
               // textWoman.setTextColor(getResources().getColor(R.color.word));
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                mViewModel=false;
                changeCircleSelected();
               // man.setBackgroundResource(R.drawable.circle);
               // textMan.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
               // textMan.setTextColor(getResources().getColor(R.color.word));
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                mViewModel=false;
                changeCircleSelected();
               // boy.setBackgroundColor(getResources().getColor(R.color.trasparente));
                //boy.setBackgroundResource(R.drawable.circle);
               // textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
               // textBoy.setTextColor(getResources().getColor(R.color.word));
            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorio";
                mViewModel=false;
                changeCircleSelected();
                accesories.setBackgroundResource(R.drawable.circle);
              //  textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
              //  textAcc.setTextColor(getResources().getColor(R.color.word));
            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                mViewModel=true;
                changeCircleSelected();
               // tecnico.setBackgroundResource(R.drawable.circle);
              //  textTec.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
              //  textTec.setTextColor(getResources().getColor(R.color.word));
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Calzado";
                mViewModel=true;
                changeCircleSelected();
               // zapas.setBackgroundResource(R.drawable.circle);
               // textZap.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
               // textZap.setTextColor(getResources().getColor(R.color.word));
                // tecnico.setBackgroundResource(R.drawable.circle_selected);
            }
        });

        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Luz";
                mViewModel=false;
                changeCircleSelected();
              //  luz.setBackgroundResource(R.drawable.circle);
              //  textLuz.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
              //  textLuz.setTextColor(getResources().getColor(R.color.word));
            }
        });

        oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Oferta";
                mViewModel=false;
                changeCircleSelected();
               // oferta.setBackgroundResource(R.drawable.circle);
               // textOferta.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
             //   textOferta.setTextColor(getResources().getColor(R.color.word));
            }
        });
    }

    private void viewModel(){
        if(mViewModel){
            select_model.setVisibility(View.VISIBLE);
            mAdapter.setIsModel(true);
        }else{
            select_model.setVisibility(View.GONE);
            mAdapter.setIsModel(false);
        }
    }
    private void listProdListener(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void clearView(){
        if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            mAdapter.resetPrevOpenView();
            hasMoreItems=true;
            loadSumAllStockByProduct();
        }
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

        if(mAdapter.getItemCount()==0){
            swipeRefreshLayout.setRefreshing(true);
        }
        ApiClient.get().getProductsByPageByItemByBrandAndType(mCurrentPage, mItem, mBrand, mType,mModel,"false", new GenericCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
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
                swipeRefreshLayout.setRefreshing(false);

                if(mCurrentPage == 0 && data.size()==0){
                    mEmptyRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyRecyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(Error error) {
                loadingInProgress = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_deleted_products:
                startActivity(new Intent(this,DeletedProductsActivity.class));
                return true;
            case android.R.id.home:
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void addProduct(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_product, null);
        builder.setView(dialogView);

        final EditText type= dialogView.findViewById(R.id.type);
        final EditText brand= dialogView.findViewById(R.id.brand);
        final EditText item= dialogView.findViewById(R.id.item);
        final EditText model= dialogView.findViewById(R.id.model);
        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);

        brand.setVisibility(View.GONE);
        type.setVisibility(View.GONE);
        item.setVisibility(View.GONE);
        model.setVisibility(View.GONE);

        final Spinner spinnerModel=dialogView.findViewById(R.id.spinner_model);

        if(mViewModel){

            //---------------- models
            ArrayAdapter<String> adapterModel = new ArrayAdapter<String>(this,
                    R.layout.spinner_item, mModels);
            adapterModel.setDropDownViewResource(R.layout.spinner_item);
            spinnerModel.setAdapter(adapterModel);
            spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selected=spinnerModel.getSelectedItem().toString().trim();
                    if(selected.equals("Nuevo")){
                        model.setVisibility(View.VISIBLE);
                        spinnerModel.setVisibility(View.GONE);
                    }else{
                        model.setText(selected);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    model.setText("");
                }
            });
        }else{
            spinnerModel.setVisibility(View.GONE);
        }

        //---------------- brands
        ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mBrands);

        adapterBrand.setDropDownViewResource(R.layout.spinner_item);
        final Spinner spinnerBrand=dialogView.findViewById(R.id.spinner_brand);
        spinnerBrand.setAdapter(adapterBrand);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selected=spinnerBrand.getSelectedItem().toString().trim();
                if(selected.equals("Nuevo")){
                    brand.setVisibility(View.VISIBLE);
                    spinnerBrand.setVisibility(View.GONE);
                }else{
                    brand.setText(selected);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                brand.setText("");
            }
        });

        //------------- types
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mTypes);
        adapterType.setDropDownViewResource(R.layout.spinner_item);
        final Spinner spinnerType=dialogView.findViewById(R.id.spinner_type);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected=spinnerType.getSelectedItem().toString().trim();
                if(selected.equals("Nuevo")){
                    type.setVisibility(View.VISIBLE);
                    type.setFocusableInTouchMode(true);
                    spinnerType.setVisibility(View.GONE);
                }else{
                    type.setText(selected);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type.setText("");
            }
        });

        //------------- items
        ArrayAdapter<String> adapterItem = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mItems);
        adapterItem.setDropDownViewResource(R.layout.spinner_item);
        final Spinner spinner_item=dialogView.findViewById(R.id.spinner_item);
        spinner_item.setAdapter(adapterItem);
        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected=spinner_item.getSelectedItem().toString().trim();
                if(selected.equals("Nuevo")){
                    item.setVisibility(View.VISIBLE);
                    spinner_item.setVisibility(View.GONE);
                }else{
                    item.setText(selected);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                item.setText("");
            }
        });

        limitHeighSpinner(spinner_item);
        limitHeighSpinner(spinnerBrand);
        limitHeighSpinner(spinner_item);

        if(!mItem.equals("Todos")){
            item.setVisibility(View.VISIBLE);
            item.setText(mItem);
            item.setFocusable(false);
            spinner_item.setVisibility(View.GONE);
        }
        if(!mType.equals("Todos")){
            type.setVisibility(View.VISIBLE);
            type.setText(mType);
            type.setFocusable(false);
            spinnerType.setVisibility(View.GONE);
        }
        if(!mBrand.equals("Todos")){
            brand.setVisibility(View.VISIBLE);
            brand.setText(mBrand);
            brand.setFocusable(false);
            spinnerBrand.setVisibility(View.GONE);
        }
        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brand.setFocusableInTouchMode(true);
                spinnerBrand.setVisibility(View.VISIBLE);
            }
        });

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setFocusable(true);
                spinner_item.setVisibility(View.VISIBLE);
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type.setFocusableInTouchMode(true);

                spinnerType.setVisibility(View.VISIBLE);
            }
        });

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!type.getText().toString().trim().equals("") & !brand.getText().toString().trim().equals("")  & !item.getText().toString().trim().equals("")){

                    String typeProduct=type.getText().toString().trim();
                    String brandProduct=brand.getText().toString().trim();
                    String itemProduct=item.getText().toString().trim();
                    String modelProduct=model.getText().toString().trim();

                    Product newProduct= new Product(itemProduct,typeProduct,brandProduct,modelProduct,0);

                    ApiClient.get().checkExistProduct(itemProduct,brandProduct,typeProduct,modelProduct ,new GenericCallback<ResponseData>() {
                        @Override
                        public void onSuccess(ResponseData data) {
                           // mAdapter.pushItem(data);
                            if(data.res.equals("existe")){
                                Toast.makeText(dialogView.getContext(),"Este tipo de producto ya existe ", Toast.LENGTH_LONG).show();
                            }else if(data.res.equals("creado")){
                                Toast.makeText(dialogView.getContext(),"Producto creado", Toast.LENGTH_LONG).show();
                                //clearView(); este duplica
                                clearAndList();
                                loadSpinners();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error",error.message,ProductsActivity.this);
                            dialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(ProductsActivity.this,"Todos los campos deben estar completos",Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private List<String> createArrayType(List<SpinnerType> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).type != null){
                listN.add(list.get(i).type);
            }
        }
        listN.add("Nuevo");
        return listN;
    }
    private List<String> createArrayBrand(List<SpinnerData> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).brand != null){
                listN.add(list.get(i).brand);
            }
        }
        listN.add("Nuevo");
        return listN;
    }

    private List<String> createArrayModel(List<SpinnerModel> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).model != null){
                listN.add(list.get(i).model);
            }
        }
        listN.add("Nuevo");
        return listN;
    }

    private List<String> createArrayItem(List<SpinnerItem> list){
        List<String> listN=new ArrayList<>();

        listN.add("Hombre");
        listN.add("Dama");
        listN.add("Niño");
        listN.add("Tecnico");
        listN.add("Accesorio");
        listN.add("Calzado");

        return listN;
    }

    private void limitHeighSpinner(Spinner spinner){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
            popupWindow.setHeight(400);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }


    private String getExpandedDate(){

        System.out.println("Entra a expanded date");

        String date= DateHelper.get().actualDateExtractions();
        String time= DateHelper.get().getOnlyTime(date);

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            //Date date1 = sdf.parse("19:28:00");
            Date date1 = sdf.parse(time);
            //Date date2 = sdf.parse("21:13:00");
            Date date2 = sdf.parse("04:13:00");

            // Outputs -1 as date1 is before date2
            System.out.println(date1.compareTo(date2));

            if(date1.compareTo(date2) < 0){
                System.out.println(date1.compareTo(date2));

                return DateHelper.get().getPreviousDay(date);
            }else{
                return date;
            }
/*
            // Outputs 1 as date1 is after date1
            System.out.println(date2.compareTo(date1));

            date2 = sdf.parse("19:28:00");
            // Outputs 0 as the dates are now equal
            System.out.println(date1.compareTo(date2));
            */

        } catch (ParseException e){
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }
}