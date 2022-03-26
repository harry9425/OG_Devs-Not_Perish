package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
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
    TextView locationdisplay,welomeshow;
    public static String location;
    usermodel usermodel;
    String address;
    String currentPhotoPath;
    Button save;
    String uid;
    Uri uri=null;
    StorageReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeruser);
        mref= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        welomeshow=(TextView) findViewById(R.id.welcomeuser_register);
        circleImageView = (CircleImageView) findViewById(R.id.registeruser_dpuser);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        username = (EditText) findViewById(R.id.registeruser_username);
        useremail = (EditText) findViewById(R.id.registeruser_email);
        save = (Button) findViewById(R.id.registeruser_savedetail);
        locationdisplay=(TextView) findViewById(R.id.registeruser_locationshow);
        locationdisplay.setSelected(true);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            welomeshow.setText("Welcome, "+username.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        if(!name.isEmpty() && !email.isEmpty() && !location.isEmpty()){
            progressDialog.show();
            usermodel=new usermodel();
            usermodel.setAddress(address);
            usermodel.setCoordinates(location);
            usermodel.setEmail(email);
            usermodel.setName(name);
            usermodel.setPhone(phone);
            usermodel.setUid(uid);
            if(uri!=null){
                StorageReference filepath = mref.child("users").child(uid).child("dp");
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String fileLink = task.getResult().toString();
                                        usermodel.setDp(fileLink);
                                        progressDialog.dismiss();
                                        sdb(usermodel);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(registeruser.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else {
                usermodel.setDp("def");
                sdb(usermodel);
            }
        }
        else {
            Toast.makeText(this,"Fields cant be empty",Toast.LENGTH_LONG).show();
        }
    }
    private void sdb(usermodel usermodel){
        databaseReference.child("users").child(uid).setValue(usermodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(registeruser.this,"Signed_up",Toast.LENGTH_LONG).show();
                Intent i =new Intent(registeruser.this,startpage.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registeruser.this,"cant connect to server",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void bkbk(View view){
        onBackPressed();
    }
    public  void refresh(View view){
        Intent i=new Intent(this,registeruser.class);
        startActivity(i);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_Temp_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.harry9425.perish.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f=new File(currentPhotoPath);
                    uri=Uri.fromFile(f);
                    Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.fooddefaultpic)
                            .into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).placeholder(R.drawable.fooddefaultpic).into(circleImageView);
                                }
                            });
                    break;
            }
        }
    }

}