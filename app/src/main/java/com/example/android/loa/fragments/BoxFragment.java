package com.example.android.loa.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.CurrentValuesHelper;
import com.example.android.loa.CustomLoadingListItemCreator;
import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.Events.RefreshBoxesEvent;
import com.example.android.loa.R;
import com.example.android.loa.activities.BoxActivity;
import com.example.android.loa.activities.CreateBoxActivity;
import com.example.android.loa.activities.CreateClientActivity;
import com.example.android.loa.activities.ExtractionsActivity;
import com.example.android.loa.adapters.BoxAdapter;
import com.example.android.loa.adapters.ExtractionAdapter;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.network.models.Item_box;
import com.paginate.Paginate;
import com.example.android.loa.network.GenericCallback;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.theartofdev.edmodo.cropper.CropImage;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BoxFragment extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private BoxAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;

    private String mSelectDate;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    public void onClickButton(){
        startActivity(new Intent(getContext(), CreateBoxActivity.class));
           // addBox();

    }
    public int getIconButton(){
        return R.drawable.add_white;
    }

    public int getVisibility(){
        return 0;
    }

    @Subscribe
    public void onEvent(RefreshBoxesEvent event){

        Toast.makeText(getActivity(),event.mMessage,Toast.LENGTH_SHORT).show();
        clearView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            clearView();
        }
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
        listBoxes();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_box, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_box);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BoxAdapter(getActivity(), new ArrayList<Box>());

         registerForContextMenu(mRecyclerView);
         mRecyclerView.setAdapter(mAdapter);
         setHasOptionsMenu(true);

         mSelectDate=DateHelper.get().getActualDate();

        implementsPaginate();
        EventBus.getDefault().register(this);

        return mRootView;
    }

    private void listBoxes(){

            loadingInProgress=true;
            ApiClient.get().getBoxesByPage(mCurrentPage, "", new GenericCallback<List<Box>>() {
                @Override
                public void onSuccess(List<Box> data) {
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

    @Override
    public void onLoadMore() {

            listBoxes();

    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

    private void addBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_dialog_add_box, null);
        builder.setView(dialogView);

        final TextView counted_sale=  dialogView.findViewById(R.id.counted_sale);
        final TextView credit_card=  dialogView.findViewById(R.id.credit_card);
        final TextView total_amount=  dialogView.findViewById(R.id.total_amount);
        final TextView rest_box=  dialogView.findViewById(R.id.rest_box);
        final TextView deposit=  dialogView.findViewById(R.id.deposit);
        final TextView detail=  dialogView.findViewById(R.id.detail);
        final TextView date=  dialogView.findViewById(R.id.date);
        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        ImageView takePhoto= dialogView.findViewById(R.id.select_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  onSelectImageClick(view);
            }
        });


        date.setText(DateHelper.get().getOnlyDate(mSelectDate));
        //deposit.setText(String.valueOf(dep));

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);
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
                                mSelectDate=datePicker;
                                deposit.setText("");

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double countedSale=Double.valueOf((counted_sale.getText().toString().trim().equals("")?"0":counted_sale.getText().toString().trim()));
                Double creditCard=Double.valueOf(credit_card.getText().toString().trim().equals("")?"0":credit_card.getText().toString().trim());
                Double totalAmount=Double.valueOf(total_amount.getText().toString().trim().equals("")?"0":total_amount.getText().toString().trim());

                Double restBox=Double.valueOf(rest_box.getText().toString().trim().equals("")?"0":rest_box.getText().toString().trim());
                Double dep=Double.valueOf(deposit.getText().toString().trim().equals("")?"0":deposit.getText().toString().trim());
                String det=detail.getText().toString().trim();


                String picpath="/uploads/preimpresos/person_color.png";
                Box b= new Box(countedSale,creditCard,totalAmount,restBox,dep,det,picpath);

                b.created= DateHelper.get().changeFormatDateUserToServer(mSelectDate);

                ApiClient.get().postBox(b, new GenericCallback<Box>() {
                    @Override
                    public void onSuccess(Box data) {
                        clearView();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error", "No se pudo crear la caja",getContext());
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

                                Intent intent= new Intent(getContext(), BoxActivity.class);
                                intent.putExtra("DATE",mSelectDate);
                                intent.putExtra("NEXTDATE",DateHelper.get().getNextDay(mSelectDate));
                                startActivity(intent);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
}
