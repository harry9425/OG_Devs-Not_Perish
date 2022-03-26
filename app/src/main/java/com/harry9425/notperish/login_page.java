package com.harry9425.notperish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class login_page extends AppCompatActivity {

    EditText phoneno;
    Button checkforotp;
    String countryCodeAndroid = "91";
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        phoneno=(EditText) findViewById(R.id.loginpage_enterno);
        checkforotp=(Button) findViewById(R.id.loginpage_sendotpbtn);
        ccp=(CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCodeAndroid = ccp.getSelectedCountryCode();
            }
        });
        checkforotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneno.setError(null);
                phoneno.setFocusable(true);
                if(phoneno.getText().toString().trim().isEmpty()){
                    phoneno.setError("Empty");
                    phoneno.setFocusable(false);
                }
                else if(phoneno.getText().toString().trim().length()!=10){
                    phoneno.setError("InValid Number");
                    phoneno.setFocusable(false);
                }
                else {
                    otpverify.phone=countryCodeAndroid+phoneno.getText().toString().trim();
                    Intent i=new Intent(login_page.this,otpverify.class);
                    startActivity(i);
                }
            }
        });
    }
}