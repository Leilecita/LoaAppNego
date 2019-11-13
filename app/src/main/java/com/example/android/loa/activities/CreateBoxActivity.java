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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


public class CreateBoxActivity extends BaseActivity {

    private EditText counted_sale;
    private EditText credit_card;
    private EditText total_amount;
    private EditText rest_box;
    private EditText deposit;
    private EditText detail;
    private TextView date;

    private String mSelectDate;

    private String mRestBoxDayBefore;

    private Double mTotalExtractionsByDay;

    private ImageView date_picker;
    private ImageView mImageView;

    private Uri mCropImageUri;
    private String image_path=null;

    private Boolean mDetailNoNUll=false;

    private TextView tot_extractions;
    private TextView rest_box_day_before;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_box;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle(" Caja del día ");

        mRestBoxDayBefore= getIntent().getStringExtra("RESTBOX");

        rest_box_day_before=findViewById(R.id.res_box_day_before);
        tot_extractions = findViewById(R.id.tot_extract);

        rest_box_day_before.setText(String.valueOf(mRestBoxDayBefore));

        counted_sale=  findViewById(R.id.counted_sale);
        credit_card=  findViewById(R.id.credit_card);
        total_amount=  findViewById(R.id.total_amount);
        rest_box=  findViewById(R.id.rest_box);
        deposit=  findViewById(R.id.deposit);
        detail=  findViewById(R.id.detail);
        date=  findViewById(R.id.date);
        date_picker=  findViewById(R.id.date_picker);

        mSelectDate=DateHelper.get().getActualDate();

        date.setText(DateHelper.get().getOnlyDate(mSelectDate));

        date_picker.setOnClickListener(new View.OnClickListener() {
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

                                String datePicker=sdayOfMonth + "/" + smonthOfYear + "/" +  year +" "+time ;
                                date.setText(datePicker);
                                mSelectDate=datePicker;
                                deposit.setText("");
                                getAmountExtractionByDay();
                                mRestBoxDayBefore="0";
                                rest_box_day_before.setText("0");

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        ImageView takePhoto= findViewById(R.id.select_photo);
        mImageView= findViewById(R.id.imageview);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });

        getAmountExtractionByDay();
        loadInfo();
    }

    private void getAmountExtractionByDay(){
        ApiClient.get().getTotalExtractionAmount(DateHelper.get().getOnlyDateComplete(DateHelper.get().changeFormatDateUserToServer(mSelectDate)),
            DateHelper.get().getOnlyDateComplete(DateHelper.get().getNextDay(DateHelper.get().changeFormatDateUserToServer(mSelectDate))), new GenericCallback<AmountResult>() {
                @Override
                public void onSuccess(AmountResult data) {
                    mTotalExtractionsByDay = data.total;
                    tot_extractions.setText(String.valueOf(data.total));
                }
                @Override
                public void onError(Error error) {
                }
            });
    }


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
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);

            } else {
                image_path = imageUri.getPath();
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
            image_path = result.getUri().getPath();
            mImageView.setImageBitmap(BitmapFactory.decodeFile(image_path));
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
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
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
                      /*  Double countedSale=Double.valueOf((counted_sale.getText().toString().trim().equals("")?"0":counted_sale.getText().toString().trim()));
                Double creditCard=Double.valueOf(credit_card.getText().toString().trim().equals("")?"0":credit_card.getText().toString().trim());
                Double totalAmount=Double.valueOf(total_amount.getText().toString().trim().equals("")?"0":total_amount.getText().toString().trim());

                Double restBox=Double.valueOf(rest_box.getText().toString().trim().equals("")?"0":rest_box.getText().toString().trim());
                Double dep=Double.valueOf(deposit.getText().toString().trim().equals("")?"0":deposit.getText().toString().trim());
                String det=detail.getText().toString().trim();


                String picpath="/uploads/preimpresos/person_color.png";
                Box b= new Box(countedSale,creditCard,totalAmount,restBox,dep,det,picpath);
                */

                    Box b=loadInfoBox();

                    if(image_path!=null){
                        try {
                            b.imageData = fileToBase64(image_path);
                        }catch (Exception e){
                        }
                    }
                    b.created= DateHelper.get().changeFormatDateUserToServer(mSelectDate);

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
        counted_sale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                if(!total_amount.getText().toString().trim().equals("") && ValidatorHelper.get().isTypeDouble(total_amount.getText().toString().trim())){
                    Double total=Double.valueOf(total_amount.getText().toString().trim());
                    Double restDaybefore=Double.valueOf(mRestBoxDayBefore);
                    Double countedsale=Double.valueOf(counted_sale.getText().toString().trim());

                    if(total != restDaybefore+countedsale){
                        total_amount.setTextColor(getResources().getColor(R.color.loa_red));
                        mDetailNoNUll=true;
                    }else{
                        total_amount.setTextColor(getResources().getColor(R.color.word));
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
                if(!rest_box.getText().toString().trim().equals("") && ValidatorHelper.get().isTypeDouble(rest_box.getText().toString().trim())){
                    Double restbox=Double.valueOf(rest_box.getText().toString().trim());
                    Double total=Double.valueOf(total_amount.getText().toString().trim());
                    Double totalextr=Double.valueOf(mTotalExtractionsByDay);

                    if(restbox != total-totalextr){
                        rest_box.setTextColor(getResources().getColor(R.color.loa_red));
                        mDetailNoNUll=true;
                    }else{
                        rest_box.setTextColor(getResources().getColor(R.color.word));
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
        Box b= new Box(countedSale,creditCard,totalAmount,restBox,mTotalExtractionsByDay,det,picpath,Double.valueOf(mRestBoxDayBefore));

        return b;
    }
}

