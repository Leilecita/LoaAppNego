package com.example.android.loa.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.ReportDetail;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailAdapter extends BaseAdapter<ReportDetail, ItemDetailAdapter.ViewHolder>  {
    private Context mContext;

    private OnSelectedItem onSelectedItem= null;

    private List<String> selected_details_not_to_see;
    private List<String> selected_details_to_see;

    private Boolean type_view_to_see;


    public void setOnSelectedItem(OnSelectedItem lister){
        onSelectedItem=lister;
    }

    public ItemDetailAdapter(Context context, List<ReportDetail> events){
        setItems(events);
        mContext = context;
        selected_details_not_to_see = new ArrayList<>();
        selected_details_to_see = new ArrayList<>();
        type_view_to_see = true;
    }

    public List<String> getSelectedDetailsNotToSee(){
        return this.selected_details_not_to_see;
    }

    public List<String> getSelectedDetailsToSee(){
        return this.selected_details_to_see;
    }

    public void setTypeViewToSee(Boolean val){
        this.type_view_to_see = val;
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
            holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

            holder.text.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensansregular));
        }else{
            holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryClearLetter));
            holder.text.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensanslight));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(current.detail.equals("Todos")){
                    selected_details_to_see.clear();
                    selected_details_not_to_see.clear();
                }else{

                    if(current.selected){

                        current.selected = false;
                        if(type_view_to_see){
                            removeSelectedDetailToSee(current.detail);
                        }else{
                            removeSelectedDetailNotToSee(current.detail);
                        }

                        holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryClearLetter));
                        holder.text.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensanslight));
                    }else{
                        current.selected = true;
                        if(type_view_to_see){
                            selected_details_to_see.add(current.detail);
                        }else{
                            selected_details_not_to_see.add(current.detail);
                        }

                        holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                        holder.text.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensansregular));
                    }
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



    private void removeSelectedDetailNotToSee(String id){
        for(int i = 0; i < selected_details_not_to_see.size(); ++i){
            if(id == selected_details_not_to_see.get(i)){
                selected_details_not_to_see.remove(i);
            }
        }
    }

    private void removeSelectedDetailToSee(String id){
        for(int i = 0; i < selected_details_to_see.size(); ++i){
            if(id == selected_details_to_see.get(i)){
                selected_details_to_see.remove(i);
            }
        }
    }

}
