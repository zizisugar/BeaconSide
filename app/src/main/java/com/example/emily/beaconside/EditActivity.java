package com.example.emily.beaconside;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


/*頁面  編輯我的物品*/
public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextbName;
    private TextView textViewMac;
    private EditText editTextbContent;
    private Button buttonSubmit;
    ImageButton add_event;
    ImageButton add_group;
    ImageButton add_notification;

    private String uEmail = "sandy@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        textViewMac = (TextView) findViewById(R.id.textViewMac);
        editTextbName = (EditText) findViewById(R.id.editTextbName);
        editTextbContent = (EditText) findViewById(R.id.editTextbContent);

        add_event = (ImageButton)findViewById(R.id.add_event);
        add_group = (ImageButton)findViewById(R.id.add_group);
        add_notification = (ImageButton)findViewById(R.id.add_notification);

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);

        //先寫死
        String bName = "tag02";
        String macAddress = "D0:39:72:DE:DC:3A";

        editTextbName.setText(bName);
        textViewMac.setText(macAddress);


                /* 新增Event */
        add_event.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditActivity.this);
                dialog.setContentView(R.layout.event_dialog);
//                dialog.setTitle("Title...");//沒有作用
                dialog.show();


            }
        });


        /* 新增Group */
        add_group.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditActivity.this);
                dialog.setContentView(R.layout.group_dialog);
                dialog.show();
            }
        });

        /* 新增Notification */
        add_notification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditActivity.this);
                dialog.setContentView(R.layout.notification_dialog);
                dialog.show();
            }
        });


        /* cancel : go back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onClick(View v) {

    }
}
