package com.rvsoftlab.kanoon.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RVishwakarma on 12/29/2017.
 */

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    public ViewPagerFragmentAdapter(FragmentManager manager) {
        super(manager);
        mFragmentList = new ArrayList<>();
        mTitleList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    public void addFragment(Fragment fragment,String title){
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }
}
