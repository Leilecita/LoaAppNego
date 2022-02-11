package com.example.android.loa.adapters;


import android.content.Context;

import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.R;

import com.example.android.loa.network.models.StatisticVal;

import java.util.List;

public class ItemStatisticAdapter extends BaseAdapter<StatisticVal, ItemStatisticAdapter.ViewHolder> {

    private Context mContext;
    private Integer mTotalVal;
    private String mSelectedVal;

    public ItemStatisticAdapter(Context context, List<StatisticVal> incomes) {
        setItems(incomes);
        mContext = context;
        mTotalVal = 0;
        mSelectedVal = "ninguno";
    }

    public void setTotalVal(Integer val){
        this.mTotalVal = val;
    }

    public void setSelectedVal(String val){
        this.mSelectedVal = val;
    }

    public ItemStatisticAdapter() {

    }

    public List<StatisticVal> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView total;
        public TextView percentage;
        public LinearLayout linear;


        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            total = v.findViewById(R.id.total);
            percentage = v.findViewById(R.id.percentage);
            linear = v.findViewById(R.id.line);

        }

    }

    @Override
    public ItemStatisticAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_statistic_val, parent, false);
        ItemStatisticAdapter.ViewHolder vh = new ItemStatisticAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ItemStatisticAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.total != null)
            vh.total.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ItemStatisticAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final StatisticVal income = getItem(position);

        if (position%2==0){
            holder.linear.setBackgroundColor(mContext.getResources().getColor(R.color.background_line1));
        }else {
            holder.linear.setBackgroundColor(mContext.getResources().getColor(R.color.background_line2));
        }

        holder.total.setText(income.total);

        holder.percentage.setText(String.valueOf(calculatePercentage(mTotalVal,Integer.valueOf(income.total))));
        holder.description.setText(income.descr);

        String text= income.descr;
        if(text.equals(mSelectedVal)){
            holder.description.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }else{
            holder.description.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private int calculatePercentage(Integer totalBig, Integer valueToCalclulate){
        return 100*valueToCalclulate/totalBig;
    }
}
