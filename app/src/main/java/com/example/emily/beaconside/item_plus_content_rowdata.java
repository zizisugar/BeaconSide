package com.example.emily.beaconside;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import java.util.ArrayList;

public class item_plus_content_rowdata extends BaseAdapter{

    ArrayList<String> selectedStrings = new ArrayList<String>();

    private final Context context;
    private final String[] value_listName;


    private LayoutInflater mInflater;

    public item_plus_content_rowdata(Context context, String[] values) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_listName = values;
    }



    @Override
    public int getCount() {//算device Name長度
        return value_listName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        item_plus_content_rowdata.ViewHolder holder;//緩存
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.activity_item_plus_content_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new item_plus_content_rowdata.ViewHolder();

            holder.myListName = (CheckBox) convertView.findViewById(R.id.myListName);

            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (item_plus_content_rowdata.ViewHolder) convertView.getTag();
        }

        holder.myListName.setText(value_listName[position]);


        holder.myListName.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedStrings.add(buttonView.getText().toString());
                }else{
                    selectedStrings.remove(buttonView.getText().toString());
                }

            }
        });


        return convertView;
    }


    public ArrayList<String> getSelectedString(){
        return selectedStrings;
    }



    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        CheckBox myListName;

    }
}
