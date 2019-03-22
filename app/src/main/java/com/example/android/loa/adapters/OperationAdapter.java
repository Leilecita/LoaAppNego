package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Operation;

import java.security.DigestOutputStream;
import java.util.Calendar;
import java.util.List;

public class OperationAdapter extends  BaseAdapter<Operation,OperationAdapter.ViewHolder> {

    private Context mContext;
    private Double mTotalAmount;

    private OnAmountChange onAmountChangeListener = null;
    public void setOnAmountCangeListener(OnAmountChange listener){
        onAmountChangeListener = listener;
    }

    public OperationAdapter(Context context, List<Operation> items) {
        setItems(items);
        mContext = context;
        mTotalAmount=0.0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_valueOut;
        public TextView tot;
        public TextView previous_balance;
        public TextView text_date;
        public TextView description;
        public TextView brand;
        public TextView product_kind;
        public TextView code;
        public TextView settled;
        public LinearLayout text_color;
        public LinearLayout item_layout;


        public ViewHolder(View v) {
            super(v);
            text_valueOut = v.findViewById(R.id.text_valueOut);
            previous_balance = v.findViewById(R.id.previous_balance);
            tot = v.findViewById(R.id.tot);
            text_date = v.findViewById(R.id.text_date);
            description = v.findViewById(R.id.description);
            brand = v.findViewById(R.id.brand);
            code = v.findViewById(R.id.code);
            product_kind = v.findViewById(R.id.product_kind);
            settled = v.findViewById(R.id.settled);
            item_layout = v.findViewById(R.id.item_layout);

        }
    }

    @Override
    public OperationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_history_file2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void setTotalAmount(Double tot){
        mTotalAmount=tot;
    }


    public void clearViewHolder(OperationAdapter.ViewHolder vh) {
        if (vh.tot != null)
            vh.tot.setText(null);
        if (vh.text_valueOut != null)
            vh.text_valueOut.setText(null);
        if (vh.text_date != null)
            vh.text_date.setText(null);
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.code != null)
            vh.code.setText(null);
        if (vh.product_kind != null)
            vh.product_kind.setText(null);

        if (vh.brand != null)
            vh.brand.setText(null);
        if (vh.settled != null)
            vh.settled.setText(null);
        if (vh.previous_balance != null)
            vh.previous_balance.setText(null);
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final Operation currentOperation = getItem(position);

        holder.tot.setText(String.valueOf(Math.abs(currentOperation.previous_balance+currentOperation.value)));
        if(currentOperation.previous_balance+currentOperation.value <0){
            holder.tot.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.tot.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }

