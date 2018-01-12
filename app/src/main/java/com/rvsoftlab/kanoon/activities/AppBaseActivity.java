package com.rvsoftlab.kanoon.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by RVishwakarma on 1/12/2018.
 */

public abstract class AppBaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Activity mActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Loading...");
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
}
