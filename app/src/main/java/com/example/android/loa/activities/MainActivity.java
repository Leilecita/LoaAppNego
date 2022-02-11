package com.example.android.loa.activities;

import android.content.Intent;

import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loa.R;
import com.example.android.loa.adapters.PageAdapter;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.fragments.BaseFragment;

import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    ImageView buys;

    Toolbar myToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_incomes) {
            startActivity(new Intent(getBaseContext(), IncomesListActivity.class));
        } else if (id == R.id.nav_movements) {
            startActivity(new Intent(getBaseContext(), StockMovementsListActivity.class));
        } else if (id == R.id.nav_billing) {
            startActivity(new Intent(getBaseContext(), ParallelBillingActiviy.class));
        }else if (id == R.id.nav_prices) {
            startActivity(new Intent(getBaseContext(), PriceEventsActivity.class));
        } else if (id == R.id.nav_events) {
            startHistoryEventsActivity();
        } else if (id == R.id.nav_price_manager) {
            startActivity(new Intent(getBaseContext(), PriceManagerActivity.class));
        } else if (id == R.id.nav_movements_santi) {
            if(SessionPrefs.get(this).getName().equals("santi") || SessionPrefs.get(this).getName().equals("lei")){
                startSantiMoneyMovement();
            }else{
                Toast.makeText(this,"Debe loguearse como administrador", Toast.LENGTH_SHORT).show();
            }
        }else if( id == R.id.nav_session){
            signOut();
        }if (id == R.id.nav_buy) {
            Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
            i.putExtra("NAMEFRAGMENT", "compras");
            startActivity(i);
        }else if( id == R.id.nav_list_boxes){
            Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
            i.putExtra("NAMEFRAGMENT", "box");
            startActivity(i);
        }else if( id == R.id.nav_extractions){

            Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
            i.putExtra("NAMEFRAGMENT", "extractions");
            startActivity(i);
        }else if( id == R.id.nav_sales){
            Intent i = new Intent(getBaseContext(), BoxMovementsActivity.class);
            i.putExtra("NAMEFRAGMENT", "lei");
            startActivity(i);
        }else if( id == R.id.nav_debts){
            startActivity(new Intent(getBaseContext(),ClientsActivity.class));
        }else if( id == R.id.nav_personal){
            startActivity(new Intent(getBaseContext(),EmployeesActivity.class));
        }else if( id == R.id.nav_statistics){
            startActivity(new Intent(getBaseContext(),StatisticsActivity.class));
        }else if( id == R.id.nav_productos){
            startActivity(new Intent(getBaseContext(),ProductsActivity.class));
        }else if( id == R.id.nav_buys_balance){
            startActivity(new Intent(getBaseContext(),BuyProductsActivity.class));
        }else if( id == R.id.nav_buy_billings){
            startActivity(new Intent(getBaseContext(), BuyBillingsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut(){
        SessionPrefs.get(getBaseContext()).logOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
        finish();
    }

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerLayout.setScrimColor(getResources().getColor(R.color.shadownav));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        TextView name = headerLayout.findViewById(R.id.user_name);
        name.setText(SessionPrefs.get(this).getName());
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem statistics = menu.findItem(R.id.nav_statistics);
        MenuItem movements_santi = menu.findItem(R.id.nav_movements_santi);
       // MenuItem santi = menu.findItem(R.id.santi);

        if (SessionPrefs.get(this).isLoggedIn()) {
            if(!SessionPrefs.get(this).getName().equals("santi") && !SessionPrefs.get(this).getName().equals("lei")){
               movements_santi.setVisible(false);
               statistics.setVisible(false);
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        System.out.println("entra aca");

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        System.out.println("entra aca1");

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

        System.out.println("entra aca2");

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
        buys=findViewById(R.id.compras);

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
                startActivity(new Intent(getBaseContext(), StatisticsActivity.class));
            }
        });

        buys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), BuyProductsActivity.class));
            }
        });
    }

    @Override
    public int getLayoutRes() {
       // return R.layout.activity_main2;
        return R.layout.activity_main_drawer;
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
    private void startHistoryEventsActivity(){
        startActivity(new Intent(this, EventHistoryActivity.class));
    }

    private void startSantiMoneyMovement(){
        startActivity(new Intent(this, ParallelMoneyMovementsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main3, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_list_students){
            startActivity(new Intent(getBaseContext(), StudentsListActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }
}


/*
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
*/
