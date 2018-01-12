package com.rvsoftlab.kanoon.activities;

import android.animation.ObjectAnimator;
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
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rvsoftlab.kanoon.R;
import com.rvsoftlab.kanoon.adapters.ViewPagerItemAdapter;
import com.rvsoftlab.kanoon.view.KiewPager;

public class LoginActivity extends AppBaseActivity {

    Button btnLogin;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private String TAG = LoginActivity.class.getSimpleName();
    private FirebaseUser user;
    private FirebaseDatabase database;
    FirebaseFirestore db;
    private Menu menu;
    private KiewPager viewPager;
    private ViewPagerItemAdapter pagerAdapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("");
        }

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //authenticate();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);
        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        viewPager = findViewById(R.id.view_pager);

        progressBar = findViewById(R.id.time_progress);
        //getData();
        setupViewpager();
        //showProgress();
    }

    private void showProgress() {
        final int[] sec = {0};
        final int oneMin = 60 * 1000; // 1 minute in milli seconds
        final int[] total = {0};
        progressBar.setProgress(0);
        progressBar.setMax(100*100);
        new CountDownTimer(oneMin,1000){
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
        }.start();
    }

    private void setupViewpager() {
        pagerAdapter = new ViewPagerItemAdapter(this);
        pagerAdapter.addView(R.id.mobile_holder,"Mobile Verification");
        viewPager.setAdapter(pagerAdapter);
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

    private void authenticate() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://rvsoft.esy.es/Android/kanoon/firebase.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,response);
                signInWithFirebase(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void signInWithFirebase(String token) {
        mAuth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    user = task.getResult().getUser();
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
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
}
