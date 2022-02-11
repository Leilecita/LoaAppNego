package com.example.android.loa.activities.photos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.loa.R;
import com.example.android.loa.activities.BaseActivity;
import com.example.android.loa.activities.DeletedProductsActivity;
import com.example.android.loa.activities.ProductsActivity;
import com.example.android.loa.activities.balances.GeneralBalanceActivity;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.ApiUtils;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.Box;
import com.github.chrisbanes.photoview.PhotoView;


public class BoxPhotoActivity extends BaseActivity {

    private Box mCurrentBox;
    private ImageView photo;
    private  PhotoView photoView;
    private  PhotoView photoViewPosnet;

    private LinearLayout home;
    private LinearLayout options;
    private TextView title;

    public static void start(Context mContext, Long id,String image_url,String dateToShow,String name){
        Intent i=new Intent(mContext, BoxPhotoActivity.class);
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

        photoView = (PhotoView) findViewById(R.id.image_box);



        options = findViewById(R.id.options);
        home = findViewById(R.id.line_home);

        loadOptions();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title= findViewById(R.id.title);
        title.setText("Caja del día "+getIntent().getStringExtra("DATE"));

        if(getIntent().getStringExtra("PHOTOURL").contains("person_color")){
            PhotoEdithActivity.startBox(this,getIntent().getLongExtra("ID",-1));
            finish();
        }else{
            Glide.with(this).load(ApiUtils.getImageUrl(getIntent().getStringExtra("PHOTOURL"))).into(photoView);
        }
    }

    private void loadOptions(){
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(BoxPhotoActivity.this, options);
                popup.getMenuInflater().inflate(R.menu.menu_edith, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edith:
                                PhotoEdithActivity.startBox(BoxPhotoActivity.this,getIntent().getLongExtra("ID",-1));
                                return true;
                            case android.R.id.home:
                                finish();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
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
                System.out.println("imageurl "+data.image_url);
                Glide.with(BoxPhotoActivity.this).load(ApiUtils.getImageUrl(data.image_url)).into(photoView);
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

   /* @Override
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
    }*/


}
