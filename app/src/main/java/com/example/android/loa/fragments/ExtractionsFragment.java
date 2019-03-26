package com.example.android.loa.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CurrentValuesHelper;
import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.Interfaces.OnExtractionsAmountChange;
import com.example.android.loa.R;
import com.example.android.loa.activities.ExtractionsActivity;
import com.example.android.loa.adapters.ExtractionAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Extraction;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExtractionsFragment extends BaseFragment implements Paginate.Callbacks,OnExtractionsAmountChange {

    private RecyclerView mRecyclerView;
    private ExtractionAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;

    private boolean mFirstDate;

    private String mSelectDate;
    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    public void onClickButton(){ addExtraction();  }
    public int getIconButton(){
        return R.drawable.add_white;
    }

    public int getVisibility(){
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            mAdapter.setLastDateDecoration("");
          //  mFirstDate.setText("");
            hasMoreItems=true;
            listExtractions();
        }
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        mAdapter.setLastDateDecoration("");
       // mFirstDate.setText("");
        hasMoreItems=true;
        listExtractions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_extractions, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_extractions);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ExtractionAdapter(getActivity(), new ArrayList<Extraction>(),false);

        mFirstDate=false;
        // registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        mAdapter.setOnExtractionsAmountCangeListener(ExtractionsFragment.this);

        TextView ref=mRootView.findViewById(R.id.ref);
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.cuad_ref, null);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //STICKY

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
      // mRecyclerView.addItemDecoration(new DividerDecoration(this));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

       //------------------------


        implementsPaginate();

        return mRootView;
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


    public void reloadExtractionsAmount(){
        clearView();
    }


    private void listExtractions(){
        loadingInProgress=true;
        ApiClient.get().getExtractionsByPage(mCurrentPage, "", new GenericCallback<List<Extraction>>() {
            @Override
            public void onSuccess(List<Extraction> data) {
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
    private void addExtraction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_extraction, null);
        builder.setView(dialogView);

        final TextView description=  dialogView.findViewById(R.id.description);
        final Spinner spinner=  dialogView.findViewById(R.id.spinner_type);
        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView value=  dialogView.findViewById(R.id.value);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        createSpinner(spinner);

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
                String descriptionT=description.getText().toString().trim();
                Double valueT=0.0;
                if(!value.getText().toString().trim().matches("")){
                    valueT=Double.valueOf(value.getText().toString().trim());
                }

                String typeT=String.valueOf(spinner.getSelectedItem());

                Extraction extr=new Extraction(descriptionT,typeT,valueT);
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
