package com.example.android.loa.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;

import com.example.android.loa.ValuesHelper;
import com.example.android.loa.network.models.Income;
import com.example.android.loa.network.models.ReportItemFileClientEvent;
import com.example.android.loa.network.models.ReportSale;
import com.example.android.loa.network.models.ReportStockEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportSaleAdapter extends BaseAdapter<ReportSale,ReportSaleAdapter.ViewHolder> {
    private Context mContext;
    private String mGroupBy="day";

    public ReportSaleAdapter(Context context, List<ReportSale> sales){
        setItems(sales);
        mContext = context;
    }

    public void setGroupBy(String group){
        this.mGroupBy=group;
    }

    public ReportSaleAdapter(){

    }

    public List<ReportSale> getListSales(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public RecyclerView recylerSales;
        public RecyclerView recyclerIncomes;
        public RecyclerView recyclerItemsFile;
        public TextView month;
        public TextView numberDay;
        public TextView amount;
        public TextView amountCard;
        public TextView countSales;
        public LinearLayout content;
        public LinearLayout lineIncomes;
        public LinearLayout lineFile;


        public ViewHolder(View v){
            super(v);
            recyclerIncomes= v.findViewById(R.id.list_incomes);
            recyclerItemsFile=v.findViewById(R.id.list_items_file_events);
            recylerSales=v.findViewById(R.id.list_events);
            month=v.findViewById(R.id.month);
            numberDay=v.findViewById(R.id.number_day);
            amount=v.findViewById(R.id.amount);
            amountCard=v.findViewById(R.id.amount_card);
            content=v.findViewById(R.id.barTop);
            lineFile=v.findViewById(R.id.lin_files);
            lineIncomes=v.findViewById(R.id.lin_incomes);
            countSales=v.findViewById(R.id.counted_sale);
        }
    }

    @Override
    public ReportSaleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.report_sale_by_day,parent,false);
        ReportSaleAdapter.ViewHolder vh = new ReportSaleAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ReportSaleAdapter.ViewHolder vh){

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportSaleAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportSale current = getItem(position);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.recylerSales.setLayoutManager(layoutManager);
        ReportStockEventAdapter mAdapter = new ReportStockEventAdapter(mContext, new ArrayList<ReportStockEvent>());
        mAdapter.setItems(current.listStockEventSale);
        holder.recylerSales.setAdapter(mAdapter);



        LinearLayoutManager layoutManagerFile = new LinearLayoutManager(mContext);
        holder.recyclerItemsFile.setLayoutManager(layoutManagerFile);
        ReportItemFileClientAdapter mAdapterItemFileClient = new ReportItemFileClientAdapter(mContext, new ArrayList<ReportItemFileClientEvent>());
        mAdapterItemFileClient.setItems(current.listItems);
        holder.recyclerItemsFile.setAdapter(mAdapterItemFileClient);
        if(current.listItems.size()>0){
            holder.lineFile.setVisibility(View.VISIBLE);
           // holder.lineSales.setVisibility(View.VISIBLE);
        }else{
            holder.lineFile.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(mContext);
        holder.recyclerIncomes.setLayoutManager(layoutManagerIncome);
        IncomesAdapter mAdapterIncomes = new IncomesAdapter(mContext, new ArrayList<Income>());
        mAdapterIncomes.setItems(current.listIncomes);
        holder.recyclerIncomes.setAdapter(mAdapterIncomes);
        if(current.listIncomes.size()>0){
            holder.lineIncomes.setVisibility(View.VISIBLE);
           // holder.lineSales.setVisibility(View.VISIBLE);
        }else{
            holder.lineIncomes.setVisibility(View.GONE);
        }


        holder.month.setText(DateHelper.get().getNameMonth(current.created).substring(0,3));

        if(mGroupBy.equals("day")){
            holder.numberDay.setText(DateHelper.get().numberDay(current.created));
            holder.numberDay.setVisibility(View.VISIBLE);
        }else{
            holder.numberDay.setText("");
            holder.numberDay.setVisibility(View.GONE);
        }

        holder.amount.setText(ValuesHelper.get().getIntegerQuantityByLei(current.efectAmount));
        holder.amountCard.setText(ValuesHelper.get().getIntegerQuantityByLei(current.cardAmount));
        holder.countSales.setText(String.valueOf(current.countSales));

    }
}
