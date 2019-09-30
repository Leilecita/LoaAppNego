package com.example.android.loa.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.activities.BalanceActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.StockEvent;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ProductAdapter extends BaseAdapter<Product,ProductAdapter.ViewHolder>  {
    private Context mContext;
    private OnChangeViewStock onChangeViewStock= null;

    private Integer prevPosOpenView;

    public void setOnChangeViewStock(OnChangeViewStock lister){
        onChangeViewStock=lister;
    }

    public ProductAdapter(Context context, List<Product> products){
        setItems(products);
        mContext = context;

        prevPosOpenView=-1;
    }

    public void resetPrevOpenView(){
        prevPosOpenView=-1;
    }

    public ProductAdapter(){

    }

    public List<Product> getListProduct(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView type;
        public TextView brand;
        public TextView stock;

        public LinearLayout line_options;

        public ImageView add_stock;
        public ImageView less_stock;
        public ImageView get_balance;
        public ImageView delete_product;

        public LinearLayout updateStock;
        public EditText load_stock;
        public TextView detail;
        public Button ok;

        public ImageView salir;

        public ViewHolder(View v){
            super(v);
            type= v.findViewById(R.id.type);
            brand= v.findViewById(R.id.brand);
            stock= v.findViewById(R.id.stock);

            line_options= v.findViewById(R.id.line_options);
            delete_product= v.findViewById(R.id.delete_product);
            add_stock= v.findViewById(R.id.add_stock);
            less_stock= v.findViewById(R.id.less_stock);
            get_balance= v.findViewById(R.id.see_balance);

            updateStock= v.findViewById(R.id.update_stock);
            load_stock= v.findViewById(R.id.load_stock);
            detail= v.findViewById(R.id.detail);
            ok= v.findViewById(R.id.button_ok);
            salir= v.findViewById(R.id.salir);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_product,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ProductAdapter.ViewHolder vh){
        if(vh.type!=null)
            vh.type.setText(null);
        if(vh.brand!=null)
            vh.brand.setText(null);
        if(vh.stock!=null)
            vh.stock.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Product currentProduct=getItem(position);

        holder.type.setText(currentProduct.type);
        holder.brand.setText(currentProduct.brand);
        holder.stock.setText(String.valueOf(currentProduct.stock));

        holder.salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(holder.salir.getVisibility() == View.VISIBLE){
                holder.updateStock.setVisibility(View.GONE);
                holder.salir.setVisibility(View.GONE);
                if(onChangeViewStock!=null){
                    onChangeViewStock.OnChangeViewStock();
                }
            }

            }
        });
        optionsItem(holder,currentProduct,position);

        holder.line_options.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.line_options.getVisibility() == View.VISIBLE){
                    holder.line_options.setVisibility(View.GONE);


                }else{
                    if(prevPosOpenView!=-1 ){
                        if(prevPosOpenView!=position)
                        updateItem(prevPosOpenView,getItem(prevPosOpenView));
                    }
                    prevPosOpenView=position;
                    holder.line_options.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    private void closeItem(ViewHolder holder){
        if(holder.salir.getVisibility() == View.VISIBLE){
            holder.updateStock.setVisibility(View.GONE);
            holder.salir.setVisibility(View.GONE);
            if(onChangeViewStock!=null){
                onChangeViewStock.OnChangeViewStock();
            }
        }
    }
    private void optionsItem(final ViewHolder holder, final Product p, final Integer position){

        holder.get_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance(p);
            }
        });
        holder.less_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.salir.getVisibility() == View.GONE){
                    holder.updateStock.setVisibility(View.VISIBLE);
                    holder.salir.setVisibility(View.VISIBLE);
                    if(onChangeViewStock!=null){
                        onChangeViewStock.OnChangeViewStock();
                    }
                }

                lessStock(p,position,holder);
            }
        });

        holder.add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(holder.salir.getVisibility() == View.GONE){
                   holder.updateStock.setVisibility(View.VISIBLE);
                   holder.salir.setVisibility(View.VISIBLE);
                   if(onChangeViewStock!=null){
                       onChangeViewStock.OnChangeViewStock();
                   }
               }
                loadStock(p,position,holder);
            }
        });
        holder.delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(p,position);
            }
        });


    }
    private void getBalance(Product p ){
        Intent i = new Intent(mContext, BalanceActivity.class);
        i.putExtra("ID", p.id);
        i.putExtra("TYPE", p.type);
        i.putExtra("BRAND", p.brand);
        mContext.startActivity(i);
    }


    private void lessStock(final Product p, final int position, final ViewHolder holder){
        holder.detail.setText("Salida venta");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuOut(holder);
            }
        });

        holder.load_stock.setHint("-");
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stockP=holder.load_stock.getText().toString().trim();
                String detailP=holder.detail.getText().toString().trim();

                if(!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)) {

                    StockEvent s = new StockEvent(p.id, 0, Integer.valueOf(stockP),p.stock, detailP);
                    s.ideal_stock=s.stock_ant + s.stock_in - s.stock_out;
                    p.stock-=Integer.valueOf(stockP);

                    ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {
                            holder.stock.setText(String.valueOf(p.stock));
                            holder.load_stock.setText("");

                            if(onChangeViewStock!= null){
                                onChangeViewStock.onReloadTotalQuantityStock();
                            }

                            Toast.makeText(mContext,"El stock ha sido modificado", Toast.LENGTH_SHORT).show();

                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(holder.load_stock.getRootView().getWindowToken(), 0);

                            closeItem(holder);
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," Error al cargar stock",mContext);
                        }
                    });
                }else{

                    Toast.makeText(mContext,"Tipo de dato no válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createMenuIn(final ViewHolder holder){
        PopupMenu popup = new PopupMenu(mContext, holder.itemView);
        popup.getMenuInflater().inflate(R.menu.menu_stock_in, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.in_buy:
                        holder.detail.setText("Ingreso compra");
                        return true;
                    case R.id.in_dev:
                        holder.detail.setText("Ingreso dev");
                        return true;
                    case R.id.in_dev_luz:
                        holder.detail.setText("ingreso dev Luz");
                        return true;
                    case R.id.in_dev_wrong:
                        holder.detail.setText("Ingreso dev falla");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void createMenuOut(final ViewHolder holder){

        PopupMenu popup = new PopupMenu(mContext, holder.itemView);
        popup.getMenuInflater().inflate(R.menu.menu_stock_out, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.out_buy:
                        holder.detail.setText("Salida venta");
                        return true;
                    case R.id.out_dev:
                        holder.detail.setText("Salida dev");
                        return true;
                    case R.id.out_falla:
                        holder.detail.setText("Salida dev falla");
                        return true;
                    case R.id.out_santi:
                        holder.detail.setText("Salida santi");
                        return true;
                    case R.id.out_gifts:
                        holder.detail.setText("Salida premios");
                        return true;

                    default:
                        return false;
                }
            }
        });

        popup.show();

    }

    private void loadStock(final Product p, final int position, final ViewHolder holder){

        holder.detail.setText("Ingreso compra");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuIn(holder);
            }
        });
        holder.load_stock.setHint("+");
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stockP = holder.load_stock.getText().toString().trim();
                String detailP = holder.detail.getText().toString().trim();

                if (!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)) {
                    StockEvent s = new StockEvent(p.id, Integer.valueOf(stockP), 0, p.stock, detailP);
                    s.ideal_stock = s.stock_ant + s.stock_in - s.stock_out;
                    p.stock += Integer.valueOf(stockP);

                    ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {
                            if(onChangeViewStock!= null){
                                onChangeViewStock.onReloadTotalQuantityStock();
                            }


                          holder.stock.setText(String.valueOf(p.stock));
                          holder.load_stock.setText("");


                          Toast.makeText(mContext,"El stock ha sido modificado", Toast.LENGTH_SHORT).show();

                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(holder.load_stock.getRootView().getWindowToken(), 0);
                           // updateItem(position, p);

                            closeItem(holder);
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error", " Error al cargar stock", mContext);
                        }
                    });
                }else{
                    Toast.makeText(mContext,"Tipo de dato no válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
     }

    private void deleteProduct(final Product p,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.delete_product, null);
        builder.setView(dialogView);

        final TextView brand = dialogView.findViewById(R.id.brand);
        final TextView type = dialogView.findViewById(R.id.type);
        final TextView stock= dialogView.findViewById(R.id.stock);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        brand.setText(p.brand);
        stock.setText(String.valueOf(p.stock));
        type.setText(p.type);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiClient.get().deleteProduct(p.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        removeItem(position);
                        Toast.makeText(mContext, "Se ha eliminado el producto "+p.type, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al eliminar el producto "+p.type,mContext);
                    }
                });


                dialog.dismiss();
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

  /*

    private void loadStock2(final Product p,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.load_stock, null);
        builder.setView(dialogView);


        final TextView type= dialogView.findViewById(R.id.type);
        type.setText(p.type);
        final TextView brand= dialogView.findViewById(R.id.brand);
        brand.setText(p.brand);
        final TextView stockact= dialogView.findViewById(R.id.stockact);
        stockact.setText(String.valueOf(p.stock));
        final TextView stock= dialogView.findViewById(R.id.stock);
        final TextView detail= dialogView.findViewById(R.id.detail);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);


        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockP=stock.getText().toString().trim();
                String detailP=detail.getText().toString().trim();


                if(!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)) {
                    StockEvent s = new StockEvent(p.id, Integer.valueOf(stockP), 0, p.stock,detailP);
                    s.ideal_stock=s.stock_ant + s.stock_in - s.stock_out;
                    p.stock+=Integer.valueOf(stockP);

                    ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {

                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," Error al cargar stock",dialogView.getContext());
                        }
                    });


                    updateItem(position,p);

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

     private void lessStock2(final Product p,final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.load_stock, null);
        builder.setView(dialogView);


        final TextView type= dialogView.findViewById(R.id.type);
        type.setText(p.type);
        final TextView brand= dialogView.findViewById(R.id.brand);
        brand.setText(p.brand);
        final TextView stockact= dialogView.findViewById(R.id.stockact);
        stockact.setText(String.valueOf(p.stock));
        final TextView stock= dialogView.findViewById(R.id.stock);
        final TextView title= dialogView.findViewById(R.id.title);
        final TextView title2= dialogView.findViewById(R.id.title2);
        final TextView detail= dialogView.findViewById(R.id.detail);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);

        title.setText("Salida mercaderia");
        title2.setText("Cantidad vendida");

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockP=stock.getText().toString().trim();
                String detailP=detail.getText().toString().trim();

                if(!stockP.matches("") && ValidatorHelper.get().isTypeInteger(stockP)) {

                    StockEvent s = new StockEvent(p.id, 0, Integer.valueOf(stockP),p.stock, detailP);
                    s.ideal_stock=s.stock_ant + s.stock_in - s.stock_out;
                    p.stock-=Integer.valueOf(stockP);

                    ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                        @Override
                        public void onSuccess(StockEvent data) {

                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," Error al cargar stock",dialogView.getContext());
                        }
                    });


                    updateItem(position,p);
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

   private void edithProduct(final Product p,final int position, final ViewHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_product, null);
        builder.setView(dialogView);


        final TextView edit_name= dialogView.findViewById(R.id.edit_name);
        final TextView edit_price= dialogView.findViewById(R.id.edit_price);
        final TextView edit_stock= dialogView.findViewById(R.id.edit_stock);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);


        edit_name.setText(p.getFish_name());
        edit_name.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));
        edit_price.setText(getIntegerQuantity(p.getPrice()));
        edit_price.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));
        edit_stock.setText(getIntegerQuantity(p.getStock()));
        edit_stock.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName=edit_name.getText().toString().trim();
                String productPrice=edit_price.getText().toString().trim();
                String productStock=edit_stock.getText().toString().trim();

                boolean isDataValid=true;

                if(!productName.matches("")){
                    p.setFish_name(productName);
                }

                if(!productPrice.matches("")) {
                    if (ValidatorHelper.get().isTypeDouble(productPrice)) {
                        p.setPrice(Double.valueOf(productPrice));
                    }else {
                        isDataValid=false;
                        Toast.makeText(dialogView.getContext(), " Tipo de precio no valido ", Toast.LENGTH_LONG).show();
                    }
                }

                if(!productStock.matches("")) {
                    if (ValidatorHelper.get().isTypeDouble(productStock)) {
                        p.setStock(Double.valueOf(productStock));
                    }else {
                        isDataValid=false;
                        Toast.makeText(dialogView.getContext(), " Tipo de stock no valido ", Toast.LENGTH_LONG).show();
                    }
                }

                if(isDataValid){
                    updateItem(position,p);

                    ApiClient.get().putProduct(p, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            EventBus.getDefault().post(new EventProductState(p.id,"edited",p.stock));
                            Toast.makeText(dialogView.getContext(), " El producto "+data.fish_name +" ha sido modificado ", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error"," Error al modificar producto",dialogView.getContext());
                        }
                    });

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
    private String getIntegerQuantity(Double val){
        String[] arr=String.valueOf(val).split("\\.");
        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }

    }

}*/


}
   /*
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_menu_balance, null);

                TextView load_stock=dialogView.findViewById(R.id.load_stock);
                TextView less_stock=dialogView.findViewById(R.id.less_stock);
                TextView balance=dialogView.findViewById(R.id.balance);
                TextView delete_product=dialogView.findViewById(R.id.delete);
                TextView title=dialogView.findViewById(R.id.data_product);

                title.setText(currentProduct.type+" "+currentProduct.brand);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                load_stock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadStock(currentProduct,position);
                        dialog.dismiss();
                    }
                });

                less_stock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lessStock(currentProduct,position);
                        dialog.dismiss();
                    }
                });
                balance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBalance(currentProduct);
                        dialog.dismiss();
                    }
                });

                delete_product.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProduct(currentProduct, position);
                        dialog.dismiss();

                    }
                });

                dialog.show();


                */


             /*   PopupMenu popup = new PopupMenu(mContext, holder.itemView);
                popup.getMenuInflater().inflate(R.menu.menu_products, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                deleteProduct(currentProduct, position);
                                return true;
                            case R.id.menu_load_stock:
                                loadStock(currentProduct,position);
                                return true;
                            case R.id.menu_solded:
                                lessStock(currentProduct,position);
                                return true;
                            case R.id.menu_balance:
                                getBalance(currentProduct);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });*/
