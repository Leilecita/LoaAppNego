package com.example.android.loa.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.cardview.widget.CardView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.android.loa.DateHelper;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateClientActivity extends BaseActivity{

    private EditText mUserName;
    private EditText mUserPhone;
    private EditText mAlternativePhone;
    private EditText mUserAddress;
    private Spinner mEmployeeCreator;

    private ImageView mImageView;

    private Uri mCropImageUri;
    private String image_path=null;

    private LinearLayout home;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_client;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackArrow();

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mUserName = findViewById(R.id.user_name);
        mImageView =  findViewById(R.id.imageview);
        mUserPhone =  findViewById(R.id.user_phone);
        mAlternativePhone =  findViewById(R.id.alternative_phone);
        mUserAddress =  findViewById(R.id.user_address);
        mEmployeeCreator = findViewById(R.id.employee_creator);


        ApiClient.get().getEmployees(new GenericCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> data) {
                createSpinner(mEmployeeCreator,createArray(data));
            }

            @Override
            public void onError(Error error) {

            }
        });


        CardView takePhoto= findViewById(R.id.select_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });

    }

    private void createSpinner(final Spinner spinner,  List<String> data){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(),
                R.layout.spinner_item, data);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    private List<String> createArray(List<Employee> list){
        List<String> listN=new ArrayList<>();
        listN.add(" ");
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).name != null){
                listN.add(list.get(i).getName());
            }
        }
        return listN;
    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
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
                if(mEmployeeCreator.getSelectedItem().toString().trim().equals("")){

                    Toast.makeText(this, "El campo encargado debe estar completo", Toast.LENGTH_LONG).show();
                    return false;
                }else if(mUserName.getText().toString().trim().equals("")){
                    Toast.makeText(this, "El campo nombre debe estar completo", Toast.LENGTH_LONG).show();
                    return false;
                }else{

                    String name=mUserName.getText().toString().trim();
                    String address=mUserAddress.getText().toString().trim();
                    String phone=mUserPhone.getText().toString().trim();
                    String phone2=mAlternativePhone.getText().toString().trim();
                    String employee=mEmployeeCreator.getSelectedItem().toString().trim();

                    String picpath="/uploads/preimpresos/person_color.png";
                    final Client newClient= new Client(name,address,phone,phone2,picpath,0d,employee);
                    newClient.created=DateHelper.get().getActualDate2();
                    if(image_path!=null){
                        try {
                            newClient.imageData = fileToBase64(image_path);
                        }catch (Exception e){
                        }
                    }
                    final ProgressDialog progress = ProgressDialog.show(this, "Creando cliente",
                            "Aguarde un momento", true);

                    ApiClient.get().postClient(newClient, new GenericCallback<Client>() {
                        @Override
                        public void onSuccess(Client data) {
                            finish();
                            progress.dismiss();
                        }

                        @Override
                        public void onError(Error error) {
                            DialogHelper.get().showMessage("Error","Error al crear el usuario",CreateClientActivity.this);
                        }
                    });
                    return true;
                }


            case android.R.id.home:
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

}
