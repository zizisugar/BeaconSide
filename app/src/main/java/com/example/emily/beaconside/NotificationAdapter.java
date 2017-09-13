package com.example.emily.beaconside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2017/9/5.
 */

public class NotificationAdapter extends BaseAdapter{

    private final Context context;
    private final ArrayList<String> nContent_array;
    private final ArrayList<String> nStartTime_array;
    private final ArrayList<String> nEndTime_array;

    private LayoutInflater mInflater;

    public NotificationAdapter(Context context, ArrayList<String> nContent, ArrayList<String> nStartTime, ArrayList<String> nEndTime) {//架構子
        mInflater = LayoutInflater.from(context);//傳入Activity
        this.context = context;
        this.nContent_array = nContent;
        this.nStartTime_array = nStartTime;
        this.nEndTime_array = nEndTime;
    }


    @Override
    public int getCount() {//算device Name長度
        return nContent_array.size();
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
        NotificationAdapter.ViewHolder holder;//緩存
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.notification_list_item,null);//inflate(要加載的佈局id，佈局外面再嵌套一層父佈局(root)->如果不需要就寫null)
            holder = new NotificationAdapter.ViewHolder();
            holder.nContent = (TextView) convertView
                    .findViewById(R.id.textView_nContent);
            holder.nStartTime = (TextView) convertView
                    .findViewById(R.id.textView_nStartTime);
            holder.nEndTime = (TextView) convertView
                    .findViewById(R.id.textView_nEndTime);

            convertView.setTag(holder);//把查找的view通過ViewHolder封裝好緩存起來方便 ​​多次重用，當需要時可以getTag拿出來
        }else{
            holder = (NotificationAdapter.ViewHolder) convertView.getTag();
        }

//        Toast.makeText(context,"distance"+value_deviceDsc,Toast.LENGTH_SHORT).show();
        holder.nContent.setText(nContent_array.get(position));
        holder.nStartTime.setText(nStartTime_array.get(position));
        holder.nEndTime.setText(nEndTime_array.get(position));


        /*if(isLoading) {
            holder.spinner.setVisibility(View.VISIBLE);
            holder.beaconDistance.setVisibility(View.GONE);
        }
        else
            holder.spinner.setVisibility(View.GONE);*/
        return convertView;
    }



    static class ViewHolder {//緩存用。一種設計方法，就是設計個靜態類，緩存一下，省得Listview更新的時候，還要重新操作。

        TextView nContent;
        TextView nStartTime;
        TextView nEndTime;
    }
}
