package com.example.android.loa.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.R;

import com.example.android.loa.network.models.SpinnerData;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends BaseAdapter<SpinnerData,ItemAdapter.ViewHolder>  {
    private Context mContext;

    private OnSelectedItem onSelectedItem= null;

    private ArrayList<String> mColorsBrand;

    public void setOnSelectedItem(OnSelectedItem lister){
        onSelectedItem=lister;
    }

    public ItemAdapter(Context context, List<SpinnerData> events){
        setItems(events);
        mContext = context;

        mColorsBrand=new ArrayList<>();
    }

    public ItemAdapter(){

    }

    public List<SpinnerData> getListStockEvents(){
        return getList();
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

    private Drawable getDrawableFirstLetter(SpinnerData c,int colorSave){

        //get first letter of each String item
        String firstLetter = String.valueOf(c.brand.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(c);

       // System.out.println(String.format("#%06X", (0xFFFFFF & color)));

        //int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .endConfig()
                .buildRound(firstLetter, colorSave);
        return drawable;
    }
    private Drawable getDrawableFirstLetterClose(String c,int color){

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
                .buildRound(firstLetter,color);
        return drawable;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ItemAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final SpinnerData current=getItem(position);

        holder.text.setText(current.brand);

      if(current.brand.equals("Nuevo")){
            holder.circle.setImageDrawable(getDrawableFirstLetterClose("+",R.color.word_clear2));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onSelectedItem!=null){
                        onSelectedItem.onSelectedItem("Nuevo","","brand");
                    }
                }
            });
        }else{
           // holder.circle.setImageDrawable(getDrawableFirstLetter(current, Color.parseColor(mColorsBrand.get(position))));
            holder.circle.setImageDrawable(getDrawableFirstLetter(current, Color.parseColor(current.color)));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedItem!=null){
                    onSelectedItem.onSelectedItem(current.brand,"","brand");
                }
            }
        });
    }

    private void loadColors(){

        mColorsBrand.add("#E57373");
        mColorsBrand.add("#4DD0E1");
        mColorsBrand.add("#64B5F6");

        mColorsBrand.add("#D4E157");
        mColorsBrand.add("#FFB74D");
        mColorsBrand.add("#FF8A65");
        mColorsBrand.add("#9575CD");

        mColorsBrand.add("#F06292");
        mColorsBrand.add("#90A4AE");
        mColorsBrand.add("#9575CD");
        mColorsBrand.add("#FFD54F");
        mColorsBrand.add("#4DB6AC");

        mColorsBrand.add("#9575CD");
        mColorsBrand.add("#64B5F6");
        mColorsBrand.add("#90A4AE");
        mColorsBrand.add("#81C784");
        mColorsBrand.add("#FF8A65");

        mColorsBrand.add("#4FC3F7");
        mColorsBrand.add("#BA68C8");
        mColorsBrand.add("#4DD0E1");
        mColorsBrand.add("#7986CB");

    }



}
