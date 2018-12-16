package com.example.android.loa.adapters;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.android.loa.R;
import com.example.android.loa.network.models.Item_employee;

import java.util.List;

public class HourEmployeeAdapter extends  BaseAdapter<Item_employee,HourEmployeeAdapter.ViewHolder> {

    private Context mContext;

    public HourEmployeeAdapter(Context context, List<Item_employee> items) {
        setItems(items);
        mContext = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView turn;
        public TextView time_worked;
        public TextView observation;



        public ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.date);
            time_worked = v.findViewById(R.id.time_worked);
            turn = v.findViewById(R.id.turn);
            observation = v.findViewById(R.id.observation);
        }
    }

    @Override
    public HourEmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_employe_file, parent, false);
        HourEmployeeAdapter.ViewHolder vh = new HourEmployeeAdapter.ViewHolder(v);
        return vh;
    }


    public void clearViewHolder(HourEmployeeAdapter.ViewHolder vh) {
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.time_worked != null)
            vh.time_worked.setText(null);
        if (vh.turn != null)
            vh.turn.setText(null);
        if (vh.observation != null)
            vh.observation.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final HourEmployeeAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final Item_employee currentItemEmployee = getItem(position);

        holder.date.setText(currentItemEmployee.date);
        holder.turn.setText(currentItemEmployee.turn);
        holder.time_worked.setText(String.valueOf(currentItemEmployee.time_worked));
        holder.observation.setText(currentItemEmployee.observation);

    }
}
