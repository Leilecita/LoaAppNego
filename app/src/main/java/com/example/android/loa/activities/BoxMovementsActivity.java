package com.example.android.loa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.android.loa.R;
import com.example.android.loa.activities.todelete.BoxByMonthActivity;
import com.example.android.loa.adapters.PageAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.fragments.BaseFragment;
import com.google.android.material.tabs.TabLayout;

public class BoxMovementsActivity extends BaseActivity {

    PageAdapter mAdapter;
    TabLayout mTabLayout;
    LinearLayout button;
    ImageView image_button;

    ViewPager viewPager;

    private LinearLayout home;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        home = findViewById(R.id.line_home);
        title = findViewById(R.id.title);

        title.setText("Loa surf shop");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        viewPager =  findViewById(R.id.viewpager);

        mAdapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setSelectedTabIndicatorHeight(11);

        button= findViewById(R.id.fab_agregarTod);
        image_button= findViewById(R.id.image_button);


        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_text);
                View v= tab.getCustomView();
                TextView t= v.findViewById(R.id.text1);

                setTextByPosition(t,i);

            }
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.white));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.word_clear));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        String name=getIntent().getStringExtra("NAMEFRAGMENT");
        if(name.equals("extractions")){
            selectFragment(1);
        }else if(name.equals("box")){
            selectFragment(3);
        }


        actionFloatingButton();
        setImageButton();
        setVisivilityButton();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                setImageButton();
                actionFloatingButton();
                setVisivilityButton();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_box_movemens;
    }

    public void actionFloatingButton(){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((BaseFragment)f).onClickButton();
                }
            });
        }
    }

    private void setTextByPosition(TextView t, Integer i) {
        if (i == 0) {
            t.setText("ventas");
            t.setTextColor(getResources().getColor(R.color.white));
        } else if (i == 1) {
            t.setText("extracc");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        } else if (i == 2) {
            t.setText("compras");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        } else {
            t.setText("cajas");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        }
    }
    public void selectFragment(int position){
        viewPager.setCurrentItem(position, true);
    }

    public void setImageButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            image_button.setImageResource(((BaseFragment)f).getIconButton());
        }
    }

    public void setVisivilityButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            button.setVisibility(((BaseFragment)f).getVisibility());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if ( id == R.id.action_logout ) {
            SessionPrefs.get(getBaseContext()).logOut();
            startActivity(new Intent(getBaseContext(),LoginActivity.class));
            finish();
            return true;
        }
        else if(id == R.id.action_mov_stock){

            startActivity(new Intent(getBaseContext(), StockMovementsListActivity.class));
        } else if(id == R.id.action_incomes){

            startActivity(new Intent(getBaseContext(), IncomesListActivity.class));
        }else if(id == R.id.action_santi_money){
            startSantiMoneyMovement();
        }else if(id == R.id.action_log_events){
            startHistoryEventsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSantiMoneyMovement(){
        startActivity(new Intent(this, ParallelMoneyMovementsActivity.class));
    }

    private void startHistoryEventsActivity(){
        startActivity(new Intent(this, EventHistoryActivity.class));
    }
}
