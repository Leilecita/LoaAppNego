package com.example.android.loa.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.loa.R;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutRes());

    }



    public abstract int getLayoutRes();

    public void showBackArrow(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setContentView(int layoutResId) {
        setContentView(getLayoutInflater().inflate(layoutResId, null));
    }

    @Override
    public void setContentView( View view ) {
        ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base, null);
        ViewGroup contentFrame = root.findViewById(R.id.main_content);
        contentFrame.addView(view,0);
        super.setContentView(root);
    }

}
