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
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.network.models.ReportExtraction;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportExtractionAdapter  extends BaseAdapter<ReportExtraction,ReportExtractionAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private String groupby;


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
                .inflate(R.layout.view_header_extr2, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportExtraction e = getItem(position);

            String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String numberDay = DateHelper.get().numberDay(e.created);

            String text = ValuesHelper.get().getIntegerQuantityByLei(e.amountDay);

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

                            if(groupby.equals("day")){
                                t.setGravity(Gravity.CENTER_HORIZONTAL);
                            }else{
                                t.setGravity(Gravity.CENTER);
                            }
                        } else if (i == 1) {

                            TextView t2 = (TextView) v;
                            t2.setText(numberDay);

                            if(groupby.equals("day")){
                                t2.setVisibility(View.VISIBLE);
                            }else{
                                t2.setVisibility(View.GONE);
                            }
                        }
                    }
                } else if (k == 2) {
                    TextView t2 = (TextView) v3;
                    t2.setText(text);
                }
            }
        }
    }


    public ReportExtractionAdapter(Context context, List<ReportExtraction> extractions) {
        setItems(extractions);
        mContext = context;
        groupby="day";
    }

    public void setGroupBy(String groupby){
        this.groupby=groupby;

    }
    public ReportExtractionAdapter() {
    }

    public List<ReportExtraction> getListEmployees() {
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
    public ReportExtractionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_report_extraction, parent, false);
        ReportExtractionAdapter.ViewHolder vh = new ReportExtractionAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ReportExtractionAdapter.ViewHolder vh) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportExtractionAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final ReportExtraction current = getItem(position);

        ExtractionAdapter extrAdapter = new ExtractionAdapter(mContext, new ArrayList<Extraction>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.listExtr.setLayoutManager(layoutManager);
        holder.listExtr.setAdapter(extrAdapter);
        extrAdapter.setItems(current.listExtractions);


        if(groupby.equals("month")){
            extrAdapter.setGroupby("month");
        }


    }
}
