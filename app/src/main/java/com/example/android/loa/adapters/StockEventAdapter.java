package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;

import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.StockEvent;

import java.util.Date;
import java.util.List;


public class StockEventAdapter extends BaseAdapter<StockEvent,StockEventAdapter.ViewHolder>  {
    private Context mContext;

    public StockEventAdapter(Context context, List<StockEvent> events){
        setItems(events);
        mContext = context;
    }

    public StockEventAdapter(){

    }

    public List<StockEvent> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView actual_stock;
        public TextView stock_in;
        public TextView stock_out;
        public TextView ideal_stock;
        public TextView balance_stock;
        public TextView dif;
        public TextView date;
        public TextView year;


        public ViewHolder(View v){
            super(v);
            actual_stock= v.findViewById(R.id.stock_ant);
            stock_in= v.findViewById(R.id.stock_in);
            stock_out= v.findViewById(R.id.stock_out);
            balance_stock= v.findViewById(R.id.balance_stock);
            ideal_stock= v.findViewById(R.id.ideal_stock);
            date= v.findViewById(R.id.date);
            year= v.findViewById(R.id.year);
            dif= v.findViewById(R.id.dif);
        }
    }

    @Override
    public StockEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_stock_event,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(StockEventAdapter.ViewHolder vh){
        if(vh.stock_in!=null)
            vh.stock_in.setText(null);
        if(vh.stock_out!=null)
            vh.stock_out.setText(null);
        if(vh.actual_stock!=null)
            vh.actual_stock.setText(null);
        if(vh.balance_stock!=null)
            vh.balance_stock.setText(null);
        if(vh.ideal_stock!=null)
            vh.ideal_stock.setText(null);
        if(vh.date!=null)
            vh.date.setText(null);
        if(vh.year!=null)
            vh.year.setText(null);
        if(vh.dif!=null)
            vh.dif.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final StockEvent current=getItem(position);

             holder.stock_out.setText(String.valueOf(current.stock_out));
        if(current.stock_out !=0){
            holder.stock_out.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }

        holder.stock_in.setText(String.valueOf(current.stock_in));
        if(current.stock_in != 0){
            holder.stock_in.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }

        holder.ideal_stock.setText(String.valueOf(current.ideal_stock));

        holder.actual_stock.setText(String.valueOf(current.stock_ant));
        if(current.stock_ant != 0){
            holder.actual_stock.setTypeface(holder.stock_in.getTypeface(), Typeface.BOLD);
        }


        if(current.balance_stock == null){

            holder.balance_stock.setText("*");
            holder.balance_stock.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            holder.balance_stock.setText(String.valueOf(current.balance_stock));
            Integer diff=current.balance_stock-current.ideal_stock;

            String text=(diff > 0) ? "+" : "";
            holder.dif.setText(text+String.valueOf(diff));
            holder.stock_in.setText("*");
            holder.stock_out.setText("*");
            holder.actual_stock.setText("*");

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_info_stock_event, null);
                builder.setView(dialogView);


                final TextView detail= dialogView.findViewById(R.id.detail);
                detail.setText(current.detail);

                final AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });

        final String dateToShow=DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(current.created));

        holder.date.setText(DateHelper.get().onlyDayMonth(dateToShow));
        holder.year.setText(DateHelper.get().getOnlyYear(dateToShow));

    }

    private void balance(final StockEvent currentStockEvent,final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.update_balance, null);
        builder.setView(dialogView);


        final TextView stock_real= dialogView.findViewById(R.id.real_stock_balance);

        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);


        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockP=stock_real.getText().toString().trim();

                if(!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)){
                    currentStockEvent.balance_stock=Integer.valueOf(stockP);

                    ApiClient.get().putStockEvent(currentStockEvent, new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {

                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," No se pudo guardar el balance",dialogView.getContext());
                        }
                    });
                    updateItem(position,currentStockEvent);
                    dialog.dismiss();
                }



            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}

