package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harry9425.notperish.getlocation;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class mapdonate extends FragmentActivity implements OnMapReadyCallback  {

    SupportMapFragment supportMapFragment;
    GoogleMap map;
    Double lat=0d,lng=0d;
    AlertDialog.Builder alertDialog;
    FirebaseDatabase mDatabase;
    public  static String uid,mycoor;
    float distancebw;
    TextView distancerem;
    donatemodel donatemodel;
    SeekBar seekBar;
    EditText search;
    ImageButton donesearch;
    String tosr="";
    ImageButton openbtn;
    String touchedmark="";
    donatemodel selmodel;
    ArrayList<donatemodel> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapdonate);
        openbtn=(ImageButton) findViewById(R.id.openbtn_map);
        openbtn.setVisibility(View.GONE);
        distancerem=(TextView) findViewById(R.id.distanceshown3);
        seekBar=(SeekBar) findViewById(R.id.distance_slider3);
        search=(EditText) findViewById(R.id.searchbymarker);
        donesearch=(ImageButton) findViewById(R.id.searchmapdonatedone);
        seekBar.setMax(20);
        seekBar.setMin(1);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distancebw=seekBar.getProgress();
                distancerem.setText(seekBar.getProgress()+" Km");
                getdonationlist();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.donatemaparea);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mycoor=snapshot.child("coordinates").getValue().toString();
                supportMapFragment.getMapAsync(mapdonate.this);
                getdonationlist();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        donesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tosr=search.getText().toString().trim();
                getdonationlist();
            }
        });
        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mapdonate.this,donateview.class);
                donateview.id=touchedmark;
                startActivity(i);
            }
        });
    }
    private void getdonationlist(){
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("donation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                map.clear();
                onMapReady(map);
                list.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    for (DataSnapshot var:snap.getChildren()) {
                        String percoor = var.child("latltd").getValue().toString();
                        String[] s = percoor.split("&");
                        Double perlat = Double.parseDouble(s[0]);
                        Double perlong = Double.parseDouble(s[1]);
                        String[] p = mycoor.split("&");
                        Double mylat = Double.parseDouble(p[0]);
                        Double mylong = Double.parseDouble(p[1]);
                        float[] results = new float[1];
                        Location.distanceBetween(mylat, mylong,
                                perlat, perlong, results);
                        float dist = results[0]/1000;
                        donatemodel = new donatemodel();
                        donatemodel = var.getValue(donatemodel.class);
                        // Toast.makeText(mapdonate.this, dist+"\n"+distancebw, Toast.LENGTH_LONG).show();
                        if (dist <= distancebw*1.0f && (tosr.isEmpty() || donatemodel.getName().contains(tosr))) {
                            LatLng latLng = new LatLng(perlat, perlong);
                            DecimalFormat df = new DecimalFormat();
                            df.setMaximumFractionDigits(1);
                            list.add(donatemodel);
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(donatemodel.getName().toUpperCase() + " Details: " + donatemodel.getDetails()
                                    + " Distance: " + df.format(dist)+" Km");
                            Marker m= map.addMarker(markerOptions);
                            m.setTag(new String(donatemodel.getUid()+"&"+donatemodel.getDonationid()+"&"+percoor));
                            m.showInfoWindow();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mapdonate.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void repin(View view){
        onMapReady(map);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map=googleMap;
        String[] s=mycoor.split("&");
        Double perlat=Double.parseDouble(s[0]);
        Double perlong=Double.parseDouble(s[1]);
        LatLng latLng=new LatLng(perlat,perlong);
        map.setBuildingsEnabled(true);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        Marker m= map.addMarker(markerOptions);
        m.setTag(new String("og"));
        m.showInfoWindow();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if(marker.getTag().toString().equals("og")){
                    openbtn.setVisibility(View.GONE);
                    touchedmark="";
                }
                else {
                    openbtn.setVisibility(View.VISIBLE);
                    touchedmark=marker.getTag().toString();
                }
                return false;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                openbtn.setVisibility(View.GONE);
                touchedmark="";
            }
        });
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
                Intent i=new Intent(mapdonate.this, getlocation.class);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }
}