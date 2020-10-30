package com.example.android.loa.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;

import com.example.android.loa.adapters.ItemDetailAdapter;
import com.example.android.loa.adapters.StockEventAdapter;

import com.example.android.loa.adapters.StockStatisticsEventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;

import com.example.android.loa.network.models.ReportDetail;
import com.example.android.loa.network.models.ReportStatistic;
import com.example.android.loa.network.models.ReportStockEvent;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerModel;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class StatisticsActivity extends BaseActivity implements Paginate.Callbacks, OnSelectedItem {

    private RecyclerView mRecyclerView;
    private StockStatisticsEventAdapter mAdapter;
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

    private TextView art_name;
    private TextView brand_name;
    private TextView model_name;

    private TextView mDif;
    private TextView mEntriesQuantity;
    private TextView mSalesQuantity;
    private TextView mLocalExtractions;
    private TextView mSalariesOutcomes;
    private TextView mMercOutcomes;
    private TextView mAmountSales;
    private TextView mStockProduct;

    private TextView mViewDateSince;
    private TextView mViewDateTo;
    private String mDateSine;
    private String mDateTo;

    private TextView mEmptyRecyclerView;
    private TextView products;

    private ProgressBar simpleProgressBar;
    private ImageView clean_date_since;
    private ImageView clean_date_to;

    private LinearLayout bottomSheet;

    private List<String> mBrandsAutoCompl;
    private List<String> mTypesAutoCompl;
    private List<String> mModelsAutoCompl;

    private AutoCompleteTextView auto_type ;
    private AutoCompleteTextView auto_brand;
    private AutoCompleteTextView auto_model;

    private LinearLayout home;
    private ImageView view_eye;

    private RecyclerView mRecyclerViewDetail;
    private RecyclerView.LayoutManager layoutManagerDetail;
    private ItemDetailAdapter mDetailAdapter;

   // private String mDetails;

    //Initi selection
    private Integer mTotalSales;
    private Integer mItemTotalSales;
    private Integer mArtTotalSales;
    private Integer mBrandTotalSales;

    private Integer mTotalEntries;
    private Integer mItemTotalEntries;
    private Integer mArtTotalEntries;
    private Integer mBrandTotalEntries;

    private Integer mTotalStock;
    private Integer mItemTotalStock;
    private Integer mArtTotalStock;
    private Integer mBrandTotalStock;

    private LinearLayout line_statistics_item;
    private LinearLayout line_statistics_type;
    private LinearLayout line_statistics_brand;

    private TextView percentage_entries_item;
    private TextView percentage_entries_type;
    private TextView percentage_entries_brand;

    private TextView percentage_sale_item;
    private TextView percentage_sale_type;
    private TextView percentage_sale_brand;

    private TextView percentage_stock_item;
    private TextView percentage_stock_type;
    private TextView percentage_stock_brand;
    //public FitChart fitChart_entries_item ;

    private DecoView decoView_entries_precentege_item;
    private DecoView deco_item_sales;
    private DecoView deco_item_stock;

    private DecoView deco_type_entries;
    private DecoView deco_type_sales;
    private DecoView deco_type_stock;

    private DecoView deco_brand_entries;
    private DecoView deco_brand_sales;
    private DecoView deco_brand_stock;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_statistics2;
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

        simpleProgressBar.setVisibility(View.VISIBLE);

        if(selection.equals("detail")){
            if(brand.equals("Todos")){
                mDetailAdapter.unSelectAll();
            }
        }else{
            loadEventsDetail();
        }

        if(selection.equals("brand")){

            if(!brand.equals("Todos")){
                mBrand = brand;
                brand_name.setText(brand);
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

                line_statistics_brand.setVisibility(View.VISIBLE);
            }else{
                mBrand = "Todos";
                brand_name.setText("Marca");
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                line_statistics_brand.setVisibility(View.GONE);
            }
            mGridAdapter.clear();

            auto_brand.setVisibility(View.GONE);
            auto_brand.setText("");


        }

        if(selection.equals("type")){
            if(!type.equals("Todos")) {
                mType = type;
                art_name.setText(type);
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

                line_statistics_type.setVisibility(View.VISIBLE);
            }else{
                mType = "Todos";
                art_name.setText("Articulo");
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                line_statistics_type.setVisibility(View.GONE);
            }
            mTypeGridAdapter.clear();

            auto_type.setVisibility(View.GONE);
            auto_type.setText("");

        }

        if(selection.equals("model")){
            if(!brand.equals("Todos")) {
                mModel = type;
                model_name.setText(type);
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));

            }else{
                mModel = "Todos";
                model_name.setText("Modelo");
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));
            }
            mModelGridAdapter.clear();

        }
        clearAndList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // showBackArrow();
        decoView_entries_precentege_item = findViewById(R.id.dynamicArcView);
        deco_item_sales =  findViewById(R.id.deco_item_vtas);
        deco_item_stock = findViewById(R.id.deco_item_stock);

        deco_type_entries = findViewById(R.id.deco_type_entries);
        deco_type_sales =  findViewById(R.id.deco_type_sales);
        deco_type_stock = findViewById(R.id.deco_type_stock);

        deco_brand_entries =  findViewById(R.id.deco_brand_entries);
        deco_brand_sales =  findViewById(R.id.deco_brand_vtas);
        deco_brand_stock =  findViewById(R.id.deco_brand_stock);

        line_statistics_item = findViewById(R.id.line_statistics_item);
        line_statistics_type = findViewById(R.id.line_statistics_type);
        line_statistics_brand = findViewById(R.id.line_statistics_brand);

        percentage_sale_item = findViewById(R.id.percentage_sale_item);
        percentage_sale_type = findViewById(R.id.percentage_sale_type);
        percentage_sale_brand = findViewById(R.id.percentage_sale_brande);

        percentage_entries_item = findViewById(R.id.percentage_entries_item);
        percentage_entries_type = findViewById(R.id.percentage_entries_type);
        percentage_entries_brand = findViewById(R.id.percentage_entries_brand);

        percentage_stock_item = findViewById(R.id.percentage_stock_item);
        percentage_stock_type = findViewById(R.id.percentage_stock_type);
        percentage_stock_brand = findViewById(R.id.percentage_stock_brand);

       /* fitChart_entries_item = findViewById(R.id.fitChart);
        fitChart_entries_item.setMinValue(0f);
        fitChart_entries_item.setMaxValue(100f);

        */

        view_eye = findViewById(R.id.view_eye);
        view_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerView.getVisibility() == View.VISIBLE){
                    view_eye.setImageDrawable(getResources().getDrawable(R.drawable.open_eye));
                    mRecyclerView.setVisibility(View.GONE);
                }else{
                    view_eye.setImageDrawable(getResources().getDrawable(R.drawable.close_eye));
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        auto_type = findViewById(R.id.auto_type);
        auto_brand = findViewById(R.id.auto_brand);
        auto_model = findViewById(R.id.auto_model);

        clean_date_since = findViewById(R.id.clean_dateSince);
        clean_date_since.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateSine = "Todos";
                mViewDateSince.setText("");
                clearAndList();
            }
        });
        clean_date_to = findViewById(R.id.clean_dateTo);
        clean_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTo = "Todos";
                mViewDateTo.setText("");
                clearAndList();
            }
        });

        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        mViewDateSince = findViewById(R.id.date_since);
        mViewDateTo = findViewById(R.id.date_to);

        mViewDateSince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(mViewDateSince,"since");
            }
        });

        mViewDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(mViewDateTo,"to" );
            }
        });

       /* mRecyclerView =  findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new StockStatisticsEventAdapter(this,new ArrayList<ReportStockEvent>());
        mRecyclerView.setAdapter(mAdapter);

        */

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

        mGridRecyclerViewModel =  findViewById(R.id.recycler_view_grid_model);
        gridlayoutmanager=new GridLayoutManager(this,5);
        mGridRecyclerViewModel.setLayoutManager(gridlayoutmanager);
        mModelGridAdapter=new ItemAdapterModel(this,new ArrayList<SpinnerModel>());
        mGridRecyclerViewModel.setAdapter(mModelGridAdapter);
        mModelGridAdapter.setOnSelectedItem(this);

        select_art=findViewById(R.id.select_art);
        select_brand=findViewById(R.id.select_brand);
        select_model=findViewById(R.id.select_model);

        art_name=findViewById(R.id.art_name);
        brand_name=findViewById(R.id.brand_name);
        model_name=findViewById(R.id.model_name);

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
                    auto_brand.setVisibility(View.VISIBLE);
                }

                if(mGridAdapter.getList().size() > 0 ){
                    mGridAdapter.clear();
                }else{
                    mTypeGridAdapter.clear();
                    mModelGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, "Todos", "Todos", "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {

                            SpinnerData sp1=new SpinnerData("Todos","#64B5F6");
                            data.brands.add(0,sp1);
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
                                    select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

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
                            SpinnerType sp1=new SpinnerType("Todos","#64B5F6");
                            data.types.add(0,sp1);
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
                                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

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

                if(mModelGridAdapter.getList().size() > 0){
                    mModelGridAdapter.clear();
                }else{
                    mGridAdapter.clear();
                    mTypeGridAdapter.clear();

                    ApiClient.get().getSpinners(mItem, mBrand, mType, "Todos","false",new GenericCallback<Spinners>() {
                        @Override
                        public void onSuccess(Spinners data) {
                            SpinnerModel sp1=new SpinnerModel("Todos");
                            data.models.add(0,sp1);
                            mModelGridAdapter.pushList(data.models);

                            mModelsAutoCompl = createArrayModel(data.models);
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        mEmptyRecyclerView=findViewById(R.id.empty);

       // mAmountSales = findViewById(R.id.sum_amount_sales);
      //  mMercOutcomes = findViewById(R.id.sum_mercaderia_out);
        mStockProduct = findViewById(R.id.quantity_stock);

        mEntriesQuantity = findViewById(R.id.entries);
        mSalesQuantity = findViewById(R.id.sales);
       // mDif = findViewById(R.id.dif);

        topBarListener();

        bottomSheet = findViewById(R.id.bottomSheet);
        topBarListenerBottomShet(bottomSheet);

        loadStatisticValues();

        loadEventsDetail();

        implementsPaginate();
    }

    private void topBarListenerBottomShet(View bottomShet){

        mRecyclerView =  bottomShet.findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new StockStatisticsEventAdapter(this,new ArrayList<ReportStockEvent>());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerViewDetail =  bottomShet.findViewById(R.id.recycler_detail);
        layoutManagerDetail = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        mRecyclerViewDetail.setLayoutManager(layoutManagerDetail);
        mDetailAdapter=new ItemDetailAdapter(this,new ArrayList<ReportDetail>());
        mRecyclerViewDetail.setAdapter(mDetailAdapter);
        mDetailAdapter.setOnSelectedItem(this);

    }

    private void cleanStatisticsValues(){
      //  mAmountSales.setText("0");
        mEntriesQuantity.setText("0");
        mSalesQuantity.setText("0");
       // mMercOutcomes.setText("0");
        mStockProduct.setText("0");
//        mDif.setText("0");
    }

    private void loadEventsDetail(){
       ApiClient.get().getDetails(mItem, mBrand, mType, mModel,new GenericCallback<List<ReportDetail>>() {
           @Override
           public void onSuccess(List<ReportDetail> data) {
               ReportDetail r = new ReportDetail("Todos");
               data.add(0,r);
               mDetailAdapter.setItems(data);
           }

           @Override
           public void onError(Error error) {

           }
       });
    }

    private String calculatePercentage(Integer totalBig, Integer valueToCalclulate){
        return String.valueOf(100*valueToCalclulate / totalBig);
    }

    private void createSeriesInit( Integer mParcialtotal, DecoView decoview){

        final SeriesItem seriesItemInit = new SeriesItem.Builder(this.getResources().getColor(R.color.word_clear2))
                .setRange(0, 100, 100)
                .build();

        final SeriesItem seriesItem = new SeriesItem.Builder(this.getResources().getColor(R.color.colorPrimaryDark))
                .setRange(0, 100,mParcialtotal)
                .build();

        decoview.deleteAll();
        decoview.addSeries(seriesItemInit);

        int backIndex = decoview.addSeries(seriesItem);
    }
    private void check(ReportStatistic data){
        if(mItem.equals("Todos") && mType.equals("Todos") && mBrand.equals("Todos")){

            this.mTotalSales = data.sum_sales;
            this.mTotalEntries = data.sum_entries;
            this.mTotalStock = data.sum_stock_product;

        }else if(!mItem.equals("Todos") && mType.equals("Todos") && mBrand.equals("Todos")){
            this.mItemTotalSales = data.sum_sales;
            this.mItemTotalEntries = data.sum_entries;
            this.mItemTotalStock = data.sum_stock_product;

            percentage_sale_item.setText(calculatePercentage(mTotalSales,mItemTotalSales));
            percentage_entries_item.setText(calculatePercentage(mTotalEntries,mItemTotalEntries));
            percentage_stock_item.setText(calculatePercentage(mTotalStock,mItemTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalEntries,mItemTotalEntries)),decoView_entries_precentege_item);
            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalSales,mItemTotalSales)),deco_item_sales);
            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalStock,mItemTotalStock)),deco_item_stock);


        }else if(!mItem.equals("Todos") && !mType.equals("Todos") && mBrand.equals("Todos")){
            this.mArtTotalSales = data.sum_sales;
            this.mArtTotalEntries = data.sum_entries;
            this.mArtTotalStock = data.sum_stock_product;

            percentage_sale_type.setText(calculatePercentage(mItemTotalSales,mArtTotalSales));
            percentage_entries_type.setText(calculatePercentage(mItemTotalEntries,mArtTotalEntries));
            percentage_stock_type.setText(calculatePercentage(mItemTotalStock,mArtTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mItemTotalSales,mArtTotalSales)),deco_type_sales);
            createSeriesInit(Integer.valueOf(calculatePercentage(mItemTotalEntries,mArtTotalEntries)),deco_type_entries);
            createSeriesInit(Integer.valueOf(calculatePercentage(mItemTotalStock,mArtTotalStock)),deco_type_stock);

        }else if(!mItem.equals("Todos") && !mType.equals("Todos") && !mBrand.equals("Todos")){
            this.mBrandTotalSales = data.sum_sales;
            this.mBrandTotalEntries = data.sum_entries;
            this.mBrandTotalStock = data.sum_stock_product;

            percentage_sale_brand.setText(calculatePercentage(mArtTotalSales,mBrandTotalSales));
            percentage_entries_brand.setText(calculatePercentage(mArtTotalEntries,mBrandTotalEntries));
            percentage_stock_brand.setText(calculatePercentage(mArtTotalStock,mBrandTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mArtTotalSales,mBrandTotalSales)),deco_brand_sales);
            createSeriesInit(Integer.valueOf(calculatePercentage(mArtTotalEntries,mBrandTotalEntries)),deco_brand_entries);
            createSeriesInit(Integer.valueOf(calculatePercentage(mArtTotalStock,mBrandTotalStock)),deco_brand_stock);
        }
    }

    private void loadStatisticValues(){
        ApiClient.get().getStatisticValues(mItem, mBrand, mType, mModel, mDateSine , mDateTo, generateString(mDetailAdapter.getSelectedDetails()), new GenericCallback<ReportStatistic>() {
            @Override
            public void onSuccess(ReportStatistic data) {

                check(data);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);
                String yourFormattedString = decimalFormat.format(data.sum_money_sales);

              //  mAmountSales.setText(yourFormattedString);
                mEntriesQuantity.setText(String.valueOf(data.sum_entries));
                mSalesQuantity.setText(String.valueOf(data.sum_sales));
              //  mMercOutcomes.setText(String.valueOf(data.sum_mercaderia_outcomes));
                mStockProduct.setText(String.valueOf(data.sum_stock_product));

                Integer d = data.sum_entries  - data.sum_sales;

               /* if(d < 0){
                    mDif.setTextColor(getResources().getColor(R.color.loa_red));
                }else{
                    mDif.setTextColor(getResources().getColor(R.color.loa_green));
                }

                mDif.setText(String.valueOf(data.sum_entries  - data.sum_sales));*/
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void changeCircleSelected(){

        line_statistics_item.setVisibility(View.VISIBLE);
        simpleProgressBar.setVisibility(View.VISIBLE);

        woman.setImageResource(R.drawable.bwomcl);
        boy.setImageResource(R.drawable.bnincl);
        man.setImageResource(R.drawable.bmancl);
        tecnico.setImageResource(R.drawable.btecl);
        zapas.setImageResource(R.drawable.bcalcl);
        accesories.setImageResource(R.drawable.bacccl);
        luz.setImageResource(R.drawable.bluzcl);
        oferta.setImageResource(R.drawable.bofercl);
        all.setImageResource(R.drawable.ballcl);

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();
        clearAndList();
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

                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                auto_brand.setVisibility(View.GONE);
                auto_type.setVisibility(View.GONE);

                mDetailAdapter.unSelectAll();
                loadEventsDetail();

                line_statistics_item.setVisibility(View.GONE);
                line_statistics_type.setVisibility(View.GONE);
                line_statistics_brand.setVisibility(View.GONE);
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

    private void clearView(){
        if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
        }
    }

    private void clearAndList(){
        mEmptyRecyclerView.setVisibility(View.GONE);
        clearView();
       // list2();duplicaaa

        cleanStatisticsValues();
        loadStatisticValues();
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
        ApiClient.get().getStatistics(mCurrentPage, mItem, mBrand, mType, mModel,mDateSine , mDateTo, generateString(mDetailAdapter.getSelectedDetails()),new GenericCallback<List<ReportStockEvent>>() {
            @Override
            public void onSuccess(List<ReportStockEvent> data) {

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

                simpleProgressBar.setVisibility(View.GONE);

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

    private void selectDate(final TextView t, final String select){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(this,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }
                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }
                        t.setText(sdayOfMonth+"-"+smonthOfYear+"-"+year);
                        if(select.equals("since")){
                            mDateSine = year+"-"+smonthOfYear+"-"+sdayOfMonth+" 00:00:00";
                        }else{
                            mDateTo = year+"-"+smonthOfYear+"-"+sdayOfMonth+" 00:00:00";
                        }

                        clearAndList();

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Todas", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(select.equals("since")){
                    mDateSine = "Todos";
                }else{
                    mDateTo = "Todos";
                }
                t.setText("");
                clearAndList();
                dialog.dismiss();
            }
        });
        datePickerDialog.show();
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

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private String generateString(List<String> list){
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
