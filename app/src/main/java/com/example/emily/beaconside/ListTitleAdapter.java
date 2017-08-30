package com.example.emily.beaconside;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
/**
 * Created by Emily on 2017/8/29.
 */


public class ListTitleAdapter extends BaseAdapter {

    Context context;
    BaseAdapter parentAdapter;

    public ListTitleAdapter(Context c) {
        this(c, null);
    }

    public ListTitleAdapter(Context c, BaseAdapter dependentAdapter) {
        super();
        context = c;
        if (dependentAdapter != null) {
            parentAdapter = dependentAdapter;
        }
    }

    public int getCount() {
        if (parentAdapter != null) {
            if (parentAdapter.getCount() == 0) {
                return 0;
            }
        }
        return 1;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(context);


        return layout;
    }
}