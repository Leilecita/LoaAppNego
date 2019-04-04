package com.example.android.loa.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.loa.R;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;


public class BoxPhotoActivity extends BaseActivity {

    private Box mCurrentBox;
    private ImageView photo;

    public static void start(Context mContext, Box box,String dateToShow){
        Intent i=new Intent(mContext, BoxPhotoActivity.class);
        i.putExtra("ID",box.id);
        i.putExtra("PHOTOURL",box.image_url);
        i.putExtra("DATE",dateToShow);
        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_box_photo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        photo= findViewById(R.id.image_box);
        TextView title= findViewById(R.id.title);
        title.setText("Caja del día "+getIntent().getStringExtra("DATE"));
        Glide.with(this).load(ApiUtils.getImageUrl(getIntent().getStringExtra("PHOTOURL"))).into(photo);

    }

    @Override
    public void onResume() {
        super.onResume();
        getBox();

    }
    private void getBox(){
        ApiClient.get().getBox(getIntent().getLongExtra("ID", -1), new GenericCallback<Box>() {
            @Override
            public void onSuccess(Box data) {
                Glide.with(BoxPhotoActivity.this).load(ApiUtils.getImageUrl(data.image_url)).into(photo);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edith, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edith:

                PhotoEdithActivity.startBox(this,getIntent().getLongExtra("ID",-1));
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
