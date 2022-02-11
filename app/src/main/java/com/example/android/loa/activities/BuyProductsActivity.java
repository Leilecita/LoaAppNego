package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.activities.balances.GeneralBalanceActivity;
import com.example.android.loa.activities.balances.GeneralBalanceByItemTypeActivity;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ItemProductAdapter;
import com.example.android.loa.adapters.ProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.example.android.loa.network.models.ResponseData;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerModel;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.example.android.loa.types.Constants;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuyProductsActivity extends BaseActivity implements Paginate.Callbacks, OnChangeViewStock, OnSelectedItem, OnSelectedProductItem {

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

    private String mQuery = "";
    private String token = "";

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
    private List<String> mTypesAutoCompl;
    private List<String> mBrands;
    private List<String> mBrandsAutoCompl;
    private List<String> mItems;
    private List<String> mModels;
    private List<String> mModelsAutoCompl;

    //top selectcion
    private TextView mQuantityPordByFilter;

    private TextView mEmptyRecyclerView;

    private LinearLayout balance;
    private LinearLayout addProduct;

    private LinearLayout home;
    private LinearLayout options;
    private TextView title;
    private SearchView searchView;

    private AutoCompleteTextView auto_type ;
    private AutoCompleteTextView auto_brand;
    private AutoCompleteTextView auto_model;

    private ItemProductAdapter mGridProductAdapter;
    private RecyclerView mGridRecyclerViewItem;
    private RecyclerView.LayoutManager gridlayoutmanagerItem;

    private String group;
    private ImageView view_group;
    private String stockcero;
    private TextView text_stock_cero;
    private LinearLayout addBill;

    public void OnChangeViewStock(){
        loadSumAllStockByProduct();
    }

    public void scrollToPosition(Integer position){
    }

    @Override
    public int getLayoutRes() {
        return R.layout.act_products_buys;
    }

    public void onReloadTotalQuantityStock(){
        loadSumAllStockByProduct();
    }


    private void cleanAutocompleteView(){
        auto_brand.setVisibility(View.GONE);
        auto_type.setVisibility(View.GONE);
        auto_model.setVisibility(View.GONE);

        auto_brand.setText("");
        auto_model.setText("");
        auto_type.setText("");
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

        balance.setVisibility(View.GONE);

        cleanAutocompleteView();
    }

    public void onSelectedItem(String brand, String type, String selection){

        if(selection.equals("brand")){

            if(!brand.equals("Nuevo")){
                mBrand=brand;
                if(brand.equals("Todos")){
                    brand_name.setText("Marca");
                    select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                }else{
                    brand_name.setText(brand);
                    select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                }
            }else{
                addProduct();
            }

            mGridAdapter.clear();
            auto_brand.setVisibility(View.GONE);
            auto_brand.setText("");
        }

        if(selection.equals("type")){
            if(!type.equals("Nuevo")){
                mType=type;
                if(type.equals("Todos")){
                    art_name.setText("Articulo");
                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                }else{
                    art_name.setText(type);
                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                }
                balance.setVisibility(View.VISIBLE);
            }else{
                addProduct();
            }
            mTypeGridAdapter.clear();

            auto_type.setVisibility(View.GONE);
            auto_type.setText("");
        }

        if(selection.equals("model")){

            if(!brand.equals("Nuevo")){
                mModel=type;

                if(brand.equals("Todos")){
                    model_name.setText("Modelo");
                    select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                }else{
                    model_name.setText(type);
                    select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                }
            }else{
                addProduct();
            }
            mModelGridAdapter.clear();

            auto_model.setVisibility(View.GONE);
            auto_model.setText("");
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
        //showBackArrow();

        this.group = "no";
        this.stockcero = "false";

        addBill = findViewById(R.id.add_bill);
        addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BuyProductsActivity.this, BuyBillingsActivity.class));
            }
        });

        text_stock_cero = findViewById(R.id.stockcero);
        text_stock_cero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stockcero.equals("true")){
                    stockcero = "false";
                    text_stock_cero.setText("con stock 0");
                }else{
                    stockcero = "true";
                    text_stock_cero.setText("sin stock 0");
                }

                clearAndList();
            }
        });

        view_group = findViewById(R.id.view_group);
        view_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(group.equals("yes")){
                    group = "no";
                    view_group.setImageDrawable(getResources().getDrawable(R.drawable.desagrupar));
                    Toast.makeText(BuyProductsActivity.this,"La vista esta desagrupada", Toast.LENGTH_SHORT).show();

                }else{
                    group = "yes";
                    view_group.setImageDrawable(getResources().getDrawable(R.drawable.agrupar));
                    Toast.makeText(BuyProductsActivity.this,"La vista esta agrupada", Toast.LENGTH_SHORT).show();
                }

                clearAndList();
            }
        });

        mGridRecyclerViewItem = findViewById(R.id.list_items);
        gridlayoutmanagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerViewItem.setLayoutManager(gridlayoutmanagerItem);
        mGridProductAdapter = new ItemProductAdapter(this, new ArrayList<SpinnerItem>());
        mGridRecyclerViewItem.setAdapter(mGridProductAdapter);
        mGridProductAdapter.setOnSelectedProductItem(this);

        listItems();

        auto_type = findViewById(R.id.auto_type);
        auto_brand = findViewById(R.id.auto_brand);
        auto_model = findViewById(R.id.auto_model);

        this.searchView = findViewById(R.id.searchview);
        options = findViewById(R.id.options);
        loadOptions();

        home = findViewById(R.id.line_home);
        title = findViewById(R.id.title);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        balance = findViewById(R.id.balance);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mItem.equals("Todos") && !mType.equals("Todos") && !mQuantityPordByFilter.getText().toString().matches("")
                    && mModel.equals("Todos")
                ){
                    Intent i = new Intent(BuyProductsActivity.this, GeneralBalanceByItemTypeActivity.class);
                    i.putExtra("TYPE", mType);
                    i.putExtra("ITEM", mItem);
                    i.putExtra("BRAND", mBrand); // nuevo
                    i.putExtra("CANT", mQuantityPordByFilter.getText().toString().trim());
                    BuyProductsActivity.this.startActivity(i);
                }

                if(!mModel.equals("Todos")){
                    Toast.makeText(BuyProductsActivity.this, "No se puede realizar balance para un modelo en particular", Toast.LENGTH_LONG).show();
                }
            }
        });

        mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ProductAdapter(this,new ArrayList<Product>());
        mAdapter.setOnChangeViewStock(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setProduct_sales(false);
        // mAdapter.setExtendedDate(getExpandedDate());

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



                if(auto_brand.getVisibility() == View.VISIBLE){
                    auto_brand.setVisibility(View.GONE);

                }else{
                    cleanAutocompleteView();
                    auto_brand.setVisibility(View.VISIBLE);
                }

                if(mGridAdapter.getList().size() > 0 ){
                    mGridAdapter.clear();
                }else{
                    mTypeGridAdapter.clear();
                    mModelGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, "Todos", mType, "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            SpinnerData sp2=new SpinnerData("Todos","#64B5F6");
                            data.brands.add(0,sp2);
                            SpinnerData sp1=new SpinnerData("Nuevo","#64B5F6");
                            data.brands.add(sp1);
                            mGridAdapter.pushList(data.brands);

                            mBrandsAutoCompl = createArrayBrand(data.brands);

                            ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>
                                    (getBaseContext(), R.layout.item_auto, mBrandsAutoCompl);
                            auto_brand.setThreshold(1);
                            auto_brand.setAdapter(adapterBrand);
                            auto_brand.setDropDownBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.rec_text_edit));

                            auto_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selected=auto_brand.getText().toString().trim();
                                    mBrand = selected;
                                    brand_name.setText(selected);
                                    select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                                    mGridAdapter.clear();
                                    auto_brand.setVisibility(View.GONE);

                                    clearAndList();
                                    hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                                }
                            });
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

                if(auto_type.getVisibility() == View.VISIBLE){
                    auto_type.setVisibility(View.GONE);

                }else{

                    cleanAutocompleteView();
                    auto_type.setVisibility(View.VISIBLE);
                }
                if(mTypeGridAdapter.getList().size() > 0){
                    mTypeGridAdapter.clear();
                }else{

                    mGridAdapter.clear();
                    mModelGridAdapter.clear();
                    ApiClient.get().getSpinners(mItem, "Todos", "Todos", "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {

                            SpinnerType sp2=new SpinnerType("Todos","#64B5F6");
                            data.types.add(0,sp2);

                            SpinnerType sp1=new SpinnerType("Nuevo","#64B5F6");
                            data.types.add(sp1);
                            mTypeGridAdapter.pushList(data.types);

                            mTypesAutoCompl = createArrayType(data.types);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getBaseContext(), R.layout.item_auto, mTypesAutoCompl);
                            auto_type.setThreshold(1);
                            auto_type.setAdapter(adapter);
                            auto_type.setDropDownBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.rec_text_edit));

                            auto_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selected=auto_type.getText().toString().trim();
                                    mType = selected;
                                    art_name.setText(selected);
                                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                                    mTypeGridAdapter.clear();
                                    auto_type.setVisibility(View.GONE);

                                    clearAndList();

                                    hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                                }
                            });
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

                if(auto_model.getVisibility() == View.VISIBLE){
                    auto_model.setVisibility(View.GONE);
                }else{

                    cleanAutocompleteView();
                    auto_model.setVisibility(View.VISIBLE);
                }

                if(mModelGridAdapter.getList().size() > 0){
                    mModelGridAdapter.clear();
                }else{
                    mGridAdapter.clear();
                    mTypeGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, mBrand, mType, "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            SpinnerModel sp2=new SpinnerModel("Todos");
                            data.models.add(0,sp2);

                            SpinnerModel sp1=new SpinnerModel("Nuevo");
                            data.models.add(sp1);
                            mModelGridAdapter.pushList(data.models);

                            mModelsAutoCompl = createArrayModel(data.models);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getBaseContext(), R.layout.item_auto, mModelsAutoCompl);
                            auto_model.setThreshold(1);
                            auto_model.setAdapter(adapter);
                            auto_model.setDropDownBackgroundDrawable(getBaseContext().getResources().getDrawable(R.drawable.rec_text_edit));

                            auto_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selected=auto_model.getText().toString().trim();
                                    mModel = selected;
                                    model_name.setText(selected);
                                    select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                                    mModelGridAdapter.clear();
                                    auto_model.setVisibility(View.GONE);

                                    clearAndList();

                                    hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                                }
                            });
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        loadSpinners();

        mEmptyRecyclerView=findViewById(R.id.empty);

        //topBarListener();

        loadSumAllStockByProduct();

        getClients();

        implementsPaginate();

        search();
    }

    private void listItems(){

        ApiClient.get().getItems(new GenericCallback<List<SpinnerItem>>() {
            @Override
            public void onSuccess(List<SpinnerItem> data) {
                SpinnerItem s=new SpinnerItem("Todos");
                data.add(0,s);

                for(int i=0; i < data.size(); ++i){
                    if(data.get(i).item.equals(Constants.ITEM_HOMBRE)){
                        data.get(i).resId = R.drawable.bmancl;
                    }else if(data.get(i).item.equals(Constants.ITEM_TODOS)){
                        data.get(i).resId = R.drawable.ballcl;
                    }else if(data.get(i).item.equals(Constants.ITEM_DAMA)){
                        data.get(i).resId = R.drawable.bwomcl;
                    }else if(data.get(i).item.equals(Constants.ITEM_NINIO)){
                        data.get(i).resId = R.drawable.bnincl;
                    }else if(data.get(i).item.equals(Constants.ITEM_ACCESORIO)){
                        data.get(i).resId = R.drawable.bacccl;
                    }else if(data.get(i).item.equals(Constants.ITEM_TECNICO)){
                        data.get(i).resId = R.drawable.btecl;
                    }else if(data.get(i).item.equals(Constants.ITEM_CALZADO)){
                        data.get(i).resId = R.drawable.bcalcl;
                    }else if(data.get(i).item.equals(Constants.ITEM_OFERTA)){
                        data.get(i).resId = R.drawable.bofercl;
                    }else if(data.get(i).item.equals(Constants.ITEM_LUZ)){
                        data.get(i).resId = R.drawable.bluzcl;
                    }
                }

                mGridProductAdapter.setItems(data);

            }

            @Override
            public void onError(Error error) {

            }
        });
    }



    private void loadSpinners(){
        ApiClient.get().getSpinners(mItem, mBrand, mType, mModel,"false",new GenericCallback<Spinners>() {
            @Override
            public void onSuccess(Spinners data) {
                mItems=createArrayItem(data.items);

                mBrands=createArrayBrand(data.brands);
                mBrandsAutoCompl = mBrands;
                mBrandsAutoCompl.remove(0);

                mTypes=createArrayType(data.types);
                mTypesAutoCompl = mTypes;
                mTypesAutoCompl.remove(0);

                mModels=createArrayModel(data.models);

            }
            @Override
            public void onError(Error error) {

            }
        });
    }

    public void onSelectedProductItem(String item){
        mItem = item;

        if(item.equals("Todos")){

            select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
            select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
            select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

            auto_brand.setVisibility(View.GONE);
            auto_type.setVisibility(View.GONE);
            auto_model.setVisibility(View.GONE);

            auto_brand.setText("");
            auto_model.setText("");
            auto_type.setText("");
        }

        listProdListener();

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();
        clearView();
        loadSpinners();
    }

    private void listProdListener(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void clearView(){
        if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            mAdapter.resetPrevOpenView();

            if(group.equals("yes")){
                mAdapter.setIsGrouped(true);
            }else {
                mAdapter.setIsGrouped(false);
            }

            hasMoreItems=true;
            loadSumAllStockByProduct();
        }
    }

    private void clearAndList(){
        clearView();
        list2(mQuery);
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

    public void list2(final String query){
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;

        loadingInProgress=true;

        if(mAdapter.getItemCount()==0){
            //  swipeRefreshLayout.setRefreshing(true);
        }
        ApiClient.get().getProductsByPageByItemByBrandAndType(mCurrentPage, mItem, mBrand, mType,mModel,"false", query,group, stockcero,new GenericCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {

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
                        //   swipeRefreshLayout.setRefreshing(false);

                        if(mCurrentPage == 0 && data.size()==0){
                            mEmptyRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            mEmptyRecyclerView.setVisibility(View.GONE);
                        }
                    }
                }else{
                    Log.e("TOKEN", "Descarta token: " + newToken);
                }
            }
            @Override
            public void onError(Error error) {
                loadingInProgress = false;
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }



    private void search(){

        searchView.setQueryHint("Buscar");


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setMaxWidth(Integer.MAX_VALUE);

               // addProduct.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                searchView.requestFocus();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
                //addProduct.setVisibility(View.VISIBLE);
                return false;
            }
        });

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

                    list2(newText.trim().toLowerCase());
                }
                return false;
            }
        });
    }



    private void loadOptions(){
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(BuyProductsActivity.this, options);
                popup.getMenuInflater().inflate(R.menu.menu_add_product, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_deleted_products:
                                startActivity(new Intent(getBaseContext(),DeletedProductsActivity.class));
                                return true;
                            case R.id.general_balance:
                                startActivity(new Intent(getBaseContext(), GeneralBalanceActivity.class));
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

    @Override
    public void onLoadMore() {
        list2(mQuery);
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    private void createMenuIn(final TextView detail){
        PopupMenu popup = new PopupMenu(this, detail);
        popup.getMenuInflater().inflate(R.menu.menu_stock_in_mini, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.in_update_app:
                        detail.setText(Constants.MENU_INGRESO_ACTUALIZACION_APP);
                        return true;
                    case R.id.in_buy:
                        detail.setText(Constants.MENU_INGRESO_COMPRA);
                        return true;
                    case R.id.in_balance_stock:
                        detail.setText(Constants.MENU_INGRESO_BALANCE_STOCK);
                        return true;
                    case R.id.in_stock_local:
                        detail.setText(Constants.MENU_INGRESO_STOCK_LOCAL);
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void addProduct(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_product, null);
        builder.setView(dialogView);

        final EditText price= dialogView.findViewById(R.id.price);
        final TextView detail= dialogView.findViewById(R.id.detail);
        final EditText stock= dialogView.findViewById(R.id.stock);
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

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuIn(detail);
            }
        });

        final Spinner spinnerModel=dialogView.findViewById(R.id.spinner_model);

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

                if(!type.getText().toString().trim().equals("") & !brand.getText().toString().trim().equals("")  & !item.getText().toString().trim().equals("") & !stock.getText().toString().trim().equals("")
                        & !detail.getText().toString().trim().equals("")){

                    String typeProduct=type.getText().toString().trim();
                    String brandProduct=brand.getText().toString().trim();
                    String itemProduct=item.getText().toString().trim();
                    String modelProduct=model.getText().toString().trim();
                    String stockProduct=stock.getText().toString().trim();
                    String detailProduct=detail.getText().toString().trim();

                    String priceProduct = ((price.getText().toString().trim().equals("")) ? "0.0" : price.getText().toString().trim());

                    Product newProduct= new Product(itemProduct,typeProduct,brandProduct,modelProduct,Integer.valueOf(stockProduct));

                    ApiClient.get().checkExistProduct(itemProduct,brandProduct,typeProduct,modelProduct,Integer.valueOf(stockProduct),detailProduct,priceProduct,new GenericCallback<ResponseData>() {
                        @Override
                        public void onSuccess(ResponseData data) {
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
                            DialogHelper.get().showMessage("Error",error.message,BuyProductsActivity.this);
                            dialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(BuyProductsActivity.this,"Todos los campos deben estar completos",Toast.LENGTH_SHORT).show();
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



    private List<String> createArrayType(List<SpinnerType> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        listN.add("Nuevo");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).type != null){
                listN.add(list.get(i).type);
            }
        }

        return listN;
    }

    private List<String> createArrayBrand(List<SpinnerData> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        listN.add("Nuevo");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).brand != null){
                listN.add(list.get(i).brand);
            }
        }

        return listN;
    }

    private List<String> createArrayModel(List<SpinnerModel> list){
        List<String> listN=new ArrayList<>();
        listN.add("Nuevo");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).model != null){
                listN.add(list.get(i).model);
            }
        }

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

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}



   /* private void changeCircleSelected(){

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

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Todos";
                changeCircleSelected();
                all.setImageResource(R.drawable.ball);

                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

                auto_brand.setVisibility(View.GONE);
                auto_type.setVisibility(View.GONE);
                auto_model.setVisibility(View.GONE);

                auto_brand.setText("");
                auto_model.setText("");
                auto_type.setText("");
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
    */
