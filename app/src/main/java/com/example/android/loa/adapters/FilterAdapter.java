package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnSelectedFilter;
import com.example.android.loa.Interfaces.OnSelectedProductItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.FilterType;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.types.Constants;

import java.util.List;

public class FilterAdapter extends BaseAdapter<FilterType,FilterAdapter.ViewHolder> {
    private Context mContext;

    private OnSelectedFilter onSelectedFilter = null;

    private Integer prevPosOpenView;

    public void setOnselectedFilter(OnSelectedFilter lister) {
        onSelectedFilter = lister;
    }

    public FilterAdapter(Context context, List<FilterType> events) {
        setItems(events);
        mContext = context;
        prevPosOpenView=-1;
    }
    public void resetPrevOpenView(){
        prevPosOpenView=-1;
    }

    public FilterAdapter() {

    }

    public List<FilterType> getListStockEvents() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout circle;
        public ImageView selected;
        public TextView name;
        public TextView firstLetter;

        public ViewHolder(View v) {
            super(v);
            circle = v.findViewById(R.id.item);
            selected = v.findViewById(R.id.selected);
            name = v.findViewById(R.id.name);
            firstLetter = v.findViewById(R.id.firstLetter);

        }
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_filter, parent, false);
        FilterAdapter.ViewHolder vh = new FilterAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(FilterAdapter.ViewHolder vh) {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final FilterAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final FilterType current = getItem(position);

        holder.selected.setVisibility(View.GONE);

        holder.firstLetter.setText(String.valueOf(current.type.charAt(0)));
        holder.name.setText(current.type);

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

                if (onSelectedFilter != null) {
                    onSelectedFilter.onSelectedFilter(current.type);
                }
            }
        });
    }


}

