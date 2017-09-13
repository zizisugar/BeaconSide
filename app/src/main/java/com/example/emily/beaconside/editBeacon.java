package com.example.emily.beaconside;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

public class editBeacon extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener ,OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    EditText editTextbName;
    TextView textViewMac;
    EditText editTextbContent;
    EditText editTextMile;
    Button buttonSubmit;
    ImageButton add_event;
    ImageButton add_group;
    ImageButton add_notification;
    ImageButton buttonChangePic;
    String JSON_STRING;
    String bName;
    String macAddress;
    ImageView device_pic;
    String bPic = "wallet"; //一開始圖片預設wallet
    String title;//dialog的title

    public static final int resultNum = 0;

    private String repeat = "";

    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list
    ArrayList<String> select = new ArrayList<>(Arrays.asList("false","false"));

    String[] eventName_array;
    int[] eventId_array;
    String[] groupName_array;
    int[] groupId_array;
    private boolean[]  event_select;//紀錄哪些event被選
    private boolean[]  group_select;//紀錄哪些group被選
    StringBuffer eventIdSelect = new StringBuffer();
    StringBuffer groupIdSelect = new StringBuffer();
    int[] eventId_beacon;//beacon編輯前有的event id
    int[] groupId_beacon;//beacon編輯前有的group id
    private boolean[] event_beaconSelect;//紀錄最初哪些event被選(beacon編輯前的event)
    private boolean[] group_beaconSelect;//紀錄最初哪些event被選(beacon編輯前event)
    StringBuffer eventId_delete = new StringBuffer();
    StringBuffer eventId_add = new StringBuffer();
    StringBuffer groupId_delete = new StringBuffer();
    StringBuffer groupId_add = new StringBuffer();


    private RecyclerView horizontal_recycler_view_event,horizontal_recycler_view_group;
    private ArrayList<String> horizontalList_event,horizontalList_group;
    private HorizontalAdapter horizontalAdapter_event,horizontalAdapter_group;


    boolean switchMode = false;//紀錄switch是開或關
    Switch alarmSwitch;//switch按鈕
    item_plus_content_rowdata adapter;
    View inflatedView;
    ListView dialog_list;
    Button add_check;

    ArrayList<String> text_listName;

    //ListView event_dialog_listview;//增加event的視窗內的listview
    List<Boolean> listShow;    // 這個用來記錄哪幾個 item 是被打勾的

    String uEmail = "sandy@gmail.com";
    String get_uEmail = "\"sandy@gmail.com\"";

    /* time */
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    public int noti_year,noti_month,noti_day;
    public int noti_hourOfDay,noti_minute;
    /* time end */

    Button buttonTime,buttonDate;
    Button buttonAddnotice;
    TextView datetext;
    TextView timetext;
    Button add_notification_check;

    private ListView listView_eventScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_beacon);

        //畫面上方的bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**接收從SearchDevice傳過來的變數**/
        Intent intent = this.getIntent();
        Bundle extra = intent.getExtras();
        uEmail = intent.getStringExtra("uEmail");
        bName = intent.getStringExtra("bName");
        macAddress = intent.getStringExtra("macAddress");
        eventName_array = extra.getStringArray("eventName_array");
        eventId_array = extra.getIntArray("eventId_array");
        groupName_array = extra.getStringArray("groupName_array");
        groupId_array = extra.getIntArray("groupId_array");
        /***********/
        get_uEmail = "\""+uEmail+"\"";
        event_select = new boolean[eventName_array.length];//讓event_select和event_array一樣長度
        group_select = new boolean[groupName_array.length];//讓group_select和group_array一樣長度
        event_beaconSelect = new boolean[eventName_array.length];
        group_beaconSelect = new boolean[groupName_array.length];
        //boolean array預設為全填滿false


        textViewMac = (TextView) findViewById(R.id.textViewMac);
        editTextbName = (EditText) findViewById(R.id.editTextbName);
        editTextbContent = (EditText) findViewById(R.id.editTextbContent);
        alarmSwitch = (Switch) findViewById(R.id.switchAlarm);
        editTextMile = (EditText) findViewById(R.id.editTextMile);
        device_pic = (ImageView) findViewById(R.id.device_pic);
        horizontal_recycler_view_event= (RecyclerView) findViewById(R.id.horizontal_recycler_view_event);//event左右滑動的內容
        horizontal_recycler_view_group= (RecyclerView) findViewById(R.id.horizontal_recycler_view_group);//group左右滑動的內容
        editTextMile.setText("0", TextView.BufferType.EDITABLE);
        buttonChangePic = (ImageButton) findViewById(R.id.buttonChangePic);
        buttonChangePic.setOnClickListener(this);


        add_event = (ImageButton)findViewById(R.id.add_event);
        add_group = (ImageButton)findViewById(R.id.add_group);
        add_notification = (ImageButton)findViewById(R.id.add_notification);

        editTextbName.setText(bName);
        textViewMac.setText(macAddress);

        String uri = "@drawable/" + bPic; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        device_pic.setImageResource(imageResource);

        //初始化event灰色標籤列表
        horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
        LinearLayoutManager horizontalLayoutManagaer_event
                = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer_event);

        //初始化group灰色標籤列表
        horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
        LinearLayoutManager horizontalLayoutManagaer_group
                = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer_group);


        getBeacon();
        getBeaconEvent();
        getBeaconGroup();

        //指定alarmSwitch的click listener  要放在getBeacon後面
        if (alarmSwitch != null) {
            alarmSwitch.setOnCheckedChangeListener(this);
        }



        /* 新增Event */
        add_event.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                repeat = "";
                if(eventName_array.length == 0){
                    title = "您還沒有創建任何事件";
                }else{
                    title = "選擇事件";
                }
                new AlertDialog.Builder(editBeacon.this)
                        .setTitle(title)
                        .setMultiChoiceItems(
                                eventName_array,
                                event_select,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // TODO Auto-generated method stub

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                horizontalList_event=new ArrayList<>();

                                for (int i = 0; i < event_select.length; i++) {
                                    if (event_select[i]) { //如果選擇的是true(被勾選)
                                        eventIdSelect.append(Integer.toString(eventId_array[i])).append(",");
                                        //連接stringbuffer eventIdSelect(這是一段傳給Php的stringbuffer)

                                        horizontalList_event.add(eventName_array[i]);

                                    }
                                }
                                horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
                                horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer);
                                horizontal_recycler_view_event.setAdapter(horizontalAdapter_event);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();

            }
        });
        /*子庭版本 還在修
       add_event.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(addNewBeacon.this);
                // get inflater and inflate layour for dialogue
                inflatedView = addNewBeacon.this.getLayoutInflater().inflate(R.layout.item_plus_dialog, null);
                // now set layout to dialog
                dialog.setContentView(inflatedView);

                adapter=new item_plus_content_rowdata(addNewBeacon.this,event_array);//顯示的方式
                text_listName = adapter.getSelectedString();//text of list
                dialog_list=(ListView) inflatedView.findViewById(R.id.dialog_list);
                add_check = (Button) inflatedView.findViewById(R.id.add_check);
                add_check.setText("Add Event");
                dialog_list.setAdapter(adapter);
                dialog.setTitle("Add new Event");
                add_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {//把text_listName存進資料庫
                        Toast.makeText(addNewBeacon.this, "Add Event "+text_listName+" successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        */


        /* 新增Group */
        add_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                repeat = "";
                if(groupName_array.length == 0){//如果沒有group
                    title = "您還沒有加入任何群組";
                }else{
                    title = "選擇群組";
                }
                new AlertDialog.Builder(editBeacon.this)
                        .setTitle(title)
                        .setMultiChoiceItems(
                                groupName_array,
                                group_select,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        // TODO Auto-generated method stub

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                horizontalList_group=new ArrayList<>();//初始化

                                for (int i = 0; i < group_select.length; i++) {
                                    if (group_select[i]) { //如果選擇的是true(被勾選)
                                        groupIdSelect.append(Integer.toString(groupId_array[i])).append(",");
                                        //連接stringbuffer eventIdSelect(這是一段傳給Php的stringbuffer)                                    }
                                        horizontalList_group.add(groupName_array[i]);

                                    }
                                }

                                horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
                                horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer);
                                horizontal_recycler_view_group.setAdapter(horizontalAdapter_group);


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).show();


            }
        });

        /* 新增Notification */
        /*add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(editBeacon.this);
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),false);
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(editBeacon.this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
                // first dialog
                dialog.setContentView(R.layout.notification_dialog);


                //second dialog
                DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
                if (dpd != null) {
                    dpd.setOnDateSetListener(editBeacon.this);
                }
                TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
                if (tpd != null) {
                    tpd.setOnTimeSetListener(editBeacon.this);
                }
                buttonDate = (Button) dialog.findViewById(R.id.buttonDate);
                buttonTime = (Button) dialog.findViewById(R.id.buttonTime);
                buttonDate.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.setYearRange(1985, 2028);
                        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                    }
                });
                buttonTime.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                    }
                });



                //set first dialog
                datetext = (TextView) dialog.findViewById(R.id.datetext);
                timetext = (TextView) dialog.findViewById(R.id.timetext);
                add_notification_check = (Button) dialog.findViewById(R.id.add_notification_check);
                add_notification_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(editBeacon.this, "Add Notification successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });*/


    }

    /* time */
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        /* 透過這些值放入DB */
        noti_year = year;
        noti_month = month;
        noti_day = day;
        datetext.setText(year + "-" + month + "-" + day);
