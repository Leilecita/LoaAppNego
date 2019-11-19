package com.example.android.loa.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.StockEvent;
import com.google.android.material.snackbar.Snackbar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends BaseAdapter<Product,ProductAdapter.ViewHolder>  {
    private Context mContext;
    private OnChangeViewStock onChangeViewStock= null;

    private Integer prevPosOpenView;
    private Boolean isModel;

    private String dateSelected="";


    public void setOnChangeViewStock(OnChangeViewStock lister){
        onChangeViewStock=lister;
    }

    public ProductAdapter(Context context, List<Product> products){
        setItems(products);
        mContext = context;

        prevPosOpenView=-1;
        isModel=false;

    }

    public void setIsModel(Boolean model){
        this.isModel=model;
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
        public TextView value;

        public LinearLayout select_payment_method;
        public CheckBox check_ef;
        public CheckBox check_deb;
        public CheckBox check_card;
        public TextView date;
        public TextView less_load;
        public LinearLayout line_value;
        public RelativeLayout item;



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
            value= v.findViewById(R.id.value);

            select_payment_method= v.findViewById(R.id.select_payment_method);
            check_ef= v.findViewById(R.id.check_ef);
            check_deb= v.findViewById(R.id.check_deb);
            check_card= v.findViewById(R.id.check_card);
            date= v.findViewById(R.id.date);
            less_load= v.findViewById(R.id.less_load);
            line_value= v.findViewById(R.id.line_value);
            item= v.findViewById(R.id.item);
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

        holder.stock.setText(String.valueOf(currentProduct.stock));

        dateSelected = getExpandedDate();//DateHelper.get().actualDateExtractions();
        holder.date.setText(DateHelper.get().getOnlyDate(dateSelected));

        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(holder);
            }
        });

        if(isModel){
            holder.brand.setText(currentProduct.brand+"   "+currentProduct.model);
        }else{
            holder.brand.setText(currentProduct.brand);
        }


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
              //  holder.item.setBackgroundColor(mContext.getResources().getColor(R.color.select));
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



    public static void hideSoftKeyboard(Context ctx, View view)
    {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    private void lessStock(final Product p, final int position, final ViewHolder holder){

        holder.detail.setText("");
        holder.detail.setHint("Elegir detalle");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // hideSoftKeyboard(mContext, v);
                createMenuOut(holder);
            }
        });



        holder.load_stock.setText("1");
        holder.less_load.setText("- ");

        holder.select_payment_method.setVisibility(View.VISIBLE);
        holder.line_value.setVisibility(View.VISIBLE);
        holder.date.setVisibility(View.VISIBLE);


        holder.check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.check_ef.isChecked()){
                    holder.check_ef.setChecked(true);
                    holder.check_card.setChecked(false);
                    holder.check_deb.setChecked(false);
                }else{
                    holder.check_ef.setChecked(false);

                }
            }
        });

        holder.check_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.check_card.isChecked()){
                    holder.check_card.setChecked(true);
                    holder.check_ef.setChecked(false);
                    holder.check_deb.setChecked(false);
                }else{
                    holder.check_card.setChecked(false);
                }
            }
        });

        holder.check_deb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.check_deb.isChecked()){
                    holder.check_deb.setChecked(true);
                    holder.check_ef.setChecked(false);
                    holder.check_card.setChecked(false);
                }else{
                    holder.check_deb.setChecked(false);
                }
            }
        });

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String payment_method= getPaymentMethod(holder);

                String stockP=holder.load_stock.getText().toString().trim();
                String detailP=holder.detail.getText().toString().trim();
                String valuep=holder.value.getText().toString().trim();


                if(!stockP.matches("") && !detailP.matches("") && !valuep.matches("")){
                    if( ValidatorHelper.get().isTypeInteger(stockP) && ValidatorHelper.get().isTypeDouble(valuep)) {

                        StockEvent s = new StockEvent(p.id, 0, Integer.valueOf(stockP),p.stock, detailP,Double.valueOf(valuep),payment_method);
                        s.ideal_stock=s.stock_ant + s.stock_in - s.stock_out;
                        p.stock-=Integer.valueOf(stockP);

                        s.created=dateSelected;


                        ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                            @Override
                            public void onSuccess(StockEvent data) {
                                holder.stock.setText(String.valueOf(p.stock));
                                holder.load_stock.setText("");

                                if(onChangeViewStock!= null){
                                    onChangeViewStock.onReloadTotalQuantityStock();
                                }

                                Toast.makeText(mContext,"El stock ha sido modificado", Toast.LENGTH_SHORT).show();

                                hideSoftKeyboard(mContext,holder.itemView);

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

                }else{
                    Toast.makeText(mContext,"Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private String getPaymentMethod(ViewHolder holder){

        if(holder.check_deb.isChecked()){
            return "debito";
        }else if(holder.check_card.isChecked()){
            return "tarjeta";
        }else if(holder.check_ef.isChecked()){
            return "efectivo";
        }else{
            return "efectivo";
        }

    }

    private void loadStock(final Product p, final int position, final ViewHolder holder){

        holder.line_value.setVisibility(View.GONE);
        holder.select_payment_method.setVisibility(View.GONE);
        holder.date.setVisibility(View.INVISIBLE);

        //todo
        holder.detail.setHint("Elegir detalle");
       // holder.detail.setText("Stock inicial");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuIn(holder);
            }
        });

        holder.less_load.setText("+ ");

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stockP = holder.load_stock.getText().toString().trim();
                String detailP = holder.detail.getText().toString().trim();
                String valueP = holder.value.getText().toString().trim();

                if(valueP.equals("")){
                    valueP="0.0";
                }

                if ( ValidatorHelper.get().isTypeInteger(stockP) && ValidatorHelper.get().isTypeDouble(valueP)) {
                    if(!stockP.matches("") && !detailP.matches("")){
                        StockEvent s = new StockEvent(p.id, Integer.valueOf(stockP), 0, p.stock, detailP,Double.valueOf(valueP),"");
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
                        Toast.makeText(mContext,"Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
                    }
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

                ApiClient.get().deleteProduct(p.id, new GenericCallback<SpinnerData>() {
                    @Override
                    public void onSuccess(SpinnerData data) {
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


  private void createMenuIn(final ViewHolder holder){
      PopupMenu popup = new PopupMenu(mContext, holder.itemView);
      popup.getMenuInflater().inflate(R.menu.menu_stock_in, popup.getMenu());
      popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

          public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.in_buy:
                      holder.detail.setText("Ingreso compra");
                      return true;
                  case R.id.in_error:
                      holder.detail.setText("Suma por error anterior");
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
                    case R.id.out_error:
                        holder.detail.setText("Resta por error anterior");
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
                    case R.id.out_stole:
                        holder.detail.setText("Salida por robo");
                        return true;

                    default:
                        return false;
                }
            }
        });

        popup.show();

    }
    private void selectDate(final ViewHolder holder){

        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(mContext,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }

                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }

                        String time= "10:00:00";
                        String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;

                        holder.date.setText(DateHelper.get().getOnlyDate(datePicker));
                        dateSelected=datePicker;
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private String getExpandedDate(){
        String date= DateHelper.get().actualDateExtractions();
        String time= DateHelper.get().getOnlyTime(date);

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            //Date date1 = sdf.parse("19:28:00");
            Date date1 = sdf.parse(time);
            //Date date2 = sdf.parse("21:13:00");
            Date date2 = sdf.parse("04:13:00");

            // Outputs -1 as date1 is before date2
            System.out.println(date1.compareTo(date2));

            if(date1.compareTo(date2) < 0){
                System.out.println(date1.compareTo(date2));

                return DateHelper.get().getPreviousDay(date);
            }else{
                return date;
            }
/*
            // Outputs 1 as date1 is after date1
            System.out.println(date2.compareTo(date1));

            date2 = sdf.parse("19:28:00");
            // Outputs 0 as the dates are now equal
            System.out.println(date1.compareTo(date2));
            */

        } catch (ParseException e){
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }
}
