package com.example.android.loa.activities.todelete;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.Interfaces.OnAmountSaleChange;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.activities.ProductsActivity;
import com.example.android.loa.adapters.IncomesAdapter;
import com.example.android.loa.adapters.ReportItemFileClientAdapter;
import com.example.android.loa.adapters.sales.ReportStockEventAdapter;
import com.example.android.loa.fragments.BottomSheetFragment;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Income;
import com.example.android.loa.network.models.ReportItemFileClientEvent;
import com.example.android.loa.network.models.ReportStockEvent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SaleMovementsActivity extends BaseActivity implements Paginate.Callbacks, BottomSheetFragment.BottomSheetListener, OnAmountSaleChange {

    private RecyclerView mRecyclerView;
    private ReportStockEventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private RecyclerView mRecyclerViewItemFileClient;
    private ReportItemFileClientAdapter mAdapterItemFileClient;
    private RecyclerView.LayoutManager layoutManagerFile;

    private RecyclerView mRecyclerViewIncomes;
    private IncomesAdapter mAdapterIncomes;
    private RecyclerView.LayoutManager layoutManagerIncome;

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
    private LinearLayout all;

    private LinearLayout income;

    private TextView textMan;
    private TextView textWoman;
    private TextView textBoy;
    private TextView textTec;
    private TextView textZap;
    private TextView textAcc;
    private TextView textLuz;
    private TextView textOferta;
    private TextView textAll;

    private TextView filesView;
    private TextView incomesView;

    private String mItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyRecyclerView;
    private LinearLayout line_files;
    private LinearLayout lin_incomes;
    private TextView line_ventas;

    private LinearLayout bottomSheet;
    private FloatingActionButton filter;


    @Override
    public void onButtonClicked(String txt){

        Toast.makeText(this,"holaaa "+txt,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAmountSalesChange(){
        loadValuesAmount();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.event_history_activity;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Ventas");

        line_files=findViewById(R.id.lin_files);
        line_ventas=findViewById(R.id.line_ventas);
        lin_incomes=findViewById(R.id.lin_incomes);
        filesView=findViewById(R.id.filesview);
        incomesView=findViewById(R.id.incomesview);
        filesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerViewItemFileClient.getVisibility() == View.VISIBLE){
                    mRecyclerViewItemFileClient.setVisibility(View.GONE);
                }else{
                    mRecyclerViewItemFileClient.setVisibility(View.VISIBLE);
                }
            }
        });

        incomesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerViewIncomes.getVisibility() == View.VISIBLE){
                    mRecyclerViewIncomes.setVisibility(View.GONE);

                }else{
                    mRecyclerViewIncomes.setVisibility(View.VISIBLE);
                }
            }
        });

        mRecyclerView =  findViewById(R.id.list_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportStockEventAdapter(this, new ArrayList<ReportStockEvent>());
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerViewItemFileClient =  findViewById(R.id.list_items_file_events);
        layoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewItemFileClient.setLayoutManager(layoutManagerFile);
        mAdapterItemFileClient = new ReportItemFileClientAdapter(this, new ArrayList<ReportItemFileClientEvent>(),"");
        mRecyclerViewItemFileClient.setAdapter(mAdapterItemFileClient);

        mRecyclerViewIncomes =  findViewById(R.id.list_incomes);
        layoutManagerIncome = new LinearLayoutManager(this);
        mRecyclerViewIncomes.setLayoutManager(layoutManagerIncome);
        mAdapterIncomes = new IncomesAdapter(this, new ArrayList<Income>());
        mRecyclerViewIncomes.setAdapter(mAdapterIncomes);

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

        loadValuesAmount();


        mItem="Todos";
        //topBarListener();

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

        listItemsFileClientEvents();
        listIncomes();

        bottomSheet = (LinearLayout)findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);


      /*  filter=findViewById(R.id.button_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomShet=new BottomSheetFragment();
                bottomShet.show(getSupportFragmentManager(),"hola");
            }
        });
*/

        bts(bsb);


    }

    private void loadValuesAmount(){
        loadAmountSales();
        loadAmountSalesCard();
    }

    private void bts(BottomSheetBehavior bsb){
        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                topBarListener(bottomSheet);
                String nuevoEstado = "";

                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;
                }
                Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }
        });
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
        all.setBackgroundResource(R.drawable.circle_unselected);

        changeViewStyleUnselected(textZap);
        changeViewStyleUnselected(textTec);
        changeViewStyleUnselected(textMan);
        changeViewStyleUnselected(textWoman);
        changeViewStyleUnselected(textBoy);
        changeViewStyleUnselected(textAcc);
        changeViewStyleUnselected(textLuz);
        changeViewStyleUnselected(textOferta);
        changeViewStyleUnselected(textAll);

        clearView();
        listReportStockEvents();

    }
    private void topBarListener(View bottomSheet){
        man=bottomSheet.findViewById(R.id.man);
        woman=bottomSheet.findViewById(R.id.woman);
        boy=bottomSheet.findViewById(R.id.boy);
        tecnico=bottomSheet.findViewById(R.id.tecnico);
        zapas=bottomSheet.findViewById(R.id.zapas);
        accesories=bottomSheet.findViewById(R.id.acces);
        luz=bottomSheet.findViewById(R.id.luz);
        oferta=bottomSheet.findViewById(R.id.oferta);
        all=bottomSheet.findViewById(R.id.all);

        income=bottomSheet.findViewById(R.id.new_income);

        textAcc=bottomSheet.findViewById(R.id.textAcc);
        textMan=bottomSheet.findViewById(R.id.textMan);
        textWoman=bottomSheet.findViewById(R.id.textWoman);
        textZap=bottomSheet.findViewById(R.id.textZapas);
        textTec=bottomSheet.findViewById(R.id.textTec);
        textBoy=bottomSheet.findViewById(R.id.textBoy);
        textLuz=bottomSheet.findViewById(R.id.textLuz);
        textOferta=bottomSheet.findViewById(R.id.textOferta);
        textAll=bottomSheet.findViewById(R.id.textAll);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncomeMoney();
            }
        });


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Todos";
                changeCircleSelected();
                all.setBackgroundResource(R.drawable.circle);
                changeViewStyle(textAll);
            }
        });

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

    private void listIncomes(){

        String dateTo=DateHelper.get().getNextDay(mDate);
        /*ApiClient.get().getIncomesByDate(mDate, dateTo, new GenericCallback<List<Income>>() {
            @Override
            public void onSuccess(List<Income> data) {
                if(data.size() >0){
                    lin_incomes.setVisibility(View.VISIBLE);
                    line_ventas.setVisibility(View.VISIBLE);
                    mAdapterIncomes.setItems(data);
                }
            }

            @Override
            public void onError(Error error) {

            }
        });*/
    }
    private void listItemsFileClientEvents(){

        String dateTo=DateHelper.get().getNextDay(mDate);

        ApiClient.get().getItemsFileClientEvent(mDate, dateTo, new GenericCallback<List<ReportItemFileClientEvent>>() {
            @Override
            public void onSuccess(List<ReportItemFileClientEvent> data) {
                if(data.size() >0){
                    line_files.setVisibility(View.VISIBLE);
                    line_ventas.setVisibility(View.VISIBLE);

                    mAdapterItemFileClient.setItems(data);
                }
            }

            @Override
            public void onError(Error error) {

            }
        });
    }
    private void listReportStockEvents(){
        loadingInProgress=true;

        String dateTo=DateHelper.get().getNextDay(mDate);

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

                        clearView();
                        clearIncomes();
                        clearItemsFileClient();

                        loadValuesAmount();

                        listReportStockEvents();
                        listIncomes();
                        listItemsFileClientEvents();
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

    private void addIncomeMoney(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_income, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final TextView date_picker=  dialogView.findViewById(R.id.date_picker);
        final CheckBox check_card=  dialogView.findViewById(R.id.check_card);
        final CheckBox check_deb=  dialogView.findViewById(R.id.check_deb);
        final CheckBox check_ef=  dialogView.findViewById(R.id.check_ef);

        final CheckBox retire_product=  dialogView.findViewById(R.id.out_product);
        final CheckBox not_retire=  dialogView.findViewById(R.id.not_out);

        final TextView name=  dialogView.findViewById(R.id.name);
        final TextView phone=  dialogView.findViewById(R.id.phone);
        final TextView address=  dialogView.findViewById(R.id.adress);
        final TextView value_product=  dialogView.findViewById(R.id.value_product);

        date.setText(DateHelper.get().getActualDate());

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getBaseContext(),R.style.datepicker,
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

                                String time=DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                                String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time ;
                                date.setText(datePicker);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        retire_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(retire_product.isChecked()){
                    retire_product.setChecked(true);
                    not_retire.setChecked(false);
                }else{
                    retire_product.setChecked(false);
                }
            }
        });

        not_retire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(not_retire.isChecked()){
                    not_retire.setChecked(true);
                    retire_product.setChecked(false);
                }else{
                    not_retire.setChecked(false);
                }
            }
        });

        check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_ef.isChecked()){
                   check_ef.setChecked(true);
                   check_card.setChecked(false);
                   check_deb.setChecked(false);
                }else{
                    check_ef.setChecked(false);
                }
            }
        });

        check_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_card.isChecked()){
                    check_card.setChecked(true);
                    check_ef.setChecked(false);
                    check_deb.setChecked(false);
                }else{
                    check_card.setChecked(false);
                }
            }
        });

        check_deb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_deb.isChecked()){
                    check_deb.setChecked(true);
                    check_ef.setChecked(false);
                    check_card.setChecked(false);
                }else{
                    check_deb.setChecked(false);
                }
            }
        });

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(retire_product.isChecked() || not_retire.isChecked()){
                    String descriptionT=description.getText().toString().trim();
                    Double valueT=0.0;
                    if(!value.getText().toString().trim().matches("")){
                        valueT=Double.valueOf(value.getText().toString().trim());
                    }

                    String payment_method="efectivo";
                    String retired_product="false";

                    if(check_deb.isChecked()){
                        payment_method= "debito";
                    }else if(check_card.isChecked()){
                        payment_method= "tarjeta";
                    }else if(check_ef.isChecked()){
                        payment_method= "efectivo";
                    }

                    if(not_retire.isChecked()){
                        retired_product="false";
                    }else if(retire_product.isChecked()){
                        retired_product="true";
                    }

                    String nameT=name.getText().toString().trim();
                    String addressT=address.getText().toString().trim();
                    String phoneT=phone.getText().toString().trim();
                    Double valueP=Double.valueOf(value_product.getText().toString());

                    Income inc=new Income(descriptionT,"",valueT,payment_method,retired_product,nameT,addressT,phoneT,valueP);
                    inc.created= DateHelper.get().changeFormatDateUserToServer(date.getText().toString().trim());

                    ApiClient.get().postIncome(inc, new GenericCallback<Income>() {
                        @Override
                        public void onSuccess(Income data) {
                            clearIncomes();
                            loadValuesAmount();
                            if(data.retired_product.equals("true")){
                                startActivity(new Intent(getBaseContext(), ProductsActivity.class));
                            }

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                    dialog.dismiss();

                }else{
                    Toast.makeText(getBaseContext(),"Debe seleccionar si retira o no el producto", Toast.LENGTH_LONG).show();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void clearIncomes(){
        mAdapterIncomes.clear();
        listIncomes();
    }

    private void clearItemsFileClient(){
        mAdapterItemFileClient.clear();
        listItemsFileClientEvents();
    }

}
