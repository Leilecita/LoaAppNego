package com.example.android.loa.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.network.models.Event;

import com.example.android.loa.R;

import java.util.List;


/**
 * Created by leila on 23/11/17.
 */

public class EventAdapter extends BaseAdapter<Event,EventAdapter.ViewHolder> {

    private Context mContext;

    public EventAdapter(Context context, List<Event> events){
        setItems(events);
        mContext = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name_employee;
        public TextView description;
        public TextView date;
        public TextView state;

        public TextView value;


        public ViewHolder(View v){
            super(v);
            name_employee= v.findViewById(R.id.name_employee);
            description= v.findViewById(R.id.description);
            value= v.findViewById(R.id.value);
            state= v.findViewById(R.id.state);
            date= v.findViewById(R.id.event_time);


        }
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_event_history,parent,false);
        EventAdapter.ViewHolder vh = new EventAdapter.ViewHolder(v);
        return vh;
    }


    public void clearBindView(EventAdapter.ViewHolder vh){
        if (vh.description != null)
            vh.description.setText(null);
        if (vh.name_employee != null)
            vh.name_employee.setText(null);
        if (vh.value != null)
            vh.value.setText(null);
        if (vh.date != null)
            vh.date.setText(null);
        if (vh.state != null)
            vh.state.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final EventAdapter.ViewHolder holder, final int position){
        clearBindView(holder);

        final Event currentEvent= getItem(position);
        holder.description.setText(currentEvent.description);
        holder.name_employee.setText(currentEvent.employee_name);
        holder.value.setText(String.valueOf(currentEvent.value));
        holder.date.setText(DateHelper.get().serverToUserFormatted(currentEvent.created));
        holder.state.setText(currentEvent.state);

        if(currentEvent.state.equals("Modificado")){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.cuad_dialog_previous, null);

                    builder.setView(dialogView);
                    String[] parts = currentEvent.description.split(" ");
                    String client_name = parts[0];

                    final TextView previous_description=  dialogView.findViewById(R.id.description);
                    final TextView previous_value=  dialogView.findViewById(R.id.previous_value);

                    previous_description.setText(client_name+" "+currentEvent.previous);
                    previous_value.setText(String.valueOf(currentEvent.previous_value));

                    final AlertDialog dialog = builder.create();
                    dialog.show();


                    return false;
                }
            });
        }

    }




}


