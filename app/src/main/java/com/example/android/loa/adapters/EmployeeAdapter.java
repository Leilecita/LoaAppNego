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

import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;

import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter<Employee,EmployeeAdapter.ViewHolder> {
    private Context mContext;

    public EmployeeAdapter(Context context, List<Employee> employees){
        setItems(employees);
        mContext = context;
    }

    public EmployeeAdapter(){

    }

    public List<Employee> getListEmployees(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public ImageView photo;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name);
            photo=v.findViewById(R.id.photo);
        }
    }

    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_employee,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(EmployeeAdapter.ViewHolder vh){
        if(vh.name!=null)
            vh.name.setText(null);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Employee currentEmployee = getItem(position);

        holder.name.setText(currentEmployee.name);

        if (currentEmployee.image_url == null) {
            Glide.with(mContext).load(R.drawable.person_color).into(holder.photo);
        } else {
            Glide.with(mContext).load(ApiUtils.getImageUrl(currentEmployee.image_url)).into(holder.photo);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"holaaa",Toast.LENGTH_LONG).show();
                //createInfoDialog(currentEmployee,position);
            }
        });



    }


    private void createInfoDialog(final Employee e, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_employee_information, null);

        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final ImageView delete=  dialogView.findViewById(R.id.deleteuser);
        final ImageView edituser=  dialogView.findViewById(R.id.edituser);
        final ImageView call=  dialogView.findViewById(R.id.phone);
        final ImageView mens=  dialogView.findViewById(R.id.mens);

        name.setText(e.getName());
        address.setText(e.getAddress());
        phone.setText(e.getPhone());

        mens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWhatsapp("",e.phone);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + e.phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(e,position);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void sendWhatsapp(String text, String phone){
        Uri uri = Uri.parse("smsto:" + phone);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        i.putExtra(Intent.EXTRA_TEXT,text);
        mContext.startActivity(Intent.createChooser(i, ""));
    }

    private void deleteUser( final Employee e,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.dialog_delete_client, null);
        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        name.setText(e.getName());
        phone.setText(e.getPhone());
        address.setText(e.getAddress());

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               ApiClient.get().deleteEmployee(e.id, new GenericCallback<Void>() {
                   @Override
                   public void onSuccess(Void data) {

                       Toast.makeText(dialogView.getContext(), "El empleado "+e.getName()+" ha sido eliminado.", Toast.LENGTH_LONG).show();
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

}
