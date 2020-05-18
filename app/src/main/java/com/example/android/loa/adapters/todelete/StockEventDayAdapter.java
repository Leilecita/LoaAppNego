package com.example.android.loa.adapters.todelete;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.adapters.BaseAdapter;
import com.example.android.loa.network.models.StockEvent;

import java.util.List;

public class StockEventDayAdapter extends BaseAdapter<StockEvent,StockEventDayAdapter.ViewHolder> {
    private Context mContext;
    private Boolean mHideDetail;

    public StockEventDayAdapter(Context context, List<StockEvent> events) {
        setItems(events);
        mContext = context;

        mHideDetail = true;
    }

    public void setHideDetail(boolean val) {
        this.mHideDetail = val;
    }

    public boolean getHideDetail() {
        return mHideDetail;
    }

    public StockEventDayAdapter() {

    }

    public List<StockEvent> getListStockEvents() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView actual_stock;
        public TextView stock_in;
        public TextView stock_out;
        public TextView ideal_stock;
        public TextView balance_stock;
        public TextView dif;
        public TextView date;
        public TextView year;

        public TextView detail;

        public LinearLayout line_options;


        public ViewHolder(View v) {
            super(v);
            actual_stock = v.findViewById(R.id.stock_ant);
            stock_in = v.findViewById(R.id.stock_in);
            stock_out = v.findViewById(R.id.stock_out);
            balance_stock = v.findViewById(R.id.balance_stock);
            ideal_stock = v.findViewById(R.id.ideal_stock);
            date = v.findViewById(R.id.date);
            year = v.findViewById(R.id.year);
            dif = v.findViewById(R.id.dif);

            detail = v.findViewById(R.id.detail);
            line_options = v.findViewById(R.id.line_options);

        }
    }

    @Override
    public StockEventDayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.stock_event_day_adapter, parent, false);
        StockEventDayAdapter.ViewHolder vh = new StockEventDayAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(StockEventDayAdapter.ViewHolder vh) {
        if (vh.stock_in != null)
            vh.stock_in.setText(null);
        if (vh.stock_out != null)
            vh.stock_out.setText(null);
        if (vh.actual_stock != null)
            vh.actual_stock.setText(null);
        if (vh.balance_stock != null)
            vh.balance_stock.setText(null);
        if (vh.ideal_stock != null)
            vh.ideal_stock.setText(null);
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.year != null)
            vh.year.setText(null);
        if (vh.dif != null)
            vh.dif.setText(null);
        if (vh.detail != null)
            vh.detail.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final StockEventDayAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final StockEvent current = getItem(position);

        holder.stock_out.setText(String.valueOf(current.stock_out));
        if (current.stock_out != 0) {
            holder.stock_out.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }

        holder.stock_in.setText(String.valueOf(current.stock_in));
        if (current.stock_in != 0) {
            holder.stock_in.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }

        holder.ideal_stock.setText(String.valueOf(current.ideal_stock));

        holder.actual_stock.setText(String.valueOf(current.stock_ant));
        if (current.stock_ant != 0) {
            holder.actual_stock.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }

        if (current.balance_stock == null) {

            holder.balance_stock.setText("*");
            holder.balance_stock.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.balance_stock.setText(String.valueOf(current.balance_stock));
            Integer diff = current.balance_stock - current.ideal_stock;

            String text = (diff > 0) ? "+" : "";
            holder.dif.setText(text + String.valueOf(diff));
            holder.stock_in.setText("*");
            holder.stock_out.setText("*");
            holder.actual_stock.setText("*");
        }

        if (mHideDetail) {
            holder.line_options.setVisibility(View.GONE);
        } else {
            holder.detail.setText(current.detail);
            holder.line_options.setVisibility(View.VISIBLE);
        }

        final String dateToShow = DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(current.created));

        holder.date.setText(DateHelper.get().onlyDayMonth(dateToShow));
        //holder.year.setText(DateHelper.get().getOnlyYear(dateToShow));

        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, DateHelper.get().getOnlyDate(dateToShow), Toast.LENGTH_SHORT).show();
            }
        });

        holder.detail.setText(current.detail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.line_options.getVisibility() == View.VISIBLE) {
                    holder.line_options.setVisibility(View.GONE);
                } else {
                    holder.line_options.setVisibility(View.VISIBLE);
                }

            }
        });

    }
}