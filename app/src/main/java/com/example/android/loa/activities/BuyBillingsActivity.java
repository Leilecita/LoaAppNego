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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnRefreshList;
import com.example.android.loa.R;
import com.example.android.loa.adapters.BuyBillingAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.BuyBilling;
import com.example.android.loa.network.models.FilterName;
import com.example.android.loa.network.models.FilterType;
import com.example.android.loa.network.models.ReportBuyBilling;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuyBillingsActivity extends BaseActivity implements Paginate.Callbacks, OnRefreshList {

    private RecyclerView mRecyclerView;
    private BuyBillingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    private LinearLayout home;
    private LinearLayout add;
    private TextView title;
    private TextView cant_art;
    private TextView tot_amount;

    private String bill_type = "remito";
    private String isIva= "no";

    private ImageView clean_date_since;
    private ImageView clean_date_to;

    private TextView mViewDateSince;
    private TextView mViewDateTo;
    private String mDateSine;
    private String mDateTo;

    private ProgressBar simpleProgressBar;

    private List<FilterName> listNames;

    public void onRefreshList(){
        clearView();
    }


    @Override
    public int getLayoutRes() {
        return R.layout.activity_buy_billings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // showBackArrow();

        title = findViewById(R.id.title);
        title.setText("Facturas de compra");
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
        mAdapter=new BuyBillingAdapter(this,new ArrayList<BuyBilling>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnRefreshlistListener(this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBill();
            }
        });


        cant_art = findViewById(R.id.cant_art);
        tot_amount = findViewById(R.id.tot_amount);
        clean_date_since = findViewById(R.id.clean_dateSince);
        clean_date_since.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateSine = "Todos";
                mViewDateSince.setText("");
                clearView();
                //clearAndList();
            }
        });
        clean_date_to = findViewById(R.id.clean_dateTo);
        clean_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTo = "Todos";
                mViewDateTo.setText("");
                clearView();
               // clearAndList();
            }
        });

        simpleProgressBar = findViewById(R.id.simpleProgressBar);

        mViewDateSince = findViewById(R.id.date_since);
        mViewDateTo = findViewById(R.id.date_to);

        mDateSine = "2019-09-01 00:00:00";
        mViewDateSince.setText("");

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

        listNames = new ArrayList<>();
        getBusinessName();

        implementsPaginate();
    }


    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;

        cant_art.setText("");
        tot_amount.setText("");
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

        ApiClient.get().getReportBuyBillings(mCurrentPage, "Todo",mDateSine , mDateTo, new GenericCallback<ReportBuyBilling>() {
            @Override
            public void onSuccess(ReportBuyBilling data2) {

                cant_art.setText(String.valueOf(data2.tot_art));
                tot_amount.setText(String.valueOf(data2.tot_amount));


                List<BuyBilling> data = data2.list;
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
                simpleProgressBar.setVisibility(View.GONE);
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

    private List<String> convertTolistString(List<FilterName> list){
        ArrayList<String>  l = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            l.add(list.get(i).b_name);
        }

        return l;
    }

    private void createBill(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BuyBillingsActivity.this);
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.add_new_product_billing, null);
        builder.setView(dialogView);

        final TextView amount_bill=  dialogView.findViewById(R.id.amount);
        final EditText type_bill=  dialogView.findViewById(R.id.type);
        final AutoCompleteTextView business_name=  dialogView.findViewById(R.id.business_name);

        final TextView date=  dialogView.findViewById(R.id.date);
        final EditText number_bill=  dialogView.findViewById(R.id.number_bill);
        final EditText cant_art_bill=  dialogView.findViewById(R.id.cant);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        final CheckBox check_remito=  dialogView.findViewById(R.id.check_remito);
        final CheckBox check_factura=  dialogView.findViewById(R.id.check_factura);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,convertTolistString(listNames));
        //Getting the instance of AutoCompleteTextView
        business_name.setThreshold(1);//will start working from first character
        business_name.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        check_remito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_remito.isChecked()){
                    check_remito.setChecked(true);
                    check_factura.setChecked(false);

                    bill_type = "remito";

                }else{
                    check_remito.setChecked(false);
                }
            }
        });
        check_factura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_factura.isChecked()){
                    check_factura.setChecked(true);
                    check_remito.setChecked(false);

                    bill_type = "factura";
                }else{
                    check_factura.setChecked(false);
                }
            }
        });

        final CheckBox check_siva=  dialogView.findViewById(R.id.check_siva);
        final CheckBox check_civa=  dialogView.findViewById(R.id.check_civa);

        check_siva.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_siva.isChecked()){
                    check_siva.setChecked(true);
                    check_civa.setChecked(false);

                    isIva="no";

                }else{
                    check_siva.setChecked(false);
                }
            }
        });
        check_civa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_civa.isChecked()){
                    check_civa.setChecked(true);
                    check_siva.setChecked(false);

                    isIva="si";
                }else{
                    check_civa.setChecked(false);
                }
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

                Double valueT=0.0;
                if(!amount_bill.getText().toString().trim().matches("")){
                    valueT=Double.valueOf(amount_bill.getText().toString().trim());
                }

                BuyBilling bill = new BuyBilling(valueT, bill_type,business_name.getText().toString().trim(),isIva, Integer.valueOf(cant_art_bill.getText().toString().trim()),
                        number_bill.getText().toString().trim(), SessionPrefs.get(BuyBillingsActivity.this).getName());
                bill.billing_date= date.getText().toString().trim();

                ApiClient.get().postProductBilling(bill, new GenericCallback<BuyBilling>() {
                    @Override
                    public void onSuccess(BuyBilling data) {
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

    private void getBusinessName(){

        ApiClient.get().getDistinctiBusinessName(new GenericCallback<List<FilterName>>() {
            @Override
            public void onSuccess(List<FilterName> data) {
                    listNames = data;
                System.out.println("hola"+data.size()+data.get(0).b_name);
            }

            @Override
            public void onError(Error error) {

            }
        });
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

                        simpleProgressBar.setVisibility(View.VISIBLE);
                        clearView();
                        //clearAndList();

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

                clearView();
                //clearAndList();
                dialog.dismiss();
            }
        });
        datePickerDialog.show();
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
