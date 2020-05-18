package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.adapters.IncomesAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Income;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomesListActivity extends BaseActivity implements Paginate.Callbacks{


    private RecyclerView mRecyclerView;
    private IncomesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout button;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_list_incomes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Señas");
        mRecyclerView = findViewById(R.id.list_incomes);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new IncomesAdapter(this,new ArrayList<Income>());
        mRecyclerView.setAdapter(mAdapter);

        button= findViewById(R.id.fab_agregarTod);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncomeMoney();
            }
        });

        implementsPaginate();
    }

    private void listIncomes(){
        loadingInProgress=true;

        ApiClient.get().getIncomes(mCurrentPage, new GenericCallback<List<Income>>() {
            @Override
            public void onSuccess(List<Income> data) {
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
    private void clearAndList(){
        clearView();
        listIncomes();
    }
    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    private void addIncomeMoney(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_income, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final CheckBox check_card=  dialogView.findViewById(R.id.check_card);
        final CheckBox check_deb=  dialogView.findViewById(R.id.check_deb);
        final CheckBox check_ef=  dialogView.findViewById(R.id.check_ef);

        final CheckBox retire_product=  dialogView.findViewById(R.id.out_product);
        final CheckBox not_retire=  dialogView.findViewById(R.id.not_out);

        date.setText(DateHelper.get().getActualDate());

        final TextView name=  dialogView.findViewById(R.id.name);
        final TextView phone=  dialogView.findViewById(R.id.phone);
        final TextView address=  dialogView.findViewById(R.id.adress);
        final TextView value_product=  dialogView.findViewById(R.id.value_product);


        date.setOnClickListener(new View.OnClickListener() {
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
                            clearAndList();

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



    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView, this)
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

    @Override
    public void onLoadMore() {
        listIncomes();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

}
