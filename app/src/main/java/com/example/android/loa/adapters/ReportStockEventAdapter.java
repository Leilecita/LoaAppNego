package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;

import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.ReportStockEvent;
import com.example.android.loa.network.models.StockEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportStockEventAdapter extends BaseAdapter<ReportStockEvent,ReportStockEventAdapter.ViewHolder>  {
    private Context mContext;
    private Boolean mHideDetail;

    private String item_date="";

    public ReportStockEventAdapter(Context context, List<ReportStockEvent> events){
        setItems(events);
        mContext = context;

        mHideDetail=true;
    }


    public ReportStockEventAdapter(){

    }

    public List<ReportStockEvent> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView stock_in;
        public TextView stock_out;
        public TextView date;
        public TextView year;

        public TextView item;
        public TextView type;
        public TextView brand;
        public TextView model;
        public TextView value;


        public LinearLayout line_edit;
        public EditText value_edit;
        public TextView date_edit;
        public TextView payment_method;
        public TextView detail;
        public ImageView done;
        public ImageView close;

        public LinearLayout select_payment_method;
        public CheckBox check_ef;
        public CheckBox check_deb;
        public CheckBox check_card;


        public ViewHolder(View v){
            super(v);
            item= v.findViewById(R.id.item);
            stock_out= v.findViewById(R.id.stock_out);
            type= v.findViewById(R.id.type);
            brand= v.findViewById(R.id.brand);
            date= v.findViewById(R.id.date);
            year= v.findViewById(R.id.year);
            value= v.findViewById(R.id.value);

            line_edit= v.findViewById(R.id.line_edit);
            value_edit= v.findViewById(R.id.value_edit);
            date_edit= v.findViewById(R.id.date_edit);
            payment_method= v.findViewById(R.id.payment_method);
            detail= v.findViewById(R.id.detail);
            date_edit= v.findViewById(R.id.date_edit);
            done= v.findViewById(R.id.done);
            close= v.findViewById(R.id.close);

            select_payment_method= v.findViewById(R.id.select_payment_method);
            check_ef= v.findViewById(R.id.check_ef);
            check_deb= v.findViewById(R.id.check_deb);
            check_card= v.findViewById(R.id.check_card);


        }
    }

    @Override
    public ReportStockEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_report_stock_event,parent,false);
        ReportStockEventAdapter.ViewHolder vh = new ReportStockEventAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ReportStockEventAdapter.ViewHolder vh){
        if(vh.stock_in!=null)
            vh.stock_in.setText(null);
        if(vh.stock_out!=null)
            vh.stock_out.setText(null);

        if(vh.date!=null)
            vh.date.setText(null);
        if(vh.year!=null)
            vh.year.setText(null);

        if(vh.value!=null)
            vh.value.setText(null);

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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportStockEventAdapter.ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportStockEvent current=getItem(position);

        holder.stock_out.setText(String.valueOf(current.stock_out));

        if(current.stock_out > 1){
            holder.stock_out.setVisibility(View.VISIBLE);
        }else{
            holder.stock_out.setVisibility(View.GONE);
        }

        holder.value.setText(String.valueOf(current.value));

       // holder.model.setText(current.model);
        holder.item.setText(current.item);
        holder.type.setText(current.type);
        holder.brand.setText(current.brand);

        if(current.payment_method.equals("efectivo")){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.ef));

        }else{
            holder.value.setTextColor(mContext.getResources().getColor(R.color.card));
        }


        holder.value_edit.setText(String.valueOf(current.value));
        holder.date_edit.setText(DateHelper.get().getOnlyDate(current.stock_event_created));

        item_date=current.stock_event_created;

        holder.detail.setText(current.detail);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuOut(holder);
            }
        });
        holder.payment_method.setText(current.payment_method);

        holder.payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.select_payment_method.getVisibility()== View.GONE){
                    holder.select_payment_method.setVisibility(View.VISIBLE);
                }else{
                    holder.select_payment_method.setVisibility(View.GONE);
                }

            }
        });

        holder.date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(holder);
            }
        });


        load_checks(holder,current);

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String val=holder.value_edit.getText().toString().trim();

                if(ValidatorHelper.get().isTypeDouble(val)){
                    ApiClient.get().updateItemStockEventReport(Double.valueOf(val), item_date,getPaymentMethod(holder),
                            holder.detail.getText().toString().trim(),current.stock_event_id, new GenericCallback<ReportStockEvent>() {
                        @Override
                        public void onSuccess(ReportStockEvent data) {
                            updateItem(position,data);
                            holder.line_edit.setVisibility(View.GONE);
                            holder.select_payment_method.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                }else{
                    Toast.makeText(mContext,"Tipo no valido", Toast.LENGTH_LONG).show();
                }

            }
        });

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.line_edit.setVisibility(View.GONE);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.line_edit.getVisibility() == View.GONE){
                    holder.line_edit.setVisibility(View.VISIBLE);


                }else{
                    holder.line_edit.setVisibility(View.GONE);
                }
            }
        });
    }

    private void createMenuOut(final ReportStockEventAdapter.ViewHolder holder){

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

    private void load_checks(final ViewHolder holder, ReportStockEvent current){

        if(current.payment_method.equals("debito")){
            holder.check_deb.setChecked(true);
        }else if(current.payment_method.equals("tarjeta")){
            holder.check_card.setChecked(true);
        }else if(current.payment_method.equals("efectivo")){
            holder.check_ef.setChecked(true);
        }

        holder.check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.check_ef.isChecked()){
                    holder.check_ef.setChecked(true);
                    holder.payment_method.setText("efectivo");
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
                    holder.payment_method.setText("tarjeta");
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
                    holder.payment_method.setText("debito");
                    holder.check_ef.setChecked(false);
                    holder.check_card.setChecked(false);
                }else{
                    holder.check_deb.setChecked(false);
                }
            }
        });
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

                        item_date=datePicker;
                        holder.date_edit.setText(DateHelper.get().getOnlyDate(datePicker));


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }



}