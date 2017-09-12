package com.example.emily.beaconside;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Menu;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    int listItemPositionForPopupMenu;


    Context mContext;
    Button side_new,side_group_bt,side_class_bt;
    View side_class_ls,side_group_ls;
    ImageView chooseGroup,chooseClass,userPicture;
    ListView listView1;
    rowdata adapter;
    ArrayAdapter<String> adapterPress;
    ArrayAdapter<String> adapter_sideList_group;
    main_side_event_rowdata adapter_sideList_event1;
    main_side_event_rowdata adapter_sideList_event2;
    ListView group_list;
    //    String[] testValues= new String[]{	"Wallet","Key","Camera","Laptop"};
//    String[] testValues2= new String[]{	"Out of Range","Out of Range","Out of Range","Out of Range"};
//    String[] address = new String[]{"84:EB:18:7A:5B:80","D0:39:72:DE:DC:3A","D0:39:72:DE:DC:3A","84:EB:18:7A:5B:80"};

    TextView userName;


    BluetoothMethod bluetooth = new BluetoothMethod();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //寫死目前的用戶
    public static String uEmail;
    public static String get_uEmail;
    public static String uName;
    public static String uId;
    private String JSON_STRING; //用來接收php檔傳回的json

    public static ArrayList<String> bName_list = new ArrayList<String>();//我的beacon名稱list
    ArrayList<String> macAddress_list = new ArrayList<String>();//我的beacon mac list
    ArrayList<String> bPic_list = new ArrayList<String>();//我的beacon 圖片 list
    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> distance= new ArrayList<String>();
    ArrayList<Integer> bAlert_list = new ArrayList<>();

    int[] eventId_array;//儲存event id
    String[] eventName_array;//儲存event name
    int[] groupId_array;//儲存group id
    String[] groupName_array;//儲存group name
    ArrayList<String> groupName_list;
    ArrayList<String> eventName_list1 = new ArrayList<String>();
    ArrayList<String> eventName_list2 = new ArrayList<String>();

    /* class main side */
    private RelationListView event_list1;
    private RelationListView event_list2;


    /* long press */
    MergeAdapter mergeAdapter;
    /* end lon */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 取得從Login頁面傳來的用戶的FB帳號
        Intent intent = this.getIntent();

        uEmail = Login.uEmail;
        uName = Login.uName;
        uId = Login.uId;
//        uEmail = "jennifer1024@livemail.tw";
//        uName = intent.getStringExtra("uName");
//        uId = "10211681925182086";
//        uName = "Cuties";

//        uId = intent.getStringExtra("uId");
//        if(!intent.getStringExtra("uEmail").equals(""))
//            uEmail = intent.getStringExtra("uEmail");
        get_uEmail = "\""+uEmail+"\"";
//        get_uEmail = "jennifer1024@livemail.tw";
//        Toast.makeText(this, uName, Toast.LENGTH_SHORT).show();
        // 初始化藍牙
