package com.example.android.loa.adapters;

import android.app.AlertDialog;
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
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;

import com.example.android.loa.activities.HoursHistoryEmployeeActivity;
import com.example.android.loa.activities.LoadEmployeeHoursActivity;
import com.example.android.loa.activities.OperationHistoryClientActivity;
import com.example.android.loa.activities.PhotoEdithActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter<Employee,EmployeeAdapter.ViewHolder> {
    private Context mContext;

    public EmployeeAdapter(Context context, List<Employee> employees){
        setItems(employees);
        mContext = context;
    }

    public static void start(Context mContext, Employee e) {
        Intent i = new Intent(mContext, LoadEmployeeHoursActivity.class);
        i.putExtra("ID", e.id);
        i.putExtra("NAME", e.name);
        mContext.startActivity(i);
    }

    public EmployeeAdapter(){

    }

    public List<Employee> getListEmployees(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public ImageView photo;
        public ImageView options;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name);
            photo=v.findViewById(R.id.photo);
            options=v.findViewById(R.id.options);
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edithPhoto(currentEmployee);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHours(currentEmployee);
            }
        });
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createInfoDialog(currentEmployee,position);
            }
        });
    }

    private void edithPhoto(final Employee currentEmployee){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_photo, null);
        final ImageView photo_info=  dialogView.findViewById(R.id.image_user);
        Glide.with(mContext).load(ApiUtils.getImageUrl(currentEmployee.getImage_url())).into(photo_info);
        ImageView edit= dialogView.findViewById(R.id.edit_photo);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PhotoEdithActivity.startEmployee(mContext,currentEmployee);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadHours(final Employee e){
        start(mContext,e);

     /*   AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_hours, null);

        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.name_employee);
        name.setText(e.name);
        final TextView turn=  dialogView.findViewById(R.id.turn);
        final TextView entry=  dialogView.findViewById(R.id.entry);
        final TextView finish=  dialogView.findViewById(R.id.finish);
        final TextView date=  dialogView.findViewById(R.id.date);
        date.setText(DateHelper.get().getActualDate());
        final TextView time_worked=  dialogView.findViewById(R.id.time_worked);
        final TextView obs=  dialogView.findViewById(R.id.observation);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String turnT=turn.getText().toString().trim();
                Double time_workedT=Double.valueOf(time_worked.getText().toString().trim());
                String obsT=obs.getText().toString().trim();
                String entryT=entry.getText().toString().trim();
                String finishT=finish.getText().toString().trim();
                String dateT=date.getText().toString().trim();

                Item_employee i=new Item_employee(e.id,time_workedT,turnT,dateT,obsT,entryT,finishT);

                ApiClient.get().postItemEmploye(i, new GenericCallback<Item_employee>() {
                    @Override
                    public void onSuccess(Item_employee data) {
                        Toast.makeText(mContext, "Horas cargadas para : "+e.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();*/

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
        final ImageView history=  dialogView.findViewById(R.id.history);
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

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoursHistoryEmployeeActivity.start(mContext,e);
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

        edituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edithEmployee(e, position, new OnEmployeetEditedCallback() {
                    @Override
                    public void onEmployeeEdited(Employee newEmployee) {
                        name.setText(newEmployee.getName());
                        address.setText(newEmployee.getAddress());
                        phone.setText(newEmployee.getPhone());
                    }
                });
            }
        });
        dialog.show();

    }

    private void edithEmployee(final Employee employeeToEdith, final int position, final OnEmployeetEditedCallback callback){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_employee, null);

        builder.setView(dialogView);

        final EditText nameEdith=dialogView.findViewById(R.id.edith_name);
        final EditText addressEdith=dialogView.findViewById(R.id.edith_address);
        final EditText phoneEdith=dialogView.findViewById(R.id.edith_phone);

        phoneEdith.setText(employeeToEdith.getPhone());
        phoneEdith.setTextColor(mContext.getResources().getColor(R.color.word_info));
        addressEdith.setText(employeeToEdith.getAddress());
        addressEdith.setTextColor(mContext.getResources().getColor(R.color.word_info));
        nameEdith.setText(employeeToEdith.getName());
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

                employeeToEdith.setName(nameNew);
                employeeToEdith.setAddress(addressNew);
                employeeToEdith.setPhone(phoneNew);

                ApiClient.get().putEmployee(employeeToEdith, new GenericCallback<Employee>() {
                    @Override
                    public void onSuccess(Employee data) {
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "El usuario ha sido editado",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al editar usuario",mContext);
                    }
                });


                dialog.dismiss();
                callback.onEmployeeEdited(employeeToEdith);
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

    interface OnEmployeetEditedCallback {
        void onEmployeeEdited(Employee employee);
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
