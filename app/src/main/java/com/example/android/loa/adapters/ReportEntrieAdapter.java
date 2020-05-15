package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.network.models.ReportEntrie;

import com.example.android.loa.network.models.ReportStockEvent;

import java.util.ArrayList;
import java.util.List;

public class ReportEntrieAdapter extends BaseAdapter<ReportEntrie,ReportEntrieAdapter.ViewHolder> {
    private Context mContext;
    private String mGroupBy="day";


    public ReportEntrieAdapter(Context context, List<ReportEntrie> sales){
        setItems(sales);
        mContext = context;
    }

    public void setGroupBy(String group){
        this.mGroupBy=group;
    }

    public ReportEntrieAdapter(){

    }

    public List<ReportEntrie> getListSales(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public RecyclerView recylerEntries;

        public TextView countEntries;
        public LinearLayout content;

        public TextView month;
        public TextView numberDay;


        public ViewHolder(View v){
            super(v);
            recylerEntries=v.findViewById(R.id.list_events);
            content=v.findViewById(R.id.content);
            countEntries=v.findViewById(R.id.entries);

            month=v.findViewById(R.id.month);
            numberDay=v.findViewById(R.id.number_day);
        }
    }

    @Override
    public ReportEntrieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.report_entry,parent,false);
        ReportEntrieAdapter.ViewHolder vh = new ReportEntrieAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ReportEntrieAdapter.ViewHolder vh){

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportEntrieAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportEntrie current = getItem(position);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.recylerEntries.setLayoutManager(layoutManager);
        ReportStockEventAdapter mAdapter = new ReportStockEventAdapter(mContext, new ArrayList<ReportStockEvent>());
        mAdapter.setItems(current.listEntries);
        holder.recylerEntries.setAdapter(mAdapter);


        holder.month.setText(DateHelper.get().getNameMonth(current.created).substring(0,3));

        if(mGroupBy.equals("day")){
            holder.numberDay.setText(DateHelper.get().numberDay(current.created));
            holder.numberDay.setVisibility(View.VISIBLE);
        }else{
            holder.numberDay.setText("");
            holder.numberDay.setVisibility(View.GONE);
        }

        holder.countEntries.setText(String.valueOf(current.countEntries));

    }
}
