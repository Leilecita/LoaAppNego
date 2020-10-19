package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.Interfaces.OnSelectedItem;
import com.example.android.loa.Interfaces.OnSelectedProduct;
import com.example.android.loa.R;

import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportProduct;

import java.util.ArrayList;
import java.util.List;

public class PriceManagerAdapter extends BaseAdapter<ReportProduct,PriceManagerAdapter.ViewHolder>  {
    private Context mContext;
    private OnChangeViewStock onChangeViewStock= null;

    private List<Long> selected_products;

    private OnSelectedProduct onSelectedProduct= null;

    public void setOnSelectedProduct(OnSelectedProduct lister){
        onSelectedProduct=lister;
    }


    public PriceManagerAdapter(Context context, List<ReportProduct> products){
        setItems(products);
        mContext = context;
        selected_products = new ArrayList<>();
    }

    public List<Long> getSelectedProducts(){
        return this.selected_products;
    }

    public void clearSelectedProducts(){
        this.selected_products.clear();
    }

    public PriceManagerAdapter(){

    }

    public List<ReportProduct> getListProduct(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView type;
        public TextView brand;
        public TextView model;
        public TextView stock;

        public LinearLayout line_options;

        public RelativeLayout edith;

        public Button ok;
        public TextView date;
        public RelativeLayout item;

        public EditText price_product;
        public ImageView imageButton;
        public TextView observation;

        public TextView original_product_price;
        public TextView previous_price;

        public ViewHolder(View v){
            super(v);
            type= v.findViewById(R.id.type);
            brand= v.findViewById(R.id.brand);
            stock= v.findViewById(R.id.stock);

            line_options= v.findViewById(R.id.line_options);

            item= v.findViewById(R.id.item);
            price_product= v.findViewById(R.id.price_product);
            model= v.findViewById(R.id.model);
            imageButton= v.findViewById(R.id.imagebutton);
            edith= v.findViewById(R.id.edith);

            original_product_price= v.findViewById(R.id.original_product_price);
            previous_price= v.findViewById(R.id.previous_price);
        }
    }

