package com.example.android.loa.adapters;

import android.app.DatePickerDialog;
import android.content.Context;

import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.Interfaces.OnAmountSaleChange;
import com.example.android.loa.Interfaces.OnChangeViewStock;
import com.example.android.loa.R;

import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Income;
import com.example.android.loa.network.models.ReportStockEvent;

import java.util.Calendar;
import java.util.List;

public class IncomesAdapter  extends BaseAdapter<Income,IncomesAdapter.ViewHolder>  {

    private Context mContext;
    private String item_date="";

    private Integer prevPosOpenView;
    private Boolean isModel;

    private String dateSelected="";

    private OnAmountSaleChange onAmountSaleChange= null;
    public void setOnAmountSaleChange(OnAmountSaleChange lister){
        onAmountSaleChange=lister;
    }

    public IncomesAdapter(Context context, List<Income> incomes) {
        setItems(incomes);
        mContext = context;
    }

    public IncomesAdapter() {

    }

    public List<Income> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView year;
        public TextView value;
        public TextView date;

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

         public CheckBox retire_product;
         public CheckBox not_retire;

        public TextView name_edith;
        public TextView address_edith;
        public TextView product_value_edith;
        public TextView phone_edith;

        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            // year = v.findViewById(R.id.year);
            value = v.findViewById(R.id.value);
            date = v.findViewById(R.id.date);

            line_edit= v.findViewById(R.id.line_edit);
            value_edit= v.findViewById(R.id.value_edit);
            date_edit= v.findViewById(R.id.date_edit);
            payment_method= v.findViewById(R.id.payment_method);
            detail= v.findViewById(R.id.detail);
            date_edit= v.findViewById(R.id.date_edit);
            done= v.findViewById(R.id.done);
            close= v.findViewById(R.id.close);

            name_edith= v.findViewById(R.id.edith_name);
            address_edith= v.findViewById(R.id.edith_address);
            product_value_edith= v.findViewById(R.id.edith_value_product);
            phone_edith= v.findViewById(R.id.edith_phone);

            select_payment_method= v.findViewById(R.id.select_payment_method);
            check_ef= v.findViewById(R.id.check_ef);
            check_deb= v.findViewById(R.id.check_deb);
            check_card= v.findViewById(R.id.check_card);

            retire_product=  v.findViewById(R.id.out_product);
            not_retire=  v.findViewById(R.id.not_out);
        }
    }

    @Override
    public IncomesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_income, parent, false);
        IncomesAdapter.ViewHolder vh = new IncomesAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(IncomesAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
        if (vh.date != null)
            vh.date.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final IncomesAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Income income = getItem(position);

        holder.phone_edith.setText(income.phone);
        holder.name_edith.setText(income.name);
        holder.address_edith.setText(income.address);
        holder.product_value_edith.setText(String.valueOf(income.value_product));

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(income.value));
        holder.description.setText(income.name);

        if(income.payment_method.equals("efectivo")){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }else{
            holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
            holder.value.setTypeface(Typeface.DEFAULT_BOLD);
        }

        item_date=income.created;
        //edit
        load_checks(holder,income);

        holder.value_edit.setText(String.valueOf(income.value));
        holder.detail.setText(income.description);
        holder.date_edit.setText(DateHelper.get().getOnlyDate(income.created));
        holder.payment_method.setText(income.payment_method);

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

        if(income.retired_product.equals("true")){
            holder.retire_product.setChecked(true);
        }else{
            holder.not_retire.setChecked(true);
        }

        holder.retire_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.retire_product.isChecked()){
                    holder.retire_product.setChecked(true);
                    holder.not_retire.setChecked(false);
                }else{
                    holder.retire_product.setChecked(false);
                }
            }
        });

        holder.not_retire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(holder.not_retire.isChecked()){
                    holder.not_retire.setChecked(true);
                    holder.retire_product.setChecked(false);
                }else{
                    holder.not_retire.setChecked(false);
                }
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                String val=holder.value_edit.getText().toString().trim();
                String retired_product="false";
                if(ValidatorHelper.get().isTypeDouble(val)){

                    if(holder.retire_product.isChecked()){
                        retired_product="true";
                    }else if(holder.retire_product.isChecked()){
                        retired_product="false";
                    }


                    Income in= new Income(holder.detail.getText().toString().trim(),"",Double.valueOf(val),getPaymentMethod(holder),retired_product,
                            "","","",null);
                    in.id=income.id;
                    in.created=item_date;

                    ApiClient.get().putIncome(in, new GenericCallback<Income>() {
                        @Override
                        public void onSuccess(Income data) {
                            hideSoftKeyboard(mContext,v);

                            holder.line_edit.setVisibility(View.GONE);
                            holder.select_payment_method.setVisibility(View.GONE);

                            if(data.retired_product.equals("false")){
                                income.retired_product="false";
                                holder.not_retire.setChecked(true);
                                holder.retire_product.setChecked(false);
                            }else{
                                income.retired_product="true";
                                holder.not_retire.setChecked(false);
                                holder.retire_product.setChecked(true);
                            }

                            updateItem(position,data);
                            if(onAmountSaleChange!=null){
                                onAmountSaleChange.onAmountSalesChange();
                            }

                        }
                        @Override
                        public void onError(Error error) {

                        }
                    });
                }
            }
        });

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mContext,v);
                holder.line_edit.setVisibility(View.GONE);
            }
        });

        holder.date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(holder);
            }
        });

    }

    public static void hideSoftKeyboard(Context ctx, View view)
    {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    private String getPaymentMethod(IncomesAdapter.ViewHolder holder){

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
    private void load_checks(final IncomesAdapter.ViewHolder holder, Income current){

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

    private void selectDate(final IncomesAdapter.ViewHolder holder){

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
