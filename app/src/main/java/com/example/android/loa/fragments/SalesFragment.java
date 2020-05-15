package com.example.android.loa.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.activities.ProductsActivity;
import com.example.android.loa.adapters.ReportSaleAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Income;
import com.example.android.loa.network.models.ReportSale;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalesFragment extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ReportSaleAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout bottomSheet;

    private ImageView man;
    private ImageView woman;
    private ImageView boy;
    private ImageView accesories;
    private ImageView tecnico;
    private ImageView zapas;
    private ImageView luz;
    private ImageView oferta;
    private ImageView all;

    private ImageView income;
    private ImageView mes;
    private ImageView dia;

 /*   private TextView textMan;
    private TextView textWoman;
    private TextView textBoy;
    private TextView textTec;
    private TextView textZap;
    private TextView textAcc;
    private TextView textLuz;
    private TextView textOferta;
    private TextView textAll;
*/
    private String mItem;
    private String mGroupBy;

    public int getIconButton() {
        return R.drawable.add_white;
    }

    public int getVisibility() {
        return View.GONE;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_sales, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_report_sales);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportSaleAdapter(getActivity(), new ArrayList<ReportSale>());

        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        //STICKY
/*
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        mRecyclerView.addItemDecoration(new DividerDecoration(getContext()));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
*/
        //------------------------

        mItem="Todos";
        mGroupBy="day";
        mAdapter.setGroupBy(mGroupBy);

        bottomSheet = mRootView.findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        bts(bsb);
        implementsPaginate();

        return mRootView;
    }


    private void clearAndList(){
        clearView();
        listSales();
    }
    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    private void listSales(){
        loadingInProgress=true;

        ApiClient.get().getReportSales(mCurrentPage, mItem,mGroupBy,new GenericCallback<List<ReportSale>>() {
            @Override
            public void onSuccess(List<ReportSale> data) {

                System.out.println("entra aca"+data.size());

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
        listSales();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


    private void changeViewStyle(TextView t){
        t.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        t.setTextColor(getResources().getColor(R.color.word));
    }

    private void changeViewStyleUnselected(TextView t){
        t.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        t.setTextColor(getResources().getColor(R.color.word_clear));
    }

    private void changeCircleSelected(){

        woman.setImageResource(R.drawable.bwomcl);
        boy.setImageResource(R.drawable.bnincl);
        man.setImageResource(R.drawable.bmancl);
        tecnico.setImageResource(R.drawable.btecl);
        zapas.setImageResource(R.drawable.bcalcl);
        accesories.setImageResource(R.drawable.bacccl);
        luz.setImageResource(R.drawable.bluzcl);
        oferta.setImageResource(R.drawable.bofercl);
        all.setImageResource(R.drawable.ballcl);

/*
        changeViewStyleUnselected(textZap);
        changeViewStyleUnselected(textTec);
        changeViewStyleUnselected(textMan);
        changeViewStyleUnselected(textWoman);
        changeViewStyleUnselected(textBoy);
        changeViewStyleUnselected(textAcc);
        changeViewStyleUnselected(textLuz);
        changeViewStyleUnselected(textOferta);
        changeViewStyleUnselected(textAll);
        */

       clearAndList();

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
        mes=bottomSheet.findViewById(R.id.mes);
        dia=bottomSheet.findViewById(R.id.dia);

        mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupBy="month";
                mAdapter.setGroupBy(mGroupBy);
                mes.setImageResource(R.drawable.b23);
                dia.setImageResource(R.drawable.bdiacl);
                clearAndList();

            }
        });

        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupBy="day";
                mes.setImageResource(R.drawable.mescl2);
                dia.setImageResource(R.drawable.bdia);
                mAdapter.setGroupBy(mGroupBy);
                clearAndList();
            }
        });

   /*     textAcc=bottomSheet.findViewById(R.id.textAcc);
        textMan=bottomSheet.findViewById(R.id.textMan);
        textWoman=bottomSheet.findViewById(R.id.textWoman);
        textZap=bottomSheet.findViewById(R.id.textZapas);
        textTec=bottomSheet.findViewById(R.id.textTec);
        textBoy=bottomSheet.findViewById(R.id.textBoy);
        textLuz=bottomSheet.findViewById(R.id.textLuz);
        textOferta=bottomSheet.findViewById(R.id.textOferta);
        textAll=bottomSheet.findViewById(R.id.textAll);
*/
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
                all.setImageResource(R.drawable.ball);
                //changeViewStyle(textAll);
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Dama";
                changeCircleSelected();
                woman.setImageResource(R.drawable.bwom);
               // changeViewStyle(textWoman);
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Hombre";
                changeCircleSelected();
                man.setImageResource(R.drawable.bman);
              //  changeViewStyle(textMan);
            }
        });
        boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Niño";
                changeCircleSelected();
                boy.setImageResource(R.drawable.bnin);
              //  changeViewStyle(textBoy);
            }
        });
        accesories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Accesorio";
                changeCircleSelected();
                accesories.setImageResource(R.drawable.bacc);
              //  changeViewStyle(textAcc);
            }
        });
        tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Tecnico";
                changeCircleSelected();
                tecnico.setImageResource(R.drawable.btec);
              //  changeViewStyle(textTec);
            }
        });

        zapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Calzado";
                changeCircleSelected();
                zapas.setImageResource(R.drawable.bcal);
               // changeViewStyle(textZap);
            }
        });

        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Luz";
                changeCircleSelected();
                luz.setImageResource(R.drawable.bluz);
               // changeViewStyle(textLuz);
            }
        });

        oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Oferta";
                changeCircleSelected();
                oferta.setImageResource(R.drawable.bofer);
              //  changeViewStyle(textOferta);
            }
        });
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

    private void addIncomeMoney(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
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
                                startActivity(new Intent(getContext(), ProductsActivity.class));
                            }

                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                    dialog.dismiss();

                }else{
                    Toast.makeText(getContext(),"Debe seleccionar si retira o no el producto", Toast.LENGTH_LONG).show();
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
}