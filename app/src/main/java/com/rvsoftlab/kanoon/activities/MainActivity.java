package com.rvsoftlab.kanoon.activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dewarder.camerabutton.CameraButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.otaliastudios.cameraview.CameraView;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.helper.BottomNavigationViewHelper;
import com.rvsoftlab.kanoon.view.KiewPager;

import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppBaseActivity {
    private BottomNavigationViewEx navigationView;
    //private FloatingActionButton cameraButton;
    private CameraButton cameraButton;
    private CameraView cameraView;
    private RelativeLayout cameraContent;
    private KiewPager kiewPager;

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

        //cameraButton = findViewById(R.id.fab);
        cameraButton = findViewById(R.id.fab_button);
        cameraView = findViewById(R.id.camera_preview);
        kiewPager = findViewById(R.id.camera_viewpager);

        cameraContent = findViewById(R.id.camera_content);
        cameraButton.setMainCircleRadius(28);
        cameraButton.bringToFront();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cameraContent.setVisibility(View.VISIBLE);

            }
        });
        cameraButton.setOnTapEventListener(new CameraButton.OnTapEventListener() {
            @Override
            public void onTap() {
                expand(cameraContent,cameraButton);
            }
        });
        setupViewPager();
    }

    private void setupViewPager() {
        ViewPagerItemAdapter adapter = new ViewPagerItemAdapter(this);
        adapter.addView(R.id.camera_holder,"Camera");
        kiewPager.setAdapter(adapter);
    }

    public void expand(final View v, final View camera) {
        int cx = (camera.getLeft()+camera.getRight())/2;
        int cy = (camera.getTop()+camera.getBottom())/2;

        Display display = this.getWindowManager().getDefaultDisplay();
        int maxHeight = display.getHeight();
        int maxWeight = display.getWidth();

        float finalRadius = (float) Math.hypot(maxHeight,maxWeight);

        Animator animator = ViewAnimationUtils.createCircularReveal(v,cx,cy,0,finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                v.setVisibility(View.VISIBLE);
                getSupportActionBar().hide();
                cameraView.start();
                hideStatusBar();
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

    public void collapse(final View v, View toRadiusView){
        int cx = (toRadiusView.getLeft()+toRadiusView.getRight())/2;
        int cy = (toRadiusView.getTop()+toRadiusView.getBottom())/2;

        int maxHeight = v.getHeight();
        int maxWidth = v.getWidth();

        float finalRadius = (float)Math.hypot(maxHeight,maxWidth);

        Animator animator = ViewAnimationUtils.createCircularReveal(v,cx,cy,finalRadius,0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                v.setVisibility(View.GONE);
                showStatusBar();
                cameraView.stop();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
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

    @Override
    public void onBackPressed() {
        collapse(cameraContent,cameraButton);
        //super.onBackPressed();
    }

    //endregion
}
