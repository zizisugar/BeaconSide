package com.example.emily.beaconside;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

public class addNewBeacon extends AppCompatActivity implements View.OnClickListener {


    EditText editTextbName;
    TextView textViewMac;
    EditText editTextbContent;
    Button buttonSubmit;
    ImageButton add_event;
    ImageButton add_group;
    ImageButton add_notification;
    String JSON_STRING;


    ArrayList<String> cName_list = new ArrayList<String>();//我的event名稱list

    ListView event_dialog_listview;//增加event的視窗內的listview
    List<Boolean> listShow;    // 這個用來記錄哪幾個 item 是被打勾的

    String uEmail = MainActivity.uEmail;
    String get_uEmail = MainActivity.get_uEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_beacon);


        //接收從SearchDevice傳過來的變數
        Intent intent = this.getIntent();
        String bName = intent.getStringExtra("bName");
        String macAddress = intent.getStringExtra("macAddress");

        getUserEvent();

        textViewMac = (TextView) findViewById(R.id.textViewMac);
        editTextbName = (EditText) findViewById(R.id.editTextbName);
        editTextbContent = (EditText) findViewById(R.id.editTextbContent);

        event_dialog_listview = (ListView) findViewById(R.id.event_dialog_list);
        //setEventList();

        add_event = (ImageButton)findViewById(R.id.add_event);
        add_group = (ImageButton)findViewById(R.id.add_group);
        add_notification = (ImageButton)findViewById(R.id.add_notification);
        buttonSubmit= (Button) findViewById(R.id.buttonSubmit);

        //設定button onclick的動作
        buttonSubmit.setOnClickListener(this);

        editTextbName.setText(bName);
        textViewMac.setText(macAddress);


        /* 新增Event */
        add_event.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(addNewBeacon.this);
                dialog.setContentView(R.layout.event_dialog);
//                dialog.setTitle("Title...");//沒有作用
                dialog.show();

               // getUserEvent();


            }
        });


        /* 新增Group */
        add_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(addNewBeacon.this);
                dialog.setContentView(R.layout.group_dialog);
                dialog.show();
            }
        });

        /* 新增Notification */
        //還在處理選擇時間的介面問題
        /*add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddNewActivity.this);
                dialog.setContentView(R.layout.notification_dialog);
                dialog.show();
            }
        });*/


        /* cancel : go back button */
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    //新增程序
    private void addBeacon(){
        //Toast.makeText(addNewBeacon.this,"近來add",Toast.LENGTH_LONG).show();


        final String bName = editTextbName.getText().toString().trim();
        final String macAddress = textViewMac.getText().toString().trim();
        final String bContent= editTextbContent.getText().toString().trim();

        class AddBeacon extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(addNewBeacon.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(addNewBeacon.this,s,Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.setClass(addNewBeacon.this,MainActivity.class);
                //傳遞array: event list
                //intent.putStringArrayListExtra("cName_list",cName_list);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("uEmail",uEmail);
                params.put("bName",bName);
                params.put("macAddress",macAddress);
                params.put("bContent",bContent);
                params.put("bPic","camera");//圖片先寫死為camera

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_BEACON, params);
                return res;
            }
        }

        AddBeacon ae = new AddBeacon();
        ae.execute();
    }
    private void getUserEvent(){
        class GetUserEvent extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(addNewBeacon.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showUserEvent();

            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_USER_EVENT,get_uEmail);
                return s;
            }
        }
        GetUserEvent ge = new GetUserEvent();
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
                //Toast.makeText(addNewBeacon.this, cName, Toast.LENGTH_LONG).show();
                //event存成一條array
                cName_list.add(cName);
                //setEventList();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setEventList(){
        event_dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                                            {
                                                CheckedTextView chkItem = (CheckedTextView) v.findViewById(R.id.check1);
                                                chkItem.setChecked(!chkItem.isChecked());
                                                Toast.makeText(addNewBeacon.this, "您點選了第 "+(position+1)+" 項", Toast.LENGTH_SHORT).show();
                                                listShow.set(position, chkItem.isChecked());
                                            }
                                        }
        );

        listShow = new ArrayList<Boolean>();
        ArrayList<String> list = new ArrayList<String>();
        for(int x=0;x<cName_list.size();x++)
        {
            list.add(cName_list.get(x));
            listShow.add(true);
        }
        ListAdapter adapterItem = new ListAdapter(this, list);
        event_dialog_listview.setAdapter(adapterItem);
    }
    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            //按下新增按鈕後
            addBeacon();
        }
    }




}
