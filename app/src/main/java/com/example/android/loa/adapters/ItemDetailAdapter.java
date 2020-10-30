package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.ReportDetail;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailAdapter extends BaseAdapter<ReportDetail, ItemDetailAdapter.ViewHolder>  {
    private Context mContext;

    private OnSelectedItem onSelectedItem= null;

    private List<String> selected_details;


    public void setOnSelectedItem(OnSelectedItem lister){
        onSelectedItem=lister;
    }

    public ItemDetailAdapter(Context context, List<ReportDetail> events){
        setItems(events);
        mContext = context;
        selected_details = new ArrayList<>();
    }

    public List<String> getSelectedDetails(){
        return this.selected_details;
    }

    public ItemDetailAdapter(){

    }

    public List<ReportDetail> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text;

        public ImageView circle;

        public ViewHolder(View v){
            super(v);
            text= v.findViewById(R.id.text);

        }
    }

    @Override
    public ItemDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_detail_adapter,parent,false);
        ItemDetailAdapter.ViewHolder vh = new ItemDetailAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ItemDetailAdapter.ViewHolder vh){

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ItemDetailAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportDetail current=getItem(position);

        holder.text.setText(current.detail);

        if(current.selected){ // seleccionado para sacar
            holder.text.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(current.selected){
                    current.selected = false;
                    removeSelectedDetail(current.detail);
                    holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                }else{
                    current.selected = true;
                    selected_details.add(current.detail);
                    holder.text.setTextColor(mContext.getResources().getColor(R.color.loa_red));
                }
                if(onSelectedItem!=null){
                    onSelectedItem.onSelectedItem(current.detail,"","detail");
                }
            }
        });
    }

    public void unSelectAll(){
        for(int i=0; i < getList().size(); ++i){
                getList().get(i).selected = false;
                notifyDataSetChanged();
        }
    }

    private void removeSelectedDetail(String id){
        for(int i=0; i < selected_details.size(); ++i){
            if(id == selected_details.get(i)){
                selected_details.remove(i);
            }
        }
    }

}
