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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ParallelMoneyMovement;
import com.example.android.loa.types.BilledType;
import com.example.android.loa.types.Constants;
import com.example.android.loa.types.MoneyMovementPaymentType;
import com.example.android.loa.types.MoneyMovementType;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ParallelMoneyMovementAdapter extends BaseAdapter<ParallelMoneyMovement,ParallelMoneyMovementAdapter.ViewHolder> {
    private Context mContext;
    private String groupby;

    public ParallelMoneyMovementAdapter(Context context, List<ParallelMoneyMovement> movements) {
        setItems(movements);
        mContext = context;
        groupby = "day";

    }

    public void setGroupby(String groupby) {
        this.groupby = groupby;
    }


    public ParallelMoneyMovementAdapter() {

    }

    public List<ParallelMoneyMovement> getList() {
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
        public TextView detail;
        public ImageView billed;


        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            type = v.findViewById(R.id.type);
            value = v.findViewById(R.id.value);
            date = v.findViewById(R.id.date);
            day = v.findViewById(R.id.day);
            number = v.findViewById(R.id.number);
            div = v.findViewById(R.id.div);
            detail = v.findViewById(R.id.detail);
            billed = v.findViewById(R.id.billed);
        }
    }

    @Override
    public ParallelMoneyMovementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_money_movement, parent, false);
        ParallelMoneyMovementAdapter.ViewHolder vh = new ParallelMoneyMovementAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ParallelMoneyMovementAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.type != null)
            vh.type.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ParallelMoneyMovementAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ParallelMoneyMovement currenMovement = getItem(position);

        holder.day.setText(DateHelper.get().getNameDay(currenMovement.created));
        holder.number.setText(DateHelper.get().numberDay(currenMovement.created));

        if (groupby.equals("day")) {
            holder.date.setVisibility(View.GONE);
        } else {
            holder.date.setVisibility(View.VISIBLE);
        }

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(currenMovement.value));
        holder.description.setText(currenMovement.description);
        holder.type.setText(currenMovement.type);
        holder.detail.setText(currenMovement.detail);

        if(currenMovement.billed.equals(Constants.TYPE_REMITO)){
            holder.billed.setVisibility(View.INVISIBLE);
        }else if(currenMovement.billed.equals(Constants.TYPE_FACTURA)){
            holder.billed.setVisibility(View.VISIBLE);
        }

        if (currenMovement.type.equals(Constants.TYPE_GASTO_LOCAL)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.local));
        } else if (currenMovement.type.equals(Constants.TYPE_GASTO_PERSONAL)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.pers));
        } else if (currenMovement.type.equals(Constants.TYPE_GASTO_SANTI)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.santi));
        } else if (currenMovement.type.equals(Constants.TYPE_SANTI)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.exrt));
        } else if (currenMovement.type.equals(Constants.TYPE_MERCADERIA)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.merc));
        } else if (currenMovement.type.equals(Constants.TYPE_SUELDO)) {
            holder.value.setTextColor(mContext.getResources().getColor(R.color.sueldos));
        }

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
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMovement(currenMovement, position);
                        dialog.dismiss();
                    }
                });

                edith.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edithMovement(currenMovement, position);
                        dialog.dismiss();
                    }
                });

                TextView desc = dialogView.findViewById(R.id.description);
                TextView detail = dialogView.findViewById(R.id.detail);
                desc.setText(currenMovement.description);
                detail.setText(currenMovement.detail);
                TextView value = dialogView.findViewById(R.id.value);
                TextView type = dialogView.findViewById(R.id.type);
                type.setText(currenMovement.type);
                value.setText(String.valueOf(currenMovement.value));
                TextView date = dialogView.findViewById(R.id.obs_date);

                date.setText(DateHelper.get().changeFormatDate(currenMovement.created));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                return false;
            }
        });
    }

    private void deleteMovement(final ParallelMoneyMovement e, final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_extraction, null);
        builder.setView(dialogView);

        final TextView date = dialogView.findViewById(R.id.date);
        final TextView title = dialogView.findViewById(R.id.title);
        final TextView value = dialogView.findViewById(R.id.value);
        final TextView desc = dialogView.findViewById(R.id.description);

        title.setText("Movimiento");
        date.setText(e.created);
        value.setText(String.valueOf(e.value));
        desc.setText(e.description);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.get().deleteMoneyMovement(e.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {

                        Toast.makeText(mContext, "Se elimina el movimiento " + e.description, Toast.LENGTH_LONG).show();
                        removeItem(position);
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
    }

    private static <T extends Enum<MoneyMovementPaymentType>> void enumNameToStringArray(MoneyMovementPaymentType[] values, List<String> spinner_type) {
        for (MoneyMovementPaymentType value : values) {
                spinner_type.add(value.getName());
        }
    }

    private void edithMovement(final ParallelMoneyMovement e, final Integer position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_edith_movement, null);
        builder.setView(dialogView);

        final TextView value = dialogView.findViewById(R.id.value);
        final TextView description = dialogView.findViewById(R.id.description);
        final TextView date = dialogView.findViewById(R.id.date);
        final TextView type = dialogView.findViewById(R.id.type);
        final TextView detail = dialogView.findViewById(R.id.detail);
        final LinearLayout line_detail = dialogView.findViewById(R.id.line_detail);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        final Spinner spinnerType = dialogView.findViewById(R.id.spinner_type1);
        final Spinner spinnerDetail = dialogView.findViewById(R.id.spinner_detail);

        final CheckBox check_remito=  dialogView.findViewById(R.id.check_remito);
        final CheckBox check_factura=  dialogView.findViewById(R.id.check_factura);

        if(e.billed.equals(BilledType.SIN_FACTURAR.getName())){
            check_remito.setChecked(true);
        }else if(e.billed.equals(BilledType.FACTURADO.getName())){
            check_factura.setChecked(true);
        }

        check_remito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_remito.isChecked()){
                    check_remito.setChecked(true);
                    check_factura.setChecked(false);
                    e.billed= BilledType.SIN_FACTURAR.getName();

                }else{
                    check_remito.setChecked(false);
                }
            }
        });
        check_factura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_factura.isChecked()){
                    check_factura.setChecked(true);
                    check_remito.setChecked(false);
                    e.billed=BilledType.FACTURADO.getName();
                }else{
                    check_factura.setChecked(false);
                }
            }
        });

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

                ApiClient.get().putMoneyMovement(e, new GenericCallback<ParallelMoneyMovement>() {
                    @Override
                    public void onSuccess(ParallelMoneyMovement data) {
                        updateItem(position, e);
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
