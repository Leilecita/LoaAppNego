package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.activities.OperationHistoryClientActivity;
import com.example.android.loa.activities.PhotoEdithActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Item_file;

import java.util.Calendar;
import java.util.List;

public class ClientAdapter extends BaseAdapter<Client,ClientAdapter.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    public ClientAdapter(Context context, List<Client> clients){
        setItems(clients);
        mContext = context;
    }

    public ClientAdapter(){

    }

    public List<Client> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text_name;
        public TextView text_value;
        public ImageView photo;
        public ImageView add;

        public ViewHolder(View v){
            super(v);
             text_name= v.findViewById(R.id.text_name);
            text_value= v.findViewById(R.id.text_value);
             add=v.findViewById(R.id.add);
             photo=v.findViewById(R.id.photo_user);
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

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Client currentClient=getItem(position);

        holder.text_name.setText(currentClient.name);
        if(currentClient.image_url==null){
            Glide.with(mContext).load(R.drawable.person_color).into(holder.photo);
        }else{
            Glide.with(mContext).load(ApiUtils.getImageUrl(currentClient.image_url)).into(holder.photo);
        }

        Double debt=currentClient.debt;
        if(debt<0){
            holder.text_value.setText(String.valueOf(Math.abs(debt)));
            holder.text_value.setTextColor(mContext.getResources().getColor(R.color.loa_red));
        }else{
            holder.text_value.setText(String.valueOf(debt));
            holder.text_value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
        }


        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog_edith_photo, null);
                final ImageView photo_info=  dialogView.findViewById(R.id.image_user);
                Glide.with(mContext).load(ApiUtils.getImageUrl(currentClient.getImage_url())).into(photo_info);
                ImageView edit= dialogView.findViewById(R.id.edit_photo);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PhotoEdithActivity.start(mContext,currentClient);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_debt, null);
                builder.setView(dialogView);

                final TextView value=  dialogView.findViewById(R.id.value);
                final TextView name=  dialogView.findViewById(R.id.name_user);
                final TextView date=  dialogView.findViewById(R.id.date);
                final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);
                final TextView description=  dialogView.findViewById(R.id.description);
                final TextView brand=  dialogView.findViewById(R.id.brand);
                final TextView size=  dialogView.findViewById(R.id.size);
                final TextView product_kind=  dialogView.findViewById(R.id.product_kind);
                final TextView code=  dialogView.findViewById(R.id.code);
                final CheckBox checkBox =  dialogView.findViewById(R.id.checkBoxCancelar);

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


                            Item_file item=new Item_file(0L,currentClient.getId(),desc,val,0d,"",brandT,codeT,sizeT,product_kindT);
                            //TODO ver fechas
                            if(currentClient.debt + val == 0){
                                item.settled="true";
                            }


                            ApiClient.get().postItemfile(item, new GenericCallback<Item_file>() {
                                @Override
                                public void onSuccess(Item_file data) {
                                   currentClient.debt+=data.value;
                                   updateItem(position,currentClient);
                                   Toast.makeText(mContext, "Transaccion creada para: "+currentClient.getName(), Toast.LENGTH_SHORT).show();
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
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInfoDialog(currentClient,position);
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
        final TextView phone2=  dialogView.findViewById(R.id.alternative_phone);
        final ImageView delete=  dialogView.findViewById(R.id.deleteuser);
        final ImageView edituser=  dialogView.findViewById(R.id.edituser);
        final ImageView history=  dialogView.findViewById(R.id.historyuser);
        final ImageView call=  dialogView.findViewById(R.id.phone);

        name.setText(c.getName());
        address.setText(c.getAddress());
        phone.setText(c.getPhone());
        phone2.setText(c.getAlternative_phone());

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
                deleteUser(c,position);
                dialog.dismiss();
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
        dialog.show();
    }

    interface OnClientEditedCallback {
        void onUserEdited(Client client);
    }
}