//        Toast.makeText(new_item.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        /* 透過這些值放入DB */
        noti_hourOfDay = hourOfDay;
        noti_minute = minute;
        timetext.setText(hourOfDay + ":" + minute);
//        Toast.makeText(new_item.this, "new time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
    }
    /* time end */

    private void getBeacon(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeacon();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeacon() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);

                String bContent = jo.getString("bContent");
                String alertMiles = jo.getString("alertMiles");
                String isAlert = jo.getString("isAlert");
                String bPic = jo.getString("bPic");

                editTextbContent.setText(bContent);
                editTextMile.setText(alertMiles);
                editTextMile.setTag(editTextMile.getKeyListener());
                //設定switch
                if(isAlert.equals("1")){
                    alarmSwitch.setChecked(true);

                }else{
                    alarmSwitch.setChecked(false);
                }

                //設定圖片
                String uri = "@drawable/" + bPic; //圖片路徑和名稱
                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
                device_pic.setImageResource(imageResource);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBeaconEvent(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeaconEvent();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_EVENT,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeaconEvent() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            eventId_beacon = new int[result.length()];
            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                int cId = jo.getInt("cId");
                eventId_beacon[i] = cId;
            }

            horizontalList_event=new ArrayList<>();

            //如果user擁有的(全部)event ID 和 beacon 的 ID 相同  這個event就是被select(true)
            for(int x = 0 ; x < eventId_array.length ; x++){
                for(int y = 0 ; y < eventId_beacon.length ; y++){
                    if(eventId_array[x] == eventId_beacon[y]){
                        event_select[x] = true;//紀錄當前beacon event誰被選了
                        horizontalList_event.add(eventName_array[x]);
                        event_beaconSelect[x] = true;//紀錄beacon編輯前  event誰被選了
                    }
                }
            }

            horizontalAdapter_event=new HorizontalAdapter(horizontalList_event);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view_event.setLayoutManager(horizontalLayoutManagaer);

            horizontal_recycler_view_event.setAdapter(horizontalAdapter_event);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBeaconGroup(){
        class GetBeacon extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                //Toast.makeText(editBeacon.this,s,Toast.LENGTH_LONG).show();
                //將取得的json轉換為array list, 顯示在畫面上
                showBeaconGroup();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_BEACON_GROUP,"\""+macAddress+"\"");
                return s;
            }
        }
        GetBeacon ge = new GetBeacon();
        ge.execute();
    }

    private void showBeaconGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);//放入JSON_STRING 即在getBeacon()中得到的json
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);//轉換為array

            groupId_beacon = new int[result.length()];
            for (int i = 0; i < result.length(); i++) {//從頭到尾跑一次array
                JSONObject jo = result.getJSONObject(i);
                int gId = jo.getInt("gId");
                groupId_beacon[i] = gId;
            }

            horizontalList_group=new ArrayList<>();

            //如果user擁有的(全部)event ID 和 beacon 的 ID 相同  這個event就是被select(true)
            for(int x = 0 ; x < groupId_array.length ; x++){
                for(int y = 0 ; y < groupId_beacon.length ; y++){
                    if(groupId_array[x] == groupId_beacon[y]){
                        group_select[x] = true;
                        horizontalList_group.add(groupName_array[x]);
                        group_beaconSelect[x] = true;
                    }
                }
            }

            horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(editBeacon.this, LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view_group.setLayoutManager(horizontalLayoutManagaer);
            horizontal_recycler_view_group.setAdapter(horizontalAdapter_group);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateBeacon(){

        final String bName = editTextbName.getText().toString().trim();
        final String bContent= editTextbContent.getText().toString().trim();
        final String alertMiles = editTextMile.getText().toString().trim();

        for(int i = 0 ; i < eventId_array.length ; i++){
            if(event_beaconSelect[i] && !event_select[i]){//原本為true(勾選) 更新後為false(不勾選)
                //要被刪除的紀錄
                eventId_delete.append(Integer.toString(eventId_array[i])).append(",");
            }else if(!event_beaconSelect[i] && event_select[i]){//原本為false(不勾選) 更新後為true(勾選)
                //要被增加的紀錄
                eventId_add.append(Integer.toString(eventId_array[i])).append(",");
            }else{
                //編輯前後狀態都一樣 就不用動作
            }
        }

        for(int i = 0 ; i < groupId_array.length ; i++){
            if(group_beaconSelect[i] && !group_select[i]){//原本為true(勾選) 更新後為false(不勾選)
                //要被刪除的紀錄
                groupId_delete.append(Integer.toString(groupId_array[i])).append(",");
            }else if(!group_beaconSelect[i] && group_select[i]){//原本為false(不勾選) 更新後為true(勾選)
                //要被增加的紀錄
                groupId_add.append(Integer.toString(groupId_array[i])).append(",");
            }else{
                //編輯前後狀態都一樣 就不用動作
            }
        }


        class UpdateBeacon extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(editBeacon.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(editBeacon.this,s, Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("macAddress",macAddress);
                hashMap.put("bName",bName);
                hashMap.put("bContent",bContent);
                hashMap.put("alertMiles",alertMiles);

                if (switchMode) {
                    //如果switch開
                    hashMap.put("isAlert","1");
                }else{
                    //如果switch關
                    hashMap.put("isAlert","0");
                }
                hashMap.put("bPic",bPic);
                hashMap.put("eventId_delete",eventId_delete.toString());
                hashMap.put("eventId_add",eventId_add.toString());
                hashMap.put("groupId_delete",groupId_delete.toString());
                hashMap.put("groupId_add",groupId_add.toString());


                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Config.URL_UPDATE_BEACON,hashMap);

                return s;
            }
        }

        UpdateBeacon ue = new UpdateBeacon();
        ue.execute();
    }



    @Override
    public void onClick(View v) {
        if(v == buttonChangePic){

            Intent intent = new Intent();
            intent.setClass(editBeacon.this, ChangePic.class);
            startActivityForResult(intent, resultNum);
        }
    }

    /* cancel : go back button */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /* check button*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_item_save, menu);
        //Toast.makeText(this,"叫出menu", Toast.LENGTH_SHORT).show();

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_item_check) {

            updateBeacon();
            //執行更新beacon
            /* 切回到原本的畫面 */
            Intent intent = new Intent();
            intent.setClass(editBeacon.this, MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                bPic = data.getExtras().getString(ChangePic.FLAG);//從changPic得到的值(圖片名稱)

                String uri = "@drawable/" + bPic; //圖片路徑和名稱

                int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

                device_pic.setImageResource(imageResource);

            }
        }
    }

    //switchAlarm 開或關的動作
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Toast.makeText(this, "The Switch is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
        if(isChecked) {
            //do stuff when Switch is ON
            //設定讓editTextMile可以編輯
            editTextMile.setKeyListener((KeyListener) editTextMile.getTag());
            editTextMile.setTextColor(0xff000000);//設定editTextMile為黑色
            switchMode = true;
        } else {
            //do stuff when Switch if OFF
            //設定讓editTextMile不能編輯
            editTextMile.setTag(editTextMile.getKeyListener());
            editTextMile.setKeyListener(null);
            editTextMile.setTextColor(0xff808080);//設定editTextMile為灰色
            switchMode = false;
            Toast.makeText(this,"若要再次編輯警報距離，請開啟警報模式",
                    Toast.LENGTH_SHORT).show();

        }
    }




}
