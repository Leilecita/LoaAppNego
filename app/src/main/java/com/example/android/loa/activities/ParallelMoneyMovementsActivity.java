package com.example.android.loa.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.adapters.ReportMovementMoneyAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ParallelMoneyMovement;
import com.example.android.loa.network.models.ReportParallelMoneyMovement;
import com.example.android.loa.types.BilledType;
import com.example.android.loa.types.Constants;
import com.example.android.loa.types.GroupByType;
import com.example.android.loa.types.MoneyMovementPaymentType;
import com.example.android.loa.types.MoneyMovementType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ParallelMoneyMovementsActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ReportMovementMoneyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout bottomSheet;

    private LinearLayout sueldos;
    private LinearLayout contador;
    private LinearLayout autonomos;
    private LinearLayout alquiler;
    private LinearLayout mercaderia;
    private LinearLayout all;

    private LinearLayout monthFilter;
    private LinearLayout dayFilter;

    private LinearLayout sinFacturar;
    private LinearLayout facturado;
    private LinearLayout all_factura;

    private LinearLayout createMoneyEvent;

    private BilledType selectedBilled = BilledType.SIN_FACTURAR;

    private MoneyMovementType selectedMovementType = MoneyMovementType.ALL;

    private GroupByType groupByType= GroupByType.DAY;

    private BilledType billedType= BilledType.ALL;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_money_movements;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Movimiento paralelos Santi");

        mRecyclerView =  findViewById(R.id.list_report_moeny_movements);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new ReportMovementMoneyAdapter(this,new ArrayList<ReportParallelMoneyMovement>());
        mRecyclerView.setAdapter(mAdapter);

        createMoneyEvent=this.findViewById(R.id.fab_agregarTod);
        createMoneyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMovement();
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
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        topBarListener(bottomSheet);

        implementsPaginate();
    }

    private void unselectDayMonth(){
        monthFilter.getBackground().setColorFilter(getResources().getColor(R.color.mes), PorterDuff.Mode.SRC_IN);
        dayFilter.getBackground().setColorFilter(getResources().getColor(R.color.dia), PorterDuff.Mode.SRC_IN);
    }

    private void unselectBilled(){
        sinFacturar.getBackground().setColorFilter(getResources().getColor(R.color.mes), PorterDuff.Mode.SRC_IN);
        facturado.getBackground().setColorFilter(getResources().getColor(R.color.dia), PorterDuff.Mode.SRC_IN);
        all_factura.getBackground().setColorFilter(getResources().getColor(R.color.mes), PorterDuff.Mode.SRC_IN);
    }

    private void topBarListener(View bottomSheet){
        sueldos=bottomSheet.findViewById(R.id.sueldos);
        alquiler=bottomSheet.findViewById(R.id.alquiler);
        mercaderia=bottomSheet.findViewById(R.id.mercaderia);
        autonomos=bottomSheet.findViewById(R.id.autonomos);
        contador=bottomSheet.findViewById(R.id.contador);
        mercaderia=bottomSheet.findViewById(R.id.mercaderia);
        all=bottomSheet.findViewById(R.id.all);

        monthFilter=bottomSheet.findViewById(R.id.mes);
        dayFilter=bottomSheet.findViewById(R.id.dia);

        sinFacturar=bottomSheet.findViewById(R.id.sin_facturar);
        facturado=bottomSheet.findViewById(R.id.facturado);
        all_factura=bottomSheet.findViewById(R.id.all_billed);

        all_factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectBilled();
                billedType=BilledType.ALL;
                all_factura.getBackground().setColorFilter(getResources().getColor(R.color.mes_selected), PorterDuff.Mode.SRC_IN);
                clearView();
            }
        });

        sinFacturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectBilled();
                billedType=BilledType.SIN_FACTURAR;
                sinFacturar.getBackground().setColorFilter(getResources().getColor(R.color.mes_selected), PorterDuff.Mode.SRC_IN);
                clearView();
            }
        });

        facturado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectBilled();
                billedType=BilledType.FACTURADO;
                facturado.getBackground().setColorFilter(getResources().getColor(R.color.dia_selected), PorterDuff.Mode.SRC_IN);
                clearView();
            }
        });

        monthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectDayMonth();
                groupByType=GroupByType.MONTH;
                clearView();
                monthFilter.getBackground().setColorFilter(getResources().getColor(R.color.mes_selected), PorterDuff.Mode.SRC_IN);
                mAdapter.setGroupBy(groupByType.getName());
            }
        });

        dayFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectDayMonth();
                groupByType=GroupByType.DAY;
                mAdapter.setGroupBy(groupByType.getName());
                dayFilter.getBackground().setColorFilter(getResources().getColor(R.color.dia_selected), PorterDuff.Mode.SRC_IN);
                clearView();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.ALL;
                clearView();
            }
        });
        contador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.SANTI_PAGO_CONTADOR;
                clearView();
            }
        });
        autonomos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.SANTI_PAGO_AUTONOMOS;
                clearView();
            }
        });
        sueldos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.SANTI_PAGO_SUELDO;
                clearView();
            }
        });
        alquiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.SANTI_PAGO_ALQUILER;
                clearView();
            }
        });
        mercaderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovementType= MoneyMovementType.SANTI_PAGO_MERCADERIA;
                clearView();
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

        ApiClient.get().getReportMoneyMovements(mCurrentPage,selectedMovementType.getName(),groupByType.getName(),billedType.getName(), new GenericCallback<List<ReportParallelMoneyMovement>>() {
            @Override
            public void onSuccess(List<ReportParallelMoneyMovement> data) {
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

    private static <T extends Enum<MoneyMovementType>> void enumNameToStringArray(MoneyMovementType[] values, List<String> spinner_type) {
        for (MoneyMovementType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_type.add("Tipo");
            }else{
                spinner_type.add(value.getName());
            }
        }
    }

    private static <T extends Enum<MoneyMovementPaymentType>> void enumNameToStringArray(MoneyMovementPaymentType[] values, List<String> spinner_detail) {
        for (MoneyMovementPaymentType value : values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_detail.add("Detalle");
            }else{
                spinner_detail.add(value.getName());
            }
        }
    }


    private void createMovement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_money_movement, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_type1);
        final Spinner spinnerDetail=  dialogView.findViewById(R.id.spinner_detail);
        final LinearLayout line_detail=  dialogView.findViewById(R.id.line_detail);

        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        final CheckBox check_remito=  dialogView.findViewById(R.id.check_remito);
        final CheckBox check_factura=  dialogView.findViewById(R.id.check_factura);

        check_remito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_remito.isChecked()){
                    check_remito.setChecked(true);
                    check_factura.setChecked(false);

                    selectedBilled=BilledType.SIN_FACTURAR;

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

                    selectedBilled=BilledType.FACTURADO;
                }else{
                    check_factura.setChecked(false);
                }
            }
        });

        //SPINNER DETAIL
        final List<String> spinner_detail = new ArrayList<>();
        enumNameToStringArray(MoneyMovementPaymentType.values(), spinner_detail);

        //SPINNER TYPE
        List<String> spinner_type = new ArrayList<>();
        enumNameToStringArray(MoneyMovementType.values(),spinner_type);

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this,
                R.layout.spinner_item,spinner_type);
        adapter_type.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(adapter_type);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinnerType.getSelectedItem());

                if(itemSelected.equals(Constants.MONEY_SANTI_PAGO_MERCADERIA)){
                   line_detail.setVisibility(View.VISIBLE);
                }else{
                    line_detail.setVisibility(View.GONE);
                }

                ArrayAdapter<String> adapter_detail = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.spinner_item,spinner_detail);
                adapter_detail.setDropDownViewResource(R.layout.spinner_item);
                spinnerDetail.setAdapter(adapter_detail);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        date.setText(DateHelper.get().getActualDate());

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
                String descr=description.getText().toString().trim();

                String detail=String.valueOf(spinnerDetail.getSelectedItem());
                if(detail.equals("Detalle")){
                    detail="";
                }

                Double valueT=0.0;
                if(!value.getText().toString().trim().matches("")){
                    valueT=Double.valueOf(value.getText().toString().trim());
                }

                ParallelMoneyMovement money= new ParallelMoneyMovement(descr,type,valueT,detail,selectedBilled.getName());
                money.created=DateHelper.get().changeFormatDateUserToServer(date.getText().toString().trim());

                ApiClient.get().postMoneyMovement(money, new GenericCallback<ParallelMoneyMovement>() {
                    @Override
                    public void onSuccess(ParallelMoneyMovement data) {
                        clearView();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo crear el movimiento",getBaseContext());

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
