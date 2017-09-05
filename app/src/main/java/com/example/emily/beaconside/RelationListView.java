package com.example.emily.beaconside;

/**
 * Created by Emily on 2017/9/4.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
public class twoListView extends ListView {
    private twoListView mListView;

    public twoListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public twoListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = getMeasuredHeight();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST) {
            final int childCount = getChildCount();
            for(int i=0;i<childCount;i++) {
                View item = getChildAt(i);
                measureChild(item, widthMeasureSpec, heightMeasureSpec);
                width = Math.max(width, item.getMeasuredWidth());
            }
        }
        setMeasuredDimension(width, height);
    }

    public void setRelatedListView(twoListView listView) {
        mListView = listView;
    }

    public void onTouch(MotionEvent ev) {
        super.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(null != mListView) {
            mListView.onTouch(ev);
        }

        return super.onTouchEvent(ev);
    }
}