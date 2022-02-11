package com.example.android.loa.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.activities.BoxMovementsActivity;
import com.example.android.loa.activities.balances.BalanceActivity;
import com.example.android.loa.activities.SalesActivity;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.fragments.SalesFragment;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.example.android.loa.network.models.ReportStockEvent;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.StockEvent;
import com.example.android.loa.types.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends BaseAdapter<Product,ProductAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private OnChangeViewStock onChangeViewStock= null;

    private Integer prevPosOpenView;
    private Boolean isModel;

    private String dateSelected="";

    private List<ReportSimpelClient> students;
    private Long clientId;
    private String client_name;
    private String client_created;

    private Boolean product_sales;

    private Boolean isGrouped;

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).deleted_time));
            return date.getDay();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_event_day, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final Product e = getItem(position);

            String month = DateHelper.get().getNameMonth2(e.deleted_time).substring(0, 3);
            String numberDay = DateHelper.get().numberDay(e.deleted_time);

            //primer linear
            int count = linear.getChildCount();
            View v = null;
            View v2 = null;
            View v3 = null;
            View v5 = null;
            View v6 = null;
            View v7 = null;
            View v8 = null;
            View v9 = null;
            View v10 = null;

            for (int k = 0; k < count; k++) {
                v3 = linear.getChildAt(k);

                //frame
                if (k == 0) {
                    FrameLayout linear4 = (FrameLayout) v3;
                    int count3 = linear4.getChildCount();
                    for (int i = 0; i < count3; i++) {
                        v = linear4.getChildAt(i);
                        if (i == 1) {

                            RelativeLayout rel2 = (RelativeLayout) v;
                            int countRel2 = rel2.getChildCount();

                            for (int r = 0; r < countRel2; r++) {

                                v5=rel2.getChildAt(r);
                                if (r == 0) {
                                    TextView t2 = (TextView) v5;
                                    t2.setText(month);
                                }else if(r==1){
                                    TextView t = (TextView) v5;
                                    t.setText(numberDay);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private String getExpandedDate(){

        String date= DateHelper.get().actualDateExtractions();
        String time= DateHelper.get().getOnlyTime(date);

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse("04:13:00");

            if(date1.compareTo(date2) < 0){
                System.out.println(date1.compareTo(date2));
                return DateHelper.get().getPreviousDay(date);
            }else{
                return date;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
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
        client_created="";

        product_sales = true;
        isGrouped = false;
    }

    public void setProduct_sales(Boolean val){
        product_sales = val;
    }

    public void setIsGrouped(Boolean val){
        isGrouped = val;
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
        public RelativeLayout balance;
        public RelativeLayout edith;

        public LinearLayout updateStock;
        public EditText load_stock;
        public TextView detail;
        public Button ok;
        public Button cancel;

        public ImageView salir;
        public TextView value_sale;

        public LinearLayout select_payment_method;
        public CheckBox check_ef;
        public CheckBox check_deb;
        public CheckBox check_card;
        public CheckBox check_trans;
        public CheckBox check_merc_pago;

        public TextView date;
        public TextView less_load;
        public LinearLayout line_value;
        public LinearLayout line_student;
        public AutoCompleteTextView name;
        public RelativeLayout item;

        public TextView close_select_student;
        public EditText price_product;
        public ImageView imageButton;
        public TextView observation;

        public TextView original_product_price;

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
            value_sale = v.findViewById(R.id.value);

            select_payment_method= v.findViewById(R.id.select_payment_method);
            check_ef= v.findViewById(R.id.check_ef);
            check_deb= v.findViewById(R.id.check_deb);
            check_card= v.findViewById(R.id.check_card);
            check_trans= v.findViewById(R.id.check_trans);
            check_merc_pago= v.findViewById(R.id.check_merc);

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
            balance= v.findViewById(R.id.balance);
            edith= v.findViewById(R.id.edith);
            observation= v.findViewById(R.id.observation);

            original_product_price= v.findViewById(R.id.original_product_price);
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
        if(vh.model!=null)
            vh.model.setText(null);
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
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Product currentProduct=getItem(position);

        if(!isGrouped){
            holder.original_product_price.setText(String.valueOf(currentProduct.price));
        }

        if(currentProduct.price > 0){
            holder.value_sale.setText("0.0");
            //esto lo hago solo cuando selecciona salida por venta
           // holder.value_sale.setText(String.valueOf(currentProduct.price));
        }

        if(product_sales){
            holder.balance.setVisibility(View.GONE);
        }else{
            holder.balance.setVisibility(View.VISIBLE);
        }

        holder.model.setText(currentProduct.model);
        holder.type.setText(currentProduct.type);
        holder.stock.setText(String.valueOf(currentProduct.stock));
        holder.date.setText(DateHelper.get().getOnlyDate(getExpandedDate()));
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(holder);
            }
        });

        loadIcon(holder.imageButton,currentProduct.item);

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


        holder.brand.setText(currentProduct.brand);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeItem(holder);
            }
        });

        optionsItem(holder,currentProduct,position);

        holder.line_options.setVisibility(View.GONE);

        if(currentProduct.deleted.equals("false")){

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isGrouped) {
                            if (holder.line_options.getVisibility() == View.VISIBLE) {
                                holder.line_options.setVisibility(View.GONE);
                                closeItem(holder);
                            } else {
                                if (prevPosOpenView != -1) {
                                    if (prevPosOpenView != position)
                                        updateItem(prevPosOpenView, getItem(prevPosOpenView));
                                }
                                prevPosOpenView = position;
                                holder.line_options.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        }


    }

    private void closeItem(ViewHolder holder){

        if(product_sales){
            holder.balance.setVisibility(View.GONE);
        }else{
            holder.balance.setVisibility(View.VISIBLE);
        }
       // holder.balance.setVisibility(View.VISIBLE);
        holder.edith.setVisibility(View.VISIBLE);
        holder.detail.setVisibility(View.GONE);
        holder.updateStock.setVisibility(View.GONE);
        holder.line_student.setVisibility(View.GONE);
        holder.value_sale.setText("0.0");
        if(onChangeViewStock!=null){
            onChangeViewStock.OnChangeViewStock();
        }
    }
    private void optionsItem(final ViewHolder holder, final Product p, final Integer position){

        holder.balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance(p);
            }
        });

        holder.edith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithProductPrice(p,position,holder);
            }
        });
        holder.less_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.balance.setVisibility(View.GONE);
                holder.edith.setVisibility(View.GONE);
                holder.updateStock.setVisibility(View.VISIBLE);
                lessStock(p,position,holder);
            }
        });

        holder.add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.balance.setVisibility(View.GONE);
                holder.edith.setVisibility(View.GONE);
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

    private void loadIdAndCreatedClient(String name){
        for(int i=0;i < students.size();++i ){
            if(students.get(i).name.equals(name)){
                clientId= students.get(i).id;
                client_created=students.get(i).created;
                client_name=name;
            }
        }
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
                //clientId=getIdByName(item);
                loadIdAndCreatedClient(item);
                //client_name=item;
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

        if(product_sales){
            holder.detail.setText("Salida venta");
            holder.value_sale.setText(String.valueOf(p.price));

            holder.select_payment_method.setVisibility(View.VISIBLE);
            holder.line_value.setVisibility(View.VISIBLE);
        }

        holder.detail.setHint("Elegir detalle");

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mContext, v);
                if(product_sales){
                    createMenuOut(holder,p);
                }else{
                    holder.detail.setText("");
                    createMenuOutBuys(holder);
                }
            }
        });

        holder.load_stock.setText("1");
        holder.less_load.setText("- ");

        holder.date.setVisibility(View.VISIBLE);

        holder.check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.check_ef.isChecked()){
                    holder.check_ef.setChecked(true);
                    holder.check_card.setChecked(false);
                    holder.check_deb.setChecked(false);
                    holder.check_merc_pago.setChecked(false);
                    holder.check_trans.setChecked(false);
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
                    holder.check_merc_pago.setChecked(false);
                    holder.check_trans.setChecked(false);
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
                    holder.check_merc_pago.setChecked(false);
                    holder.check_trans.setChecked(false);
                    hideSoftKeyboard(mContext, holder.itemView);
                }else{
                    holder.check_deb.setChecked(false);
                }
            }
        });

        holder.check_merc_pago.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.check_merc_pago.isChecked()){
                    holder.check_merc_pago.setChecked(true);
                    holder.check_ef.setChecked(false);
                    holder.check_card.setChecked(false);
                    holder.check_deb.setChecked(false);
                    holder.check_trans.setChecked(false);
                    hideSoftKeyboard(mContext, holder.itemView);
                }else{
                    holder.check_merc_pago.setChecked(false);
                }
            }
        });

        holder.check_trans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.check_trans.isChecked()){
                    holder.check_trans.setChecked(true);
                    holder.check_ef.setChecked(false);
                    holder.check_card.setChecked(false);
                    holder.check_deb.setChecked(false);
                    holder.check_merc_pago.setChecked(false);
                    hideSoftKeyboard(mContext, holder.itemView);
                }else{
                    holder.check_trans.setChecked(false);
                }
            }
        });


        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ProgressDialog progressBar = new ProgressDialog(mContext);
                progressBar.setMessage("Cargando..");
                progressBar.show();//displays the progress bar

                String payment_method= getPaymentMethod(holder);

                String stockP=holder.load_stock.getText().toString().trim();
                String detailP=holder.detail.getText().toString().trim();
                String valuep=holder.value_sale.getText().toString().trim();
                String obserP=holder.observation.getText().toString().trim();

                    if(!detailP.equals("Salida ficha") || clientId !=-1l){

                    if (!stockP.matches("") && !detailP.matches("") && !valuep.matches("")) {

                        if (ValidatorHelper.get().isTypeInteger(stockP) && ValidatorHelper.get().isTypeDouble(valuep)) {

                            StockEvent s = new StockEvent(p.id, 0, Integer.valueOf(stockP), p.stock, detailP, Double.valueOf(valuep), payment_method,
                                    obserP, SessionPrefs.get(mContext).getName(), p.price);
                            s.ideal_stock = s.stock_ant + s.stock_in - s.stock_out;
                            p.stock -= Integer.valueOf(stockP);
                            if(!dateSelected.equals("")){
                                s.created = dateSelected;
                            }else{
                                s.created= getExpandedDate();
                            }

                            if (clientId != -1 && detailP.equals("Salida ficha")) {
                                s.client_id = clientId;
                                s.value_for_file = Double.valueOf(holder.price_product.getText().toString().trim());
                                s.client_name=client_name;

                                //chequea si la ficha del cliente fue creada hoy
                                if(DateHelper.get().onlyDate(client_created).equals(DateHelper.get().getOnlyDate(DateHelper.get().getActualDate2()))){
                                    s.today_created_client="true";
                                }else{
                                    s.today_created_client="false";
                                }
                            }

                            ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                                @Override
                                public void onSuccess(StockEvent data) {

                                    progressBar.dismiss();
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
                                                    mContext.startActivity(new Intent(mContext, SalesActivity.class));
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
            return Constants.TYPE_DEBITO;
        }else if(holder.check_card.isChecked()){
            return Constants.TYPE_TARJETA;
        }else if(holder.check_ef.isChecked()){
            return Constants.TYPE_EFECTIVO;
        }else if(holder.check_trans.isChecked()){
            return Constants.TYPE_TRANSFERENCIA;
        }else if(holder.check_merc_pago.isChecked()){
            return Constants.TYPE_MERCADO_PAGO;
        }else{
            return Constants.TYPE_EFECTIVO;
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

                if(product_sales){
                    createMenuIn(holder);
                }else{
                    createMenuInBuys(holder);
                }
            }
        });

        holder.less_load.setText("+ ");

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ProgressDialog progressBar = new ProgressDialog(mContext);
                progressBar.setMessage("Cargando..");
                progressBar.show();

                String stockP = holder.load_stock.getText().toString().trim();
                String detailP = holder.detail.getText().toString().trim();
                String valueP = holder.value_sale.getText().toString().trim();
                String obserP = holder.observation.getText().toString().trim();

                if(valueP.equals("")){
                    valueP="0.0";
                }

                if ( ValidatorHelper.get().isTypeInteger(stockP) && ValidatorHelper.get().isTypeDouble(valueP)) {
                    if(!stockP.matches("") && !detailP.matches("")){
                        StockEvent s = new StockEvent(p.id, Integer.valueOf(stockP), 0, p.stock, detailP,0.0,"",
                                obserP, SessionPrefs.get(mContext).getName(), p.price);
                        s.ideal_stock = s.stock_ant + s.stock_in - s.stock_out;
                        p.stock += Integer.valueOf(stockP);

                        if(!dateSelected.equals("")){
                            s.created=dateSelected;
                        }else{
                            s.created=getExpandedDate();
                        }

                        ApiClient.get().postStockEvent(s, "product", new GenericCallback<StockEvent>() {
                            @Override
                            public void onSuccess(StockEvent data) {
                                progressBar.dismiss();

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

                                                Intent i = new Intent(mContext, BoxMovementsActivity.class);
                                                i.putExtra("NAMEFRAGMENT", "box");
                                                mContext.startActivity(i);
                                               // mContext.startActivity(new Intent(mContext, SalesFragment.class));
                                            }
                                        });

                                snackbar.show();


                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(holder.load_stock.getRootView().getWindowToken(), 0);

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

    private void edithProductPrice(final Product p, final int position, final ViewHolder holder){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.edith_product, null);
        builder.setView(dialogView);


        final EditText brand_to_edith = dialogView.findViewById(R.id.brand_to_edith);
        final EditText art_to_edith = dialogView.findViewById(R.id.art_to_edith);
        final LinearLayout line_santi = dialogView.findViewById(R.id.line_santi);

        if(SessionPrefs.get(mContext).getName().equals("santi") || SessionPrefs.get(mContext).getName().equals("lei") ){
            line_santi.setVisibility(View.VISIBLE);
        }

        final EditText price = dialogView.findViewById(R.id.price);
        final EditText model_to_edith = dialogView.findViewById(R.id.model_to_edith);
        final ImageView im = dialogView.findViewById(R.id.imagebutton);
        final RelativeLayout delete = dialogView.findViewById(R.id.delete);

        loadIcon(im,p.item);

        final TextView original_price = dialogView.findViewById(R.id.original_product_price);
        final TextView type = dialogView.findViewById(R.id.type);
        final TextView brand = dialogView.findViewById(R.id.brand);
        final TextView model = dialogView.findViewById(R.id.model);
        final TextView stock = dialogView.findViewById(R.id.stock);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        model.setText(p.model);
        stock.setText(String.valueOf((p.stock)));
        brand.setText(p.brand);
        type.setText(p.type);
        original_price.setText(String.valueOf(p.price));

        price.setText(String.valueOf(p.price));
        model_to_edith.setText(p.model);
        art_to_edith.setText(p.type);
        brand_to_edith.setText(p.brand);

        final AlertDialog dialog = builder.create();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(p,position);
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!price.getText().toString().trim().matches("")){

                    Product edithedProduct = new Product(p.id, Double.valueOf(price.getText().toString().trim()),model_to_edith.getText().toString().trim());

                    edithedProduct.brand = brand_to_edith.getText().toString().trim();
                    edithedProduct.type = art_to_edith.getText().toString().trim();

                    ApiClient.get().putProduct(edithedProduct, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            p.price = data.price;
                            holder.original_product_price.setText(String.valueOf(data.price));
                            holder.value_sale.setText(String.valueOf(data.price));
                            holder.model.setText(data.model);

                            holder.type.setText(data.type);
                            holder.brand.setText(data.brand);

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

    private void deleteProduct(final Product p,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.delete_product, null);
        builder.setView(dialogView);

        final EditText obs = dialogView.findViewById(R.id.obs);
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

                if(!obs.getText().toString().trim().equals("")){

                    ApiClient.get().deleteProduct(p.id,obs.getText().toString().trim(), new GenericCallback<SpinnerData>() {
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
                }else{
                    Toast.makeText(mContext, "Ingrese detalle de borrado ", Toast.LENGTH_LONG).show();
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


    private void createMenuInBuys(final ViewHolder holder){
        PopupMenu popup = new PopupMenu(mContext, holder.detail);
        popup.getMenuInflater().inflate(R.menu.menu_stock_in_buys, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.in_buy:
                        holder.detail.setText(Constants.MENU_INGRESO_COMPRA);
                        return true;
                    case R.id.in_balance_stock:
                        holder.detail.setText(Constants.MENU_INGRESO_BALANCE_STOCK);
                        return true;
                    case R.id.in_error:
                        holder.detail.setText(Constants.MENU_INGRESO_ERROR_ANTERIOR);
                        return true;
                    case R.id.in_update_app:
                        holder.detail.setText(Constants.MENU_INGRESO_ACTUALIZACION_APP);
                        return true;
                    case R.id.in_consign:
                        holder.detail.setText(Constants.MENU_INGRESO_CONSIGNACION);
                        return true;
                    case R.id.in_dev_luz:
                        holder.detail.setText(Constants.MENU_INGRESO_DEVOLUCION_LUZ);
                        return true;
                    case R.id.in_stock_oferta:
                        holder.detail.setText(Constants.MENU_INGRESO_STOCK_OFERTA);
                        return true;
                    case R.id.in_stock_local:
                        holder.detail.setText(Constants.MENU_INGRESO_STOCK_LOCAL);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

   private void createMenuIn(final ViewHolder holder){
      PopupMenu popup = new PopupMenu(mContext, holder.detail);
      popup.getMenuInflater().inflate(R.menu.menu_stock_in, popup.getMenu());
      popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

          public boolean onMenuItemClick(MenuItem item) {
              switch (item.getItemId()) {

                  case R.id.in_dev:
                      //holder.detail.setText("Ingreso dev");
                      holder.detail.setText(Constants.MENU_INGRESO_DEVOLUCION);
                      return true;
                  case R.id.in_dev_wrong:
                      holder.detail.setText(Constants.MENU_INGRESO_DEVOLUCION_FALLA);
                      return true;

                  case R.id.in_error:
                      holder.detail.setText(Constants.MENU_INGRESO_ERROR_ANTERIOR);
                      return true;

                  default:
                      return false;
              }
          }
      });
      popup.show();
  }

    private void createMenuOutBuys(final ViewHolder holder){

        PopupMenu popup = new PopupMenu(mContext, holder.detail);
        popup.getMenuInflater().inflate(R.menu.menu_stock_out_buys, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                     case R.id.out_luz:
                        holder.detail.setText("paso al stock luz");
                        return true;
                    case R.id.out_local:
                        holder.detail.setText("paso al stock local");
                        return true;
                    case R.id.out_oferta:
                        holder.detail.setText("paso al stock oferta");
                        return true;
                         case R.id.out_falla:
                        holder.detail.setText("Salida dev falla");
                        return true;
                    case R.id.out_balance_stock:
                        holder.detail.setText("Salida balance stock");
                        return true;
                    case R.id.out_consign:
                        holder.detail.setText("Salida articulo consignacion");
                        return true;
                    case R.id.out_error:
                        holder.detail.setText("Resta por error anterior");
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();

    }

    private void createMenuOut(final ViewHolder holder,final Product p){

        PopupMenu popup = new PopupMenu(mContext, holder.detail);
        popup.getMenuInflater().inflate(R.menu.menu_stock_out, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {

                holder.select_payment_method.setVisibility(View.GONE);
                holder.line_value.setVisibility(View.GONE);
                holder.value_sale.setText("0.0");
                switch (item.getItemId()) {
                    case R.id.out_buy:
                        holder.detail.setText("Salida venta");
                        holder.value_sale.setText(String.valueOf(p.price));
                        holder.select_payment_method.setVisibility(View.VISIBLE);
                        holder.line_value.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.out_buy_with_desc:
                        holder.detail.setText("Salida venta con desc");
                        holder.select_payment_method.setVisibility(View.VISIBLE);
                        holder.line_value.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.out_person_file:
                        holder.detail.setText("Salida ficha");

                        holder.select_payment_method.setVisibility(View.GONE);
                        holder.line_value.setVisibility(View.GONE);
                        holder.value_sale.setText("0.0");

                        loadNameClients(holder);
                        return true;

                    case R.id.out_stole:
                        holder.detail.setText("Salida por robo");
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_error:
                        holder.detail.setText("Resta por error anterior");
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_dev:
                        holder.detail.setText("Salida por cambio");
                        holder.select_payment_method.setVisibility(View.VISIBLE);
                        holder.line_value.setVisibility(View.VISIBLE);
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_consign:
                        holder.detail.setText("Salida venta articulo consignacion");
                        holder.value_sale.setText("0.0");
                        return true;

                    case R.id.out_bonif:
                        holder.detail.setText("Salida ficha especial bonificacion");
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_santi:
                        holder.detail.setText("Salida ficha especial santi");
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_gifts:
                        holder.detail.setText("Salida ficha especial campeonato");
                        holder.value_sale.setText("0.0");
                        return true;
                    case R.id.out_regalo:
                        holder.detail.setText("salida por regalo");
                        holder.value_sale.setText("0.0");
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

                        String time= DateHelper.get().getOnlyTime(DateHelper.get().getActualDate2());
                        String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;

                        holder.date.setText(DateHelper.get().getOnlyDate(datePicker));
                        dateSelected=datePicker;
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

}


