package com.example.android.loa.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.DividerDecoration;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.R;
import com.example.android.loa.activities.todelete.ExtractionsActivity;
import com.example.android.loa.adapters.ReportExtractionAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.network.models.ReportExtraction;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.types.ExtractionType;
import com.example.android.loa.types.GroupByType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExtractionsFragment extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ReportExtractionAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;
    private String mSelectDate;
    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    private LinearLayout bottomSheet;
    private LinearLayout spendingsFilterSanti;
    private LinearLayout spendingsFilterLocal;
    private LinearLayout spendingsFilterPers;
    private LinearLayout santiFilter;
    private LinearLayout salariesFilter;
    private LinearLayout mercFilter;
    private LinearLayout allFilter;

    private LinearLayout monthFilter;
    private LinearLayout dayFilter;


    private ExtractionType selectedType= ExtractionType.ALL;
    private GroupByType groupByType= GroupByType.DAY;

    public void onClickButton(){ addExtraction();  }
    public int getIconButton(){
        return R.drawable.add_white;
    }

    public int getVisibility(){
        return 0;
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    private void clearAndList(){
        clearView();
        listExtractions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_extractions, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_extractions);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportExtractionAdapter(getActivity(), new ArrayList<ReportExtraction>());

        // registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);


        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        //mRecyclerView.addItemDecoration(new DividerDecoration(getContext()));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
        //STICKY


       //------------------------

        bottomSheet = mRootView.findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);


        topBarListener(bottomSheet);
       // bts(bsb);

        implementsPaginate();

        return mRootView;
    }

    private void topBarListener(View bottomSheet){
        spendingsFilterSanti=bottomSheet.findViewById(R.id.spendings);
        spendingsFilterLocal=bottomSheet.findViewById(R.id.spendings_local);
        spendingsFilterPers=bottomSheet.findViewById(R.id.spendings_pers);
        salariesFilter=bottomSheet.findViewById(R.id.salaries);
        mercFilter=bottomSheet.findViewById(R.id.mercaderia);
        santiFilter=bottomSheet.findViewById(R.id.extractions);
        allFilter=bottomSheet.findViewById(R.id.all);

        monthFilter=bottomSheet.findViewById(R.id.mes);
        dayFilter=bottomSheet.findViewById(R.id.dia);

        monthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupByType=GroupByType.MONTH;

                clearAndList();
                mAdapter.setGroupBy(groupByType.getName());
            }
        });

        dayFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupByType=GroupByType.DAY;
                mAdapter.setGroupBy(groupByType.getName());
                clearAndList();
            }
        });

        allFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.ALL;
                clearAndList();
            }
        });

        spendingsFilterPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.GASTO_PERSONAL;
                clearAndList();
            }
        });
        spendingsFilterLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.GASTO_LOCAL;
                clearAndList();
            }
        });

        spendingsFilterSanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.GASTO_SANTI;
                clearAndList();
            }
        });

        santiFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.SANTI;
                clearAndList();
            }
        });

        salariesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.SUELDO;
                clearAndList();
            }
        });

        mercFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType= ExtractionType.MERCADERIA;
                clearAndList();
            }
        });

    }

    private void selectDate(){

                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
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
                                mSelectDate=datePicker;
                                start();
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
    }

    private void start(){
        Intent intent= new Intent(getContext(), ExtractionsActivity.class);
        intent.putExtra("DATE",mSelectDate);
        intent.putExtra("NEXTDATE",DateHelper.get().getNextDay(mSelectDate));
        getActivity().startActivity(intent);
    }



    private void listExtractions(){
        loadingInProgress=true;



        ApiClient.get().getExtractions(mCurrentPage, selectedType.getName(),groupByType.getName(),new GenericCallback<List<ReportExtraction>>() {
            @Override
            public void onSuccess(List<ReportExtraction> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }
                }
                loadingInProgress = false;
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
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

    private void addExtraction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_extraction, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);

        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_type1);
        final Spinner spinnerDetail=  dialogView.findViewById(R.id.spinner_detail);

        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        //SPINNER TYPE
        List<String> spinner_type = new ArrayList<>();
        spinner_type.add("Tipo");
        spinner_type.add("Gasto santi");
        spinner_type.add("Gasto local");
        spinner_type.add("Gasto personal");
        spinner_type.add("Mercaderia");
        spinner_type.add("Santi extr");
        spinner_type.add("Sueldo");

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(getContext(),
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
                    array=createArrayGastosLocal();
                }else if(itemSelected.equals("Gasto personal")){
                    array=createArrayGastosPersonales();
                }else if(itemSelected.equals("Gasto santi")){
                    array=createArrayGastosSanti();
                }else if(itemSelected.equals("Mercaderia")){
                    array=createArrayMerc();
                }else if(itemSelected.equals("Santi extr")){
                    array=createArrayExtr();

                }else if(itemSelected.equals("Sueldo")){
                    array=createArraySueldos();

                }

                ArrayAdapter<String> adapter_detail = new ArrayAdapter<String>(getContext(),
                        R.layout.spinner_item,array);
                adapter_detail.setDropDownViewResource(R.layout.spinner_item);
                spinnerDetail.setAdapter(adapter_detail);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        date.setText(DateHelper.get().getActualDate());

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
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

                                String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time ;
                                date.setText(datePicker);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });


        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String type= String.valueOf(spinnerType.getSelectedItem());

                String descr=String.valueOf(spinnerDetail.getSelectedItem());

                String detail=description.getText().toString().trim();

                Double valueT=0.0;
                if(!value.getText().toString().trim().matches("")){
                    valueT=Double.valueOf(value.getText().toString().trim());
                }

                Extraction extr= new Extraction(descr,type,valueT,detail);
                extr.created= DateHelper.get().changeFormatDateUserToServer(date.getText().toString().trim());

                ApiClient.get().postExtraction(extr, new GenericCallback<Extraction>() {
                    @Override
                    public void onSuccess(Extraction data) {
                        clearView();
                        EventBus.getDefault().post(new RefreshBoxesEvent("Hey event subscriber!"));
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo crear la extracción",getContext());

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
        dialog.show();

    }

    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 0;
                    }
                })
                .build();
    }

    private void createSpinner(final Spinner spinner) {

        ArrayAdapter<String> adapter_extr = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item, getResources().getStringArray(R.array.types));

        adapter_extr.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter_extr);


    }


    @Override
    public void onLoadMore() {
        listExtractions();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.search);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectDate();

                return false;
            }
        });

    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                selectDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}
