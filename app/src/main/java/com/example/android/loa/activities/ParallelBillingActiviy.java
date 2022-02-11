package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnRefreshList;
import com.example.android.loa.Interfaces.OnSelectedFilter;
import com.example.android.loa.R;
import com.example.android.loa.adapters.FilterAdapter;
import com.example.android.loa.adapters.ItemAdapterType;
import com.example.android.loa.adapters.ItemProductAdapter;
import com.example.android.loa.adapters.ParallelBillingAdapter;
import com.example.android.loa.adapters.ReportParallelBillingAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.FilterType;
import com.example.android.loa.network.models.ParallelBilling;
import com.example.android.loa.network.models.ReportParallelBilling;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.types.BillingType;
import com.example.android.loa.types.Constants;
import com.example.android.loa.types.MoneyMovementPaymentType;
import com.example.android.loa.types.MoneyMovementType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ParallelBillingActiviy extends BaseActivity implements Paginate.Callbacks, OnRefreshList, OnSelectedFilter {

    private RecyclerView mRecyclerView;
    private ReportParallelBillingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    private LinearLayout home;
    private LinearLayout add;

    private LinearLayout bottomSheet;

    private FilterAdapter mGridFilterAdapter;
    private RecyclerView mGridRecyclerViewItem;
    private RecyclerView.LayoutManager gridlayoutmanagerItem;

    private String selectedType;

    public void onRefreshList(){
        clearView();
    }


    public void onSelectedFilter(String filter){

        selectedType = filter;
        clearView();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_parallel_billing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // showBackArrow();

        //setTitle("Movimientos paralelos Santi");

        home = findViewById(R.id.line_home);
        add = findViewById(R.id.fab_agregarTod);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView =  findViewById(R.id.list_report_moeny_movements);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ReportParallelBillingAdapter(this,new ArrayList<ReportParallelBilling>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnRefreshlistListener(this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBill();
            }
        });

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });


        bottomSheet = this.findViewById(R.id.bottomSheet);

        topBarListener(bottomSheet);

        implementsPaginate();
    }


    private void topBarListener(View bottomSheet) {

        mGridRecyclerViewItem = bottomSheet.findViewById(R.id.list_items);
        gridlayoutmanagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridRecyclerViewItem.setLayoutManager(gridlayoutmanagerItem);
        mGridFilterAdapter = new FilterAdapter(this, new ArrayList<FilterType>());
        mGridRecyclerViewItem.setAdapter(mGridFilterAdapter);
        mGridFilterAdapter.setOnselectedFilter(this);

        listFilters();

    }

    private void listFilters(){

        ApiClient.get().getTypesBilling(new GenericCallback<List<FilterType>>() {
            @Override
            public void onSuccess(List<FilterType> data) {
                FilterType s=new FilterType(MoneyMovementType.ALL.getName());
                data.add(0,s);

                mGridFilterAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }


    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
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

    public void listMovements(){

        loadingInProgress=true;

        ApiClient.get().getReportParalelBillings(mCurrentPage, selectedType, new GenericCallback<List<ReportParallelBilling>>() {
            @Override
            public void onSuccess(List<ReportParallelBilling> data) {
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
        listMovements();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    private static <T extends Enum<BillingType>> void enumNameToStringArray(BillingType[] values, List<String> spinner_type) {
        for (BillingType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_type.add("Tipo");
            }else{
                spinner_type.add(value.getName());
            }
        }
        spinner_type.add("Otro");
    }

    public ArrayList<String> getListFromFIlters(){
        ArrayList<String> s=new ArrayList<>();
        s.add("Vacia");
        for(int i =0;i<mGridFilterAdapter.getList().size();++i){
            s.add(mGridFilterAdapter.getList().get(i).type);
        }
        return s;
    }

    private void createBill(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ParallelBillingActiviy.this);
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.add_new_billing, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final AutoCompleteTextView other_type=  dialogView.findViewById(R.id.other_type);
        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_type1);

        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,getListFromFIlters());

        other_type.setThreshold(1);
        other_type.setAdapter(adapter);
        other_type.setTextColor(this.getResources().getColor(R.color.word));

        //SPINNER TYPE
        List<String> spinner_type = new ArrayList<>();
        enumNameToStringArray(BillingType.values(),spinner_type);

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this,
                R.layout.spinner_item,spinner_type);
        adapter_type.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(adapter_type);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinnerType.getSelectedItem());

                if(itemSelected.equals("Otro")){
                    other_type.setVisibility(View.VISIBLE);
                }else{
                    other_type.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        date.setText(DateHelper.get().getActualDate2());

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(date);
            }
        });

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type= String.valueOf(spinnerType.getSelectedItem());
                if(type.equals("Otro")){
                    type=other_type.getText().toString().trim();
                }
                String descr=description.getText().toString().trim();


                Double valueT=0.0;
                if(!value.getText().toString().trim().matches("")){
                    valueT=Double.valueOf(value.getText().toString().trim());
                }

                System.out.println(SessionPrefs.get(ParallelBillingActiviy.this).getName()+"namee");
                ParallelBilling bill = new ParallelBilling( valueT, type, descr, SessionPrefs.get(ParallelBillingActiviy.this).getName());
                bill.created= date.getText().toString().trim();

                ApiClient.get().postParallelBilling(bill, new GenericCallback<ParallelBilling>() {
                    @Override
                    public void onSuccess(ParallelBilling data) {
                        clearView();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo crear la factura",getBaseContext());
                    }
                });

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void selectDate(final TextView date){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(this,
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

                        String time = DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                        String datePicker = year + "-" + smonthOfYear + "-" + sdayOfMonth + " " + time;
                        date.setText(datePicker);

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }



}
