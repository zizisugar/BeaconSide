package com.example.emily.beaconside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {


     EditText editTextbName;
    TextView textViewMac;
     EditText editTextbContent;

    Button buttonSubmit;
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

        //add_event = (ImageButton)findViewById(R.id.add_event);
        //add_group = (ImageButton)findViewById(R.id.add_group);
        //add_notification = (ImageButton)findViewById(R.id.add_notification);

        //先寫死
        String bName = "tag02";
        String macAddress = "D0:39:72:DE:DC:3A";

        editTextbName.setText(bName);
        textViewMac.setText(macAddress);

    }
}
