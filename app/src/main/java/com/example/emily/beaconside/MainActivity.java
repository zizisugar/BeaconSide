package com.example.emily.beaconside;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.emily.beaconside.R.layout.activity_rowdata;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,PopupMenu.OnMenuItemClickListener{

    Context mContext;
    Button side_new;
    ListView listView1;
    rowdata adapter;
    ArrayList<String> name= new ArrayList<>(Arrays.asList("Wallet(Tag02)","Key(Qmote)","Camera(xBeacon)","Laptop(xBeacon)"));
    ArrayList<String> distance= new ArrayList<>(Arrays.asList("Out of Range","Out of Range","Out of Range","Out of Range"));
    ArrayList<String> address = new ArrayList<>(Arrays.asList("D0:39:72:DE:DC:3A","84:EB:18:7A:5B:80","1C:BA:8C:28:8B:5F","1C:BA:8C:28:8B:5F"));

    BluetoothMethod bluetooth = new BluetoothMethod();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);

        mContext = this;

        listView1=(ListView) findViewById(R.id.listView1);

        adapter=new rowdata(this,name,distance,address);//顯示的方式
        listView1.setAdapter(adapter);

//        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
//        spinner.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* plus button */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchDevice.class));//same as following two
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

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
                findViewById(R.id.chooseGroup).setVisibility(View.VISIBLE);
                findViewById(R.id.chooseClass).setVisibility(View.GONE);


                side_new.setText("+ New group");
            }
        });
        findViewById(R.id.side_class_bt).setOnClickListener(new View.OnClickListener() {//class
            @Override
            public void onClick(View v) {
                findViewById(R.id.side_group_ls).setVisibility(View.GONE);
                findViewById(R.id.side_class_ls).setVisibility(View.VISIBLE);
                findViewById(R.id.chooseClass).setVisibility(View.VISIBLE);
                findViewById(R.id.chooseGroup).setVisibility(View.GONE);

                side_new.setText("+ New classification");
            }
        });
        bluetooth.BTinit(this);
        bluetooth.mac = address;

    }


    /* Item setting */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.item_side, popup.getMenu());

        popup.show();
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.item_side);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                return true;
            case R.id.menu_delete:
                return true;
            default:
                return true;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_side, menu);

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit:
//                editNote(info.id);
                return true;
            case R.id.menu_delete:
//                deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /* Item setting end */


    /* refresh */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
//            startActivity(new Intent(MainActivity.this, new_item.class));//same as following two
//            Intent myIntent = new Intent(getApplicationContext(), new_item.class);
//            startActivityForResult(myIntent, 0);
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* refresh end */

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

    /*經過了dialog卻還是沒開啟 關掉check*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==bluetooth.REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED){
            bluetooth.myStatusBT = false;
        }
        else{
            bluetooth.myStatusBT = true;
        }
        bluetooth.getStartSearch(this, new Long(5000));
    }

    public void refresh() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                bluetooth.getStartSearch(getBaseContext(), new Long(360000));
            }
        }, 10000);
        adapter=new rowdata(this,name,bluetooth.myDeviceDistance,bluetooth.mac);//顯示的方式
        listView1.setAdapter(adapter);
//        spinner.setVisibility(View.GONE);
    }

}
