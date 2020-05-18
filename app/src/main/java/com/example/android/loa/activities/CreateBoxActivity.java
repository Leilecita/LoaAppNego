package com.example.android.loa.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.ValidatorHelper;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.ReportNewBox;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateBoxActivity extends BaseActivity {

    private TextView counted_sale;
    private TextView credit_card;
    private EditText total_amount;
    private EditText rest_box;
    private EditText deposit;
    private EditText detail;
    private TextView date;

    private String mSelectDate;
    private String mRestBoxDayBefore;
    private RelativeLayout date_picker;
    private ImageView mImageView;
    private ImageView mImageViewPosnet;

    private Uri mCropImageUri;
    private String image_path=null;

    private Uri mCropImageUri_posnet;
    private String image_path_posnet=null;

    private String photoname="";

    private Boolean mDetailNoNUll=false;
    private TextView tot_extractions;
    private TextView rest_box_day_before;

    private Double mRestBoxDayBeforeValue;
    private Double mTotalExtractionsByDay;
    private Double mTotalBox=0.0;
    private Double mRestBox=0.0;
    private Double mCountedSale=0.0;
    private Double mCreditCard=0.0;

    private TextView day;
    private TextView month;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_box;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle(" Caja del día ");

        day=findViewById(R.id.day);
        month=findViewById(R.id.month);

        rest_box_day_before=findViewById(R.id.res_box_day_before);
        tot_extractions = findViewById(R.id.tot_extract);
        counted_sale=  findViewById(R.id.counted_sale);
        credit_card=  findViewById(R.id.credit_card);
        total_amount=  findViewById(R.id.total_amount);
        rest_box=  findViewById(R.id.rest_box);
        deposit=  findViewById(R.id.deposit);
        detail=  findViewById(R.id.detail);
        date=  findViewById(R.id.date);
        date_picker=  findViewById(R.id.date_picker);

        mSelectDate=getExpandedDate();
        date.setText(DateHelper.get().getOnlyDate(mSelectDate));
        day.setText(DateHelper.get().numberDay(mSelectDate));
        month.setText(DateHelper.get().getNameMonth2(mSelectDate).substring(0,3));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(CreateBoxActivity.this,R.style.datepicker,
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

                               // String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time ;
                                String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                                date.setText(DateHelper.get().getOnlyDate(datePicker));
                                mSelectDate=datePicker;
                                deposit.setText("");

                                getPreviousBox();
                                //getAmountExtractionByDay();

                                /*
                                mRestBoxDayBefore="0";
                                mRestBoxDayBeforeValue=0.0;
                                rest_box_day_before.setText("0");
                                */

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        LinearLayout takePhoto= findViewById(R.id.select_photo);
        mImageView= findViewById(R.id.imageview);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoname="caja";
                onSelectImageClick(view);
            }
        });

        LinearLayout takePhotoPosnet= findViewById(R.id.select_photo_posnet);
        mImageViewPosnet=findViewById(R.id.imageview_posnet);
        takePhotoPosnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoname="posnet";
                onSelectImageClick(view);

            }
        });

        getPreviousBox();

       // getAmountExtractionByDay();

    }

    private void getPreviousBox(){
        ApiClient.get().getPreviousBox(DateHelper.get().getOnlyDate(mSelectDate),DateHelper.get().getOnlyDateComplete(mSelectDate),
                DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(mSelectDate)),new GenericCallback<ReportNewBox>() {
            @Override
            public void onSuccess(ReportNewBox data) {

                mRestBoxDayBefore= String.valueOf(data.lastBox.rest_box);
                mRestBoxDayBeforeValue = Double.valueOf(mRestBoxDayBefore);
                rest_box_day_before.setText(String.valueOf(mRestBoxDayBefore));

                mTotalExtractionsByDay = data.amountExtractions;
                tot_extractions.setText(String.valueOf(data.amountExtractions));

                loadInfo();
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

   /* private void getAmountExtractionByDay(){
        ApiClient.get().getTotalExtractionAmount(DateHelper.get().getOnlyDateComplete(mSelectDate),
            DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(mSelectDate)), new GenericCallback<AmountResult>() {
                @Override
                public void onSuccess(AmountResult data) {
                    mTotalExtractionsByDay = data.total;
                    tot_extractions.setText(String.valueOf(data.total));

                    loadInfo();
                }
                @Override
                public void onError(Error error) {
                }
            });
    }*/


    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()

                if(photoname.equals("caja")){
                    mCropImageUri = imageUri;
                }else{
                    mCropImageUri_posnet = imageUri;
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);

            } else {

                if(photoname.equals("caja")){
                    image_path = imageUri.getPath();
                }else{
                    image_path_posnet = imageUri.getPath();
                }

                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }else
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            CropImage.ActivityResult  result = CropImage.getActivityResult(data);
            /*
                Este image_path es una imagen que devuelve el cropper tal vez habria que moverla a otro lugar.
                Por ahora va a funcionar bien igual, cuando la envies a la nube se acaba el problema.
            */

            if(photoname.equals("caja")){
                image_path = result.getUri().getPath();
                mImageView.setImageBitmap(BitmapFactory.decodeFile(image_path));

            }else{
                image_path_posnet = result.getUri().getPath();
                mImageViewPosnet.setImageBitmap(BitmapFactory.decodeFile(image_path_posnet));
            }

        }

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {

            if(photoname.equals("caja")){
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCropImageActivity(mCropImageUri);
                } else {
                    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                }

            }else{

                if (mCropImageUri_posnet != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCropImageActivity(mCropImageUri_posnet);
                } else {
                    Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setAllowFlipping(false).setAspectRatio(1,1)
                .start(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(!mDetailNoNUll){
                    Box b=loadInfoBox();

                    if(image_path!=null){
                        try {
                            b.imageData = fileToBase64(image_path);
                        }catch (Exception e){
                        }
                    }

                    if(image_path_posnet!=null){
                        try {
                            b.imageDataPosnet = fileToBase64(image_path_posnet);
                        }catch (Exception e){
                        }
                    }
                    b.created= mSelectDate;

                    final ProgressDialog progress = ProgressDialog.show(this, "Creando caja del día",
                            "Aguarde un momento", true);
                    ApiClient.get().postBox(b, new GenericCallback<Box>() {
                        @Override
                        public void onSuccess(Box data) {
                            setResult(RESULT_OK);
                            finish();
                            progress.dismiss();
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error", "No se pudo crear la caja",getBaseContext());
                        }
                    });

                }else{
                    Toast.makeText(this,"Ingrese detalle de caja", Toast.LENGTH_LONG).show();
                }
                return true;

            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String fileToBase64(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    private void loadInfo(){
        loadAmountSales();   //mCountedSale=
        loadAmountSalesCard();   //mCreditCard=

        counted_sale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {


                System.out.println("ejecuta sale");

                if(!counted_sale.getText().toString().trim().equals("") && ValidatorHelper.get().isTypeDouble(counted_sale.getText().toString().trim())){
                    Double d=Double.valueOf(counted_sale.getText().toString().trim());
                    Double d2=Double.valueOf(mRestBoxDayBefore);

                    total_amount.setText(String.valueOf(d+d2));
                    rest_box.setText(String.valueOf(d+d2-mTotalExtractionsByDay));
                }else{
                    total_amount.setText("");
                    rest_box.setText("");
                }
            }
        });

        total_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

                System.out.println("ejecuta amount");
                if(!total_amount.getText().toString().trim().equals("") && ValidatorHelper.get().isTypeDouble(total_amount.getText().toString().trim())){
                    Double total=Double.valueOf(total_amount.getText().toString().trim());
                    Double restDaybefore=Double.valueOf(mRestBoxDayBefore);
                    Double countedsale=Double.valueOf(counted_sale.getText().toString().trim());

                    Double rest_b=total - mTotalExtractionsByDay;
                    rest_box.setText(String.valueOf(rest_b));

                    if(total != restDaybefore+countedsale){
                        total_amount.setTextColor(getResources().getColor(R.color.loa_red));
                        mDetailNoNUll=true;
                    }else{
                        total_amount.setTextColor(getResources().getColor(R.color.colorPrimaryDarkLetter));
                        mDetailNoNUll=false;
                    }
                }
            }
        });

        rest_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {


                System.out.println("ejecuta box");
                if(!rest_box.getText().toString().trim().equals("") && ValidatorHelper.get().isTypeDouble(rest_box.getText().toString().trim())){
                    Double restbox=Double.valueOf(rest_box.getText().toString().trim());
                    Double total=Double.valueOf(total_amount.getText().toString().trim());
                    Double totalextr=Double.valueOf(mTotalExtractionsByDay);

                    System.out.println("caja  BOXX");
                    if(restbox != total-totalextr){
                        rest_box.setTextColor(getResources().getColor(R.color.loa_red));
                        mDetailNoNUll=true;
                    }else{
                        rest_box.setTextColor(getResources().getColor(R.color.colorPrimaryDarkLetter));
                        mDetailNoNUll=false;
                    }
                }
            }
        });

        detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(!detail.getText().toString().trim().equals("") ){
                   mDetailNoNUll=false;
                }else{
                    mDetailNoNUll=true;
                }
            }
        });

    }

    private Box loadInfoBox(){

        Double countedSale=Double.valueOf((counted_sale.getText().toString().trim().equals("")?"0":counted_sale.getText().toString().trim()));
        Double creditCard=Double.valueOf(credit_card.getText().toString().trim().equals("")?"0":credit_card.getText().toString().trim());

        Double totalAmount= Double.valueOf(total_amount.getText().toString().trim());

        Double restBox= Double.valueOf(rest_box.getText().toString().trim());

        String det=detail.getText().toString().trim();

        String picpath="/uploads/preimpresos/person_color.png";
        String picpathposnet="/uploads/preimpresos/person_color.png";
        Box b= new Box(countedSale,creditCard,totalAmount,restBox,mTotalExtractionsByDay,det,picpath,picpathposnet,mRestBoxDayBeforeValue);

        return b;
    }

    private void loadAmountSales(){
        ApiClient.get().getAmountSalesByDay(mSelectDate, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                counted_sale.setText(String.valueOf(data.total));
                mCountedSale=data.total;

                System.out.println("loadsale");
            }

            @Override
            public void onError(Error error) {
            }
        });
    }

    private void loadAmountSalesCard(){
        ApiClient.get().getAmountSalesByDayCard(mSelectDate, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                credit_card.setText(String.valueOf(data.total));
                mCreditCard=data.total;


                System.out.println("loadcard");
            }

            @Override
            public void onError(Error error) {
            }
        });
    }

    private String getExpandedDate(){
        String date= DateHelper.get().actualDateExtractions();
        String time= DateHelper.get().getOnlyTime(date);

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            //Date date1 = sdf.parse("19:28:00");
            Date date1 = sdf.parse(time);
            //Date date2 = sdf.parse("21:13:00");
            Date date2 = sdf.parse("04:13:00");
            // Outputs -1 as date1 is before date2
            System.out.println(date1.compareTo(date2));

            if(date1.compareTo(date2) < 0){
                System.out.println(date1.compareTo(date2));

                return DateHelper.get().getPreviousDay(date);
            }else{
                return date;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

}

