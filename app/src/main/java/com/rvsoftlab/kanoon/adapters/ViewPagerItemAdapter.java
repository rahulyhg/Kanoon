package com.rvsoftlab.kanoon.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RVishwakarma on 12/27/2017.
 */

public class ViewPagerItemAdapter extends PagerAdapter {
    private Activity activity;
    private List<Integer> viewList;
    private List<String> titieList;
    public ViewPagerItemAdapter(Activity activity) {
        this.activity = activity;
        viewList = new ArrayList<>();
        titieList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titieList.get(position);
    }

    public void addView(int viewID, String title){
        viewList.add(viewID);
        titieList.add(title);
        notifyDataSetChanged();
    }

    public void removeView(int viewID){
        titieList.remove(viewList.indexOf(viewID));
        viewList.remove(viewList.indexOf(viewID));
        notifyDataSetChanged();
    }
}
