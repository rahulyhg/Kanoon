package com.rvsoftlab.kanoon.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.helper.Constants;
import com.rvsoftlab.kanoon.helper.Helper;
import com.rvsoftlab.kanoon.helper.PermissionUtil;
import com.rvsoftlab.kanoon.view.KiewPager;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppBaseActivity {

    Button btnLogin;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;

    private FirebaseUser user;
    private FirebaseDatabase database;
    FirebaseFirestore db;
    private Menu menu;
    private KiewPager viewPager;
    private ViewPagerItemAdapter pagerAdapter;
    private ProgressBar progressBar;

    private EditText editMobile;
    private EditText editOtp;
    private EditText editUser;
    private SmsVerifyCatcher smsVerifyCatcher;
    private Activity mActivity;
    private String userMobile;
    private String userName;
    private PermissionUtil permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("");
        }
        permission = new PermissionUtil(this);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()){
                    case 0:
                        if (isSteponeOk()){
                            permission.checkAndAskPermission(Manifest.permission.READ_SMS, Constants.PERMISSION.SMS, new PermissionUtil.PermissionAskListener() {
                                @Override
                                public void onPermissionGranted() {
                                    registerLoginUser(editMobile.getText().toString());
                                }

                                @Override
                                public void onPermissionDenied() {

                                }
                            });
                        }
                        break;
                    case 1:
                        if (isSteptwoOk()){
                            authenticate();
                        }
                        break;
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);
        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

        //region VARIABLE INIT
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.time_progress);
        editMobile = findViewById(R.id.edit_mobile);
        editOtp = findViewById(R.id.edit_otp);
        editUser = findViewById(R.id.edit_user);

        mActivity = this;
        //endregion

        //region SMS CATCHER
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                editOtp.setText(parseCode(message));
                showProgress(true);
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter(Constants.SMS_SENDER);
        //endregion

        //getData();
        setupViewpager();
        //showProgress();
    }

    private void setupViewpager() {
        pagerAdapter = new ViewPagerItemAdapter(this);
        pagerAdapter.addView(R.id.mobile_holder,"Mobile Verification");
        pagerAdapter.addView(R.id.user_holder,"User");
        viewPager.setAdapter(pagerAdapter);
    }

    private boolean isSteponeOk() {
        boolean isOk;
        if (editMobile.getText().toString().equals("")){
            editMobile.setError("Please Enter Mobile No.");
            isOk = false;
        }else if (editMobile.getText().toString().length()<10){
            editMobile.setError("Please Enter Valid Text");
            editMobile.requestFocus();
            isOk = false;
        }else {
            isOk = true;
        }
        return isOk;
    }

    private boolean isSteptwoOk() {
        boolean isOk;
        if (editUser.getText().toString().equals("")){
            editUser.setError("Please Enter your name");
            editUser.requestFocus();
            isOk = false;
        }else if (editOtp.getText().toString().equals("")){
            editOtp.setError("Please Enter OTP");
            editOtp.requestFocus();
            isOk = false;
        }else {
            isOk = true;
        }
        return isOk;
    }

    private void showProgress(boolean isCancel) {
        final int[] sec = {0};
        final int oneMin = 60 * 1000; // 1 minute in milli seconds
        final int[] total = {0};
        progressBar.setProgress(0);
        progressBar.setMax(100*100);

        CountDownTimer timer = new CountDownTimer(oneMin,1000){
            @Override
            public void onTick(long timePassed) {
                //int total = (int) (timePassed/oneMin*60);
                /*progressBar.setProgress(total[0]++);
                Log.d(TAG,String.valueOf(total[0]));*/
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", total[0]++*100);
                animation.setDuration(500); // 0.5 second
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }

            @Override
            public void onFinish() {

            }
        };
        if (!isCancel){
            timer.start();
        }else {
            timer.cancel();
        }
    }

    private void registerLoginUser(final String mobile) {
        showLoading();
        if (Helper.isNetworkAvailable(mActivity)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.API, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideLoading();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")){
                            JSONObject user = json.getJSONObject("response");
                            userMobile = user.optString("mobile","");
                            userName = user.optString("Ravikant","");
                            viewPager.setCurrentItem(1,true);
                            showProgress(false);
                        }else {
                            Toast.makeText(mActivity, json.optString("error",""), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoading();
                    Helper.requestErrorHandling(mActivity,error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param = new HashMap<>();
                    param.put("tag","AUTH");
                    param.put("mobile",mobile);
                    return param;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(true);
            requestQueue.add(stringRequest);
        }else {
            Toast.makeText(mActivity, "Please check your network", Toast.LENGTH_SHORT).show();
        }
    }

    private void authenticate() {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                Log.d(TAG,response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("success")){
                        JSONObject res = json.optJSONObject("response");
                        signInWithFirebase(res.getString("token"));
                    }else {
                        Toast.makeText(mActivity, json.optString("error",""), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                Helper.requestErrorHandling(mActivity,error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> value = new HashMap<>();
                value.put("tag","verify");
                value.put("mobile",userMobile);
                value.put("code",editOtp.getText().toString());
                value.put("username",editUser.getText().toString());
                return value;
            }
        };
        requestQueue.add(stringRequest);
    }

    private String parseCode(String message) {
        return message.replaceAll("[^0-9]", "");
    }

    private void getData() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void signInWithFirebase(String token) {
        mAuth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user = task.getResult().getUser();
                    DocumentReference ref = fireDb.collection("users").document(user.getUid());
                    Map<String,String> userData = new HashMap<>();
                    userData.put("name",userName);
                    userData.put("mobile",userMobile);
                    ref.set(userData);
                    startActivity(new Intent(mActivity,MainActivity.class));
                    finish();
                }else {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //region JOIN
        MenuItem menuItem = menu.findItem(R.id.action_join);
        View view = menuItem.getActionView();
        Button join = view.findViewById(R.id.btn_join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion
        //region NEXT
        MenuItem nextItem = menu.findItem(R.id.action_next);
        View nextView = nextItem.getActionView();
        Button next = nextView.findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //endregion
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        //this.menu = menu;
        menu.findItem(R.id.action_next).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode,permissions,grantResults);
        permission.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    //region LIFECYCLE
    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
        user = mAuth.getCurrentUser();
        if (user!=null){
            //Toast.makeText(mActivity, user.getUid(), Toast.LENGTH_SHORT).show();

            startActivity(new Intent(mActivity,MainPageActivity.class));
            finish();

        }
        if (user==null){
            //Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
            /*DatabaseReference ref = database.getReference("social/users/"+user.getUid());
            ref.setValue(user.getUid());*//*
            Map<String,Map<String,Object>> parmas = new HashMap<>();
            Map<String,Object> userparam = new HashMap<>();
            userparam.put("created_at","created_at_timestamp");
            userparam.put("custom_id", new Random(10).toString());
            userparam.put("email","");
            userparam.put("enabled",true);
            parmas.put(user.getUid()+"1",userparam);
            String pushId = database.getReference().getRef().push().getKey();
            database.getReference().getRef().child("social/users").child(user.getUid()+"1").setValue(userparam);*/
        }
    }
    //endregion
}
