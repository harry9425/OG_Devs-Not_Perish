package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class otpverify extends AppCompatActivity {

    public static String phone;
    String verificationCodeBySystem;
    public static int what=0;
    ProgressDialog progressDialog;
    EditText otpbyuser;
    TextView dis;
    Button verify;
    private DatabaseReference databaseReference;
    public static String token;
    ImageButton reloadotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        otpbyuser=(EditText) findViewById(R.id.otpverify_otp);
        verify=(Button) findViewById(R.id.otpverify_verifybtn);
        dis=(TextView) findViewById(R.id.phonedis_otp);
        dis.setText("Enter OTP sent to +"+phone);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("ACCOUNT VERIFICATION");
        progressDialog.setMessage("Waiting for otp");
        progressDialog.show();
        reloadotp=(ImageButton) findViewById(R.id.otp_reloadbtn);
        reloadotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendverificationcodetouser(phone);
            }
        });
        phone="+"+phone;
        sendverificationcodetouser(phone);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = otpbyuser.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    otpbyuser.setError("Wrong OTP...");
                    otpbyuser.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }
    private void sendverificationcodetouser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s,forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!= null)
                verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otpverify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(otpverify.this, "WRONG OTP", Toast.LENGTH_SHORT).show();

        }
    }
    public void bkbk(View view){
        onBackPressed();
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(otpverify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            String userid=firebaseAuth.getCurrentUser().getUid().toString();
                            Toast.makeText(otpverify.this,userid,Toast.LENGTH_LONG).show();
                            getlocation.phone=phone;
                            Intent i =new Intent(otpverify.this,getlocation.class);
                            startActivity(i);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(otpverify.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}