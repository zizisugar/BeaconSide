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
        import android.os.Bundle;
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

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.List;

        import static android.R.id.list;

public class addNewBeacon extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener ,OnDateSetListener, TimePickerDialog.OnTimeSetListener{


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
    /* time end */

    Button buttonTimeto,buttonDateto;
    Button buttonTimefrom,buttonDatefrom;
    Button add_notification_check;
    Button notification_content_button;
    EditText notification_content;
    boolean edit=false;
    int dateFlag = 0;//0:not setting, 1:start/from, 2:end/to
    int timeFlag = 0;//0:not setting, 1:start/from, 2:end/to
    int dateFromYear, dateFromMonth, dateFromDay;
    int dateToYear, dateToMonth, dateToDay;
    int timeFromHour, timeFromMin;
    int timeToHour, timeToMin;

    private ListView listView_eventScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_beacon);

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
        notification_content_button = (Button) findViewById(R.id.notification_content_button);
        notification_content_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*進入編輯狀態，所以要傳入原本的值*/
                edit=true;
                notification_click_claim();


            }
        });
        notification_content_button.setOnLongClickListener(new View.OnLongClickListener() {//長按就刪除
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder alert = new AlertDialog.Builder(addNewBeacon.this);
                alert.setTitle("Delete this notification");
                alert.setMessage("Do you want to delete "+"ItemName"+"?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(addNewBeacon.this, "SONG LA", Toast.LENGTH_LONG).show();
                        notification_content_button.setVisibility(View.GONE);
                        //Your action here
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(addNewBeacon.this, "OK FINE", Toast.LENGTH_LONG).show();
                    }
                });

                alert.show();
                return true;
            }
        });


//        listView_eventScroll = (ListView) findViewById(R.id.listview_eventScroll);
//        buttonSubmit= (Button) findViewById(R.id.buttonSubmit);
        //設定button onclick的動作
//        buttonSubmit.setOnClickListener(this);

        //switch開或關的動作  在function onCheckedChanged
        if (alarmSwitch != null) {
            alarmSwitch.setOnCheckedChangeListener(this);
        }
        //預設alarmSwitch是關,editTextMile不能編輯
        editTextMile.setTag(editTextMile.getKeyListener());
        editTextMile.setKeyListener(null);


        editTextbName.setText(bName);
        textViewMac.setText(macAddress);


        String uri = "@drawable/" + bPic; //圖片路徑和名稱
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
        device_pic.setImageResource(imageResource);


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
                new AlertDialog.Builder(addNewBeacon.this)
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
                                        = new LinearLayoutManager(addNewBeacon.this, LinearLayoutManager.HORIZONTAL, false);
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
                new AlertDialog.Builder(addNewBeacon.this)
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

                                horizontalList_group=new ArrayList<>();

                                for (int i = 0; i < group_select.length; i++) {
                                    if (group_select[i]) { //如果選擇的是true(被勾選)
                                        groupIdSelect.append(Integer.toString(groupId_array[i])).append(",");
                                        //連接stringbuffer eventIdSelect(這是一段傳給Php的stringbuffer)                                    }
                                        horizontalList_group.add(groupName_array[i]);

                                    }
                                }

                                horizontalAdapter_group=new HorizontalAdapter(horizontalList_group);
                                LinearLayoutManager horizontalLayoutManagaer
                                        = new LinearLayoutManager(addNewBeacon.this, LinearLayoutManager.HORIZONTAL, false);
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
        add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification_click_claim();
            }
        });


    }

    /* time */
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        boolean y = false;
        switch (dateFlag){
            case 0://nothing happen
                break;
            case 1://click start/from button
                dateFromYear = year; dateFromMonth = month+1; dateFromDay = day;
                dateToYear = year; dateToMonth = month+1; dateToDay = day+1;/*還有一個防呆機制沒有做，就是+1的時候可能換月*/
                buttonDatefrom.setText(dateFromYear + "-" + dateFromMonth + "-" + dateFromDay);//I don't know why month will less one so I add it
                buttonDateto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay);
                break;
            case 2://click end/to button
                if(year >= dateFromYear)
                    if((month+1) >= dateFromMonth)
                        if(day>=dateFromDay)
                            y=true;
                if(y){//avoid stupid
                    dateToYear = year; dateToMonth = month+1; dateToDay = day;
                    buttonDateto.setText(dateToYear + "-" + dateToMonth + "-" + dateToDay);
                }
                break;
        }
        /* 透過這些值放入DB */
        //dateFromYear + "-" + dateFromMonth + "-" + dateFromDay
        //dateToYear + "-" + dateToMonth + "-" + dateToDay

//        Toast.makeText(addNewBeacon.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        boolean y = false;
        switch (timeFlag){
            case 0://nothing happen
                break;
            case 1://click start/from button
                timeFromHour = hourOfDay; timeFromMin = minute;
                timeToHour = hourOfDay+1; timeToMin = minute;/*還有一個防呆機制沒有做，就是+1的時候可能換日*/
                buttonTimefrom.setText(timeFromHour + ":" + timeFromMin);
                buttonTimeto.setText(timeToHour + ":" + timeToMin);
                break;
            case 2://click end/to button
                if(hourOfDay >= timeFromHour)
                    if(minute>=timeFromMin)
                        y=true;
                if(y){
                    timeToHour = hourOfDay; timeToMin = minute;
                    buttonTimeto.setText(timeToHour + ":" + timeToMin);
                }
                break;
        }
        /* 透過這些值放入DB */
        //timeFromHour + ":" + timeFromMin
        //timeToHour + ":" + timeToMin

