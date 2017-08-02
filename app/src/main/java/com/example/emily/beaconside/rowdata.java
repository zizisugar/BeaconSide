package com.example.emily.beaconside;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class rowdata extends BaseAdapter {
    private final Context context;
    private final String[] value_deviceName;
    private final String[] value_deviceDsc;

    private LayoutInflater mInflater;

    public rowdata(Context context, String[] values, String[] values2) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_deviceName = values;
        this.value_deviceDsc = values2;
    }

    @Override
    public int getCount() {//算device Name長度
        return value_deviceName.length;
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
        ViewHolder holder;//緩存
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.activity_rowdata,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new ViewHolder();

            holder.beaconImage = (ImageView) convertView
                    .findViewById(R.id.beaconImage);
            holder.beaconName = (TextView) convertView
                    .findViewById(R.id.beaconName);
            holder.beaconDistance = (TextView) convertView
                    .findViewById(R.id.beaconDistance);
            holder.item_setting = (ImageButton) convertView.findViewById(R.id.item_setting);


            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        switch(position) {
            case 0:
                holder.beaconImage.setImageResource(R.drawable.wallet);
                break;
            case 1:
                holder.beaconImage.setImageResource(R.drawable.key);
                break;
            case 2:
                holder.beaconImage.setImageResource(R.drawable.camera);
                break;
            case 3:
                holder.beaconImage.setImageResource(R.drawable.laptop);
                break;
            default:
                holder.beaconImage.setImageResource(R.drawable.beacon);
                break;
        }

        holder.beaconName.setText(value_deviceName[position]);
        holder.beaconDistance.setText(value_deviceDsc[position]);
        return convertView;
    }

    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        ImageView beaconImage;
        TextView beaconName;
        TextView beaconDistance;
        TextView beaconNearby;
        ImageButton item_setting;
    }
}
