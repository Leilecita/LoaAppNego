package com.example.android.loa.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;

import com.example.android.loa.network.models.ReportStockEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Date;
import java.util.List;

public class StockEventAdapter  extends BaseAdapter<ReportStockEvent, StockEventAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;

    private String item_date = "";

    public StockEventAdapter(Context context, List<ReportStockEvent> events) {
        setItems(events);
        mContext = context;

    }
    public StockEventAdapter() {

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).stock_event_created));
            return date.getDay();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_event_day, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportStockEvent e = getItem(position);

            String month = DateHelper.get().getNameMonth2(e.stock_event_created).substring(0, 3);

            String numberDay = DateHelper.get().numberDay(e.stock_event_created);

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

                                   /* if(mGroupBy.equals("day")){
                                        t2.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }else{
                                        t2.setGravity(Gravity.CENTER);
                                    }*/
                                }else if(r==1){
                                    TextView t = (TextView) v5;
                                    t.setText(numberDay);

                                    /*if(mGroupBy.equals("day")){
                                        t.setVisibility(View.VISIBLE);
                                    }else{
                                        t.setVisibility(View.GONE);
                                    }*/
                                }
                            }
                        }
                    }
                    //Linear layout
                }

            }
        }
    }

    public List<ReportStockEvent> getListStockEvents() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // public TextView stock_in;
        public TextView stock_out;
        public TextView date;
        public TextView year;

        public TextView item;
        public TextView type;
        public TextView brand;
        public TextView model;
        public TextView value;
        public TextView div;


        public LinearLayout line_edit;
        public EditText value_edit;
        public TextView date_edit;
        public TextView payment_method;
        public TextView detail;
        public ImageView done;
        public ImageView close;

        public LinearLayout select_payment_method;
        public CheckBox check_ef;
        public CheckBox check_deb;
        public CheckBox check_card;
        public CheckBox check_transf;
        public CheckBox check_merc_pago;

        public TextView cant_stock_out;
        public TextView cant_stock_in;
        public ImageView imageButton;
        public TextView detailup;
        public TextView obs;


        public ViewHolder(View v) {
            super(v);

            type = v.findViewById(R.id.type);
            brand = v.findViewById(R.id.brand);
            date = v.findViewById(R.id.date);
            year = v.findViewById(R.id.year);
            value = v.findViewById(R.id.value);

            line_edit = v.findViewById(R.id.line_edit);
            value_edit = v.findViewById(R.id.value_edit);
            date_edit = v.findViewById(R.id.date_edit);
            payment_method = v.findViewById(R.id.payment_method);
            detail = v.findViewById(R.id.detail);
            detailup = v.findViewById(R.id.detailup);
            date_edit = v.findViewById(R.id.date_edit);
            done = v.findViewById(R.id.done);
            close = v.findViewById(R.id.close);

            select_payment_method = v.findViewById(R.id.select_payment_method);
            check_ef = v.findViewById(R.id.check_ef);
            check_deb = v.findViewById(R.id.check_deb);
            check_card = v.findViewById(R.id.check_card);

            cant_stock_out = v.findViewById(R.id.cant_stock_out);

            imageButton = v.findViewById(R.id.imagebutton);
            model = v.findViewById(R.id.model);
            div = v.findViewById(R.id.div);
            obs = v.findViewById(R.id.observation);
        }
    }

    @Override
    public StockEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_stock_event_day, parent, false);
        StockEventAdapter.ViewHolder vh = new StockEventAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(StockEventAdapter.ViewHolder vh) {
        if (vh.cant_stock_out != null)
            vh.cant_stock_out.setText(null);
        if (vh.stock_out != null)
            vh.stock_out.setText(null);

        if (vh.date != null)
            vh.date.setText(null);

        if (vh.year != null)
            vh.year.setText(null);

        if (vh.value != null)
            vh.value.setText(null);

    }


    private void loadIcon(StockEventAdapter.ViewHolder holder, final String item) {

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
    public void onBindViewHolder(final StockEventAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportStockEvent current = getItem(position);


        loadIcon(holder, current.item);

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(current.value));

        holder.cant_stock_out.setText(String.valueOf(current.stock_out));
        holder.detailup.setText(current.detail);

        if (current.detail.equals("Ingreso dev")) {
            holder.cant_stock_out.setText("+" + current.stock_in);
            holder.value.setVisibility(View.INVISIBLE);
        } else {
            holder.value.setVisibility(View.VISIBLE);
        }


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

        holder.type.setText(current.type);
        holder.brand.setText(current.brand);
        holder.obs.setText(current.observation);

        if (current.payment_method.equals("efectivo")) {
        } else {
            holder.value.setTypeface(Typeface.DEFAULT_BOLD);
        }

        holder.value_edit.setText(String.valueOf(current.value));
        holder.date_edit.setText(DateHelper.get().getOnlyDate(current.stock_event_created));

        item_date = current.stock_event_created;

        holder.detail.setText(current.detail);

        holder.payment_method.setText(current.payment_method);

       /* holder.payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.select_payment_method.getVisibility() == View.GONE) {
                    holder.select_payment_method.setVisibility(View.VISIBLE);
                } else {
                    holder.select_payment_method.setVisibility(View.GONE);
                }
            }
        });*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.line_edit.getVisibility() == View.GONE) {
                    holder.line_edit.setVisibility(View.VISIBLE);
                    holder.div.setVisibility(View.GONE);

                } else {
                    holder.line_edit.setVisibility(View.GONE);
                    holder.div.setVisibility(View.VISIBLE);
                }


            }
        });

        if (current.client_name != null) {
            holder.value.setText(current.client_name);
        }
    }

}