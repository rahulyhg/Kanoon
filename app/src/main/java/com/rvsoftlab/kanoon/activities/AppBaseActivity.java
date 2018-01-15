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
    private static final long ANIMATION_TRANSLATION_DURATION = 200L;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatusBar(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void translateToRight(View view, boolean show) {
        float x = show ? 0f : view.getWidth();
        float alpha = show ? 1f : 0f;
        view.animate().translationX(-x)
                .alpha(alpha)
                .setDuration(ANIMATION_TRANSLATION_DURATION);
    }

    public void translateToLeft(View view, boolean show) {
        float x = show ? 0f : view.getWidth();
        float alpha = show ? 1f : 0f;
        view.animate().translationX(x)
                .alpha(alpha)
                .setDuration(ANIMATION_TRANSLATION_DURATION);
    }

    public void translateToDown(View view, boolean show){
        float y = show?0f:view.getHeight();
        float alpha = show?1f:0f;
        view.animate().translationY(y)
                .alpha(alpha)
                .setDuration(ANIMATION_TRANSLATION_DURATION);
    }

    public void translateToUp(View view, boolean show){
        float y = show ? 0f : view.getHeight();
        float alpha = show ? 1f : 0f;
        view.animate().translationY(-y)
                .alpha(alpha)
                .setDuration(ANIMATION_TRANSLATION_DURATION);
    }
}
