package com.rvsoftlab.kanoon.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.view.KameraPreview;


public class CameraFragment extends Fragment {
    Context mContext;
    //region INITIAL SETUP
    public CameraFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }
    //endregion

    private FrameLayout cameraPriview;
    private KameraPreview preview;

    // Native camera.
    private Camera mCamera;
    // Reference to the containing view.
    private View mCameraView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_camera, container, false);
        cameraPriview = view.findViewById(R.id.camera_preview);



        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 50);
        }else {
            safeCameraOpenInView(cameraPriview);
        }

        return view;
    }


    /**
     * Recommended "safe" way to open the camera.
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true){
            preview = new KameraPreview(mContext, mCamera,view);
            FrameLayout frameLayout = view.findViewById(R.id.camera_preview);
            frameLayout.addView(preview);
            preview.startCameraPreview();
        }
        return qOpened;
    }

    /**
     * Safe method for getting a camera instance.
     * @return
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(preview != null){
            preview.destroyDrawingCache();
            preview.mCamera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        safeCameraOpenInView(cameraPriview);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
