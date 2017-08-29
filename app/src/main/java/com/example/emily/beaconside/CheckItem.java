package com.example.emily.beaconside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
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
        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list,false);
        already_list.setAdapter(adapter);
        adapter=new checkitem_rowdata(getBaseContext(),bPic_list, bName_list,bStatus_list,true);
        forget_list.setAdapter(adapter);
        // 解決ListView和ScrollView衝突的問題，需手動計算ListView裡內容的高度並指定給ListView
//        setListViewHeightBasedOnChildren(already_list);
//        setListViewHeightBasedOnChildren(forget_list);
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

    public void setListViewHeightBasedOnChildren(ListView listView) {

        //获取ListView对应的Adapter
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        //listAdapter.getCount()返回数据项的数目
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度，15是为了适用我的需求而加上的
        params.height =totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
