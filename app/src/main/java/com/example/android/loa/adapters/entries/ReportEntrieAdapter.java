package com.example.android.loa.adapters.entries;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.adapters.BaseAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportEntrie;

import com.example.android.loa.network.models.ReportStockEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportEntrieAdapter extends BaseAdapter<ReportEntrie,ReportEntrieAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private String mGroupBy="day";
    private String mItem="Todos";


    public ReportEntrieAdapter(Context context, List<ReportEntrie> sales){
        setItems(sales);
        mContext = context;
    }

    public void setGroupBy(String group){
        this.mGroupBy=group;
    }
    public void setItem(String item){
        this.mItem=item;
    }

    public ReportEntrieAdapter(){

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
                .inflate(R.layout.view_header_entries, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportEntrie e = getItem(position);

            String month = DateHelper.get().getNameMonth2(e.created).substring(0, 3);

            String numberDay = DateHelper.get().numberDay(e.created);

           // String card= ValuesHelper.get().getIntegerQuantityByLei(e.cardAmount);
            //String ef=ValuesHelper.get().getIntegerQuantityByLei(e.efectAmount);
            String cantArt=String.valueOf(e.countEntries);

            //primer linear
            int count = linear.getChildCount();
            View v = null;
            View v2 = null;
            View v3 = null;
            View v5 = null;
            View v6 = null;
            View v7 = null;
            View v8 = null;
            View v9 = null;
            View v10 = null;

            for (int k = 0; k < count; k++) {
                v3 = linear.getChildAt(k);

                //frame
                if (k == 0) {
                    FrameLayout linear4 = (FrameLayout) v3;

                    int count3 = linear4.getChildCount();

                    for (int i = 0; i < count3; i++) {

                        v = linear4.getChildAt(i);
                        if (i == 1) {

                            RelativeLayout rel2 = (RelativeLayout) v;
                            int countRel2 = rel2.getChildCount();

                            for (int r = 0; r < countRel2; r++) {

                                v5=rel2.getChildAt(r);
                                if (r == 0) {

                                    TextView t2 = (TextView) v5;
                                    t2.setText(month);

                                    if(mGroupBy.equals("day")){
                                        t2.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }else{
                                        t2.setGravity(Gravity.CENTER);
                                    }
                                }else if(r==1){
                                    TextView t = (TextView) v5;
                                    t.setText(numberDay);

                                    if(mGroupBy.equals("day")){
                                        t.setVisibility(View.VISIBLE);
                                    }else{
                                        t.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                    //Linear layout
                }else if(k==1){
                    LinearLayout line = (LinearLayout) v3;
                    int countline = line.getChildCount();
                    for(int o=0; o< countline; o++){
                        v7=line.getChildAt(o);
                        if(o==1){
                            TextView t4 = (TextView) v7;
                            t4.setText(cantArt);
                        }
                    }
                    //linear layout
                }
            }
        }
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
        public LinearLayout viewMore;


        public ViewHolder(View v){
            super(v);
            recylerEntries=v.findViewById(R.id.list_events);
            content=v.findViewById(R.id.content);
            viewMore=v.findViewById(R.id.view_more);
          //  countEntries=v.findViewById(R.id.entries);

          //  month=v.findViewById(R.id.month);
            //numberDay=v.findViewById(R.id.number_day);
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

    private void listEntries(String created,final ReportStockEventEntrieAdapter mAdapter){
        ApiClient.get().getStockeventsEntries(created, mItem, mGroupBy, new GenericCallback<List<ReportStockEvent>>() {
            @Override
            public void onSuccess(List<ReportStockEvent> data) {
                mAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportEntrieAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportEntrie current = getItem(position);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.recylerEntries.setLayoutManager(layoutManager);
        final ReportStockEventEntrieAdapter mAdapter = new ReportStockEventEntrieAdapter(mContext, new ArrayList<ReportStockEvent>());
       // mAdapter.setItems(current.listEntries);
        holder.recylerEntries.setAdapter(mAdapter);

        if(position==0){
            holder.viewMore.setVisibility(View.GONE);

            listEntries(current.created,mAdapter);
        }else{
            holder.viewMore.setVisibility(View.VISIBLE);
        }

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.viewMore.setVisibility(View.GONE);

                listEntries(current.created,mAdapter);

            }
        });

    }
}
//        holder.month.setText(DateHelper.get().getNameMonth2(current.created).substring(0,3));

      /*  if(mGroupBy.equals("day")){
            holder.numberDay.setText(DateHelper.get().numberDay(current.created));
            holder.numberDay.setVisibility(View.VISIBLE);
        }else{
            holder.numberDay.setText("");
            holder.numberDay.setVisibility(View.GONE);
        }

        holder.countEntries.setText(String.valueOf(current.countEntries));
*/