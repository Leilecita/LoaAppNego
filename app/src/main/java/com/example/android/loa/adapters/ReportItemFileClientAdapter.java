package com.example.android.loa.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.R;

import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.models.ReportItemFileClientEvent;

import java.util.List;

public class ReportItemFileClientAdapter  extends BaseAdapter<ReportItemFileClientEvent,ReportItemFileClientAdapter.ViewHolder> {

    private Context mContext;

    private String item_date="";

    public ReportItemFileClientAdapter(Context context, List<ReportItemFileClientEvent> events){
        setItems(events);
        mContext = context;
    }

    public ReportItemFileClientAdapter(){

    }

    public List<ReportItemFileClientEvent> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView description;
        public TextView detail;
        public TextView value;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name);
            description= v.findViewById(R.id.description);
            value= v.findViewById(R.id.value);
            detail= v.findViewById(R.id.detail);
        }
    }

    @Override
    public ReportItemFileClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_file_client,parent,false);
        ReportItemFileClientAdapter.ViewHolder vh = new ReportItemFileClientAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ReportItemFileClientAdapter.ViewHolder vh){

        if(vh.name!=null)
            vh.name.setText(null);
        if(vh.value!=null)
            vh.value.setText(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportItemFileClientAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportItemFileClientEvent current=getItem(position);

        if(current.value>0){
            holder.detail.setText("A cta");
        }else{
            if(current.retired_product.equals("true")){
                holder.detail.setText("Sale producto");
            }else {
                holder.detail.setText("Sale ");
            }
        }

        holder.name.setText(current.name);
        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(current.value));

        if(current.payment_method.equals("efectivo")){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }else{
            holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
            holder.value.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}
