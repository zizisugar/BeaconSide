package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

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

        bluetooth.BTinit(this);
        bluetooth.getStartMyItemDistance(check_list);
        already_list=(ListView) findViewById(R.id.already);
        forget_list=(ListView) findViewById(R.id.forget);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this,"distance2"+bluetooth.myDeviceDistance, Toast.LENGTH_SHORT).show();

//        //建立存放HashMap資訊的ArrayList物件
//        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
//        //將資料轉換成HashMap型態存進ListView裡
//        for(int i = 0; i < bName_list.size(); i++){
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("bPic", bPic_list.get(i));//Img
//            map.put("bName", bName_list.get(i));//Name
//            map.put("bStatus", bStatus_list.get(i)); //Status
//            listItem.add(map);
//        }
//
//        //利用SimpleAdapter產生動態資訊
//        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem, //套入動態資訊
//                R.layout.checkitem_rowdata,//套用自訂的XML
//                new String[] {"bPic","bName","bStatus"}, //動態資訊取出順序
//                new int[] {R.id.bImage,R.id.bName,R.id.bStatus} //將動態資訊對應到元件ID
//        );
        for(int i=0;i<bName_list.size();i++){
            double d = tryParse(bStatus_list.get(i));
            if(d<40)
                ;
        }

        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list);
        already_list.setAdapter(adapter);
        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list);
        forget_list.setAdapter(adapter);

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
