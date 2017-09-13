package com.example.emily.beaconside;

/**
 * Created by user on 2017/9/12.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2017/9/3.
 */

public class BeaconCheckboxAdapter extends BaseAdapter{

    private Context context;

    private LayoutInflater mLayInf;
    ArrayList<HashMap<String, Object>> mItemList;
    List<Boolean> mChecked;
    //    List<Person> listPerson;
    HashMap<Integer,View> map = new HashMap<Integer,View>();





    public BeaconCheckboxAdapter(Context context, ArrayList<HashMap<String, Object>> itemList)
    {
        this.context = context;
        mChecked = new ArrayList<Boolean>();
        for(int i=0;i<itemList.size();i++){//依照beacon有幾個
            mChecked.add(false);//初始化mCheck的數量
        }
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = itemList;
    }

    public BeaconCheckboxAdapter(MainActivity mainActivity, ArrayList<HashMap<String,String>> list, int list_item, String[] strings, int[] ints) {
    }

    @Override
    public int getCount()
    {
        //取得 ListView 列表 Item 的數量
        return mItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItemList.get(position).get("macAddress");
        //取得 ListView 列表於 position 位置上的 Item

    }

    @Override
    public long getItemId(int position)
    {
        //取得 ListView 列表於 position 位置上的 Item 的 ID
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        //設定與回傳 convertView 作為顯示在這個 position 位置的 Item 的 View。
        View v = mLayInf.inflate(R.layout.new_event_beacon_list_item, parent, false);
        ViewHolder holder = null;
        holder = new ViewHolder();
        holder.selected = (CheckBox) v.findViewById(R.id.checkBox);
        holder.bName = (TextView)v.findViewById(R.id.bName);
        holder.bPic = (ImageView)v.findViewById(R.id.bPic);
        //holder.imgView = (ImageView)v.findViewById(R.id.pic);

        final int p = position;
        map.put(position, v);
        holder.selected.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                mChecked.set(p, cb.isChecked());
            }
        });
        v.setTag(holder);

        final ViewHolder finalHolder = holder;

        holder.bName.setText(mItemList.get(position).get("bName").toString());

        //設定圖片
        String bPic = mItemList.get(position).get("bPic").toString();
        int resID = context.getResources().getIdentifier(bPic, "drawable","com.example.emily.beaconside");
        holder.bPic.setImageResource(resID);

        holder.selected.setChecked(mChecked.get(position));



        return v;
    }
    class ViewHolder{
        CheckBox selected;
        TextView bName;
        ImageView bPic;
        //ImageView imgView;
    }

}
