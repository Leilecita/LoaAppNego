package com.example.android.loa.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.models.ReportExtraction;
import com.example.android.loa.network.models.ReportPriceEvent;
import com.example.android.loa.network.models.ReportStockEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Date;
import java.util.List;

public class PriceEventAdapter extends BaseAdapter<ReportPriceEvent, PriceEventAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;

    public PriceEventAdapter(Context context, List<ReportPriceEvent> events) {
        setItems(events);
        mContext = context;
    }

    public PriceEventAdapter() {

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).created));
            return date.getDay();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_price_events, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportPriceEvent e = getItem(position);

            String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String numberDay = DateHelper.get().numberDay(e.created);

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
                }
            }
        }
    }

    public List<ReportPriceEvent> getListStockEvents() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item;
        public TextView type;
        public TextView brand;
        public TextView model;
        public TextView value;
        public TextView div;

        public ImageView imageButton;

        public TextView previous_price;
        public TextView user_name;
        public TextView time;
        public LinearLayout info_user;
        public LinearLayout line_percentage;
        public TextView percentage;


        public ViewHolder(View v) {
            super(v);

            type = v.findViewById(R.id.type);
            brand = v.findViewById(R.id.brand);
            imageButton = v.findViewById(R.id.imagebutton);
            model = v.findViewById(R.id.model);

            div = v.findViewById(R.id.div);

            value = v.findViewById(R.id.value);
            previous_price = v.findViewById(R.id.previous_price);
            user_name = v.findViewById(R.id.user_name);
            info_user = v.findViewById(R.id.info_user);
            time = v.findViewById(R.id.time);
            line_percentage = v.findViewById(R.id.line_percentage);
            percentage = v.findViewById(R.id.percentage);
        }
    }

    @Override
    public PriceEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_price_event, parent, false);
        PriceEventAdapter.ViewHolder vh = new PriceEventAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(PriceEventAdapter.ViewHolder vh) {
        if (vh.previous_price != null)
            vh.previous_price.setText(null);

        if (vh.value != null)
            vh.value.setText(null);
        if (vh.user_name != null)
            vh.user_name.setText(null);
        if (vh.time != null)
            vh.time.setText(null);
        if (vh.percentage != null)
            vh.percentage.setText(null);
    }


    private void loadIcon(PriceEventAdapter.ViewHolder holder, final String item) {

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item, Toast.LENGTH_SHORT).show();
            }
        });

        if (item.equals("Hombre")) {
            holder.imageButton.setImageResource(R.drawable.bmancl);
        } else if (item.equals("Dama")) {
            holder.imageButton.setImageResource(R.drawable.bwomcl);
        } else if (item.equals("Accesorio")) {
            holder.imageButton.setImageResource(R.drawable.bacccl);
        } else if (item.equals("Niño")) {
            holder.imageButton.setImageResource(R.drawable.bnincl);
        } else if (item.equals("Tecnico")) {
            holder.imageButton.setImageResource(R.drawable.btecl);
        } else if (item.equals("Calzado")) {
            holder.imageButton.setImageResource(R.drawable.bcalcl);
        } else if (item.equals("Luz")) {
            holder.imageButton.setImageResource(R.drawable.bluzcl);
        } else if (item.equals("Oferta")) {
            holder.imageButton.setImageResource(R.drawable.bofercl);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final PriceEventAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportPriceEvent current = getItem(position);

        if(current.percentage != 0){
            holder.line_percentage.setVisibility(View.VISIBLE);
            holder.percentage.setText(ValuesHelper.get().getIntegerQuantityWithoutPoint(current.percentage));
        }else{
            holder.line_percentage.setVisibility(View.GONE);
        }

        loadIcon(holder, current.item);

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(current.actual_price));
        holder.previous_price.setText(ValuesHelper.get().getIntegerQuantityByLei(current.previous_price));

        holder.type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, current.type, Toast.LENGTH_SHORT).show();
            }
        });

        if (current.model.equals("")) {
            holder.model.setText("-");
        } else {
            holder.model.setText(current.model);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.info_user.getVisibility() == View.VISIBLE){
                    holder.info_user.setVisibility(View.GONE);
                }else{
                    holder.info_user.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.type.setText(current.type);
        holder.brand.setText(current.brand);
        holder.user_name.setText(current.user_name);
        holder.time.setText(DateHelper.get().onlyHourMinut(DateHelper.get().getOnlyTime(current.created)));

    }

}