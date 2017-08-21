package com.example.emily.beaconside;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class rowdata extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> value_deviceName;
    private final ArrayList<String> value_deviceDsc;
    private final ArrayList<String> value_address;
    private final ArrayList<String> value_bPic;

    private LayoutInflater mInflater;

    public rowdata(Context context, ArrayList<String> values, ArrayList<String> values2, ArrayList<String> value_address, ArrayList<String> value_bPic) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.value_deviceName = values;
        this.value_deviceDsc = values2;
        this.value_address = value_address;
        this.value_bPic = value_bPic;
    }


    @Override
    public int getCount() {//算device Name長度
        return value_deviceName.size();
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
            holder.beaconAddress = (TextView) convertView.findViewById(R.id.beaconAddress);
            holder.item_setting = (ImageButton) convertView.findViewById(R.id.item_setting);
            holder.beaconAddress = (TextView) convertView.findViewById(R.id.beaconAddress);

            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //根據value_bPic array來顯示圖片
        String bPic = value_bPic.get(position);
        int resID = context.getResources().getIdentifier(bPic, "drawable","com.example.emily.beaconside");
        holder.beaconImage.setImageResource(resID);

        /*switch(position) {
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
        }*/

        holder.beaconName.setText(value_deviceName.get(position));
        holder.beaconDistance.setText(value_deviceDsc.get(position));
        holder.beaconAddress.setText(value_address.get(position));
        return convertView;
    }



    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。
        ImageView beaconImage;
        TextView beaconName;
        TextView beaconDistance;
        TextView beaconAddress;
        TextView beaconNearby;
        ImageButton item_setting;
    }
}
