package com.example.android.loa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.adapters.StockEventAdapter;
import com.example.android.loa.adapters.sales.ReportSaleAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportSale;
import com.example.android.loa.network.models.ReportStockEvent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

public class SalesActivity extends BaseActivity implements Paginate.Callbacks {


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



    @Override
    public int getLayoutRes() {
        return R.layout.fragment_sales;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mRecyclerView = this.findViewById(R.id.list_report_sales);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportSaleAdapter(this, new ArrayList<ReportSale>());

        mRecyclerView.setAdapter(mAdapter);

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

        bottomSheet = this.findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        // bts(bsb);
        topBarListener(bottomSheet);
        implementsPaginate();
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


    private void implementsPaginate() {
        loadingInProgress = false;
        mCurrentPage = 0;
        hasMoreItems = true;

        paginate = Paginate.with(mRecyclerView, this)
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


    private void clearAndList() {
        clearView();
        listSales();
    }


    private void clearView() {
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems = true;
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




}