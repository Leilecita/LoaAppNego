package com.example.android.loa.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.android.loa.activities.todelete.SaleMovementsActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.StockEvent;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProductAdapter extends BaseAdapter<Product,ProductAdapter.ViewHolder>  {
    private Context mContext;
    private OnChangeViewStock onChangeViewStock= null;

    private Integer prevPosOpenView;
    private Boolean isModel;

    private String dateSelected="";

    private List<ReportSimpelClient> students;
    private Long clientId;
    private String client_name;

    public void setExtendedDate(String extendedDate){
        this.dateSelected=extendedDate;
    }


    public void setOnChangeViewStock(OnChangeViewStock lister){
        onChangeViewStock=lister;
    }

    public ProductAdapter(Context context, List<Product> products){
        setItems(products);
        mContext = context;

        prevPosOpenView=-1;
        isModel=false;
        clientId=-1l;
        client_name="";

    }

    public void setClients(List<ReportSimpelClient> cls){
        students=cls;
    }
    public ArrayList<String> getListFromClients(){
        ArrayList<String> s=new ArrayList<>();
        s.add("Vacia");
        for(int i =0;i<students.size();++i){
            s.add(students.get(i).name);
        }
        return s;
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
        public TextView model;
        public TextView stock;

        public LinearLayout line_options;

        public ImageView add_stock;
        public ImageView less_stock;

        public LinearLayout updateStock;
        public EditText load_stock;
        public TextView detail;
        public Button ok;
        public Button cancel;

        public ImageView salir;
        public TextView value;

        public LinearLayout select_payment_method;
        public CheckBox check_ef;
        public CheckBox check_deb;
        public CheckBox check_card;
        public TextView date;
        public TextView less_load;
        public LinearLayout line_value;
        public LinearLayout line_student;
        public AutoCompleteTextView name;
        public RelativeLayout item;

        public TextView close_select_student;
        public EditText price_product;
        public ImageView imageButton;


        public ViewHolder(View v){
            super(v);
            type= v.findViewById(R.id.type);
            brand= v.findViewById(R.id.brand);
            stock= v.findViewById(R.id.stock);

            line_options= v.findViewById(R.id.line_options);
            add_stock= v.findViewById(R.id.add_stock);
            less_stock= v.findViewById(R.id.less_stock);

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
            line_student= v.findViewById(R.id.line_select_student);
            name= v.findViewById(R.id.name);
            close_select_student= v.findViewById(R.id.close_select_student);
            price_product= v.findViewById(R.id.price_product);
            cancel= v.findViewById(R.id.cancel);
            model= v.findViewById(R.id.model);
            imageButton= v.findViewById(R.id.imagebutton);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_product_new,parent,false);
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

    private void loadIcon(ViewHolder holder,final String item){

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,item,Toast.LENGTH_SHORT).show();
            }
        });

        if(item.equals("Hombre")){
            holder.imageButton.setImageResource(R.drawable.bmancl);

            // holder.color.getBackground().setColorFilter(mContext.getResources().getColor(R.color.hombre), PorterDuff.Mode.SRC_ATOP);
            // holder.image.setImageResource(R.drawable.man);
        }else if(item.equals("Dama")){
            holder.imageButton.setImageResource(R.drawable.bwomcl);
        }else if(item.equals("Accesorio")){
            holder.imageButton.setImageResource(R.drawable.bacccl);
        }else if(item.equals("Niño")){
            holder.imageButton.setImageResource(R.drawable.bnincl);
        }else if(item.equals("Tecnico")){
            holder.imageButton.setImageResource(R.drawable.btecl);
        }else if(item.equals("Calzado")){
            holder.imageButton.setImageResource(R.drawable.bcalcl);
        }else if(item.equals("Luz")){
            holder.imageButton.setImageResource(R.drawable.bluzcl);
        }else if(item.equals("Oferta")){
            holder.imageButton.setImageResource(R.drawable.bofercl);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Product currentProduct=getItem(position);

        holder.model.setText(currentProduct.model);
        holder.type.setText(currentProduct.type);
        holder.stock.setText(String.valueOf(currentProduct.stock));
        holder.date.setText(DateHelper.get().getOnlyDate(dateSelected));
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(holder);
            }
        });

        loadIcon(holder,currentProduct.item);

        holder.close_select_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientId=-1l;
                holder.line_student.setVisibility(View.GONE);
                holder.line_value.setVisibility(View.VISIBLE);
                holder.select_payment_method.setVisibility(View.VISIBLE);
                holder.detail.setText("Salida venta");
            }
        });


        if(isModel){
            holder.brand.setText(currentProduct.brand+"   "+currentProduct.model);
        }else{
            holder.brand.setText(currentProduct.brand);
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.updateStock.setVisibility(View.GONE);
                holder.line_options.setVisibility(View.GONE);
                if(onChangeViewStock!=null){
                    onChangeViewStock.OnChangeViewStock();
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteProduct(currentProduct,position);
                return false;
            }
        });

        optionsItem(holder,currentProduct,position);

        holder.line_options.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.line_options.getVisibility() == View.VISIBLE){
                    holder.line_options.setVisibility(View.GONE);
                    closeItem(holder);

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
        holder.detail.setVisibility(View.INVISIBLE);
        holder.updateStock.setVisibility(View.GONE);
        holder.line_student.setVisibility(View.GONE);
        if(onChangeViewStock!=null){
            onChangeViewStock.OnChangeViewStock();
        }
    }
    private void optionsItem(final ViewHolder holder, final Product p, final Integer position){

      /*  holder.get_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance(p);
            }
        });*/
        holder.less_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.updateStock.setVisibility(View.VISIBLE);
                lessStock(p,position,holder);
                holder.detail.setVisibility(View.VISIBLE);
            }
        });

        holder.add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.updateStock.setVisibility(View.VISIBLE);
                loadStock(p,position,holder);
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

    private Long getIdByName(String name){
        for(int i=0;i < students.size();++i ){
            if(students.get(i).name.equals(name)){
                return students.get(i).id;
            }
        }
        return -1l;
    }

    private void loadNameClients(ViewHolder holder){

        holder.line_student.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.select_dialog_item, getListFromClients());

        holder.name.setThreshold(1);
        holder.name.setAdapter(adapter);
        holder.name.setTextColor(mContext.getResources().getColor(R.color.word));
        holder.name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(mContext, "Selected Item is: \t" + item, Toast.LENGTH_LONG).show();
                hideSoftKeyboard(mContext,view);
                clientId=getIdByName(item);
                client_name=item;
            }
        });
    }


    public static void hideSoftKeyboard(Context ctx, View view)
    {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    private void lessStock(final Product p, final int position, final ViewHolder holder){
        holder.detail.setVisibility(View.VISIBLE);
        holder.detail.setText("Salida venta");
        holder.detail.setHint("Elegir detalle");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mContext, v);
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
                    hideSoftKeyboard(mContext, holder.itemView);
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
                    hideSoftKeyboard(mContext, holder.itemView);
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
                    hideSoftKeyboard(mContext, holder.itemView);
                }else{
                    holder.check_deb.setChecked(false);
                }
            }
        });

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                String payment_method= getPaymentMethod(holder);

                String stockP=holder.load_stock.getText().toString().trim();
                String detailP=holder.detail.getText().toString().trim();
                String valuep=holder.value.getText().toString().trim();

                    if(!detailP.equals("Salida ficha") || clientId !=-1l){

                    if (!stockP.matches("") && !detailP.matches("") && !valuep.matches("")) {

                        if (ValidatorHelper.get().isTypeInteger(stockP) && ValidatorHelper.get().isTypeDouble(valuep)) {

                            StockEvent s = new StockEvent(p.id, 0, Integer.valueOf(stockP), p.stock, detailP, Double.valueOf(valuep), payment_method);
                            s.ideal_stock = s.stock_ant + s.stock_in - s.stock_out;
                            p.stock -= Integer.valueOf(stockP);
                            s.created = dateSelected;

                            if (clientId != -1 && detailP.equals("Salida ficha")) {
                                s.client_id = clientId;
                                s.value_for_file = Double.valueOf(holder.price_product.getText().toString().trim());
                                s.client_name=client_name;
                            }

                            ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                                @Override
                                public void onSuccess(StockEvent data) {
                                    holder.stock.setText(String.valueOf(p.stock));
                                    holder.load_stock.setText("");

                                    if (onChangeViewStock != null) {
                                        onChangeViewStock.onReloadTotalQuantityStock();
                                    }

                                    Snackbar snackbar = Snackbar
                                            .make(v, "El stock ha sido modificado", Snackbar.LENGTH_LONG)
                                            .setAction("VER PLANILLA VENTAS", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mContext.startActivity(new Intent(mContext, SaleMovementsActivity.class));
                                                }
                                            });

                                    snackbar.show();
                                    hideSoftKeyboard(mContext, holder.itemView);

                                    closeItem(holder);
                                }

                                @Override
                                public void onError(Error error) {
                                    DialogHelper.get().showMessage("Error", " Error al cargar stock", mContext);
                                }
                            });
                        } else {
                            Toast.makeText(mContext, "Tipo de dato no válido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, "Debe ingresar un nombre para completar la salida de la ficha", Toast.LENGTH_LONG).show();
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

        //// TODO: 2020-05-11
        holder.detail.setVisibility(View.VISIBLE);
        holder.detail.setText("");
        holder.detail.setHint("Elegir detalle");
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mContext,v);
                createMenuIn(holder);
            }
        });

        holder.less_load.setText("+ ");

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

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

                       // s.created=getExpandedDate();
                        s.created=dateSelected;


                        ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                            @Override
                            public void onSuccess(StockEvent data) {
                                if(onChangeViewStock!= null){
                                    onChangeViewStock.onReloadTotalQuantityStock();
                                }

                                holder.stock.setText(String.valueOf(p.stock));
                                holder.load_stock.setText("");


                                Snackbar snackbar = Snackbar
                                        .make(v, "El stock ha sido modificado", Snackbar.LENGTH_LONG)
                                        .setAction("VER PLANILLA VENTAS", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                mContext.startActivity(new Intent(mContext, SaleMovementsActivity.class));
                                            }
                                        });

                                snackbar.show();


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
                      return true;
                  case R.id.in_balance_stock:
                      holder.detail.setText("Ingreso balance stock");
                      return true;
                  case R.id.in_dev:
                      //holder.detail.setText("Ingreso dev");
                      holder.detail.setText("Ingreso dev");
                      return true;

                  case R.id.in_dev_wrong:
                      holder.detail.setText("Ingreso dev falla");
                      return true;
                  case R.id.in_consign:
                      holder.detail.setText("Ingreso consignacion");
                      return true;
                  case R.id.in_dev_luz:
                      holder.detail.setText("ingreso dev Luz");
                      return true;
                  case R.id.in_stock_oferta:
                      holder.detail.setText("ingreso stock oferta");
                      return true;
                  case R.id.in_stock_local:
                      holder.detail.setText("ingreso stock local");
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
                    case R.id.out_person_file:
                        holder.detail.setText("Salida ficha");

                        holder.select_payment_method.setVisibility(View.GONE);
                        holder.line_value.setVisibility(View.GONE);
                        holder.value.setText("0.0");

                        loadNameClients(holder);
                        return true;

                    case R.id.out_falla:
                        holder.detail.setText("Salida dev falla");
                        return true;
                    case R.id.out_balance_stock:
                        holder.detail.setText("Salida balance stock");
                        return true;
                    case R.id.out_stole:
                        holder.detail.setText("Salida por robo");
                        return true;
                    case R.id.out_error:
                        holder.detail.setText("Resta por error anterior");
                        return true;
                    case R.id.out_dev:
                        holder.detail.setText("Salida dev");
                        return true;
                    case R.id.out_consign:
                        holder.detail.setText("Salida articulo consignacion");
                        return true;

                    case R.id.out_bonif:
                        holder.detail.setText("Salida ficha especial bonificacion");
                        return true;
                    case R.id.out_santi:
                        holder.detail.setText("Salida santi");
                        return true;
                    case R.id.out_gifts:
                        holder.detail.setText("Salida premios");
                        return true;

                    case R.id.out_luz:
                        holder.detail.setText("Salida luz");
                        return true;
                    case R.id.out_local:
                        holder.detail.setText("paso al stock local");
                        return true;
                    case R.id.out_oferta:
                        holder.detail.setText("paso al stock oferta");
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

}
