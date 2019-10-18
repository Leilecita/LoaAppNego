package com.example.android.loa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;
import com.example.android.loa.network.models.SpinnerModel;

import java.util.List;

public class ItemAdapterModel extends BaseAdapter<SpinnerModel,ItemAdapter.ViewHolder>  {
    private Context mContext;

    private OnSelectedItem onSelectedItem= null;

    public void setOnSelectedItem(OnSelectedItem lister){
        onSelectedItem=lister;
    }

    public ItemAdapterModel(Context context, List<SpinnerModel> events){
        setItems(events);
        mContext = context;
    }

    public ItemAdapterModel(){

    }



    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text;

        public ImageView circle;

        public ViewHolder(View v){
            super(v);
            text= v.findViewById(R.id.text);
            circle= v.findViewById(R.id.circle);

        }
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_product_grid,parent,false);
        ItemAdapter.ViewHolder vh = new ItemAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ItemAdapter.ViewHolder vh){

    }

    private Drawable getDrawableFirstLetter(SpinnerModel c){

        //get first letter of each String item
        String firstLetter = String.valueOf(c.model.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(c);
        //int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .endConfig()
                .buildRound(firstLetter, color);
        return drawable;
    }
    private Drawable getDrawableFirstLetterClose(String c,int color) {

        //get first letter of each String item
        String firstLetter = String.valueOf(c);
        ColorGenerator generator = ColorGenerator.DEFAULT; // or use DEFAULT
        // generate random color
        //int color = generator.getColor(c);
        //int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .endConfig()
                .buildRound(firstLetter, color);
        return drawable;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ItemAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final SpinnerModel current=getItem(position);

        holder.text.setText(current.model);

        if(current.model.equals("Nuevo")){
            holder.circle.setImageDrawable(getDrawableFirstLetterClose("+",R.color.word_clear2));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onSelectedItem!=null){
                        onSelectedItem.onSelectedItem("Nuevo","","model");
                    }
                }
            });
        }else{
            if(!current.model.equals(""))
            holder.circle.setImageDrawable(getDrawableFirstLetter(current));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onSelectedItem!=null){
                        onSelectedItem.onSelectedItem("",current.model,"model");
                    }
                }
            });




        }


    }



}