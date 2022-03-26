package com.harry9425.notperish;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class donatepage extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    Location currentlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    AlertDialog.Builder alertDialog;
    ImageButton refresh;
    SupportMapFragment supportMapFragment;
    ImageButton next;
    private static final int Request_code = 101;
    EditText locationet,detailset,quantityet;
    Button chagqunty;
    Switch vegn;
    SeekBar seekBar;
    DatabaseReference databaseReference;
    int choose=1;
    TextView edibledis;
    CircleImageView circleImageView;
    String currentPhotoPath;
    StorageReference mref;
    Uri uri=null;
    String nameofuser=null;
    String location,latltd,details,quantity,chngq,type,edible="1",dp=null,uid,donateid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donatepage);
        mref= FirebaseStorage.getInstance().getReference();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        circleImageView=(CircleImageView) findViewById(R.id.donate_fooddp);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.donatemap);
        refresh=(ImageButton) findViewById(R.id.refresh_donatemap);
        next=(ImageButton) findViewById(R.id.save_donatemap);
        locationet=(EditText) findViewById(R.id.location_donate);
        detailset=(EditText) findViewById(R.id.details_donate);
        quantityet=(EditText) findViewById(R.id.quantity_donate);
        chagqunty=(Button) findViewById(R.id.chngqn_donate);
        vegn=(Switch) findViewById(R.id.switch_donate);
        seekBar=(SeekBar) findViewById(R.id.edibleslider_donate);
        edibledis=(TextView) findViewById(R.id.slidertxt_donate);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(statusOfGPS)
                {
                    fetchlastLocation();
                }
                else {
                    showSettingAlert();
                }
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
            return;
        }
        fetchlastLocation();
        manager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                2000,
                5, locationListenerGPS);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });
        chagqunty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose==1){
                    choose=2;
                    chagqunty.setText("g");
                }
                else if(choose==2){
                    choose=3;
                    chagqunty.setText("Kg");
                }
                else {
                    choose=1;
                    chagqunty.setText("P");
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                edible=seekBar.getProgress()+"";
                edibledis.setText(edible);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameofuser=snapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) {
            fetchlastLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            showSettingAlert();
        }
    };

    public void savetodb(View view){
        location=locationet.getText().toString().trim();
        latltd=currentlocation.getLatitude()+"&"+currentlocation.getLongitude();
        details=detailset.getText().toString().trim();
        quantity=quantityet.getText().toString().trim();
        chngq=chagqunty.getText().toString();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        if(!vegn.isChecked()){
            type="Veg";
        }
        else {
            type="NonVeg";
        }
        if(!location.isEmpty() && !latltd.isEmpty() && !details.isEmpty() && !quantity.isEmpty() && !chngq.isEmpty() && nameofuser!=null) {
            progressDialog.show();
            donatemodel donatemodel = new donatemodel();
            donatemodel.setDetails(details);
            donatemodel.setTime(System.currentTimeMillis()+"");
            donatemodel.setEdible(edible);
            donatemodel.setName(nameofuser);
            donatemodel.setLatltd(latltd);
            donatemodel.setLocation(location);
            donatemodel.setQuantity(quantity);
            donatemodel.setQynchng(chngq);
            donatemodel.setUid(uid);

            donatemodel.setType(type);
            donatemodel.setDonationid(databaseReference.push().getKey());
            if(uri!=null){
                StorageReference filepath = mref.child("donation").child(uid).child(donatemodel.getDonationid());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String fileLink = task.getResult().toString();
                                        donatemodel.setDp(fileLink);
                                        databaseReference.child("donation").child(uid).child(donatemodel.getDonationid()).setValue(donatemodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                progressDialog.cancel();
                                                Toast.makeText(donatepage.this,"Successfull",Toast.LENGTH_LONG).show();
                                                Intent i=new Intent(donatepage.this,startpage.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                progressDialog.cancel();
                                Toast.makeText(donatepage.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            else {
                donatemodel.setDp("def");
                databaseReference.child("donation").child(uid).child(donatemodel.getDonationid()).setValue(donatemodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        Toast.makeText(donatepage.this,"Successfull",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(donatepage.this,startpage.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }
    }

    public void bkbk(View view){
        onBackPressed();
    }

    private void fetchlastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    currentlocation=location;
                    Geocoder geocoder = new Geocoder(donatepage.this, Locale.getDefault());
                    List<Address> addresses  = null;
                    try {
                        addresses = geocoder.getFromLocation(currentlocation.getLatitude(),currentlocation.getLongitude(), 1);
                        locationet.setText(addresses.get(0).getAddressLine(0).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    supportMapFragment.getMapAsync(donatepage.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map=googleMap;
        LatLng latLng=new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
        map.setBuildingsEnabled(true);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView("https://firebasestorage.googleapis.com/v0/b/hackathonproj-b48e0.appspot.com/o/users%2FT18XIj4HxgaMWH56LE33Fwm8KS42%2FInventory%2F-Myqfx4pDTD-Wu1qo12x?alt=media&token=45236fcb-27f3-48d1-ba8c-06079c72a007")));
        map.addMarker(markerOptions);
        next.setEnabled(true);
        next.setAlpha(1f);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  Request_code:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fetchlastLocation();
                } break;
        }
    }

    public void showSettingAlert()
    {
        alertDialog= new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, go to settings to enavle gps to acces this feature !! ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i=new Intent(donatepage.this,donatepage.class);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }
    public void repin(View view){
        onMapReady(map);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f=new File(currentPhotoPath);
                    uri= Uri.fromFile(f);
                    dp=uri.toString();
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
}