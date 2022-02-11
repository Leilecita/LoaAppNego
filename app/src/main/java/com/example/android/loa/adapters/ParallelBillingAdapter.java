package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnRefreshList;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ParallelBilling;
import com.example.android.loa.network.models.ParallelMoneyMovement;
import com.example.android.loa.network.models.ReportParallelBilling;
import com.example.android.loa.network.models.ReportParallelMoneyMovement;
import com.example.android.loa.types.BilledType;
import com.example.android.loa.types.Constants;
import com.example.android.loa.types.MoneyMovementPaymentType;
import com.example.android.loa.types.MoneyMovementType;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParallelBillingAdapter extends BaseAdapter<ParallelBilling,ParallelBillingAdapter.ViewHolder>{
    private Context mContext;

    private OnRefreshList onRefreshlistListener = null;
    public void setOnRefreshlistListener(OnRefreshList listener){
        onRefreshlistListener = listener;
    }

    public ParallelBillingAdapter(Context context, List<ParallelBilling> movements) {
        setItems(movements);
        mContext = context;

    }

    public ParallelBillingAdapter() {

    }


    public List<ParallelBilling> getList() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView type;
        public TextView value;
        public RelativeLayout date;
        public TextView day;
        public TextView number;
        public TextView div;

        public TextView user_name;
        public TextView time;
        public LinearLayout info_user;

        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            type = v.findViewById(R.id.type);
            value = v.findViewById(R.id.value);
            date = v.findViewById(R.id.date);
            day = v.findViewById(R.id.day);
            number = v.findViewById(R.id.number);
            div = v.findViewById(R.id.div);

            user_name = v.findViewById(R.id.user_name);
            info_user = v.findViewById(R.id.info_user);
            time = v.findViewById(R.id.time);

        }
    }

    @Override
    public ParallelBillingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_parallel_billing, parent, false);
        ParallelBillingAdapter.ViewHolder vh = new ParallelBillingAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ParallelBillingAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.type != null)
            vh.type.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ParallelBillingAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ParallelBilling currenMovement = getItem(position);

        holder.day.setText(DateHelper.get().getNameDay(currenMovement.created));
        holder.number.setText(DateHelper.get().numberDay(currenMovement.created));

        holder.description.setText(currenMovement.description);
        holder.type.setText(currenMovement.type);

        holder.value.setText(String.valueOf(currenMovement.amount));

        holder.user_name.setText(currenMovement.user_name);
        holder.time.setText(DateHelper.get().onlyHourMinut(DateHelper.get().getOnlyTime(currenMovement.created)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.info_user.getVisibility() == View.VISIBLE){
                    holder.info_user.setVisibility(View.GONE);
                }else{
                    holder.info_user.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_information_extraction, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete = dialogView.findViewById(R.id.delete);
                ImageView edith = dialogView.findViewById(R.id.edith);
                edith.setVisibility(View.GONE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteParallelBilling(currenMovement, position);
                        dialog.dismiss();
                    }
                });

                TextView desc = dialogView.findViewById(R.id.description);
                LinearLayout detail = dialogView.findViewById(R.id.line_detail);
                detail.setVisibility(View.GONE);
                desc.setText(currenMovement.description);
                TextView value = dialogView.findViewById(R.id.value);
                TextView type = dialogView.findViewById(R.id.type);
                type.setText(currenMovement.type);
                value.setText(String.valueOf(currenMovement.amount));
                TextView date = dialogView.findViewById(R.id.obs_date);

                date.setText(DateHelper.get().changeFormatDate(currenMovement.created));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                return false;
            }
        });
    }

    private void deleteParallelBilling(final ParallelBilling e, final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_extraction, null);
        builder.setView(dialogView);

        final TextView date = dialogView.findViewById(R.id.date);
        final TextView title = dialogView.findViewById(R.id.title);
        final TextView value = dialogView.findViewById(R.id.value);
        final TextView desc = dialogView.findViewById(R.id.description);

        title.setText("Facturacion");
        date.setText(e.created);
        value.setText(String.valueOf(e.amount));
        desc.setText(e.description);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.get().deleteParallelBilling(e.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {

                        Toast.makeText(mContext, "Se elimina la factura " + e.description, Toast.LENGTH_LONG).show();
                        removeItem(position);
                        if(onRefreshlistListener!=null){
                            onRefreshlistListener.onRefreshList();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo eliminar la operacion", mContext);
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

    private static <T extends Enum<MoneyMovementType>> void enumNameToStringArray(MoneyMovementType[] values, List<String> spinner_type) {
        for (MoneyMovementType value : values) {
            if (value.getName().equals(Constants.TYPE_ALL)) {
                spinner_type.add("Tipo");
            } else {
                spinner_type.add(value.getName());
            }
        }
        spinner_type.add("Otro");
    }

    private static <T extends Enum<MoneyMovementPaymentType>> void enumNameToStringArray(MoneyMovementPaymentType[] values, List<String> spinner_type) {
        for (MoneyMovementPaymentType value : values) {
            spinner_type.add(value.getName());
        }
    }
/*
    private void edithMovement(final ParallelBilling e, final Integer position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_edith_movement, null);
        builder.setView(dialogView);

        final TextView value = dialogView.findViewById(R.id.value);
        final TextView description = dialogView.findViewById(R.id.description);
        final TextView other_type = dialogView.findViewById(R.id.other_type);
        final TextView date = dialogView.findViewById(R.id.date);
        final TextView type = dialogView.findViewById(R.id.type);
        final TextView detail = dialogView.findViewById(R.id.detail);
        final LinearLayout line_detail = dialogView.findViewById(R.id.line_detail);
        final LinearLayout line_other_type = dialogView.findViewById(R.id.line_other_type);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        final Spinner spinnerType = dialogView.findViewById(R.id.spinner_type1);
        final Spinner spinnerDetail = dialogView.findViewById(R.id.spinner_detail);

        final CheckBox check_remito=  dialogView.findViewById(R.id.check_remito);
        final CheckBox check_factura=  dialogView.findViewById(R.id.check_factura);


        //SPINNER DETAIL
        final List<String> spinner_detail = new ArrayList<>();
        enumNameToStringArray(MoneyMovementPaymentType.values(), spinner_detail);

        //SPINNER TYPE
        final List<String> spinner_type = new ArrayList<>();
        enumNameToStringArray(MoneyMovementType.values(), spinner_type);

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, spinner_type);
        adapter_type.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(adapter_type);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected = String.valueOf(spinnerType.getSelectedItem());

                if(!itemSelected.equals("Tipo"))
                    type.setText(itemSelected);

                if (itemSelected.equals(Constants.MONEY_SANTI_PAGO_MERCADERIA)) {
                    line_detail.setVisibility(View.VISIBLE);
                }else{
                    line_detail.setVisibility(View.GONE);
                }

                if(itemSelected.equals("Otro")){
                    line_other_type.setVisibility(View.VISIBLE);
                }else{
                    line_other_type.setVisibility(View.GONE);
                }

                ArrayAdapter<String> adapter_detail = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item, spinner_detail);
                adapter_detail.setDropDownViewResource(R.layout.spinner_item);
                spinnerDetail.setAdapter(adapter_detail);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = String.valueOf(spinnerDetail.getSelectedItem());
                detail.setText(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        description.setText(e.description);
        value.setText(String.valueOf(MathHelper.get().getIntegerQuantity(e.value)));
        type.setText(e.type);
        detail.setText(e.detail);

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(e.created))));
        date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(date,e);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueT = value.getText().toString().trim();

                if(!valueT.equals("")){
                    e.value = Double.valueOf(valueT);
                }else{
                    e.value=0.0;
                }

                e.type = type.getText().toString().trim();
                e.detail = detail.getText().toString().trim();
                e.description = description.getText().toString().trim();

                type.setText(e.type);
                description.setText(e.description);
                value.setText(String.valueOf(e.value));

                if(e.type.equals("Otro")){
                    e.type=other_type.getText().toString().trim();
                }

                ApiClient.get().putMoneyMovement(e, new GenericCallback<ParallelMoneyMovement>() {
                    @Override
                    public void onSuccess(ParallelMoneyMovement data) {

                        updateItem(position, data);
                        if(onRefreshlistListener!=null){
                            onRefreshlistListener.onRefreshList();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo modificar la operación", mContext);
                    }
                });

                dialog.dismiss();
                notifyDataSetChanged();
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
*/
    private void selectDate(final TextView date,final ParallelMoneyMovement e){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(mContext,
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

                        String time = DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                        String datePicker = year + "-" + smonthOfYear + "-" + sdayOfMonth + " " + time;
                        date.setText(datePicker);
                        e.created = datePicker;

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }



}