        holder.text_date.setText(DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(currentOperation.created)));
        if(currentOperation.settled.equals("true")){
            holder.settled.setText("SALDADO A 0");
        }
        holder.brand.setText(checkEmpty(currentOperation.brand));
        holder.code.setText(checkEmpty(currentOperation.code).toUpperCase());
        holder.product_kind.setText(checkEmpty(currentOperation.product_kind));
        holder.previous_balance.setText(String.valueOf(currentOperation.previous_balance));
        if(currentOperation.previous_balance<0){
            holder.previous_balance.setTextColor(mContext.getResources().getColor(R.color.loa_red));
            holder.previous_balance.setText(String.valueOf(Math.abs(currentOperation.previous_balance)));
        }else{
            holder.previous_balance.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }

        Double value= currentOperation.value;
        if(value <0){
            holder.text_valueOut.setText(String.valueOf(Math.abs(value)));
            holder.text_valueOut.setTextColor(mContext.getResources().getColor(R.color.word));
        }else{
            holder.text_valueOut.setText(String.valueOf(value));
            holder.text_valueOut.setTextColor(mContext.getResources().getColor(R.color.loa_green));

          //  holder.text_valueIn.setText(String.valueOf(value));
        }
        holder.description.setText(checkEmpty(currentOperation.description));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_model_info_operation, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete=  dialogView.findViewById(R.id.delete);
                ImageView edith=  dialogView.findViewById(R.id.edith);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOperation(currentOperation,position);
                        dialog.dismiss();
                    }
                });

                edith.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edithOperation(currentOperation,position);
                    }
                });

                TextView size=  dialogView.findViewById(R.id.size);
                size.setText(currentOperation.size);
                TextView value=  dialogView.findViewById(R.id.value);
                TextView prod=  dialogView.findViewById(R.id.product_kind);
                prod.setText(currentOperation.product_kind);
                value.setText(String.valueOf(currentOperation.value));
                TextView date=  dialogView.findViewById(R.id.obs_date);


                date.setText(DateHelper.get().serverToUserFormatted(currentOperation.created));


                dialog.show();

                return false;
            }
        });

    }
    private String checkEmpty(String text){
        if(text.equals("")){
            return "  -";

        }else{
            return text;
        }
    }

    private void deleteOperation(final Operation op,final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_operation, null);
        builder.setView(dialogView);

        final TextView date= dialogView.findViewById(R.id.date);
        final TextView value= dialogView.findViewById(R.id.value);
        final TextView desc= dialogView.findViewById(R.id.description);

        date.setText(op.created);
        value.setText(String.valueOf(op.value));
        desc.setText(op.description);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.get().deleteItemFile(op.item_file_id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(mContext,"Se elimina la operacion "+op.description,Toast.LENGTH_LONG).show();
                        removeItem(position);

                       if(onAmountChangeListener!=null){
                            onAmountChangeListener.loadOperationAcum(true);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo eliminar la operacion",mContext);
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

    private void edithOperation(final Operation op, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_operation, null);
        builder.setView(dialogView);

        final TextView value= dialogView.findViewById(R.id.value);

        final TextView edit_valuePositive= dialogView.findViewById(R.id.edit_valuePositive);
        final TextView edit_valueNegative= dialogView.findViewById(R.id.edit_valueNegative);
        final TextView sizeT=dialogView.findViewById(R.id.size);
        final TextView brandT =dialogView.findViewById(R.id.brand);
        final TextView codeT =dialogView.findViewById(R.id.code);
        final TextView product_kindT =dialogView.findViewById(R.id.product_kind);
        final TextView descriptionT =dialogView.findViewById(R.id.description);
        final TextView edit_date =dialogView.findViewById(R.id.edit_date);
        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final ImageView date_picker =dialogView.findViewById(R.id.date_picker);
        final Button ok =dialogView.findViewById(R.id.ok);

        if(op.description.isEmpty()){
            descriptionT.setHint(" - ");
            descriptionT.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));
        }else{
            descriptionT.setHint(op.description);
        }

        sizeT.setText(op.size);
        brandT.setText(op.brand);
        codeT.setText(op.code);
        product_kindT.setText(op.product_kind);

        value.setText((op.value < 0) ? String.valueOf(op.value) : String.valueOf("+ "+op.value));

        edit_date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().changeFormatDate(op.created))));
        edit_date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" 00:00:00" ;
                                edit_date.setText(datePicker);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueTransPositive=edit_valuePositive.getText().toString().trim();
                String valueTransNegative=edit_valueNegative.getText().toString().trim();
                String dateTrans=edit_date.getText().toString().trim();
                String codeOp=codeT.getText().toString().trim();
                String brandOp=brandT.getText().toString().trim();
                String productOp=product_kindT.getText().toString().trim();
                String sizeOp=sizeT.getText().toString().trim();
                String descOp=descriptionT.getText().toString().trim();

                if(!valueTransPositive.matches("")) {
                    if (ValidatorHelper.get().isTypeDouble(valueTransPositive)) {
                        op.value=Double.valueOf(valueTransPositive);
                    } else {
                        Toast.makeText(dialogView.getContext(), " Tipo de precio no valido ", Toast.LENGTH_LONG).show();
                    }
                }else if(!valueTransNegative.matches("")) {
                    if (ValidatorHelper.get().isTypeDouble(valueTransNegative)) {
                        op.value=Double.valueOf(valueTransNegative)*(-1);
                    } else {
                        Toast.makeText(dialogView.getContext(), " Tipo de precio no valido ", Toast.LENGTH_LONG).show();
                    }
                }

                if(!codeOp.matches("")) {
                    op.code=codeOp;
                }

                if(!brandOp.matches("")) {
                    op.brand=brandOp;
                }
                if(!sizeOp.matches("")) {
                    op.size=sizeOp;
                }
                if(!productOp.matches("")) {
                    op.product_kind=productOp;
                }
                if(!descOp.matches("")) {
                    op.description=descOp;
                }
                if(!dateTrans.matches("")) {
                    op.created=DateHelper.get().userToServer(dateTrans);
                    Toast.makeText(dialogView.getContext(), " fecha "+dateTrans, Toast.LENGTH_LONG).show();
                }

                Item_file item=new Item_file();
                item.value=op.value;
                item.description=op.description;
                item.created=op.created;
                item.id=op.item_file_id;
                item.observation=op.observation;
                item.size=op.size;
                item.code=op.code;
                item.brand=op.brand;
                item.product_kind=op.product_kind;

                ApiClient.get().putItemFile(item, new GenericCallback<Item_file>() {
                    @Override
                    public void onSuccess(Item_file data) {
                        updateItem(position,op);
                        if(onAmountChangeListener!=null){
                            onAmountChangeListener.loadOperationAcum(true);
                        }
                        Toast.makeText(mContext,"La operación se ha modificado con éxito" ,Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","No se pudo modificar la operación",mContext);
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
        dialog.show();

    }


}