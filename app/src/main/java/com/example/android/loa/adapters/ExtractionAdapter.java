package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
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


       /* if(position==0){
            holder.div.setVisibility(View.GONE);
        }else{
            holder.div.setVisibility(View.VISIBLE);
        }*/

        holder.value.setText(ValuesHelper.get().getIntegerQuantityByLei(currentExtraction.value));
        holder.description.setText(currentExtraction.description);
        holder.type.setText(currentExtraction.type);
        holder.detail.setText(currentExtraction.detail);

       /* ViewTreeObserver vto = holder.description.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout l = holder.description.getLayout();
                if ( l != null){
                    int lines = l.getLineCount();
                    if ( lines > 0)
                        if ( l.getEllipsisCount(lines-1) > 0){
                            holder.description.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(mContext," is",Toast.LENGTH_SHORT).show();
                                }
                            });
                            System.out.println("text is ellipsed123");
                            System.out.println(currentExtraction.description);
                        }
                }
            }
        });*/


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
                desc.setText(currentExtraction.description);
                TextView value=  dialogView.findViewById(R.id.value);
                TextView type=  dialogView.findViewById(R.id.type);
                type.setText(currentExtraction.type);
                value.setText(String.valueOf(currentExtraction.value));
                TextView date=  dialogView.findViewById(R.id.obs_date);

                date.setText(DateHelper.get().changeFormatDate(currentExtraction.created));

                System.out.println(date.getText().toString().trim());
                System.out.println(currentExtraction.created);

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
        dialog.show();
    }

    private void edithExtraction(final Extraction e, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_model_edith_extraction, null);
        builder.setView(dialogView);

        final TextView value= dialogView.findViewById(R.id.value);
        //final TextView type= dialogView.findViewById(R.id.type);
        final TextView description= dialogView.findViewById(R.id.description);
        final TextView date =dialogView.findViewById(R.id.date);

        final TextView cancel =dialogView.findViewById(R.id.cancel);
        final ImageView date_picker =dialogView.findViewById(R.id.date_picker);
        final Button ok =dialogView.findViewById(R.id.ok);

        final AutoCompleteTextView type = dialogView.findViewById(R.id.autocomplete_region);
        String[] regions = mContext.getResources().getStringArray(R.array.types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, regions);
        type.setAdapter(adapter);
        type.setHint(e.type);

        //type.setText(e.type);
        description.setText(e.description);
        value.setText(String.valueOf(MathHelper.get().getIntegerQuantity(e.value)));

        date.setHint(DateHelper.get().getOnlyDate((DateHelper.get().getOnlyDate(e.created))));
        date.setHintTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();
        date_picker.setOnClickListener(new View.OnClickListener() {
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

                       // Toast.makeText(mContext,"La extracción se ha modificado con éxito" ,Toast.LENGTH_LONG).show();
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
        dialog.show();

    }

}
