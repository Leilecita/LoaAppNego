package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Interfaces.OnAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.activities.OperationHistoryClientActivity;
import com.example.android.loa.activities.photos.PhotoEdithActivity;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.ReportExtraction;
import com.example.android.loa.network.models.StockEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ClientAdapter extends BaseAdapter<Client,ClientAdapter.ViewHolder>  implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    List<String> listColor=new ArrayList<>();
    Random random;

    private OnAmountChange onAmountChangeListener = null;
    public void setOnAmountCangeListener(OnAmountChange listener){
        onAmountChangeListener = listener;
    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).created));
            return date.getTime();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_clients, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final Client e = getItem(position);

            String dateToShow2 = DateHelper.get().getOnlyDate(DateHelper.get().changeOrderDate(e.created));
           // String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String numberDay = DateHelper.get().numberDay(e.created);

            int count = linear.getChildCount();
            View v = null;
            View v2 = null;

            for (int k = 0; k < count; k++) {
                v = linear.getChildAt(k);
                if (k == 0) {

                    TextView t = (TextView) v;
                    t.setText(dateToShow2);
                }
            }
        }
    }



    public ClientAdapter(Context context, List<Client> clients){
        setItems(clients);
        mContext = context;

        listColor.add("#685c85");
        listColor.add("#4f426b");
        listColor.add("#A49FB8");
        listColor.add("#817699");
        listColor.add("#a197b8");
        listColor.add("#9088a3");
        listColor.add("#C6BCDA");
        random=new Random();

    }

    public ClientAdapter(){

    }

    public List<Client> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text_name;
        public TextView text_value;
        public ImageView add;
        public ImageView cuad_image;

        public TextView firstLetter;

        public ViewHolder(View v){
            super(v);
             text_name= v.findViewById(R.id.text_name);
            text_value= v.findViewById(R.id.text_value);
             add=v.findViewById(R.id.add);
             cuad_image=v.findViewById(R.id.cuad_image);
             firstLetter=v.findViewById(R.id.firstLetter);
        }
    }

    @Override
    public ClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_client,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(ClientAdapter.ViewHolder vh){
        if(vh.text_name!=null)
            vh.text_name.setText(null);
        if(vh.text_value!=null)
            vh.text_value.setText(null);

        if(vh.firstLetter!=null){
            vh.firstLetter.setText(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Client currentClient=getItem(position);

        holder.text_name.setText(currentClient.name);

        holder.firstLetter.setText(String.valueOf(currentClient.name.charAt(0)));

        holder.cuad_image.setColorFilter(Color.parseColor(listColor.get(random.nextInt(listColor.size()))),PorterDuff.Mode.SRC_ATOP);

        Double debt=currentClient.debt;
        if(debt<0){
            holder.text_value.setText(ValuesHelper.get().getIntegerQuantityByLei(Math.abs(debt)));
            holder.text_value.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.text_value.setText(ValuesHelper.get().getIntegerQuantityByLei(debt));
            holder.text_value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_payment, null);
                builder.setView(dialogView);

                final TextView value=  dialogView.findViewById(R.id.value);
                final TextView name=  dialogView.findViewById(R.id.name_user);
                final TextView date=  dialogView.findViewById(R.id.date);
                final LinearLayout date_picker=  dialogView.findViewById(R.id.date_picker);
                final TextView description=  dialogView.findViewById(R.id.description);

                final CheckBox check_card=  dialogView.findViewById(R.id.check_card);
                final CheckBox check_deb=  dialogView.findViewById(R.id.check_deb);
                final CheckBox check_ef=  dialogView.findViewById(R.id.check_ef);
                final CheckBox check_mer=  dialogView.findViewById(R.id.check_mer);

                final TextView text_balance=  dialogView.findViewById(R.id.text_balance);
                final LinearLayout line_balance=  dialogView.findViewById(R.id.line_balance);
                final CheckBox check_balance=  dialogView.findViewById(R.id.check_balance);

                if(SessionPrefs.get(mContext).getName().equals("santi") || SessionPrefs.get(mContext).getName().equals("lei")){
                    line_balance.setVisibility(View.VISIBLE);
                }


                check_balance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(check_balance.isChecked()){
                            text_balance.setVisibility(View.VISIBLE);
                            description.setText("Balance a 0 FUERA LOCAL");
                            if(currentClient.debt< 0){
                                value.setText(String.valueOf(Math.abs(currentClient.debt)));
                            }else{
                                value.setText(String.valueOf(-currentClient.debt));
                            }

                        }else{
                            text_balance.setVisibility(View.GONE);
                            description.setText("");
                            value.setText("");
                        }
                    }
                });

                check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_ef.isChecked()){
                            check_ef.setChecked(true);
                            check_card.setChecked(false);
                            check_deb.setChecked(false);
                            check_mer.setChecked(false);
                        }else{
                            check_ef.setChecked(false);
                        }
                    }
                });

                check_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_card.isChecked()){
                            check_card.setChecked(true);
                            check_ef.setChecked(false);
                            check_deb.setChecked(false);
                            check_mer.setChecked(false);
                        }else{
                            check_card.setChecked(false);
                        }
                    }
                });

                check_deb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_deb.isChecked()){
                            check_deb.setChecked(true);
                            check_ef.setChecked(false);
                            check_card.setChecked(false);
                            check_mer.setChecked(false);
                        }else{
                            check_deb.setChecked(false);
                        }
                    }
                });

                check_mer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_mer.isChecked()){
                            check_mer.setChecked(true);
                            check_ef.setChecked(false);
                            check_card.setChecked(false);
                            check_deb.setChecked(false);
                        }else{
                            check_mer.setChecked(false);
                        }
                    }
                });


                final TextView cancel=dialogView.findViewById(R.id.cancel);
                final Button ok=dialogView.findViewById(R.id.ok);

                name.setText(currentClient.getName());
                date.setText(DateHelper.get().getActualDate());

                date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatePickerDialog datePickerDialog;
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
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

                                        String time=DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());
                                        String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time;
                                        date.setText(datePicker);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });

                final AlertDialog dialog = builder.create();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String valueAmount= value.getText().toString().trim();
                        if(ValidatorHelper.get().isTypeDouble(valueAmount)) {
                            Double val = Double.valueOf(valueAmount);

                            String desc = description.getText().toString().trim();
                            String dateToServer=DateHelper.get().changeFormatDateUserToServer( date.getText().toString());

                            String payment_method="efectivo";
                            if(check_deb.isChecked()){
                                payment_method= "debito";
                            }else if(check_card.isChecked()){
                                payment_method= "tarjeta";
                            }else if(check_ef.isChecked()){
                                payment_method= "efectivo";
                            }else if(check_mer.isChecked()){
                                payment_method= "mercado pago";
                            }


                            Item_file item=new Item_file(0L,currentClient.getId(),desc,val,0d,"","","","","","false",payment_method,"false");
                            //TODO ver fechas
                            item.created=dateToServer;

                            if(currentClient.debt + val == 0){
                                item.settled="true";
                            }

                            if(check_balance.isChecked()){
                                item.balance="true";
                            }

                            item.modify_by=currentClient.employee_creator_id;
                            ApiClient.get().postItemfile(item, new GenericCallback<Item_file>() {
                                @Override
                                public void onSuccess(Item_file data) {
                                   currentClient.debt+=data.value;
                                   updateItem(position,currentClient);
                                   Toast.makeText(mContext, "Transaccion creada para: "+currentClient.getName(), Toast.LENGTH_SHORT).show();

                                    if(onAmountChangeListener!=null){
                                        onAmountChangeListener.loadOperationAcum(true);
                                    }
                                }

                                @Override
                                public void onError(Error error) {
                                    DialogHelper.get().showMessage("Error", "No se pudo crear la transaccion",mContext);
                                }
                            });

                            dialog.dismiss();
                        }else{
                            Toast.makeText(dialogView.getContext(), "El valor debe ser numerico, vuelva a ingresarlo", Toast.LENGTH_LONG).show();
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
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperationHistoryClientActivity.start(mContext,currentClient);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createInfoDialog(currentClient,position);
                return false;
            }
        });
    }


    private void createInfoDialog(final Client c, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_client_information, null);

        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final TextView employee=  dialogView.findViewById(R.id.employee_creator);
        final ImageView delete=  dialogView.findViewById(R.id.deleteuser);
        final ImageView edituser=  dialogView.findViewById(R.id.edituser);
        final ImageView history=  dialogView.findViewById(R.id.historyuser);
        final ImageView call=  dialogView.findViewById(R.id.phone);
        final ImageView mens=  dialogView.findViewById(R.id.mens);

        name.setText(c.getName());
        address.setText(c.getAddress());

        if(c.alternative_phone.equals("")){
            phone.setText(c.getPhone());
        }else{
            phone.setText(c.getPhone()+" / "+c.alternative_phone);
        }

        employee.setText(c.employee_creator_id);
        mens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsapp("",c.phone);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + c.phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                OperationHistoryClientActivity.start(mContext,c);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SessionPrefs.get(mContext).getName().equals("santi") ||SessionPrefs.get(mContext).getName().equals("leila")){
                    deleteUser(c,position);
                }else{
                    Toast.makeText(mContext,"Por seguridad solo lo puede borrar Leila", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithClient(c, position, new OnClientEditedCallback() {
                    @Override
                    public void onUserEdited(Client newClient) {
                        name.setText(newClient.getName());
                        address.setText(newClient.getAddress());
                        phone.setText(newClient.getPhone());
                    }
                });
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void deleteUser( final Client c,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.dialog_delete_client, null);
        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        name.setText(c.getName());
        phone.setText(c.getPhone());
        address.setText(c.getAddress());

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.get().deleteClient(c.getId(), new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(dialogView.getContext(), "El cliente "+c.getName()+" ha sido eliminado.", Toast.LENGTH_LONG).show();
                        removeItem(position);
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","El usuario a eliminar no existe",dialogView.getContext());
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

    private void edithClient(final Client clientToEdith, final int position, final OnClientEditedCallback callback){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_client, null);

        builder.setView(dialogView);

        final EditText nameEdith=dialogView.findViewById(R.id.edith_name);
        final EditText addressEdith=dialogView.findViewById(R.id.edith_address);
        final EditText phoneEdith=dialogView.findViewById(R.id.edith_phone);

        phoneEdith.setText(clientToEdith.getPhone());
        phoneEdith.setTextColor(mContext.getResources().getColor(R.color.word_info));
        addressEdith.setText(clientToEdith.getAddress());
        addressEdith.setTextColor(mContext.getResources().getColor(R.color.word_info));
        nameEdith.setText(clientToEdith.getName());
        nameEdith.setTextColor(mContext.getResources().getColor(R.color.word_info));

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                final String nameNew= nameEdith.getText().toString().trim();
                String addressNew= addressEdith.getText().toString().trim();
                String phoneNew= phoneEdith.getText().toString().trim();

                clientToEdith.setName(nameNew);
                clientToEdith.setAddress(addressNew);
                clientToEdith.setPhone(phoneNew);

                ApiClient.get().putClient(clientToEdith, new GenericCallback<Client>() {
                    @Override
                    public void onSuccess(Client data) {
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "El cliente ha sido editado",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al editar usuario",mContext);
                    }
                });

                dialog.dismiss();
                callback.onUserEdited(clientToEdith);
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

    interface OnClientEditedCallback {
        void onUserEdited(Client client);
    }


    public void sendWhatsapp(String text, String phone){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+549"+phone));
            mContext.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Context ctx, View view)
    {
        InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
/*


  AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_debt, null);
                builder.setView(dialogView);

                final TextView value=  dialogView.findViewById(R.id.value);
                final TextView name=  dialogView.findViewById(R.id.name_user);
                final TextView date=  dialogView.findViewById(R.id.date);
                final TextView date_picker=  dialogView.findViewById(R.id.date_picker);
                final TextView description=  dialogView.findViewById(R.id.description);
                final TextView brand=  dialogView.findViewById(R.id.brand);
                final TextView size=  dialogView.findViewById(R.id.size);
                final TextView product_kind=  dialogView.findViewById(R.id.product_kind);
                final TextView code=  dialogView.findViewById(R.id.code);
                final CheckBox checkBox =  dialogView.findViewById(R.id.checkBoxCancelar);

                final LinearLayout line_payment=  dialogView.findViewById(R.id.select_payment_method);
                final CheckBox check_card=  dialogView.findViewById(R.id.check_card);
                final CheckBox check_deb=  dialogView.findViewById(R.id.check_deb);
                final CheckBox check_ef=  dialogView.findViewById(R.id.check_ef);

                final CheckBox retire_product=  dialogView.findViewById(R.id.out_product);
                final CheckBox not_retire=  dialogView.findViewById(R.id.not_out);

                retire_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(retire_product.isChecked()){
                            retire_product.setChecked(true);
                            not_retire.setChecked(false);
                        }else{
                            retire_product.setChecked(false);
                        }
                    }
                });

                not_retire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(not_retire.isChecked()){
                            not_retire.setChecked(true);
                            retire_product.setChecked(false);
                        }else{
                            not_retire.setChecked(false);
                        }
                    }
                });

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        hideSoftKeyboard(mContext,buttonView);
                        if(checkBox.isChecked()){
                            checkBox.setChecked(true);
                            line_payment.setVisibility(View.VISIBLE);
                        }else{
                            checkBox.setChecked(false);
                            line_payment.setVisibility(View.GONE);
                        }
                    }
                });

                check_ef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_ef.isChecked()){
                            check_ef.setChecked(true);
                            check_card.setChecked(false);
                            check_deb.setChecked(false);
                        }else{
                            check_ef.setChecked(false);
                        }
                    }
                });

                check_card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_card.isChecked()){
                            check_card.setChecked(true);
                            check_ef.setChecked(false);
                            check_deb.setChecked(false);
                        }else{
                            check_card.setChecked(false);
                        }
                    }
                });

                check_deb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(check_deb.isChecked()){
                            check_deb.setChecked(true);
                            check_ef.setChecked(false);
                            check_card.setChecked(false);
                        }else{
                            check_deb.setChecked(false);
                        }
                    }
                });


                final TextView cancel=dialogView.findViewById(R.id.cancel);
                final Button ok=dialogView.findViewById(R.id.ok);

                name.setText(currentClient.getName());
                date.setText(DateHelper.get().getActualDate());

                date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatePickerDialog datePickerDialog;
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
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

                                        String time=DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());
                                        String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time;
                                        date.setText(datePicker);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });

                final AlertDialog dialog = builder.create();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String valueAmount= value.getText().toString().trim();
                        if(ValidatorHelper.get().isTypeDouble(valueAmount)) {
                            Double val = Double.valueOf(valueAmount);
                            if(!checkBox.isChecked()){
                                val = val * (-1);
                            }

                            String desc = description.getText().toString().trim();
                            String brandT = brand.getText().toString().trim();
                            String sizeT = size.getText().toString().trim();
                            String codeT = code.getText().toString().trim();
                            String  product_kindT= product_kind.getText().toString().trim();
                            String dateToServer=DateHelper.get().changeFormatDateUserToServer( date.getText().toString());

                            String payment_method="efectivo";
                            String retired_product="false";
                            if(check_deb.isChecked()){
                                payment_method= "debito";
                            }else if(check_card.isChecked()){
                                payment_method= "tarjeta";
                            }else if(check_ef.isChecked()){
                                payment_method= "efectivo";
                            }

                            if(not_retire.isChecked()){
                                retired_product="false";
                            }else if(retire_product.isChecked()){
                                retired_product="true";
                            }

                            Item_file item=new Item_file(0L,currentClient.getId(),desc,val,0d,"",brandT,codeT,sizeT,product_kindT,"false",payment_method,retired_product);
                            //TODO ver fechas
                            item.created=dateToServer;

                            if(currentClient.debt + val == 0){
                                item.settled="true";
                            }

                            item.modify_by=currentClient.employee_creator_id;
                            ApiClient.get().postItemfile(item, new GenericCallback<Item_file>() {
                                @Override
                                public void onSuccess(Item_file data) {
                                   currentClient.debt+=data.value;
                                   updateItem(position,currentClient);
                                   Toast.makeText(mContext, "Transaccion creada para: "+currentClient.getName(), Toast.LENGTH_SHORT).show();

                                    if(onAmountChangeListener!=null){
                                        onAmountChangeListener.loadOperationAcum(true);
                                    }

                                    if(data.retired_product.equals("true")){
                                        mContext.startActivity(new Intent(mContext, ProductsActivity.class));
                                    }
                                }

                                @Override
                                public void onError(Error error) {
                                    DialogHelper.get().showMessage("Error", "No se pudo crear la transaccion",mContext);
                                }
                            });
                            dialog.dismiss();
                        }else{
                            Toast.makeText(dialogView.getContext(), "El valor debe ser numerico, vuelva a ingresarlo", Toast.LENGTH_LONG).show();
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
 */