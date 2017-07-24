package com.ezgo.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Context context;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private Menu mMenu;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        fragmentManager = getSupportFragmentManager();



        //--------------取得裝置ID-----------------
        if(getId==null){
            getId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //取得Android ID
            if(getId.equals("9774d56d682e549c")){
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //取得Device ID
                getId = tm.getDeviceId();
            }
        }
        TextView text_ar = (TextView) findViewById(R.id.text_ar);
        //text_ar.setText(getId);


        //--------------設定ActionBar-----------------
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //--------------設定預設字型-----------------
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/wp010-08.ttf");

        //---------------drawer設定---------------------
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //-----------------NavigationView設定------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //---------設定預設地圖Fragment--------
        FragmentTransaction defultFragment = getSupportFragmentManager().beginTransaction();
        defultFragment.replace(R.id.main_frame, new MainFragment());
        defultFragment.commit();

    }

    //--------------------------------------------------選擇動物----------------------------------------
    public void chooseAnimal(View view){
        Intent intent=new Intent();
        intent.setClass(context,ChooseAnimalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){//當按下左上三條線或顯示工具列
            return true;
        }

        switch(item.getItemId()) {
            case R.id.action_food:
                Toast.makeText(this, "已選擇"+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_drink:
                Toast.makeText(this, "已選擇"+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_gift:
                Toast.makeText(this, "已選擇"+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_toilet:
                Toast.makeText(this, "已選擇"+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_hall:
                Toast.makeText(this, "已選擇"+item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if(fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.nav_map:     //---------切換地圖頁面---------
                fragment = new MainFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.items).setVisible(true);
                break;
            case R.id.nav_info:     //---------切換園區簡介頁面---------
                fragment = new IntroductionFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.items).setVisible(false);
                break;
            case R.id.nav_mile:     //---------切換里程碑頁面---------
                fragment = new MilestoneFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.items).setVisible(false);
                break;
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
