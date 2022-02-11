package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.Interfaces.OnRefreshList;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.models.ParallelBilling;
import com.example.android.loa.network.models.ReportParallelBilling;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportParallelBillingAdapter  extends BaseAdapter<ReportParallelBilling,ReportParallelBillingAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private OnRefreshList onRefreshlistListener = null;
    public void setOnRefreshlistListener(OnRefreshList listener){
        onRefreshlistListener = listener;
    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).created));
            return date.getTime();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_extr, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportParallelBilling e = getItem(position);

            String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String numberDay = DateHelper.get().getYear(DateHelper.get().onlyDate(e.created));

            String text = ValuesHelper.get().getIntegerQuantityByLei(e.amount_month);

            int count = linear.getChildCount();
            View v = null;
            View v2 = null;
            View v3 = null;

            for (int k = 0; k < count; k++) {
                v3 = linear.getChildAt(k);
                if (k == 0) {
                    RelativeLayout linear4 = (RelativeLayout) v3;

                    int count3 = linear4.getChildCount();

                    for (int i = 0; i < count3; i++) {
                        v = linear4.getChildAt(i);
                        if (i == 0) {
                            TextView t = (TextView) v;
                            t.setText(dateToShow2);

                        } else if (i == 1) {
                            TextView t2 = (TextView) v;
                            t2.setText(numberDay);
                        }
                    }
                } else if (k == 2) {
                    TextView t2 = (TextView) v3;
                    t2.setText(text);
                }
            }
        }
    }


    public ReportParallelBillingAdapter(Context context, List<ReportParallelBilling> extractions) {
        setItems(extractions);
        mContext = context;
    }


    public ReportParallelBillingAdapter() {
    }

    public List<ReportParallelBilling> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView listExtr;

        public ViewHolder(View v) {
            super(v);
            listExtr = v.findViewById(R.id.list_extractions);
        }
    }

    @Override
    public ReportParallelBillingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_report_extraction, parent, false);
        ReportParallelBillingAdapter.ViewHolder vh = new ReportParallelBillingAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ReportParallelBillingAdapter.ViewHolder vh) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportParallelBillingAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final ReportParallelBilling current = getItem(position);

        ParallelBillingAdapter mvmtAdapter = new ParallelBillingAdapter(mContext, new ArrayList<ParallelBilling>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.listExtr.setLayoutManager(layoutManager);
        holder.listExtr.setAdapter(mvmtAdapter);
        mvmtAdapter.setItems(current.list);

        mvmtAdapter.setOnRefreshlistListener(onRefreshlistListener);


    }
}
