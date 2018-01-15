package com.rvsoftlab.kanoon.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by RVishwakarma on 1/12/2018.
 */

public abstract class AppBaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Activity mActivity;
    public static final String TAG = "KANOON";
    public FirebaseFirestore fireDb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Loading...");
        fireDb = FirebaseFirestore.getInstance();
    }

    public void showLoading(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void hideLoading(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void hideStatusBar(){
        // Hide Status Bar
        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatusBar(){
        /*if (Build.VERSION.SDK_INT < 16) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            // Show Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }*/
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
