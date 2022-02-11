package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.types.Constants;

import java.util.List;

public class ItemProductAdapter extends BaseAdapter<SpinnerItem,ItemProductAdapter.ViewHolder> {
    private Context mContext;

    private OnSelectedProductItem onSelectedProductItem = null;

    private Integer prevPosOpenView;

    public void setOnSelectedProductItem(OnSelectedProductItem lister) {
        onSelectedProductItem = lister;
    }

    public ItemProductAdapter(Context context, List<SpinnerItem> events) {
        setItems(events);
        mContext = context;
        prevPosOpenView=-1;
    }
    public void resetPrevOpenView(){
        prevPosOpenView=-1;
    }

    public ItemProductAdapter() {

    }

    public List<SpinnerItem> getListStockEvents() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView circle;
        public ImageView selected;

        public ViewHolder(View v) {
            super(v);
            circle = v.findViewById(R.id.item);
            selected = v.findViewById(R.id.selected);

        }
    }

    @Override
    public ItemProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_prod_adapter, parent, false);
        ItemProductAdapter.ViewHolder vh = new ItemProductAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ItemProductAdapter.ViewHolder vh) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ItemProductAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final SpinnerItem current = getItem(position);

        holder.selected.setVisibility(View.GONE);
        holder.circle.setImageResource(current.resId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(prevPosOpenView!=-1){
                        if(prevPosOpenView!=position){
                            updateItem(prevPosOpenView,getItem(prevPosOpenView));
                        }
                    }

                    holder.selected.setVisibility(View.VISIBLE);
                    prevPosOpenView=position;

                    if (onSelectedProductItem != null) {
                        onSelectedProductItem.onSelectedProductItem(current.item);
                    }
            }
        });
    }

    private void loadSelected(ViewHolder holder,SpinnerItem current){
        holder.selected.setVisibility(View.VISIBLE);

    }

    private void loadClear(ViewHolder holder,SpinnerItem current){
        holder.selected.setVisibility(View.GONE);
        if(current.item.equals(Constants.ITEM_HOMBRE)){
            holder.circle.setImageResource(R.drawable.bmancl);
        }else if(current.item.equals(Constants.ITEM_TODOS)){
            holder.circle.setImageResource(R.drawable.ballcl);
        }else if(current.item.equals(Constants.ITEM_DAMA)){
            holder.circle.setImageResource(R.drawable.bwomcl);
        }else if(current.item.equals(Constants.ITEM_NINIO)){
            holder.circle.setImageResource(R.drawable.bnincl);
        }else if(current.item.equals(Constants.ITEM_ACCESORIO)){
            holder.circle.setImageResource(R.drawable.bacccl);
        }else if(current.item.equals(Constants.ITEM_TECNICO)){
            holder.circle.setImageResource(R.drawable.btecl);
        }else if(current.item.equals(Constants.ITEM_CALZADO)){
            holder.circle.setImageResource(R.drawable.bcalcl);
        }else if(current.item.equals(Constants.ITEM_OFERTA)){
            holder.circle.setImageResource(R.drawable.bofercl);
        }else if(current.item.equals(Constants.ITEM_LUZ)){
            holder.circle.setImageResource(R.drawable.bluzcl);
        }

    }
}

