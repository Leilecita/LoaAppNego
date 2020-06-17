package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.Interfaces.OnExtractionsAmountChange;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.types.Constants;
import com.example.android.loa.types.ExtractionType;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

public class ExtractionAdapter  extends BaseAdapter<Extraction,ExtractionAdapter.ViewHolder> {
    private Context mContext;


    private String groupby;

    private OnExtractionsAmountChange onExtractionsAmountChangeListener = null;
    public void setOnExtractionsAmountCangeListener(OnExtractionsAmountChange listener){
        onExtractionsAmountChangeListener = listener;
    }

    public ExtractionAdapter(Context context, List<Extraction> extractions) {
        setItems(extractions);
        mContext = context;
        groupby="day";

    }

    public void setGroupby(String groupby){
        this.groupby=groupby;
    }


    public ExtractionAdapter() {

    }

    public List<Extraction> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView type;
        public TextView value;
        public RelativeLayout date;
        public TextView day;
        public TextView number;
        public TextView div;
        public TextView detail;


        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
            type = v.findViewById(R.id.type);
            value = v.findViewById(R.id.value);
            date = v.findViewById(R.id.date);
            day = v.findViewById(R.id.day);
            number = v.findViewById(R.id.number);
            div = v.findViewById(R.id.div);
            detail = v.findViewById(R.id.detail);
        }
    }

    @Override
    public ExtractionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_extraction, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ExtractionAdapter.ViewHolder vh) {
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.type != null)
            vh.type.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Extraction currentExtraction = getItem(position);


        holder.day.setText(DateHelper.get().getNameDay(currentExtraction.created));
        holder.number.setText(DateHelper.get().numberDay(currentExtraction.created));

        if(groupby.equals("day")){
            holder.date.setVisibility(View.GONE);
        }else{
            holder.date.setVisibility(View.VISIBLE);
        }


        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(currentExtraction.value));
        holder.description.setText(currentExtraction.description);
        holder.type.setText(currentExtraction.type);
        holder.detail.setText(currentExtraction.detail);

        if(currentExtraction.type.equals(Constants.TYPE_GASTO_LOCAL)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.local));
        }else if(currentExtraction.type.equals(Constants.TYPE_GASTO_PERSONAL)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.pers));
        }else if(currentExtraction.type.equals(Constants.TYPE_GASTO_SANTI)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.santi));
        }else if(currentExtraction.type.equals(Constants.TYPE_SANTI)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.exrt));
        }else if(currentExtraction.type.equals(Constants.TYPE_MERCADERIA)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.merc));
        }else if(currentExtraction.type.equals(Constants.TYPE_SUELDO)){
            holder.value.setTextColor(mContext.getResources().getColor(R.color.sueldos));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_information_extraction, null);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                final ImageView delete=  dialogView.findViewById(R.id.delete);
                ImageView edith=  dialogView.findViewById(R.id.edith);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteExtraction(currentExtraction,position);
                        dialog.dismiss();
                    }
                });

                edith.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edithExtraction(currentExtraction,position);
                        dialog.dismiss();
                    }
                });

                TextView desc=  dialogView.findViewById(R.id.description);
                TextView detail=  dialogView.findViewById(R.id.detail);
                desc.setText(currentExtraction.description);
                detail.setText(currentExtraction.detail);
                TextView value=  dialogView.findViewById(R.id.value);
                TextView type=  dialogView.findViewById(R.id.type);
                type.setText(currentExtraction.type);
                value.setText(String.valueOf(currentExtraction.value));
                TextView date=  dialogView.findViewById(R.id.obs_date);

                date.setText(DateHelper.get().changeFormatDate(currentExtraction.created));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                return false;
            }
        });
    }

    private void deleteExtraction(final Extraction e,final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_delete_extraction, null);
        builder.setView(dialogView);

        final TextView date= dialogView.findViewById(R.id.date);
        final TextView value= dialogView.findViewById(R.id.value);
        final TextView desc= dialogView.findViewById(R.id.description);

        date.setText(e.created);
        value.setText(String.valueOf(e.value));
        desc.setText(e.description);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiClient.get().deleteExtraction(e.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {

                        EventBus.getDefault().post(new RefreshBoxesEvent("Hey event subscriber!"));
                        Toast.makeText(mContext,"Se elimina la extracción "+e.description,Toast.LENGTH_LONG).show();
                        removeItem(position);

                        if(onExtractionsAmountChangeListener!=null){
                            onExtractionsAmountChangeListener.reloadExtractionsAmount();
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

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private static <T extends Enum<ExtractionType>> void enumNameToStringArray(ExtractionType[] values,List<String> spinner_type) {
        for (ExtractionType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_type.add("Tipo");
            }else{
                spinner_type.add(value.getName());
            }
        }
      //  return spinner_type;
    }

    private void edithExtraction(final Extraction e, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_extraction, null);
        builder.setView(dialogView);

        final TextView value= dialogView.findViewById(R.id.value);
        final TextView description= dialogView.findViewById(R.id.description);
        final TextView date =dialogView.findViewById(R.id.date);
        final TextView type =dialogView.findViewById(R.id.type);
        final TextView detail =dialogView.findViewById(R.id.detail);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final Button ok =dialogView.findViewById(R.id.ok);

        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_type1);
        final Spinner spinnerDetail=  dialogView.findViewById(R.id.spinner_detail);

        //SPINNER DETAIL
        final List<String> spinner_detail = new ArrayList<>();
        enumNameToStringArray(ExtractionType.values(),spinner_detail);

        ArrayAdapter<String> adapter_detail = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item,spinner_detail);
        adapter_detail.setDropDownViewResource(R.layout.spinner_item);
        spinnerDetail.setAdapter(adapter_detail);

        //SPINNER TYPE
        final List<String> spinner_type = new ArrayList<>();
        enumNameToStringArray(ExtractionType.values(),spinner_type);

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item,spinner_type);
        adapter_type.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(adapter_type);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinnerType.getSelectedItem());
                List<String> array=new ArrayList<>();
                array.add("Detalle");

                if(itemSelected.equals("Gasto local")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                     array=createArrayGastosLocal();
                }else if(itemSelected.equals("Gasto personal")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                    array=createArrayGastosPersonales();
                }else if(itemSelected.equals("Gasto santi")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                     array=createArrayGastosSanti();
                }else if(itemSelected.equals("Mercaderia")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                    array=createArrayMerc();
                }else if(itemSelected.equals("Santi extr")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                    array=createArrayExtr();

                }else if(itemSelected.equals("Sueldo")){
                    type.setText(spinnerType.getSelectedItem().toString().trim());
                       array=createArraySueldos();
                }

                ArrayAdapter<String> adapter_detail = new ArrayAdapter<String>(mContext,
                        R.layout.spinner_item,array);
                adapter_detail.setDropDownViewResource(R.layout.spinner_item);
                spinnerDetail.setAdapter(adapter_detail);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinnerDetail.getSelectedItem());
                List<String> array=new ArrayList<>();
                array.add("Detalle");

                if(!itemSelected.equals("Detalle")){
                    detail.setText(spinnerDetail.getSelectedItem().toString().trim());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        description.setText(e.description);
        value.setText(String.valueOf(MathHelper.get().getIntegerQuantity(e.value)));

        type.setText(e.type);
        detail.setText(e.detail);

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(e.created))));
        date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();
        date.setOnClickListener(new View.OnClickListener() {
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

                                String time=DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                                String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                                date.setText(datePicker);
                                e.created=datePicker;

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueT=value.getText().toString().trim();

                if(ValidatorHelper.get().isTypeDouble(valueT)){
                    e.value=Double.valueOf(valueT);
                }else{
                    Toast.makeText(dialogView.getContext(), " Tipo de valor no valido ", Toast.LENGTH_LONG).show();
                }

                e.type=type.getText().toString().trim();
                e.detail=detail.getText().toString().trim();
               // e.type=spinnerType.getSelectedItem().toString().trim();
                e.description=description.getText().toString().trim();

                type.setText(e.type);
                description.setText(e.description);
                value.setText(String.valueOf(e.value));

                ApiClient.get().putExtraction(e, new GenericCallback<Extraction>() {
                    @Override
                    public void onSuccess(Extraction data) {

                        updateItem(position,e);
                        if(onExtractionsAmountChangeListener!=null){
                            onExtractionsAmountChangeListener.reloadExtractionsAmount();
                        }

                        EventBus.getDefault().post(new RefreshBoxesEvent("Hey event subscriber!"));
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private List<String> createArrayGastosSanti(){
        List<String> spinner_gastos = new ArrayList<>();
        spinner_gastos.add("Seguro casa");
        spinner_gastos.add("Seguro auto");
        spinner_gastos.add("Celular");
        return spinner_gastos;
    }

    private List<String> createArrayGastosPersonales(){
        List<String> spinner_gastos = new ArrayList<>();
        spinner_gastos.add("Yerba");
        spinner_gastos.add("Agua");
        spinner_gastos.add("Kiosco");
        spinner_gastos.add("Otro");
        return spinner_gastos;
    }

    private List<String> createArrayGastosLocal(){
        List<String> spinner_gastos = new ArrayList<>();
        spinner_gastos.add("Seguro nego");
        spinner_gastos.add("Luz");
        spinner_gastos.add("Tel fijo");
        spinner_gastos.add("Contador");
        spinner_gastos.add("Alquiler");
        spinner_gastos.add("Limpieza");
        spinner_gastos.add("Encomienda");
        spinner_gastos.add("Libreria");
        spinner_gastos.add("Otro");
        return spinner_gastos;
    }

    private List<String> createArrayExtr(){
        List<String> spinner_extr = new ArrayList<>();
        spinner_extr.add("Deposito");
        spinner_extr.add("Directo a deposito");
        spinner_extr.add("Otro");
        return spinner_extr;
    }

    private List<String> createArrayMerc(){
        List<String> spinner_merc = new ArrayList<>();
        spinner_merc.add("Compra");
        spinner_merc.add("Otro");
        return spinner_merc;
    }

    private List<String> createArraySueldos(){
        List<String> spinner_suel = new ArrayList<>();
        spinner_suel.add("Adelanto");
        spinner_suel.add("Liquidacion total");
        spinner_suel.add("Otro");
        return spinner_suel;
    }

}
