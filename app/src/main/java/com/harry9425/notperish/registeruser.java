package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class registeruser extends AppCompatActivity {

    DatabaseReference databaseReference;
    CircleImageView circleImageView;
    EditText username,useremail,userlocation;
    String name,email;
    public  static String phone;
    TextView locationdisplay;
    public static String location;
    usermodel usermodel;
    String address;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeruser);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        circleImageView = (CircleImageView) findViewById(R.id.registeruser_dpuser);
        username = (EditText) findViewById(R.id.registeruser_username);
        useremail = (EditText) findViewById(R.id.registeruser_email);
        save = (Button) findViewById(R.id.registeruser_savedetail);
        locationdisplay=(TextView) findViewById(R.id.registeruser_locationshow);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String[] s=location.split("&");
        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(s[0]),Double.parseDouble(s[1]), 1);
            address= addresses.get(0).getAddressLine(0);
            locationdisplay.setText(address);
            locationdisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri mapUri = Uri.parse("geo:0,0?q="+s[0]+","+s[1]+"(Your location)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void savetodb(View view){
        name=username.getText().toString().trim();
        email=useremail.getText().toString().trim();
        if(!name.isEmpty() && !email.isEmpty() && !location.isEmpty()){
            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            usermodel=new usermodel();
            usermodel.setAddress(address);
            usermodel.setCoordinates(location);
            usermodel.setEmail(email);
            usermodel.setName(name);
            usermodel.setPhone(phone);
            usermodel.setUid(uid);
            databaseReference.child("users").child(uid).setValue(usermodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(registeruser.this,"Signed_up",Toast.LENGTH_LONG).show();
                    //Intent i =new Intent(registeruser.this,startpage.class);
                   // startActivity(i);
                    //finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(registeruser.this,"cant connect to server",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(this,"Fields cant be empty",Toast.LENGTH_LONG).show();
        }
    }
}