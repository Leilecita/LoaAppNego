package com.example.android.loa.activities;

import android.content.Intent;

import com.example.android.loa.activities.todelete.SaleMovementsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.loa.R;
import com.example.android.loa.adapters.PageAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.fragments.BaseFragment;

public class MainActivity extends BaseActivity {

    PageAdapter mAdapter;
    TabLayout mTabLayout;
    FloatingActionButton button;

    ImageView extractions;
    ImageView box;
    ImageView listBoxes;
    ImageView hours;
    ImageView products;
    ImageView clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        extractions=findViewById(R.id.extractions);
        box=findViewById(R.id.box);
        listBoxes=findViewById(R.id.listcajas);
        products=findViewById(R.id.mercaderia);
        hours=findViewById(R.id.hours);
        clients=findViewById(R.id.clients);

        extractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
                i.putExtra("NAMEFRAGMENT", "extractions");
                startActivity(i);

            }
        });
        listBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
                i.putExtra("NAMEFRAGMENT", "box");
                startActivity(i);
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
                i.putExtra("NAMEFRAGMENT", "lei");
                startActivity(i);
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),ProductsActivity.class));
            }
        });
        hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),EmployeesActivity.class));
            }
        });
        clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),ClientsActivity.class));
            }
        });

      /*  ViewPager viewPager =  findViewById(R.id.viewpager);

        mAdapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);

        button= findViewById(R.id.fab_agregarTod);

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
        });*/
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main2;
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

    public void setImageButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            button.setImageResource(((BaseFragment)f).getIconButton());
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
        }else if(id == R.id.action_stock){

            startActivity(new Intent(getBaseContext(), ProductsActivity.class));
        }else if(id == R.id.action_box_month){

            startActivity(new Intent(getBaseContext(), BoxByMonthActivity.class));
        }else if(id == R.id.action_sale){

            startActivity(new Intent(getBaseContext(), SaleMovementsActivity.class));
        }else if(id == R.id.action_employees){

            startActivity(new Intent(getBaseContext(), EmployeesActivity.class));
        }else if(id == R.id.action_clients){
            startActivity(new Intent(getBaseContext(), ClientsActivity.class));
        }

        return super.onOptionsItemSelected(item);

    }
}