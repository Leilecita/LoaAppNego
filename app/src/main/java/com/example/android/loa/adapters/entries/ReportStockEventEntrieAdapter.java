package com.example.android.loa.adapters.entries;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;

import com.example.android.loa.adapters.BaseAdapter;
import com.example.android.loa.network.models.ReportStockEvent;

import java.util.List;

public class ReportStockEventEntrieAdapter extends BaseAdapter<ReportStockEvent, ReportStockEventEntrieAdapter.ViewHolder> {
    private Context mContext;
    private String item_date="";

    public ReportStockEventEntrieAdapter(Context context, List<ReportStockEvent> events){
        setItems(events);
        mContext = context;

    }


    public ReportStockEventEntrieAdapter(){

    }

    public List<ReportStockEvent> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
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

        public TextView cant_stock_out;
        public ImageView imageButton;


        public ViewHolder(View v){
            super(v);

            type= v.findViewById(R.id.type);
            brand= v.findViewById(R.id.brand);
            date= v.findViewById(R.id.date);
            year= v.findViewById(R.id.year);
            value= v.findViewById(R.id.value);

            line_edit= v.findViewById(R.id.line_edit);
            value_edit= v.findViewById(R.id.value_edit);
            date_edit= v.findViewById(R.id.date_edit);
            payment_method= v.findViewById(R.id.payment_method);
            detail= v.findViewById(R.id.detail);
            date_edit= v.findViewById(R.id.date_edit);
            done= v.findViewById(R.id.done);
            close= v.findViewById(R.id.close);

            select_payment_method= v.findViewById(R.id.select_payment_method);
            check_ef= v.findViewById(R.id.check_ef);
            check_deb= v.findViewById(R.id.check_deb);
            check_card= v.findViewById(R.id.check_card);

            cant_stock_out= v.findViewById(R.id.cant_stock_out);

            imageButton= v.findViewById(R.id.imagebutton);
            model= v.findViewById(R.id.model);
            div= v.findViewById(R.id.div);
        }
    }

    @Override
    public ReportStockEventEntrieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_report_stock_event,parent,false);
        ReportStockEventEntrieAdapter.ViewHolder vh = new ReportStockEventEntrieAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ReportStockEventEntrieAdapter.ViewHolder vh){
        if(vh.cant_stock_out!=null)
            vh.cant_stock_out.setText(null);
        if(vh.stock_out!=null)
            vh.stock_out.setText(null);

        if(vh.date!=null)
            vh.date.setText(null);

        if(vh.year!=null)
            vh.year.setText(null);

        if(vh.value!=null)
            vh.value.setText(null);

    }


    private void loadIcon(ViewHolder holder,final String item){

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,item,Toast.LENGTH_SHORT).show();
            }
        });

        if(item.equals("Hombre")){
            holder.imageButton.setImageResource(R.drawable.bmancl);

            // holder.color.getBackground().setColorFilter(mContext.getResources().getColor(R.color.hombre), PorterDuff.Mode.SRC_ATOP);
            // holder.image.setImageResource(R.drawable.man);
        }else if(item.equals("Dama")){
            holder.imageButton.setImageResource(R.drawable.bwomcl);
        }else if(item.equals("Accesorio")){
            holder.imageButton.setImageResource(R.drawable.bacccl);
        }else if(item.equals("Niño")){
            holder.imageButton.setImageResource(R.drawable.bnincl);
        }else if(item.equals("Tecnico")){
            holder.imageButton.setImageResource(R.drawable.btecl);
        }else if(item.equals("Calzado")){
            holder.imageButton.setImageResource(R.drawable.bcalcl);
        }else if(item.equals("Luz")){
            holder.imageButton.setImageResource(R.drawable.bluzcl);
        }else if(item.equals("Oferta")){
            holder.imageButton.setImageResource(R.drawable.bofercl);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportStockEventEntrieAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportStockEvent current = getItem(position);

        loadIcon(holder, current.item);

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(current.value));

        holder.cant_stock_out.setText(String.valueOf(current.stock_out));

        if (current.detail.equals("Ingreso compra")) {
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.line_edit.getVisibility() == View.GONE){
                    holder.line_edit.setVisibility(View.VISIBLE);
                    holder.div.setVisibility(View.GONE);

                }else{
                    holder.line_edit.setVisibility(View.GONE);
                    holder.div.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.detail.setText(current.detail);
        holder.date_edit.setText(DateHelper.get().getOnlyDate(current.stock_event_created));
    }
}
