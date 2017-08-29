package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jennifer9759 on 2017/8/23.
 */

public class CheckItem extends AppCompatActivity {
    ListView listView1;
    rowdata adapter;
    BluetoothMethod bluetooth = new BluetoothMethod();

    public ArrayList check_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_item);
        Intent intent = this.getIntent();
        check_list = intent.getStringArrayListExtra("macAddress");
        bluetooth.BTinit(this);
        bluetooth.getStartMyItemDistance(check_list);
        listView1=(ListView) findViewById(R.id.listView1);
    }

}
