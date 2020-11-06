package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.activities.balances.GeneralBalanceActivity;
import com.example.android.loa.activities.balances.GeneralBalanceByItemTypeActivity;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;
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

    private String mQuery = "";
    private String token = "";

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

    //top selectcion
    private TextView mQuantityPordByFilter;

    private TextView mEmptyRecyclerView;

    private ImageView balance;

    private LinearLayout home;
    private LinearLayout options;
    private TextView title;
    private SearchView searchView;

    private LinearLayout balance_buys;

    public void OnChangeViewStock(){
        loadSumAllStockByProduct();
    }

    public void scrollToPosition(Integer position){
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_prod4;
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

        balance.setVisibility(View.GONE);
    }

    public void onSelectedItem(String brand, String type, String selection){

        if(selection.equals("brand")){

           // if(!brand.equals("Nuevo")){
            mBrand=brand;
            if(brand.equals("Todos")){
                brand_name.setText("Marca");
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
            }else{
                brand_name.setText(brand);
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));
            }
          /*  }else{
                addProduct();
            }*/

            mGridAdapter.clear();
        }

        if(selection.equals("type")){
           // if(!type.equals("Nuevo")){
                mType=type;
                if(type.equals("Todos")){
                    art_name.setText("Articulo");
                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                }else{
                    art_name.setText(type);
                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                }
                balance.setVisibility(View.VISIBLE);
           /* }else{
                addProduct();
            }*/
            mTypeGridAdapter.clear();
        }

        if(selection.equals("model")){
           // if(!brand.equals("Nuevo")){
                mModel=type;

                if(brand.equals("Todos")){
                    model_name.setText("Modelo");
                    select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
                }else{
                    model_name.setText(type);
                    select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                }
           /* }else{
                addProduct();
            }*/
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
        //showBackArrow();

        balance_buys = findViewById(R.id.buys);
        balance_buys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),BuyProductsActivity.class));
                finish();
            }
        });

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
        balance.setVisibility(View.GONE);
         balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getBaseContext(),"El balance se realiza de la planilla de compras y balance",Toast.LENGTH_SHORT).show();
             /*   if(!mItem.equals("Todos") && !mType.equals("Todos") && !mQuantityPordByFilter.getText().toString().matches("")){
                    Intent i = new Intent(getBaseContext(), GeneralBalanceByItemTypeActivity.class);
                    i.putExtra("TYPE", mType);
                    i.putExtra("ITEM", mItem);
                    i.putExtra("CANT", mQuantityPordByFilter.getText().toString().trim());
                    getBaseContext().startActivity(i);
                }*/
            }
        });

        mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ProductAdapter(this,new ArrayList<Product>());
        mAdapter.setOnChangeViewStock(this);
        mRecyclerView.setAdapter(mAdapter);

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
                                SpinnerData sp2=new SpinnerData("Todos","#64B5F6");
                                data.brands.add(0,sp2);
                               // SpinnerData sp1=new SpinnerData("Nuevo","#64B5F6");
                                //data.brands.add(sp1);
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

                                SpinnerType sp2=new SpinnerType("Todos","#64B5F6");
                                data.types.add(0,sp2);

                               // SpinnerType sp1=new SpinnerType("Nuevo","#64B5F6");
                                //data.types.add(sp1);
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
                            SpinnerModel sp2=new SpinnerModel("Todos");
                            data.models.add(0,sp2);

                          //  SpinnerModel sp1=new SpinnerModel("Nuevo");
                           // data.models.add(sp1);
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

        mEmptyRecyclerView=findViewById(R.id.empty);

        topBarListener();

        loadSumAllStockByProduct();

        getClients();

        implementsPaginate();

        search();
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

        ApiClient.get().getProductsByPageByItemByBrandAndType(mCurrentPage, mItem, mBrand, mType,mModel,"false", query,new GenericCallback<List<Product>>() {
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
            }
        });
    }

    private void search(){

        searchView.setQueryHint("Buscar");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setMaxWidth(Integer.MAX_VALUE);

                title.setVisibility(View.GONE);
                searchView.requestFocus();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
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
                PopupMenu popup = new PopupMenu(ProductsActivity.this, options);
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
}


 /*
   private void addProduct2(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_product_2, null);
        builder.setView(dialogView);

        final EditText item= dialogView.findViewById(R.id.item);
        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);

        ImageView all_model = dialogView.findViewById(R.id.all_models);
        ImageView all_brands = dialogView.findViewById(R.id.all_brands);
        ImageView all_types = dialogView.findViewById(R.id.all_types);

        final AutoCompleteTextView auto_type = dialogView.findViewById(R.id.auto_type);
        final AutoCompleteTextView auto_brand = dialogView.findViewById(R.id.auto_brand);
        final AutoCompleteTextView auto_model = dialogView.findViewById(R.id.auto_model);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.item_auto, mTypesAutoCompl);
        auto_type.setThreshold(1);//will start working from first character
        auto_type.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        auto_type.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_text_edit));
        all_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_type.showDropDown();
            }
        });

        ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>
                (this, R.layout.item_auto, mBrandsAutoCompl);
        auto_brand.setThreshold(1);//will start working from first character
        auto_brand.setAdapter(adapterBrand);//setting the adapter data into the AutoCompleteTextView
        auto_brand.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_text_edit));
        all_brands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_brand.showDropDown();
            }
        });

        ArrayAdapter<String> adapterModelAuto = new ArrayAdapter<String>
                (this, R.layout.item_auto, mModels);
        auto_model.setThreshold(1);//will start working from first character
        auto_model.setAdapter(adapterModelAuto);//setting the adapter data into the AutoCompleteTextView
        auto_model.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_text_edit));
        all_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_model.showDropDown();
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

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!auto_type.getText().toString().trim().equals("") & !auto_brand.getText().toString().trim().equals("")  & !item.getText().toString().trim().equals("")){

                    String typeProduct = auto_type.getText().toString().trim();
                    String brandProduct = auto_brand.getText().toString().trim();
                    String itemProduct = item.getText().toString().trim();
                    String modelProduct = auto_model.getText().toString().trim();

                    //Product newProduct= new Product(itemProduct,typeProduct,brandProduct,modelProduct,0);

                    ApiClient.get().checkExistProduct(itemProduct,brandProduct,typeProduct,modelProduct ,0,"",new GenericCallback<ResponseData>() {
                        @Override
                        public void onSuccess(ResponseData data) {
                            if(data.res.equals("existe")){
                                Toast.makeText(dialogView.getContext(),"Este tipo de producto ya existe ", Toast.LENGTH_LONG).show();
                            }else if(data.res.equals("creado")){
                                Toast.makeText(dialogView.getContext(),"Producto creado", Toast.LENGTH_LONG).show();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
  */