package com.example.android.loa.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.R;
import com.example.android.loa.adapters.PriceEventAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportPriceEvent;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

public class PriceEventsActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private PriceEventAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_price_events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Log de precios");

        mRecyclerView = findViewById(R.id.list_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PriceEventAdapter(this, new ArrayList<ReportPriceEvent>());
        mRecyclerView.setAdapter(mAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        implementsPaginate();
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

    private void listReportStockEvents() {
        loadingInProgress = true;

        ApiClient.get().getPriceEvents(mCurrentPage, new GenericCallback<List<ReportPriceEvent>>() {
            @Override
            public void onSuccess(List<ReportPriceEvent> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                } else {
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if (prevSize == 0) {
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
        listReportStockEvents();
    }

    private void clearView() {
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems = true;
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
}