package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.activities.BoxPhotoActivity;
import com.example.android.loa.activities.ExtractionsActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;

import java.sql.Date;
import java.util.List;

public class BoxAdapter  extends BaseAdapter<Box,BoxAdapter.ViewHolder> {
    private Context mContext;

    public BoxAdapter(Context context, List<Box> extractions) {
        setItems(extractions);
        mContext = context;
    }

    public BoxAdapter() {

    }

    public List<Box> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView year;
        public TextView counted_sale;
        public TextView card;
        public TextView total_amount;
        public TextView rest_box;
        public TextView dep;

        public ViewHolder(View v) {
            super(v);
            counted_sale = v.findViewById(R.id.venta_ctdo);
            card = v.findViewById(R.id.card);
            total_amount = v.findViewById(R.id.total_amount);
            rest_box = v.findViewById(R.id.rest_box);
            dep = v.findViewById(R.id.dep);
            date = v.findViewById(R.id.date);
            year = v.findViewById(R.id.year);
        }
    }

    @Override
    public BoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_box_2, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(BoxAdapter.ViewHolder vh) {
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.year != null)
            vh.year.setText(null);
        if (vh.card != null)
            vh.card.setText(null);
        if (vh.counted_sale != null)
            vh.counted_sale.setText(null);
        if (vh.total_amount != null)
            vh.total_amount.setText(null);
        if (vh.rest_box != null)
            vh.rest_box.setText(null);
        if (vh.dep != null)
            vh.dep.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Box currentBox = getItem(position);

        holder.counted_sale.setText(MathHelper.get().getIntegerQuantity(currentBox.counted_sale));
        holder.card.setText(MathHelper.get().getIntegerQuantity(currentBox.credit_card));

        holder.total_amount.setText(MathHelper.get().getIntegerQuantity(currentBox.total_box));
        holder.rest_box.setText(MathHelper.get().getIntegerQuantity(currentBox.rest_box));
        holder.dep.setText(MathHelper.get().getIntegerQuantity(currentBox.deposit));

        if (currentBox.rest_box != currentBox.total_box - currentBox.deposit) {
            holder.rest_box.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.rest_box.setTextColor(mContext.getResources().getColor(R.color.word));
        }


        if(currentBox.total_box != currentBox.rest_box_ant + currentBox.counted_sale){
            holder.total_amount.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.total_amount.setTextColor(mContext.getResources().getColor(R.color.word));
        }

        holder.dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, ExtractionsActivity.class);
                intent.putExtra("DATE",currentBox.created);
                intent.putExtra("NEXTDATE",DateHelper.get().getNextDay(currentBox.created));
                mContext.startActivity(intent);
            }
        });
        final String dateToShow=DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(currentBox.created));

        holder.date.setText(DateHelper.get().onlyDayMonth(dateToShow));
        //holder.year.setText(DateHelper.get().getOnlyYear(dateToShow));

        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,DateHelper.get().onlyDayMonth(dateToShow)+"/"+ DateHelper.get().getOnlyYear(dateToShow),Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxPhotoActivity.start(mContext,currentBox.id,currentBox.image_url,dateToShow);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_information_box, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete=  dialogView.findViewById(R.id.delete);
                ImageView edith=  dialogView.findViewById(R.id.edith);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBox(currentBox,position);
                        dialog.dismiss();
                    }
                });

                edith.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edithBox(currentBox,position);
                        dialog.dismiss();
                    }
                });

                TextView desc=  dialogView.findViewById(R.id.detail);
                desc.setText(currentBox.detail);
                TextView countedSale=  dialogView.findViewById(R.id.counted_sale);
                TextView creditCard=  dialogView.findViewById(R.id.credit_card);
                TextView total_box=  dialogView.findViewById(R.id.total_box);
                TextView rest_box=  dialogView.findViewById(R.id.rest_box);
                TextView rest_box_ant=  dialogView.findViewById(R.id.rest_box_ant);
                TextView deposit=  dialogView.findViewById(R.id.deposit);

                countedSale.setText(String.valueOf(currentBox.counted_sale));
                creditCard.setText(String.valueOf(currentBox.credit_card));
                total_box.setText(String.valueOf(currentBox.total_box));
                rest_box.setText(String.valueOf(currentBox.rest_box));
                rest_box_ant.setText(String.valueOf(currentBox.rest_box_ant));
                deposit.setText(String.valueOf(currentBox.deposit));
                TextView date=  dialogView.findViewById(R.id.date);

               // date.setText(DateHelper.get().serverToUserFormatted(currentBox.created));
                date.setText(DateHelper.get().changeFormatDate(currentBox.created));
                dialog.show();
                return false;
            }
        });
    }

    private void deleteBox(final Box b,final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_box, null);
        builder.setView(dialogView);

        final TextView date= dialogView.findViewById(R.id.date);
        final TextView desc= dialogView.findViewById(R.id.detail);

        date.setText(b.created);
        desc.setText(b.detail);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ApiClient.get().deleteBox(b.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(mContext,"Se elimina la caja "+b.detail,Toast.LENGTH_LONG).show();
                        removeItem(position);
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo eliminar la caja",mContext);
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

    private void edithBox(final Box b, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_box, null);
        builder.setView(dialogView);

        final TextView counted_sale= dialogView.findViewById(R.id.counted_sale);
        final TextView credit_card= dialogView.findViewById(R.id.credit_card);
        final TextView total_box= dialogView.findViewById(R.id.total_box);
        final TextView rest_box =dialogView.findViewById(R.id.rest_box);
        final TextView rest_box_ant =dialogView.findViewById(R.id.rest_box_ant);
        final TextView date =dialogView.findViewById(R.id.date);
        final TextView dep =dialogView.findViewById(R.id.deposit);
        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        counted_sale.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.counted_sale)));
        credit_card.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.credit_card)));
        total_box.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.total_box)));
        rest_box.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.rest_box)));
        rest_box_ant.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.rest_box_ant)));
        dep.setText(String.valueOf(MathHelper.get().getIntegerQuantity(b.deposit)));

        dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " Se modifica de la pantalla 'Extracciones' ",Toast.LENGTH_LONG).show();
            }
        });

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(b.created))));
        date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ValidatorHelper.get().isTypeDouble(counted_sale.getText().toString().trim())
                        && ValidatorHelper.get().isTypeDouble(credit_card.getText().toString().trim())
                        && ValidatorHelper.get().isTypeDouble(total_box.getText().toString().trim())
                        && ValidatorHelper.get().isTypeDouble(rest_box.getText().toString().trim())
                        && ValidatorHelper.get().isTypeDouble(rest_box_ant.getText().toString().trim())
                       ){

                    b.counted_sale=Double.valueOf(counted_sale.getText().toString().trim());
                    b.credit_card=Double.valueOf(credit_card.getText().toString().trim());
                    b.total_box=Double.valueOf(total_box.getText().toString().trim());
                    b.rest_box=Double.valueOf(rest_box.getText().toString().trim());
                    b.rest_box_ant=Double.valueOf(rest_box_ant.getText().toString().trim());
                    // b.deposit=Double.valueOf(dep.getText().toString().trim());

                    ApiClient.get().putBox(b, new GenericCallback<Box>() {
                        @Override
                        public void onSuccess(Box data) {
                            updateItem(position,b);
                            Toast.makeText(mContext,"La caja se ha modificado con éxito" ,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error","No se pudo modificar la caja",mContext);
                        }
                    });
                    dialog.dismiss();
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(dialogView.getContext(), " Tipo de valor no valido ", Toast.LENGTH_LONG).show();
                }
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
