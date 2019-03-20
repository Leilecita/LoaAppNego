package com.example.android.loa.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnAmountHoursChange;
import com.example.android.loa.Interfaces.OnExtractionsAmountChange;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Item_employee;
import com.example.android.loa.network.models.Operation;

import java.util.List;

import javax.xml.transform.dom.DOMLocator;

public class HourEmployeeAdapter extends  BaseAdapter<Item_employee,HourEmployeeAdapter.ViewHolder> {

    private Context mContext;

    private OnAmountHoursChange onAmountHoursListener = null;
    public void setOnAmountHoursListener(OnAmountHoursChange listener){
        onAmountHoursListener = listener;
    }

    public HourEmployeeAdapter(Context context, List<Item_employee> items) {
        setItems(items);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView time_worked;
        public TextView entry;
        public TextView finish;

        public TextView time_worked_aft;
        public TextView entry_aft;
        public TextView finish_aft;

        public LinearLayout morning;
        public LinearLayout afternoon;

        public ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.date);
            time_worked = v.findViewById(R.id.time_worked);
            entry = v.findViewById(R.id.entry);
            finish = v.findViewById(R.id.finish);
            time_worked_aft = v.findViewById(R.id.time_workedT);
            entry_aft = v.findViewById(R.id.entryT);
            finish_aft = v.findViewById(R.id.finishT);
            morning = v.findViewById(R.id.line1Aft);
            afternoon = v.findViewById(R.id.line2Aft);
        }
    }

    @Override
    public HourEmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_employe_file, parent, false);
        HourEmployeeAdapter.ViewHolder vh = new HourEmployeeAdapter.ViewHolder(v);
        return vh;
    }


    public void clearViewHolder(HourEmployeeAdapter.ViewHolder vh) {
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.time_worked != null)
            vh.time_worked.setText(null);
        if (vh.entry != null)
            vh.entry.setText(null);
        if (vh.finish != null)
            vh.finish.setText(null);
        if (vh.time_worked_aft != null)
            vh.time_worked_aft.setText(null);
        if (vh.entry_aft != null)
            vh.entry_aft.setText(null);
        if (vh.finish_aft != null)
            vh.finish_aft.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final HourEmployeeAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final Item_employee currentItemEmployee = getItem(position);

        String date=DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(currentItemEmployee.created));
        holder.date.setText(DateHelper.get().onlyDayMonth(date));

        holder.entry.setText(currentItemEmployee.entry);
        holder.finish.setText(currentItemEmployee.finish);
        holder.time_worked.setText(String.valueOf(round(currentItemEmployee.time_worked,1)));

        holder.entry_aft.setText(currentItemEmployee.entry_aft);
        holder.finish_aft.setText(currentItemEmployee.finish_aft);
        holder.time_worked_aft.setText(String.valueOf(round(currentItemEmployee.time_worked_aft,1)));

        holder.morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithHour(currentItemEmployee,position,true);
            }
        });

        holder.afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithHour(currentItemEmployee,position,false);
            }
        });

        if(!currentItemEmployee.finish.equals("") && !currentItemEmployee.entry.equals("") && !currentItemEmployee.time_worked.equals("") ){

            if(!((Double.valueOf(currentItemEmployee.finish) - Double.valueOf(currentItemEmployee.entry)) == currentItemEmployee.time_worked)){
                holder.time_worked.setTextColor(mContext.getResources().getColor(R.color.loa_red));
            }
        }

        if(!currentItemEmployee.finish_aft.equals("") && !currentItemEmployee.entry_aft.equals("") && !currentItemEmployee.time_worked_aft.equals("") ){
            if(!((Double.valueOf(currentItemEmployee.finish_aft) - Double.valueOf(currentItemEmployee.entry_aft)) == currentItemEmployee.time_worked_aft)){
                holder.time_worked_aft.setTextColor(mContext.getResources().getColor(R.color.loa_red));
            }
        }

        holder.afternoon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteHour(currentItemEmployee,position);
                return false;
            }
        });

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_info_hour, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete=  dialogView.findViewById(R.id.delete);
                ImageView edith=  dialogView.findViewById(R.id.edith);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteHour(currentItemEmployee,position);
                        dialog.dismiss();
                    }
                });
                edith.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //edithHour(currentItemEmployee,position,true);
                        dialog.dismiss();
                    }
                });

                TextView entry=  dialogView.findViewById(R.id.entry);
                entry.setText(currentItemEmployee.entry);
                TextView finish=  dialogView.findViewById(R.id.finish);
                TextView time_worked=  dialogView.findViewById(R.id.time_worked);
                time_worked.setText(String.valueOf(currentItemEmployee.time_worked));
                finish.setText(String.valueOf(currentItemEmployee.finish));
                TextView date=  dialogView.findViewById(R.id.date);

                date.setText(DateHelper.get().serverToUserFormatted(currentItemEmployee.created));
                dialog.show();

            }
        });*/
    }

    private void deleteHour(final Item_employee it, final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_operation, null);
        builder.setView(dialogView);

        final TextView date= dialogView.findViewById(R.id.date);
        final TextView value= dialogView.findViewById(R.id.value);
        final TextView desc= dialogView.findViewById(R.id.description);

        date.setText(it.created);
        value.setText(String.valueOf(it.time_worked));
        desc.setText(it.observation);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiClient.get().deleteItemEmployee(it.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(mContext,"Se elimina la operacion "+it.observation,Toast.LENGTH_LONG).show();
                        removeItem(position);

                        if(onAmountHoursListener !=null){
                            onAmountHoursListener.onAmountHoursChange();
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

    private void edithHour(final Item_employee e, final Integer position,final boolean morning){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_hour, null);
        builder.setView(dialogView);

        final TextView obs= dialogView.findViewById(R.id.observation);
        final TextView title= dialogView.findViewById(R.id.title);
        final TextView entrada= dialogView.findViewById(R.id.entry);
        final TextView salida= dialogView.findViewById(R.id.out);
        final TextView time_worked= dialogView.findViewById(R.id.time_worked);
        final TextView date =dialogView.findViewById(R.id.date);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final ImageView date_picker =dialogView.findViewById(R.id.date_picker);
        final Button ok =dialogView.findViewById(R.id.ok);



        if(morning){
            title.setText("Horario mañana");
            obs.setText(String.valueOf(e.observation));
            entrada.setText(e.entry);
            salida.setText(e.finish);
            time_worked.setText(String.valueOf(MathHelper.get().getIntegerQuantity(e.time_worked)));
        }else{
            obs.setText(String.valueOf(e.obs_aft));
            entrada.setText(e.entry_aft);
            salida.setText(e.finish_aft);
            time_worked.setText(String.valueOf(MathHelper.get().getIntegerQuantity(e.time_worked_aft)));
            title.setText("Horario tarde");
        }

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(e.created))));
        date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(morning){
                    e.observation=obs.getText().toString().trim();
                    e.entry=entrada.getText().toString().trim();
                    e.finish=salida.getText().toString().trim();
                    e.time_worked=Double.valueOf(time_worked.getText().toString().trim());
                }else{
                    e.obs_aft=obs.getText().toString().trim();
                    e.entry_aft=entrada.getText().toString().trim();
                    e.finish_aft=salida.getText().toString().trim();
                    e.time_worked_aft=Double.valueOf(time_worked.getText().toString().trim());
                }


                ApiClient.get().putItemEmployee(e, new GenericCallback<Item_employee>() {
                    @Override
                    public void onSuccess(Item_employee data) {
                        updateItem(position,e);
                        if(onAmountHoursListener !=null){
                            onAmountHoursListener.onAmountHoursChange();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","No se pudo realizar la operación",mContext);
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


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
