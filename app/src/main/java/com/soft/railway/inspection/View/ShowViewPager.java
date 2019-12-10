package com.soft.railway.inspection.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ShowViewPager extends ViewPager {
    private boolean scrollable =false;

    public ShowViewPager(Context context) {
        super(context);
    }

    public ShowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrolable(boolean scrolable){
        this.scrollable=scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollable;
    }
}
