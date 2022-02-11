package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;

import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.GeneralStock;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.types.Constants;

import java.util.ArrayList;
import java.util.List;

public class GeneralStockEventAdapter extends BaseAdapter<GeneralStock, GeneralStockEventAdapter.ViewHolder> {
    private Context mContext;

    public GeneralStockEventAdapter(Context context, List<GeneralStock> events) {
        setItems(events);
        mContext = context;

    }
    public GeneralStockEventAdapter() {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView number;
        public TextView month;

        public TextView item;
        public TextView type;
        public TextView ideal_stock;
        public TextView real_stock;
        public ImageView result;
        public TextView dif;
        public TextView div;
        public ImageView imageButton;

        public RecyclerView listProd;
        public LinearLayout info;


        public ViewHolder(View v) {
            super(v);

            type = v.findViewById(R.id.type);
            item = v.findViewById(R.id.item);
            month = v.findViewById(R.id.month);
            number = v.findViewById(R.id.number);
            ideal_stock = v.findViewById(R.id.ideal_stock);
            real_stock = v.findViewById(R.id.real_stock);
            result = v.findViewById(R.id.result);
            dif = v.findViewById(R.id.dif);
            imageButton = v.findViewById(R.id.imagebutton);

            div = v.findViewById(R.id.div);
            listProd = v.findViewById(R.id.list_stock_events);
            info = v.findViewById(R.id.information);

        }
    }

    @Override
    public GeneralStockEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_general_stock, parent, false);
        GeneralStockEventAdapter.ViewHolder vh = new GeneralStockEventAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(GeneralStockEventAdapter.ViewHolder vh) {
        if (vh.type != null)
            vh.type.setText(null);
        if (vh.ideal_stock != null)
            vh.ideal_stock.setText(null);
        if (vh.real_stock != null)
            vh.real_stock.setText(null);

    }


    private void loadIcon(GeneralStockEventAdapter.ViewHolder holder, final String item) {

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item, Toast.LENGTH_SHORT).show();
            }
        });

        if (item.equals(Constants.ITEM_HOMBRE)) {
            holder.imageButton.setImageResource(R.drawable.bmancl);
        } else if (item.equals(Constants.ITEM_DAMA)) {
            holder.imageButton.setImageResource(R.drawable.bwomcl);
        } else if (item.equals(Constants.ITEM_ACCESORIO)) {
            holder.imageButton.setImageResource(R.drawable.bacccl);
        } else if (item.equals(Constants.ITEM_NINIO)) {
            holder.imageButton.setImageResource(R.drawable.bnincl);
        } else if (item.equals(Constants.ITEM_TECNICO)) {
            holder.imageButton.setImageResource(R.drawable.btecl);
        } else if (item.equals(Constants.ITEM_CALZADO)) {
            holder.imageButton.setImageResource(R.drawable.bcalcl);
        } else if (item.equals(Constants.ITEM_LUZ)) {
            holder.imageButton.setImageResource(R.drawable.bluzcl);
        } else if (item.equals(Constants.ITEM_OFERTA)) {
            holder.imageButton.setImageResource(R.drawable.bofercl);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final GeneralStockEventAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final GeneralStock current = getItem(position);

        loadIcon(holder, current.item);

        holder.month.setText((DateHelper.get().getNameMonth2(DateHelper.get().onlyDate(current.created))).substring(0,3));
        holder.number.setText(DateHelper.get().numberDay(current.created));

        holder.dif.setText(String.valueOf(current.difference));

        holder.type.setText(current.type+" "+current.brand);

        if(current.brand.equals("Todos")){
            final Typeface typeface = ResourcesCompat.getFont(mContext, R.font.opensansregular);
            holder.type.setTypeface(typeface);
        }else{
            final Typeface typeface = ResourcesCompat.getFont(mContext, R.font.opensanslight);
            holder.type.setTypeface(typeface);
        }

        if(current.result.equals("bien")){
            holder.result.setImageResource(R.drawable.correct);
        }else{
            holder.result.setImageResource(R.drawable.wrong);
        }

        holder.ideal_stock.setText(String.valueOf(current.ideal_stock));
        holder.real_stock.setText(String.valueOf(current.real_stock));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edithProductGeneralBalance(current,position);
                return false;
            }
        });

        /*final ProductAdapter productAdapter = new ProductAdapter(mContext, new ArrayList<Product>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.listProd.setLayoutManager(layoutManager);
        holder.listProd.setAdapter(productAdapter);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(productAdapter.getList().size() >0){

                }else{
                    getProductsByItemType(current,productAdapter);
                }

                if(holder.listProd.getVisibility() == View.VISIBLE){
                    holder.info.setVisibility(View.GONE);
                    holder.listProd.setVisibility(View.GONE);
                }else{
                    holder.listProd.setVisibility(View.VISIBLE);
                    holder.info.setVisibility(View.VISIBLE);
                }*/
            }
        });

    }

    private void getProductsByItemType(GeneralStock g, final ProductAdapter p){

        ApiClient.get().getProductsByItemType(g.item, g.type, new GenericCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                p.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }



    private void edithProductGeneralBalance(final GeneralStock g, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_edith_general_balance, null);
        builder.setView(dialogView);

        final TextView ideal_stock= dialogView.findViewById(R.id.ideal_stock);
        final EditText stock_real= dialogView.findViewById(R.id.real_stock);
        final TextView dif= dialogView.findViewById(R.id.dif);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        ideal_stock.setText(String.valueOf(g.ideal_stock));

        stock_real.setText(String.valueOf(g.real_stock));

        dif.setText(String.valueOf(g.difference));
        final AlertDialog dialog = builder.create();


        stock_real.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                if(!stock_real.getText().toString().trim().equals("") ){

                    Integer i=Integer.valueOf(stock_real.getText().toString().trim());
                    Integer i2=g.ideal_stock;

                    dif.setText(String.valueOf(i-i2));
                }else{

                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!stock_real.getText().toString().matches("")){
                    String stockT=stock_real.getText().toString().trim();
                    String difT=dif.getText().toString().trim();

                    String result;

                    if(Integer.valueOf(difT) == 0){
                        result="bien";
                    }else{
                        result="mal";
                    }

                    GeneralStock gToPut=new GeneralStock(g.item,g.type,g.ideal_stock,Integer.valueOf(stockT),result,Integer.valueOf(difT));
                    gToPut.id=g.id;

                    ApiClient.get().putGeneralStock(gToPut, new GenericCallback<GeneralStock>() {
                        @Override
                        public void onSuccess(GeneralStock data) {

                            updateItem(position,data);
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error","No se pudo modificar la operación",mContext);
                        }
                    });
                    dialog.dismiss();

                }else{
                    Toast.makeText(mContext,"El campo Stock real debe estar completo", Toast.LENGTH_LONG).show();
                }



            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}
