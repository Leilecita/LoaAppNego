package com.example.android.loa.activities;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ReportItemFileClientAdapter;
import com.example.android.loa.adapters.ReportStockEventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.Response;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.ReportItemFileClientEvent;
import com.example.android.loa.network.models.ReportStockEvent;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SaleMovementsActivity extends BaseActivity implements Paginate.Callbacks{

    private RecyclerView mRecyclerView;
    private ReportStockEventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView mRecyclerViewItemFileClient;
    private ReportItemFileClientAdapter mAdapterItemFileClient;

    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private TextView mSelectDate;
    private TextView mAmountSales;
    private TextView mAmountSalesCard;


    private String mDate;
    private LinearLayout date_picker;

    private LinearLayout man;
    private LinearLayout woman;
    private LinearLayout boy;
    private LinearLayout accesories;
    private LinearLayout tecnico;
    private LinearLayout zapas;
    private LinearLayout luz;
    private LinearLayout oferta;

    private TextView textMan;
    private TextView textWoman;
    private TextView textBoy;
    private TextView textTec;
    private TextView textZap;
    private TextView textAcc;
    private TextView textLuz;
    private TextView textOferta;

    private String mItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyRecyclerView;

    @Override
    public int getLayoutRes() {
        return R.layout.event_history_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Ventas");

        mRecyclerView =  findViewById(R.id.list_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportStockEventAdapter(this, new ArrayList<ReportStockEvent>());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerViewItemFileClient =  findViewById(R.id.list_items_file_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewItemFileClient.setLayoutManager(layoutManager);
        mAdapterItemFileClient = new ReportItemFileClientAdapter(this, new ArrayList<ReportItemFileClientEvent>());
        mRecyclerViewItemFileClient.setAdapter(mAdapterItemFileClient);

        mSelectDate = findViewById(R.id.date_sale);
        date_picker = findViewById(R.id.date_picker);
        mAmountSales = findViewById(R.id.amount);
        mAmountSalesCard = findViewById(R.id.amount_card);

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        mDate=DateHelper.get().actualDateExtractions();
        mSelectDate.setText(DateHelper.get().getOnlyDate(mDate));

        loadAmountSales();
        loadAmountSalesCard();

        listItemsFileClientEvents();

        mItem="Todos";
        topBarListener();

        swipeRefreshLayout =  findViewById(R.id.swipeRefreshLayout);
        mEmptyRecyclerView=findViewById(R.id.empty);
      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void
            onRefresh() {
                mItem="Todos";
               changeCircleSelected();
            }
        });*/


        implementsPaginate();
    }

    private void changeCircleSelected(){

        woman.setBackgroundResource(R.drawable.circle_unselected);
        boy.setBackgroundResource(R.drawable.circle_unselected);
        man.setBackgroundResource(R.drawable.circle_unselected);
        tecnico.setBackgroundResource(R.drawable.circle_unselected);
        zapas.setBackgroundResource(R.drawable.circle_unselected);
        accesories.setBackgroundResource(R.drawable.circle_unselected);
        luz.setBackgroundResource(R.drawable.circle_unselected);
        oferta.setBackgroundResource(R.drawable.circle_unselected);

        changeViewStyleUnselected(textZap);
        changeViewStyleUnselected(textTec);
        changeViewStyleUnselected(textMan);
        changeViewStyleUnselected(textWoman);
        changeViewStyleUnselected(textBoy);
        changeViewStyleUnselected(textAcc);
        changeViewStyleUnselected(textLuz);
        changeViewStyleUnselected(textOferta);

        clearView();
        listReportStockEvents();

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

        textAcc=findViewById(R.id.textAcc);
        textMan=findViewById(R.id.textMan);
        textWoman=findViewById(R.id.textWoman);
        textZap=findViewById(R.id.textZapas);
        textTec=findViewById(R.id.textTec);
        textBoy=findViewById(R.id.textBoy);
        textLuz=findViewById(R.id.textLuz);
        textOferta=findViewById(R.id.textOferta);

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                changeCircleSelected();
                woman.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textWoman);
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                changeCircleSelected();
                man.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textMan);
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                changeCircleSelected();
                boy.setBackgroundColor(getResources().getColor(R.color.trasparente));
                boy.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textBoy);
            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorio";
                changeCircleSelected();
                accesories.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textAcc);
            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                changeCircleSelected();
                tecnico.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textTec);
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Calzado";
                changeCircleSelected();
                zapas.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textZap);
            }
        });

        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Luz";
                changeCircleSelected();
                luz.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textLuz);
            }
        });

        oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Oferta";
                changeCircleSelected();
                oferta.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textOferta);
            }
        });
    }

    private void changeViewStyle(TextView t){
        t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        t.setTextColor(getResources().getColor(R.color.word));
    }

    private void changeViewStyleUnselected(TextView t){
        t.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        t.setTextColor(getResources().getColor(R.color.word_clear));
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

    private void listItemsFileClientEvents(){

        String dateTo=DateHelper.get().getNextDay(mDate);

        ApiClient.get().getItemsFileClientEvent(mDate, dateTo, new GenericCallback<List<ReportItemFileClientEvent>>() {
            @Override
            public void onSuccess(List<ReportItemFileClientEvent> data) {
                mAdapterItemFileClient.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }
    private void listReportStockEvents(){
        loadingInProgress=true;

        String dateTo=DateHelper.get().getNextDay(mDate);

       /* if(mAdapter.getItemCount()==0){
            swipeRefreshLayout.setRefreshing(true);
        }*/

        ApiClient.get().getReportStockevents(mCurrentPage,mDate,dateTo, mItem,new GenericCallback<List<ReportStockEvent>>() {
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

               // swipeRefreshLayout.setRefreshing(false);

                if(mCurrentPage == 0 && data.size()==0){
                    mEmptyRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    mEmptyRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Error error) {

                loadingInProgress = false;
               // swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void clearAndList(){
        clearView();
        listReportStockEvents();
    }


    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }
    @Override
    public void onLoadMore() {
       listReportStockEvents();
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

    private void selectDate(){

        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(this,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }

                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }

                        String time= "10:00:00";

                        String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                        mDate=datePicker;
                        mSelectDate.setText(DateHelper.get().getOnlyDate(mDate));
                        loadAmountSales();
                        loadAmountSalesCard();
                        clearView();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
    private void loadAmountSales(){
        ApiClient.get().getAmountSalesByDay(mDate, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                mAmountSales.setText(String.valueOf(data.total));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void loadAmountSalesCard(){
        ApiClient.get().getAmountSalesByDayCard(mDate, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                mAmountSalesCard.setText(String.valueOf(data.total));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }


}
