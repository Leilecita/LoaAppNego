package com.example.android.loa.activities;

import android.content.Intent;

import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.loa.R;
import com.example.android.loa.adapters.PageAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.fragments.BaseFragment;

import java.util.List;

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
    ImageView statistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ApiClient.get().getClients(new GenericCallback<List<ReportSimpelClient>>() {
            @Override
            public void onSuccess(List<ReportSimpelClient> data) {

            }

            @Override
            public void onError(Error error) {
                System.out.println(error.message);
                System.out.println(error.result);
                if(error.message.equals("Session invalida")){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        });

        System.out.println(SessionPrefs.get(this).getToken());
        System.out.println(SessionPrefs.get(this).getName());

        setTitle("Loa surf shop");
        extractions=findViewById(R.id.extractions);
        box=findViewById(R.id.box);
        listBoxes=findViewById(R.id.listcajas);
        products=findViewById(R.id.mercaderia);
        hours=findViewById(R.id.hours);
        clients=findViewById(R.id.clients);
        statistics=findViewById(R.id.statistics);

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

        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"todavia no esta implementado, paciencia",Toast.LENGTH_SHORT).show();
            }
        });


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
        }
        else if(id == R.id.action_mov_stock){

            startActivity(new Intent(getBaseContext(), StockMovementsListActivity.class));
        }else if(id == R.id.action_incomes){

            startActivity(new Intent(getBaseContext(), IncomesListActivity.class));
        }else if(id == R.id.action_log_events){
           startHistoryEventsActivity();
        }else if(id == R.id.action_santi_money){
            if(SessionPrefs.get(this).getName().equals("santi") || SessionPrefs.get(this).getName().equals("lei")){
                startSantiMoneyMovement();
            }else{
                Toast.makeText(this,"Debe loguearse como administrador", Toast.LENGTH_SHORT).show();
            }
        }else if(id == R.id.action_price_events){
            startActivity(new Intent(getBaseContext(), PriceEventsActivity.class));
        }else if(id == R.id.action_product_prices){
            startActivity(new Intent(getBaseContext(), PriceManagerActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    private void startHistoryEventsActivity(){
        startActivity(new Intent(this, EventHistoryActivity.class));
    }

    private void startSantiMoneyMovement(){
        startActivity(new Intent(this, ParallelMoneyMovementsActivity.class));
    }
}