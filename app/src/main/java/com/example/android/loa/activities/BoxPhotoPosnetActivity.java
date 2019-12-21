package com.example.android.loa.activities;

import android.content.Context;
import android.content.Intent;
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
import com.github.chrisbanes.photoview.PhotoView;

public class BoxPhotoPosnetActivity extends BaseActivity {

    private Box mCurrentBox;
    private ImageView photo;
    private PhotoView photoView;
    private  PhotoView photoViewPosnet;

    public static void start(Context mContext, Long id, String image_url, String dateToShow, String name){
        Intent i=new Intent(mContext, BoxPhotoPosnetActivity.class);
        i.putExtra("ID",id);
        i.putExtra("PHOTOURL",image_url);
        i.putExtra("DATE",dateToShow);
        i.putExtra("NAME",name);

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

        photoView = (PhotoView) findViewById(R.id.image_box);
        // photoView.setImageResource(R.drawable.image);
        //  photo= findViewById(R.id.image_box);

        TextView title= findViewById(R.id.title);
        title.setText("Caja del día "+getIntent().getStringExtra("DATE"));

        Glide.with(this).load(ApiUtils.getImageUrl(getIntent().getStringExtra("PHOTOURL"))).into(photoView);
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
                Glide.with(BoxPhotoPosnetActivity.this).load(ApiUtils.getImageUrl(data.image_url_posnet)).into(photoView);
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

                PhotoPosnetEditActivity.startBox(this,getIntent().getLongExtra("ID",-1));
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
