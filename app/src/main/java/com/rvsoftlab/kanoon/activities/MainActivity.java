package com.rvsoftlab.kanoon.activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dewarder.camerabutton.CameraButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.SessionType;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.helper.BottomNavigationViewHelper;
import com.rvsoftlab.kanoon.view.KiewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppBaseActivity {
    //private BottomNavigationViewEx navigationView;
    //private FloatingActionButton fabCamera;
    private CameraButton cameraButton;
    private CameraView cameraView;
    private RelativeLayout cameraContent;
    private KiewPager kiewPager;
    private ImageButton fabCamera;
    private ImageButton gallerySwitch;
    private ImageButton cameraSwitch;
    private boolean isExpand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region BOTTOM NAVIGATION
        /*navigationView = findViewById(R.id.bottom_navigation);
        navigationView.enableAnimation(true);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableShiftingMode(false);
        navigationView.setTextVisibility(false);
        navigationView.setItemIconTintList(null);*/
        //endregion

        //region CAMERA BUTTON
        cameraButton = findViewById(R.id.camera_button);
        cameraView = findViewById(R.id.camera_preview);
        kiewPager = findViewById(R.id.camera_viewpager);
        cameraContent = findViewById(R.id.camera_content);
        fabCamera = findViewById(R.id.fab_camera);
        gallerySwitch = findViewById(R.id.gallery_switch);
        cameraSwitch = findViewById(R.id.camera_switch);

        //region LISTENERS
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExpand)
                    expand(cameraContent,fabCamera);
            }
        });

        cameraButton.setOnTapEventListener(new CameraButton.OnTapEventListener() {
            @Override
            public void onTap() {
                capture();
            }
        });
        cameraButton.setOnStateChangeListener(new CameraButton.OnStateChangeListener() {
            @Override
            public void onStateChanged(@NonNull CameraButton.State state) {
                if (state == CameraButton.State.START_EXPANDING){
                    translateToLeft(cameraSwitch,false);
                    translateToRight(gallerySwitch,false);
                }else if (state == CameraButton.State.START_COLLAPSING){
                    translateToLeft(cameraSwitch,true);
                    translateToRight(gallerySwitch,true);
                }
            }
        });
        cameraButton.setOnHoldEventListener(new CameraButton.OnHoldEventListener() {
            @Override
            public void onStart() {
                cameraView.setSessionType(SessionType.VIDEO);
                cameraView.startCapturingVideo(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/test.mp4"));
            }

            @Override
            public void onFinish() {
                cameraView.setSessionType(SessionType.PICTURE);
                cameraView.stopCapturingVideo();
            }

            @Override
            public void onCancel() {
                cameraView.setSessionType(SessionType.PICTURE);
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion

        setupViewPager();
    }

    private void capture() {
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                if (jpeg!=null){
                    CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                        @Override
                        public void onBitmapReady(Bitmap bitmap) {
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/test.jpg"));
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                            }catch (Exception e){
                                if (out!=null)
                                    try {
                                        out.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                            }
                        }
                    });
                }
            }
        });
        cameraView.capturePicture();
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
                cameraView.start();
                hideStatusBar();

                //translateButton(camera,false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isExpand = true;
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
                isExpand = false;
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
        collapse(cameraContent,fabCamera);
        //super.onBackPressed();
    }

    //endregion
}
