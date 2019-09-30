package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ProductAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivityOld extends BaseActivity implements Paginate.Callbacks, OnChangeViewStock {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Spinner spinnerArt;
    private Spinner spinerBrand;

    private List<String> mTypes;
    private List<String> mBrands;
    private List<String> mItems;

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
    private TextView itemSelectedName;

    private ImageView search;

    private RecyclerView mGridRecyclerView;
    private ItemAdapter mGridAdapter;
    private GridLayoutManager gridlayoutmanager;

    public void OnChangeViewStock(){
        if(linefilter.getVisibility() == View.VISIBLE){
            linefilter.setVisibility(View.GONE);
            lineBrandType.setVisibility(View.GONE);
        }else{
            linefilter.setVisibility(View.VISIBLE);

            lineBrandType.setVisibility(View.VISIBLE);
        }

    }
    public void onReloadTotalQuantityStock(){
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_protducts2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Productos");

        mGridRecyclerView =  findViewById(R.id.recycler_view_grid);
        gridlayoutmanager=new GridLayoutManager(this,4);
        mGridRecyclerView.setLayoutManager(gridlayoutmanager);
        mGridAdapter = new ItemAdapter(this, new ArrayList<SpinnerData>());
        mGridRecyclerView.setAdapter(mGridAdapter);


        mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ProductAdapter(this,new ArrayList<Product>());
        mAdapter.setOnChangeViewStock(this);

        mRecyclerView.setAdapter(mAdapter);

        spinnerArt =  findViewById(R.id.spinner_type);
        spinerBrand =  findViewById(R.id.spinner_brand);

        mBrands=new ArrayList<String>();
        mTypes=new ArrayList<String>();

        checkSpinner=0;

        mType="Todos";
        mBrand="Todos";
        mItem="Todos";

        topBarSelection="";

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


        lineBrandType=findViewById(R.id.lineBrandType);
        linefilter=findViewById(R.id.lineFilter);

        topBarListener();

        ImageView search = findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearView();
            }
        });

        FloatingActionButton addProduct=findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();

            }
        });

        implementsPaginate();
    }
    private void refreshSpinners(){
        checkSpinner=0;
        //clearView();
    }


    private void loadSpinnerArt(){
        ApiClient.get().getSpinnerByItemByTypeByBrandType("type", mItem, mBrand, mType, new GenericCallback<List<SpinnerType>>() {
            @Override
            public void onSuccess(List<SpinnerType> data) {
                createSpinnerArt(spinnerArt,createArrayType(data));

            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void loadSpinnerBrand(){
        ApiClient.get().getSpinnerByItemByTypeByBrand("brand", mItem, mBrand, mType, new GenericCallback<List<SpinnerData>>() {
            @Override
            public void onSuccess(List<SpinnerData> data) {
                 createSpinnerBrand(spinerBrand,createArrayBrand(data));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void loadSpinnerItem(){
        ApiClient.get().getSpinnerByItemByTypeByBrand("item", mItem, mBrand, mType, new GenericCallback<List<SpinnerData>>() {
            @Override
            public void onSuccess(List<SpinnerData> data) {
                createArrayItem(data);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }
    private void loadSpinners(){
        checkSpinner=0;

        loadSpinnerBrand();
        loadSpinnerItem();
        loadSpinnerArt();
    }

    private void changeCircleSelected(){
       /* woman.setBackgroundResource(R.drawable.circle);
        man.setBackgroundResource(R.drawable.circle);
        tecnico.setBackgroundResource(R.drawable.circle);
        zapas.setBackgroundResource(R.drawable.circle);
        accesories.setBackgroundResource(R.drawable.circle);
*/
        textZap.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textTec.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textMan.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        listProdListener();

        mBrand="Todos";
        mType="Todos";
        loadSpinners();
        clearView();
    }

    private void topBarListener(){

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                changeCircleSelected();
                textWoman.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                changeCircleSelected();

                textMan.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                changeCircleSelected();
                textBoy.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorios";
                changeCircleSelected();
                textAcc.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                // accesories.setBackgroundResource(R.drawable.circle_selected);

            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                changeCircleSelected();
                textTec.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                // tecnico.setBackgroundResource(R.drawable.circle_selected);
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Zapatillas";
                changeCircleSelected();
                textZap.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                // tecnico.setBackgroundResource(R.drawable.circle_selected);
            }
        });
    }

    private void listProdListener(){
        lineBrandType.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }


    private List<String> createArrayType(List<SpinnerType> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        listN.add("Todos");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).type != null){
                listN.add(list.get(i).type);
            }
        }
        listN.add("Nuevo");
        mTypes=listN;
        return listN;
    }
    private List<String> createArrayBrand(List<SpinnerData> list){
        List<String> listN=new ArrayList<>();
        listN.add("");
        listN.add("Todos");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).brand != null){
                listN.add(list.get(i).brand);
            }
        }
        listN.add("Crear nuevo");
        mBrands=listN;
        return listN;
    }

    private List<String> createArrayItem(List<SpinnerData> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).brand != null){
                listN.add(list.get(i).brand);
            }
        }
        listN.add("Crear nuevo");
        mItems=listN;
        return listN;
    }

    private void createSpinnerBrand(final Spinner spinnerBrand,List<String> brands){

        ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(this,
                R.layout.spinner_item_products, brands);

        adapterBrand.setDropDownViewResource(R.layout.spinner_item);
        spinnerBrand.setAdapter(adapterBrand);


        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBrand= spinnerBrand.getSelectedItem().toString().trim();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void createSpinnerArt(final Spinner spinnerArt, List<String> types){

        ArrayAdapter<String> adapterArt = new ArrayAdapter<String>(this,
                R.layout.spinner_item_products, types);

        adapterArt.setDropDownViewResource(R.layout.spinner_item);
        spinnerArt.setAdapter(adapterArt);



        spinnerArt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mType= spinnerArt.getSelectedItem().toString().trim();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void clearView(){

        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        type.setThreshold(1);
        type.setAdapter(adapter);

        ArrayAdapter<String> adapterB = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, mBrands);
        brand.setThreshold(1);
        brand.setAdapter(adapterB);

        ArrayAdapter<String> adapterI = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, mItems);
        item.setThreshold(1);
        item.setAdapter(adapterI);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!type.getText().equals("") & !brand.getText().equals("") ){
                    String typeProduct=type.getText().toString().trim();
                    String brandProduct=brand.getText().toString().trim();
                    String itemProduct=item.getText().toString().trim();

                    Product newProduct= new Product(itemProduct,typeProduct,brandProduct,0);
                    ApiClient.get().postProduct(newProduct, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            mAdapter.pushItem(data);
                            refreshSpinners();
                            Toast.makeText(dialogView.getContext(),"Se ha creado el producto "+data.type, Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error","Error al crear el producto",ProductsActivityOld.this);
                        }
                    });
                    dialog.dismiss();

                }else{
                    Toast.makeText(ProductsActivityOld.this,"Todos los campos deben estar completos",Toast.LENGTH_SHORT).show();
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
}

 /* ApiClient.get().getSpinner("brand",new GenericCallback<List<SpinnerData>>() {
            @Override
            public void onSuccess(List<SpinnerData> data) {
                createSpinnerBrand(spinerBrand,createArrayBrand(data));
            }

            @Override
            public void onError(Error error) {

            }
        });

        ApiClient.get().getSpinner("item",new GenericCallback<List<SpinnerData>>() {
            @Override
            public void onSuccess(List<SpinnerData> data) {
                createArrayItem(data);
            }

            @Override
            public void onError(Error error) {

            }
        });



        ApiClient.get().getSpinnerType("type",new GenericCallback<List<SpinnerType>>() {
            @Override
            public void onSuccess(List<SpinnerType> data) {
               // System.out.println("holaaa"+data.size());
                createSpinnerArt(spinnerArt,createArrayType(data));
            }

            @Override
            public void onError(Error error) {
                DialogHelper.get().showMessage("Error","no se pudo cargar el spinner",ProductsActivity.this);
            }
        });*/
