package com.example.emily.beaconside;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.example.emily.beaconside.R.layout.activity_rowdata;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    //寫死目前的用戶
    public String uEmail = "\"sandy@gmail.com\"";

    private String JSON_STRING; //用來接收php檔傳回的json

    ArrayList<String> bName_list = new ArrayList<String>();//我的beacon名稱list
    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> distance= new ArrayList<String>();

    Context mContext;
    Button side_new;
    ListView listView1;
    rowdata adapter;
    //ArrayList<String> name= new ArrayList<>(Arrays.asList("Wallet(Tag02)","Key(Qmote)","Camera(xBeacon)","Laptop(xBeacon)"));
    //distance距離先寫死
    //ArrayList<String> distance= new ArrayList<>(Arrays.asList("Out of Range","Out of Range","Out of Range","Out of Range"));

    //ArrayList<String> address = new ArrayList<>(Arrays.asList("D0:39:72:DE:DC:3A","84:EB:18:7A:5B:80","1C:BA:8C:28:8B:5F","1C:BA:8C:28:8B:5F"));

    BluetoothMethod bluetooth = new BluetoothMethod();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        // 初始化藍牙
//        bluetooth.BTinit(getBaseContext());
//        bluetooth.mac = address;
        // 設置listview
        mContext = this;
        listView1=(ListView) findViewById(R.id.listView1);

        //取得用戶擁有的beacon
        getBeacon();
        getUserEvent();
        /*adapter*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* plus button */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SearchDevice.class);
                //傳遞array: event list
                //intent.putStringArrayListExtra("cName_list",cName_list);
                startActivity(intent);
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

    }
    //取得用戶擁有的beacon
    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showMyBeacon();


            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_ALL_BEACON,uEmail);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    //將取得的json轉換為array list, 顯示在畫面上
    private void showMyBeacon(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name

                //bName,macAddress各自單獨存成一個array
                bName_list.add(bName);
                macAddress_list.add(macAddress);
                bPic_list.add(bPic);
                distance.add("out of range");//distance先寫死
            }

            adapter = new rowdata(this,bName_list,distance,macAddress_list,bPic_list);//顯示的方式
            listView1.setAdapter(adapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //選項按下反應
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = bName_list.get(position);      //哪一個列表
                    Toast.makeText(MainActivity.this, item + " selected", Toast.LENGTH_LONG).show(); //顯示訊號
//                bluetooth.bluetoothFunction="searchItem";
//                bluetooth.currentItem= address.get(position);
                /*換畫面 不換Activity*/
                    setContentView(R.layout.activity_search);

                /* infomation on the second page*/
                    TextView deviceInfo = (TextView) findViewById(R.id.beaconinfo);
                    TextView devicedegree = (TextView) findViewById(R.id.beaconinfo);
                    String itemlist = String.valueOf(bluetooth.currentDistance);
//                deviceInfo.setText(itemlist);
//                devicedegree.setText(String.valueOf(bluetooth.currentDegree));
//
//
//                page = 2;
//
//
//                //image direction
//                image = (ImageView) findViewById(R.id.imageViewCompass);
                /*換頁面 有換Activity*/
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, search.class);
//                intent.putExtra("sayHi",123);//試著傳值
//                startActivity(intent);
                }
            } );

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void getUserEvent(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showUserEvent();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_USER_EVENT,uEmail);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showUserEvent() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String cName = jo.getString("cName");//取得event名稱
                //Toast.makeText(MainActivity.this, cName, Toast.LENGTH_LONG).show();
                //event存成一條array
                cName_list.add(cName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.item_side);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Toast.makeText(MainActivity.this,"我按了編輯",Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_delete:
                return true;
            default:
                return true;
        }
    }

    /*我的物品 右邊的三點點選單*/
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
                Toast.makeText(MainActivity.this,"我按了編輯",Toast.LENGTH_LONG).show();
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
        bluetooth.bluetoothFunction = "myItemDistance";
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                bluetooth.getStartSearch(getBaseContext(), new Long(360000));
            }
        }, 10000);
        adapter=new rowdata(this,bName_list,bluetooth.myDeviceDistance,bluetooth.mac,bPic_list);//顯示的方式
        listView1.setAdapter(adapter);
    }

}
