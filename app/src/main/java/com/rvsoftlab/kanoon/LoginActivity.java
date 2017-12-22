package com.rvsoftlab.kanoon;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.lang.reflect.Method;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private String TAG = LoginActivity.class.getSimpleName();
    private FirebaseUser user;
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
                authenticate();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);
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
        MenuItem menuItem = menu.findItem(R.id.action_join);
        View view = menuItem.getActionView();
        Button join = view.findViewById(R.id.btn_join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_join){
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user==null){
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
