package com.example.android.loa.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.R;
import com.example.android.loa.activities.todelete.BoxActivity;
import com.example.android.loa.activities.CreateBoxActivity;
import com.example.android.loa.adapters.BoxAdapter;
import com.example.android.loa.adapters.ReportBoxMonthAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportMonthBox;
import com.example.android.loa.network.models.ReportSumByPeriodBox;
import com.example.android.loa.network.models.SpinnerData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.paginate.Paginate;
import com.example.android.loa.network.GenericCallback;
import com.paginate.recycler.LoadingListItemSpanLookup;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BoxFragment extends BaseFragment implements Paginate.Callbacks {

    //box by month
    private RecyclerView mRecyclerViewMonth;
    private ReportBoxMonthAdapter mAdapterMonth;
    private RecyclerView.LayoutManager layoutManagerMonth;

    //box by day
    private RecyclerView mRecyclerView;
    private BoxAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //box by period
    private RecyclerView mRecyclerViewPeriod;
    private BoxAdapter mAdapterPeriod;
    private RecyclerView.LayoutManager layoutManagerPeriod;

    private LinearLayout line_period_tot;
    private TextView tot_ctdo;
    private TextView tot_card;
    private TextView tot_total_box;
    private TextView tot_extr;
    private TextView tot_dep;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    private String mSelectedView;
    private LinearLayout bottomSheet;
    private LinearLayout dia;
    private LinearLayout mes;
    private LinearLayout periodo;

    private View mRootView;

    private String mSelectDate;

    private TextView rest_box;

    private String mDateSince="";
    private String mDateTo="";


    private static final int CREATE_BOX_REQUEST_CODE=2020;

    public int getIconButton(){
        return R.drawable.add3;
    }

    public int getVisibility(){
        return 0;
    }

    @Subscribe
    public void onEvent(RefreshBoxesEvent event){
        Toast.makeText(getActivity(),event.mMessage,Toast.LENGTH_SHORT).show();
        clearView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void clearView(){
        System.out.println("entra aca");
        if(!isLoading()){
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            mAdapterMonth.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_box, container, false);

        line_period_tot=mRootView.findViewById(R.id.line_tot_by_period);
        tot_ctdo=mRootView.findViewById(R.id.tot_venta_ctdo);
        tot_card=mRootView.findViewById(R.id.tot_credit_card);
        tot_dep=mRootView.findViewById(R.id.tot_dep);
        tot_extr=mRootView.findViewById(R.id.tot_rest_box);
        tot_total_box=mRootView.findViewById(R.id.tot_total_amount);

        //box by period
        mRecyclerViewPeriod= mRootView.findViewById(R.id.list_box_period);
        layoutManagerPeriod = new LinearLayoutManager(getActivity());
        mRecyclerViewPeriod.setLayoutManager(layoutManagerPeriod);
        mAdapterPeriod = new BoxAdapter(getActivity(), new ArrayList<Box>());
        mRecyclerViewPeriod.setAdapter(mAdapterPeriod);


        //box by month
        mRecyclerViewMonth =  mRootView.findViewById(R.id.list_box_month);
        layoutManagerMonth = new LinearLayoutManager(getActivity());
        mRecyclerViewMonth.setLayoutManager(layoutManagerMonth);
        mAdapterMonth= new ReportBoxMonthAdapter(getActivity(),new ArrayList<ReportMonthBox>());
        mRecyclerViewMonth.setAdapter(mAdapterMonth);

        //box by dya
        mRecyclerView = mRootView.findViewById(R.id.list_box);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BoxAdapter(getActivity(), new ArrayList<Box>());
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);
        registerForContextMenu(mRecyclerView);

        mSelectDate=DateHelper.get().getActualDate();

        //cuando se ven las cajas por mes, el rest_box desaparece
        rest_box=mRootView.findViewById(R.id.rest_box);


        mSelectedView="dia";
        mRecyclerViewMonth.setVisibility(View.GONE);
        mRecyclerViewPeriod.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        rest_box.setVisibility(View.VISIBLE);

        implementsPaginate();

        EventBus.getDefault().register(this);

        bottomSheet = mRootView.findViewById(R.id.bottomSheet);
        topbarListener(bottomSheet);

        return mRootView;
    }

    private void getAmountByPeriod(String since, String to){
        ApiClient.get().getAmountByPeriod(since, to, new GenericCallback<ReportSumByPeriodBox>() {
            @Override
            public void onSuccess(ReportSumByPeriodBox data) {
                tot_ctdo.setText(String.valueOf(data.tot_ctdo));
                tot_card.setText(String.valueOf(data.tot_card));
                tot_total_box.setText(String.valueOf(data.tot_box));

                tot_dep.setText(String.valueOf(data.tot_dep));
                tot_extr.setText(String.valueOf(data.tot_rest_box));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }



    private void topbarListener(View bottomSheet){
        dia=bottomSheet.findViewById(R.id.dia);
        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!mSelectedView.equals("dia")){
                mSelectedView="dia";

                mRecyclerViewMonth.setVisibility(View.GONE);
                mRecyclerViewPeriod.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                rest_box.setVisibility(View.VISIBLE);


                mAdapter.getList().clear();
                implementsPaginate();
            }

            }
        });
        mes=bottomSheet.findViewById(R.id.mes);
        mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SessionPrefs.get(getContext()).getName().equals("santi") || SessionPrefs.get(getContext()).getName().equals("lei")) {
                    if (!mSelectedView.equals("mes")) {
                        mSelectedView = "mes";

                        mRecyclerView.setVisibility(View.GONE);
                        mRecyclerViewPeriod.setVisibility(View.GONE);
                        mRecyclerViewMonth.setVisibility(View.VISIBLE);

                        rest_box.setVisibility(View.GONE);
                        mAdapterMonth.getList().clear();


                        implementsPaginate();

                    }
                }else{
                    Toast.makeText(getContext(),"Debe loguearse como administrador", Toast.LENGTH_SHORT).show();
                }
            }
        });

        periodo=bottomSheet.findViewById(R.id.periodo);

        periodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SessionPrefs.get(getContext()).getName().equals("santi") || SessionPrefs.get(getContext()).getName().equals("lei")) {
                    cuadSelectPeriod();
                }else{
                    Toast.makeText(getContext(),"Debe loguearse como administrador", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void listBoxesByPeriod(){

        loadingInProgress=true;
        ApiClient.get().getBoxesByPageByPeriod(mCurrentPage,mDateSince,mDateTo,new GenericCallback<List<Box>>() {
            @Override
            public void onSuccess(List<Box> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapterPeriod.getItemCount();
                    mAdapterPeriod.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManagerPeriod.scrollToPosition(0);
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

    private void listBoxes(){

        loadingInProgress=true;

        System.out.println(mCurrentPage);
        ApiClient.get().getBoxesByPage2(mCurrentPage,new GenericCallback<List<Box>>() {
            @Override
            public void onSuccess(List<Box> data) {
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

    public void listByMonth(){
        loadingInProgress=true;

        System.out.println(mCurrentPage);
        ApiClient.get().getTotalMonthBoxes(mCurrentPage, new GenericCallback<List<ReportMonthBox>>() {
            @Override
            public void onSuccess(List<ReportMonthBox> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapterMonth.getItemCount();
                    mAdapterMonth.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManagerMonth.scrollToPosition(0);
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

        if(mSelectedView.equals("mes")){

            paginate= Paginate.with(mRecyclerViewMonth, this)
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
        }else if(mSelectedView.equals("dia")){

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
        }else{
            paginate= Paginate.with(mRecyclerViewPeriod, this)
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
    }

    @Override
    public void onLoadMore() {
        if(mSelectedView.equals("dia")){
            line_period_tot.setVisibility(View.GONE);
            listBoxes();
        }else if(mSelectedView.equals("mes")){
            line_period_tot.setVisibility(View.GONE);
            listByMonth();
        }else{
            line_period_tot.setVisibility(View.VISIBLE);
            listBoxesByPeriod();
        }
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.search);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectDate();
                return false;
            }
        });
    }

    public void onClickButton(){
        if(mSelectedView.equals("dia")){
            Intent intent = new Intent(getContext(), CreateBoxActivity.class);
            if(mAdapter.getList().size() >0){
                intent.putExtra("RESTBOX", String.valueOf(mAdapter.getList().get(0).rest_box));
            }else{
                intent.putExtra("RESTBOX", String.valueOf(0.0));
            }
            startActivityForResult(intent,CREATE_BOX_REQUEST_CODE);
        }else{
            Toast.makeText(getContext(), " Debe seleccionar vista por dia para poder crear una caja",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_BOX_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){
                clearView();
            }
        }
    }

    private void selectDate(){
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

                                String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                                mSelectDate=datePicker;

                                Intent intent= new Intent(getContext(), BoxActivity.class);
                                intent.putExtra("DATE",mSelectDate);
                                intent.putExtra("NEXTDATE",DateHelper.get().getNextDay(mSelectDate));
                                startActivity(intent);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }



    private void selectPeriodDate(final TextView date, final String which){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getActivity(), R.style.datepicker,
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

                        String d=year + "-" + smonthOfYear + "-" +  sdayOfMonth;
                        String dToShow=sdayOfMonth + "-" + smonthOfYear + "-" +  year;

                        date.setText(dToShow);

                        if(which.equals("since")){
                            mDateSince=d+" 00:00:00";
                        }else{
                            mDateTo=d+" 00:00:00";
                        }


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void cuadSelectPeriod(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_select_period, null);
        builder.setView(dialogView);

        final TextView dateSince = dialogView.findViewById(R.id.dateSince);
        final TextView dateTo = dialogView.findViewById(R.id.dateTo);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);


        dateSince.setHint(DateHelper.get().getOnlyDate(DateHelper.get().changeOrderDate(mDateSince)));
        dateTo.setHint(DateHelper.get().getOnlyDate(DateHelper.get().changeOrderDate(mDateTo)));

        dateSince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPeriodDate(dateSince,"since");
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPeriodDate(dateTo,"to");
            }
        });
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(!dateSince.getText().toString().trim().equals("") && !dateTo.getText().toString().trim().equals("") ){

                mSelectedView="periodo";

                mRecyclerViewMonth.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mRecyclerViewPeriod.setVisibility(View.VISIBLE);

                rest_box.setVisibility(View.VISIBLE);

                mAdapterPeriod.getList().clear();

                implementsPaginate();


                getAmountByPeriod(mDateSince,mDateTo);
                dialog.dismiss();
            }else{
                Toast.makeText(getContext(),"Las dos fechas deben estar completas", Toast.LENGTH_SHORT).show();
            }

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

}
