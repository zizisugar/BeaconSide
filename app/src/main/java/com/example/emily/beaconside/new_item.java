package com.example.emily.beaconside;


import android.app.Dialog;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.ArrayList;


public class new_item extends AppCompatActivity implements OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /* time */
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    public int noti_year,noti_month,noti_day;
    public int noti_hourOfDay,noti_minute;
    /* time end */





    ImageButton plus_event;
    ImageButton plus_group;
    ImageButton plus_notification;

    TextView datetext;
    TextView timetext;
    View inflatedView;
    ListView dialog_list;
    Button add_check;
    Button add_notification_check;
    Button buttonTime,buttonDate;
    item_plus_content_rowdata adapter;
    ArrayList<String> text_listName;

    /* 設定ist Name 由此 */
    String[] value_event= new String[]{	"School","Dating","Daily"};
    String[] value_group= new String[]{	"Friend","Family","Coworker"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        setContentView(R.layout.activity_add_new_beacon);



        plus_event = (ImageButton)findViewById(R.id.plus_event);
        plus_group = (ImageButton)findViewById(R.id.add_group);
        plus_notification = (ImageButton)findViewById(R.id.add_notification);

        /* 新增Event */
        plus_event.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(new_item.this);
                // get inflater and inflate layour for dialogue
                inflatedView = new_item.this.getLayoutInflater().inflate(R.layout.item_plus_dialog, null);
                // now set layout to dialog
                dialog.setContentView(inflatedView);

                adapter=new item_plus_content_rowdata(new_item.this,value_event);//顯示的方式
                text_listName = adapter.getSelectedString();//text of list
                dialog_list=(ListView) inflatedView.findViewById(R.id.dialog_list);
                add_check = (Button) inflatedView.findViewById(R.id.add_check);
                add_check.setText("Add Event");
                dialog_list.setAdapter(adapter);
                dialog.setTitle("Add new Event");
                add_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {/*把text_listName存進資料庫*/
                        Toast.makeText(new_item.this, "Add Event "+text_listName+" successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        /* 新增Group */
        plus_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(new_item.this);
                // get inflater and inflate layour for dialogue
                inflatedView = new_item.this.getLayoutInflater().inflate(R.layout.item_plus_dialog, null);
                // now set layout to dialog
                dialog.setContentView(inflatedView);

                adapter=new item_plus_content_rowdata(new_item.this,value_group);//顯示的方式
                text_listName = adapter.getSelectedString();//text of list
                dialog_list=(ListView) inflatedView.findViewById(R.id.dialog_list);
                add_check = (Button) inflatedView.findViewById(R.id.add_check);
                add_check.setText("Add Group");
                dialog_list.setAdapter(adapter);
                dialog.setTitle("Add new Group");
                add_check.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {/*把text_listName存進資料庫*/
                        Toast.makeText(new_item.this, "Add Group "+text_listName+" successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /* 新增Notification */
        plus_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(new_item.this);
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new_item.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),false);
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new_item.this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
                // first dialog
                dialog.setContentView(R.layout.notification_dialog);


                //second dialog
                DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
                if (dpd != null) {
                    dpd.setOnDateSetListener(new_item.this);
                }
                TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
                if (tpd != null) {
                    tpd.setOnTimeSetListener(new_item.this);
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
                        Toast.makeText(new_item.this, "Add Notification successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        /* cancel : go back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
            /* 切回到原本的畫面 */
            startActivity(new Intent(new_item.this, MainActivity.class));//same as following two
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* check end */


}

