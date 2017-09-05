package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jennifer9759 on 2017/8/23.
 */

public class CheckItem extends AppCompatActivity {
    ListView already_list;
    ListView forget_list;
    checkitem_rowdata adapter;
    BluetoothMethod bluetooth = new BluetoothMethod();
    ArrayList<String> check_list;
    ArrayList<String> bPic_list;
    ArrayList<String> bName_list;
    ArrayList<String> bStatus_list;
    ArrayList<String> forget;
    ArrayList<String> already;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_item);
        Intent intent = this.getIntent();
        check_list = intent.getStringArrayListExtra("macAddress");
        bPic_list = intent.getStringArrayListExtra("bPic_list");
        bName_list = intent.getStringArrayListExtra("bName_list");
        bStatus_list = intent.getStringArrayListExtra("bStatus_list");
//        bluetooth.bluetoothStop();
//        bluetooth.BTinit(this);
//        bluetooth.getStartMyItemDistance(check_list);
        already_list=(ListView) findViewById(R.id.already);
        forget_list=(ListView) findViewById(R.id.forget);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list,false);
        already_list.setAdapter(adapter);
        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list,true);
        forget_list.setAdapter(adapter);
        forget_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //選項按下反應
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = bName_list.get(position);      //哪一個列表
                String itemAddress = check_list.get(position);
                Toast.makeText(CheckItem.this, itemName + " selected", Toast.LENGTH_SHORT).show(); //顯示訊號
                bluetooth.bluetoothFunction="searchItem";
//                /*換頁面 有換Activity*/
                Intent intent = new Intent();
                intent.setClass(CheckItem.this, Compass.class);
                Bundle bundle = new Bundle();
                bundle.putString("itemName", itemName);
                bundle.putString("itemAddress", itemAddress);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } );
    }

    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }

    public void onBackPressed(View view) {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }

    public static double tryParse(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

}
