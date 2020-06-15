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
import com.example.android.loa.network.models.Item_employee;
import com.example.android.loa.network.models.ReportItemEmployee;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportHourEmployeeAdapter  extends BaseAdapter<ReportItemEmployee,ReportHourEmployeeAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;


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
                .inflate(R.layout.view_header_hours, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportItemEmployee e = getItem(position);

            String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String year = DateHelper.get().getYear(e.created);


            long h = Math.round(e.amountMonth);
            String text = getHourMinutes(h);

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
                            t2.setText(year);

                        }
                    }
                } else if (k == 2) {
                    TextView t2 = (TextView) v3;
                    t2.setText(text);
                }
            }
        }
    }

    private String getHourMinutes(long minutes){

        int hoursf= (int)minutes / 60;
        int minutesf= (int) minutes % 60;

        return String.valueOf(hoursf)+"."+String.valueOf(minutesf);
    }


    public ReportHourEmployeeAdapter(Context context, List<ReportItemEmployee> reports) {
        setItems(reports);
        mContext = context;
    }


    public ReportHourEmployeeAdapter() {
    }

    public List<ReportItemEmployee> getListEmployees() {
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
    public ReportHourEmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_report_extraction, parent, false);
        ReportHourEmployeeAdapter.ViewHolder vh = new ReportHourEmployeeAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ReportHourEmployeeAdapter.ViewHolder vh) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportHourEmployeeAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final ReportItemEmployee current = getItem(position);

        HourEmployeeAdapter hourEmployeeAdapter = new HourEmployeeAdapter(mContext, new ArrayList<Item_employee>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.listExtr.setLayoutManager(layoutManager);
        holder.listExtr.setAdapter(hourEmployeeAdapter);
        hourEmployeeAdapter.setItems(current.listItemsFile);

    }
}
