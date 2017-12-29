package com.rvsoftlab.kanoon;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rvsoftlab.kanoon.adapters.ViewPagerFragmentAdapter;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.fragments.CameraFragment;
import com.rvsoftlab.kanoon.fragments.HomeFragment;
import com.rvsoftlab.kanoon.view.KiewPagerVertical;

public class MainActivity extends AppCompatActivity {

    private KiewPagerVertical viewPager;
    private ViewPagerFragmentAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.main_view_pager);
        viewPager.setSwipeEnable(true);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new HomeFragment(),"Home");
        pagerAdapter.addFragment(new CameraFragment(),"Camera");
        viewPager.setAdapter(pagerAdapter);
    }
}
