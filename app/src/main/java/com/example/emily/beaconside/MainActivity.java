package com.example.emily.beaconside;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {


    int listItemPositionForPopupMenu;

    Context mContext;
    Button side_new,side_group_bt,side_class_bt;
    View side_class_ls,side_group_ls;
    ImageView chooseGroup,chooseClass;
    ListView listView1;
    rowdata adapter;
    TextView userName;

    BluetoothMethod bluetooth = new BluetoothMethod();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //寫死目前的用戶
    public static String uEmail = "sandy@gmail.com";
    public static String get_uEmail;
    public static String uName;
    private String JSON_STRING; //用來接收php檔傳回的json

    ArrayList<String> bName_list = new ArrayList<String>();//我的beacon名稱list
    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> distance= new ArrayList<String>();

    int[] eventId_array;//儲存event id
    String[] eventName_array;//儲存event name
    int[] groupId_array;//儲存group id
    String[] groupName_array;//儲存group name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);
        // 取得從Login頁面傳來的用戶的FB帳號
        /*Intent intent = this.getIntent();
        uEmail = Login.uEmail;
        uName = intent.getStringExtra("uName");*/
//        if(!intent.getStringExtra("uEmail").equals(""))
//            uEmail = intent.getStringExtra("uEmail");
        get_uEmail = "\""+uEmail+"\"";
     Toast.makeText(this, uEmail, Toast.LENGTH_SHORT).show();
        // 初始化藍牙
        bluetooth.BTinit(this);
        bluetooth.getStartSearchDevice();
        // 設置SwipeView重整
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_main);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresh();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 300);
            }
        });
        //listview

        mContext = this;
        listView1=(ListView) findViewById(R.id.listView1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 設置側邊欄使用者名稱
        userName = (TextView) findViewById(R.id.name);
        userName.setText("Hi! "+uName);

        /* 右下角plus button */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //按下加號換頁到SearchDevice
                Intent toSearchDevice = new Intent();
                toSearchDevice.setClass(MainActivity.this,SearchDevice.class);
                toSearchDevice.putExtra("uEmail",uEmail);
                toSearchDevice.putExtra("eventId_array",eventId_array);
                toSearchDevice.putExtra("eventName_array",eventName_array);
                toSearchDevice.putExtra("groupName_array",groupName_array);
                toSearchDevice.putExtra("groupId_array",groupId_array);
                startActivity(toSearchDevice);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //some id
        //左側滑動選單
        side_new = (Button)findViewById(R.id.side_new);
        side_group_bt = (Button)findViewById(R.id.side_group_bt);
        side_class_bt = (Button)findViewById(R.id.side_class_bt);
        side_class_ls = (View)findViewById(R.id.side_class_ls);
        side_group_ls = (View)findViewById(R.id.side_group_ls);
        chooseGroup = (ImageView)findViewById(R.id.chooseGroup);
        chooseClass = (ImageView)findViewById(R.id.chooseClass);

        side_group_bt.setOnClickListener(new View.OnClickListener() {//group
            @Override
            public void onClick(View v) {
                side_class_ls.setVisibility(View.GONE);
                side_group_ls.setVisibility(View.VISIBLE);
                chooseGroup.setVisibility(View.VISIBLE);
                chooseClass.setVisibility(View.GONE);

                side_new.setText("+ New group");
                side_new.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent();
//                        intent.setClass(MainActivity.this,NewGroup.class);
//                        startActivity(intent);
                        Toast.makeText(MainActivity.this,"I'm Clicked ",Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
        side_class_bt.setOnClickListener(new View.OnClickListener() {//class
            @Override
            public void onClick(View v) {
                side_group_ls.setVisibility(View.GONE);
                side_class_ls.setVisibility(View.VISIBLE);
                chooseClass.setVisibility(View.VISIBLE);
                chooseGroup.setVisibility(View.GONE);

                side_new.setText("+ New Event");
                side_new.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"new event Clicked ",Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
        getBeacon();
        getUserEvent();
        getUserGroup();
    }

    @Override
    public void onResume(){
        super.onResume();
        //uEmail = Login.uEmail;
        //get_uEmail = "\""+uEmail+"\"";
//        refresh();
        getBeacon();
        getUserEvent();
        getUserGroup();
    }
    //取得用戶擁有的beacon
    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showMyBeacon();


            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_ALL_BEACON,get_uEmail);
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
            bName_list = new ArrayList<>();
            macAddress_list = new ArrayList<>();
            bPic_list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name

                //bName,macAddress各自單獨存成一個array
                bName_list.add(bName);
                macAddress_list.add(macAddress);
                bPic_list.add(bPic);

//                distance.add("out of range");//distance先寫死
            }
//            bluetooth.mac = macAddress_list;
//            bluetooth.getStartMyItemDistance(macAddress_list);
            //上面的資料讀取完  才設置listview
//            adapter=new rowdata(this,bName_list,distance,macAddress_list,bPic_list,false);//顯示的方式
            adapter=new rowdata(getBaseContext(),bName_list,macAddress_list,macAddress_list,bPic_list,false);//顯示的方式
//            adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,true);//顯示的方式
            listView1.setAdapter(adapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //選項按下反應
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemName = bName_list.get(position);      //哪一個列表
                    String itemAddress = macAddress_list.get(position);
                    Toast.makeText(MainActivity.this, itemName + " selected", Toast.LENGTH_SHORT).show(); //顯示訊號
                    bluetooth.bluetoothFunction="searchItem";

//                /*換頁面 有換Activity*/
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Compass.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("itemName", itemName);
                    bundle.putString("itemAddress", itemAddress);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
//                loading = ProgressDialog.show(MainActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showUserEvent();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_USER_EVENT,get_uEmail);
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

            eventName_array = new String[result.length()];
            eventId_array = new int[result.length()];

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                int cId = Integer.parseInt(jo.getString("cId"));//取得event id , 由string轉為cId
                String cName = jo.getString("cName");//取得event名稱

                //Toast.makeText(MainActivity.this, cName, Toast.LENGTH_LONG).show();

                eventId_array[i] = cId;
                eventName_array[i] = cName;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getUserGroup(){
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
                showUserGroup();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_USER_GROUP,get_uEmail);
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showUserGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacno()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            groupName_array = new String[result.length()];
            groupId_array = new int[result.length()];

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                int gId = Integer.parseInt(jo.getString("gId"));//取得event id , 由string轉為cId
                String gName = jo.getString("gName");//取得event名稱

                groupId_array[i] = gId;
                groupName_array[i] = gName;

                //Toast.makeText(MainActivity.this, gName, Toast.LENGTH_LONG).show();


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
        showMenu(v,popup);
    }



    public void showMenu(View v, PopupMenu popup) {

//        PopupMenu popup = new PopupMenu(this, v);
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
//        popup.inflate(R.menu.item_side);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
//                Toast.makeText(MainActivity.this, "Enter another page", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete this Item");
                alert.setMessage("Do you want to delete "+"ItemName"+"?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //Your action here
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();

                return true;
            default:
                return true;
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
    }


    public void refresh() {
        bluetooth.getStartMyItemDistance(macAddress_list);  // 傳送使用者目前擁有的裝置列表，檢查是否在周圍，如果有的話就會顯示距離
//        getBeacon();
//        getUserEvent();
        adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,true);//顯示的方式
        listView1.setAdapter(adapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,false);//顯示的方式
                listView1.setAdapter(adapter);
            }
        }, 3000);
    }

    public void checkItem(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,CheckItem.class);
        intent.putExtra("macAddress",macAddress_list);
        intent.putExtra("bPic_list",bPic_list);
        intent.putExtra("bName_list",bName_list);
        intent.putExtra("bStatus_list", bluetooth.myDeviceDistance);
        startActivity(intent);
//        finish();
    }
}
