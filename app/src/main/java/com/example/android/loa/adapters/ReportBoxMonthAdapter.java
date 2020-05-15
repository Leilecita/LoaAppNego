package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.ReportMonthBox;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class ReportBoxMonthAdapter extends BaseAdapter<ReportMonthBox,ReportBoxMonthAdapter.ViewHolder>  {
    private Context mContext;

    private OnSelectedItem onSelectedItem= null;

    public void setOnSelectedItem(OnSelectedItem lister){
        onSelectedItem=lister;
    }

    public ReportBoxMonthAdapter(Context context, List<ReportMonthBox> events){
        setItems(events);
        mContext = context;
    }

    public ReportBoxMonthAdapter(){

    }

    public List<ReportMonthBox> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView month;
        public TextView year;
        public TextView counted_sale;
        public TextView card;
        public TextView dep;
        public RecyclerView listBoxes;

        public ViewHolder(View v) {
            super(v);
            counted_sale = v.findViewById(R.id.venta_ctdo);
            card = v.findViewById(R.id.card);
            dep = v.findViewById(R.id.dep);
            month = v.findViewById(R.id.month);
            year = v.findViewById(R.id.year);
            listBoxes = v.findViewById(R.id.list_box);
        }
    }

    @Override
    public ReportBoxMonthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.report_month_box_item,parent,false);
        ReportBoxMonthAdapter.ViewHolder vh = new ReportBoxMonthAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ReportBoxMonthAdapter.ViewHolder vh){
        if (vh.month != null)
            vh.month.setText(null);
        if (vh.year != null)
            vh.year.setText(null);
        if (vh.card != null)
            vh.card.setText(null);
        if (vh.counted_sale != null)
            vh.counted_sale.setText(null);

        if (vh.dep != null)
            vh.dep.setText(null);
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportBoxMonthAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportMonthBox current=getItem(position);
        holder.month.setText(getMonth(current.m).substring(0, 3));

        holder.year.setText(current.y);
        holder.counted_sale.setText(String.valueOf(current.sale));
        holder.card.setText(String.valueOf(current.card));
        holder.dep.setText(String.valueOf(current.dep));

        final BoxAdapter adapterItemOrder= new BoxAdapter(mContext,new ArrayList<Box>());
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(mContext);
        holder.listBoxes.setLayoutManager(layoutManager);
        holder.listBoxes.setAdapter(adapterItemOrder);

        adapterItemOrder.setItems(current.listBoxesByMonth);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.listBoxes.getVisibility() == View.GONE){
                    holder.listBoxes.setVisibility(View.VISIBLE);
                }else{
                    holder.listBoxes.setVisibility(View.GONE);
                }

            }
        });

    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}