package com.rvsoftlab.kanoon.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by RVishwakarma on 12/29/2017.
 */

public class KiewPagerVertical extends ViewPager {
    private boolean isSwipeEnable = false;
    public KiewPagerVertical(@NonNull Context context) {
        super(context);
        init();
    }

    public KiewPagerVertical(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPageTransformer(true,new VerticalPagerTransformer());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setSwipeEnable(boolean swipeEnable){
        isSwipeEnable = swipeEnable;
    }

    private class VerticalPagerTransformer implements PageTransformer {
        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position<-1){
                view.setAlpha(0);
            }else if (position<=1){
                view.setAlpha(1);
                view.setTranslationX(view.getWidth() * -position);

                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);
            }else {
                view.setAlpha(0);
            }
        }
    }

    private MotionEvent swapXY(MotionEvent e){
        float width = getWidth();
        float height = getHeight();

        float newX = (e.getY()/height)*width;
        float newY = (e.getX()/width)*height;
        e.setLocation(newX,newY);

        return e;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev);
        return isSwipeEnable && intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSwipeEnable && super.onTouchEvent(swapXY(ev));
    }
}
