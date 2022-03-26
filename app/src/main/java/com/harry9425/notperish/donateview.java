package com.harry9425.notperish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class donateview extends FragmentActivity implements OnMapReadyCallback {

    public static String id;
    donatemodel donatemodel;
    DatabaseReference databaseReference;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    Double lat=0d,lng=0d;
    String mycoor;
    AlertDialog.Builder alertDialog;
    Double perlat,perlong;
    CircleImageView circleImageView;
    String phone=null;
    RoundedImageView roundedImageView;
    TextView namemain,namemin,email,about,type,quantity,edible,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donateview);
        donatemodel=new donatemodel();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("donation");
        databaseReference.keepSynced(true);
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dv_map);
        namemain=(TextView) findViewById(R.id.dv_namemain);
        namemin=(TextView) findViewById(R.id.dv_namemini);
        email=(TextView) findViewById(R.id.dv_email);
        about=(TextView) findViewById(R.id.dv_about);
        type=(TextView) findViewById(R.id.dev_type);
        quantity=(TextView) findViewById(R.id.dev_quantty);
        edible=(TextView) findViewById(R.id.dv_edible);
        address=(TextView) findViewById(R.id.dv_address);
        address.setSelected(true);
        circleImageView=(CircleImageView) findViewById(R.id.dv_dp);
        roundedImageView=(RoundedImageView) findViewById(R.id.dv_dpprod);
        String[] s=id.split("&");
        donatemodel.setUid(s[0]);
        donatemodel.setDonationid(s[1]);
        perlat=Double.parseDouble(s[2]);
        perlong=Double.parseDouble(s[3]);
        mycoor=mapdonate.mycoor;
        databaseReference.child(donatemodel.getUid()).child(donatemodel.getDonationid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donatemodel=snapshot.getValue(donatemodel.class);
                namemain.setText(donatemodel.getName());
                namemin.setText(donatemodel.getName());
                about.setText(donatemodel.getDetails());
                type.setText(donatemodel.getType());
                quantity.setText(donatemodel.getQuantity()+" "+donatemodel.getQynchng());
                edible.setText(donatemodel.getEdible()+" days");
                address.setText(donatemodel.getLocation());
                Glide.with(donateview.this)
                        .load(donatemodel.getDp())
                        .placeholder(R.drawable.logoloading)
                        .into(roundedImageView);
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("users").child(donatemodel.getUid());
                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        email.setText(snapshot.child("email").getValue().toString());
                        //Toast.makeText(donateview.this,snapshot.getValue().toString(),Toast.LENGTH_LONG).show();
                        if(snapshot.hasChild("phone"))
                        phone=snapshot.child("phone").getValue().toString();
                        supportMapFragment.getMapAsync(donateview.this);
                        if(snapshot.hasChild("dp")) {
                            String dp = snapshot.child("dp").getValue().toString();
                            Glide.with(donateview.this)
                                    .load(dp)
                                    .placeholder(R.drawable.logoloading)
                                    .into(circleImageView);
                        }
                        else {
                            circleImageView.setImageResource(R.drawable.logoloading);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void repin(View view){
        onMapReady(map);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map=googleMap;
        String[] s=mycoor.split("&");
        LatLng latLng=new LatLng(Double.parseDouble(s[0]),Double.parseDouble(s[1]));
        map.setBuildingsEnabled(true);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        Marker m= map.addMarker(markerOptions);
        float[] results = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude,
                perlat, perlong, results);
        float dist = results[0]/1000;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        MarkerOptions mq=new MarkerOptions().position(new LatLng(perlat,perlong)).title("Distance: "+df.format(dist)+" Km");
        map.addMarker(mq);
        m.showInfoWindow();
    }

    public void bkbk(View view){

    }
    public void phonecall(View view){
        if(phone!=null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }
}