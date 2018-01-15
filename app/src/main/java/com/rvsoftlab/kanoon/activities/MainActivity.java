package com.rvsoftlab.kanoon.activities;

import android.animation.Animator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.otaliastudios.cameraview.CameraView;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.helper.BottomNavigationViewHelper;

import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppBaseActivity {
    private BottomNavigationViewEx navigationView;
    private FloatingActionButton cameraButton;
    private CameraView cameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.enableAnimation(true);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.setTextVisibility(false);
        navigationView.setItemIconTintList(null);

        cameraButton = findViewById(R.id.fab);
        cameraView = findViewById(R.id.camera_preview);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.setVisibility(View.VISIBLE);
                expand(cameraView,cameraButton);
            }
        });

    }

    public void expand(View v, final View camera) {
        int cx = (camera.getLeft()+camera.getRight())/2;
        int cy = (camera.getTop()+camera.getBottom())/2;

        Display display = this.getWindowManager().getDefaultDisplay();
        int maxHeight = display.getHeight();
        int maxWeight = display.getWidth();

        float finalRadius = (float) Math.hypot(maxHeight,maxWeight);

        Animator animator = ViewAnimationUtils.createCircularReveal(v,cx,cy,0,finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                getSupportActionBar().hide();
                cameraView.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !cameraView.isStarted()) {
            cameraView.start();
        }
    }

    //region LIFE CYCLE

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    //endregion
}