    @Override
    public PriceManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_price_manager,parent,false);
        PriceManagerAdapter.ViewHolder vh = new PriceManagerAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(PriceManagerAdapter.ViewHolder vh){
        if(vh.type!=null)
            vh.type.setText(null);
        if(vh.brand!=null)
            vh.brand.setText(null);
        if(vh.stock!=null)
            vh.stock.setText(null);
        if(vh.model!=null)
            vh.model.setText(null);
        if(vh.previous_price!=null)
            vh.previous_price.setText(null);
        if(vh.original_product_price!=null)
            vh.original_product_price.setText(null);

    }

    //private void loadIcon(ViewHolder holder,final String item){
    private void loadIcon(ImageView imageButton,final String item){

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,item,Toast.LENGTH_SHORT).show();
            }
        });

        if(item.equals("Hombre")){
            imageButton.setImageResource(R.drawable.bmancl);
        }else if(item.equals("Dama")){
            imageButton.setImageResource(R.drawable.bwomcl);
        }else if(item.equals("Accesorio")){
            imageButton.setImageResource(R.drawable.bacccl);
        }else if(item.equals("Niño")){
            imageButton.setImageResource(R.drawable.bnincl);
        }else if(item.equals("Tecnico")){
            imageButton.setImageResource(R.drawable.btecl);
        }else if(item.equals("Calzado")){
            imageButton.setImageResource(R.drawable.bcalcl);
        }else if(item.equals("Luz")){
            imageButton.setImageResource(R.drawable.bluzcl);
        }else if(item.equals("Oferta")){
            imageButton.setImageResource(R.drawable.bofercl);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final PriceManagerAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportProduct currentProduct=getItem(position);

        holder.brand.setText(currentProduct.brand);


        holder.original_product_price.setText(String.valueOf(currentProduct.price));
        holder.previous_price.setText(String.valueOf(currentProduct.previous_price));

        holder.original_product_price.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edithProductPrice(currentProduct,position,holder);
                return false;
            }

        });

        holder.model.setText(currentProduct.model);
        holder.type.setText(currentProduct.type);
        holder.stock.setText(String.valueOf(currentProduct.stock));

        loadIcon(holder.imageButton,currentProduct.item);
        optionsItem(holder,currentProduct,position);

        if(currentProduct.isSelected || checkExist(currentProduct.product_id)){
            holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.clearhintletter));
        }else{
            holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.background));
        }

        holder.line_options.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentProduct.isSelected){
                    currentProduct.isSelected = false;
                    holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.background));
                    removeSelectedProduct(currentProduct.product_id);
                }else{
                    currentProduct.isSelected = true;
                    selected_products.add(currentProduct.product_id);
                    holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.clearhintletter));
                }

                if(onSelectedProduct!=null){
                    onSelectedProduct.onSelectedProduct(selected_products.size());
                }
            }
        });
    }

    private void removeSelectedProduct(Long id){
        for(int i=0; i < selected_products.size(); ++i){
            if(id == selected_products.get(i)){
                selected_products.remove(i);
            }
        }
    }

    public void selectAll(){
        for(int i=0; i < getList().size(); ++i){
           if(!getList().get(i).isSelected ){
               getList().get(i).isSelected = true;
               selected_products.add(getList().get(i).product_id);
               notifyDataSetChanged();
           }
        }
    }

    public void unSelectAll(){
        for(int i=0; i < getList().size(); ++i){
            if(getList().get(i).isSelected ){
                getList().get(i).isSelected = false;
                notifyDataSetChanged();
            }
            selected_products.clear();
        }
    }


    private Boolean checkExist(Long id){
        for(int i=0; i < selected_products.size(); ++i){
            if(id == selected_products.get(i)){
                return true;
            }
        }
        return false;
    }

    private void closeItem(PriceManagerAdapter.ViewHolder holder){
        holder.edith.setVisibility(View.VISIBLE);
        if(onChangeViewStock!=null){
            onChangeViewStock.OnChangeViewStock();
        }
    }
    private void optionsItem(final PriceManagerAdapter.ViewHolder holder, final ReportProduct p, final Integer position){

        holder.edith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithProductPrice(p,position,holder);
            }
        });
    }

    public void updatePrice(Long id, Double d){
        Product p = new Product(id,d);
        ApiClient.get().putProduct(p, new GenericCallback<Product>() {
            @Override
            public void onSuccess(Product data) {

            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void edithProductPrice(final ReportProduct p, final int position, final PriceManagerAdapter.ViewHolder holder){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.edith_product, null);
        builder.setView(dialogView);

        final EditText price = dialogView.findViewById(R.id.price);
        final ImageView im = dialogView.findViewById(R.id.imagebutton);
        final RelativeLayout delete = dialogView.findViewById(R.id.delete);

        loadIcon(im,p.item);

        final TextView original_price = dialogView.findViewById(R.id.original_product_price);
        final TextView type = dialogView.findViewById(R.id.type);
        final TextView brand = dialogView.findViewById(R.id.brand);
        final TextView model = dialogView.findViewById(R.id.model);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        model.setText(p.model);
        brand.setText(p.brand);
        type.setText(p.type);
        original_price.setText(String.valueOf(p.price));

        price.setText(String.valueOf(p.price));
        price.setSelection(price.getText().length());
        delete.setVisibility(View.GONE);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!price.getText().toString().trim().matches("")){

                    Product edithedProduct = new Product(p.product_id, Double.valueOf(price.getText().toString().trim()));
                    ApiClient.get().putProduct(edithedProduct, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            p.price = data.price;
                            holder.previous_price.setText(holder.original_product_price.getText().toString().trim());
                            holder.original_product_price.setText(String.valueOf(data.price));

                            Toast.makeText(mContext,"El precio ha sido cambiado", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                    dialog.dismiss();

                }else{
                    Toast.makeText(mContext, "El campo precio debe estar completo", Toast.LENGTH_SHORT).show();
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