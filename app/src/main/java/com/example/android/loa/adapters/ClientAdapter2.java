package com.example.android.loa.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
/*
public class ClientAdapter2 extends BaseAdapter<Client,ClientAdapter2.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    private OnAmountChange onAmountChangeListener = null;
    public void setOnAmountCangeListener(OnAmountChange listener){
        onAmountChangeListener = listener;
    }


    public ClientAdapter2(Context context, List<Client> clients){
        setItems(clients);
        mContext = context;
    }

    public ClientAdapter2(){

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
    // Static inner class to initialize the views of rows
    static class ViewHolderOne extends RecyclerView.ViewHolder {
        public TextView text_name;
        public TextView text_value;
        public ImageView photo;
        public ImageView add;
        public ViewHolderOne(View itemView) {
            super(itemView);
            text_name= itemView.findViewById(R.id.text_name);
            text_value= itemView.findViewById(R.id.text_value);
            add=itemView.findViewById(R.id.add);
            photo=itemView.findViewById(R.id.photo_user);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        public TextView text_name;
        public TextView text_value;
        public ImageView photo;
        public ImageView add;
        public ViewHolderTwo(View itemView) {
            super(itemView);
            text_name= itemView.findViewById(R.id.text_name);
            text_value= itemView.findViewById(R.id.text_value);
            add=itemView.findViewById(R.id.add);
            photo=itemView.findViewById(R.id.photo_user);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_item_client, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_item_client2, parent, false);
            return new ViewHolderTwo(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){

        switch (holder.getItemViewType()) {
            case 1:
                loadHolderOne((ViewHolderOne) holder, position);
                break;
            case 2:
                loadHolderOne((ViewHolderOne) holder, position);
                break;
            default:
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        Client client = getListClient().get(position);

        if(ApiUtils.getImageUrl(client.image_url).equals("http://loa.abarbieri.com.ar/uploads/preimpresos/person_color.png")){
            return 1;
        }else{
            return 2;
        }
    }

    private void clearViewHolder(ClientAdapter2.ViewHolder vh){
        if(vh.text_name!=null)
            vh.text_name.setText(null);
        if(vh.text_value!=null)
            vh.text_value.setText(null);

    }





    private void loadHolderOne(ViewHolderOne holder , final Integer position){

       // clearViewHolder(holder);

        final Client currentClient=getItem(position);

        holder.text_name.setText(currentClient.name);

        //if(ApiUtils.getImageUrl(currentClient.image_url).equals("http://loa.abarbieri.com.ar/uploads/preimpresos/person_color.png")){

        //get first letter of each String item
        String firstLetter = String.valueOf(getItem(position).name.charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(getItem(position));
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        //Glide.with(mContext).load(drawable.getCurrent()).into(holder.photo2);
        // holder.photo2.setImageDrawable(drawable);


        holder.photo.setImageDrawable(drawable);
        // }else{
        //    Glide.with(mContext).load(ApiUtils.getImageUrl(currentClient.image_url)).into(holder.photo2);
        //}

        //Glide.with(mContext).load(drawable).into(holder.photo);
        //  }else{
        //    Glide.with(mContext).load(ApiUtils.getImageUrl(currentClient.image_url)).into(holder.photo);
        //}

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


                            Item_file item=new Item_file(0L,currentClient.getId(),desc,val,0d,"",brandT,codeT,sizeT,product_kindT,"false");
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


    public void sendWhatsapp(String text, String phone){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+549"+phone));
            mContext.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
*/