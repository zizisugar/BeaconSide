package com.example.emily.beaconside;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    Button side_new;
    ListView listView1;
    rowdata adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);

        listView1=(ListView) findViewById(R.id.listView1);
        String[] testValues= new String[]{	"Wallet","Key","Camera","Laptop"};
        String[] testValues2= new String[]{	"3m","5m","1m","1m"};

        adapter=new rowdata(this,testValues,testValues2);//顯示的方式
        listView1.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        side_new = (Button)findViewById(R.id.side_new);



        findViewById(R.id.side_group_bt).setOnClickListener(new View.OnClickListener() {//group
            @Override
            public void onClick(View v) {
                findViewById(R.id.side_class_ls).setVisibility(View.GONE);
                findViewById(R.id.side_group_ls).setVisibility(View.VISIBLE);
                findViewById(R.id.side_group_bt).setBackgroundDrawable(getResources().getDrawable(R.drawable.my_group_l));
                findViewById(R.id.side_class_bt).setBackgroundDrawable(getResources().getDrawable(R.drawable.my_class));
                side_new.setText("+ New group");
//                Toast.makeText(MainActivity.this, "Button1 Click!", Toast.LENGTH_SHORT).show();
//                onBackPressed();
            }
        });
        findViewById(R.id.side_class_bt).setOnClickListener(new View.OnClickListener() {//class
            @Override
            public void onClick(View v) {
                findViewById(R.id.side_group_ls).setVisibility(View.GONE);
                findViewById(R.id.side_class_ls).setVisibility(View.VISIBLE);
                findViewById(R.id.side_group_bt).setBackgroundDrawable(getResources().getDrawable(R.drawable.my_group));
                findViewById(R.id.side_class_bt).setBackgroundDrawable(getResources().getDrawable(R.drawable.my_class_l));
                side_new.setText("+ New classification");
//                Toast.makeText(MainActivity.this, "Button2 Click!", Toast.LENGTH_SHORT).show();
//                onBackPressed();
            }
        });


    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