//        Toast.makeText(addNewBeacon.this, "new time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
    }
    /* time end */


    /*notification click*/
    public void notification_click_claim(){
        final Dialog dialog = new Dialog(addNewBeacon.this);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(addNewBeacon.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(addNewBeacon.this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
        // first dialog
        dialog.setContentView(R.layout.notification_dialog);
        buttonDateto = (Button) dialog.findViewById(R.id.buttonDateto);
        buttonTimeto = (Button) dialog.findViewById(R.id.buttonTimeto);
        buttonDatefrom = (Button) dialog.findViewById(R.id.buttonDatefrom);
        buttonTimefrom = (Button) dialog.findViewById(R.id.buttonTimefrom);
        add_notification_check = (Button) dialog.findViewById(R.id.add_notification_check);
        notification_content = (EditText) dialog.findViewById(R.id.notification_content);

        //second dialog
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
        if (dpd != null) {
            dpd.setOnDateSetListener(addNewBeacon.this);
        }
        TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
        if (tpd != null) {
            tpd.setOnTimeSetListener(addNewBeacon.this);
        }


//                buttonDateto.setClickable(false);
//                buttonDateto.setClickable(false);
        if(edit){
            //抓資料庫裡的data去設定這些的值
            //timeFromHour + ":" + timeFromMin
            //timeToHour + ":" + timeToMin
            //dateFromYear + "-" + dateFromMonth + "-" + dateFromDay
            //dateToYear + "-" + dateToMonth + "-" + dateToDay
            //notification_content
            add_notification_check.setText("Check");//原本是"Add notifiaction"
            edit = false;//此次編輯結束
        }


        buttonDatefrom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                dateFlag = 1;
//                        buttonDateto.setClickable(true);
            }
        });
        buttonDateto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                dateFlag = 2;

            }
        });
        buttonTimefrom.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
//                        buttonTimeto.setClickable(true);
                timeFlag = 1;
            }
        });
        buttonTimeto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                timeFlag = 2;
            }
        });




        //set first dialog

        add_notification_check.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                String content = notification_content.getText().toString();
                if(dateFlag!=0 && timeFlag!=0 && content!=null){
                    notification_content_button.setText(content+"\n\n"+
                            dateFromYear+"/"+dateFromMonth+"/"+dateFromDay+" ~ "+dateToYear+"/"+dateToMonth+"/"+dateToDay+"\n"+
                            timeFromHour+":"+timeFromMin+" ~ "+timeToHour+":"+timeToMin);

                }
//                        Toast.makeText(addNewBeacon.this, "Add Notification successfully", Toast.LENGTH_LONG).show();
//                Toast.makeText(addNewBeacon.this, "this:"+notification_content.getText().toString(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });



        dialog.show();

    }


    //新增程序
    private void addBeacon(){
        //Toast.makeText(addNewBeacon.this,"近來add",Toast.LENGTH_LONG).show();


        final String bName = editTextbName.getText().toString().trim();
        final String macAddress = textViewMac.getText().toString().trim();
        final String bContent= editTextbContent.getText().toString().trim();
        final String alertMiles = editTextMile.getText().toString().trim();

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

                /*Intent intent = new Intent();
                intent.setClass(addNewBeacon.this,MainActivity.class);
                //傳遞array: event list
                //intent.putStringArrayListExtra("cName_list",cName_list);
                startActivity(intent);*/
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> params = new HashMap<>();
                params.put("uEmail",uEmail);
                params.put("bName",bName);
                params.put("macAddress",macAddress);
                params.put("bContent",bContent);
                params.put("alertMiles",alertMiles);
                if (switchMode) {
                    //如果switch開
                    params.put("isAlert","1");
                }else{
                    //如果switch關
                    params.put("isAlert","0");
                }
                params.put("bPic",bPic);
                params.put("eventIdSelect",eventIdSelect.toString());
                params.put("groupIdSelect",groupIdSelect.toString());


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_BEACON, params);
                return res;
            }
        }

        AddBeacon ae = new AddBeacon();
        ae.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonChangePic){

            Intent intent = new Intent();
            intent.setClass(addNewBeacon.this, ChangePic.class);
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
            //執行新增beacon
            addBeacon();
            /* 切回到原本的畫面 */
            Intent intent = new Intent();
            intent.setClass(addNewBeacon.this, MainActivity.class);
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
            switchMode = true;
        } else {
            //do stuff when Switch if OFF
            //設定讓editTextMile不能編輯
            editTextMile.setTag(editTextMile.getKeyListener());
            editTextMile.setKeyListener(null);
            switchMode = false;
            Toast.makeText(this,"若要再次編輯警報距離，請開啟警報模式",
                    Toast.LENGTH_SHORT).show();
        }
    }


//    /* cancel : go back button */
//    @Override
//    public boolean onSupportNavigateUp(){
//        finish();
//        return true;
//    }
//
//    /* check button*/
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_item_save, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_new_item_check) {
//
//            addBeacon();
//
//            Intent intent = new Intent();
//            intent.setClass(addNewBeacon.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            /* 切回到原本的畫面 */
//            startActivity(new Intent(addNewBeacon.this, MainActivity.class));//same as following two
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    /* check end */



}
