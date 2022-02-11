package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ItemAdapter;
import com.example.android.loa.adapters.ItemAdapterModel;
import com.example.android.loa.adapters.ItemAdapterType;

import com.example.android.loa.adapters.ItemDetailAdapter;

import com.example.android.loa.adapters.ItemProductAdapter;
import com.example.android.loa.adapters.ItemStatisticAdapter;
import com.example.android.loa.adapters.StockStatisticsEventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;

import com.example.android.loa.network.models.ReportDetail;
import com.example.android.loa.network.models.ReportFilterStatistic;
import com.example.android.loa.network.models.ReportStatistic;
import com.example.android.loa.network.models.ReportStockEvent;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerModel;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.example.android.loa.network.models.StatisticVal;
import com.example.android.loa.types.Constants;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends BaseActivity implements Paginate.Callbacks, OnSelectedItem , OnSelectedProductItem {

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

    private Double mTotalAmountSale;
    private Double mItemTotalAmountSale;
    private Double mArtTotalAmountSale;
    private Double mBrandTotalAmountSale;

    private Integer mTotalStock;
    private Integer mItemTotalStock;
    private Integer mArtTotalStock;
    private Integer mBrandTotalStock;

    private TextView mItemDif;
    private TextView mArtDif;
    private TextView mBrandDif;

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

    private DecoView decoView_entries_precentege_item;
    private DecoView deco_item_sales;

    private DecoView deco_type_entries;
    private DecoView deco_type_sales;

    private DecoView deco_brand_entries;
    private DecoView deco_brand_sales;

    private TextView cant_entries_item;
    private TextView cant_entries_type;
    private TextView cant_entries_brand;

    private TextView cant_sale_item;
    private TextView cant_sale_type;
    private TextView cant_sale_brand;

    private TextView cant_stock_item;
    private TextView cant_stock_type;
    private TextView cant_stock_brand;

    private TextView selected_item;
    private TextView selected_art;
    private TextView selected_brand_art;
    private TextView selected_art_item;
    private TextView selected_item_loc;

    private CheckBox view_selection;
    private CheckBox view_not_selection;

    private TextView amount_item_sales;
    private TextView amount_brand_sales;
    private TextView amount_type_sales;

    private LinearLayout line_compare_brand;
    private RelativeLayout compare_ingr_brand;
    private RelativeLayout compare_stock_brand;
    private RelativeLayout compare_sales_brand;

    private RelativeLayout compare_ingr_type;
    private RelativeLayout compare_stock_type;
    private RelativeLayout compare_sales_type;

    private RecyclerView recycler_statistics_sales;
    private RecyclerView.LayoutManager layoutManagerStSales;
    private ItemStatisticAdapter mSalesStatisticsAdapter;

    private RecyclerView recycler_statistics_sales_amount;
    private RecyclerView.LayoutManager layoutManagerStSalesAmount;
    private ItemStatisticAdapter mSalesAmountatisticsAdapter;


    private RecyclerView recycler_statistics_entries;
    private RecyclerView.LayoutManager layoutManagerStEntr;
    private ItemStatisticAdapter mEntriesStatisticsAdapter;

    private RecyclerView recycler_statistics_stock;
    private RecyclerView.LayoutManager layoutManagerStStock;
    private ItemStatisticAdapter mStockStatisticsAdapter;

    private LinearLayout line_entries;
    private LinearLayout line_sales;
    private LinearLayout line_sales_amount;
    private LinearLayout line_stock;

    private String mCompareSelected;

    private TextView close_compare_entrie;
    private TextView close_compare_sales;
    private TextView close_compare_sales_amount;
    private TextView close_compare_stock;

    private ItemProductAdapter mGridProductAdapter;
    private RecyclerView mGridRecyclerViewItem;
    private RecyclerView.LayoutManager gridlayoutmanagerItem;

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

    private void cleanTextInfo(){
        selected_art.setText("");
        selected_art_item.setText("");
        selected_brand_art.setText("");
    }

    public void onSelectedItem(String brand, String type, String selection){

        simpleProgressBar.setVisibility(View.VISIBLE);

        if(selection.equals("brand")){

            if(!brand.equals("Todos")){
                mBrand = brand;
                brand_name.setText(brand);
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                line_statistics_brand.setVisibility(View.VISIBLE);
                selected_brand_art.setText(brand);

            }else{
                mBrand = "Todos";
                brand_name.setText("Marca");
                select_brand.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

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
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                line_statistics_type.setVisibility(View.VISIBLE);

              selected_art.setText(type);
              selected_art_item.setText(type);

            }else{
                mType = "Todos";
                art_name.setText("Articulo");
                select_art.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

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
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_unselected));

            }else{
                mModel = "Todos";
                model_name.setText("Modelo");
                select_model.setBackground(getResources().getDrawable(R.drawable.rec_selected));
            }
            mModelGridAdapter.clear();
        }

        if(selection.equals("detail")){
            if(brand.equals("Todos")){
                mDetailAdapter.unSelectAll();
            }
        }else{
            loadEventsDetail();
        }

        clearAndList();
    }

    private void loadStatisticsAdapter(ReportFilterStatistic data){
        mSalesStatisticsAdapter.setItems(data.list_sales);
        mSalesAmountatisticsAdapter.setItems(data.list_sales_amount);
        mEntriesStatisticsAdapter.setItems(data.list_entries);
        mStockStatisticsAdapter.setItems(data.list_stock);
    }

    private void statisticsByType(String limit){
        ApiClient.get().statisticsByType(mDateSine, mDateTo, limit,new GenericCallback<ReportFilterStatistic>() {
            @Override
            public void onSuccess(ReportFilterStatistic data) {
                loadStatisticsAdapter(data);
                mSalesStatisticsAdapter.setTotalVal(mTotalSales);
                mSalesStatisticsAdapter.setSelectedVal(mType);
                mEntriesStatisticsAdapter.setTotalVal(mTotalEntries);
                mEntriesStatisticsAdapter.setSelectedVal(mType);
                mStockStatisticsAdapter.setTotalVal(mTotalStock);
                mStockStatisticsAdapter.setSelectedVal(mType);
                mSalesAmountatisticsAdapter.setTotalVal(mTotalAmountSale.intValue());
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void clearFilterStatistics(){
        mSalesStatisticsAdapter.clear();
        mEntriesStatisticsAdapter.clear();
        mStockStatisticsAdapter.clear();
    }

    private void statisticsByBrand(String limit){
        ApiClient.get().statisticsByBrand(mDateSine, mDateTo, limit,new GenericCallback<ReportFilterStatistic>() {
            @Override
            public void onSuccess(ReportFilterStatistic data) {

                loadStatisticsAdapter(data);
                mSalesStatisticsAdapter.setTotalVal(mTotalSales);
                mSalesStatisticsAdapter.setSelectedVal(mBrand);
                mEntriesStatisticsAdapter.setTotalVal(mTotalEntries);
                mEntriesStatisticsAdapter.setSelectedVal(mBrand);
                mStockStatisticsAdapter.setTotalVal(mTotalStock);
                mStockStatisticsAdapter.setSelectedVal(mBrand);
                mSalesAmountatisticsAdapter.setTotalVal(mTotalAmountSale.intValue());
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void statisticsByItemType(String limit){
        ApiClient.get().statisticsByItemType(mDateSine, mDateTo, mItem,limit,new GenericCallback<ReportFilterStatistic>() {
            @Override
            public void onSuccess(ReportFilterStatistic data) {
                loadStatisticsAdapter(data);
                mSalesStatisticsAdapter.setTotalVal(mItemTotalStock);
                mSalesStatisticsAdapter.setSelectedVal(mType);
                mEntriesStatisticsAdapter.setTotalVal(mItemTotalEntries);
                mEntriesStatisticsAdapter.setSelectedVal(mType);
                mStockStatisticsAdapter.setTotalVal(mItemTotalStock);
                mStockStatisticsAdapter.setSelectedVal(mType);
                mSalesAmountatisticsAdapter.setTotalVal(mItemTotalAmountSale.intValue());
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void clearLines(){
        line_entries.setVisibility(View.GONE);
        line_sales.setVisibility(View.GONE);
        line_sales_amount.setVisibility(View.GONE);
        line_stock.setVisibility(View.GONE);
    }

    private void checkVisibility(LinearLayout lin){
        if(lin.getVisibility() == View.VISIBLE){
            lin.setVisibility(View.GONE);
        }else{
            lin.setVisibility(View.VISIBLE);
        }
    }

    private void showCuadCompare(final String type, String selection){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.select_filter, null);
        builder.setView(dialogView);

        final CheckBox top10 = dialogView.findViewById(R.id.top10);
        final CheckBox top20 = dialogView.findViewById(R.id.top20);

        final LinearLayout line_select= dialogView.findViewById(R.id.line_select);
        final TextView cant = dialogView.findViewById(R.id.cant);
        final TextView amount= dialogView.findViewById(R.id.amount);

        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final TextView selectionT= dialogView.findViewById(R.id.selection);
        final TextView buttonselection= dialogView.findViewById(R.id.button_selection);

        selectionT.setText(selection);
        top10.setChecked(true);
        buttonselection.setText("cant");

        top10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(top10.isChecked()){
                    top10.setChecked(true);
                    top20.setChecked(false);
                }
            }
        });

        top20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(top20.isChecked()){
                    top20.setChecked(true);
                    top10.setChecked(false);
                }
            }
        });


        if(type.equals("sales")) {
            line_select.setVisibility(View.VISIBLE);
        }

        cant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cant.setBackground(getResources().getDrawable(R.drawable.rec_selected_dialog));
                amount.setBackground(getResources().getDrawable(R.drawable.rec_unselected_dialog));
                buttonselection.setText("cant");
            }
        });

        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setBackground(getResources().getDrawable(R.drawable.rec_selected_dialog));
                cant.setBackground(getResources().getDrawable(R.drawable.rec_unselected_dialog));
                buttonselection.setText("amount");
            }
        });


        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearFilterStatistics();

                if(type.equals("ingr")){
                    if(top10.isChecked()){
                        compareIngr("10");
                    }else{
                        compareIngr("20");
                    }

                }else if(type.equals("stock")){
                    if(top10.isChecked()){
                        compareStock("10");
                    }else{
                        compareStock("20");
                    }

                }else{
                    if(buttonselection.getText().toString().trim().equals("cant")){
                        if(top10.isChecked()){
                            compareSales("10","cant");
                        }else{
                            compareSales("20","cant");
                        }
                    }else{

                        if(top10.isChecked()){
                            compareSales("10","amount");
                        }else{
                            compareSales("20","amount");
                        }


                    }
                }
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

    private void compareIngr(String limit){
        line_stock.setVisibility(View.GONE);
        line_sales.setVisibility(View.GONE);
        line_sales_amount.setVisibility(View.GONE);
        line_entries.setVisibility(View.VISIBLE);

        if(mCompareSelected.equals("type")){
            if(!mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByItemType(limit); //traer por item y articulo
            }else if(mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByType(limit);   //traer solo por articulo
            }
        }
        if(mCompareSelected.equals("brand")){
            if(mType.equals("Todos") && !mBrand.equals("Todos")){
                statisticsByBrand(limit);  //traer solo por marca
            }
        }
    }

    private void compareStock(String limit){
        line_entries.setVisibility(View.GONE);
        line_sales.setVisibility(View.GONE);
        line_sales_amount.setVisibility(View.GONE);
        line_stock.setVisibility(View.VISIBLE);

        if(mCompareSelected.equals("brand")){
            if(mType.equals("Todos") && !mBrand.equals("Todos")){
                statisticsByBrand(limit);  //traer solo por marca
            }
        }
        if(mCompareSelected.equals("type")){
            if(!mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByItemType(limit);//traer por item y articulo

            }else if(mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByType(limit);   //traer solo por articulo
            }
        }
    }

    private void compareSales(String limit, String amountOrCant){
        line_stock.setVisibility(View.GONE);
        line_entries.setVisibility(View.GONE);

        if(amountOrCant.equals("cant")){
            line_sales.setVisibility(View.VISIBLE);
            line_sales_amount.setVisibility(View.GONE);
        }else{
            line_sales_amount.setVisibility(View.VISIBLE);
            line_sales.setVisibility(View.GONE);
        }

        if(mCompareSelected.equals("type")){
            if(!mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByItemType(limit);//traer por item y articulo
            }else if(mItem.equals("Todos") && !mType.equals("Todos")){
                statisticsByType(limit);   //traer solo por articulo
            }
        }
        if(mCompareSelected.equals("brand")){
            if(mType.equals("Todos") && !mBrand.equals("Todos")){
                statisticsByBrand(limit); //traer ventas solo por marca
            }
        }
    }

    private void clearCompareBackground(){
        compare_ingr_brand.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));
        compare_sales_brand.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));
        compare_stock_brand.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));

        compare_ingr_type.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));
        compare_sales_type.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));
        compare_stock_type.setBackground(getResources().getDrawable(R.drawable.circle_statist_more));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // showBackArrow();
        mGridRecyclerViewItem = findViewById(R.id.list_items);
        gridlayoutmanagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerViewItem.setLayoutManager(gridlayoutmanagerItem);
        mGridProductAdapter = new ItemProductAdapter(this, new ArrayList<SpinnerItem>());
        mGridRecyclerViewItem.setAdapter(mGridProductAdapter);
        mGridProductAdapter.setOnSelectedProductItem(this);

        listItems();

        close_compare_entrie = findViewById(R.id.close_compare_entries);
        close_compare_sales = findViewById(R.id.close_compare_sales);
        close_compare_sales_amount = findViewById(R.id.close_compare_sales_amount);
        close_compare_stock = findViewById(R.id.close_compare_stock);

        close_compare_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_stock.setVisibility(View.GONE);
            }
        });

        close_compare_entrie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_entries.setVisibility(View.GONE);
            }
        });

        close_compare_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_sales.setVisibility(View.GONE);
            }
        });

        close_compare_sales_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line_sales_amount.setVisibility(View.GONE);
            }
        });

        line_entries = findViewById(R.id.line_entries);
        line_sales = findViewById(R.id.line_sales);
        line_sales_amount = findViewById(R.id.line_sales_amount);
        line_stock = findViewById(R.id.line_stock);

        line_compare_brand = findViewById(R.id.line_compare_brand);
        compare_ingr_brand = findViewById(R.id.compare_ingr_brand);
        compare_sales_brand = findViewById(R.id.compare_sales_brand);
        compare_stock_brand = findViewById(R.id.compare_stock_brand);

        compare_ingr_type = findViewById(R.id.compare_ingr_type);
        compare_sales_type = findViewById(R.id.compare_sales_type);
        compare_stock_type = findViewById(R.id.compare_stock_type);

        recycler_statistics_entries = findViewById(R.id.recycler_entries);
        layoutManagerStEntr = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_statistics_entries.setLayoutManager(layoutManagerStEntr);
        mEntriesStatisticsAdapter = new ItemStatisticAdapter(this,new ArrayList<StatisticVal>());
        recycler_statistics_entries.setAdapter(mEntriesStatisticsAdapter);

        recycler_statistics_sales = findViewById(R.id.recycler_sales);
        layoutManagerStSales = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_statistics_sales.setLayoutManager(layoutManagerStSales);
        mSalesStatisticsAdapter = new ItemStatisticAdapter(this,new ArrayList<StatisticVal>());
        recycler_statistics_sales.setAdapter(mSalesStatisticsAdapter);

        recycler_statistics_sales_amount = findViewById(R.id.recycler_sales_amount);
        layoutManagerStSalesAmount = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_statistics_sales_amount.setLayoutManager(layoutManagerStSalesAmount);
        mSalesAmountatisticsAdapter = new ItemStatisticAdapter(this,new ArrayList<StatisticVal>());
        recycler_statistics_sales_amount.setAdapter(mSalesAmountatisticsAdapter);

        recycler_statistics_stock = findViewById(R.id.recycler_stock);
        layoutManagerStStock = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_statistics_stock.setLayoutManager(layoutManagerStStock);
        mStockStatisticsAdapter = new ItemStatisticAdapter(this,new ArrayList<StatisticVal>());
        recycler_statistics_stock.setAdapter(mStockStatisticsAdapter);

        compare_ingr_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_ingr_type.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "type";
                showCuadCompare("ingr","compras por articulos");
            }
        });

        compare_ingr_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_ingr_brand.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "brand";
                showCuadCompare("ingr","compras por marcas");
            }
        });
        compare_stock_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_stock_brand.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "brand";
                showCuadCompare("stock","stock por marcas");
            }
        });

        compare_stock_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_stock_type.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "type";
                showCuadCompare("stock","stock por articulos");
            }
        });

        compare_sales_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_sales_brand.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "brand";
                showCuadCompare("sales","ventas por marcas");
            }
        });

        compare_sales_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompareBackground();
                compare_sales_type.setBackground(getResources().getDrawable(R.drawable.circ_statistic_more_select));
                mCompareSelected = "type";
                showCuadCompare("sales","ventas por articulos");
            }
        });


        decoView_entries_precentege_item = findViewById(R.id.dynamicArcView);
        deco_item_sales =  findViewById(R.id.deco_item_vtas);

        deco_type_entries = findViewById(R.id.deco_type_entries);
        deco_type_sales =  findViewById(R.id.deco_type_sales);

        deco_brand_entries =  findViewById(R.id.deco_brand_entries);
        deco_brand_sales =  findViewById(R.id.deco_brand_vtas);

        line_statistics_item = findViewById(R.id.line_statistics_item);
        line_statistics_type = findViewById(R.id.line_statistics_type);
        line_statistics_brand = findViewById(R.id.line_statistics_brand);

        percentage_sale_item = findViewById(R.id.percentage_sale_item);
        percentage_sale_type = findViewById(R.id.percentage_sale_type);
        percentage_sale_brand = findViewById(R.id.percentage_sale_brand);

        percentage_entries_item = findViewById(R.id.percentage_entries_item);
        percentage_entries_type = findViewById(R.id.percentage_entries_type);
        percentage_entries_brand = findViewById(R.id.percentage_entries_brand);

        percentage_stock_item = findViewById(R.id.percentage_stock_item);
        percentage_stock_type = findViewById(R.id.percentage_stock_type);
        percentage_stock_brand = findViewById(R.id.percentage_stock_brand);

        cant_entries_item = findViewById(R.id.cant_item_entries);
        cant_sale_item = findViewById(R.id.cant_item_sales);
        cant_stock_item = findViewById(R.id.cant_item_stock);

        cant_entries_type = findViewById(R.id.cant_type_ingr);
        cant_sale_type = findViewById(R.id.cant_type_vtas);
        cant_stock_type = findViewById(R.id.cant_type_stock);

        cant_entries_brand = findViewById(R.id.cant_brand_ingr);
        cant_sale_brand= findViewById(R.id.cant_brand_vtas);
        cant_stock_brand = findViewById(R.id.cant_brand_stock);

        selected_item = findViewById(R.id.selected_item);
        selected_art = findViewById(R.id.selected_art);
        selected_art_item = findViewById(R.id.selected_art_item);
        selected_brand_art = findViewById(R.id.selected_brand_art);
        selected_item_loc = findViewById(R.id.selected_item_loc);

        amount_brand_sales = findViewById(R.id.amount_brand_vtas);
        amount_item_sales = findViewById(R.id.amount_item_vtas);
        amount_type_sales = findViewById(R.id.amount_type_vtas);

        mItemDif = findViewById(R.id.item_dif);
        mArtDif = findViewById(R.id.type_dif);
        mBrandDif = findViewById(R.id.brand_dif);

       /* view_eye = findViewById(R.id.view_eye);
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
        });*/

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

        mDateSine = "2019-09-01 00:00:00";
        mViewDateSince.setText("01-09-2020");

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
                                    select_brand.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                                    mGridAdapter.clear();
                                    auto_brand.setVisibility(View.GONE);

                                    line_statistics_brand.setVisibility(View.VISIBLE);
                                    selected_brand_art.setText(selected);

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
                                    select_art.setBackground(getResources().getDrawable(R.drawable.rec_selected));

                                    mTypeGridAdapter.clear();
                                    auto_type.setVisibility(View.GONE);

                                    line_statistics_type.setVisibility(View.VISIBLE);

                                    selected_art.setText(selected);
                                    selected_art_item.setText(selected);

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

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        mEmptyRecyclerView=findViewById(R.id.empty);

       // topBarListener();

        bottomSheet = findViewById(R.id.bottomSheet);
        topBarListenerBottomShet(bottomSheet);

        loadStatisticValues();

        loadEventsDetail();

        implementsPaginate();
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

        view_not_selection = bottomShet.findViewById(R.id.view_not_selection);
        view_selection = bottomShet.findViewById(R.id.view_selection);

        view_not_selection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDetailAdapter.unSelectAll();
                if(view_not_selection.isChecked()) {
                    view_not_selection.setChecked(true);
                    view_selection.setChecked(false);
                    mDetailAdapter.setTypeViewToSee(false);
                }else{
                    view_not_selection.setChecked(false);
                    view_selection.setChecked(true);
                    mDetailAdapter.setTypeViewToSee(true);
                }

                clearAndList();
            }
        });

        view_selection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDetailAdapter.unSelectAll();
                if(view_selection.isChecked()) {
                    view_selection.setChecked(true);
                    mDetailAdapter.setTypeViewToSee(true);
                    view_not_selection.setChecked(false);
                }else{
                    view_selection.setChecked(false);
                    mDetailAdapter.setTypeViewToSee(false);
                    view_not_selection.setChecked(true);
                }

                clearAndList();
            }
        });
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

    private void createSeriesInit( Integer mParcialtotal, DecoView decoview, int color, int selected_color){

        final SeriesItem seriesItemInit = new SeriesItem.Builder(this.getResources().getColor(color))
                .setRange(0, 100, 100)
                .build();

        final SeriesItem seriesItem = new SeriesItem.Builder(this.getResources().getColor(selected_color))
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

            this.mTotalAmountSale = data.sum_money_sales;

        }else if(!mItem.equals("Todos") && mType.equals("Todos") && mBrand.equals("Todos")){
            this.mItemTotalSales = data.sum_sales;
            this.mItemTotalEntries = data.sum_entries;
            this.mItemTotalStock = data.sum_stock_product;

            this.mItemTotalAmountSale = data.sum_money_sales;

            mItemDif.setText(String.valueOf(mItemTotalEntries - mItemTotalSales));

            amount_item_sales.setText("$"+getFormattedNumber(data.sum_money_sales));

            percentage_sale_item.setText(calculatePercentage(mTotalSales,mItemTotalSales));
            percentage_entries_item.setText(calculatePercentage(mTotalEntries,mItemTotalEntries));
            percentage_stock_item.setText(calculatePercentage(mTotalStock,mItemTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalEntries,mItemTotalEntries)),decoView_entries_precentege_item,R.color.violet3,R.color.green3);
            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalSales,mItemTotalSales)),deco_item_sales, R.color.violet3,R.color.green3);

            cant_sale_item.setText(String.valueOf(data.sum_sales));
            cant_entries_item.setText(String.valueOf(data.sum_entries));
            cant_stock_item.setText(String.valueOf(data.sum_stock_product));

        }else if(!mItem.equals("Todos") && !mType.equals("Todos") && mBrand.equals("Todos")){
            this.mArtTotalSales = data.sum_sales;
            this.mArtTotalEntries = data.sum_entries;
            this.mArtTotalStock = data.sum_stock_product;

            this.mArtTotalAmountSale = data.sum_money_sales;

            mArtDif.setText(String.valueOf(mArtTotalEntries - mArtTotalSales));

            amount_type_sales.setText("$ "+getFormattedNumber(data.sum_money_sales));

            percentage_sale_type.setText(calculatePercentage(mItemTotalSales,mArtTotalSales));
            percentage_entries_type.setText(calculatePercentage(mItemTotalEntries,mArtTotalEntries));
            percentage_stock_type.setText(calculatePercentage(mItemTotalStock,mArtTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mItemTotalSales,mArtTotalSales)),deco_type_sales,R.color.violet3,R.color.green3);
            createSeriesInit(Integer.valueOf(calculatePercentage(mItemTotalEntries,mArtTotalEntries)),deco_type_entries, R.color.violet3,R.color.green3);

            cant_sale_type.setText(String.valueOf(data.sum_sales));
            cant_entries_type.setText(String.valueOf(data.sum_entries));
            cant_stock_type.setText(String.valueOf(data.sum_stock_product));

        }else if(!mItem.equals("Todos") && !mType.equals("Todos") && !mBrand.equals("Todos")){
            this.mBrandTotalSales = data.sum_sales;
            this.mBrandTotalEntries = data.sum_entries;
            this.mBrandTotalStock = data.sum_stock_product;

            this.mBrandTotalAmountSale = data.sum_money_sales;

            mBrandDif.setText(String.valueOf(mBrandTotalEntries - mBrandTotalSales));

            amount_brand_sales.setText("$"+getFormattedNumber(data.sum_money_sales));

            percentage_sale_brand.setText(calculatePercentage(mArtTotalSales,mBrandTotalSales));
            percentage_entries_brand.setText(calculatePercentage(mArtTotalEntries,mBrandTotalEntries));
            percentage_stock_brand.setText(calculatePercentage(mArtTotalStock,mBrandTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mArtTotalSales,mBrandTotalSales)),deco_brand_sales,R.color.violet3,R.color.green3);
            createSeriesInit(Integer.valueOf(calculatePercentage(mArtTotalEntries,mBrandTotalEntries)),deco_brand_entries, R.color.violet3,R.color.green3);

            cant_sale_brand.setText(String.valueOf(data.sum_sales));
            cant_entries_brand.setText(String.valueOf(data.sum_entries));
            cant_stock_brand.setText(String.valueOf(data.sum_stock_product));
        }else if(mItem.equals("Todos") && !mType.equals("Todos") && mBrand.equals("Todos")){

            this.mArtTotalSales = data.sum_sales;
            this.mArtTotalEntries = data.sum_entries;
            this.mArtTotalStock = data.sum_stock_product;

            this.mArtTotalAmountSale = data.sum_money_sales;

            mArtDif.setText(String.valueOf(mArtTotalEntries - mArtTotalSales));

            amount_type_sales.setText("$ "+getFormattedNumber(data.sum_money_sales));

            percentage_sale_type.setText(calculatePercentage(mTotalSales,mArtTotalSales));
            percentage_entries_type.setText(calculatePercentage(mTotalEntries,mArtTotalEntries));
            percentage_stock_type.setText(calculatePercentage(mTotalStock,mArtTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalSales,mArtTotalSales)),deco_type_sales,R.color.violet3,R.color.green3);
            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalEntries,mArtTotalEntries)),deco_type_entries, R.color.violet3,R.color.green3);

            cant_sale_type.setText(String.valueOf(data.sum_sales));
            cant_entries_type.setText(String.valueOf(data.sum_entries));
            cant_stock_type.setText(String.valueOf(data.sum_stock_product));

        }else if(mItem.equals("Todos") && mType.equals("Todos") && !mBrand.equals("Todos")){
            this.mBrandTotalSales = data.sum_sales;
            this.mBrandTotalEntries = data.sum_entries;
            this.mBrandTotalStock = data.sum_stock_product;

            this.mBrandTotalAmountSale = data.sum_money_sales;

            mBrandDif.setText(String.valueOf(mBrandTotalEntries - mBrandTotalSales));

            amount_brand_sales.setText("$"+String.valueOf(data.sum_money_sales));

            percentage_sale_brand.setText(calculatePercentage( mTotalSales,mBrandTotalSales));
            percentage_entries_brand.setText(calculatePercentage(mTotalEntries,mBrandTotalEntries));
            percentage_stock_brand.setText(calculatePercentage(mTotalStock,mBrandTotalStock));

            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalSales,mBrandTotalSales)),deco_brand_sales,R.color.violet3,R.color.green3);
            createSeriesInit(Integer.valueOf(calculatePercentage(mTotalEntries,mBrandTotalEntries)),deco_brand_entries, R.color.violet3,R.color.green3);

            cant_sale_brand.setText(String.valueOf(data.sum_sales));
            cant_entries_brand.setText(String.valueOf(data.sum_entries));
            cant_stock_brand.setText(String.valueOf(data.sum_stock_product));

            line_compare_brand.setVisibility(View.VISIBLE);
        }
    }


    private String getFormattedNumber(Double number){

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(number);
    }
    private void loadStatisticValues(){
        ApiClient.get().getStatisticValues(mItem, mBrand, mType, mModel, mDateSine , mDateTo, generateString(mDetailAdapter.getSelectedDetailsNotToSee()),generateString(mDetailAdapter.getSelectedDetailsToSee()) ,new GenericCallback<ReportStatistic>() {
            @Override
            public void onSuccess(ReportStatistic data) {

                check(data);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                decimalFormat.setGroupingUsed(true);
                decimalFormat.setGroupingSize(3);
                String yourFormattedString = decimalFormat.format(data.sum_money_sales);

              //  mEntriesQuantity.setText(String.valueOf(data.sum_entries));
               // mSalesQuantity.setText(String.valueOf(data.sum_sales));
               // mStockProduct.setText(String.valueOf(data.sum_stock_product));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }



    public void onSelectedProductItem(String item){
        line_statistics_type.setVisibility(View.GONE);
        line_statistics_brand.setVisibility(View.GONE);
        line_statistics_item.setVisibility(View.VISIBLE);

        simpleProgressBar.setVisibility(View.VISIBLE);

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


            mDetailAdapter.unSelectAll();

            line_statistics_item.setVisibility(View.GONE);
            line_statistics_type.setVisibility(View.GONE);
            line_statistics_brand.setVisibility(View.GONE);

            line_compare_brand.setVisibility(View.GONE);


        }

        mGridAdapter.clear();
        mTypeGridAdapter.clear();

        cleanInfo();
        cleanTextInfo();
        clearAndList();

        selected_item.setText(mItem);
        selected_item_loc.setText(mItem);

        clearCompareBackground();

        clearLines();
    }

/*
    private void changeCircleSelected(){
        line_statistics_type.setVisibility(View.GONE);
        line_statistics_brand.setVisibility(View.GONE);

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
        cleanTextInfo();
        clearAndList();

        selected_item.setText(mItem);
        selected_item_loc.setText(mItem);

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

                mDetailAdapter.unSelectAll();

                line_statistics_item.setVisibility(View.GONE);
                line_statistics_type.setVisibility(View.GONE);
                line_statistics_brand.setVisibility(View.GONE);

                line_compare_brand.setVisibility(View.GONE);

                clearLines();
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
        ApiClient.get().getStatistics(mCurrentPage, mItem, mBrand, mType, mModel,mDateSine , mDateTo, generateString(mDetailAdapter.getSelectedDetailsNotToSee()),generateString(mDetailAdapter.getSelectedDetailsToSee()),new GenericCallback<List<ReportStockEvent>>() {
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

    private List<String> createArray(){
        List<String> listN=new ArrayList<>();
        listN.add("10");
        listN.add("20");
        return listN;
    }

}
