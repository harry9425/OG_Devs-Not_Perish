package com.harry9425.notperish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    Intent i = new Intent(splashscreen.this, startpage.class);
                    startActivity(i);
                    finish();
                }
                else {
                    Intent i = new Intent(splashscreen.this, login_page.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }
}