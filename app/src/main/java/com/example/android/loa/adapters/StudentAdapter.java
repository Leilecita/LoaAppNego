package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Student;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import java.text.DateFormatSymbols;


public class StudentAdapter extends BaseAdapter<Student,StudentAdapter.ViewHolder>  implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    List<String> listColor=new ArrayList<>();
    Random random;



    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).updated_date));
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
            final Student e = getItem(position);

            String dateToShow2 = DateHelper.get().getOnlyDate(DateHelper.get().changeOrderDate(e.updated_date));
            // String dateToShow2 = DateHelper.get().getNameMonth2(e.created).substring(0, 3);
            String numberDay = DateHelper.get().numberDay(e.updated_date);

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

    public StudentAdapter(Context context, List<Student> clients){
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

    public StudentAdapter(){

    }

    public List<Student> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text_name;
        public TextView text_name2;
        public TextView text_year;
        public ImageView add;
        public ImageView cuad_image;

        public TextView firstLetter;
        public LinearLayout info_student;
        public LinearLayout line_tel_student;
        public LinearLayout line_mama;
        public LinearLayout line_papa;

        public TextView nombre_mama;
        public TextView tel_mama;
        public TextView nombre_papa;
        public TextView tel_papa;
        public TextView direccion;
        public TextView localidad;
        public TextView tel;
        public TextView category;

        public LinearLayout call_mama;
        public LinearLayout call_papa;
        public LinearLayout call_alumno;
        public LinearLayout whatsapp_mama;
        public LinearLayout whatsapp_papa;
        public LinearLayout whatsapp_alumno;

        public ViewHolder(View v){
            super(v);
            text_name= v.findViewById(R.id.text_name);
            text_name2= v.findViewById(R.id.name2_student);
            text_year= v.findViewById(R.id.year);
            add=v.findViewById(R.id.add);
            cuad_image=v.findViewById(R.id.cuad_image);
            firstLetter=v.findViewById(R.id.firstLetter);
            info_student = v.findViewById(R.id.info_student);
            nombre_mama = v.findViewById(R.id.nombre_mama);
            nombre_papa = v.findViewById(R.id.nombre_papa);
            tel_mama = v.findViewById(R.id.tel_mama);
            tel_papa = v.findViewById(R.id.tel_papa);
            direccion = v.findViewById(R.id.direccion);
            localidad = v.findViewById(R.id.loc);
            tel = v.findViewById(R.id.tel);
            line_tel_student = v.findViewById(R.id.line_tel_student);
            line_mama = v.findViewById(R.id.line_mama);
            line_papa = v.findViewById(R.id.line_papa);
            call_mama = v.findViewById(R.id.call_mama);
            call_papa = v.findViewById(R.id.call_papa);
            call_alumno = v.findViewById(R.id.call_student);
            whatsapp_mama = v.findViewById(R.id.wh_mam);
            whatsapp_papa = v.findViewById(R.id.wh_pap);
            whatsapp_alumno = v.findViewById(R.id.wh_student);

            category = v.findViewById(R.id.category);
        }
    }

    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_student,parent,false);
        StudentAdapter.ViewHolder vh = new StudentAdapter.ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(StudentAdapter.ViewHolder vh){
        if(vh.text_name!=null)
            vh.text_name.setText(null);
        if(vh.text_year!=null)
            vh.text_year.setText(null);
        if(vh.nombre_papa!=null)
            vh.nombre_papa.setText(null);
        if(vh.tel_papa!=null)
            vh.tel_papa.setText(null);
        if(vh.nombre_mama!=null)
            vh.nombre_mama.setText(null);
        if(vh.tel_mama!=null)
            vh.tel_mama.setText(null);
        if(vh.direccion!=null)
            vh.direccion.setText(null);
        if(vh.localidad!=null)
            vh.localidad.setText(null);
        if(vh.firstLetter!=null){
            vh.firstLetter.setText(null);
        }
        if(vh.category!=null){
            vh.category.setText(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Student currentClient = getItem(position);

        holder.text_name.setText(currentClient.nombre+" "+currentClient.apellido);
        holder.category.setText(currentClient.category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.text_year.setText(String.valueOf(getAge(currentClient)));
        }
        holder.nombre_mama.setText(currentClient.nombre_mama);
        holder.nombre_papa.setText(currentClient.nombre_papa);
        holder.tel_mama.setText(currentClient.tel_mama);
        holder.tel_papa.setText(currentClient.tel_papa);
        holder.direccion.setText(currentClient.direccion);
        holder.localidad.setText(currentClient.localidad);

        holder.tel.setText(currentClient.tel_adulto);

        if(currentClient.tel_adulto.matches("")){
            holder.line_tel_student.setVisibility(View.GONE);
        }else{
            holder.line_tel_student.setVisibility(View.VISIBLE);
        }

        if(currentClient.nombre_mama.matches("") && currentClient.tel_mama.matches("") ){
            holder.line_mama.setVisibility(View.GONE);
        }else{
            holder.line_mama.setVisibility(View.VISIBLE);
        }

        if(currentClient.nombre_papa.matches("") && currentClient.tel_papa.matches("") ){
            holder.line_papa.setVisibility(View.GONE);
        }else{
            holder.line_papa.setVisibility(View.VISIBLE);
        }

        holder.firstLetter.setText(String.valueOf(currentClient.nombre.charAt(0)));

        holder.cuad_image.setColorFilter(Color.parseColor(listColor.get(random.nextInt(listColor.size()))), PorterDuff.Mode.SRC_ATOP);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.info_student.getVisibility() == View.VISIBLE){
                    holder.text_name2.setText("");
                   // holder.text_name.setText(currentClient.nombre);
                    holder.info_student.setVisibility(View.GONE);
                }else{
                    holder.info_student.setVisibility(View.VISIBLE);
                  //  holder.text_name.setText("");
                    holder.text_name2.setText(currentClient.nombre);
                }
            }
        });

        holder.call_alumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(currentClient.tel_adulto);
            }
        });

        holder.call_papa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(currentClient.tel_papa);
            }
        });

        holder.call_mama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(currentClient.tel_mama);
            }
        });

        holder.whatsapp_papa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWhatsapp("Hola", currentClient.tel_papa);
            }
        });

        holder.whatsapp_mama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWhatsapp("Hola", currentClient.tel_mama);
            }
        });

        holder.whatsapp_alumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWhatsapp("Hola", currentClient.tel_adulto);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                edithStudent(currentClient,position);
                return false;
            }
        });

    }

    private void edithStudent(final Student studentToEdith, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.edith_student, null);

        builder.setView(dialogView);

        final TextView nameEdith=dialogView.findViewById(R.id.edith_name);
        final EditText categoryEdith=dialogView.findViewById(R.id.edith_category);
        final EditText phoneEdith=dialogView.findViewById(R.id.edith_phone);

        phoneEdith.setText(studentToEdith.tel_adulto);
        categoryEdith.setText(studentToEdith.category);
        nameEdith.setText(studentToEdith.nombre+" "+studentToEdith.apellido);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                String category= categoryEdith.getText().toString().trim();
                String phoneNew= phoneEdith.getText().toString().trim();

                studentToEdith.category = category;
                studentToEdith.tel_adulto = phoneNew;

                ApiClient.get().putStudent(studentToEdith, new GenericCallback<Student>() {
                    @Override
                    public void onSuccess(Student data) {
                        notifyItemChanged(position);
                        Toast.makeText(mContext, "El alumno ha sido editado",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al editar alumno",mContext);
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


    private void call(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Integer getAge(Student s){
        if(!s.fecha_nacimiento.equals("")){
            String[] parts = s.fecha_nacimiento.split("-");
            String anio = parts[0]; // año
            String mes = parts[1]; // mes
            String dia = parts[2]; // dia

            LocalDate today = LocalDate.now();

            LocalDate birthday = LocalDate.of(Integer.valueOf(anio), Integer.valueOf(mes),Integer.valueOf(dia));  //Birth date

            Period p = Period.between(birthday, today);
            return p.getYears();
        }else{
            return 0;
        }

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
