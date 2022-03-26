package com.harry9425.notperish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class startpage extends AppCompatActivity {

    FirebaseAuth mAuth;
    String uid;
    nearbyfoodadpter nearbyfoodadpter;
    RecyclerView recyclerView, inventoryrecyclerview;
    ArrayList<donatemodel> list = new ArrayList<>();
    ArrayList<productmodel> plist = new ArrayList<>();
    inventoryadapter inventoryadapter;
    FirebaseDatabase mDatabase;
    productmodel productmodel;
    donatemodel donatemodel;
    int allow = 0;
    CircleImageView billsbtn;
    public static String mycoor;
    private static final double r2d = 180.0D / 3.141592653589793D;
    private static final double d2r = 3.141592653589793D / 180.0D;
    private static final double d2km = 111189.57696D * r2d;
    public static String nameofuser;
    TextView distancerem;
    int distancebw = 1;
    SeekBar seekBar;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Toast.makeText(this,currentUser+" "+currentUser.getUid(), Toast.LENGTH_LONG).show();
        if (currentUser == null) {
            Intent i = new Intent(this, login_page.class);
            startActivity(i);
            Toast.makeText(startpage.this, "going to signing page", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            uid = mAuth.getCurrentUser().getUid().toString();
            circleImageView = (CircleImageView) findViewById(R.id.startpage_dp);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(startpage.this, accountsetting.class);
                    startActivity(i);
                }
            });
            distancerem = (TextView) findViewById(R.id.distanceshown);
            seekBar = (SeekBar) findViewById(R.id.distance_slider);
            recyclerView = (RecyclerView) findViewById(R.id.nearbyfoodrecyclerview);
            inventoryrecyclerview = (RecyclerView) findViewById(R.id.inventoryrecycler);
            nearbyfoodadpter = new nearbyfoodadpter(list, this);
            recyclerView.setAdapter(nearbyfoodadpter);
            billsbtn = (CircleImageView) findViewById(R.id.billscanbtn);
            // recyclerView.setItemViewCacheSize(8);
            //GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            inventoryadapter = new inventoryadapter(plist, this);
            inventoryrecyclerview.setAdapter(inventoryadapter);
            //inventoryrecyclerview.setItemViewCacheSize(8);
            //GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
            inventoryrecyclerview.setLayoutManager(linearLayoutManager2);
            getproductslist();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mycoor = snapshot.child("coordinates").getValue().toString();
                    nameofuser = snapshot.child("name").getValue().toString();
                    getdonationlist();
                    if (snapshot.hasChild("dp")) {
                        String dp = snapshot.child("dp").getValue().toString();
                        Glide.with(startpage.this)
                                .load(dp)
                                .placeholder(R.drawable.logoloading)
                                .into(circleImageView);
                    } else {
                        circleImageView.setImageResource(R.drawable.logoloading);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            seekBar.setMax(20);
            seekBar.setMin(1);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    distancebw = seekBar.getProgress();
                    distancerem.setText(seekBar.getProgress() + " Km");
                    getdonationlist();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

    }


    private void getproductslist() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("users").child(uid).child("inventory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    productmodel = new productmodel();
                    productmodel = snapshot.getValue(productmodel.class);
                    plist.add(productmodel);
                }
                inventoryadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(startpage.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openmapsdonate(View view) {
       Intent i = new Intent(this, mapdonate.class);
        startActivity(i);
    }

    public void openinventory(View view) {
        Intent i = new Intent(this, inventory_large.class);
        startActivity(i);
    }

    public void nearbylarge(View view) {
       Intent i = new Intent(this, nearbyfood_layout.class);
        startActivity(i);
    }

    private void getdonationlist() {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabase.getReference().child("donation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        if (dist <= distancebw*1.0f) {
                            donatemodel = new donatemodel();
                            donatemodel = var.getValue(donatemodel.class);
                            // Toast.makeText(startpage.this,donatemodel.getUid(), Toast.LENGTH_LONG).show();
                            list.add(donatemodel);
                        }
                    }
                }
                nearbyfoodadpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(startpage.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void signout(View view) {
        mAuth.signOut();
        Intent i1 = new Intent(startpage.this, login_page.class);
        startActivity(i1);
        finish();
    }

    public void qrcodescan(View view) {
         Intent i =new Intent(startpage.this,barcodescan.class);
        startActivity(i);
    }

    public void billscan(View view) {
        Intent i =new Intent(startpage.this,MainActivity.class);
         startActivity(i);
    }

    public void enlist(View view) {
        Intent i =new Intent(startpage.this,manualenlistpage.class);
         startActivity(i);
    }

    public void donate(View view) {
        Intent i =new Intent(startpage.this,donatepage.class);
        startActivity(i);
    }
    public void expirein(View view) {
        Intent i =new Intent(startpage.this,expirefood.class);
        startActivity(i);
    }
}