package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.Interfaces.OnExtractionsAmountChange;
import com.example.android.loa.MathHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Extraction;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

public class ExtractionAdapter  extends BaseAdapter<Extraction,ExtractionAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private String mLastDateDecoration;

    private boolean mResumAmountOn;

    private OnExtractionsAmountChange onExtractionsAmountChangeListener = null;
    public void setOnExtractionsAmountCangeListener(OnExtractionsAmountChange listener){
        onExtractionsAmountChangeListener = listener;
    }


    @Override
    public long getHeaderId(int position) {
        if (position == 0) {
                return 0;
        } else {
            return getItem(position).id;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;

        final Extraction e=getItem(position);

        String dateToShow=DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(e.created));

        if(mLastDateDecoration.equals("") || !mLastDateDecoration.equals(dateToShow)){
            textView.setText(dateToShow);
            mLastDateDecoration=DateHelper.get().getOnlyDate(DateHelper.get().changeFormatDate(e.created));

        }
        else{
            textView.setTextSize(0);
            textView.setVisibility(View.GONE);
        }
    }



    public ExtractionAdapter(Context context, List<Extraction> extractions,Boolean amount) {
        setItems(extractions);
        mContext = context;
        mLastDateDecoration="";
        mResumAmountOn=amount;

    }

    public void setLastDateDecoration(String s){
        mLastDateDecoration=s;
    }

    public ExtractionAdapter() {

    }

    public List<Extraction> getListEmployees() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public TextView year;
        public TextView value;
        public TextView date;


        public ViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.description);
           // year = v.findViewById(R.id.year);
            value = v.findViewById(R.id.value);
            date = v.findViewById(R.id.date);
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
        if (vh.year != null)
            vh.year.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
        if (vh.date != null)
            vh.date.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Extraction currentExtraction = getItem(position);


            holder.value.setText(String.valueOf(currentExtraction.value));
            holder.description.setText(currentExtraction.description);
            //  holder.type.setText(currentExtraction.type);

            if(currentExtraction.type.equals("Santi")){
                holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_red));
            }else if(currentExtraction.type.equals("Local")){
                // holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
            }else if(currentExtraction.type.equals("Mercaderia")){
                holder.value.setTypeface(Typeface.create("san-serif", Typeface.BOLD));
            }else if(currentExtraction.type.equals("Sueldo")){
                holder.value.setTextColor(mContext.getResources().getColor(R.color.loa_green));
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
