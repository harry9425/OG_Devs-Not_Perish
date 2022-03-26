package com.harry9425.notperish;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class accountsetting extends FragmentActivity implements OnMapReadyCallback {

    ImageView imageView;
    TextView name, email, phone, inventory, donate, location;
    GoogleMap map;
    String uid;
    Location currentlocation;
    public static String dp;
    DatabaseReference databaseReference;
    StorageReference mref;
    AlertDialog.Builder alertDialog;
    ImageButton refresh, edit;
    SupportMapFragment supportMapFragment;
    private static final int Request_code = 101;
    int touch = 0;
    String currentPhotoPath;
    Uri uri;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);
        mref = FirebaseStorage.getInstance().getReference();
        edit = (ImageButton) findViewById(R.id.editpagebtn);
        refresh = (ImageButton) findViewById(R.id.donatemap3relocate);
        location = (TextView) findViewById(R.id.accountpage_location);
        location.setSelected(true);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.donatemap3);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.keepSynced(true);
        imageView = (ImageView) findViewById(R.id.accountpage_dp);
        name = (TextView) findViewById(R.id.accountpage_name);
        email = (TextView) findViewById(R.id.accountpage_email);
        phone = (TextView) findViewById(R.id.accountpage_phone);
        inventory = (TextView) findViewById(R.id.accountpage_inventory);
        donate = (TextView) findViewById(R.id.accountpage_donate);
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                email.setText(snapshot.child("email").getValue().toString());
                location.setText(snapshot.child("address").getValue().toString());
                phone.setText(snapshot.child("phone").getValue().toString());
                inventory.setText(snapshot.child("inventory").getChildrenCount() + " items");
                if (snapshot.hasChild("dp")) {
                    dp = snapshot.child("dp").getValue().toString();
                    Glide.with(accountsetting.this)
                            .load(dp)
                            .placeholder(R.drawable.logoloading)
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.logoloading);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent(view);
                    }
                });
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("donation").child(uid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long val = snapshot.getChildrenCount();
                        donate.setText(val + " items");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (statusOfGPS) {
                    fetchlastLocation();
                } else {
                    showSettingAlert();
                }
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);
            return;
        }
        fetchlastLocation();
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                5, locationListenerGPS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchlastLocation();
                }
                break;
        }
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            fetchlastLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            showSettingAlert();
        }
    };

    private void fetchlastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlocation = location;
                    Geocoder geocoder = new Geocoder(accountsetting.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(currentlocation.getLatitude(), currentlocation.getLongitude(), 1);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                        databaseReference.child("address").setValue(addresses.get(0).getAddressLine(0)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child("coordinates").setValue(currentlocation.getLatitude() + "&" + currentlocation.getLongitude()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    supportMapFragment.getMapAsync(accountsetting.this);
                }
            }
        });
    }

    public void edittouch(View view) {
        if (touch == 0) {
            touch = 1;
            edit.setImageResource(R.drawable.ic_baseline_close_24);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatealertbox(view, "name");
                }
            });
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatealertbox(view, "email");
                }
            });
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatealertbox(view, "phone");
                }
            });
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generatealertbox(view, "address");
                }
            });
        } else {
            touch = 0;
            edit.setImageResource(R.drawable.ic_baseline_edit_24);
            name.setOnClickListener(null);
            email.setOnClickListener(null);
            location.setOnClickListener(null);
            phone.setOnClickListener(null);
        }
    }

    public void generatealertbox(View view, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view2 = getLayoutInflater().inflate(R.layout.editdetailbox, null);
        builder.setView(view2);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                edit.setImageResource(R.drawable.ic_baseline_edit_24);
                touch = 0;
                name.setOnClickListener(null);
                email.setOnClickListener(null);
                location.setOnClickListener(null);
                phone.setOnClickListener(null);
                alertDialog.cancel();
            }
        });
        alertDialog.show();
        TextView main = (TextView) findViewById(view.getId());
        EditText val = view2.findViewById(R.id.editpage_name);
        TextView textView = view2.findViewById(R.id.editpage_display);
        Button save = view2.findViewById(R.id.editpage_save);
        textView.setText("Enter new " + type + ":");
        val.setHint("Enter " + type);
        val.setText(main.getText().toString());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!main.getText().toString().trim().equals(val.getText().toString().trim())) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    databaseReference.child(type).setValue(val.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertDialog.dismiss();
                            Toast.makeText(accountsetting.this, "Updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    public void showSettingAlert() {
        alertDialog = new AlertDialog.Builder(this);
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
                Intent i = new Intent(accountsetting.this, accountsetting.class);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
        map.setBuildingsEnabled(true);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView("https://firebasestorage.googleapis.com/v0/b/hackathonproj-b48e0.appspot.com/o/users%2FT18XIj4HxgaMWH56LE33Fwm8KS42%2FInventory%2F-Myqfx4pDTD-Wu1qo12x?alt=media&token=45236fcb-27f3-48d1-ba8c-06079c72a007")));
        map.addMarker(markerOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    File f = new File(currentPhotoPath);
                    uri = Uri.fromFile(f);
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    StorageReference filepath = mref.child("users").child(uid).child("dp");
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String fileLink = task.getResult().toString();
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                            databaseReference.child("dp").setValue(fileLink).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(accountsetting.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                                }
                            });
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