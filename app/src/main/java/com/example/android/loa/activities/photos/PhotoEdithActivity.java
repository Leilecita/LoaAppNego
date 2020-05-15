package com.example.android.loa.activities.photos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.loa.DialogHelper;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoEdithActivity extends BaseActivity {

    private ImageView mImageView;
    private Uri mCropImageUri;
    private String image_path = null;
    private Client mCurrentClient;
    private Employee mCurrentEmployee;

    private Box mCurrentBox;

    private boolean isBox=false;
    private boolean isClient=false;
    private boolean isEmployee=false;

    public static void start(Context mContext, Client client) {
        Intent i = new Intent(mContext, PhotoEdithActivity.class);
        i.putExtra("ID", client.id);
        mContext.startActivity(i);
    }

    public static void startEmployee(Context mContext, Employee employee) {
        Intent i = new Intent(mContext, PhotoEdithActivity.class);
        i.putExtra("IDEMPLOYEE", employee.id);
        mContext.startActivity(i);
    }

    public static void startBox(Context mContext, Long boxid) {
        Intent i = new Intent(mContext, PhotoEdithActivity.class);
        i.putExtra("IDBOX", boxid);
        mContext.startActivity(i);
    }


    @Override
    public int getLayoutRes() {
        return R.layout.activity_photo_edith;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();
        //photo
        mImageView = findViewById(R.id.imageview);

       // clientOrEmployee();
        isClient();
        isBox();
        isEmployee();


        if(isClient){
            ApiClient.get().getClient( getIntent().getLongExtra("ID", -1), new GenericCallback<Client>() {
                @Override
                public void onSuccess(Client data) {
                    mCurrentClient=data;
                    Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(mCurrentClient.getImage_url())).into(mImageView);
                }

                @Override
                public void onError(Error error) {

                }
            });
        }else if(isEmployee){
            ApiClient.get().getEmployee(getIntent().getLongExtra("IDEMPLOYEE", -1), new GenericCallback<Employee>() {
                @Override
                public void onSuccess(Employee data) {
                    mCurrentEmployee=data;
                    Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(mCurrentEmployee.getImage_url())).into(mImageView);
                }

                @Override
                public void onError(Error error) {

                }
            });
        }else if(isBox){
            ApiClient.get().getBox(getIntent().getLongExtra("IDBOX", -1), new GenericCallback<Box>() {
                @Override
                public void onSuccess(Box data) {
                    mCurrentBox=data;
                    Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(mCurrentBox.image_url)).into(mImageView);
                }

                @Override
                public void onError(Error error) {

                }
            });

        }


        CardView takePhoto = findViewById(R.id.select_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });
    }


    private void isBox(){
        Long l= getIntent().getLongExtra("IDBOX", -1);
        if(l>0){
            isBox=true;
            System.out.println("box");
        }

    }

    private void isEmployee(){
        Long l= getIntent().getLongExtra("IDEMPLOYEE", -1);
        if(l>0){
            isEmployee=true;
            System.out.println("empleado");
        }
    }

    private void isClient(){
        Long l= getIntent().getLongExtra("ID", -1);
        if(l>0){
            isClient=true;
            System.out.println("client");
        }
    }
   /* private void clientOrEmployee(){
       Long l= getIntent().getLongExtra("ID", -1);
       if(l <0 ){
           isClient=false;
       }else{
           isClient=true;
       }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (image_path != null) {
                    try {
                        if(isClient){
                            mCurrentClient.imageData = fileToBase64(image_path);
                        }else if(isEmployee){
                            mCurrentEmployee.imageData = fileToBase64(image_path);
                        }else if(isBox){
                            mCurrentBox.imageData = fileToBase64(image_path);
                        }

                    } catch (Exception e) {
                    }

                    final ProgressDialog progress = ProgressDialog.show(this, "Editando foto",
                            "Aguarde un momento", true);

                    if(isClient){
                        ApiClient.get().putClient(mCurrentClient, new GenericCallback<Client>() {
                            @Override
                            public void onSuccess(Client data) {
                                Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(data.getImage_url())).into(mImageView);
                                finish();
                                progress.dismiss();
                            }

                            @Override
                            public void onError(Error error) {
                                DialogHelper.get().showMessage("Error", "La foto no ha sido editada", getBaseContext());
                            }
                        });
                    }else if(isEmployee){

                        ApiClient.get().putEmployee(mCurrentEmployee, new GenericCallback<Employee>() {
                            @Override
                            public void onSuccess(Employee data) {
                                Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(data.getImage_url())).into(mImageView);
                                finish();
                                progress.dismiss();
                            }

                            @Override
                            public void onError(Error error) {
                                DialogHelper.get().showMessage("Error", "La foto no ha sido editada", getBaseContext());
                            }
                        });
                    }else if(isBox){
                        ApiClient.get().putBox(mCurrentBox, new GenericCallback<Box>() {
                            @Override
                            public void onSuccess(Box data) {
                                Glide.with(getBaseContext()).load(ApiUtils.getImageUrl(data.image_url)).into(mImageView);
                                finish();
                                progress.dismiss();
                            }

                            @Override
                            public void onError(Error error) {
                                DialogHelper.get().showMessage("Error", "La foto no ha sido editada", getBaseContext());
                            }
                        });
                    }
                }

                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);

            } else {
                image_path = imageUri.getPath();
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            image_path = result.getUri().getPath();

            mImageView.setImageBitmap(BitmapFactory.decodeFile(image_path));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setAllowFlipping(false).setAspectRatio(1, 1)
                .start(this);
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