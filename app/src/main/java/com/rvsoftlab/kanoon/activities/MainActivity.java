package com.rvsoftlab.kanoon.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dewarder.camerabutton.CameraButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.adapters.ViewPagerFragmentAdapter;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.fragments.HomeFragment;
import com.rvsoftlab.kanoon.helper.BottomNavigationViewHelper;
import com.rvsoftlab.kanoon.helper.Constants;
import com.rvsoftlab.kanoon.helper.Helper;
import com.rvsoftlab.kanoon.helper.PermissionUtil;
import com.zxy.tiny.Tiny;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppBaseActivity {

    private CameraButton cameraButton;
    private CameraView cameraView;
    private View cameraContent;

    private ImageButton fabCamera;
    private ImageButton gallerySwitch;
    private ImageButton cameraSwitch;
    private boolean isExpand = false;
    private PermissionUtil permission;
    private Activity mActivity = this;
    private boolean isOpned = false;
    private View cameraHolder;
    private ImageView imagePreview;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private BottomNavigationView bottomNavigationView;
    private ViewPager kiewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        //region VARIABLE
        permission = new PermissionUtil(mActivity);
        cameraHolder = findViewById(R.id.camera_holder);
        imagePreview = findViewById(R.id.image_preview);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.BLACK));
        //endregion

        //region CAMERA BUTTON
        cameraButton = findViewById(R.id.camera_button);
        cameraView = findViewById(R.id.camera_preview);
        kiewPager = findViewById(R.id.main_view_pager);
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
                permission.checkAndAskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Constants.PERMISSION.STORAGE, new PermissionUtil.PermissionAskListener() {
                    @Override
                    public void onPermissionGranted() {
                        capture();
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(mActivity, "Please Allow Permission to capture the pic", Toast.LENGTH_SHORT).show();
                    }
                });
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
                                processData(bitmap);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        cameraView.capturePicture();
    }

    private void processData(Bitmap bitmap) {
        try {
            imagePreview.setImageBitmap(bitmap);
            cameraView.stop();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading..");
            dialog.show();

            FileOutputStream out = null;
            out = new FileOutputStream(new File(Helper.getDataDirectory(mActivity)+"/test.jpg"));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);

            final File file = new File(Helper.getDataDirectory(mActivity)+"/test.jpg");
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(Uri.fromFile(file))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.d(TAG,taskSnapshot.getStorage().getDownloadUrl().toString());
                            imagePreview.setImageBitmap(null);
                            file.delete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mActivity, "Fail", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            dialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupViewPager() {
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(),"Home");
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
                //changeContentVisibility();
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
                //changeContentVisibility();
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
