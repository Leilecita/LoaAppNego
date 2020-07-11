package com.example.android.loa.adapters.sales;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.loa.DateHelper;
import com.example.android.loa.R;

import com.example.android.loa.ValuesHelper;
import com.example.android.loa.adapters.BaseAdapter;
import com.example.android.loa.adapters.ReportItemFileClientAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportItemFileClientEvent;
import com.example.android.loa.network.models.ReportSale;
import com.example.android.loa.network.models.ReportStockEvent;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportSaleAdapter extends BaseAdapter<ReportSale,ReportSaleAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private String mGroupBy="day";
    private String mItem="Todos";


    public ReportSaleAdapter(Context context, List<ReportSale> sales){
        setItems(sales);
        mContext = context;
    }

    public void setGroupBy(String group){
        this.mGroupBy=group;
    }
    public void setItem(String item){
        this.mItem=item;
    }

    public ReportSaleAdapter(){

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).created));
            return date.getTime();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viw_header_pru, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportSale e = getItem(position);

            String month = DateHelper.get().getNameMonth2(e.created).substring(0, 3);

            String numberDay = DateHelper.get().numberDay(e.created);

            String card=ValuesHelper.get().getIntegerQuantityByLei(e.cardAmount);
            String ef=ValuesHelper.get().getIntegerQuantityByLei(e.efectAmount);
            String cantArt=String.valueOf(e.countSales);
            String trans=String.valueOf(e.transfAmount);
            String merc=String.valueOf(e.mercPagoAmount);

            if (Double.compare(e.transfAmount, 0.0) != 0) {


            }

            //primer linear
            int count = linear.getChildCount();
            View v = null;
            View v2 = null;
            View v3 = null;
            View v5 = null;
            View v6 = null;
            View v7 = null;
            View v8 = null;
            View v9 = null;
            View v10 = null;
            View v11 = null;
            View v12 = null;
            View v13 = null;
            View v14 = null;
            View v15 = null;

            for (int k = 0; k < count; k++) {
                v3 = linear.getChildAt(k);

                //frame
                if (k == 0) {
                    FrameLayout linear4 = (FrameLayout) v3;

                    int count3 = linear4.getChildCount();

                    for (int i = 0; i < count3; i++) {

                        v = linear4.getChildAt(i);
                        if (i == 1) {

                            RelativeLayout rel2 = (RelativeLayout) v;
                            int countRel2 = rel2.getChildCount();

                            for (int r = 0; r < countRel2; r++) {

                                v5=rel2.getChildAt(r);
                                if (r == 0) {

                                    TextView t2 = (TextView) v5;
                                    t2.setText(month);

                                    if(mGroupBy.equals("day")){
                                        t2.setGravity(Gravity.CENTER_HORIZONTAL);
                                    }else{
                                        t2.setGravity(Gravity.CENTER);
                                    }
                                }else if(r==1){
                                    TextView t = (TextView) v5;
                                    t.setText(numberDay);

                                    if(mGroupBy.equals("day")){
                                        t.setVisibility(View.VISIBLE);
                                    }else{
                                        t.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                    //Linear layout
                }else if(k==1){
                    LinearLayout line = (LinearLayout) v3;
                    int countline = line.getChildCount();
                    for(int o=0; o< countline; o++){
                        v7=line.getChildAt(o);
                        if(o==1){
                            TextView t4 = (TextView) v7;
                            t4.setText(cantArt);
                        }
                    }
                    //linear layout
                }else if(k==2){

                    LinearLayout lines = (LinearLayout) v3;
                    int countline = lines.getChildCount();
                    for(int wj=0; wj< countline; wj++){
                        v13=lines.getChildAt(wj);
                        if(wj==0){

                            LinearLayout lin= (LinearLayout) v13;
                            int countlin = lin.getChildCount();

                            for(int f=0; f< countlin; f++){
                                v8=lin.getChildAt(f);

                                if(f==0){
                                    LinearLayout l=(LinearLayout) v8;
                                    int countl = l.getChildCount();
                                    for(int s=0; s< countl; s++){
                                        v9=l.getChildAt(s);

                                        //efectivo
                                        if(s==1){
                                            TextView t9 = (TextView) v9;
                                            t9.setText(ef);
                                        }
                                    }
                                }else if(f==1){
                                    LinearLayout l2=(LinearLayout) v8;
                                    int countl2 = l2.getChildCount();
                                    for(int w=0; w< countl2; w++){
                                        v10=l2.getChildAt(w);

                                        if(w==1){
                                            TextView t10 = (TextView) v10;
                                            t10.setText(card);
                                        }
                                    }
                                }
                            }

                        }else if(wj==1){

                            LinearLayout lin= (LinearLayout) v13;
                            int countlin = lin.getChildCount();

                            for(int f=0; f< countlin; f++){
                                v8=lin.getChildAt(f);

                                if(f==0){
                                    LinearLayout l=(LinearLayout) v8;
                                    int countl = l.getChildCount();

                                    if (Double.compare(e.transfAmount, 0.0) != 0) {
                                        l.setVisibility(View.VISIBLE);
                                    }else{
                                        l.setVisibility(View.GONE);
                                    }
                                    for(int s=0; s< countl; s++){
                                        v9=l.getChildAt(s);

                                        if(s==0){

                                            ImageView i2=(ImageView) v9;
                                            i2.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
                                        }
                                        //trans
                                        if(s==1){
                                            TextView t9 = (TextView) v9;
                                            t9.setText(trans);
                                        }
                                    }
                                }else if(f==1){
                                    LinearLayout l2=(LinearLayout) v8;
                                    int countl2 = l2.getChildCount();

                                    if (Double.compare(e.mercPagoAmount, 0.0) != 0) {
                                        l2.setVisibility(View.VISIBLE);
                                    }else{
                                        l2.setVisibility(View.GONE);
                                    }
                                    for(int w=0; w< countl2; w++){
                                        v10=l2.getChildAt(w);

                                        if(w==0){

                                            ImageView i1=(ImageView) v10;
                                            i1.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
                                        }

                                        if(w==1){
                                            TextView t10 = (TextView) v10;
                                            t10.setText(merc);
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }
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
        public LinearLayout lineFile;
        public LinearLayout viewMore;

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
            countSales=v.findViewById(R.id.counted_sale);
            viewMore=v.findViewById(R.id.view_more);
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


    private void listSales(String created, final ReportStockEventAdapter mAdapter){

        ApiClient.get().getStockeventsSales(created,mItem,mGroupBy, new GenericCallback<List<ReportStockEvent>>() {
            @Override
            public void onSuccess(List<ReportStockEvent> data) {
                mAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }
    private void listSalesItems(String created, final ReportItemFileClientAdapter mAdapter,final ViewHolder holder){
        ApiClient.get().getStockeventsFileSales(created,mGroupBy, new GenericCallback<List<ReportItemFileClientEvent>>() {
            @Override
            public void onSuccess(List<ReportItemFileClientEvent> data) {
                mAdapter.setItems(data);
                if(data.size()>0){
                    holder.lineFile.setVisibility(View.VISIBLE);
                }else{
                    holder.lineFile.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ReportSaleAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportSale current = getItem(position);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.recylerSales.setLayoutManager(layoutManager);
        final ReportStockEventAdapter mAdapter = new ReportStockEventAdapter(mContext, new ArrayList<ReportStockEvent>());
       // mAdapter.setItems(current.listStockEventSale);
        holder.recylerSales.setAdapter(mAdapter);

        LinearLayoutManager layoutManagerFile = new LinearLayoutManager(mContext);
        holder.recyclerItemsFile.setLayoutManager(layoutManagerFile);
        final ReportItemFileClientAdapter mAdapterItemFileClient = new ReportItemFileClientAdapter(mContext, new ArrayList<ReportItemFileClientEvent>(), current.created);
        // mAdapterItemFileClient.setItems(current.listItems);
        holder.recyclerItemsFile.setAdapter(mAdapterItemFileClient);

        if(position==0){
            holder.viewMore.setVisibility(View.GONE);

            listSales(current.created, mAdapter);
            listSalesItems(current.created,mAdapterItemFileClient,holder);
        }else{
            holder.viewMore.setVisibility(View.VISIBLE);
        }

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.viewMore.setVisibility(View.GONE);

                listSales(current.created,mAdapter);
                listSalesItems(current.created,mAdapterItemFileClient,holder);
            }
        });




    }
}


//  holder.month.setText(DateHelper.get().getNameMonth(current.created).substring(0,3));

       /* if(mGroupBy.equals("day")){
            holder.numberDay.setText(DateHelper.get().numberDay(current.created));
            holder.numberDay.setVisibility(View.VISIBLE);
        }else{
            holder.numberDay.setText("");
            holder.numberDay.setVisibility(View.GONE);
        }*/

//  holder.amount.setText(ValuesHelper.get().getIntegerQuantityByLei(current.efectAmount));
// holder.amountCard.setText(ValuesHelper.get().getIntegerQuantityByLei(current.cardAmount));
// holder.countSales.setText(String.valueOf(current.countSales));
