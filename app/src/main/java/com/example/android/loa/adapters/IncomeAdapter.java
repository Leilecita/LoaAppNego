package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;

import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Income;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IncomeAdapter extends BaseAdapter<Income, IncomeAdapter.ViewHolder> {

    private Context mContext;

    public IncomeAdapter(Context context, List<Income> incomes) {
        setItems(incomes);
        mContext = context;
    }

    public IncomeAdapter() {

    }

    public List<Income> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView value_prod;
        public TextView value;
        public TextView date;
        public TextView day_number;
        public TextView day_name;

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

        public TextView name;
        public TextView phone;
        public TextView address;
        public ImageView state;
        public LinearLayout change_state_to_pendient;
        public LinearLayout change_state_to_done;
        public LinearLayout cuad_change_state;

        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            name = v.findViewById(R.id.name);
            value = v.findViewById(R.id.value);
            value_prod = v.findViewById(R.id.value_product);
            date = v.findViewById(R.id.date);

            payment_method= v.findViewById(R.id.payment_method);
            detail= v.findViewById(R.id.detail);
            done= v.findViewById(R.id.done);
            close= v.findViewById(R.id.close);

            retire_product=  v.findViewById(R.id.out_product);
            not_retire=  v.findViewById(R.id.not_out);
            state =  v.findViewById(R.id.state);
            change_state_to_pendient =  v.findViewById(R.id.change_to_pendient);
            change_state_to_done =  v.findViewById(R.id.change_to_done);
            cuad_change_state =  v.findViewById(R.id.cuad_change_state);
            day_name =  v.findViewById(R.id.day_name);
            day_number =  v.findViewById(R.id.day_number);
        }
    }

    @Override
    public IncomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_income, parent, false);
        IncomeAdapter.ViewHolder vh = new IncomeAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(IncomeAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.name != null)
            vh.name.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final IncomeAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Income income = getItem(position);

        holder.name.setText(income.name);
        holder.description.setText(income.description);

        holder.day_number.setText(DateHelper.get().numberDay(income.created));
        holder.day_name.setText(DateHelper.get().getNameDay(DateHelper.get().onlyDate(income.created)));

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(income.value));
        holder.value_prod.setText(ValuesHelper.get().getIntegerQuantityByLei(income.value_product));

        if(income.payment_method.equals("efectivo")){
        }else{
            holder.value.setTypeface(Typeface.DEFAULT_BOLD);
        }

        if(income.state.equals("pendient")){
            holder.state.setImageResource(R.mipmap.pendient);
        }else {
            holder.state.setImageResource(R.mipmap.done);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cuad_change_state.getVisibility() == View.VISIBLE){
                    holder.cuad_change_state.setVisibility(View.GONE);
                }else{
                    holder.cuad_change_state.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.change_state_to_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income.state="done";
                changeStateIncome(income,position);
            }
        });
        holder.change_state_to_pendient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                income.state="pendient";
                changeStateIncome(income,position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edithIncome(income,position);
                return false;
            }
        });
    }

    private void changeStateIncome(final Income inc, final Integer position){

        ApiClient.get().putIncome(inc, new GenericCallback<Income>() {
            @Override
            public void onSuccess(Income data) {
                inc.state=data.state;

                updateItem(position,inc);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    private void edithIncome(final Income i,final Integer pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.state_income, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final CheckBox check_card=  dialogView.findViewById(R.id.check_card);
        final CheckBox check_deb=  dialogView.findViewById(R.id.check_deb);
        final CheckBox check_ef=  dialogView.findViewById(R.id.check_ef);

        final TextView name=  dialogView.findViewById(R.id.name);
        final TextView phone=  dialogView.findViewById(R.id.phone);
        final TextView address=  dialogView.findViewById(R.id.adress);
        final TextView value_product=  dialogView.findViewById(R.id.value_product);

        date.setText(i.created);
        description.setText(i.description);
        value.setText(String.valueOf(i.value));
        value_product.setText(String.valueOf(i.value_product));
        name.setText(String.valueOf(i.name));
        phone.setText(String.valueOf(i.phone));
        address.setText(String.valueOf(i.address));


        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);


        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.created=date.getText().toString().trim();
                i.description=description.getText().toString().trim();
                i.value=Double.valueOf(value.getText().toString().trim());
                i.value_product=Double.valueOf(value_product.getText().toString().trim());
                i.name=name.getText().toString().trim();
                i.phone=phone.getText().toString().trim();
                i.address=address.getText().toString().trim();

                ApiClient.get().putIncome(i, new GenericCallback<Income>() {
                    @Override
                    public void onSuccess(Income data) {
                       updateItem(pos,i);
                    }

                    @Override
                    public void onError(Error error) {

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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void hideSoftKeyboard(Context ctx, View view)
    {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    private String getPaymentMethod(IncomeAdapter.ViewHolder holder){

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
    private void load_checks(final IncomeAdapter.ViewHolder holder, Income current){

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

    private void selectDate(final IncomeAdapter.ViewHolder holder){

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

                       // holder.date_edit.setText(DateHelper.get().getOnlyDate(datePicker));


                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}
