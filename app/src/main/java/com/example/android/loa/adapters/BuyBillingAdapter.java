package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnRefreshList;
import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.BuyBilling;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.SpinnerData;

import java.util.List;

public class BuyBillingAdapter extends BaseAdapter<BuyBilling, BuyBillingAdapter.ViewHolder> {
    private Context mContext;

    private OnRefreshList onRefreshlistListener = null;

    public void setOnRefreshlistListener(OnRefreshList listener) {
        onRefreshlistListener = listener;
    }

    public BuyBillingAdapter(Context context, List<BuyBilling> movements) {
        setItems(movements);
        mContext = context;

    }

    public BuyBillingAdapter() {

    }


    public List<BuyBilling> getList() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView billing_number;
        public TextView business_name;
        public TextView type;
        public TextView amount;
        public RelativeLayout date;
        public TextView day;
        public TextView number_date;
        public TextView div;
        public TextView iva;
        public TextView cant_art;

        public TextView user_name;
        public TextView time;
        public LinearLayout info_user;

        public ViewHolder(View v) {
            super(v);
            billing_number = v.findViewById(R.id.number_);
            type = v.findViewById(R.id.type);
            amount = v.findViewById(R.id.amount);
            date = v.findViewById(R.id.date);
            day = v.findViewById(R.id.day);
            number_date = v.findViewById(R.id.number);
            div = v.findViewById(R.id.div);
            business_name = v.findViewById(R.id.business_name);
            iva = v.findViewById(R.id.iva);
            cant_art = v.findViewById(R.id.art_cant);

            user_name = v.findViewById(R.id.user_name);
            info_user = v.findViewById(R.id.info_user);
            time = v.findViewById(R.id.time);

        }
    }

    @Override
    public BuyBillingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_product_billing, parent, false);
        BuyBillingAdapter.ViewHolder vh = new BuyBillingAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(BuyBillingAdapter.ViewHolder vh) {
        if (vh.billing_number != null)
            vh.billing_number.setText(null);
        if (vh.business_name != null)
            vh.business_name.setText(null);
        if (vh.type != null)
            vh.type.setText(null);
        if (vh.amount != null)
            vh.amount.setText(null);
        if (vh.iva != null)
            vh.iva.setText(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final BuyBillingAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final BuyBilling currenMovement = getItem(position);

        holder.day.setText(DateHelper.get().getNameDay(currenMovement.billing_date));
        holder.number_date.setText(DateHelper.get().numberDay(currenMovement.billing_date));

        holder.billing_number.setText("Nº "+currenMovement.number);
        holder.type.setText(currenMovement.type);

        holder.amount.setText("$"+String.valueOf(currenMovement.amount));
        holder.business_name.setText(currenMovement.business_name);
        if(currenMovement.iva.equals("no")){
            holder.iva.setText("s/ IVA");
        }else{
            holder.iva.setText("c/ IVA");
        }
        holder.cant_art.setText(String.valueOf(currenMovement.art_cant));

        holder.user_name.setText(currenMovement.user_name);
        holder.time.setText(DateHelper.get().onlyHourMinut(DateHelper.get().getOnlyTime(currenMovement.created)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.info_user.getVisibility() == View.VISIBLE) {
                    holder.info_user.setVisibility(View.GONE);
                } else {
                    holder.info_user.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.bill_buy_information, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete = dialogView.findViewById(R.id.delete);
                ImageView edith = dialogView.findViewById(R.id.edith);
                edith.setVisibility(View.GONE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       // deleteProducBilling(currenMovement.id,position);

                        confirmDeleteBuyBilling(currenMovement,position,currenMovement.id);

                        dialog.dismiss();
                    }
                });

                TextView bill_date = dialogView.findViewById(R.id.bill_date);
                TextView date = dialogView.findViewById(R.id.obs_date);
                TextView value = dialogView.findViewById(R.id.amount);
                TextView type = dialogView.findViewById(R.id.type);
                TextView business_name = dialogView.findViewById(R.id.business_name);
                TextView number = dialogView.findViewById(R.id.number);


                date.setText(DateHelper.get().changeFormatDate(currenMovement.created));
                value.setText(String.valueOf(currenMovement.amount));
                bill_date.setText(currenMovement.billing_date);

                type.setText(currenMovement.type+" "+((currenMovement.iva.equals("no")) ? "s/ IVA" : "c/ IVA"));
                business_name.setText(currenMovement.business_name);
                number.setText(currenMovement.number);


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                return false;
            }
        });
    }


    private void confirmDeleteBuyBilling(final BuyBilling b, final int position,final Long id){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.confirm_delete, null);
        builder.setView(dialogView);

        final TextView title = dialogView.findViewById(R.id.title);
        final TextView amount = dialogView.findViewById(R.id.amount);
        final TextView number = dialogView.findViewById(R.id.number);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        title.setText(b.business_name);

        amount.setText(String.valueOf(b.amount));
        number.setText(b.number);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               deleteProducBilling(id,position);
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


    private void deleteProducBilling(Long id,final Integer pos){

        ApiClient.get().deleteProductBilling(id, new GenericCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                removeItem(pos);

                if(onRefreshlistListener != null){
                    onRefreshlistListener.onRefreshList();
                }


            }

            @Override
            public void onError(Error error) {

            }
        });

    }

}