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
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Employee;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CreateEmployeeActivity extends BaseActivity {
    private EditText mUserName;
    private EditText mSurname;
    private EditText mUserPhone;
    private EditText mUserAddress;

    private ImageView mImageView;

    private Uri mCropImageUri;
    private String image_path=null;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_employee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mUserName = findViewById(R.id.user_name);
        mSurname = findViewById(R.id.user_surname);
        mImageView =  findViewById(R.id.imageview);
        mUserPhone =  findViewById(R.id.user_phone);
        mUserAddress =  findViewById(R.id.user_address);

        CardView takePhoto= findViewById(R.id.select_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });

    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                String name=mUserName.getText().toString().trim();
                String surname=mSurname.getText().toString().trim();
                String address=mUserAddress.getText().toString().trim();
                String phone=mUserPhone.getText().toString().trim();

                String picpath="/uploads/preimpresos/person_color.png";

                final Employee newEmployee=new Employee(name,surname,address,phone,picpath);

                if(image_path!=null){
                    try {
                        newEmployee.imageData = fileToBase64(image_path);
                    }catch (Exception e){
                    }
                }
                final ProgressDialog progress = ProgressDialog.show(this, "Creando cliente",
                        "Aguarde un momento", true);

                ApiClient.get().postEmployee(newEmployee, new GenericCallback<Employee>() {
                    @Override
                    public void onSuccess(Employee data) {
                        finish();
                        progress.dismiss();
                    }

                    @Override
                    public void onError(Error error) {
                        DialogHelper.get().showMessage("Error","Error al crear el usuario",CreateEmployeeActivity.this);
                    }
                });
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