//        bluetooth.BTinit(this);
//        bluetooth.getStartSearchDevice();
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

        /* list view function */
        mergeAdapter = new MergeAdapter();/*留這個*/

        adapterPress = new ArrayAdapter<String>(this, R.layout.activity_rowdata, bName_list);/*留這個*/
        mergeAdapter.addAdapter(new ListTitleAdapter(this,adapterPress));/*留這個*/
        mergeAdapter.addAdapter(adapterPress);///*留這個*/


        registerForContextMenu(listView1);

        // 設置側邊欄使用者名稱
        userName = (TextView) findViewById(R.id.name);
        userName.setText("Hi! "+uName);
        userPicture = (ImageView)findViewById(R.id.userPicture) ;
        String url ="https://graph.facebook.com/"+uId+"/picture?type=large";
        new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                Bitmap bmp = toRoundBitmap(result);
                userPicture.setImageBitmap (bmp);
                super.onPostExecute(result);
            }
        }.execute(url);



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
                bluetooth.bluetoothStop();
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
        group_list = (ListView)findViewById(R.id.group_list);
        event_list1 = (RelationListView) findViewById(R.id.event_list1);
        event_list2 = (RelationListView) findViewById(R.id.event_list2);
        event_list1.setRelatedListView(event_list2);
        event_list2.setRelatedListView(event_list1);

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
                        Toast.makeText(MainActivity.this,"new group Clicked ",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,NewGroup.class);
                        bluetooth.bluetoothStop();
                        startActivity(intent);
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
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,NewEvent.class);
                        bluetooth.bluetoothStop();
                        startActivity(intent);
                    }

                });
            }
        });
        getBeacon();
        getUserEvent();
        getUserGroup();
        listView1.setAdapter(mergeAdapter);/*留這個*/
    }

    @Override
    public void onResume(){
        super.onResume();
        uEmail = Login.uEmail;
//        uEmail = "jennifer1024@livemail.tw";
//
        get_uEmail = "\""+uEmail+"\"";
//        uName = Login.uName;
//        Toast.makeText(this, uName, Toast.LENGTH_SHORT).show();
        bluetooth.BTinit(this);
        bluetooth.getStartSearchDevice();
        getBeacon();
        getUserEvent();
        getUserGroup();
        bluetooth.getStartMyItemDistance(macAddress_list);
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
//                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
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
            bAlert_list = new ArrayList<>();

            for(int i = 0; i<result.length(); i++){//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                String macAddress = jo.getString("macAddress");//取得macAddress
                String bName = jo.getString("bName");//取得beacon name
                String bPic = jo.getString("bPic");//取得beacon name
                String bAlert = jo.getString("alertMiles");//取得beacon的alertMile
                String isAlert = jo.getString("isAlert");

                //bName,macAddress各自單獨存成一個array
                bName_list.add(bName);
                macAddress_list.add(macAddress);
                bPic_list.add(bPic);
                if(isAlert.equals("1")) {
                    bAlert_list.add(parseInt(bAlert));
//                    Toast.makeText(MainActivity.this, bName + " alert is" + parseInt(bAlert), Toast.LENGTH_SHORT).show(); //顯示訊號
                }
                else
                    bAlert_list.add(100000);
//                distance.add("out of range");//distance先寫死
            }
            //上面的資料讀取完  才設置listview
            adapter=new rowdata(getBaseContext(),bName_list,macAddress_list,macAddress_list,bPic_list,false);//顯示的方式/*留這個*/
            mergeAdapter.notifyDataSetChanged();/*留這個*/


            bluetooth.Alert = bAlert_list;

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

                int cId = parseInt(jo.getString("cId"));//取得event id , 由string轉為cId
                String cName = jo.getString("cName");//取得event名稱
//                Toast.makeText(this, cName.toString()+" i="+i, Toast.LENGTH_LONG).show();
                eventId_array[i] = cId;
                eventName_array[i] = cName;

                if(i==0) //left
                    eventName_list1.add(cName);
                else
                    if(i%2==0) //left
                        eventName_list1.add(cName);
                    else //right
                        eventName_list2.add(cName);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Toast.makeText(MainActivity.this, "start"+eventName_list1, Toast.LENGTH_LONG).show();
        adapter_sideList_event1 = new main_side_event_rowdata(this,eventName_list1);
        adapter_sideList_event2 = new main_side_event_rowdata(this,eventName_list2);
        event_list1.setAdapter(adapter_sideList_event1);
        event_list2.setAdapter(adapter_sideList_event2);
        event_list1.setRelatedListView(event_list2);
        event_list2.setRelatedListView(event_list1);

    }

    private void getUserGroup(){
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
//                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
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

                int gId = parseInt(jo.getString("gId"));//取得event id , 由string轉為cId
                String gName = jo.getString("gName");//取得event名稱
//                Toast.makeText(this, "hello"+gName.toString(), Toast.LENGTH_LONG).show();
                groupId_array[i] = gId;
                groupName_array[i] = gName;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        groupName_list= new ArrayList<>(Arrays.asList(groupName_array));//array to arraylist
//        Toast.makeText(MainActivity.this, "start"+groupName_list+"end", Toast.LENGTH_LONG).show();
        adapter_sideList_group = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,groupName_list);
        group_list.setAdapter(adapter_sideList_group);
    }

    private void deleteBeacon(final String macAddress){
        class DeleteEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
//                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                getBeacon();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_DELETE_BEACON, macAddress);
                return s;
            }
        }

        DeleteEmployee de = new DeleteEmployee();
        de.execute();
    }

    /* Item setting */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);//!!!
        if (v.getId()==R.id.listView1) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(bName_list.get(info.position-1));
            /*長按著的選項*/
            String[] menuItems = new String[]{"Edit","Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems   = new String[]{"Edit","Delete"};
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = bName_list.get(info.position-1);
        final String listItemMac = macAddress_list.get(info.position-1);
//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch (menuItemName){
            case "Edit":
                //進入編輯頁面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,editBeacon.class);
                intent.putExtra("macAddress",listItemMac);
                intent.putExtra("bName",listItemName);
                intent.putExtra("uEmail",uEmail);
                intent.putExtra("eventId_array",eventId_array);
                intent.putExtra("eventName_array",eventName_array);
                intent.putExtra("groupName_array",groupName_array);
                intent.putExtra("groupId_array",groupId_array);
                startActivity(intent);
                finish();
                break;
            case "Delete":
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete this Item");
                alert.setMessage("Do you want to delete "+listItemName+" ?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteBeacon(listItemMac);
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();

                break;
        }
//        Toast.makeText(MainActivity.this, String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_LONG).show();
        return true;
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
        getUserEvent();
        getUserGroup();
        adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,true);//顯示的方式/*留這個*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                adapter=new rowdata(getBaseContext(),bName_list,bluetooth.myDeviceDistance,macAddress_list,bPic_list,false);//顯示的方式/*留這個*/
            }
        }, 3000);

        mergeAdapter.addAdapter(new ListTitleAdapter(this,adapter));/*留這個*/
        mergeAdapter.addAdapter(adapter);/*留這個*/
    }

    public void checkItem(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,CheckItem.class);
        intent.putExtra("macAddress",macAddress_list);
        intent.putExtra("bPic_list",bPic_list);
        intent.putExtra("bName_list",bName_list);
        intent.putExtra("bStatus_list", bluetooth.myDeviceDistance);
        bluetooth.bluetoothStop();
        startActivity(intent);
//        finish();
    }

    // 從URL下載圖片
    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }
}

