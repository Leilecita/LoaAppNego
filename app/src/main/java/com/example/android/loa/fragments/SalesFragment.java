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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.activities.ProductsActivity;
import com.example.android.loa.adapters.sales.ReportSaleAdapter;
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

    private String mItem;
    private String mGroupBy;

    public int getIconButton() {
        return R.drawable.add3;
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

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        //mRecyclerView.addItemDecoration(new DividerDecoration(getContext()));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        //STICKY
        //------------------------

        mItem="Todos";
        mGroupBy="day";
        mAdapter.setGroupBy(mGroupBy);

        bottomSheet = mRootView.findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

       // bts(bsb);
        topBarListener(bottomSheet);
        implementsPaginate();

        return mRootView;
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

       clearView();

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
                clearView();

            }
        });

        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupBy="day";
                mes.setImageResource(R.drawable.mescl2);
                dia.setImageResource(R.drawable.bdia);
                mAdapter.setGroupBy(mGroupBy);
                clearView();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem="Todos";
                changeCircleSelected();
                all.setImageResource(R.drawable.ball);
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


}