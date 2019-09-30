package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;

import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends BaseActivity implements Paginate.Callbacks, OnChangeViewStock, OnSelectedItem {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    String mType;
    String mBrand;
    String mItem;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private Integer checkSpinner;

    private LinearLayout man;
    private LinearLayout woman;
    private LinearLayout boy;
    private LinearLayout accesories;
    private LinearLayout tecnico;
    private LinearLayout zapas;

    private LinearLayout lineBrandType;
    private LinearLayout linefilter;

    private String topBarSelection;

    private TextView textMan;
    private TextView textWoman;
    private TextView textBoy;
    private TextView textTec;
    private TextView textZap;
    private TextView textAcc;

    private RecyclerView mGridRecyclerView;
    private RecyclerView mGridRecyclerViewType;
    private ItemAdapter mGridAdapter;
    private ItemAdapterType mTypeGridAdapter;
    private GridLayoutManager gridlayoutmanager;

    private LinearLayout select_art;
    private LinearLayout select_brand;

    private TextView art_name;
    private TextView brand_name;

    //para crear products
    private List<String> mTypes;
    private List<String> mBrands;
    private List<String> mItems;

    private TextView mQuantityPordByFilter;

    public void OnChangeViewStock(){
        if(lineBrandType.getVisibility() == View.VISIBLE){
            lineBrandType.setVisibility(View.GONE);
        }else{
            lineBrandType.setVisibility(View.VISIBLE);
        }
        loadSumAllStockByProduct();
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

        brand_name.setText("");
        art_name.setText("");
        brand_name.setHint("Marca");
        art_name.setHint("Articulo");


    }

    public void onSelectedItem(String brand, String type, String selection){

        if(selection.equals("brand")){
            if(!brand.equals("Salir") & !brand.equals("Nuevo")){
                mBrand=brand;
                brand_name.setText(brand);
            }

            if(brand.equals("Nuevo")){
                addProduct();
            }

            if(brand.equals("Salir")) {
               cleanInfo();
            }

            mGridAdapter.clear();
        }

        if(selection.equals("type")){
            if(!type.equals("Salir") & !type.equals("Nuevo")){
                mType=type;
                art_name.setText(type);
            }

            if(type.equals("Nuevo")){
                addProduct();
            }else if(type.equals("Salir"))
            {
               cleanInfo();
            }

            mTypeGridAdapter.clear();
        }

        loadSumAllStockByProduct();
        clearView();
    }

    private void loadSumAllStockByProduct(){
        ApiClient.get().getSumStockByFilterProducts( mItem, mBrand, mType, new GenericCallback<Integer>() {
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

        setTitle("Productos");
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

        select_art=findViewById(R.id.select_art);
        select_brand=findViewById(R.id.select_brand);

        art_name=findViewById(R.id.art_name);
        brand_name=findViewById(R.id.brand_name);
        mQuantityPordByFilter=findViewById(R.id.quantity_prod);

        mType="Todos";
        mBrand="Todos";
        mItem="Todos";

        select_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGridRecyclerView.setVisibility(View.VISIBLE);
                mGridRecyclerViewType.setVisibility(View.GONE);
                mTypeGridAdapter.clear();
                mGridAdapter.clear();
                ApiClient.get().getSpinnerByItemByTypeByBrand("brand", mItem, mBrand, mType, new GenericCallback<List<SpinnerData>>() {
                    @Override
                    public void onSuccess(List<SpinnerData> data) {

                        SpinnerData sp=new SpinnerData("Salir");
                        SpinnerData sp1=new SpinnerData("Nuevo");
                        data.add(sp);
                        data.add(sp1);

                        mGridAdapter.pushList(data); }
                    @Override
                    public void onError(Error error) {
                    }
                });
            }
        });

        select_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridRecyclerView.setVisibility(View.GONE);
                mGridRecyclerViewType.setVisibility(View.VISIBLE);
                mTypeGridAdapter.clear();
                mGridAdapter.clear();
                ApiClient.get().getSpinnerByItemByTypeByBrandType("type", mItem, mBrand, mType, new GenericCallback<List<SpinnerType>>() {
                    @Override
                    public void onSuccess(List<SpinnerType> data) {
                        SpinnerType sp=new SpinnerType("Salir");
                        SpinnerType sp1=new SpinnerType("Nuevo");

                        data.add(sp);
                        data.add(sp1);
                        mTypeGridAdapter.pushList(data);
                    }
                    @Override
                    public void onError(Error error) {

                    }
                });
            }
        });


        topBarSelection="";

        lineBrandType=findViewById(R.id.lineBrandType);
        linefilter=findViewById(R.id.lineFilter);

        topBarListener();

        loadSpinnerItem();
        loadSpinnerBrand();
        loadSpinnerArt();

       // implementsPaginate();
    }


    private void changeCircleSelected(){

        woman.setBackgroundResource(R.drawable.circle_unselected);
        boy.setBackgroundResource(R.drawable.circle_unselected);
        man.setBackgroundResource(R.drawable.circle_unselected);
        tecnico.setBackgroundResource(R.drawable.circle_unselected);
        zapas.setBackgroundResource(R.drawable.circle_unselected);
        accesories.setBackgroundResource(R.drawable.circle_unselected);

        textZap.setTextColor(getResources().getColor(R.color.word_clear));
        textTec.setTextColor(getResources().getColor(R.color.word_clear));
        textMan.setTextColor(getResources().getColor(R.color.word_clear));
        textWoman.setTextColor(getResources().getColor(R.color.word_clear));
        textBoy.setTextColor(getResources().getColor(R.color.word_clear));
        textAcc.setTextColor(getResources().getColor(R.color.word_clear));


        textZap.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textTec.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textMan.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        listProdListener();

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();

        clearView();
    }

    private void topBarListener(){
        man=findViewById(R.id.man);
        woman=findViewById(R.id.woman);
        boy=findViewById(R.id.boy);
        tecnico=findViewById(R.id.tecnico);
        zapas=findViewById(R.id.zapas);
        accesories=findViewById(R.id.acces);



        textAcc=findViewById(R.id.textAcc);
        textMan=findViewById(R.id.textMan);
        textWoman=findViewById(R.id.textWoman);
        textZap=findViewById(R.id.textZapas);
        textTec=findViewById(R.id.textTec);
        textBoy=findViewById(R.id.textBoy);
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                changeCircleSelected();
                woman.setBackgroundResource(R.drawable.circle);
                textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textWoman.setTextColor(getResources().getColor(R.color.word));
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                changeCircleSelected();
                man.setBackgroundResource(R.drawable.circle);

                textMan.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textMan.setTextColor(getResources().getColor(R.color.word));
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                changeCircleSelected();
                boy.setBackgroundColor(getResources().getColor(R.color.trasparente));
                boy.setBackgroundResource(R.drawable.circle);
                textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textBoy.setTextColor(getResources().getColor(R.color.word));


            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorios";
                changeCircleSelected();
                accesories.setBackgroundResource(R.drawable.circle);
                textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textAcc.setTextColor(getResources().getColor(R.color.word));
               // accesories.setBackgroundResource(R.drawable.circle_selected);

            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                changeCircleSelected();
                tecnico.setBackgroundResource(R.drawable.circle);
                textTec.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textTec.setTextColor(getResources().getColor(R.color.word));
               // tecnico.setBackgroundResource(R.drawable.circle_selected);
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Zapatillas";
                changeCircleSelected();
                zapas.setBackgroundResource(R.drawable.circle);
                textZap.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                textZap.setTextColor(getResources().getColor(R.color.word));
                // tecnico.setBackgroundResource(R.drawable.circle_selected);
            }
        });
    }

    private void listProdListener(){
        lineBrandType.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }



    private void clearView(){

        mCurrentPage = 0;
        mAdapter.clear();
        mAdapter.resetPrevOpenView();
        hasMoreItems=true;
        loadSumAllStockByProduct();
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
        ApiClient.get().getProductsByPageByItemByBrandAndType(mCurrentPage, mItem, mBrand, mType, new GenericCallback<List<Product>>() {
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
            }
            @Override
            public void onError(Error error) {
                loadingInProgress = false;
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
            case R.id.action_add:
                addProduct();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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

        final AutoCompleteTextView type= dialogView.findViewById(R.id.type);
        final AutoCompleteTextView brand= dialogView.findViewById(R.id.brand);
        final AutoCompleteTextView item= dialogView.findViewById(R.id.item);
        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, mTypes);
        type.setThreshold(0);
        type.setAdapter(adapter);

        ArrayAdapter<String> adapterB = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, mBrands);
        brand.setThreshold(0);
        brand.setAdapter(adapterB);

        ArrayAdapter<String> adapterI = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, mItems);
        item.setThreshold(0);
        item.setAdapter(adapterI);


        //---------------- brands
        ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mBrands);

        adapterBrand.setDropDownViewResource(R.layout.spinner_item);
        final Spinner spinnerBrand=dialogView.findViewById(R.id.spinner_brand);
        spinnerBrand.setAdapter(adapterBrand);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                brand.setText(spinnerBrand.getSelectedItem().toString().trim());
                brand.setThreshold(15);

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
                type.setText(spinnerType.getSelectedItem().toString().trim());
                type.setThreshold(15);
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
                item.setText(spinner_item.getSelectedItem().toString().trim());
                item.setThreshold(15);
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
            item.setText(mItem);
            item.setFocusable(false);
            spinner_item.setVisibility(View.GONE);
        }
        if(!mType.equals("Todos")){
            type.setText(mType);
            type.setFocusable(false);
            spinnerType.setVisibility(View.GONE);
        }
        if(!mBrand.equals("Todos")){
            brand.setText(mBrand);
            brand.setFocusable(false);
            spinnerBrand.setVisibility(View.GONE);
        }
        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brand.setFocusable(true);
                spinnerBrand.setVisibility(View.VISIBLE);
                brand.setThreshold(1);
            }
        });

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setFocusable(true);
                spinner_item.setVisibility(View.VISIBLE);
                item.setThreshold(1);
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type.setFocusable(true);
                spinnerType.setVisibility(View.VISIBLE);
                type.setThreshold(1);
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

                    Product newProduct= new Product(itemProduct,typeProduct,brandProduct,0);
                    ApiClient.get().postProduct(newProduct, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            mAdapter.pushItem(data);
                            Toast.makeText(dialogView.getContext(),"Se ha creado el producto "+data.type, Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error","Error al crear el producto",ProductsActivity.this);
                        }
                    });
                    dialog.dismiss();

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

    private void loadSpinnerArt(){
        ApiClient.get().getSpinnerByItemByTypeByBrandType("type", "Todos", "Todos", "Todos", new GenericCallback<List<SpinnerType>>() {
            @Override
            public void onSuccess(List<SpinnerType> data) {
                mTypes=createArrayType(data);
                mTypes.add(0, "");

            }
            @Override
            public void onError(Error error) {
            }
        });
    }

    private void loadSpinnerBrand(){
        ApiClient.get().getSpinnerByItemByTypeByBrand("brand", "Todos", "Todos", "Todos", new GenericCallback<List<SpinnerData>>() {
            @Override
            public void onSuccess(List<SpinnerData> data) {
                mBrands=createArrayBrand(data);
                mBrands.add(0, "");

            }
            @Override
            public void onError(Error error) {
            }
        });
    }


    private void loadSpinnerItem(){
        ApiClient.get().getSpinnerItemByItemByTypeByBrandType("item", "Todos", "Todos", "Todos", new GenericCallback<List<SpinnerItem>>() {
            @Override
            public void onSuccess(List<SpinnerItem> data) {
                mItems=createArrayItem(data);
                mItems.add(0, "");
            }

            @Override
            public void onError(Error error) {

            }
        });

    }


    private List<String> createArrayType(List<SpinnerType> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).type != null){
                listN.add(list.get(i).type);
            }
        }
        return listN;
    }
    private List<String> createArrayBrand(List<SpinnerData> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).brand != null){
                listN.add(list.get(i).brand);
            }
        }
        return listN;
    }

    private List<String> createArrayItem(List<SpinnerItem> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).item != null){
                listN.add(list.get(i).item);
            }
        }
        return listN;
    }

    private void limitHeighSpinner(Spinner spinner){

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }

}

